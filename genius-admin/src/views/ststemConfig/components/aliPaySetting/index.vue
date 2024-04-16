<!--
 * @Description: 
 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jl.g
 * @LastEditTime: 2023-07-30 10:36:24
-->
<template>
  <div>
    <p class="payInfo">
      如果微信和支付宝都不开启支付,下面配置的提示语会在用户进行支付时提示,告诉用户关闭支付的原因。输入&lt;br
      /&gt; 进行换行。
    </p>
    <el-input
      type="textarea"
      placeholder="请填写提示语"
      v-model="closePayNotice"
      row="5"
    ></el-input>
    <el-tabs v-model="activeName">
      <el-tab-pane
        v-for="item in tabsList"
        :label="item.label"
        :name="item.value"
        :key="item.value"
      ></el-tab-pane>
    </el-tabs>

    <aliPay :closePayNotice="closePayNotice" v-if="activeName === '1'" />

    <wechatPay :closePayNotice="closePayNotice" v-if="activeName === '2'" />
  </div>
</template>

<script>
import aliPay from "./components/aliPay";
import wechatPay from "./components/wechatPay";
import { getPaySetting } from "@/api/aliPaySetting";

export default {
  components: {
    aliPay,
    wechatPay,
  },
  data() {
    return {
      closePayNotice: "",
      activeName: "1",
      tabsList: [
        {
          label: "支付宝配置",
          value: "1",
        },
        {
          label: "微信配置",
          value: "2",
        },
      ],
    };
  },

  methods: {
    // 获取系统配置
    async getSettingFn() {
      try {
        let res = await getPaySetting();
        if (res.code === 200) {
          this.closePayNotice = res.result.closePayNotice;
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

  mounted() {
    this.getSettingFn();
  },
};
</script>
