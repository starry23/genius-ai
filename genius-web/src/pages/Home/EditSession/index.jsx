/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-08 10:06:25
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:45:48
 */
import React, {
  useState,
  forwardRef,
  useImperativeHandle,
  useRef,
} from "react";
import { Modal, Input } from "antd";
import styles from "./index.module.less";
import { messageFn } from "@/utils";
import { updateSessionApi } from "@/api/home";
const EditSession = forwardRef(({getSessionList}, ref) => {
  const [pageState, setPageState] = useState(false);
  const [sessionName, setSessionName] = useState("");
  const [rowData, setRowData] = useState(null);
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
  const getPage = (data) => {
    setPageState(true);
    setRowData(data);
  };

  /**
   * @description: 弹框隐藏
   * @return {*}
   * @author: jl.g
   */

  const hidePage = () => {
    setPageState(false);
  };

  const handleOk = async () => {
    if (!sessionName) {
      messageFn({
        type: "error",
        content: "请输入会话名称",
      });
      return;
    }

    try {
      let data = {
        reqId: rowData?.reqId,
        name: sessionName,
      };
      let res = await updateSessionApi(data);

      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "操作成功",
        });

        getSessionList();
        hidePage();
      } else {
        messageFn({
          type: "error",
          content: "操作失败",
        });
      }
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <Modal
      className={styles.projectSettingWarp}
      title="编辑会话名称"
      width={350}
      visible={pageState}
      onCancel={hidePage}
      onOk={handleOk}
      destroyOnClose={true}
    >
      <Input
        defaultValue={rowData?.sessionName}
        onChange={(e) => setSessionName(e.target.value)}
        placeholder="请输入会话名称"
      ></Input>
    </Modal>
  );
});

export default EditSession;
