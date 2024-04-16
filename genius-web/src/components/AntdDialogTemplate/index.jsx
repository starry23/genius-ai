/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-08 10:06:25
 * @LastEditors: jl.g
 * @LastEditTime: 2023-07-18 11:19:48
 */
import React, { useState, forwardRef, useImperativeHandle } from "react";
import { Modal } from "antd";

const AntdDialogTemplate = forwardRef((props, ref) => {
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

  //   点击确定
  const handleOk = () => {};
  return (
    <Modal
      title="新建"
      visible={pageState}
      onOk={handleOk}
      onCancel={hidePage}
    ></Modal>
  );
});

export default AntdDialogTemplate;
