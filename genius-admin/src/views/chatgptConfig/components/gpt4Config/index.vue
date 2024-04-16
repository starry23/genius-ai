<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-17 09:41:38
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-13 21:58:42
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
          </el-form-item>

          <el-form-item label="模型" prop="model">
            <!-- 选择 -->
            <div class="modelSelect" v-if="modelChooseType === '1'">
              <el-select
                placeholder="请选择模型"
                style="width: 75%"
                v-model="chatForm.model"
              >
                <el-option
                  v-for="item in modelList"
                  :key="item.key"
                  :label="item.key"
                  :value="item.value"
                ></el-option>
              </el-select>

              <el-button @click="changeModelChooseType" size="small" type="primary"
                >手动输入</el-button
              >
            </div>

            <!-- 手动输入 -->
            <div class="modelSelect" v-if="modelChooseType === '2'">
              <el-input
                placeholder="请输入模型"
                style="width: 75%"
                v-model="chatForm.model"
              >
              </el-input>

              <el-button @click="changeModelChooseType" size="small" type="primary"
                >手动选择</el-button
              >
            </div>
          </el-form-item>

          <el-form-item label="反代地址 注意: 只填域名 不要加任何斜杠">
            <el-input placeholder="请填写反代地址" v-model="chatForm.domain"></el-input>
          </el-form-item>

          <el-form-item label="temperature(温度)" prop="temperature">
            <el-input-number
              :min="0"
              :max="100"
              style="width: 100%"
              placeholder="请填写temperature"
              v-model="chatForm.temperature"
            ></el-input-number>
          </el-form-item>

          <el-form-item label="maxTokens" prop="maxTokens">
            <el-input-number
              :min="0"
              style="width: 100%"
              placeholder="请填写maxTokens"
              v-model="chatForm.maxTokens"
            ></el-input-number>
          </el-form-item>

          <el-form-item
            label="创造性和独特性设置[presencePenalty](-2-2)"
            prop="presencePenalty"
          >
            <el-input-number
              :min="-2"
              :max="2"
              :precision="1"
              style="width: 100%"
              placeholder="请填写创造性和独特性设置(-2-2)"
              v-model="chatForm.presencePenalty"
            ></el-input-number>
          </el-form-item>

          <el-form-item
            label="多样化的文本设置[frequencyPenalty](-2-2)"
            prop="frequencyPenalty"
          >
            <el-input-number
              :min="-2"
              :max="2"
              :precision="1"
              style="width: 100%"
              placeholder="请填写多样化的文本设置(-2-2)"
              v-model="chatForm.frequencyPenalty"
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
        </div>
        <el-form-item style="text-align: center">
          <el-button type="primary" @click="onFormSubmit">保存配置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import {
  chatModelList,
  saveOrUpdateChatConfig,
  getChatConfig,
} from "@/api/chatgptConfig";
export default {
  data() {
    return {
      // 1 选择 2 手动输入
      modelChooseType: "1",
      allChatConfig: null,
      modelList: [],
      chatForm: {
        apiKeys: [],
        model: "",
        temperature: 0,
        maxTokens: 0,
        presencePenalty: 0, // -2.0 to 2.0, 0 means no penalty.
        frequencyPenalty: 0,
        contextLimit: "",
        contextSplit: "",
        domain:""
      },
      chatFormRules: {
        apiKeys: [
          {
            required: true,
            message: "请输入apiKey",
          },
        ],
        model: [
          {
            required: true,
            message: "请选择chatgpt模型",
          },
        ],
        temperature: [
          {
            required: true,
            message: "请填写temperature",
          },
        ],

        presencePenalty: [
          {
            required: true,
            message: "请填写创造性和独特性设置(-2.0~2.0)",
          },
        ],

        frequencyPenalty: [
          {
            required: true,
            message: "请填写多样化的文本设置(-2.0~2.0)",
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

        maxTokens: [
          {
            required: true,
            message: "请填写内容",
          },
        ],
      },
    };
  },
  mounted() {
    this.chatModelListFn();
    this.getChatConfigFn();
  },
  methods: {
    changeModelChooseType() {
      this.modelChooseType = this.modelChooseType === "1" ? "2" : "1";
      this.chatForm.model = "";
    },
    // 获取gpt配置
    async getChatConfigFn() {
      try {
        let res = await getChatConfig();
        if (res.code === 200) {
          this.allChatConfig = res.result;
          this.chatForm = res.result.entity4 || {
            apiKeys: [],
            model: "",
            temperature: 0,
            maxTokens: 0,
            presencePenalty: 0, // -2.0 to 2.0, 0 means no penalty.
            frequencyPenalty: 0,
            contextLimit: "",
            contextSplit: "",
            domain:""
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
     * @description: 获取模型列表
     * @return {*}
     * @author: jinglin.gao
     */
    async chatModelListFn() {
      try {
        let res = await chatModelList();
        if (res.code === 200) {
          this.modelList = res.result || [];
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
      if (this.chatForm.apiKeys.some((v) => v === "")) {
        this.$message({
          type: "info",
          message: "当前存在尚未填写完毕的key,请填写完毕后再次新增",
        });
      } else {
        this.chatForm.apiKeys.push("");
      }
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
        this.allChatConfig.entity4 = this.chatForm;
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
