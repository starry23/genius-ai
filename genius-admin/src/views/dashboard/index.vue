<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-04 16:29:05
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-07-10 08:50:59
-->
<template>
  <div class="dashboard">
    <div class="dashboard_warp">
      <div class="dashboard_warp-top">
        <div class="dashboard_title">今日数据统计</div>
        <div class="dashboard_head-warp">
          <div class="dashboard_head-item">
            <div class="dashboard_item-head">
              <img class="item-img" src="@/assets/dashborad/user.png" alt="" />
              新增用户
            </div>
            <div class="dashboard_item-num">{{ registerUserTrendCount }} 人</div>
          </div>

          <div class="dashboard_head-item">
            <div class="dashboard_item-head">
              <img class="item-img" src="@/assets/dashborad/user.png" alt="" />
              邀新数量
            </div>
            <div class="dashboard_item-num">{{ inviteTrendCount }} 人</div>
          </div>

          <div class="dashboard_head-item">
            <div class="dashboard_item-head">
              <img class="item-img" src="@/assets/dashborad/trend.png" alt="" />
              问答数量
            </div>
            <div class="dashboard_item-num">{{ qATrendCount }} 次</div>
          </div>

          <div class="dashboard_head-item">
            <div class="dashboard_item-head">
              <img class="item-img" src="@/assets/dashborad/trend.png" alt="" />
              绘画数量
            </div>
            <div class="dashboard_item-num">{{ mjCount }} 次</div>
          </div>

          <div class="dashboard_head-item">
            <div class="dashboard_item-head">
              <img class="item-img" src="@/assets/dashborad/order.png" alt="" />
              支付成功数量
            </div>
            <div class="dashboard_item-num">{{ userPaymentCount }} 次</div>
          </div>

          <div class="dashboard_head-item">
            <div class="dashboard_item-head">
              <img class="item-img" src="@/assets/dashborad/order.png" alt="" />
              成功支付金额
            </div>
            <div class="dashboard_item-num">{{ paymentSuccessCount }} 元</div>
          </div>
        </div>
      </div>

      <div class="dashboard_content-warp">
        <div class="dashboard_title">数据详情</div>

        <div class="dashboard_chat-box">
          <div class="dashboard_chat-item">
            <div class="chart_item-head">
              <div class="chart_item-head-title">注册用户趋势</div>

              <div class="chart_item-tabs">
                <el-radio-group size="small" v-model="registerUserTrendFilter">
                  <el-radio-button :label="7">7天</el-radio-button>
                  <el-radio-button :label="15">15天</el-radio-button>
                </el-radio-group>
              </div>
            </div>

            <div class="chat-box">
              <CustomEchart :option="registerUserTrendOption" />
            </div>
          </div>

          <div class="dashboard_chat-item">
            <div class="chart_item-head">
              <div class="chart_item-head-title">用户提问趋势图</div>

              <div class="chart_item-tabs">
                <el-radio-group size="small" v-model="qATrendFilter">
                  <el-radio-button :label="7">7天</el-radio-button>
                  <el-radio-button :label="15">15天</el-radio-button>
                </el-radio-group>
              </div>
            </div>

            <div class="chat-box">
              <CustomEchart :option="qATrendOption" />
            </div>
          </div>
        </div>

        <div class="dashboard_chat-box">
          <div class="dashboard_chat-item">
            <div class="chart_item-head">
              <div class="chart_item-head-title">用户支付趋势图</div>

              <div class="chart_item-tabs">
                <el-radio-group size="small" v-model="userPaymentFilter">
                  <el-radio-button :label="7">7天</el-radio-button>
                  <el-radio-button :label="15">15天</el-radio-button>
                </el-radio-group>

                <el-radio-group
                  size="small"
                  style="margin-left: 20px"
                  v-model="userPaymentState"
                >
                  <el-radio-button :label="0">支付中</el-radio-button>
                  <el-radio-button :label="1">支付成功</el-radio-button>
                  <el-radio-button :label="2">支付失败</el-radio-button>
                  <el-radio-button :label="3">支付取消</el-radio-button>
                </el-radio-group>
              </div>
            </div>

            <div class="chat-box">
              <CustomEchart :option="userPaymentOption" />
            </div>
          </div>

          <div class="dashboard_chat-item">
            <div class="chart_item-head">
              <div class="chart_item-head-title">成功支付金额趋势图</div>

              <div class="chart_item-tabs">
                <el-radio-group size="small" v-model="paymentSuccessfulFilter">
                  <el-radio-button :label="7">7天</el-radio-button>
                  <el-radio-button :label="15">15天</el-radio-button>
                </el-radio-group>
              </div>
            </div>

            <div class="chat-box">
              <CustomEchart :option="paymentSuccessfulOption" />
            </div>
          </div>
        </div>

        <div class="dashboard_chat-box">
          <div class="dashboard_chat-item">
            <div class="chart_item-head">
              <div class="chart_item-head-title">用户mj绘画趋势图</div>

              <div class="chart_item-tabs">
                <el-radio-group size="small" v-model="mjFilter">
                  <el-radio-button :label="7">7天</el-radio-button>
                  <el-radio-button :label="15">15天</el-radio-button>
                </el-radio-group>

                <el-radio-group
                  size="small"
                  style="margin-left: 20px"
                  v-model="mjFileStatus"
                >
                  <el-radio-button label="IN_PROGRESS">执行中</el-radio-button>
                  <el-radio-button label="SUCCESS">成功</el-radio-button>
                  <el-radio-button label="FAILURE">失败</el-radio-button>
                </el-radio-group>
              </div>
            </div>

            <div class="chat-box">
              <CustomEchart :option="mjOption" />
            </div>
          </div>

          <div class="dashboard_chat-item">
            <div class="chart_item-head">
              <div class="chart_item-head-title">邀新趋势图</div>

              <div class="chart_item-tabs">
                <el-radio-group size="small" v-model="inviteTrendFilter">
                  <el-radio-button :label="7">7天</el-radio-button>
                  <el-radio-button :label="15">15天</el-radio-button>
                </el-radio-group>
              </div>
            </div>

            <div class="chat-box">
              <CustomEchart :option="inviteTrendOption" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  registerUserTrend,
  qATrend,
  payTrend,
  payAmountTrend,
  getMjTrend,
  getInviteTrend,
} from "@/api/dashboard.js";
import CustomEchart from "@/components/CustomEchart";
import {
  registerUserTrendOption,
  qATrendOption,
  userPaymentOption,
  paymentSuccessfulOption,
  mjOption,
  inviteTrendOption,
} from "./options/index";
export default {
  name: "Dashboard",
  components: {
    CustomEchart,
  },
  data() {
    return {
      // 注册用户趋势图
      registerUserTrendOption,
      registerUserTrendFilter: 7,
      registerUserTrendCount: 0,

      // 用户提问趋势图
      qATrendOption,
      qATrendFilter: 7,
      qATrendCount: 0,

      // mi绘画趋势图
      mjOption,
      mjFilter: 7,
      mjCount: 0,
      mjFileStatus: "SUCCESS",

      // 邀请趋势图
      inviteTrendOption,
      inviteTrendFilter: 7,
      inviteTrendCount: 0,

      // 用户支付趋势图
      userPaymentOption,
      userPaymentFilter: 7, //7天或以上天数记录展示用户支付趋势图的条目数量，
      userPaymentCount: 0, //记录数量。
      userPaymentState: 1, //0失败  1成功

      // 成功支付金额趋势图
      paymentSuccessfulOption,
      paymentSuccessfulFilter: 7,
      paymentSuccessCount: 0,
    };
  },
  watch: {
    // 注册用户趋势图
    registerUserTrendFilter(value) {
      this.registerUserTrendFn(value);
    },

    // 用户提问趋势图
    qATrendFilter(value) {
      this.qATrendFn(value);
    },

    // 用户支付趋势图
    userPaymentFilter() {
      this.payTrendFn(this.userPaymentFilter, this.userPaymentState);
    },

    // 用户支付趋势图
    userPaymentState() {
      this.payTrendFn(this.userPaymentFilter, this.userPaymentState);
    },

    // 成功支付金额趋势图
    paymentSuccessfulFilter() {
      this.payAmountTrendFn(this.paymentSuccessfulFilter);
    },

    // 用户绘画次数
    mjFilter() {
      this.getMjTrendFn(this.mjFilter);
    },
    // mj绘画趋势图
    mjFileStatus() {
      this.getMjTrendFn(this.mjFilter);
    },

    // 拉新趋势图
    inviteTrendFilter() {
      this.getInviteTrendFn(this.inviteTrendFilter);
    },
  },

  mounted() {
    this.initData();
  },
  methods: {
    async initData() {
      // 查询今日的用户注册趋势
      await this.registerUserTrendFn(0);
      // 注册用户趋势
      await this.registerUserTrendFn(this.registerUserTrendFilter);

      // 查询今日用户提问次数
      await this.qATrendFn(0);
      // 用户提问趋势图
      await this.qATrendFn(this.userPaymentFilter);

      // 用户支付成功趋势图
      await this.payTrendFn(0, 1);
      // 今日支付次数
      await this.payTrendFn(this.userPaymentFilter, this.userPaymentState);

      // 今日成功支付金额
      await this.payAmountTrendFn(0);

      // 支付金额趋势图
      await this.payAmountTrendFn(this.paymentSuccessfulFilter);

      // 获取用户绘画趋势图
      await this.getMjTrendFn(0);
      await this.getMjTrendFn(this.mjFilter);

      // 拉新趋势图
      await this.getInviteTrendFn(0);
      await this.getInviteTrendFn(this.inviteTrendFilter);
    },

    // 拉新趋势图
    async getInviteTrendFn(day) {
      try {
        let params = {
          day,
        };
        let res = await getInviteTrend(params);
        if (res.code === 200) {
          if (day === 0) {
            this.inviteTrendCount = res.result.length ? res.result[0].count : 0;
          } else {
            let resData = res.result || [];
            this.inviteTrendOption.xAxis.data = resData.map((v) => v.date);
            this.inviteTrendOption.series[0].data = resData.map((v) => Number(v.count));
          }
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

    // mj绘画趋势图
    async getMjTrendFn(day) {
      try {
        let params = {
          day,
          fileStatus: this.mjFileStatus,
        };
        let res = await getMjTrend(params);
        if (res.code === 200) {
          if (day === 0) {
            this.mjCount = res.result.length ? res.result[0].count : 0;
          } else {
            let resData = res.result || [];
            this.mjOption.xAxis.data = resData.map((v) => v.date);
            this.mjOption.series[0].data = resData.map((v) => Number(v.count));
          }
        }
      } catch (error) {
        console.log(error);
      }
    },

    /**
     * @description: 成功支付金额趋势图
     * @return {*}day
     * @author: jinglin.gao
     */
    async payAmountTrendFn(day) {
      try {
        let params = {
          day,
        };
        let res = await payAmountTrend(params);
        if (res.code === 200) {
          if (day === 0) {
            this.paymentSuccessCount = res.result.length ? res.result[0].count : 0;
          } else {
            let resData = res.result || [];
            this.paymentSuccessfulOption.xAxis.data = resData.map((v) => v.date);
            this.paymentSuccessfulOption.series[0].data = resData.map((v) =>
              Number(v.count)
            );
          }
        }
      } catch (error) {
        console.log(error);
      }
    },

    /**
     * @description: 获取注册用户趋势图
     * @return {*}
     * @author: jinglin.gao
     */
    async registerUserTrendFn(day) {
      try {
        let params = {
          day,
        };
        let res = await registerUserTrend(params);
        if (res.code === 200) {
          if (day === 0) {
            this.registerUserTrendCount = res.result.length ? res.result[0].count : 0;
          } else {
            let resData = res.result || [];
            this.registerUserTrendOption.xAxis.data = resData.map((v) => v.date);
            this.registerUserTrendOption.series[0].data = resData.map((v) =>
              Number(v.count)
            );
          }
        }
      } catch (error) {
        console.log(error);
      }
    },

    // 用户提问趋势图
    async qATrendFn(day) {
      try {
        let params = {
          day,
        };
        let res = await qATrend(params);
        if (res.code === 200) {
          if (day === 0) {
            this.qATrendCount = res.result.length ? res.result[0].count : 0;
          } else {
            let resData = res.result || [];
            this.qATrendOption.xAxis.data = resData.map((v) => v.date);
            this.qATrendOption.series[0].data = resData.map((v) => Number(v.count));
          }
        }
      } catch (error) {
        console.log(error);
      }
    },

    //用户支付趋势图
    async payTrendFn(day, payState = 1) {
      try {
        let params = {
          day,
          payState,
        };
        let res = await payTrend(params);
        if (res.code === 200) {
          if (day === 0) {
            this.userPaymentCount = res.result.length ? res.result[0].count : 0;
          } else {
            let resData = res.result || [];
            this.userPaymentOption.xAxis.data = resData.map((v) => v.date);
            this.userPaymentOption.series[0].data = resData.map((v) => Number(v.count));
          }
        }
      } catch (error) {
        console.log(error);
      }
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
