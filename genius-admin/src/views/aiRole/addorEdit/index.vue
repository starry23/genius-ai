<template>
  <el-dialog
    :destroy-on-close="true"
    :title="rowData ? '编辑角色' : '新建角色'"
    :visible.sync="dialogVisible"
    width="550px"
  >
    <el-form ref="form" :rules="rules" :model="form" label-width="150px">
      <el-form-item label="角色类型" prop="roleType">
        <el-select
          style="width: 100%"
          v-model="form.roleType"
          placeholder="请选择角色类型"
        >
          <el-option
            v-for="item in roleTypeList"
            :key="item.key"
            :value="item.key"
            :label="item.value"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="角色名称" prop="roleName">
        <el-input placeholder="请输入角色名称" v-model="form.roleName"></el-input>
      </el-form-item>

      <el-form-item label="角色图标(jpg或png)" prop="imageUrl">
        <el-upload
          name="imageUrl"
          :data="uploadFileParams"
          :file-list="fileList"
          :multiple="false"
          class="upload-demo"
          ref="upload"
          :action="uploadUrl"
          :before-upload="beforeUpload"
          :on-remove="handleRemove"
          :on-success="handlerSuccess"
          :on-change="uploadFile"
          accept="image/jpg,image/jpeg,image/png,image/svg"
        >
          <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
          <img
            v-if="previewImgUrl || form.fullUrl"
            :src="previewImgUrl || form.fullUrl"
            class="el-upload--picture"
          />
        </el-upload>
      </el-form-item>

      <el-form-item label="角色描述" prop="roleDesc">
        <el-input
          type="textarea"
          placeholder="请输入角色描述"
          v-model="form.roleDesc"
        ></el-input>
      </el-form-item>

      <el-form-item label="提示词" prop="prompt">
        <el-input
          rows="5"
          type="textarea"
          placeholder="请输入提示词"
          v-model="form.prompt"
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
import { saveOrUpdateAiRole, getAiRoleType } from "@/api/aiRole";
export default {
  props: ["getAiRolesListFn"],
  data() {
    return {
      uploadFileParams: {
        file: "",
        viewType: 2,
      },
      previewImgUrl: "",
      uploadUrl: "/api/upload/file",
      fileList: [],
      roleTypeList: [],
      rowData: null,
      dialogVisible: false,
      form: {
        roleName: "",
        roleDesc: "",
        prompt: "",
        roleType: "",
        imageUrl: "",
      },
      rules: {
        roleName: [{ required: true, message: "角色名称不能为空" }],
        roleDesc: [{ required: true, message: "角色描述不能为空" }],
        prompt: [{ required: true, message: "提示词不能为空" }],
        roleType: [{ required: true, message: "角色类型不能为空" }],
        imageUrl: [{ required: false, message: "角色图片不能为空" }],
      },
    };
  },
  methods: {
    getPage(data) {
      this.dialogVisible = true;
      this.form = {
        roleName: "",
        roleDesc: "",
        prompt: "",
        roleType: "",
        imageUrl: "",
      };
      this.previewImgUrl = "";
      if (data) {
        this.rowData = data;
        console.log(this.rowData, "this.rowData");
        this.form = JSON.parse(JSON.stringify(data));
      }

      this.getAiRoleTypeFn();
    },
    uploadFile(item) {
      this.previewImgUrl = URL.createObjectURL(item.raw); // 图片上传浏览器回显地址
    },
    handlerSuccess(data) {
      if (data.code === 200) {
        this.form.imageUrl = data.result.path;
        this.$message({
          type: "success",
          message: "图片上传成功",
        });
      } else {
        this.$message({
          type: "error",
          message: data.message,
        });
      }
    },

    handleRemove() {
      this.fileList = [];
      this.form.imageUrl = "";
      this.previewImgUrl = "";
    },

    beforeUpload(file) {
      this.uploadFileParams.file = file;
      return true;
    },
    submitUpload() {
      this.$refs.upload.submit();
    },

    // 获取ai角色类型
    async getAiRoleTypeFn() {
      try {
        let res = await getAiRoleType();
        if (res.code === 200) {
          this.roleTypeList = res.result || [];
        }
      } catch (error) {
        console.log(error);
      }
    },
    handleClose() {
      this.dialogVisible = false;
    },

    /**
     * @description: 新建角色
     * @return {*}
     * @author: jinglin.gao
     */
    async saveOrUpdateFn() {
      try {
        let res = await saveOrUpdateAiRole(this.form);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "操作成功",
          });
          this.handleClose(); // 关闭窗口 或 保存配置项 按钮被点击后 执行此函数
          this.getAiRolesListFn();
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

<style lang="scss" scoped>
.el-upload--picture {
  width: 100px;
  height: 100px;
  display: block;
  margin: 0 auto;
  margin-bottom: 10px;
}
</style>
