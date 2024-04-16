<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-06 13:15:01
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-06-25 12:18:33
-->
<template>
  <el-dialog
    :destroy-on-close="true"
    title="图片详情"
    :visible.sync="dialogVisible"
    width="850px"
  >
    <img class="imgUrl" :src="imgUrl" alt="" />
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      dialogVisible: false,
      imgUrl: "",
    };
  },

  methods: {
    async getPage(data) {
      console.log(data, "2222");
      this.dialogVisible = true;
      this.imgUrl = data.cosUrl;
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
