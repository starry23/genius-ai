<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-07-02 17:08:56
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-07-08 12:36:29
-->
<template>
  <el-dialog
    v-if="dialogVisible"
    :destroy-on-close="true"
    :title="rowData ? '编辑' : '新增'"
    :visible.sync="dialogVisible"
    width="650px"
  >
    <el-form ref="form" :rules="rules" :model="redemptionForm" label-width="150px">
      <el-form-item label="生成数量" prop="num">
        <el-input-number
          style="width: 100%"
          v-model="redemptionForm.num"
          :min="0"
          placeholder="请输入生成数量"
        ></el-input-number>
      </el-form-item>

      <el-form-item label="兑换码额度" prop="eachAmount">
        <el-input-number
          style="width: 100%"
          v-model="redemptionForm.eachAmount"
          :min="0"
          placeholder="请输入兑换码额度"
        ></el-input-number>
      </el-form-item>

      <el-form-item label="过期时间" prop="expirationDate">
        <el-date-picker
      
          style="width: 100%"
          v-model="redemptionForm.expirationDate"
          type="datetime"
          placeholder="选择过期时间"
        >
        </el-date-picker>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="submit">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { createRedemptionCode } from "@/api/redemptionCode";
export default {
  props: ["getDataListFn"],
  data() {
    return {
      rowData: null,
      dialogVisible: false,
      redemptionForm: {
        num: "",
        eachAmount: "",
        expirationDate: "",
      },
      rules: {
        num: [{ required: true, message: "数量不能为空" }],
        eachAmount: [{ required: true, message: "兑换码额度不能为空" }],
        expirationDate: [{ required: true, message: "过期时间不能为空" }],
      },
    };
  },
  methods: {
    getPage(data) {
      console.log(data, "2222");
      this.rowData = null;
      this.dialogVisible = true;
      this.redemptionForm = {
        num: "",
        eachAmount: "",
      };
      if (data) {
        this.rowData = data;
        this.redemptionForm = JSON.parse(JSON.stringify(data));
      }
    },

    handleClose() {
      this.dialogVisible = false;
    },

    /**
     * @description: 新建
     * @return {*}
     * @author: jinglin.gao
     */
    async createRedemptionCodeFn() {
      try {
        let res = await createRedemptionCode(this.redemptionForm);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "新建成功",
          });
          this.handleClose(); // 关闭窗口 或 保存配置项 按钮被点击后 执行此函数
          this.getDataListFn();
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
          this.createRedemptionCodeFn();
        } else {
          return false;
        }
      });
    },
  },
};
</script>
