/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-05-25 09:47:06
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-04 20:58:07
 */
import React, { useState } from "react";
import { Route, Switch } from "react-router-dom";
import { useHistory } from "react-router-dom";
import MjGenerate from "./mj-generate";
import { getCookie } from "@/utils";
import PictureSquare from "./pictureSquare";
import styles from "./index.module.less";
const Midjourney = () => {
  const history = useHistory();

  // 导航菜单
  const navMenu = [
    {
      label: "文生图",
      id: 0,
      path: "/ai/mj/generate",
    },
    {
      label: "风格广场",
      id: 1,
      path: "/ai/mj/pictureSquare",
    },
  ];
  const [navNode, setNavNode] = useState(
    getCookie("tokenValue") ? navMenu[0] : navMenu[1]
  );

  /**
   * @description: 导航点击
   * @param {*} item
   * @return {*}
   * @author: jl.g
   */
  const navClick = (item) => {
    setNavNode(item);
    history.replace(item.path);
  };
  return (
    <div className={styles.midjourneyWarp}>
      <div className="midjourne-nav">
        <div className="mj-nav_box">
          {navMenu.map((v) => (
            <div
              onClick={() => navClick(v)}
              key={v.id}
              className={`mj-nav_item ${
                navNode.id === v.id ? "mj-nav_selected" : ""
              }`}
            >
              {v.label}
            </div>
          ))}
        </div>
      </div>
      <div className="midjourne-content">
        <Switch>
          <Route
            path="/ai/mj/generate"
            component={MjGenerate}
            key="/ai/mj/generate"
          ></Route>

          <Route
            path="/ai/mj/pictureSquare"
            component={PictureSquare}
            key="/ai/mj/PictureSquare"
          ></Route>
        </Switch>
      </div>
    </div>
  );
};

export default Midjourney;
