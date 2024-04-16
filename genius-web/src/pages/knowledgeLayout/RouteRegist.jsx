/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-07-12 09:11:16
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-02-27 20:45:52
 */
import React from "react";
import { Route, Switch } from "react-router-dom";
import Knowledge from "./pages/knowledge";
const RouteRegist = () => {
  return (
    <>
      {/* 路由注册 */}
      <Switch>
        <Route
          path="/ai/doc/knowledge"
          component={Knowledge}
          key="/ai/doc/knowledge"
        ></Route>
      </Switch>
    </>
  );
};

export default RouteRegist;
