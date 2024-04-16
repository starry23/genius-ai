<!--
 * @Description: 
 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-06-03 11:20:03
-->
<template>
  <el-form
    :model="emailForm"
    :rules="emailRules"
    ref="emailFormRef"
    label-width="150px"
    class="sys-ruleForm"
  >
    <div class="content">
      <el-form-item label="SMTP服务器" prop="host">
        <el-input placeholder="请填写SMTP服务器" v-model="emailForm.host"></el-input>
      </el-form-item>

      <el-form-item label="端口" prop="port">
        <el-input-number
          :min="0"
          placeholder="请填写端口"
          v-model="emailForm.port"
        ></el-input-number>
      </el-form-item>

      <el-form-item label="开启认证(默认开启)" prop="auth">
        <el-switch v-model="emailForm.auth"> </el-switch>
      </el-form-item>

      <el-form-item label="邮箱" prop="from">
        <el-input placeholder="请填写邮箱" v-model="emailForm.from"></el-input>
      </el-form-item>

      <el-form-item label="用户名" prop="user">
        <el-input placeholder="请填写用户名" v-model="emailForm.user"></el-input>
      </el-form-item>

      <el-form-item label="密码" prop="pass">
        <el-input placeholder="请填写密码" v-model="emailForm.pass"></el-input>
      </el-form-item>
    </div>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="onemailFormSubmit">保存配置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { saveSetting, getSetting } from "@/api/systemConfig";

export default {
  data() {
    return {
      sysConfig: null,
      emailForm: {
        host: "",
        port: 0,
        auth: true,
        from: "",
        user: "",
        pass: "",
      },
      emailRules: {
        host: [{ required: true, message: "请输入SMTP服务器" }],
        port: [{ required: true, message: "请输入端口" }],
        auth: [{ required: true, message: "请选择是否开启认证(默认开启)" }],
        from: [{ required: true, message: "请输入邮箱" }],
        user: [{ required: true, message: "请输入用户名" }],
        pass: [{ required: true, message: "请输入密码" }],
      },
    };
  },
  mounted() {
    // 获取系统配置
    this.getSettingFn();
    this.$refs.emailFormRef.clearValidate();
    // 重置输入框值为空。这是一种方法，不要仅仅获取
  },
  methods: {
    // 获取系统配置
    async getSettingFn() {
      try {
        let res = await getSetting();
        if (res.code === 200) {
          this.sysConfig = res.result;
          this.emailForm = res.result.emailConfigProperties;

          this.$nextTick(() => {
            if (this.$refs.emailFormRef) {
              this.$refs.emailFormRef.clearValidate();
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
    onemailFormSubmit() {
      // saveSetting
      this.$refs.emailFormRef.validate((valid) => {
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
        this.sysConfig.emailConfigProperties = this.emailForm;
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
