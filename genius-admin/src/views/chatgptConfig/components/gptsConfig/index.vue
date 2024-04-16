<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-17 09:41:38
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-02-03 12:31:19
-->
<template>
  <div class="chatgpt_config-warp">
    <!-- chatgpt配置 -->
    <div class="config-box">
      <el-form
        :model="chatForm"
        :rules="chatFormRules"
        ref="chatForm"
        label-width="350px"
      >
        <div class="formContent">
          <el-form-item label="apiKey列表" prop="apiKeys">
            <div class="apiKeyList">
              <div
                class="apiKey-item"
                v-for="(item, index) in chatForm.apiKeys"
                :key="index"
              >
                <el-input v-model="chatForm.apiKeys[index]"></el-input>
                <el-button @click="deleteKey(index)" size="small" type="danger"
                  >删除</el-button
                >
              </div>
              <el-button @click="addKey" class="addKey" size="small" type="primary"
                >新增key</el-button
              >
            </div>

            <div class="info">
              <a href="https://one-api.bltcy.top/register?aff=n2AH" target="_blank"
                >点击申请GPTS中转站</a
              >
            </div>
          </el-form-item>

          <el-form-item label="反代地址 注意: 只填域名 不要加任何斜杠">
            <el-input placeholder="请填写反代地址" v-model="chatForm.domain"></el-input>
          </el-form-item>

          <el-form-item label="maxTokens" prop="maxTokens">
            <el-input-number
              :min="0"
              style="width: 100%"
              placeholder="请填写maxTokens"
              v-model="chatForm.maxTokens"
            ></el-input-number>
          </el-form-item>

          <el-form-item label="上下文限制(1~10)" prop="contextLimit">
            <el-input-number
              :min="1"
              :max="10"
              style="width: 100%"
              placeholder="请填写上下文限制(1~10)"
              v-model="chatForm.contextLimit"
            ></el-input-number>
          </el-form-item>

          <el-form-item label="上线文分割字数限制(1~2048)" prop="contextSplit">
            <el-input-number
              :min="1"
              :max="2048"
              style="width: 100%"
              placeholder="请填写上线文分割字数限制(1~2048)"
              v-model="chatForm.contextSplit"
            ></el-input-number>
          </el-form-item>

          <el-form-item label="模型前缀(默认: gpt-4-gizmo-)">
            <el-input
              placeholder="请填写model前缀("
              v-model="chatForm.modelPrefix"
            ></el-input>
          </el-form-item>

          <el-form-item label="路由方式">
            <el-radio v-model="chatForm.route" label="1">路由方式1</el-radio>
            <el-radio v-model="chatForm.route" label="2">路由方式2</el-radio>
          </el-form-item>
          <p class="info">如果gpts列表加载失败,请更换路由通道</p>
        </div>
        <el-form-item style="text-align: center">
          <el-button type="primary" @click="onFormSubmit">保存配置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { saveOrUpdateChatConfig, getChatConfig } from "@/api/chatgptConfig";
export default {
  data() {
    return {
      allChatConfig: null,
      modelList: [],
      chatForm: {
        apiKeys: [],
        maxTokens: 0,
        domain: "",
        modelPrefix: "",
        route: "",
        contextLimit: "",
        contextSplit: "",
      },
      chatFormRules: {
        apiKeys: [
          {
            required: true,
            message: "请输入apiKey",
          },
        ],
        maxTokens: [
          {
            required: true,
            message: "请输入maxToken",
          },
        ],
        contextLimit: [
          {
            required: true,
            message: "请填写上下文限制",
          },
        ],
        contextSplit: [
          {
            required: true,
            message: "请填写上线文分割字数限制",
          },
        ],
      },
    };
  },
  mounted() {
    this.getChatConfigFn();
  },
  methods: {
    // 获取gpt配置
    async getChatConfigFn() {
      try {
        let res = await getChatConfig();
        if (res.code === 200) {
          this.allChatConfig = res.result;
          this.chatForm = res.result.gptsEntity || {
            apiKeys: [],
            domain: "",
            modelPrefix: "",
            route: "",
          };
          if (!this.chatForm.apiKeys) {
            this.chatForm.apiKeys = [];
          }
        }
      } catch (error) {
        console.log(error);
      }
    },

    /**
     * @description: 新增key
     * @return {*}
     * @author: jinglin.gao
     */
    addKey() {
      this.chatForm.apiKeys.push("");
    },
    /**
     * @description: 删除key
     * @param {*} index
     * @return {*}
     * @author: jinglin.gao
     */
    deleteKey(index) {
      this.chatForm.apiKeys.splice(index, 1);
    },

    /**
     * @description: 保存配置
     * @return {*}
     * @author: jinglin.gao
     */
    async saveOrUpdateChatConfigFn() {
      try {
        this.allChatConfig.gptsEntity = this.chatForm;
        let res = await saveOrUpdateChatConfig(this.allChatConfig);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "配置保存成功",
          });
        } else {
          this.$message.error(res.message);
        }
      } catch (error) {
        console.log(error);
      }
    },
    onFormSubmit() {
      this.$refs.chatForm.validate((valid) => {
        if (valid) {
          this.saveOrUpdateChatConfigFn();
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
