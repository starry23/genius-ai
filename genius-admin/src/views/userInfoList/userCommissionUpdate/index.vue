<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-06 13:15:01
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-14 16:49:31
-->
<template>
  <el-dialog
    :destroy-on-close="true"
    title="修改拉新数量"
    :visible.sync="dialogVisible"
    width="550px"
  >
    <el-form ref="form" :rules="rules" :model="form" label-width="150px">
      <el-form-item label="数量" prop="count">
        <el-input type="number" v-model="form.num" placeholder="请输入数量"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="submit">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { userCommissionUpdate } from "@/api/userInfoList";
export default {
  props: ["getUserInfoList"],
  data() {
    return {
      dialogVisible: false,
      form: {
        userId: "",
        num: "",
      },
      rules: {
        num: [{ required: true, message: "请输入内容" }],
      },
    };
  },

  methods: {
    getPage(data) {
      console.log(data, "222");
      this.dialogVisible = true;
      this.form = {
        userId: data.id,
        num: data.inviteCount,
      };
    },
    handleClose() {
      this.dialogVisible = false;
    },

    /**
     * @description: 会员卡充值
     * @return {*}
     * @author: jinglin.gao
     */
    async userCommissionUpdateFn() {
      try {
        let res = await userCommissionUpdate(this.form);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "充值成功",
          });
          this.handleClose(); // 关闭窗口 或 保存配置项 按钮被点击后 执行此函数
          this.getUserInfoList();
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

    // 提交
    submit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.userCommissionUpdateFn();
        } else {
          return false;
        }
      });
    },
  },
};
</script>
