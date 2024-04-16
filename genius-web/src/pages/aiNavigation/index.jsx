import React, { useRef, useState } from "react";
import navDataList from "./navListData";
import AI from "../../../public/assets/imgs/AI.svg";
import WebsiteRegistNum from "@/components/WebsiteRegistNum";
import CustomInput from "@/components/CustomInput";
import styles from "./index.module.less";
const AiNavigation = () => {
  const [activeTab, setActiveTab] = useState({
    name: "内容创作",
    value: "contentCreation",
  });
  const [activeTabDataList, setActiveTabDataList] = useState(
    navDataList[activeTab.value]
  );
  // 搜索备份数据
  const searchData = useRef(navDataList);

  //   ai分类
  const aiTabs = useRef([
    {
      name: "内容创作",
      value: "contentCreation",
    },
    {
      name: "高效办公",
      value: "efficientOfficeWork",
    },
    {
      name: "图像",
      value: "image",
    },
    {
      name: "音频",
      value: "audio",
    },
    {
      name: "视频",
      value: "video",
    },

    {
      name: "设计",
      value: "design",
    },

    {
      name: "编程开发",
      value: "code",
    },
    {
      name: "智能对话",
      value: "chat",
    },

    {
      name: "内容检测",
      value: "contentDetection",
    },

    {
      name: "训练模型",
      value: "modelTraining",
    },
    {
      name: "教程",
      value: "tutorial",
    },
    {
      name: "交流社区",
      value: "communityForum",
    },
  ]);

  //   分类切换
  const onTabChange = (data) => {
    setActiveTab(data);
    setActiveTabDataList(navDataList[data.value]);
  };

  //   搜索数据
  const searchAiData = (searchText) => {
    let filterData = [];

    for (let i in searchData.current) {
      let iData = searchData.current[i].filter(
        (v) =>
          v.name.indexOf(searchText) !== -1 ||
          v.description.indexOf(searchText) !== -1
      );
      filterData = filterData.concat(iData);
    }

    setActiveTabDataList(filterData);
  };

  const openWebAi = (data) => {
    window.open(data.url);
  };
  return (
    <div className={styles.aiNavigationWarp}>
      <div className="aiNav_nav-box">
        <div className="aiNav_nav-typography">超全面的 AI 网站导航</div>
        <div className="header-subtitle">你想要的这里全部都有</div>

        <CustomInput
          style={{
            height: "40px",
            marginBottom: "25px",
          }}
          showSearch
          search={searchAiData}
          placeholder="搜索你想要的内容"
        ></CustomInput>
      </div>

      <div className="aiNav_nav-tabs">
        {aiTabs.current.map((v) => (
          <div
            key={v.value}
            onClick={() => onTabChange(v)}
            className={`nav_tabs-item ${
              activeTab.value === v.value ? "nav_tabs-item-selected" : ""
            }`}
          >
            {v.name}
          </div>
        ))}
      </div>

      <div className="aiNav_nav-content">
        {activeTabDataList.map((v) => (
          <div
            onClick={() => openWebAi(v)}
            key={v.id + "_" + new Date().getTime()}
            className="aiNav_nav-content-item"
          >
            <img className="img" src={AI} alt="" />
            <div className="infoBox">
              <div className="title">{v.name}</div>
              <div className="desc">{v.description}</div>
            </div>
          </div>
        ))}
      </div>
      <WebsiteRegistNum></WebsiteRegistNum>
    </div>
  );
};

export default AiNavigation;
