/*
 * @Description:
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-09-16 16:55:06
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-29 20:53:01
 */
import React from "react";
import { Route, Switch } from "react-router-dom";

import Home from "@/pages/Home";
import News from "@/pages/news";
import Commodity from "@/pages/commodity";
import AiNavigation from "@/pages/aiNavigation";
import Midjourney from "@/pages/midjourney";
import AiAssistant from "@/pages/aiAssistant";
import MindMap from "@/pages/mindMap";
import Dalle3 from "@/pages/dalle3";
import Gpts from "@/pages/gpts";
import KnowledgeLayout from "@/pages/knowledgeLayout";
import User from "@/pages/user";
import Workbench from "@/pages/workbench/index";
import Wechatboot from "@/pages/wechatboot";
import { isMobileDevice } from "@/utils";
const RouteRegist = () => {
  return (
    <>
      {/* 路由注册 */}
      <Switch>
        <Route path="/ai/home" component={Home} key="/ai/home"></Route>

        {/* 购买 */}
        <Route
          path="/ai/commodity"
          component={Commodity}
          key="/ai/commodity"
        ></Route>

        {/* ai绘画 */}
        <Route path="/ai/mj" component={Midjourney} key="/ai/mj"></Route>

        {/* ai新闻 */}
        <Route path="/ai/news" component={News} key="/ai/news"></Route>

        {/* ai导航 */}
        <Route
          path="/ai/aiNavigation"
          component={AiNavigation}
          key="/ai/aiNavigation"
        ></Route>
        {/* Ai助手 */}
        <Route
          path="/ai/aiAssistant"
          component={AiAssistant}
          key="/ai/aiAssistant"
        ></Route>

        {/* Dalle3 */}
        <Route path="/ai/dalle3" component={Dalle3} key="/ai/dalle3"></Route>

        {/* 思维导图 */}

        <Route path="/ai/mindMap" component={MindMap} key="/ai/mindMap"></Route>

        {/* gpts */}

        <Route path="/ai/gpts" component={Gpts} key="/ai/gpts"></Route>

        {/* 知识库 */}
        <Route path="/ai/doc" component={KnowledgeLayout} key="/ai/doc"></Route>

        {/* 用户信息 */}
        <Route path="/ai/user" component={User} key="/ai/user"></Route>

        {/* 工作台 */}
        {isMobileDevice() ? (
          <Route
            path="/ai/workbench"
            component={Workbench}
            key="/ai/workbench"
          ></Route>
        ) : (
          ""
        )}

        {/* 微信客服 */}
        <Route
          path="/ai/wechatboot"
          component={Wechatboot}
          key="/ai/wechatboot"
        ></Route>
      </Switch>
    </>
  );
};

export default RouteRegist;
