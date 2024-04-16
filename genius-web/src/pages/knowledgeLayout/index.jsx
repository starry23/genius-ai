/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-07-12 08:54:24
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-02-23 16:15:58
 */
import React from "react";
import { Layout, ConfigProvider } from "antd";
import { useSelector } from "react-redux";
import zhCN from "antd/es/locale/zh_CN";
import RouteRegist from "./RouteRegist";
import ProjectMenu from "./components/projectMenu";
import styles from "./index.module.less";
const { Content } = Layout;
const KnowledgeLayout = () => {
  // 获取主题
  const systemTheme = useSelector((state) => state.systemTheme);
  return (
    <ConfigProvider locale={zhCN} prefixCls={systemTheme}>
      <Layout className={styles.knowledgeLayoutWarp}>


        <Layout className="LayoutContent">
          <Content className="site-layout-background">
            <div className="projectMenu">
              <ProjectMenu />
            </div>

            <div className="layout-content_warp">
              <RouteRegist />
            </div>
          </Content>
        </Layout>
      </Layout>
    </ConfigProvider>
  );
};

export default KnowledgeLayout;
