/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2022-08-17 09:37:55
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-28 21:30:57
 */

import React, { lazy } from "react";
import { Route, Switch, Redirect } from "react-router-dom";
import AuthRouteHoc from "@/components/AuthRouteHoc";
import Login from "@/pages/login";
import KnowledgeShare from "@/pages/knowledgeLayout/pages/sharePage";
import LayoutPage from "@/pages/layout/index";
const CustomRoute = () => {
  return (
    <div style={{ width: "100%", height: "100%" }}>
      <Switch>
        <Route
          exact={true}
          path="/"
          render={() => <Redirect to="/ai/home" />}
          key="default"
        ></Route>
        <Route path="/login" component={Login} key="login"></Route>

        <Route
          path="/ai"
          component={AuthRouteHoc(LayoutPage)}
          key="/ai"
        ></Route>

        {/* 知识库分享 */}
        <Route
          path="/knowledgeShare"
          component={KnowledgeShare}
          key="/knowledgeShare"
        ></Route>
      </Switch>
    </div>
  );
};

export default CustomRoute;
