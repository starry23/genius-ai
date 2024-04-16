<template>
  <div class="ststem_config-warp">
    <el-tabs v-model="activeName">
      <el-tab-pane
        v-for="item in tabsList"
        :label="item.label"
        :name="item.value"
        :key="item.value"
      ></el-tab-pane>
    </el-tabs>
    <!-- 网站配置 -->
    <div class="ststem_config-content">
      <div class="ststem_config-box" v-if="activeName === '0'">
        <webSetting />
      </div>

      <!-- 邮箱设置 -->
      <div class="ststem_config-box" v-if="activeName === '1'">
        <emailSetting />
      </div>

      <!-- 百度敏感词设置 -->
      <div class="ststem_config-box" v-if="activeName === '2'">
        <baiduSetting />
      </div>

      <!-- 阿里支付设置 -->
      <div class="ststem_config-box" v-if="activeName === '3'">
        <aliPaySetting />
      </div>

      <!-- 活动配置 -->
      <div class="ststem_config-box" v-if="activeName === '4'">
        <activitySetting />
      </div>

      <!-- 阿里短信配置 -->
      <div class="ststem_config-box" v-if="activeName === '5'">
        <aliSmsSetting />
      </div>

      <!-- mj设置 -->
      <div class="ststem_config-box" v-if="activeName === '6'">
        <mjSetting />
      </div>

      <!-- 菜单配置 -->
      <div class="ststem_config-box" v-if="activeName === '7'">
        <menuSetting />
      </div>

      <!-- 对象配置 -->
      <div class="ststem_config-box" v-if="activeName === '8'">
        <objectSetting />
      </div>

      <!-- 向量数据库配置 -->
      <div class="ststem_config-box" v-if="activeName === '9'">
        <vectorDatabase />
      </div>
    </div>
  </div>
</template>

<script>
import webSetting from "./components/webSetting";
import emailSetting from "./components/emailSetting";
import baiduSetting from "./components/baiduSetting";
import aliPaySetting from "./components/aliPaySetting";
import activitySetting from "./components/activitySetting";
import aliSmsSetting from "./components/aliSmsSetting";
import mjSetting from "./components/mjSetting";
import menuSetting from "./components/menuSetting";
import objectSetting from "./components/objectSetting";
import vectorDatabase from "./components/vectorDatabase";
export default {
  data() {
    return {
      activeName: "0",
      tabsList: [
        {
          label: "网站配置",
          value: "0",
        },
        {
          label: "活动配置",
          value: "4",
        },
        {
          label: "邮箱设置",
          value: "1",
        },
        {
          label: "百度智能云敏感校验设置",
          value: "2",
        },
        {
          label: "支付配置",
          value: "3",
        },
        {
          label: "阿里短信配置",
          value: "5",
        },
        {
          label: "mj配置",
          value: "6",
        },
        {
          label: "菜单配置",
          value: "7",
        },
        {
          label: "对象存储配置",
          value: "8",
        },
        {
          label: "向量数据库配置",
          value: "9",
        },
      ],
    };
  },
  components: {
    webSetting,
    emailSetting,
    baiduSetting,
    aliPaySetting,
    activitySetting,
    aliSmsSetting,
    mjSetting,
    menuSetting,
    objectSetting,
    vectorDatabase,
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
