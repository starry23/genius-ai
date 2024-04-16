<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-06 13:15:01
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-06-25 12:30:00
-->
<template>
  <el-dialog
    :destroy-on-close="true"
    title="咒语详情"
    :visible.sync="dialogVisible"
    width="850px"
  >
    <el-input type="textarea" :rows="5" v-model="promptDetali"></el-input>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      dialogVisible: false,
      promptDetali: "",
    };
  },

  methods: {
    async getPage(data) {
      console.log(data, "2222");
      this.dialogVisible = true;
      this.promptDetali = data.filePrompt;
    },
    handleClose() {
      this.dialogVisible = false;
    },
  },
};
</script>
<style lang="scss" scoped>
.imgUrl {
  width: 500px;
  height: 500px;
  display: block;
  margin: 0 auto;
}
</style>
