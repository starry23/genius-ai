<!--
 * @Description: 
 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-15 16:07:23
-->
<template>
  <el-form
    :model="vectorDatabaseForm"
    :rules="vectorDatabaseRules"
    ref="vectorDatabaseRef"
    label-width="150px"
    class="sys-ruleForm"
  >
    <div class="content">
      <el-form-item label="ip" prop="ip">
        <el-input placeholder="请填写内容" v-model="vectorDatabaseForm.ip"></el-input>
      </el-form-item>

      <el-form-item label="port" prop="port">
        <el-input placeholder="请填写内容" v-model="vectorDatabaseForm.port"></el-input>
      </el-form-item>

      <el-form-item label="用户名" prop="name">
        <el-input
          disabled
          placeholder="请填写内容"
          v-model="vectorDatabaseForm.name"
        ></el-input>
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
          disabled
          placeholder="请填写内容"
          v-model="vectorDatabaseForm.password"
        ></el-input>
      </el-form-item>
    </div>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="changePwd">修改密码</el-button>
      <el-button type="primary" @click="onaliSmsFormSubmit">保存配置</el-button>
    </el-form-item>

    <changePwd :getMilvusConfig="getMilvusConfig" ref="changePwdRef" />
  </el-form>
</template>

<script>
import { milvusConfig, saveOrUpdateMilvus } from "@/api/systemConfig";
import changePwd from "./changePwd";
export default {
  components: {
    changePwd,
  },
  data() {
    return {
      vectorDatabaseForm: {
        ip: "",
        port: "",
        name: "",
        password: "",
      },
      vectorDatabaseRules: {
        ip: [{ required: true, message: "请输入内容" }],
        port: [{ required: true, message: "请输入内容" }],
        password: [{ required: true, message: "请输入内容" }],
      },
    };
  },
  mounted() {
    // 获取系统配置
    this.getMilvusConfig();
    this.$refs.vectorDatabaseRef.clearValidate();
    // 重置输入框值为空。这是一种方法，不要仅仅获取
  },
  methods: {
    //修改密码
    changePwd() {
      this.$refs.changePwdRef.getPage(this.vectorDatabaseForm);
    },

    // 获取系统配置
    async getMilvusConfig() {
      try {
        let res = await milvusConfig();
        if (res.code === 200) {
          this.vectorDatabaseForm = res.result || {};
          this.$nextTick(() => {
            if (this.$refs.vectorDatabaseRef) {
              this.$refs.vectorDatabaseRef.clearValidate();
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
      this.$refs.vectorDatabaseRef.validate((valid) => {
        if (valid) {
          this.saveOrUpdateMilvusFn();
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },

    // 网站配置保存
    async saveOrUpdateMilvusFn() {
      try {
        let res = await saveOrUpdateMilvus(this.vectorDatabaseForm);
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
