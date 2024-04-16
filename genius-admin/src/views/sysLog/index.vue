<template>
  <div class="sysLogWarp">
    <div class="tabHead">
      <el-tabs v-model="activeName">
        <el-tab-pane label="mj日志" name="1"></el-tab-pane>
        <el-tab-pane label="问答日志" name="2"> </el-tab-pane>
      </el-tabs>
    </div>
    <div class="content">
      <drawLog v-if="activeName === '1'" />
      <userQuestionLog v-if="activeName === '2'" />
    </div>
  </div>
</template>

<script>
import drawLog from "./components/drawLog";
import userQuestionLog from "./components/userQuestionLog";
export default {
  components: {
    drawLog,
    userQuestionLog,
  },
  data() {
    return {
      activeName: "1",
    };
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
>
