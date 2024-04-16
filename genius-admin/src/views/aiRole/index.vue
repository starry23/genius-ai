<template>
  <div class="page_warp">
    <div class="page_list-head">
      <el-button @click="addRole" type="primary" size="small">新增角色</el-button>

      <div class="searchBox">
        <el-select style="margin-right:10px" clearable v-model="paramsObj.roleType" placeholder="请选择角色类型">
          <el-option
            v-for="item in roleTypeList"
            :key="item.key"
            :label="item.value"
            :value="item.key"
          ></el-option>
        </el-select>

        <el-input
          clearable
          style="width: 65%"
          v-model="paramsObj.roleName"
          placeholder="请输入角色名称"
        ></el-input>
        <el-button @click="search" style="margin-left: 10px" type="primary"
          >搜索</el-button
        >
      </div>
    </div>
    <div class="content">
      <el-table height="80vh" :data="tableData" style="width: 100%">
        <el-table-column label="序号" type="index"></el-table-column>
        <el-table-column
          v-for="item in tableColumnList"
          :prop="item.prop"
          :label="item.label"
          :key="item.prop"
        >
        </el-table-column>

        <el-table-column label="角色图标" width="80">
          <template slot-scope="scope">
            <!-- scope.row.name  -->
            <img class="roleImgIcon" :src="scope.row.fullUrl" alt="" />
          </template>
        </el-table-column>

        <el-table-column fixed="right" label="操作" width="100">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="() => edit(scope.row)"
              >编辑</el-button
            >

            <el-button type="text" size="small" @click="() => deleteRoleFn(scope.row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="float: right"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="paramsObj.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="paramsObj.size"
        layout="total, sizes, prev, pager, next"
        :total="paramsObj.total"
      >
      </el-pagination>
    </div>
    <addorEdit :getAiRolesListFn="getAiRolesListFn" ref="addorEditRef" />
  </div>
</template>

<script>
import { getAiRolesList, aiRoleDelete, getAiRoleType } from "@/api/aiRole";
import tableColumnList from "./tableColumnList";
import addorEdit from "./addorEdit/index.vue";
export default {
  data() {
    return {
      tableColumnList,
      tableData: [],
      roleTypeList: [],
      paramsObj: {
        size: 10,
        current: 1,
        total: 0,
        roleName: "",
        roleType: "",
      },
    };
  },
  components: {
    addorEdit,
  },

  methods: {
    // 获取ai角色类型
    async getAiRoleTypeFn() {
      try {
        let res = await getAiRoleType();
        if (res.code === 200) {
          this.roleTypeList = res.result || [];
          this.getAiRolesListFn();
        }
      } catch (error) {
        console.log(error);
      }
    },
    search() {
      this.paramsObj.current = 1;
      this.paramsObj.size = 10;
      this.getAiRolesListFn();
    },
    // 删除vip
    deleteRoleFn(data) {
      this.$confirm("此操作将永久删除配置, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        aiRoleDelete(data.id).then((res) => {
          if (res.code === 200) {
            this.$message({
              type: "success",
              message: "删除成功!",
            });
            this.getAiRolesListFn();
          } else {
            this.$message({
              type: "error",
              message: res.message,
            });
          }
        });
      });
    },

    /**
     * @description: 获取角色列表
     * @return {*}
     * @author: jinglin.gao
     */
    async getAiRolesListFn() {
      try {
        let res = await getAiRolesList({
          size: this.paramsObj.size,
          current: this.paramsObj.current,
          roleName: this.paramsObj.roleName,
          roleType: this.paramsObj.roleType,
        });
        if (res.code === 200) {
          let resData = res.result?.records || [];
          resData.forEach((item) => {
            item.roleTypeNameCn = this.roleTypeList.find(
              (v) => v.key === item.roleType
            )?.value;
            if (item.prompt.length > 15) {
              item.abbreviationPrompt = item.prompt.slice(0, 15) + "...";
            }
          });
          this.tableData = resData;
          this.paramsObj.total = res.result?.total;
        }
      } catch (error) {
        console.log(error);
      }
    },

    //
    handleSizeChange(val) {
      this.paramsObj.size = val;
      this.getAiRolesListFn();
    },

    // 页数跳转
    handleCurrentChange(val) {
      this.paramsObj.current = val;
      this.getAiRolesListFn();
    },

    /**
     * @description: 新增会员卡
     * @return {*}
     * @author: jinglin.gao
     */
    addRole() {
      this.$refs.addorEditRef.getPage();
    },

    // 编辑会员卡
    edit(data) {
      this.$refs.addorEditRef.getPage(data);
    },
  },

  mounted() {
    this.getAiRoleTypeFn();
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
