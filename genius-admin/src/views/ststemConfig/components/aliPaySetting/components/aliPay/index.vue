<!--
 * @Description: 
 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-13 22:20:46
-->
<template>
  <el-form
    :model="aliPayForm"
    :rules="aliPayRules"
    ref="aliPayFormRef"
    label-width="200px"
    class="sys-ruleForm"
  >
    <div class="content">
      <el-form-item label="是否开启支付功能(默认开启)">
        <el-switch v-model="aliPayForm.aliPayProperties.enable"> </el-switch>
      </el-form-item>

      <el-form-item label="是否强制开启当面付">
        <el-switch v-model="aliPayForm.aliPayProperties.openFacePay"> </el-switch>
      </el-form-item>

      <el-form-item label="商户公钥" prop="aliPayProperties.appPublicKey">
        <el-input
          placeholder="请填写商户公钥"
          v-model="aliPayForm.aliPayProperties.appPublicKey"
        ></el-input>
      </el-form-item>

      <el-form-item label="商户私钥" prop="aliPayProperties.appPrivateKey">
        <el-input
          placeholder="请填写商户私钥"
          v-model="aliPayForm.aliPayProperties.appPrivateKey"
        ></el-input>
      </el-form-item>

      <el-form-item label="支付宝公钥" prop="aliPayProperties.aliPublicKey">
        <el-input
          placeholder="请填写支付宝公钥"
          v-model="aliPayForm.aliPayProperties.aliPublicKey"
        ></el-input>
      </el-form-item>

      <el-form-item label="appId" prop="aliPayProperties.appId">
        <el-input
          placeholder="请填写appId"
          v-model="aliPayForm.aliPayProperties.appId"
        ></el-input>
      </el-form-item>
    </div>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="onaliPayFormSubmit">保存配置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { saveOrUpdatePaySetting, getPaySetting } from "@/api/aliPaySetting";

export default {
  props: ["closePayNotice"],
  data() {
    return {
      sysConfig: null,
      aliPayForm: {
        closePayNotice: "",
        aliPayProperties: {
          enable: true,
          openFacePay: false,
          appPublicKey: "",
          appPrivateKey: "",
          aliPublicKey: "",
          appId: "",
        },
      },
      aliPayRules: {
        "aliPayProperties.appPublicKey": [{ required: true, message: "请输入商户公钥" }],
        "aliPayProperties.appPrivateKey": [{ required: true, message: "请输入商户公钥" }],
        "aliPayProperties.aliPublicKey": [
          { required: true, message: "请输入支付宝公钥" },
        ],
        "aliPayProperties.appId": [{ required: true, message: "请输入appId" }],
      },
    };
  },
  mounted() {
    // 获取系统配置
    this.getSettingFn();
    this.$refs.aliPayFormRef.clearValidate();
    // 重置输入框值为空。这是一种方法，不要仅仅获取
  },
  methods: {
    // 获取系统配置
    async getSettingFn() {
      try {
        let res = await getPaySetting();
        if (res.code === 200) {
          this.aliPayForm = res.result;

          if (!this.aliPayForm.aliPayProperties) {
            this.aliPayForm.aliPayProperties = {
              appPublicKey: "",
              appPrivateKey: "",
              aliPublicKey: "",
              appId: "",
              enable: true,
              openFacePay: false,
            };
          }

          this.$nextTick(() => {
            if (this.$refs.aliPayFormRef) {
              this.$refs.aliPayFormRef.clearValidate();
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
    onaliPayFormSubmit() {
      // saveSetting
      this.$refs.aliPayFormRef.validate((valid) => {
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
        this.aliPayForm.closePayNotice = this.closePayNotice;
        let res = await saveOrUpdatePaySetting(this.aliPayForm);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "配置保存成功！",
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
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
