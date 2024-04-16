<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2024-01-06 11:00:44
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-04 21:49:54
-->
<template>
  <el-dialog
    :destroy-on-close="true"
    :title="rowData ? '编辑配置' : '新增配置'"
    :visible.sync="dialogVisible"
    width="550px"
  >
    <el-form ref="form" :rules="rules" :model="form" label-width="100px">
      <el-form-item label="商户号" prop="mchId">
        <el-row :gutter="10" align="middle" type="flex">
          <el-col :span="18">
            <el-input placeholder="请输入商户号" v-model="form.mchId"></el-input>
          </el-col>

          <el-col :span="4">
            <el-button type="primary" @click="generateMchIdFn">一键生成</el-button>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="商户描述" prop="mchDesc">
        <el-input
          type="textarea"
          :rows="3"
          placeholder="请输入商户描述"
          v-model="form.mchDesc"
        ></el-input>
      </el-form-item>
      <el-form-item label="公钥" prop="publicKey">
        <el-input
          type="textarea"
          :rows="3"
          placeholder="请输入公钥"
          v-model="form.publicKey"
        ></el-input>
      </el-form-item>

      <el-form-item label="私钥" prop="privateKey">
        <el-input
          type="textarea"
          :rows="3"
          placeholder="请输入私钥"
          v-model="form.privateKey"
        ></el-input>
      </el-form-item>

      <el-form-item label="跳转菜单编码">
        <el-select
          style="width: 100%"
          v-model="form.menuCode"
          placeholder="请选择跳转菜单编码"
        >
          <el-option
            v-for="item in menuList"
            :key="item.key"
            :label="item.value"
            :value="item.key"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <div class="info">
      <a target="_blank" href="https://www.yuque.com/apetoo/qri3fg/nqzgzc7x0mvdi2wn"
        >点击查看接入流程文档</a
      >
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="generateKeyPairFn">生成公钥&私钥</el-button>
      <el-button type="primary" @click="submit">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import {
  generateKeyPairApi,
  generateMchIdApi,
  saveOrUpdateApi,
} from "@/api/openPlatformConfig";
export default {
  props: ["getData", "menuList"],
  data() {
    return {
      rowData: null,
      dialogVisible: false,

      form: {
        publicKey: "",
        privateKey: "",
        mchId: "",
        menuCode: "",
        mchDesc: "",
      },
      rules: {
        publicKey: [{ required: true, message: "公钥不能为空" }],
        privateKey: [{ required: true, message: "私钥不能为空" }],
        mchId: [{ required: true, message: "商户号不能为空" }],
      },
    };
  },
  methods: {
    getPage(data) {
      this.dialogVisible = true;
      this.form = {
        publicKey: "",
        privateKey: "",
        mchId: "",
        menuCode: "",
      };
      if (data) {
        this.rowData = data;
        this.form = JSON.parse(JSON.stringify(data));
      }
    },
    handleClose() {
      this.dialogVisible = false;
    },

    // 生成公钥和私钥
    async generateKeyPairFn() {
      try {
        let res = await generateKeyPairApi();
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "生成成功",
          });
          let result = res.result;
          this.form.privateKey = result.privateKey;
          this.form.publicKey = result.publicKey;
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

    // 生成商户号
    async generateMchIdFn() {
      try {
        let res = await generateMchIdApi();
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "生成成功",
          });
          this.form.mchId = res.result || "";
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

    /**
     * @description: 新建会员卡
     * @return {*}
     * @author: jinglin.gao
     */
    async saveOrUpdateFn() {
      try {
        let res = await saveOrUpdateApi(this.form);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "新增配置成功",
          });
          this.handleClose(); // 关闭窗口 或 保存配置项 按钮被点击后 执行此函数
          this.getData();
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
          this.saveOrUpdateFn();
        } else {
          return false;
        }
      });
    },
  },
};
</script>

<style>
.info {
  text-align: center;
  color: blue;
}
</style>
