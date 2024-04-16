<!--
 * @Description: 
 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-04 09:48:16
-->
<template>
  <el-form
    :model="wechatForm"
    :rules="aliPayRules"
    ref="wechatFormRef"
    label-width="200px"
    class="sys-ruleForm"
  >
    <div class="content">
      <el-form-item label="是否开启支付功能(默认开启)">
        <el-switch v-model="wechatForm.wechatPayConfig.enable"> </el-switch>
      </el-form-item>

      <el-form-item label="appId" prop="wechatPayConfig.appId">
        <el-input
          placeholder="请填写appId"
          v-model="wechatForm.wechatPayConfig.appId"
        ></el-input>
      </el-form-item>

      <el-form-item label="商户号" prop="wechatPayConfig.mchId">
        <el-input
          placeholder="请填写商户号"
          v-model="wechatForm.wechatPayConfig.mchId"
        ></el-input>
      </el-form-item>

      <el-form-item label="APIv2密钥" prop="wechatPayConfig.partnerKey">
        <el-input
          placeholder="请填写APIv2密钥"
          v-model="wechatForm.wechatPayConfig.partnerKey"
        ></el-input>
      </el-form-item>

      <el-form-item label="h5安全域名wapUrl" prop="wechatPayConfig.h5WapUrl">
        <el-input
          placeholder="请填写h5WapUrl"
          v-model="wechatForm.wechatPayConfig.h5WapUrl"
        ></el-input>
      </el-form-item>
    </div>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="onwechatFormSubmit">保存配置</el-button>
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
      wechatForm: {
        closePayNotice: "",
        wechatPayConfig: {
          enable: true,
          appId: "",
          mchId: "",
          partnerKey: "",
          h5WapUrl: "",
        },
      },
      aliPayRules: {
        "wechatPayConfig.appId": [{ required: true, message: "请输入appId" }],
        "wechatPayConfig.mchId": [{ required: true, message: "请输入商户号" }],
        "wechatPayConfig.partnerKey": [{ required: true, message: "请输入APIv2密钥	" }],
        "wechatPayConfig.h5WapUrl": [{ required: true, message: "请输入h5WapUrl	" }],
      },
    };
  },
  mounted() {
    // 获取系统配置
    this.getSettingFn();
    this.$refs.wechatFormRef.clearValidate();
    // 重置输入框值为空。这是一种方法，不要仅仅获取
  },
  methods: {
    // 获取系统配置
    async getSettingFn() {
      try {
        let res = await getPaySetting();
        if (res.code === 200) {
          this.wechatForm = res.result;

          if (!this.wechatForm.wechatPayConfig) {
            this.wechatForm.wechatPayConfig = {
              enable: true,
              appId: "",
              mchId: "",
              partnerKey: "",
              h5WapUrl: "",
            };
          }
          this.$nextTick(() => {
            if (this.$refs.wechatFormRef) {
              this.$refs.wechatFormRef.clearValidate();
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
    onwechatFormSubmit() {
      // saveSetting
      this.$refs.wechatFormRef.validate((valid) => {
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
        this.wechatForm.closePayNotice = this.closePayNotice;
        let res = await saveOrUpdatePaySetting(this.wechatForm);
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
