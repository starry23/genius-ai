<template>
  <div class="vip_list_warp">
    <div class="vip_list-head">
      <div>
        <el-button @click="addVip" type="primary" size="small">新增</el-button>
        <el-button @click="exportRedemptionCode" type="primary" size="small"
          >导出兑换码</el-button
        >
      </div>

      <div class="searchBox">
        <el-input
          style="width: 150px"
          v-model="paramsObj.batch"
          placeholder="请输入批次号"
          type="text"
        />

        <el-input
          style="width: 150px"
          v-model="paramsObj.redeemedUid"
          placeholder="请输入兑换用户id"
          type="text"
        />

        <el-input
          style="width: 150px"
          v-model="paramsObj.code"
          placeholder="请输入兑换码"
          type="text"
        />

        <el-select v-model="paramsObj.status" style="width: 150px">
          <el-option label="待兑换" :value="0"></el-option>
          <el-option label="已兑换" :value="1"></el-option>
          <el-option label="已过期" :value="2"></el-option>
          <el-option label="已作废" :value="3"></el-option>
        </el-select>

        <el-button
          size="samll"
          @click="searchDataFn"
          style="margin-left: 10px"
          type="primary"
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
        <el-table-column fixed="right" label="操作" width="200">
          <template slot-scope="scope">
            <el-button
              data-clipboard-action="copy"
              :data-clipboard-text="scope.row.code"
              class="copy-btn"
              type="text"
              size="small"
              @click="() => copyInvalidateCodeFn(scope.row)"
              >复制兑换码</el-button
            >

            <el-button
              type="text"
              size="small"
              @click="() => cancelInvalidateCodeFn(scope.row)"
              >作废</el-button
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
    <addorEdit :getDataListFn="getDataListFn" ref="addorEditRef" />
    <exportRedemptionCodeDialog ref="exportRedemptionCodeDialogRef" />
  </div>
</template>

<script>
import { getDataList, cancelInvalidateCode } from "@/api/redemptionCode";
import tableColumnList from "./tableColumnList";
import addorEdit from "./addorEdit/index.vue";
import Clipboard from "clipboard";
import exportRedemptionCodeDialog from "./exportRedemptionCodeDialog";
export default {
  data() {
    return {
      tableColumnList,
      tableData: [],
      paramsObj: {
        size: 10,
        current: 1,
        batch: "",
        redeemedUid: "",
        code: "",
        status: "",
      },
    };
  },
  components: {
    addorEdit,
    exportRedemptionCodeDialog,
  },

  methods: {
    // 导出兑换码
    exportRedemptionCode() {
      this.$refs.exportRedemptionCodeDialogRef.getPage();
    },
    searchDataFn() {
      this.paramsObj.current = 1;
      this.getDataListFn();
    },
    // 每一条数该百年
    handleSizeChange(val) {
      this.paramsObj.size = val;
      this.getDataListFn();
    },

    // 页数跳转
    handleCurrentChange(val) {
      this.paramsObj.current = val;
      this.getDataListFn();
    },

    // 复制兑换码
    copyInvalidateCodeFn() {
      let clipboard = new Clipboard(".copy-btn");
      clipboard.on("success", () => {
        this.$message({
          type: "success",
          message: "复制成功",
        });
        clipboard.destroy();
      });
    },

    // 作废
    cancelInvalidateCodeFn(data) {
      this.$confirm("此操作将无法恢复数据, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        cancelInvalidateCode(data.id).then((res) => {
          if (res.code === 200) {
            this.$message({
              type: "success",
              message: "操作成功!",
            });
            this.getDataListFn();
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
     * @description: 获取数据
     * @return {*}
     * @author: jinglin.gao
     */
    async getDataListFn() {
      try {
        let res = await getDataList(this.paramsObj);
        if (res.code === 200) {
          const {
            result: { records, current, size, total },
          } = res;
          this.tableData = records || [];
          this.tableData.forEach((v) => {
            switch (v.status) {
              case 0:
                v.statusLabel = "待兑换";
                break;
              case 1:
                v.statusLabel = "已兑换";
                break;
              case 2:
                v.statusLabel = "已过期";
                break;
              case 3:
                v.statusLabel = "已作废";
                break;
              default:
                break;
            }
          });

          this.paramsObj.size = size;
          this.paramsObj.current = current;
          this.paramsObj.total = total; //记录总数计算条目数量，用于计算页面加载更多条目的
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
  },

  async mounted() {
    this.getDataListFn();
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
