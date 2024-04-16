/*
 * @Description:
 * @Version: 2.0
 * @Autor: jianqi.xue
 * @Date: 2023-03-17 17:57:17
 * @LastEditors: jl.g
 * @LastEditTime: 2023-09-19 09:40:07
 */
import React, {
  useRef,
  useState,
  forwardRef,
  useImperativeHandle,
} from "react";
import { Form, Input, Button, Modal } from "antd";
import _ from "lodash";
const { TextArea } = Input;
const AddOrEdit = forwardRef(({ tableData, setTableData }, ref) => {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [recordObj, setRecordObj] = useState(null);
  useImperativeHandle(ref, () => ({
    getPage: getPage,
  }));

  const currentIndex = useRef(null);
  const formRef = useRef(null);
  const layout = {
    labelCol: { span: 6 },
  };
  const tailLayout = {
    wrapperCol: { offset: 20, span: 4 },
  };
  // 弹窗初始化
  const getPage = (data, index) => {
    if (formRef.current) formRef.current.resetFields(); //清空form数据
    setRecordObj(data);
    currentIndex.current = index;
    setIsModalVisible(true);
    // 如果有data则为编辑，并回显数据
    setTimeout(() => {
      if (data) {
        formRef.current.setFieldsValue({
          prompt: data.prompt,
          content: data.content,
        });
      }
    });
  };
  // 取消 关闭弹框
  const handleCancel = () => {
    setRecordObj(null);
    setIsModalVisible(false);
  };

  // 保存数据
  const saveData = async (data) => {
    try {
      setTableData((valueData) => {
        let copyData = _.cloneDeep(valueData);
        copyData.push(data);
        return [...copyData];
      });
    } catch (error) {
      console.log(error);
    }
  };

  // 更新数据
  const updateData = async (data) => {
    try {
      setTableData((valueData) => {
        let copyData = _.cloneDeep(valueData);
        copyData.splice(currentIndex.current, 1, data);
        return [...copyData];
      });
    } catch (error) {
      console.log(error);
    }
  };

  // 提交
  const onFinish = async (values) => {
    try {
      recordObj ? updateData(values) : saveData(values);
      handleCancel();
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <Modal
      title={recordObj ? "编辑" : "新建"}
      maskClosable={false}
      width={500}
      visible={isModalVisible}
      onCancel={handleCancel}
      footer={[]}
    >
      <Form ref={formRef} {...layout} autoComplete="off" onFinish={onFinish}>
        <Form.Item
          name="prompt"
          label="关键词"
          rules={[{ required: true, message: "请输入关键词" }]}
        >
          <Input placeholder="请输入关键词" maxLength={50}></Input>
        </Form.Item>

        <Form.Item
          name="content"
          label="回复内容"
          rules={[{ required: true, message: "请输入回复内容" }]}
        >
          <TextArea
            rows={5}
            placeholder="请输入回复内容"
            maxLength={500}
          ></TextArea>
        </Form.Item>

        <Form.Item {...tailLayout}>
          <Button type="primary" htmlType="submit">
            确认
          </Button>
        </Form.Item>
      </Form>
    </Modal>
  );
});

export default AddOrEdit;
