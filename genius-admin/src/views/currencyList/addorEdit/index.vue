<template>
  <el-dialog
    v-if="dialogVisible"
    :destroy-on-close="true"
    :title="rowData ? '编辑' : '新增'"
    :visible.sync="dialogVisible"
    width="650px"
  >
    <el-form ref="form" :rules="rules" :model="vipStrategyListForm" label-width="150px">
      <el-form-item label="币数量" prop="currencyCount">
        <el-input
          type="number"
          placeholder="请输入币数量"
          v-model="vipStrategyListForm.currencyCount"
        ></el-input>
      </el-form-item>

      <el-form-item label="商品金额" prop="currencyAmount">
        <el-input
          type="number"
          placeholder="请输入商品金额"
          v-model="vipStrategyListForm.currencyAmount"
        ></el-input>
      </el-form-item>

      <el-form-item label="划线金额" prop="lineAmount">
        <el-input
          type="number"
          placeholder="请输入划线金额"
          v-model="vipStrategyListForm.lineAmount"
        ></el-input>
      </el-form-item>

      <el-form-item label="推荐" prop="recommend">
        <el-switch v-model="vipStrategyListForm.recommend"> </el-switch>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="submit">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { memberRightsConfigSaveOrUpdate } from "@/api/currencyList";
export default {
  props: ["getMemberRightsList"],
  data() {
    return {
      rowData: null,
      dialogVisible: false,
      resMemberList: [],
      vipStrategyListForm: {
        currencyCount: "",
        currencyAmount: "",
        lineAmount: "",
        recommend: false,
      },
      rules: {
        currencyCount: [{ required: true, message: "请输入币数量" }],
        currencyAmount: [{ required: true, message: "请输入商品金额" }],
        lineAmount: [{ required: true, message: "请输入划线金额" }],
        recommend: [{ required: false, message: "请输入划线金额" }],
      },
    };
  },
  methods: {
    getPage(data) {
      console.log(data, "2222");
      this.rowData = null;
      this.dialogVisible = true;
      this.vipStrategyListForm = {
        currencyCount: "",
        currencyAmount: "",
        lineAmount: "",
        recommend: false,
      };
      if (data) {
        this.rowData = data;
        this.vipStrategyListForm = JSON.parse(JSON.stringify(data));
      }
    },

    handleClose() {
      this.dialogVisible = false;
    },

    /**
     * @description: 新建会员卡
     * @return {*}
     * @author: jinglin.gao
     */
    async memberRightsConfigSaveOrUpdateFn() {
      try {
        let res = await memberRightsConfigSaveOrUpdate(this.vipStrategyListForm);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "新建成功",
          });
          this.handleClose(); // 关闭窗口 或 保存配置项 按钮被点击后 执行此函数
          this.getMemberRightsList();
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
          this.memberRightsConfigSaveOrUpdateFn();
        } else {
          return false;
        }
      });
    },
  },
};
</script>
