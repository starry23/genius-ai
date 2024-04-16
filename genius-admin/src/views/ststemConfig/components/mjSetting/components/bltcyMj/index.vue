<!--
 * @Description: 
 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-02-03 10:44:18
-->
<template>
  <el-form
    :model="mjForm"
    :rules="aiImageRules"
    ref="aiImageFormRef"
    label-width="150px"
    class="sys-ruleForm"
  >
    <div class="content">
      <el-form-item label="apiKey" prop="apiKey">
        <el-input placeholder="请填写apiKey" v-model="mjForm.apiKey"></el-input>
      </el-form-item>
      <el-form-item label="apiHost" prop="apiHost">
        <el-input placeholder="请填写域名" v-model="mjForm.apiHost"></el-input>
      </el-form-item>

      <el-form-item label="discordCdn" prop="discordCdn">
        <el-input placeholder="请填写cnd代理" v-model="mjForm.discordCdn"></el-input>
      </el-form-item>
    </div>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="onSubmit">保存配置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { saveSetting, getSetting } from "@/api/systemConfig";

export default {
  data() {
    return {
      sysConfig: null,
      mjForm: {
        apiKey: "",
        apiHost: "",
        discordCdn: "",
      },

      aiImageRules: {
        apiKey: [{ required: true, message: "请输入apiKey" }],
        apiHost: [{ required: true, message: "请输入域名" }],
        discordCdn: [{ required: true, message: "请输入代理" }],
      },
    };
  },
  mounted() {
    // 获取系统配置
    this.getSettingFn();
    this.$refs.aiImageFormRef.clearValidate();
    // 重置输入框值为空。这是一种方法，不要仅仅获取
  },
  methods: {
    // 获取系统配置
    async getSettingFn() {
      try {
        let res = await getSetting();
        if (res.code === 200) {
          this.sysConfig = res.result;
          if (res.result.aiImageBltcySetting) {
            this.mjForm = res.result.aiImageBltcySetting;
          }

          this.$nextTick(() => {
            if (this.$refs.aiImageFormRef) {
              this.$refs.aiImageFormRef.clearValidate();
            }
          });
        } else {
          this.$message({
            type: "error",
            message: res.message,
          });
        }
      } catch (error) {
        console.log(error);
      }
    },

    // 网站配置保存
    onSubmit() {
      // saveSetting
      this.$refs.aiImageFormRef.validate((valid) => {
        if (valid) {
          this.saveSettingFn();
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },

    // 网站配置保存
    async saveSettingFn() {
      try {
        this.sysConfig.aiImageBltcySetting = this.mjForm;
        this.sysConfig.aiImageServiceType = "bltcy";
        let res = await saveSetting(this.sysConfig);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "配置保存成功！",
          });
        } else {
          this.$message({
            type: "error",
            message: res.msg,
          });
        }
      } catch (error) {
        console.log(error);
      }
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
