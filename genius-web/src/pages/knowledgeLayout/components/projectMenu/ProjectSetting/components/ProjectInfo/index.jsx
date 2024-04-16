/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-08 10:06:25
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-06 17:59:28
 */
import React from "react";
import { Form, Input, Button } from "antd";
import { knowledgeBaseSetting } from "@/api/knowledgeBase";
import { messageFn } from "@/utils";
const { TextArea } = Input;
const ProjectInfo = ({ projetcInfo }) => {
  const [baseForm] = Form.useForm();

  //   点击确定
  const handleOk = async (values) => {
    try {
      let data = {
        ...values,
        id: projetcInfo?.itemId,
      };
      let res = await knowledgeBaseSetting(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "项目信息更新成功",
        });
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
  return (
    <Form
      initialValues={{
        itemName: projetcInfo?.itemName,
        itemDesc: projetcInfo?.itemDesc,
      }}
      form={baseForm}
      labelCol={{
        span: 4,
      }}
      wrapperCol={{
        span: 20,
      }}
      autoComplete="off"
      onFinish={handleOk}
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

      <Form.Item
        wrapperCol={{
          offset: 20,
          span: 4,
        }}
      >
        <Button type="primary" htmlType="submit">
          确定
        </Button>
      </Form.Item>
    </Form>
  );
};

export default ProjectInfo;
