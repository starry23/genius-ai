/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-08-06 16:38:41
 * @LastEditors: jl.g
 * @LastEditTime: 2023-10-23 17:19:05
 */
import React, { useState, useEffect } from "react";
import Ribbons from "@/utils/ribbon";
import { getUrlParameter, isMobileDevice } from "@/utils";
import WaveCanvas from "@/components/WaveCanvas";
import { systemConfig } from "@/api/login";
import KnowledgeChat from "./KnowledgeChat";
import { setLocalStorage,unicodeFun } from "@/utils";
import knowlegdeShareIcon from "../../../../../public/assets/knowledge/knowlegdeShareIcon.png";
import styles from "./index.module.less";
const SharePage = () => {
  const [sysConfig, setSysConfig] = useState({});
  // 项目名称
  const [projectName, setProjectName] = useState("");
  // 获取系统配置
  const systemConfigFn = async () => {
    try {
      let res = await systemConfig();
      if (res.code === 200) {
        let resData = res.result;
        setSysConfig(resData);
        // 设置网站图标
        var link =
          document.querySelector("link[rel*='icon']") ||
          document.createElement("link");

        link.type = "image/x-icon";

        link.rel = "shortcut icon";

        link.href = resData?.iconUrl || "";

        document.getElementsByTagName("head")[0].appendChild(link);
        document.title = resData.webName;
      }
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    // 设置默认为白天主题
    setLocalStorage("theme", "light");
    document.body.setAttribute("theme-mode", "light");
    systemConfigFn();

    // 获取项目名称
    let urlProjectName = getUrlParameter("projectName");
    urlProjectName = unicodeFun.UnicodeDecode(urlProjectName)
    setProjectName(decodeURIComponent(urlProjectName));

    // 只有pc端才进行菜单展示
    if (!isMobileDevice()) {
      // 彩带
      new Ribbons("", "ribbonsCanvas");
    }
  }, []);
  return (
    <div className={styles.sharePageWarp}>
      <div className="sharePage_warp-content">
        <div className="sharePage_head-warp">
          {/* <WaveCanvas></WaveCanvas> */}
          <div className="sharePage_head-box">
            {/* <div className="logoBox">
              <img className="logo" src={knowlegdeShareIcon} alt="" />
              <p className="title">{sysConfig.webName}</p>
            </div> */}

            <div className="projectName">{projectName}</div>
          </div>
        </div>
        <div className="sharePage_content">
          <KnowledgeChat></KnowledgeChat>
        </div>

        <div className="technicalSupport">
          本产品由{" "}
          <a href={window.location.origin} target="_blank">
            {sysConfig.webName}
          </a>{" "}
          提供技术支持
        </div>
      </div>

      {/* 显示彩带 */}
      <div id="ribbonsCanvas"></div>
    </div>
  );
};

export default SharePage;
