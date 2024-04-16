/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2022-08-17 13:31:40
 * @LastEditors: jl.g
 * @LastEditTime: 2023-07-18 09:44:02
 */
import React,{lazy, Suspense } from "react";
import ReactDOM from "react-dom/client";
import reportWebVitals from "./reportWebVitals";
import { HashRouter } from "react-router-dom";
import LoadingComponent from "@/components/LoadingComponent"
import "./theme/light.css";
import "./theme/dark.css";
import "./index.css";

const App = lazy(() => import("./App"));
const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <HashRouter>
    <Suspense  fallback={<LoadingComponent></LoadingComponent>}>
      <App />
    </Suspense>
 
  </HashRouter>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
