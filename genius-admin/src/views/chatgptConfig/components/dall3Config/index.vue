<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-17 09:41:38
 * @LastEditors: jl.g
 * @LastEditTime: 2023-11-17 15:38:29
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

          <el-form-item label="反代地址 注意: 只填域名 不要加任何斜杠">
            <el-input placeholder="请填写反代地址" v-model="chatForm.domain"></el-input>
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

      allChatConfig: null,

      chatForm: {
        apiKeys: [],
        domain: "",
      },
      chatFormRules: {
        apiKeys: [
          {
            required: true,
            message: "请输入apiKey",
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
          this.chatForm = res.result.dalle || {
            apiKeys: [],
            domain: "",
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
        this.allChatConfig.dalle = this.chatForm;
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
