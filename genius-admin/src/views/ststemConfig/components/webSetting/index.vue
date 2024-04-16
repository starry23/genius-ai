<!--
 * @Description: 
 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-01-06 21:34:34
-->
<template>
  <el-form
    :model="sysForm"
    :rules="sysRules"
    ref="sysFormRef"
    label-width="250px"
    class="sys-ruleForm"
  >
    <div class="content">
      <el-form-item label="系统名称" prop="webName">
        <el-input placeholder="请填写系统名称" v-model="sysForm.webName"></el-input>
      </el-form-item>

      <el-form-item label="网站默认主题" prop="sysTheme">
        <el-select v-model="sysForm.sysTheme" placeholder="请选择网站默认主题">
          <el-option value="dark" label="黑夜"></el-option>
          <el-option value="light" label="白天"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="当前系统版本">
        <el-input
          placeholder="请填写系统名称"
          disabled
          v-model="sysForm.webVersion"
        ></el-input>
      </el-form-item>
      <el-form-item label="站点域名(http或https)" prop="domain">
        <el-input placeholder="请填写站点域名" v-model="sysForm.domain"></el-input>
      </el-form-item>

      <el-form-item label="登录页欢迎语" prop="subTitle">
        <el-input placeholder="请填写登录页欢迎语" v-model="sysForm.subTitle"></el-input>
      </el-form-item>

      <el-form-item label="网站Logo" prop="iconUrl">
        <el-input placeholder="请填写网站Logo地址" v-model="sysForm.iconUrl"></el-input>
      </el-form-item>

      <el-form-item label="虚拟币名称	" prop="currencyName">
        <el-input placeholder="请填写内容" v-model="sysForm.currencyName"></el-input>
      </el-form-item>

      <el-form-item label="搜索APIkey">
        <el-input placeholder="请输入内容" v-model="sysForm.searchApiApiKey"></el-input>
      </el-form-item>
      <div class="info">
        <a href="https://serpapi.com" target="_blank">点我去注册账号</a>
      </div>

      <el-form-item label="联网开关" prop="searchApiApiKey">
        <el-switch v-model="sysForm.searchApiEnable"> </el-switch>
      </el-form-item>

      <el-form-item label="登录页面样式">
        <el-radio v-model="sysForm.loginViewType" :label="1">俯瞰地球</el-radio>
        <el-radio v-model="sysForm.loginViewType" :label="2">迷幻星空</el-radio>
      </el-form-item>

      <el-form-item label="互联网公安备案号" prop="filingNumber">
        <el-input
          placeholder="请填写互联网公安备案号"
          v-model="sysForm.filingNumber"
        ></el-input>
      </el-form-item>

      <el-form-item label="登录方式">
        <el-checkbox-group v-model="sysForm.loginTypes">
          <el-checkbox
            v-for="item in loginTypeList"
            :label="item.value"
            :key="item.value"
            >{{ item.label }}</el-checkbox
          >
        </el-checkbox-group>
      </el-form-item>

      <el-form-item label="是否开启注册">
        <el-switch v-model="sysForm.registerOpen" active-color="#13ce66"> </el-switch>
      </el-form-item>

      <div class="dragSlortWarp">
        <draggable
          :forceFallback="true"
          :move="onMove"
          :list="dragSlortList"
          handle=".dargBtn"
          :animation="500"
          filter=".undraggable"
          fallbackClass="fallbackStyle"
          ghostClass="item_ghost"
          chosenClass="chosenStyle"
          dragClass="dragStyle"
          class="dragSlortContent"
        >
          <div v-for="(item, index) in dragSlortList" :key="index" class="draggable">
            <p class="dargBtn">{{ item.label }}</p>
          </div>
        </draggable>
      </div>
      <p class="info">拖拽登录选项,从而更改登录方式的显示优先级</p>

      <el-form-item label="网站公告	" prop="announcement">
        <el-input placeholder="请填写网站公告	" v-model="sysForm.announcement"></el-input>
      </el-form-item>

      <el-form-item label="网站弹窗内容">
        <el-input
          type="textarea"
          row="5"
          placeholder="请填写网站弹窗内容	"
          v-model="sysForm.indexPopup"
        ></el-input>
      </el-form-item>
    </div>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="onSysFormSubmit">保存配置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import draggable from "vuedraggable";
