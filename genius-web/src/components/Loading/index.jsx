/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-05 09:38:57
 * @LastEditors: jl.g
 * @LastEditTime: 2023-07-09 11:03:50
 */
import React from "react";
import { SyncOutlined } from "@ant-design/icons";
import "./index.less";
import "./index.css";
const index = () => {
  return (
    <div className="newtons-cradle_warp">
      正在思考中
      <SyncOutlined style={{ marginLeft: "5px" }} spin />
    </div>
  );
};

export default index;
