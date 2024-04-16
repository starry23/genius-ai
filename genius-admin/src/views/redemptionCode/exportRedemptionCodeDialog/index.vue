<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-06 13:15:01
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-07-09 17:15:29
-->
<template>
  <el-dialog
    :destroy-on-close="true"
    title="导出兑换码"
    :visible.sync="dialogVisible"
    width="550px"
  >
    <el-form ref="form" :rules="rules" :model="form" label-width="150px">
      <el-form-item label="批次号" prop="batch">
        <el-input
          style="width: 100%"
          :min="0"
          :step="0.1"
          placeholder="请输入批次号"
          v-model="form.batch"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="submit">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { exportByBatchNo } from "@/api/redemptionCode";
export default {
  props: ["getDataList"],
  data() {
    return {
      copyAmount: 0,
      dialogVisible: false,
      memberList: [],
      form: {
        batch: "",
      },
      rules: {
        batch: [{ required: true, message: "批次号不能为空" }],
      },
    };
  },

  methods: {
    getPage(data) {
      console.log(data, "2222");
      this.dialogVisible = true;
      this.form = {
        batch: "",
      };
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
        let response = await exportByBatchNo(this.form.batch);

        const url = window.URL.createObjectURL(new Blob([response]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", `${this.form.batch}.xlsx`); // 设置要保存的文件名及扩展名
        document.body.appendChild(link);
        link.click();
      } catch (error) {
        console.log(error);
      }
    },

    // 提交
    submit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.commissionWithdrawFn();
        } else {
          return false;
        }
      });
    },
  },
};
</script>
