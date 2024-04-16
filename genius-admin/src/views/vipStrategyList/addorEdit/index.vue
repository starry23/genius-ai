<template>
  <el-dialog
    v-if="dialogVisible"
    :destroy-on-close="true"
    :title="rowData ? '编辑' : '新增'"
    :visible.sync="dialogVisible"
    width="550px"
  >
    <el-form ref="form" :rules="rules" :model="vipStrategyListForm" label-width="200px">
      <el-form-item label="权益名称" prop="rightsName">
        <el-input
          placeholder="请输入权益名称"
          v-model="vipStrategyListForm.rightsName"
        ></el-input>
      </el-form-item>
      <el-form-item label="会员卡" prop="memberCode">
        <el-select
          :disabled="this.rowData"
          style="width: 100%"
          v-model="vipStrategyListForm.memberCode"
          placeholder="请选择会员卡"
        >
          <el-option
            v-for="item in resMemberList"
            :key="item.id"
            :label="item.cardName"
            :value="item.cardCode"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="会员权益" prop="rightsType">
        <el-select
          :disabled="this.rowData"
          style="width: 100%"
          v-model="vipStrategyListForm.rightsType"
          placeholder="请选择会员权益"
        >
          <el-option
            v-for="item in rightsTypeList"
            :key="item.key"
            :label="item.value"
            :value="item.key"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="权益描述" prop="rightsDesc">
        <el-input
          type="textarea"
          placeholder="请输入权益描述"
          v-model="vipStrategyListForm.rightsDesc"
        ></el-input>
      </el-form-item>

      <el-form-item
        v-if="
          vipStrategyListForm.rightsType === 1002 ||
          vipStrategyListForm.rightsType == 1003
        "
        label="折扣范围(0.1~1.0)"
        prop="discount"
      >
        <el-input
          type="number"
          style="width: 100%"
          placeholder="1代表不打折"
          v-model="vipStrategyListForm.discount"
        ></el-input>
      </el-form-item>

      <el-form-item
        v-if="
          vipStrategyListForm.rightsType === 1001 ||
          vipStrategyListForm.rightsType === 1006 ||
          vipStrategyListForm.rightsType === 1007
        "
        label="数量"
        prop="count"
      >
        <el-input
          type="number"
          placeholder="请输入内容"
          v-model="vipStrategyListForm.count"
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
import { memberRightsConfigSaveOrUpdate, getRightsType } from "@/api/vipStrategyList";
import { memberList } from "@/api/viplList";
export default {
  props: ["getMemberRightsList"],
  data() {
    return {
      rowData: null,
      dialogVisible: false,
      resMemberList: [],
      vipStrategyListForm: {
        memberCode: "",
        count: "",
        rightsName: "",
        rightsType: "",
        rightsDesc: "",
        discount: 0,
      },
      rules: {
        name: [{ required: true, message: "请输入名称" }],
        memberCode: [{ required: true, message: "请输入会员卡" }],
        count: [{ required: false, message: "请输入会员卡次数" }],
        rightsName: [{ required: true, message: "请输入内容" }],
        rightsType: [{ required: true, message: "请选择会员权益" }],
        rightsDesc: [{ required: true, message: "请输入权益描述" }],
        discount: [{ required: false, message: "请输入折扣" }],
      },

      // 权益列表
      rightsTypeList: [],
    };
  },
  methods: {
    getPage(data) {
      console.log(data, "2222");
      this.rowData = null;
      this.dialogVisible = true;
      this.vipStrategyListForm = {
        memberCode: "",
        count: "",
        rightsName: "",
        rightsType: "",
        rightsDesc: "",
      };
      this.getRightsTypeFn();
      this.getMemberList();
      if (data) {
        this.rowData = data;
        this.vipStrategyListForm = JSON.parse(JSON.stringify(data));
      }
    },

    // 获取权益列表
    async getRightsTypeFn() {
      try {
        let res = await getRightsType();
        if (res.code === 200) {
          this.rightsTypeList = res.result || [];
        }
      } catch (error) {
        console.log(error);
      }
    },

    /**
     * @description: 获取会员列表
     * @return {*}
     * @author: jinglin.gao
     */
    async getMemberList() {
      try {
        let res = await memberList();
        if (res.code === 200) {
          this.resMemberList = res.result || [];
        }
      } catch (error) {
        console.log(error);
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
            message: "新建权益成功",
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
      if (this.vipStrategyListForm.discount == 0) {
        this.$message({
          type: "error",
          message: "折扣不能为0",
        });
        return;
      }

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