import { saveSetting, getSetting } from "@/api/systemConfig";

export default {
  components: { draggable },
  data() {
    return {
      sysForm: {
        webName: "",
        iconUrl: "",
        subTitle: "",
        filingNumber: "",
        loginTypes: [1],
        announcement: "",
        domain: "",
        currencyName: "",
        searchApiApiKey: "",
        searchApiEnable: false,
        sysTheme: "",
        warningInfo: "",
        indexPopup: "",
        webVersion: "",
        registerOpen: true,
        // 登录页样式
        loginViewType: 1,
      },
      loginTypeList: [
        {
          label: "邮箱登录",
          value: 1,
        },
        {
          label: "阿里短信验证码登录",
          value: 2,
        },
        {
          label: "公众号扫码登录",
          value: 3,
        },
        {
          label: "微信授权登录",
          value: 4,
        },
      ],
      dragSlortList: [
        {
          label: "邮箱登录",
          value: 1,
        },
      ],
      sysRules: {
        webName: [{ required: true, message: "请输入系统名称" }],
        sysTheme: [{ required: true, message: "请选择网站默认主题" }],
        currencyName: [{ required: true, message: "请输入内容" }],
        searchApiEnable: [{ required: true, message: "请选择是否开启联网功能" }],
        iconUrl: [
          {
            required: false,
            message: "请输入网站Logo地址",
          },
        ],
        subTitle: [
          {
            required: true,
            message: "请输入登录页欢迎语",
          },
        ],
        loginTypes: [
          {
            required: true,
            message: "请选择网站登录方式",
          },
        ],
        filingNumber: [
          {
            required: false,
            message: "请选择网站登录方式",
          },
        ],

        domain: [
          {
            required: true,
            message: "请输入站点域名",
          },
        ],
        announcement: [
          {
            required: false,
          },
        ],
      },
    };
  },

  computed: {
    loginTypes() {
      return this.sysForm.loginTypes;
    },
  },

  watch: {
    loginTypes: {
      handler(val) {
        this.getDragSlortTypeList();
      },
    },
  },

  mounted() {
    // 获取系统配置
    this.getSettingFn();
  },
  methods: {
    // 拖拽移动
    onMove(relatedContext, draggedContext) {
      this.dragSlortList = relatedContext.relatedContext.list;
      console.log(this.dragSlortList, "this.dragSlortListthis.dragSlortList");
    },

    // 获取拖拽排序数据
    getDragSlortTypeList() {
      let resData = [];
      this.sysForm.loginTypes.forEach((item) => {
        this.loginTypeList.forEach((val) => {
          if (item === val.value) {
            resData.push(val);
          }
        });
      });
      this.dragSlortList = resData;
    },
    // 获取系统配置
    async getSettingFn() {
      try {
        let res = await getSetting();
        if (res.code === 200) {
          this.sysForm = res.result;
          // 兼容以前版本，如果返回的默认值是字符串需要转换成数组
          if (!Array.isArray(this.sysForm.loginTypes)) {
            this.sysForm.loginTypes = [this.sysForm.loginTypes];
          }
          this.getDragSlortTypeList();
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

    // 网站配置保存
    onSysFormSubmit() {
      // saveSetting
      this.$refs.sysFormRef.validate((valid) => {
        if (valid) {
          this.sysForm.loginTypes = this.dragSlortList.map((v) => v.value);
          this.saveSettingFn();
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },

    // 网站配置保存
    async saveSettingFn() {
      try {
        let res = await saveSetting(this.sysForm);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "配置保存成功！",
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
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
