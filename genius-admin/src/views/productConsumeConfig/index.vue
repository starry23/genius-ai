<template>
  <div class="vip_list_warp">
    <div class="vip_list-head">
      <el-button @click="addVip" type="primary" size="small">新增配置</el-button>
    </div>
    <div class="content">
      <el-table height="85vh" :data="tableData" style="width: 100%">
        <el-table-column label="序号" type="index"></el-table-column>
        <el-table-column
          v-for="item in tableColumnList"
          :prop="item.prop"
          :label="item.label"
          :key="item.prop"
        >
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="150">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === 2"
              type="text"
              size="small"
              @click="() => productOnOffline(1, scope.row.id)"
              >上线</el-button
            >

            <el-button
              v-if="scope.row.status === 1"
              type="text"
              size="small"
              @click="() => productOnOffline(2, scope.row.id)"
              >下线</el-button
            >

            <el-button type="text" size="small" @click="() => editVip(scope.row)"
              >编辑</el-button
            >

            <el-button type="text" size="small" @click="() => deleteVip(scope.row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>
    <addorEdit
      :productTypeList="productTypeList"
      :getMemberList="getMemberList"
      ref="addorEditRef"
    />
  </div>
</template>

<script>
import {
  memberList,
  memberConfigDelete,
  getProductTypeList,
  productConsumeConfigOnOff,
} from "@/api/productConsumeConfig";
import tableColumnList from "./tableColumnList";
import addorEdit from "./addorEdit/index.vue";

export default {
  data() {
    return {
      tableColumnList,
      tableData: [],
      productTypeList: [],
    };
  },
  components: {
    addorEdit,
  },

  methods: {
    // 上线
    async productOnOffline(status, id) {
      try {
        let data = {
          status,
          id,
        };
        let res = await productConsumeConfigOnOff(data);
        if (res.code == 200) {
          this.getMemberList();
          this.$message({
            type: "success",
            message: "操作成功",
          });
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

    // 删除vip
    deleteVip(data) {
      this.$confirm("此操作将永久删除数据, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        memberConfigDelete(data.id).then((res) => {
          if (res.code === 200) {
            this.$message({
              type: "success",
              message: "删除成功!",
            });
            this.getMemberList();
          } else {
            this.$message({
              type: "error",
              message: res.message,
            });
          }
        });
      });
    },

    // 获取产品类型
    async getProductTypeListFn() {
      try {
        let res = await getProductTypeList();
        if (res.code === 200) {
          this.productTypeList = res.result || [];
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
          this.tableData = res.result || [];
          this.tableData.forEach((v) => {
            let tranformItem = this.productTypeList.find(
              (item) => item.key == v.productType
            );
            if (tranformItem) {
              v.productTypeLabel = tranformItem.value;
            }

            // 转换产品类型
            if (v.consumeType === 1) {
              v.consumeTypelabel = "次数";
            } else if (v.consumeType === 2) {
              v.consumeTypelabel = "token";
            }

            // 转换上下限状态
            switch (v.status) {
              case 1:
                v.statusCn = "上线";
                break;
              case 2:
                v.statusCn = "下线";
                break;
            }

            // 转换使用权限
            switch (v.useAuth) {
              case 1:
                v.useAuthCn = "会员";
                break;
              case 2:
                v.useAuthCn = "常规用户";
                break;
            }
          });
        }
      } catch (error) {
        console.log(error);
      }
    },

    /**
     * @description: 新增会员卡
     * @return {*}
     * @author: jinglin.gao
     */
    addVip() {
      this.$refs.addorEditRef.getPage();
    },

    // 编辑会员卡
    editVip(data) {
      this.$refs.addorEditRef.getPage(data);
    },
  },

  async mounted() {
    await this.getProductTypeListFn();
    await this.getMemberList();
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
