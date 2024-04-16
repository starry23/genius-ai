import React from "react";
import "./index.css";
const StarsBtn = ({ text = "登录", onClick }) => {
  const submitFn = () => {
    onClick();
  };
  return (
    <div className="starsBtnWarp" onClick={submitFn} alt="登 录">
      <i>进</i>
      <i>入</i>
      <i>A</i>
      <i>i</i>
      <i>&nbsp;</i>
      <i>世</i>
      <i>界</i>
    </div>
  );
};

export default StarsBtn;
