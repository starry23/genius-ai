<!--
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-05-17 09:41:38
 * @LastEditors: jl.g
 * @LastEditTime: 2023-07-21 16:10:43
-->
<template>
  <div class="chatgpt_config-warp">
    <!-- chatgpt配置 -->
    <div class="config-box">
      <el-form
        :model="formConfig"
        :rules="formRules"
        ref="formConfig"
        label-width="350px"
      >
        <div class="formContent">
          <el-form-item label="appId" prop="appId">
            <el-input placeholder="请输入appId" v-model="formConfig.appId"></el-input>
          </el-form-item>

          <el-form-item label="appkey" prop="client_id">
            <el-input
              placeholder="请输入appkey"
              v-model="formConfig.client_id"
            ></el-input>
          </el-form-item>

          <el-form-item label="Secret Key" prop="client_secret">
            <el-input
              placeholder="请输入Secret Key"
              v-model="formConfig.client_secret"
            ></el-input>
          </el-form-item>


          <el-form-item
            label="随机性(值越大回答越随机,越低回答集中和确定)"
            prop="temperature"
          >
            <el-input-number
              :min="0.1"
              :max="1"
              :step="0.1"
              style="width: 100%"
              placeholder="范围 (0, 1.0]"
              v-model="formConfig.temperature"
            ></el-input-number>
          </el-form-item>

          <el-form-item label="多样性(值越大，生成文本的多样性越强)" prop="top_p">
            <el-input-number
              :min="0"
              :max="1"
              :step="0.1"
              style="width: 100%"
              placeholder="取值范围 [0, 1.0]"
              v-model="formConfig.top_p"
            ></el-input-number>
          </el-form-item>

          <p class="info">随机性和多样性建议不要同时设置</p>

          <el-form-item label="精确性(值越大,重复生成的内容越少)" prop="penalty_score">
            <el-input-number
              :min="1"
              :max="2"
              :step="0.1"
              style="width: 100%"
              placeholder="取值范围：[1.0, 2.0]	"
              v-model="formConfig.penalty_score"
            ></el-input-number>
          </el-form-item>
        </div>
        <el-form-item style="text-align: center">
          <el-button type="primary" @click="onFormSubmit">保存配置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { saveOrUpdateChatConfig, getChatConfig } from "@/api/chatgptConfig";
export default {
  data() {
    return {
      allConfig: null,
      modelList: [],
      formConfig: {
   
        appId: "",

        client_id: "",
        client_secret: "",
        // 影响输出文本的多样性，取值越大，生成文本的多样性越强
        // 默认0.95，取值范围 [0，1.0]
        temperature: 0.95,
        // 影响输出文本的多样性生成文本的多样性越强\n”取值越大
        top_p: 0.8,
        // 通过对已生成的token增加惩罚，减少重复生成的现象。
        penalty_score: 1.0,
      },
      formRules: {

        appId: [
          {
            required: true,
            message: "请填写内容",
          },
        ],

        client_id: [
          {
            required: true,
            message: "请填写内容",
          },
        ],
        client_secret: [
          {
            required: true,
            message: "请填写内容",
          },
        ],

        temperature: [
          {
            required: true,
            message: "请填写内容",
          },
        ],
        top_p: [
          {
            required: true,
            message: "请填写内容",
          },
        ],
        penalty_score: [
          {
            required: true,
            message: "请填写内容",
          },
        ],
      },
    };
  },
  mounted() {
    this.getChatConfigFn();
  },
  methods: {
    // 获取gpt配置
    async getChatConfigFn() {
      try {
        let res = await getChatConfig();
        if (res.code === 200) {
          this.allConfig = res.result;
          this.formConfig = res.result.wxqfEntity || {
    
            appId: "",

            client_id: "",
            client_secret: "",
            // 影响输出文本的多样性，取值越大，生成文本的多样性越强
            // 默认0.95，取值范围 [0，1.0]
            temperature: 0.95,
            // 影响输出文本的多样性生成文本的多样性越强\n”取值越大
            top_p: 0.8,
            // 通过对已生成的token增加惩罚，减少重复生成的现象。
            penalty_score: 1.0,
          };
        }
      } catch (error) {
        console.log(error);
      }
    },

    /**
     * @description: 保存配置
     * @return {*}
     * @author: jinglin.gao
     */
    async saveOrUpdateChatConfigFn() {
      try {
        this.allConfig.wxqfEntity = this.formConfig;
        let res = await saveOrUpdateChatConfig(this.allConfig);
        if (res.code === 200) {
          this.$message({
            type: "success",
            message: "配置保存成功",
          });
        } else {
          this.$message.error(res.message);
        }
      } catch (error) {
        console.log(error);
      }
    },
    onFormSubmit() {
      this.$refs.formConfig.validate((valid) => {
        if (valid) {
          this.saveOrUpdateChatConfigFn();
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
