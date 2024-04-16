<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-06 13:15:01
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-07-01 12:28:06
-->
<template>
  <el-dialog
    fullscreen
    :destroy-on-close="true"
    title="绘画日志"
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

        <el-table-column fixed="right" label="操作" width="150">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="() => viewImgDetali(scope.row)"
              >图片详情</el-button
            >
            <el-button type="text" size="small" @click="() => viewPromptDetali(scope.row)"
              >咒语详情</el-button
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

      <detali ref="detaliRef" />

      <!-- 咒语详情 -->
      <promptDetali ref="promptDetaliRef" />
    </div>
  </el-dialog>
</template>

<script>
import detali from "./detali";
import { dataList } from "@/api/mjManagerList";
import tableColumnList from "./tableColumnList";
import promptDetali from "./promptDetali";
export default {
  components: {
    detali,
    promptDetali,
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
      console.log(data, "2222");
      this.dialogVisible = true;
      this.paramsObj = {
        size: 10,
        current: 1,
        userId: "",
      };
      this.paramsObj.userId = data.userId;
      this.getDataList();
    },
    handleClose() {
      this.dialogVisible = false;
    },

    // 查看图片详情
    viewImgDetali(data) {
      this.$refs.detaliRef.getPage(data);
    },

    // 咒语详情
    viewPromptDetali(data) {
      this.$refs.promptDetaliRef.getPage(data);
    },

    // 获取订单列表
    async getDataList() {
      try {
        let res = await dataList(this.paramsObj);
        if (res.code === 200) {
          const {
            result: { records, current, size, total },
          } = res;
          records.forEach((item) => {
            if (item.publishState === "PUBLISHED") {
              item.publishState = "已发布";
            } else {
              item.publishState = "未发布";
            }
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
      this.getDataList();
    },

    // 页数跳转
    handleCurrentChange(val) {
      this.paramsObj.current = val;
      this.getDataList();
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
