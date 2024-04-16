/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2022-11-06 13:52:22
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 20:50:11
 */

import {
  MAP_TOKEN,
  USER_INFO,
  STORE_CREATE_SESSION_FN,
  SYS_CONFIG,
  ACCOUNT_CHANGE,
  CONSUMPTION_PROMPT,
  TABS_DATA_LIST_PC,
  ACTIVE_MENU_ITEM_KEY,
  SYSTEM_THEME,
  SELECT_PROJECT_MENU,
  ACCOUNT_INFO,
  USER_CONTROLLER,
} from "../constants/home_constant";
// 我的
import user from "@/assets/chat/user.png";
import userAc from "@/assets/chat/userAc.png";
import userDark from "@/assets/chat/userDark.png";
// 助手列表
// import assistantImg from "../../../public/assets/imgs/assistant.svg";

// 购物车
import shoopingCart from "@/assets/chat/shoopingCart.png";
import shoopingCartAc from "@/assets/chat/shoopingCartAc.png";
import shoopingCartDark from "@/assets/chat/shoopingCartDark.png";


import menuApplication from "../../../public/assets/imgs/menu-application.svg";
import applicationAc from "@/assets/chat/applicationAc.png";
import applicationDark from "@/assets/chat/applicationDark.png";

import { isMobileDevice } from "@/utils";
const defaultVal = {
  // 地图token
  mapToken: "测试数据",
  userInfo: null,
  storeCreateSessionFn: null,
  sysConfig: null,
  // 账户变动
  accountChange: null,

  // 账户信息
  accountInfo: null,
  // 选中的菜单id
  activeMenuItemKey: null,
  // 账户消耗提示语
  consumptionPrompt: null,

  // 网站主题
  systemTheme: "dark",
  // 导航菜单列表pc
  tabsDataListPc: [],
  // 导航菜单列表移动端
  tabsDataListMobile: [
    {
      label: "会员购买",
      id: 777,
      path: "/ai/commodity",
      icon: shoopingCart,
      iconAc: shoopingCartAc,
      iconDark: shoopingCartDark,
    },
    {
      label: "我的",
      id: 999,
      path: "",
      icon: user,
      iconAc: userAc,
      iconDark: userDark,
    },
  ],

  // 知识库选中的项目
  selectProjectMenuItem: null,
  // 用户单词消耗代币额度
  userConsumer: null,
};

if (isMobileDevice()) {
  defaultVal.tabsDataListMobile.unshift({
    label: "工作台",
    id: 666,
    path: "/ai/workbench",
    icon: menuApplication,
    iconAc: applicationAc,
    iconDark: applicationDark,
  });
}

// 进行默认值赋值
const home_reducer = (state = defaultVal, action) => {
  const { type, data } = action;

  switch (type) {
    case MAP_TOKEN:
      return {
        ...state,
        mapToken: data,
      };
    case USER_INFO:
      return {
        ...state,
        userInfo: data,
      };

    case STORE_CREATE_SESSION_FN:
      return {
        ...state,
        storeCreateSessionFn: data,
      };

    case SYS_CONFIG:
      return {
        ...state,
        sysConfig: data,
      };
    case ACCOUNT_CHANGE:
      return {
        ...state,
        accountChange: data,
      };
    case CONSUMPTION_PROMPT:
      return {
        ...state,
        consumptionPrompt: data,
      };

    // 存储菜单
    case TABS_DATA_LIST_PC:
      return {
        ...state,
        tabsDataListPc: data,
      };

    // 选中的菜单id
    case ACTIVE_MENU_ITEM_KEY:
      return {
        ...state,
        activeMenuItemKey: data,
      };
    case SYSTEM_THEME:
      return {
        ...state,
        systemTheme: data,
      };
    // 知识库选中的项目
    case SELECT_PROJECT_MENU:
      return {
        ...state,
        selectProjectMenuItem: data,
      };

    // 账户信息
    case ACCOUNT_INFO:
      return {
        ...state,
        accountInfo: data,
      };

    // 用户单词消耗额度
    case USER_CONTROLLER:
      return {
        ...state,
        userConsumer: data,
      };

    default:
      // 当我们第一次初始化时需要将默认值返回出去
      return state;
  }
};

export default home_reducer;
