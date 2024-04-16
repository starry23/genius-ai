/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2022-11-06 14:48:47
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-20 12:20:40
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
  USER_CONTROLLER
} from "../constants/home_constant";



// 地图token
export const mapTokenAction = (data) => {
  return {
    type: MAP_TOKEN,
    data,
  };
};

// 用户信息
export const userInfoAction = (data) => {
  return {
    type: USER_INFO,
    data,
  };
};

// 创建会话方法
export const createSessionFnAction = (data) => {
  return {
    type: STORE_CREATE_SESSION_FN,
    data,
  };
};

// 网站配置信息
export const createSysConfig = (data) => {
  return {
    type: SYS_CONFIG,
    data,
  };
};

// 账户变动
export const accountChangeAction = (data) => {
  return {
    type: ACCOUNT_CHANGE,
    data,
  };
}

// 账户消耗信息提示
export const consumptionPromptAction = (data) => {
  return {
    type: CONSUMPTION_PROMPT,
    data,
  };
}

// 菜单列表
export const setMenuDataListAction = (data) => {
  return {
    type: TABS_DATA_LIST_PC,
    data,
  };
}

// 选中的菜单id
export const setActiveMenuItemKey = (data) => {
  return {
    type: ACTIVE_MENU_ITEM_KEY,
    data,
  };
}

// 网站主题
export const systemThemAction = (data) => {
  return {
    type: SYSTEM_THEME,
    data,
  };
}

// 知识库选中的项目
export const setProjectMenuItem = (data) => {
  return {
    type: SELECT_PROJECT_MENU,
    data,
  };
}


// 账户信息
export const accountInfoAction = (data) => {
  return {
    type: ACCOUNT_INFO,
    data,
  };
}

// 用户单词消耗具体额度
export const userConsumerAction = (data) => {
  return {
    type: USER_CONTROLLER,
    data,
  };
}