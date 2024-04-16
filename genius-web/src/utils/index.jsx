/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-03 09:16:53
 * @LastEditors: jl.g
 * @LastEditTime: 2023-10-23 17:16:57
 */
import jsCookie from "js-cookie";
import copy from "copy-to-clipboard";
import { message, notification, Button } from "antd";
import { SmileOutlined } from "@ant-design/icons";
import { menuDictionary } from "./menu";
export const getCookie = (name) => {
  return jsCookie.get(name);
};

export const setCookie = (name, value) => {
  jsCookie.set(name, value, {
    expires: 30,
    path: "/",
  });
};

export const removeCookie = (name) => {
  jsCookie.remove(name);
};
/**
 * @description: 异常提醒
 * @param {*} type 类型
 * @param {*} content 提示内容
 * @return {*}
 * @author: jl.g
 */
export const messageFn = ({ type, content }) => {
  switch (type) {
    case "success":
      message.success({
        content,

        className: "global_message",
        style: {
          marginTop: "10vh",
        },
      });
      break;
    case "error":
      message.error({
        content,

        className: "global_message",
        style: {
          marginTop: "10vh",
        },
      });
      break;
    case "info":
      message.info({
        content,

        className: "global_message",
        style: {
          marginTop: "10vh",
        },
      });
      break;
    default:
      message.success({
        content,

        className: "global_message",
        style: {
          marginTop: "10vh",
        },
      });
      break;
  }
};

/**
 * @description: 复制内容到剪切板
 * @return {*}
 * @author: jl.g
 */

export const copyToClipboardFn = (data, successInfo) => {
  copy(data);
  messageFn({
    type: "success",
    content: successInfo,
  });
};

/**
 * @description: 获取url中的参数
 * @return {*}
 * @author: jl.g
 */
export const queryString = (target) => {
  let url = window.location.href;

  if (url.indexOf("?") === -1) return false;
  let urlList = url.split("?");
  let paramsStr = urlList[1];
  let paramsList = paramsStr.split("&");
  let queryStringObj = {};
  paramsList.forEach((item) => {
    let newArr = item.split("=");
    queryStringObj[newArr[0]] = decodeURIComponent(newArr[1]);
  });

  return queryStringObj[target];
};

// 设置session
export const setSessionStorage = (name, value) => {
  window.sessionStorage.setItem(name, value);
};

// 读取session
export const getSessionStorage = (name) => {
  return window.sessionStorage.getItem(name);
};

// 清空sessionStorage
export const clearSessionStorage = () => {
  return window.sessionStorage.clear();
};

// 设置LocalStorage
export const setLocalStorage = (name, value) => {
  window.localStorage.setItem(name, value);
};

// 读取LocalStorage
export const getLocalStorage = (name, value) => {
  return window.localStorage.getItem(name, value);
};

// 全局提示
export const backLogoinInfo = (name) => {
  const btn = (
    <Button
      type="primary"
      size="small"
      onClick={() => {
        notification.close("noLoginTips");
        window.location.href =
          window.location.origin +
          process.env.REACT_APP_PUBLIC_PATH +
          "#/login";
      }}
    >
      去登录{" "}
    </Button>
  );
  notification.open({
    message: "尊敬的用户您好:",
    description: "当前您暂未登录,或登录状态已经失效,点击按钮,跳转登录页。",
    icon: (
      <SmileOutlined
        style={{
          color: "#108ee9",
        }}
      />
    ),
    btn,
    duration: 0,
    key: "noLoginTips",
  });
};

// 下载图片
export function downloadImage(imageUrl) {
  let fileName = "image.png";
  const link = document.createElement("a");
  link.href = imageUrl;
  link.download = fileName;

  // 触发点击事件
  document.body.appendChild(link);
  link.click();
  link.remove();
}

// 根据后台返回的菜单id，对比前端字典，获取需要展示的菜单列表
export const geneartMenu = (leftMenuIds) => {
  // menuDictionary
  let resMenuList = [];
  leftMenuIds.forEach((v) => {
    menuDictionary.forEach((menItem) => {
      if (menItem.id === v) {
        resMenuList.push(menItem);
      }
    });
  });
  return resMenuList;
};

// 判断是移动端还是pc
export const isMobileDevice = () => {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
    navigator.userAgent
  );
};

export const isWechatSide = () => {
  return navigator.userAgent.toLowerCase().indexOf('micromessenger') > -1 ||
      typeof navigator.wxuserAgent !== 'undefined'
}

export const getUrlParameter = (name) => {
  let url = window.location.href;
  let searchParams = url.split("?")[1];
  let params = [searchParams];
  if (searchParams.indexOf("&") !== -1) {
    params = searchParams.split("&");
  }
  let resultObj = {};
  params.forEach((val) => {
    let keyVal = val.split("=");
    resultObj[keyVal[0]] = keyVal[1];
  });

  return resultObj[name];
};

// 金额超过万 则换算单位
export const formatAmount = (amount) => {
  if (amount >= 10000) {
    return `${(amount / 10000).toFixed(2)} 万`;
  }
  return amount;
};

// 中文转unicodeFun 与解析
export var unicodeFun = {
  toUnicode: function (str) {
    if (str == "") return "value is null";
    return escape(str).toLocaleLowerCase().replace(/%u/gi, "T%u");
  },
  UnicodeDecode: function (str) {
    if (str == "") return "value is null";
    return unescape(str.replace(/T%u/gi, "%u"));
  },
};
