/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2022-08-17 08:47:20
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 20:44:46
 */

import React, { useEffect } from "react";
import { Modal } from "antd";
import { getLocalStorage, setLocalStorage, clearSessionStorage } from "@/utils";
import { checkVersion } from "@/api/login";
import CustomRoute from "@/components/CustomRoute/CustomRoute";
// 引入store
import store from "@/store/store.js";
import { Provider } from "react-redux";
import "./App.less";

const App = () => {
  // 检查版本更新
  const checkVersionFn = async () => {
    try {
      // 从缓存中获取verson
      let nowVerson = getLocalStorage("verson");
      let params = {
        version: nowVerson,
      };
      let res = await checkVersion(params);
      if (res.code == 2003) {
        let resVersion = res.result;

        if (nowVerson) {
          // 版本过低,强制更新
          if (resVersion !== nowVerson) {
            Modal.info({
              title: "发现新版本!",
              content: (
                <div>
                  <p>
                    尊敬的用户您好,您当前的版本过低,为了您更好的体验,点击升级按钮查看新版本内容。
                  </p>
                </div>
              ),
              okText: "升级",
              onOk() {
                setLocalStorage("verson", res.result);
                clearSessionStorage();
                window.location.reload();
              },
            });
          }
        } else {
          setLocalStorage("verson", res.result);
        }
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 是否自动展示公告
  const showNotice = () => {
    let nowNoticeTime = new Date().getTime();
    let oldNoticeTime = window.localStorage.getItem("oldNoticeTime");
    if (oldNoticeTime) {
      oldNoticeTime = Number(oldNoticeTime);
    }

    if (!oldNoticeTime) {
      window.localStorage.setItem("noticeState", true);
      window.localStorage.setItem("oldNoticeTime", nowNoticeTime);
    }

    if (oldNoticeTime + 60 * 60 * 24 * 1000 < nowNoticeTime) {
      window.localStorage.setItem("noticeState", true);
    }
  };

  showNotice();

  // 从loacl storage获取用户主题并且设置主题
  useEffect(() => {
    // 版本号对比
    checkVersionFn();
  }, []);
  return (
    <Provider store={store}>
      <CustomRoute></CustomRoute>
    </Provider>
  );
};

export default App;
