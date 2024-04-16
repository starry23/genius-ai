import React from "react";
import "./index.css"
import styles from "./index.module.less";
const LoadingComponent = () => {
  return (
    <div className={styles.loadingComponent}>
      <div className="loading-wave">
        <div className="loading-bar"></div>
        <div className="loading-bar"></div>
        <div className="loading-bar"></div>
        <div className="loading-bar"></div>
      </div>
    </div>
  );
};

export default LoadingComponent;
