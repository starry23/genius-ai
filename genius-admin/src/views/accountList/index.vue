el-option
<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-04-30 17:58:31
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-13 21:48:19
-->
<template>
  <div class="question_log_list-warp">
    <div class="question_log__list-head">
      <div class="searchBox">
        <el-select
          clearable
          style="width: 150px"
          v-model="paramsObj.accountType"
          placeholder="请选择账户类型"
        >
          <el-option
            v-for="(item, index) in accountTypeList"
            :label="item.value"
            :key="index"
            :value="item.key"
          ></el-option>
        </el-select>

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

      <el-table-column fixed="right" label="操作" width="300">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="() => userQuestionLog(scope.row)"
            >问答日志</el-button
          >

          <el-button type="text" size="small" @click="() => getDrawLog(scope.row)"
            >绘画日志</el-button
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
    <userQuestionLog ref="userQuestionLogRef" />
    <drawLog ref="drawLogRef" />
  </div>
</template>

<script>
import qustionDetali from "./qustionDetali";
import userQuestionLog from "./userQuestionLog";
import drawLog from "./drawLog";
import { dataList, getAccountTypeList } from "@/api/accountList";
import tableColumnList from "./tableColumnList";
export default {
  components: {
    qustionDetali,
    userQuestionLog,
    drawLog,
  },
  data() {
    return {
      tableData: [],
      tableColumnList,
      paramsObj: {
        size: 10,
        current: 1,
        userId: "",
        accountType: 10,
      },
      accountTypeList: [],
    };
  },
  methods: {
    // 获取账户类型
    async getAccountTypeList() {
      try {
        let res = await getAccountTypeList();
        if (res.code === 200) {
          this.accountTypeList = res.result || [];
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 查看消息详情
    viewQusTionDetali(data) {
      this.$refs.qustionDetaliRef.getPage(data);
    },

    // 绘画日志
    getDrawLog(data) {
      this.$refs.drawLogRef.getPage(data);
    },

    // 问答日志
    userQuestionLog(data) {
      this.$refs.userQuestionLogRef.getPage(data);
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
              case 30:
                v.accountTypeLable = "会员赠送知识库上传次数";
                break;
              case 40:
                v.accountTypeLable = "会员赠送MJ次数";
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
    this.getAccountTypeList();
    this.getDataList();
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
