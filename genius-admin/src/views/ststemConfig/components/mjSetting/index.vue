<!--
 * @Description:

 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-10 13:50:07
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

    <!-- apeto  -->
    <ApetoMj v-if="activeName === 'apeto'" />

    <!-- apeto  -->
    <BltcyMj v-if="activeName === 'bltcy'" />
  </div>
</template>

<script>
import ApetoMj from "./components/apetoMj";
import BltcyMj from "./components/bltcyMj";
import { getSetting } from "@/api/systemConfig";
export default {
  components: {
    ApetoMj,
    BltcyMj
  },
  data() {
    return {
      sysConfigForm: null,
      activeName: "bltcy",
      tabsList: [
        // {
        //   label: "GeniusMj",
        //   value: "apeto",
        // },
        {
          label: "柏拉图Mj",
          value: "bltcy",
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
        let res = await getSetting();
        if (res.code === 200) {
          this.activeName = res.result.aiImageServiceType;
          this.sysConfigForm = res.result;
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
