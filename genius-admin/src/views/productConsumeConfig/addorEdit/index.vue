<template>
  <el-dialog
    :destroy-on-close="true"
    :title="rowData ? '编辑' : '新建'"
    :visible.sync="dialogVisible"
    width="550px"
  >
    <el-form ref="form" :rules="rules" :model="form" label-width="120px">
      <el-form-item label="商品类型" prop="productType">
        <el-select
          placeholder="请选择商品类型"
          style="width: 100%"
          v-model="form.productType"
        >
          <el-option
            v-for="item in productTypeList"
            :key="item.key"
            :label="item.value"
            :value="item.key"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="消耗虚拟币数量	" prop="consumeCurrency">
        <el-input
          type="number"
          placeholder="请输消耗虚拟币数量"
          v-model="form.consumeCurrency"
        ></el-input>
      </el-form-item>

      <el-form-item label="消费类型" prop="consumeType">
        <el-select
          style="width: 100%"
          placeholder="请选择消费类型"
          v-model="form.consumeType"
        >
          <el-option label="次数" :value="1"></el-option>
          <el-option label="token " :value="2"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-select style="width: 100%" placeholder="请选择状态" v-model="form.status">
          <el-option label="上线" :value="1"></el-option>
          <el-option label="下线" :value="2"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="使用权限" prop="useAuth">
        <el-select
          style="width: 100%"
          placeholder="请选择使用权限"
          v-model="form.useAuth"
        >
          <el-option label="会员" :value="1"></el-option>
          <el-option label="常规用户" :value="2"></el-option>
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
import { memberConfigSaveOrUpdate } from "@/api/productConsumeConfig";
export default {
  props: ["getMemberList", "productTypeList"],
  data() {
    return {
      rowData: null,
      dialogVisible: false,
      form: {
        productType: "",
        consumeCurrency: "",
        consumeType: 1,
        status: 1,
        useAuth: 2,
      },
      rules: {
        productType: [{ required: true, message: "请选择商品类型" }],
        consumeCurrency: [{ required: true, message: "请输消耗虚拟币数量" }],
        consumeType: [{ required: true, message: "请选择消费类型" }],
        status: [{ required: true, message: "请选择类型" }],
        useAuth: [{ required: true, message: "请选择使用权限" }],
      },
    };
  },
  methods: {
    getPage(data) {
      this.dialogVisible = true;
      this.form = {
        productType: "",
        consumeCurrency: "",
        consumeType: 1,
        status: 1,
        useAuth: 2,
      };

      if (data) {
        console.log(data, "2222");
        this.rowData = data;
        this.form = JSON.parse(JSON.stringify(data));
        this.form.productType = Number(this.form.productType);
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
    async memberConfigSaveOrUpdateFn() {
      try {
        let res = await memberConfigSaveOrUpdate(this.form);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "新建会员卡成功",
          });
          this.handleClose(); // 关闭窗口 或 保存配置项 按钮被点击后 执行此函数
          this.getMemberList();
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
          this.memberConfigSaveOrUpdateFn();
        } else {
          return false;
        }
      });
    },
  },
};
</script>
