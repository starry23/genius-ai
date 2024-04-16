<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-07-16 09:56:00
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-12-19 20:31:23
-->
/* * @Description: * @Version: 2.0 * @Autor: jinglin.gao * @Date: 2023-07-16 09:56:00 *
@LastEditors: jinglin.gao * @LastEditTime: 2023-07-16 09:56:20 */
<template>
  <div class="gptConfig">
    <el-tabs v-model="activeName">
      <el-tab-pane
        v-for="item in tabsList"
        :label="item.label"
        :name="item.value"
        :key="item.value"
      ></el-tab-pane>
    </el-tabs>

    <!-- gpt3配置 -->
    <gpt3Config v-if="activeName === 'gpt3'" />

    <!-- gpt4配置 -->
    <gpt4Config v-if="activeName === 'gpt4'" />

    <!-- gpts配置 -->
    <gptsConfig v-if="activeName === 'gpts'" />

    <!-- dall3配置 -->
    <dall3Config v-if="activeName === 'dall3'" />

    <!-- 文心千帆配置 -->
    <ernieBotConfig v-if="activeName === 'ernieBot'" />

    <!-- 星火配置 -->
    <sparkDesk v-if="activeName === 'sparkdesk'" />;
  </div>
</template>

<script>
import gpt3Config from "./components/gpt3Config";
import gpt4Config from "./components/gpt4Config";
import gptsConfig from "./components/gptsConfig";
import ernieBotConfig from "./components/ernieBotConfig";
import sparkDesk from "./components/sparkDesk";
import dall3Config from "./components/dall3Config";
export default {
  components: {
    gpt3Config,
    gpt4Config,
    gptsConfig,
    ernieBotConfig,
    sparkDesk,
    dall3Config,
  },
  data() {
    return {
      activeName: "gpt3",
      tabsList: [
        {
          label: "gpt3配置",
          value: "gpt3",
        },
        {
          label: "gpt4配置",
          value: "gpt4",
        },
        {
          label: "gpts配置",
          value: "gpts",
        },

        {
          label: "dall3配置",
          value: "dall3",
        },
        {
          label: "文心千帆配置",
          value: "ernieBot",
        },
        {
          label: "星火配置",
          value: "sparkdesk",
        },
      ],
    };
  },
};
</script>

<style lang="scss">
@import "./index.scss";
</style>
