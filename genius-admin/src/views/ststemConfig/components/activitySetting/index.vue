<!--
 * @Description: 
 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-20 14:26:58
-->
<template>
  <el-form
    :model="activityForm"
    :rules="activityRules"
    ref="activityFormRef"
    label-width="200px"
    class="sys-ruleForm"
  >
    <div class="content">
      <el-form-item label="邀请赠送虚拟币" prop="inviteGiveCurrency">
        <el-input
          type="Number"
          placeholder="设置为0 则代表不赠送"
          v-model="activityForm.inviteGiveCurrency"
        ></el-input>
      </el-form-item>

      <el-form-item label="注册赠送虚拟币	" prop="registerGiveCurrency">
        <el-input
          type="Number"
          placeholder="设置为0 则代表不赠送"
          v-model="activityForm.registerGiveCurrency"
        ></el-input>
      </el-form-item>

      <el-form-item label="公众号领取虚拟币" prop="mpGiveCurrency">
        <el-input
          type="Number"
          placeholder="设置为0 则代表不赠送"
          v-model="activityForm.mpGiveCurrency"
        ></el-input>
      </el-form-item>

      <el-form-item label="余额为0后弹窗赠送代币" prop="alertCurrency">
        <el-input
          type="Number"
          placeholder="设置为0 则代表不赠送"
          v-model="activityForm.alertCurrency"
        ></el-input>
      </el-form-item>

      <el-form-item label="是否开启返佣">
        <el-switch v-model="activityForm.rebateConfig.enable" active-color="#13ce66">
        </el-switch>
      </el-form-item>

      <div class="rebateConfigDiv">
        <p class="rebateConfig">一级代理</p>
        <el-form-item label="邀新数量">
          <el-input-number
            style="width: 100%"
            :disabled="!activityForm.rebateConfig.enable"
            placeholder="邀新数量"
            v-model="rebate1.num"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="返佣比例">
          <el-input-number
            style="width: 100%"
            :disabled="!activityForm.rebateConfig.enable"
            :min="1"
            :max="100"
            placeholder="返佣比例"
            v-model="rebate1.rate"
          ></el-input-number>
        </el-form-item>
        <span class="unit">%</span>
      </div>

      <div class="rebateConfigDiv">
        <p class="rebateConfig">二级代理</p>
        <el-form-item label="邀新数量">
          <el-input-number
            style="width: 100%"
            :disabled="!activityForm.rebateConfig.enable"
            type="number"
            placeholder="邀新数量"
            v-model="rebate2.num"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="返佣比例">
          <el-input-number
            style="width: 100%"
            :disabled="!activityForm.rebateConfig.enable"
            :min="1"
            :max="100"
            placeholder="返佣比例"
            v-model="rebate2.rate"
          ></el-input-number>
        </el-form-item>
        <span class="unit">%</span>
      </div>

      <div class="rebateConfigDiv">
        <p class="rebateConfig">三级代理</p>
        <el-form-item label="邀新数量">
          <el-input-number
            style="width: 100%"
            :disabled="!activityForm.rebateConfig.enable"
            placeholder="邀新数量"
            v-model="rebate3.num"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="返佣比例">
          <el-input-number
            style="width: 100%"
            :disabled="!activityForm.rebateConfig.enable"
            :min="1"
            :max="100"
            placeholder="返佣比例"
            v-model="rebate3.rate"
          ></el-input-number>
        </el-form-item>
        <span class="unit">%</span>
      </div>

      <div class="info">当用户邀新数量大于当前代理配置的数量时,自动升级为下一级代理</div>
    </div>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="onactivityFormSubmit">保存配置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { saveSetting, getSetting } from "@/api/systemConfig";

export default {
  data() {
    return {
      sysConfig: null,
      activityForm: {
        inviteGiveCurrency: 0,
        registerGiveCurrency: 0,
        mpGiveCurrency: 0,
        alertCurrency: 0,
        rebateConfig: {
          enable: false,
        },
      },
      rebate1: {
        num: "",
        rate: "",
      },

      rebate2: {
        num: "",
        rate: "",
      },

      rebate3: {
        num: "",
        rate: "",
      },

      activityRules: {
        inviteGiveCurrency: [{ required: true, message: "请输入内容" }],
        registerGiveCurrency: [{ required: true, message: "请输入内容" }],
        mpGiveCurrency: [{ required: true, message: "请输入内容" }],
        alertCurrency: [{ required: true, message: "请输入内容" }],
      },
    };
  },
  mounted() {
    // 获取系统配置
    this.getSettingFn();
    this.$refs.activityFormRef.clearValidate();
    // 重置输入框值为空。这是一种方法，不要仅仅获取
  },
  methods: {
    // 获取系统配置
    async getSettingFn() {
      try {
        let res = await getSetting();
        if (res.code === 200) {
          this.sysConfig = res.result;
          this.activityForm = res.result.activitySetting;
          if (this.activityForm?.rebateConfig?.rebate1) {
            this.rebate1 = this.activityForm.rebateConfig.rebate1;
          }

          if (this.activityForm?.rebateConfig?.rebate2) {
            this.rebate2 = this.activityForm.rebateConfig.rebate2;
          }

          if (this.activityForm?.rebateConfig?.rebate3) {
            this.rebate3 = this.activityForm.rebateConfig.rebate3;
          }

          this.$nextTick(() => {
            if (this.$refs.activityFormRef) {
              this.$refs.activityFormRef.clearValidate();
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
    onactivityFormSubmit() {
      // saveSetting
      this.$refs.activityFormRef.validate((valid) => {
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
        this.activityForm.rebateConfig = {
          enable: this.activityForm.rebateConfig.enable,
          rebate1: this.rebate1,
          rebate2: this.rebate2,
          rebate3: this.rebate3,
        };
        this.sysConfig.activitySetting = this.activityForm;
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
