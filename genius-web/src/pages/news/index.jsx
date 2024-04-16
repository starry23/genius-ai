/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-06-13 08:50:49
 * @LastEditors: jl.g
 * @LastEditTime: 2023-06-13 09:17:09
 */
import React from "react";
import noData from "../../../public/assets/imgs/noData.svg";
import styles from "./index.module.less";
const News = () => {
  return (
    <div className={styles.newsWarp}>
      <div className="noData">
        <img className="img" src={noData} alt="" />
        <div className="info">功能正在加紧研发中,敬请期待</div>
      </div>
    </div>
  );
};

export default News;
