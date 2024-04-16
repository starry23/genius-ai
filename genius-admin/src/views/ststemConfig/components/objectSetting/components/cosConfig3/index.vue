<!--
 * @Description:

 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-07-01 16:50:40
-->
<template>
  <el-form
    :model="cosConfigForm"
    ref="cosConfigFormRef"
    label-width="150px"
    class="sys-ruleForm"
  >
    <!--    <el-form-item label="腾讯COS云绑定的域名" prop="qcloudDomain">-->
    <!--      <el-input placeholder="请填写腾讯云绑定的域名,如https://image.baidu.com" v-model="cosConfigForm.qcloudDomain"></el-input>-->
    <!--    </el-form-item>-->

    <!--    <el-form-item label="腾讯云AppId" prop="qcloudAppId">-->
    <!--      <el-input-->
    <!--        placeholder="请填写腾讯云AppId"-->
    <!--        v-model="cosConfigForm.qcloudAppId"-->
    <!--      ></el-input>-->
    <!--    </el-form-item>-->

    <el-form-item label="腾讯云SecretId" prop="qcloudSecretId">
      <el-input
        placeholder="请填写腾讯云SecretId"
        v-model="cosConfigForm.qcloudSecretId"
      ></el-input>
    </el-form-item>

    <el-form-item label="腾讯云SecretKey" prop="qcloudSecretKey">
      <el-input
        placeholder="请填写腾讯云SecretKey"
        v-model="cosConfigForm.qcloudSecretKey"
      ></el-input>
    </el-form-item>

    <el-form-item label="腾讯云BucketName" prop="qcloudBucketName">
      <el-input
        placeholder="请填写腾讯云BucketName"
        v-model="cosConfigForm.qcloudBucketName"
      ></el-input>
    </el-form-item>

    <el-form-item label="腾讯云COS所属地区" prop="qcloudRegion">
      <el-input
        placeholder="请填写腾讯云COS所属地区"
        v-model="cosConfigForm.qcloudRegion"
      ></el-input>
    </el-form-item>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="onSubmit">保存配置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { saveOssSetting } from "@/api/systemConfig";
export default {
  props: {
    config: {
      type: Object,
    },
  },
  data() {
    return {
      cosConfigForm: {
        qcloudDomain: "",
        qcloudAppId: "",
        qcloudSecretId: "",
        qcloudSecretKey: "",
        qcloudBucketName: "",
        qcloudRegion: "",
      },
      cosConfigRules: {
        qcloudDomain: [{ required: true, message: "qcloudDomain不能为空" }],
        qcloudAppId: [{ required: true, message: "qcloudAppId不能为空" }],
        qcloudSecretId: [{ required: true, message: "qcloudSecretId不能为空" }],
        qcloudSecretKey: [{ required: true, message: "qcloudSecretKey不能为空" }],
        qcloudBucketName: [{ required: true, message: "qcloudBucketName不能为空" }],
        qcloudRegion: [{ required: true, message: "qcloudRegion不能为空" }],
      },
    };
  },

  watch: {
    config(val) {
      if (val) {
        this.$refs.cosConfigFormRef.clearValidate();
        this.cosConfigForm = val;
      }
    },
  },
  methods: {
    // 网站配置保存
    onSubmit() {
      // saveSetting
      this.$refs.cosConfigFormRef.validate((valid) => {
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
        this.cosConfigForm.type = 3;
        let res = await saveOssSetting(this.cosConfigForm);
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
