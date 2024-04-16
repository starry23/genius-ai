<!--
 * @Description:

 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jl.g
 * @LastEditTime: 2023-11-17 11:14:48
-->
<template>
  <el-form
    :model="cosConfigForm"
    ref="cosConfigFormRef"
    label-width="150px"
    class="sys-ruleForm"
  >
    <el-form-item label="七牛云绑定的域名" prop="qiniuDomain">
      <el-input
        placeholder="请填写七牛绑定的域名,如https://image.baidu.com"
        v-model="cosConfigForm.qiniuDomain"
      ></el-input>
    </el-form-item>

    <el-form-item label="七牛ACCESS_KEY" prop="qiniuAccessKey">
      <el-input
        placeholder="请填写七牛ACCESS_KEY"
        v-model="cosConfigForm.qiniuAccessKey"
      ></el-input>
    </el-form-item>

    <el-form-item label="七牛云SECRET_KEY" prop="qiniuSecretKey">
      <el-input
        placeholder="请填写七牛云SECRET_KEY"
        v-model="cosConfigForm.qiniuSecretKey"
      ></el-input>
    </el-form-item>

    <el-form-item label="七牛存储空间名" prop="qiniuBucketName">
      <el-input
        placeholder="请填写七牛存储空间名BucketName"
        v-model="cosConfigForm.qiniuBucketName"
      ></el-input>
    </el-form-item>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="onSubmit">保存配置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import {
  saveOssSetting,
} from "@/api/systemConfig";
export default {
  props: {
    config: {
      type: Object,
    },
  },
  data() {
    return {
      cosConfigForm: {
        qiniuDomain: "",
        qiniuAccessKey: "",
        qiniuSecretKey: "",
        qiniuBucketName: "",
      },
      cosConfigRules: {
        qiniuDomain: [{ required: true, message: "qiniuDomain不能为空" }],
        qiniuAccessKey: [{ required: true, message: "qiniuAccessKey不能为空" }],
        qiniuSecretKey: [{ required: true, message: "qiniuSecretKey不能为空" }],
        qiniuBucketName: [{ required: true, message: "qiniuBucketName不能为空" }],
      },
    };
  },

  /*  */
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
        this.cosConfigForm.type = 1;
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
