<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-17 09:41:38
 * @LastEditors: jl.g
 * @LastEditTime: 2023-10-30 15:18:43
-->
<template>
  <div class="chatgpt_config-warp">
    <div class="config-box">
      <el-form
        :model="formConfig"
        :rules="formRules"
        ref="formConfig"
        label-width="350px"
      >
        <div class="formContent">
          <p class="info">
            <a href="https://passport.xfyun.cn/login" target="_blank"
              >点我去申请账号 https://passport.xfyun.cn/login</a
            >
          </p>

          <el-form-item label="appid" prop="appid">
            <el-input
              style="width: 100%"
              placeholder="请输入appid"
              v-model="formConfig.appid"
            ></el-input>
          </el-form-item>

          <el-form-item label="apiKey" prop="apiKey">
            <el-input
              style="width: 100%"
              placeholder="请输入apiKey"
              v-model="formConfig.apiKey"
            ></el-input>
          </el-form-item>

          <el-form-item label="apiSecret" prop="apiSecret">
            <el-input
              style="width: 100%"
              placeholder="请输入apiSecret"
              v-model="formConfig.apiSecret"
            ></el-input>
          </el-form-item>

          <el-form-item label="版本(domain)" prop="domain">
            <el-input
              style="width: 100%"
              placeholder="请输入版本"
              v-model="formConfig.domain"
            ></el-input>
          </el-form-item>

          <el-form-item label="请求地址(host)" prop="host">
            <el-input
              style="width: 100%"
              placeholder="请输入请求地址"
              v-model="formConfig.host"
            ></el-input>
          </el-form-item>

          <el-form-item label="温度:取值0~1" prop="temperature">
            <el-input-number
              style="width: 100%"
              placeholder="请输入temperature"
              v-model="formConfig.temperature"
              :max="1"
              :precision="1"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="maxTokens 最大8192">
            <el-input-number
              style="width: 100%"
              placeholder="请输入maxTokens"
              v-model="formConfig.maxTokens"
              :max="8192"
            ></el-input-number>
          </el-form-item>

          <el-form-item label="上线文限制" prop="contextLimit">
            <el-input-number
              style="width: 100%"
              placeholder="请输入上线文限制"
              v-model="formConfig.contextLimit"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="上线文分割字数限制" prop="contextSplit">
            <el-input-number
              style="width: 100%"
              placeholder="请输入上线文分割字数限制"
              v-model="formConfig.contextSplit"
            ></el-input-number>
          </el-form-item>

          <div class="sparkDeskInfo">
            <p>
              备注:domain 和 host 取值参考文档:
              <a
                class="jump"
                target="_blank"
                href="https://www.xfyun.cn/doc/spark/Web.html"
              >
                点击前往 https://www.xfyun.cn/doc/spark/Web.html</a
              >
            </p>
            <p>当前最新参考配置如下</p>
            <ul>
              <ol>
                general1: https://spark-api.xf-yun.com/v1.1/chat
              </ol>
              <ol>
                generalv2: https://spark-api.xf-yun.com/v2.1/chat
              </ol>
              <ol>
                generalv3: https://spark-api.xf-yun.com/v3.1/chat
              </ol>
            </ul>
          </div>
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
      allConfig: null,
      modelList: [],
      formConfig: {
        contextSplit: "",
        contextLimit: "",
        appid: "",
        apiSecret: "",
        apiKey: "",
        maxTokens: "",
        temperature: "",
        domain: "generalv2",
        host: "https://spark-api.xf-yun.com/v2.1/chat",
      },
      formRules: {
        temperature: [
          {
            required: true,
            message: "请填写内容",
          },
        ],
        maxTokens: [
          {
            required: true,
            message: "请填写内容",
          },
        ],
        apiKey: [
          {
            required: true,
            message: "请填写内容",
          },
        ],
        apiSecret: [
          {
            required: true,
            message: "请填写内容",
          },
        ],

        contextSplit: [
          {
            required: true,
            message: "请填写内容",
          },
        ],

        contextLimit: [
          {
            required: true,
            message: "请填写内容",
          },
        ],

        appid: [
          {
            required: true,
            message: "请填写内容",
          },
        ],
        domain: [
          {
            required: true,
            message: "请填写内容",
          },
        ],
        host: [
          {
            required: true,
            message: "请填写内容",
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
          this.allConfig = res.result;
          this.formConfig = res.result.sparkDesk || {
            contextSplit: "",
            contextLimit: "",
            appid: "",
            apiSecret: "",
            apiKey: "",
            maxTokens: "",
            temperature: "",
            domain: "",
            host: "",
          };
        }
      } catch (error) {
        console.log(error);
      }
    },

    /**
     * @description: 保存配置
     * @return {*}
     * @author: jinglin.gao
     */
    async saveOrUpdateChatConfigFn() {
      try {
        this.allConfig.sparkDesk = this.formConfig;
        let res = await saveOrUpdateChatConfig(this.allConfig);
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
      this.$refs.formConfig.validate((valid) => {
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
