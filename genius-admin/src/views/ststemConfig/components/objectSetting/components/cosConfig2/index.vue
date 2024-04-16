<!--
 * @Description:

 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jl.g
 * @LastEditTime: 2023-11-17 10:40:49
-->
<template>
  <el-form
    :model="cosConfigForm"
    ref="cosConfigFormRef"
    label-width="150px"
    class="sys-ruleForm"
  >
    <el-form-item label="阿里云OSS绑定的域名" prop="aliyunDomain">
      <el-input
        placeholder="请填写阿里云绑定的域名,如https://image.baidu.com"
        v-model="cosConfigForm.aliyunDomain"
      ></el-input>
    </el-form-item>

    <el-form-item label="阿里云EndPoint" prop="aliyunEndPoint">
      <el-input
        placeholder="请填写阿里云EndPoint"
        v-model="cosConfigForm.aliyunEndPoint"
      ></el-input>
    </el-form-item>

    <el-form-item label="阿里云AccessKeyId" prop="aliyunAccessKeyId">
      <el-input
        placeholder="请填写七牛阿里云AccessKeyId"
        v-model="cosConfigForm.aliyunAccessKeyId"
      ></el-input>
    </el-form-item>

    <el-form-item label="阿里云AccessKeySecret" prop="aliyunAccessKeySecret">
      <el-input
        placeholder="请填写阿里云AccessKeySecret"
        v-model="cosConfigForm.aliyunAccessKeySecret"
      ></el-input>
    </el-form-item>

    <el-form-item label="阿里云BucketName" prop="aliyunBucketName">
      <el-input
        placeholder="请填写阿里云BucketName"
        v-model="cosConfigForm.aliyunBucketName"
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
        aliyunDomain: "",
        aliyunEndPoint: "",
        aliyunAccessKeyId: "",
        aliyunAccessKeySecret: "",
        aliyunBucketName: "",
      },
      cosConfigRules: {
        aliyunDomain: [{ required: true, message: "aliyunDomain不能为空" }],
        aliyunEndPoint: [{ required: true, message: "aliyunEndPoint不能为空" }],
        aliyunAccessKeyId: [{ required: true, message: "aliyunAccessKeyId不能为空" }],
        aliyunAccessKeySecret: [
          { required: true, message: "aliyunAccessKeySecret不能为空" },
        ],
        aliyunBucketName: [{ required: true, message: "aliyunBucketName不能为空" }],
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
        this.cosConfigForm.type = 2;
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
