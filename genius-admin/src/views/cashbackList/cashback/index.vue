<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-06 13:15:01
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-07-09 16:03:42
-->
<template>
  <el-dialog
    :destroy-on-close="true"
    title="返佣"
    :visible.sync="dialogVisible"
    width="550px"
  >
    <el-form ref="form" :rules="rules" :model="form" label-width="150px">
      <el-form-item label="返佣金额" prop="amount">
        <el-input-number
          style="width: 100%"
          :min="0"
          :step="0.1"
          placeholder="请输入返佣金额"
          v-model="form.amount"
        ></el-input-number>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="submit">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { commissionWithdraw } from "@/api/accountList";

export default {
  props: ["getDataList"],
  data() {
    return {
      copyAmount: 0,
      dialogVisible: false,
      memberList: [],
      form: {
        userId: "",
        amount: "",
      },
      rules: {
        amount: [{ required: true, message: "返佣不能为空" }],
      },
    };
  },

  methods: {
    getPage(data) {
      console.log(data, "2222");
      this.dialogVisible = true;
      this.form = {
        userId: data.userId,
        amount: data.accountBalance,
      };
      this.copyAmount = data.accountBalance;
    },
    handleClose() {
      this.dialogVisible = false;
    },

    /**
     * @description: 返佣
     * @return {*}
     * @author: jinglin.gao
     */
    async commissionWithdrawFn() {
      try {
        let res = await commissionWithdraw(this.form);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "操作成功",
          });
          this.handleClose(); // 关闭窗口 或 保存配置项 按钮被点击后 执行此函数
          this.getDataList();
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
          if (this.form.amount > this.copyAmount) {
            this.$message({
              type: "error",
              message: "提现金额大于用户持有金额,请认真核对",
            });
            return;
          }
          this.commissionWithdrawFn();
        } else {
          return false;
        }
      });
    },
  },
};
</script>
