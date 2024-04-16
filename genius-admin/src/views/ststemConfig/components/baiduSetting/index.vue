<!--
 * @Description: 
 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-22 17:33:29
-->
<template>
  <el-form
    :model="baiduForm"
    :rules="baiduRules"
    ref="baiduFormRef"
    label-width="250px"
    class="sys-ruleForm"
  >
    <div class="content">
      <div class="info">
        <a
          target="_blank"
          href="https://login.bce.baidu.com/?account=&redirect=http%3A%2F%2Fconsole.bce.baidu.com%2Fai%2F#/ai/ocr/overview/index"
          >点我去注册</a
        >
      </div>
      <el-form-item label="百度智能云校验是否开启" prop="textEnable">
        <el-switch v-model="baiduForm.textEnable"> </el-switch>
      </el-form-item>
      <p style="textalign: center">包含 1. 文本审核 2.ORC识别</p>

      <el-form-item label="百度应用ID	" prop="appId">
        <el-input placeholder="请填写百度应用ID" v-model="baiduForm.appId"></el-input>
      </el-form-item>

      <el-form-item label="百度应用Key" prop="appKey">
        <el-input placeholder="请填写百度应用Key" v-model="baiduForm.appKey"></el-input>
      </el-form-item>

      <el-form-item label="百度应用Secret	" prop="secretKey">
        <el-input
          placeholder="请填写百度应用Secret"
          v-model="baiduForm.secretKey"
        ></el-input>
      </el-form-item>
    </div>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="onbaiduFormSubmit">保存配置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { saveSetting, getSetting } from "@/api/systemConfig";

export default {
  data() {
    return {
      sysConfig: null,
      baiduForm: {
        textEnable: false,
        appId: "",
        appKey: "",
        secretKey: "",
      },
      baiduRules: {
        textEnable: [{ required: true, message: "百度文本敏感校验是否开启	" }],
        appId: [{ required: true, message: "请输入百度应用ID" }],
        appKey: [{ required: true, message: "请输入百度应用Key" }],
        secretKey: [{ required: true, message: "请输入百度应用Secret" }],
      },
    };
  },
  mounted() {
    // 获取系统配置
    this.getSettingFn();

    // 重置输入框值为空。这是一种方法，不要仅仅获取
  },
  methods: {
    // 获取系统配置
    async getSettingFn() {
      try {
        let res = await getSetting();
        if (res.code === 200) {
          this.sysConfig = res.result;
          this.baiduForm = res.result.baiduSetting;
          this.$nextTick(() => {
            if (this.$refs.baiduFormRef) {
              this.$refs.baiduFormRef.clearValidate();
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
    onbaiduFormSubmit() {
      // saveSetting
      this.$refs.baiduFormRef.validate((valid) => {
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
        this.sysConfig.baiduSetting = this.baiduForm;
        let res = await saveSetting(this.sysConfig);
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
