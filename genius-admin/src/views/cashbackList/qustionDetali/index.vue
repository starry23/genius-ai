<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-06 13:15:01
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-04 21:49:47
-->
<template>
  <el-dialog
    fullscreen
    :destroy-on-close="true"
    title="账户消耗日志"
    :visible.sync="dialogVisible"
    width="850px"
  >
    <div class="headTools">
      <el-input
        class="headTools-item"
        clearable
        size="small"
        v-model="paramsObj.outsideCode"
        placeholder="请输入订单号"
      ></el-input>

      <el-select
        class="headTools-item"
        size="small"
        clearable
        v-model="paramsObj.logDescriptionType"
        placeholder="请选择日志操作类型"
      >
        <el-option
          v-for="(item, index) in logDescriptionTypeList"
          :label="item.value"
          :key="item.key + '_' + index"
          :value="item.key"
        ></el-option>
      </el-select>

      <el-select
        class="headTools-item"
        size="small"
        clearable
        v-model="paramsObj.directionType"
        placeholder="请选择方向类型"
      >
        <el-option
          v-for="item in directionTypeList"
          :label="item.value"
          :key="item.key"
          :value="item.key"
        ></el-option>
      </el-select>
      <el-button size="small" @click="searchData" style="margin-left: 10px" type="primary"
        >搜索</el-button
      >
    </div>
    <el-table height="550px" :data="tableData" style="width: 100%">
      <el-table-column label="序号" type="index"></el-table-column>
      <el-table-column
        v-for="item in tableColumnList"
        :prop="item.prop"
        :label="item.label"
        :key="item.prop"
      >
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
  </el-dialog>
</template>

<script>
import { accountLogs, directionType, logDescriptionType } from "@/api/accountList";
export default {
  data() {
    return {
      dialogVisible: false,
      tableData: [],
      tableColumnList: [
        {
          prop: "userId",
          label: "用户ID",
        },
        {
          prop: "outsideCode",
          label: "订单号",
        },

        {
          prop: "logDescription",
          label: "日志操作描述",
        },

        {
          prop: "directionType",
          label: "方向类型 ",
        },

        {
          prop: "amount",
          label: "变动金额",
        },

        {
          prop: "balanceAmount",
          label: "变动后余额",
        },
        {
          prop: "createTime",
          label: "创建时间 ",
        },
      ],
      paramsObj: {
        size: 10,
        current: 1,
        total: 0,
        userId: "",
        outsideCode: "",
        logDescriptionType: "",
        directionType: "",
        accountId: "",
      },
      directionTypeList: [],
      logDescriptionTypeList: [],
    };
  },

  methods: {
    searchData() {
      this.paramsObj.current = 1; // 设置从第一页开始显示 默认为1 可以添加参数来指定
      this.getAccountLogs();
    },
    // 获取操作类型
    async getDirectionType() {
      try {
        let res = await directionType(this.paramsObj);
        if (res.code === 200) {
          this.directionTypeList = res.result || [];
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 获取操作日志类型
    async getLogDescriptionType() {
      try {
        let res = await logDescriptionType(this.paramsObj);
        if (res.code === 200) {
          this.logDescriptionTypeList = res.result || [];
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 每一条数该百年
    handleSizeChange(val) {
      this.paramsObj.size = val;
      this.getAccountLogs();
    },

    // 页数跳转
    handleCurrentChange(val) {
      this.paramsObj.current = val;
      this.getAccountLogs();
    },

    // 获取数据
    async getAccountLogs() {
      try {
        let res = await accountLogs(this.paramsObj);
        if (res.code === 200) {
          let resData = res.result?.records || [];
          this.tableData = resData;
          this.paramsObj.total = res.result.total;
          this.tableData.forEach((v) => {
            // 转换日志操作类型
            let activeItem = this.logDescriptionTypeList.find(
              (item) => item.key === v.logDescriptionType
            );
            if (activeItem) {
              v.logDescriptionType = activeItem.value;
            }
            let activeItem2 = this.directionTypeList.find(
              (item) => item.key === v.directionType
            );

            if (activeItem2) {
              v.directionType = activeItem2.value;
            }
          });
        }
      } catch (error) {
        console.log(error);
      }
    },

    async getPage(data) {
      this.dialogVisible = true;
      this.paramsObj = {
        size: 10,
        current: 1,
        total: 0,
        userId: data.userId,
        outsideCode: "",
        logDescriptionType: "",
        directionType: "",
        accountId: data.id,
      };

      await this.getDirectionType();
      await this.getLogDescriptionType();
      await this.getAccountLogs();
    },
    handleClose() {
      this.dialogVisible = false;
    },
  },
};
</script>
<style lang="scss" scoped>
.headTools {
  width: 100%;
  .headTools-item {
    width: 150px;
    margin-right: 10px;
  }
}
</style>
