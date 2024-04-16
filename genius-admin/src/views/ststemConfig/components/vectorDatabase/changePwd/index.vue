<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-06 13:15:01
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-15 16:20:57
-->
<template>
  <el-dialog
    :destroy-on-close="true"
    title="修改密码"
    :visible.sync="dialogVisible"
    width="550px"
  >
    <el-form ref="form" :rules="rules" :model="form" label-width="150px">
      <el-form-item label="旧密码" prop="oldPassword">
        <el-input v-model="form.oldPassword" placeholder="请输入内容"></el-input>
      </el-form-item>

      <el-form-item label="新密码" prop="password">
        <el-input v-model="form.password" placeholder="请输入内容"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="submit">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updatePassword } from "@/api/systemConfig";
export default {
  props: ["getMilvusConfig"],
  data() {
    return {
      dialogVisible: false,
      form: {
        password: "",
        oldPassword: "",
      },
      rules: {
        password: [{ required: true, message: "请输入内容" }],
        oldPassword: [{ required: true, message: "请输入内容" }],
      },
    };
  },

  methods: {
    getPage(data) {
      this.dialogVisible = true;
      this.form = {
        password: "",
        oldPassword: data.password,
        ip:data.ip,
        port:data.port,
        name:data.name
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
    async updatePasswordFn() {
      try {
        let res = await updatePassword(this.form);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "操作成功",
          });
          this.getMilvusConfig();
          this.handleClose(); // 关闭窗口 或 保存配置项 按钮被点击后 执行此函数
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
          this.updatePasswordFn();
        } else {
          return false;
        }
      });
    },
  },
};
</script>
