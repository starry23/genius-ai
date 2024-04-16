import React from "react";
import { Drawer } from "antd";
import { useSelector } from "react-redux";
import styles from "./index.module.less";
const CustomNotic = ({ visible, handlerClose }) => {
  const sysConfig = useSelector((state) => state.sysConfig);
  return (
    <Drawer title="公告" onClose={() => handlerClose()} visible={visible}>
      <div className={styles.customNoticWarp}>{sysConfig?.indexPopup}</div>
    </Drawer>
  );
};

export default CustomNotic;
