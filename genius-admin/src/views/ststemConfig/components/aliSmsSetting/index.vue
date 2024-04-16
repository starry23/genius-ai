<!--
 * @Description: 
 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-04 15:48:26
-->
<template>
  <el-form
    :model="aliSmsForm"
    :rules="aliSmsRules"
    ref="aliSmsFormRef"
    label-width="150px"
    class="sys-ruleForm"
  >
    <div class="content">
      <el-form-item label="accessKeySecret" prop="accessKeySecret">
        <el-input
          placeholder="请填写内容"
          v-model="aliSmsForm.accessKeySecret"
        ></el-input>
      </el-form-item>

      <el-form-item label="accessKey" prop="accessKey">
        <el-input placeholder="请填写内容" v-model="aliSmsForm.accessKey"></el-input>
      </el-form-item>

      <el-form-item label="signName" prop="signName">
        <el-input placeholder="请填写内容" v-model="aliSmsForm.signName"></el-input>
      </el-form-item>

      <el-form-item label="templateCode" prop="templateCode">
        <el-input placeholder="请填写内容" v-model="aliSmsForm.templateCode"></el-input>
      </el-form-item>
    </div>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="onaliSmsFormSubmit">保存配置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { saveSetting, getSetting } from "@/api/systemConfig";

export default {
  data() {
    return {
      sysConfig: null,
      aliSmsForm: {
        accessKeySecret: "",
        accessKey: "",
        signName: "",
        templateCode: "",
      },
      aliSmsRules: {
        accessKeySecret: [{ required: true, message: "请输入内容" }],
        accessKey: [{ required: true, message: "请输入内容" }],
        signName: [{ required: true, message: "请输入内容" }],
        templateCode: [{ required: true, message: "请输入内容" }],
      },
    };
  },
  mounted() {
    // 获取系统配置
    this.getSettingFn();
    this.$refs.aliSmsFormRef.clearValidate();
    // 重置输入框值为空。这是一种方法，不要仅仅获取
  },
  methods: {
    // 获取系统配置
    async getSettingFn() {
      try {
        let res = await getSetting();
        if (res.code === 200) {
          this.sysConfig = res.result;
          this.aliSmsForm = res.result.aliSmsSetting || {
            accessKeySecret: "",
            accessKey: "",
            signName: "",
            templateCode: "",
          };

          this.$nextTick(() => {
            if (this.$refs.aliSmsFormRef) {
              this.$refs.aliSmsFormRef.clearValidate();
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
    onaliSmsFormSubmit() {
      // saveSetting
      this.$refs.aliSmsFormRef.validate((valid) => {
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
        this.sysConfig.aliSmsSetting = this.aliSmsForm;
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
