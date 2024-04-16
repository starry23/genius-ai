<!--
 * @Description:

 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-11 13:52:00
-->
<template>
  <el-form
    :model="cosConfigForm"
    ref="cosConfigFormRef"
    label-width="150px"
    class="sys-ruleForm"
  >
    <el-form-item label="域名(静态资源域名)" prop="localDomain">
      <el-input
        placeholder="请填写域名(静态资源域名)"
        v-model="cosConfigForm.localDomain"
      ></el-input>
    </el-form-item>

    <el-form-item label="路径前缀" prop="localPrefix">
      <el-input
        placeholder="请填写路径前缀"
        v-model="cosConfigForm.localPrefix"
      ></el-input>
    </el-form-item>

    <el-form-item label="根目录">
      <el-input
        placeholder="请填写根目录"
        v-model="cosConfigForm.localRootPath"
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
        localDomain: "",
        localPrefix: "",
        localRootPath: "",
      },
      cosConfigRules: {
        localDomain: [{ required: true, message: "内容不能为空" }],
        localPrefix: [{ required: true, message: "内容不能为空" }],
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
        this.cosConfigForm.type = 0;
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
