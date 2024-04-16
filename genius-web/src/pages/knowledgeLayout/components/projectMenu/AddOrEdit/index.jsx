/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-08 10:06:25
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-30 20:02:32
 */
import React, { useState, forwardRef, useImperativeHandle } from "react";
import { Modal, Form, Input } from "antd";
import { knowledgeCreateItem } from "@/api/knowledgeBase";
import { messageFn } from "@/utils";
const { TextArea } = Input;
const AddOrEdit = forwardRef(({ getKnowledgeProJectList }, ref) => {
  const [baseForm] = Form.useForm();
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

    if (baseForm) {
      baseForm.resetFields();
    }
  };

  /**
   * @description: 弹框隐藏
   * @return {*}
   * @author: jl.g
   */

  const hidePage = () => {
    setPageState(false);
  };

  // 创建项目
  const createKnowledgeItem = async (data) => {
    try {
      let res = await knowledgeCreateItem(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "项目创建成功",
        });
        getKnowledgeProJectList();
        hidePage();
      } else {
        messageFn({
          type: "error",
          content: res.message,
        });
      }
    } catch (error) {
      console.log(error);
    }
  };

  //   点击确定
  const handleOk = () => {
    baseForm
      .validateFields()
      .then((values) => {
        createKnowledgeItem(values);
      })
      .catch((err) => {
        console.log(err);
      });
  };
  return (
    <Modal
      title="新建项目"
      width={550}
      visible={pageState}
      onOk={handleOk}
      onCancel={hidePage}
    >
      <Form
        form={baseForm}
        labelCol={{
          span: 4,
        }}
        wrapperCol={{
          span: 20,
        }}
        autoComplete="off"
      >
        <Form.Item
          label="项目名称"
          name="itemName"
          rules={[
            {
              required: true,
              message: "请输入项目名称",
            },
          ]}
        >
          <Input maxLength={20} placeholder="请输入项目名称" />
        </Form.Item>

        <Form.Item label="项目简介" name="itemDesc">
          <TextArea rows={8} placeholder="请输入项目简介" maxLength={200} />
        </Form.Item>
      </Form>
    </Modal>
  );
});

export default AddOrEdit;
