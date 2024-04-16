<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-04-30 17:58:31
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-07-09 16:21:10
-->
<template>
  <div class="question_log_list-warp">
    <div class="question_log__list-head">
      <div class="searchBox">
        <el-input
          clearable
          style="width: 150px; margin-left: 10px"
          v-model="paramsObj.userId"
          placeholder="请输入用户id"
        ></el-input>
        <el-button @click="getDataList" style="margin-left: 10px" type="primary"
          >搜索</el-button
        >
      </div>
    </div>
    <el-table height="80vh" :data="tableData" style="width: 100%">
      <el-table-column label="序号" type="index"></el-table-column>
      <el-table-column
        v-for="item in tableColumnList"
        :prop="item.prop"
        :label="item.label"
        :key="item.prop"
      >
      </el-table-column>

      <el-table-column fixed="right" label="操作" width="180">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            @click="() => commissionWithdrawFn(scope.row)"
            >返佣提现</el-button
          >

          <el-button type="text" size="small" @click="() => viewQusTionDetali(scope.row)"
            >账户消耗日志</el-button
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

    <qustionDetali ref="qustionDetaliRef" />
    <cashback :getDataList="getDataList" ref="cashbackRef" />
  </div>
</template>

<script>
import qustionDetali from "./qustionDetali";
import { dataList } from "@/api/accountList";
import tableColumnList from "./tableColumnList";
import cashback from "./cashback";
export default {
  components: {
    qustionDetali,
    cashback,
  },
  data() {
    return {
      tableData: [],
      tableColumnList,
      paramsObj: {
        size: 10,
        current: 1,
        userId: "",
        accountType: 20,
      },
      accountTypeList: [],
    };
  },
  methods: {
    // 返佣提现
    commissionWithdrawFn(data) {
      this.$refs.cashbackRef.getPage(data);
    },

    // 查看消息详情
    viewQusTionDetali(data) {
      this.$refs.qustionDetaliRef.getPage(data);
    },

    // 获取订单列表
    async getDataList() {
      try {
        let res = await dataList(this.paramsObj);
        if (res.code === 200) {
          const {
            result: { records, current, size, total },
          } = res;

          this.tableData = records || []; //订单列表数组转换成列表形式展示给用户

          this.tableData.forEach((v) => {
            switch (v.accountType) {
              case 10:
                v.accountTypeLable = "代币账户";
                break;
              case 20:
                v.accountTypeLable = "RMB账户";
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

    // 每一条数该百年
    handleSizeChange(val) {
      this.paramsObj.size = val;
      this.getDataList();
    },

    // 页数跳转
    handleCurrentChange(val) {
      this.paramsObj.current = val;
      this.getDataList();
    },
  },

  mounted() {
    this.getDataList();
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
