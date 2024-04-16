/*
 * @Description:
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-09-16 16:55:06
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-28 21:56:04
 */
/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2022-08-17 08:47:20
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-02-23 16:27:19
 */

import React, { useState, useRef, useEffect } from "react";
import { Layout, ConfigProvider } from "antd";
import zhCN from "antd/es/locale/zh_CN";
import { useSelector } from "react-redux";
import { useHistory } from "react-router-dom";
import UserDetail from "@/components/HeadComponent/components/UserDetail";
import PromptsTemplate from "@/components/PromptsTemplate";
import AssistantsList from "@/components/AssistantsList";
import RouteRegist from "./RouteRegist";
import _ from "lodash";
import "../../App.less";
const { Content } = Layout;
const LayoutPage = (props) => {
  const history = useHistory();
  // 获取主题
  const systemTheme = useSelector((state) => state.systemTheme);

  // 获取系统配置
  const sysConfig = useSelector((state) => state.sysConfig);

  const tabsDataListPc = useSelector((state) => state.tabsDataListPc);
  // 菜单选中项变化
  let activeMenuItemKey = useSelector((state) => state.activeMenuItemKey);
  // 提示列表
  const assistantsListRef = useRef(null);
  // 移动端底部切换列表
  const tabsDataListMobile = useSelector((state) => state.tabsDataListMobile);
  const [tabsMobileDataList, setTabsMobileDataList] = useState([]);
  // 移动端选中tab
  const [selectTab, setSelectTab] = useState(tabsDataListMobile[0].id);
  const userDetailRef = useRef(null);

  // 移动端tab切换
  const onTabChange = (data) => {
    setSelectTab(data.id);

    // 个人信息需要弹窗展示
    if (!data.path) {
      switch (data.id) {
        // 个人信息
        case 999:
          // userDetailRef.current.getPage();
          history.replace("/ai/user");
          break;
        case 888:
          // 助手
          history.replace("/ai/home");
          assistantsListRef.current.getPage();
          break;
        default:
          break;
      }
    } else {
      history.replace(data.path);
    }
  };

  // 生成移动端菜单
  const genearteMobileMenuList = () => {
    // 判断菜单是否超过三个
    let copyTabsDataListPc = _.cloneDeep(tabsDataListPc);
    // 移动端不展示知识库菜单
    copyTabsDataListPc = copyTabsDataListPc.filter((v) => v.id !== 5);
    let showTabsDataListPc = [];

    if (copyTabsDataListPc.length > 3) {
      showTabsDataListPc = copyTabsDataListPc.slice(0, 3);
    }

    let mobileAllMenuList = [...showTabsDataListPc, ...tabsDataListMobile];

    // 此处新增逻辑 如果菜单超过3个 超过部分需要按照以下规则进行分类
    // 将菜单前三个菜单保留，超出部分将移动到工作台菜单进行展示
    // 移动端所有菜单如下
    // 系统配置的前三个菜单 + 工作台+会员购买+我的

    setTabsMobileDataList(mobileAllMenuList);
    setSelectTab(mobileAllMenuList[0].id);
  };
  useEffect(() => {
    genearteMobileMenuList();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tabsDataListPc]);

  useEffect(() => {
    if (activeMenuItemKey) {
      setSelectTab(Number(activeMenuItemKey));
    }
  }, [activeMenuItemKey]);

  return (
    <ConfigProvider locale={zhCN} prefixCls={systemTheme}>
      <Layout className="App">
        <Layout className="LayoutContent">
          <Content className="site-layout-background">
            <div className="layout-content_warp">
              {/* 左侧路由导航 */}
              <PromptsTemplate />

              <div className="layout-content-router">
                <RouteRegist></RouteRegist>
              </div>

              {/* 移动端展示底部tabbar */}
              <div
                className="mobile_bottom-tabBar"
                style={{
                  gridTemplateColumns: `repeat(${tabsMobileDataList.length}, minmax(0px, 1fr))`,
                }}
              >
                {tabsMobileDataList.map((v) => (
                  <div
                    onClick={() => onTabChange(v)}
                    key={v.id}
                    className={`tabBar_item-box`}
                  >
                    {selectTab === v.id ? (
                      <img className="tabBar_item-img " src={v.iconAc} alt="" />
                    ) : (
                      <>
                        <img
                          className="tabBar_item-img menuLightDisplay "
                          src={v.icon}
                          alt=""
                        />
                        <img
                          className="tabBar_item-img menuDarkDisplay"
                          src={v.iconDark}
                          alt=""
                        />
                      </>
                    )}

                    <span className="tabBar_item-name">{v.label}</span>
                  </div>
                ))}
              </div>
            </div>
          </Content>
        </Layout>

        {/* 提示列表 */}
        <AssistantsList ref={assistantsListRef}></AssistantsList>
        <UserDetail ref={userDetailRef}></UserDetail>
      </Layout>
    </ConfigProvider>
  );
};

export default LayoutPage;
