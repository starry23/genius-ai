<!--
 * @Description:

 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-11 13:52:42
-->
<template>
  <div class="objectSetting">
    <div class="cosTypeBox">
      <template>
        <el-radio
          v-for="item in tabsList"
          :key="item.value"
          v-model="activeName"
          :label="item.value"
          >{{ item.label }}</el-radio
        >
      </template>
    </div>

    <localCosConfig :config="cosConfigForm" v-show="activeName === '0'" />
    <cosConfig1 :config="cosConfigForm" v-show="activeName === '1'" />
    <cosConfig2 :config="cosConfigForm" v-show="activeName === '2'" />
    <cosConfig3 :config="cosConfigForm" v-show="activeName === '3'" />
  </div>
</template>

<script>
import localCosConfig from "./components/localCosConfig";
import cosConfig1 from "./components/cosConfig1";
import cosConfig2 from "./components/cosConfig2";
import cosConfig3 from "./components/cosConfig3";
import { getOssSetting } from "@/api/systemConfig";
export default {
  components: {
    localCosConfig,
    cosConfig1,
    cosConfig2,
    cosConfig3,
  },
  data() {
    return {
      cosConfigForm: null,
      activeName: "0",
      tabsList: [
        {
          label: "本地存储",
          value: "0",
        },
        {
          label: "七牛云oss",
          value: "1",
        },
        {
          label: "阿里云oss",
          value: "2",
        },
        {
          label: "腾讯云cos",
          value: "3",
        },
      ],
    };
  },

  mounted() {
    this.getSettingFn();
  },
  methods: {
    // 获取系统配置
    async getSettingFn() {
      try {
        let res = await getOssSetting();
        if (res.code === 200) {
          this.activeName = res.result.type.toString();
          this.cosConfigForm = res.result;
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
