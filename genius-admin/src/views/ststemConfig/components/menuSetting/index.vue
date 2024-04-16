<!--
 * @Description: 
 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-02 13:41:48
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-11-19 21:19:55
-->
<template>
  <el-form
    :model="menuSettingForm"
    :rules="menuSettingRules"
    ref="menuFormRef"
    label-width="150px"
    class="sys-ruleForm"
  >
    <div class="content">
      <el-form-item label="菜单列表" prop="leftMenuIds">
        <el-checkbox-group v-model="menuSettingForm.leftMenuIds">
          <el-checkbox v-for="item in menuList" :key="item.key" :label="item.key">{{
            item.value
          }}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <div class="webMenuList">
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
            <p class="dargBtn">{{ item.value }}</p>
          </div>
        </draggable>
      </div>
    </div>

    <el-form-item style="text-align: center">
      <el-button type="primary" @click="onSubmit">保存配置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { saveSetting, getSetting, getLeftMenuType } from "@/api/systemConfig";
import draggable from "vuedraggable";
export default {
  components: { draggable },
  data() {
    return {
      sysConfig: null,
      menuList: [],
      dragSlortList: [],
      menuSettingForm: {
        leftMenuIds: [],
      },
      menuSettingRules: {
        leftMenuIds: [{ required: true, message: "菜单列表不能为空" }],
      },
    };
  },

  computed: {
    leftMenuIds() {
      return this.menuSettingForm.leftMenuIds;
    },
  },

  watch: {
    leftMenuIds: {
      handler() {
        this.getDragSlortTypeList();
      },
    },
  },
  mounted() {
    // 获取系统配置
    this.getSettingFn();
    this.$refs.menuFormRef.clearValidate();
    // 重置输入框值为空。这是一种方法，不要仅仅获取
    this.getLeftMenuTypeFn();
  },
  methods: {
    // 拖拽移动
    onMove(relatedContext, draggedContext) {
      this.dragSlortList = relatedContext.relatedContext.list;
      console.log(this.dragSlortList, "this.dragSlortList");
    },
    // 获取拖拽排序数据
    getDragSlortTypeList() {
      let resData = [];
      this.menuSettingForm.leftMenuIds.forEach((item) => {
        this.menuList.forEach((val) => {
          if (item === val.key) {
            resData.push(val);
          }
        });
      });
      this.dragSlortList = resData;
      console.log(this.dragSlortList, "this.dragSlortListthis.dragSlortList");
    },

    // 获取菜单列表
    async getLeftMenuTypeFn() {
      try {
        let res = await getLeftMenuType();
        if (res.code === 200) {
          this.menuList = res.result || [];
          this.getDragSlortTypeList();
        }
      } catch (error) {
        console.log(error);
      }
    },

    // 获取系统配置
    async getSettingFn() {
      try {
        let res = await getSetting();
        if (res.code === 200) {
          this.sysConfig = res.result;
          this.menuSettingForm.leftMenuIds = res.result.leftMenuIds || [];

          this.$nextTick(() => {
            if (this.$refs.menuFormRef) {
              this.$refs.menuFormRef.clearValidate();
            }
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

    // 网站配置保存
    onSubmit() {
      // saveSetting
      this.$refs.menuFormRef.validate((valid) => {
        if (valid) {
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
        this.sysConfig.leftMenuIds = this.dragSlortList.map((v) => v.key);
        let res = await saveSetting(this.sysConfig);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "配置保存成功！",
          });
        } else {
          this.$message({
            type: "error",
            message: res.msg,
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
