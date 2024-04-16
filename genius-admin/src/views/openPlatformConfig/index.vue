<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2024-01-06 10:47:56
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-01-06 15:05:55
-->
<template>
  <div class="openPlatformConfigWarp">
    <div class="vip_list-head">
      <el-button type="primary" size="small" @click="addData">新增配置</el-button>
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
        <el-table-column fixed="right" label="操作" width="220">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="() => copyPublicKey(scope.row)"
              >复制公钥</el-button
            >

            <el-button type="text" size="small" @click="() => copyPrivateKey(scope.row)"
              >复制私钥</el-button
            >

            <el-button type="text" size="small" @click="() => editData(scope.row)"
              >编辑</el-button
            >

            <el-button type="text" size="small" @click="() => deleteData(scope.row)"
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

    <addorEdit :menuList="menuList" :getData="getData" ref="addorEditRef" />
  </div>
</template>

<script>
import tableColumnList from "./tableColumnList";
import { getDataApi, copyKeyApi, deleteDataApi } from "@/api/openPlatformConfig";
import addorEdit from "./addorEdit/index.vue";
import { getLeftMenuType } from "@/api/systemConfig";

export default {
  data() {
    return {
      tableColumnList,
      tableData: [],
      menuList: [],
      paramsObj: {
        size: 10,
        current: 1,
        total: 0,
        mchId: "",
      },
    };
  },
  components: {
    addorEdit,
  },

  methods: {
    // 删除数据
    deleteData(data) {
      this.$confirm("此操作将永久删除配置, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        deleteDataApi(data.id).then((res) => {
          if (res.code === 200) {
            this.$message({
              type: "success",
              message: "删除成功!",
            });
            this.getData();
          } else {
            this.$message({
              type: "error",
              message: res.message,
            });
          }
        });
      });
    },
    // 编辑数据
    editData(data) {
      this.$refs.addorEditRef.getPage(data);
    },
    // 新增数据
    addData() {
      this.$refs.addorEditRef.getPage();
    },

    // 获取菜单列表
    async getLeftMenuTypeFn() {
      try {
        let res = await getLeftMenuType();
        if (res.code === 200) {
          this.menuList = res.result || [];
        }
      } catch (error) {
        console.log(error);
      }
    },

    // 复制公钥
    async copyPublicKey(data) {
      try {
        let res = await copyKeyApi(data);
        if (res.code === 200) {
          let publicKey = res?.result?.publicKey;

          this.$copyText(publicKey).then((e) => {
            this.$message({
              type: "success",
              message: "复制成功",
            });
          });
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 复制私钥
    async copyPrivateKey(data) {
      try {
        let res = await copyKeyApi(data);
        if (res.code === 200) {
          let privateKey = res?.result?.privateKey;

          this.$copyText(privateKey).then((e) => {
            this.$message({
              type: "success",
              message: "复制成功",
            });
          });
        }
      } catch (error) {
        console.log(error);
      }
    },
    async getData() {
      try {
        let res = await getDataApi(this.paramsObj);
        if (res.code === 200) {
          this.tableData = res?.result?.records || [];
          this.paramsObj.total = res.result?.total;

          this.tableData.forEach((item) => {
            if (item.privateKey && item.privateKey.length > 20) {
              item.privateKeyCopy = item.privateKey.substring(0, 20);
            }

            if (item.publicKey && item.publicKey.length > 20) {
              item.publicKeyCopy = item.publicKey.substring(0, 20);
            }

            if (item.menuCode) {
              item.menuCodeNameCn = this.menuList.find(
                (v) => v.key == item.menuCode
              ).value;
            }
          });
        }
      } catch (error) {
        console.log(error);
      }
    },

    handleSizeChange(val) {
      this.paramsObj.size = val;
      this.getData();
    },

    // 页数跳转
    handleCurrentChange(val) {
      this.paramsObj.current = val;
      this.getData();
    },
  },

  mounted() {
    const asyncFn = async () => {
      try {
        await this.getLeftMenuTypeFn();
        await this.getData();
      } catch (error) {
        console.log(error);
      }
    };

    asyncFn();
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
