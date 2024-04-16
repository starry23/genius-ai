<!--
 * @Description: 
 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-02-03 10:35:40
-->
<template>
  <el-form
    :model="mjForm"
    :rules="aiImageRules"
    ref="aiImageFormRef"
    label-width="150px"
    class="sys-ruleForm"
  >
    <div class="content">
      <el-form-item label="租户ID" prop="tenantId">
        <el-input-number
          style="width: 100%"
          :min="0"
          placeholder="请填写租户ID"
          v-model="mjForm.tenantId"
        ></el-input-number>
      </el-form-item>

      <el-form-item label="apiKey" prop="apiKey">
        <el-input placeholder="请填写apiKey" v-model="mjForm.apiKey"></el-input>
      </el-form-item>

      <div class="mj_user-info">
        <el-form-item label="租户余额">
          <el-input disabled v-model="tenantInfo.accountCount"></el-input>
        </el-form-item>

        <el-form-item label="租户状态">
          <el-input disabled v-model="tenantInfo.status"></el-input>
        </el-form-item>

        <el-form-item label="联系人	">
          <el-input disabled v-model="tenantInfo.contactMobile"></el-input>
        </el-form-item>

        <el-form-item label="过期时间		">
          <el-input disabled v-model="tenantInfo.expireTime"></el-input>
        </el-form-item>
      </div>

      <div class="errorInfo" v-if="errorInfo">
        {{ errorInfo }}
      </div>
    </div>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="onSubmit">保存配置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { saveSetting, getSetting, getTenantInfo } from "@/api/systemConfig";

export default {
  data() {
    return {
      sysConfig: null,
      mjForm: {
        tenantId: 0,
        apiKey: "",
      },
      tenantInfo: {
        accountCount: "",
        status: "",
        contactMobile: "",
        expireTime: "",
      },
      aiImageRules: {
        tenantId: [{ required: true, message: "请输入租户id" }],
        apiKey: [{ required: true, message: "请输入apiKey" }],
      },
      errorInfo: "",
    };
  },
  mounted() {
    // 获取系统配置
    this.getSettingFn();
    this.$refs.aiImageFormRef.clearValidate();
    // 重置输入框值为空。这是一种方法，不要仅仅获取
  },
  methods: {
    // 获取租户信息
    async getTenantInfoFn() {
      try {
        let res = await getTenantInfo();
        if (res.code === 200) {
          if (res.result) {
            this.tenantInfo = res.result;
            switch (this.tenantInfo.status) {
              case 0:
                this.tenantInfo.status = "正常";
                break;
              case 1:
                this.tenantInfo.status = "停用";
                break;

              default:
                break;
            }
          } else {
            this.errorInfo = res.message;
          }
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
    // 获取系统配置
    async getSettingFn() {
      try {
        let res = await getSetting();
        if (res.code === 200) {
          this.sysConfig = res.result;
          if (res.result.aiImageSetting) {
            this.mjForm = res.result.aiImageSetting;
            this.getTenantInfoFn();
          }

          this.$nextTick(() => {
            if (this.$refs.aiImageFormRef) {
              this.$refs.aiImageFormRef.clearValidate();
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
    onSubmit() {
      // saveSetting
      this.$refs.aiImageFormRef.validate((valid) => {
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
        this.sysConfig.aiImageSetting = this.mjForm;
        this.sysConfig.aiImageServiceType = "apeto";
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
