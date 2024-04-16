<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-06 13:15:01
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-14 16:53:16
-->
<template>
  <el-dialog
    :destroy-on-close="true"
    title="充值金额"
    :visible.sync="dialogVisible"
    width="550px"
  >
    <el-form ref="form" :rules="rules" :model="form" label-width="150px">
      <el-form-item label="金额" prop="count">
        <el-input type="number" v-model="form.count" placeholder="请输入金额"></el-input>
      </el-form-item>

      <el-form-item label="账户类型" prop="accountType">
        <el-select style="width: 100%" v-model="form.accountType">
          <el-option
            v-for="item in accountTypeList"
            :key="item.key"
            :value="item.key"
            :label="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="submit">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { depositCurrency } from "@/api/userInfoList";
export default {
  props: ["getUserInfoList", "accountTypeList"],
  data() {
    return {
      dialogVisible: false,
      form: {
        userId: "",
        count: "",
        accountType: "",
      },
      rules: {
        count: [{ required: true, message: "请输入内容" }],
        accountType: [{ required: true, message: "请选择内容" }],
      },
    };
  },

  methods: {
    getPage(data) {
      this.dialogVisible = true;
      this.form = {
        userId: data.id,
        count: "",
        accountType: "",
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
    async depositCurrencyFn() {
      try {
        let res = await depositCurrency(this.form);
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
          this.depositCurrencyFn();
        } else {
          return false;
        }
      });
    },
  },
};
</script>
