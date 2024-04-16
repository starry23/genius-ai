<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-06 13:15:01
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-07-01 12:41:29
-->
<template>
  <el-dialog
    fullscreen
    :destroy-on-close="true"
    title="问答日志"
    :visible.sync="dialogVisible"
  >
    <div class="question_log_list-warp">
      <el-table height="80vh" :data="tableData" style="width: 100%">
        <el-table-column label="序号" type="index"></el-table-column>
        <el-table-column
          v-for="item in tableColumnList"
          :prop="item.prop"
          :label="item.label"
          :key="item.prop"
        >
        </el-table-column>

        <el-table-column fixed="right" label="操作" width="100">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="() => viewQusTionDetali(scope.row)"
              >查看消息详情</el-button
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
    </div>
  </el-dialog>
</template>

<script>
import qustionDetali from "./qustionDetali";
import { managerQlogs } from "@/api/questionLogList";
import tableColumnList from "./tableColumnList";
export default {
  components: {
    qustionDetali,
  },
  data() {
    return {
      dialogVisible: false,
      tableData: [],
      tableColumnList,
      paramsObj: {
        size: 10,
        current: 1,
        userId: "",
      },
    };
  },

  methods: {
    async getPage(data) {
      this.dialogVisible = true;
      this.paramsObj = {
        size: 10,
        current: 1,
        userId: "",
      };
      this.paramsObj.userId = data.userId;
      this.getManagerQlogs();
    },
    handleClose() {
      this.dialogVisible = false;
    },

    // 查看消息详情
    viewQusTionDetali(data) {
      this.$refs.qustionDetaliRef.getPage(data);
    },

    // 获取订单列表
    async getManagerQlogs() {
      try {
        let res = await managerQlogs(this.paramsObj);
        if (res.code === 200) {
          const {
            result: { records, current, size, total },
          } = res;

          records.forEach((v) => {
            v.contentOmit = v.content.slice(0, 10) + "...";
          });

          this.tableData = records || []; //订单列表数组转换成列表形式展示给用户
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
      this.getManagerQlogs();
    },

    // 页数跳转
    handleCurrentChange(val) {
      this.paramsObj.current = val;
      this.getManagerQlogs();
    },
  },
};
</script>
<style lang="scss" scoped>
@import "./index.scss";
</style>

