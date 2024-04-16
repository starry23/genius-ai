/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-08 10:06:25
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-12-23 17:25:25
 */
import React, { useState, forwardRef, useImperativeHandle } from "react";
import { Modal, Menu } from "antd";
import {
  InfoCircleOutlined,
  ShareAltOutlined,
  IssuesCloseOutlined,
  UnorderedListOutlined,
} from "@ant-design/icons";
import ProjectInfo from "./components/ProjectInfo";
import ProjectShare from "./components/ProjectShare";
import ProjectWelcomeMsg from "./components/ProjectWelcomeMsg";
import ProjectMenu from "./components/ProjectMenu";
import styles from "./index.module.less";
const ProjectSetting = forwardRef(({ projetcInfo }, ref) => {
  const [pageState, setPageState] = useState(false);
  const [activeMenuKey, setActiveMenuKey] = useState("projectSetting");
  useImperativeHandle(ref, () => {
    return {
      getPage,
    };
  });

  const menuList = [
    {
      label: "基本设置",
      key: "projectSetting",
      icon: <InfoCircleOutlined></InfoCircleOutlined>,
    },
    {
      label: "分享设置",
      key: "shareSetting",
      icon: <ShareAltOutlined></ShareAltOutlined>,
    },

    {
      label: "欢迎语设置",
      key: "projectWelcomeMsg",
      icon: <IssuesCloseOutlined></IssuesCloseOutlined>,
    },
    {
      label: "菜单设置",
      key: "projectMenu",
      icon: <UnorderedListOutlined></UnorderedListOutlined>,
    },
  ];

  /**
   * @description: 弹框展示
   * @return {*}
   * @author: jl.g
   */
  const getPage = () => {
    setPageState(true);
    setActiveMenuKey("projectSetting");
  };

  /**
   * @description: 弹框隐藏
   * @return {*}
   * @author: jl.g
   */

  const hidePage = () => {
    setPageState(false);
  };

  // 菜单点击
  const menuItemClick = (menuItem) => {
    setActiveMenuKey(menuItem.key);
  };

  return (
    <Modal
      footer={null}
      className={styles.projectSettingWarp}
      title="项目设置"
      width={650}
      visible={pageState}
      onCancel={hidePage}
      destroyOnClose={true}
    >
      <div className="projectWarp">
        <Menu
          onClick={menuItemClick}
          selectedKeys={[activeMenuKey]}
          style={{
            width: 125,
            fontSize: 12,
          }}
          mode="inline"
          items={menuList}
        ></Menu>
        <div className="content">
          {/* 项目信息修改 */}
          {activeMenuKey === "projectSetting" ? (
            <ProjectInfo
              key={"projectSetting"}
              projetcInfo={projetcInfo}
            ></ProjectInfo>
          ) : (
            ""
          )}

          {/* 分享设置 */}
          {activeMenuKey === "shareSetting" ? (
            <ProjectShare
              key={"shareSetting"}
              projetcInfo={projetcInfo}
            ></ProjectShare>
          ) : (
            ""
          )}

          {/* 欢迎语设置 */}
          {activeMenuKey === "projectWelcomeMsg" ? (
            <ProjectWelcomeMsg
              key={"projectWelcomeMsg"}
              projetcInfo={projetcInfo}
            ></ProjectWelcomeMsg>
          ) : (
            ""
          )}

          {/* 菜单栏设置 */}
          {activeMenuKey === "projectMenu" ? (
            <ProjectMenu
              key={"projectMenu"}
              projetcInfo={projetcInfo}
            ></ProjectMenu>
          ) : (
            ""
          )}
        </div>
      </div>
    </Modal>
  );
});

export default ProjectSetting;
