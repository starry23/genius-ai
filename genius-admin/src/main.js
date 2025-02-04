/*
 * @Description:
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2022-08-18 16:42:46
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-01-06 12:39:41
 */
import Vue from "vue";
import "normalize.css/normalize.css"; // A modern alternative to CSS resets
import VueClipBoard from 'vue-clipboard2';

import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import "@/styles/index.scss"; // global css

import App from "./App";
import store from "./store";
import router from "./router";

import "@/icons"; // icon
import "@/permission"; // permission control

// 如果想要中文版 element-ui，按如下方式声明
Vue.use(ElementUI);

Vue.config.productionTip = false;
Vue.use(VueClipBoard);

new Vue({
  el: "#app",
  router,
  store,
  render: (h) => h(App),
});
