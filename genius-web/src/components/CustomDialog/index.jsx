/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-06 08:57:51
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-04 21:46:34
 */
import React, { useState, forwardRef, useImperativeHandle } from "react";
import styles from "./index.module.less";
import { CloseOutlined } from "@ant-design/icons";

const InviteGiftUrlInfo = forwardRef((props, ref) => {
  const { title = "默认弹窗", width = 500, } = props;
  const [pageState, setPageState] = useState(false);

  useImperativeHandle(ref, () => {
    return {
      getPage,
    };
  });
  /**
   * @description: 弹框展示
   * @return {*}
   * @author: jl.g
   */
  const getPage = () => {
    setPageState(true);
  };

  /**
   * @description: 弹框隐藏
   * @return {*}
   * @author: jl.g
   */

  const hidePage = () => {
    setPageState(false);
  };

  return pageState ? (
    <div className={styles.custom_dialog_warp}>
      <div className="custom_dialog-content">
        <div className="custom_dialog-head">
          <CloseOutlined
            onClick={hidePage}
            className="closeBtn"
            twoToneColor="#fff"
          ></CloseOutlined>
        </div>
        <div className="title">{title}</div>
        <div className="detail-content">{props.children}</div>
      </div>
    </div>
  ) : (
    ""
  );
});

export default InviteGiftUrlInfo;
