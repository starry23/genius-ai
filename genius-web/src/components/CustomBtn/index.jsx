import React from "react";
import styles from "./index.module.less";
const CustomBtn = (props) => {
  const {
    icon = "",
    fontSize = 12,
    width = 100,
    height = "100%",
    onClick,
    style = {},
  } = props;

  const btnClick = () => {
    onClick();
  };
  return (
    <div
      onClick={btnClick}
      style={{ width, height, ...style }}
      className={styles.customBtn}
    >
      {icon ? <img className="icon" src={icon} alt="" /> : ""}

      <span style={{ fontSize }} className="text">
        {props.children || "默认按钮"}
      </span>
    </div>
  );
};

export default CustomBtn;
