/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-08 10:06:25
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:47:25
 */
import React, {
  useState,
  forwardRef,
  useImperativeHandle,
  useEffect,
} from "react";
import { Form, Input, Select, Upload, Button } from "antd";
import CustomBtn from "@/components/CustomBtn";
import { updateBot } from "@/api/wechatbot";
import styles from "./index.module.less";
import { CloseOutlined, UploadOutlined } from "@ant-design/icons";
import { getBotProductTypeApi } from "@/api/wechatbot";
import { messageFn } from "@/utils";
import { knowledgeProJectList } from "@/api/knowledgeBase";

import _ from "lodash";
const { TextArea } = Input;
const JoinGroup = forwardRef(({ getData, botItemData }, ref) => {
  const [baseForm] = Form.useForm();
  const [pageState, setPageState] = useState(false);
  const [pageData, setPageData] = useState(null);
  const [productConsumedTypeList, setProductConsumedTypeList] = useState([]);

  const [knowledgeState, setKnowledgeState] = useState(false);
  const [knowledgeList, setKnowledgeList] = useState([]);
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

    if (data) {
      let copyDaya = _.cloneDeep(data);
      copyDaya.kefuAvatar = [
        {
          uid: "rc-upload-1711881794185-19",
          lastModified: 1708049092149,
          lastModifiedDate: "2024-02-16T02:04:52.149Z",
          name: "DOAX-VenusVacation_240216_100452.jpg",
          size: 336440,
          type: "image/jpeg",
          percent: 100,
          originFileObj: {
            uid: "rc-upload-1711881794185-19",
          },
          status: "done",
          response: {
            result: {
              path: data.kefuAvatar,
            },
          },
          xhr: {},
        },
      ];

      if (data?.knowledgeId) {
        setKnowledgeState(true);
      }

      baseForm.setFieldsValue(copyDaya);
      setPageData(copyDaya);
    } else {
      setPageData(null);
      if (baseForm) {
        baseForm.resetFields();
      }
    }

    getKnowledgeProJectList();
  };

  // 查询项目列表
  const getKnowledgeProJectList = async () => {
    try {
      let res = await knowledgeProJectList();
      if (res.code === 200) {
        let resData = res.result || [];
        resData = resData.map((v) => {
          return {
            label: v.itemName,
            value: v.itemId,
          };
        });

        setKnowledgeList(resData);
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
  /**
   * @description: 弹框隐藏
   * @return {*}
   * @author: jl.g
   */

  const hidePage = () => {
    setPageState(false);
    setPageData(null);
    setKnowledgeState(null);
  };

  // 更新
  const updateBotFn = async (data) => {
    try {
      if (pageData) {
        data.id = pageData.id;
      }
      let res = await updateBot(data);
      if (res.code === 200) {
        setPageState(false);
        messageFn("操作成功");
        getData();
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

  // 获取模型类型
  const getProductConsumedTypeList = async () => {
    try {
      let res = await getBotProductTypeApi();
      if (res.code === 200) {
        let resData = res.result;
        // 问答模型 1 3.5  2 gpt4  4 文心  7 星火

        if (resData.length) {
          resData = resData.map((item) => {
            return {
              value: item.key,
              label: item.value,
            };
          });
        }

        setProductConsumedTypeList(resData);
      } else {
        setProductConsumedTypeList([]);
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 知识库类型选择
  const productTypeChange = (data) => {
    if (data === 5 || data === 10) {
      setKnowledgeState(true);
    } else {
      setKnowledgeState(false);
    }
  };

  // 提交
  const submit = () => {
    baseForm
      .validateFields()
      .then((values) => {
        if (values.kefuAvatar && values.kefuAvatar.length) {
          // if(values.kefuAvatar)
          let fullUrl = values?.kefuAvatar[0]?.response?.result?.path;
          values.kefuAvatar = fullUrl;
        }

        updateBotFn(values);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    getProductConsumedTypeList();
  }, []);

  useEffect(() => {
    if (botItemData) {
    }
  }, [botItemData]);
  return (
    <>
      {pageState ? (
        <div className={styles.custom_dialog}>
          <div className="custom_dialog-warp animate__animated animate__fadeInDown">
            <div className="custom_dialog-head">
              <CloseOutlined
                onClick={hidePage}
                className="closeBtn"
                twoToneColor="#fff"
              ></CloseOutlined>
            </div>

            <div className="title">
              {pageData ? "编辑机器人" : "新增机器人"}
            </div>

            <div className="custom_dialog-content">
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
                  label="机器人名称"
                  name="botName"
                  rules={[
                    {
                      required: true,
                      message: "请输入机器人名称",
                    },
                  ]}
                >
                  <Input placeholder="请输入机器人名称" />
                </Form.Item>

                <Form.Item
                  label="客服名称"
                  name="kefuName"
                  rules={[
                    {
                      required: true,
                      message: "请输入客服名称",
                    },
                  ]}
                >
                  <Input placeholder="请输入客服名称" />
                </Form.Item>

                <Form.Item
                  label="客服头像"
                  name="kefuAvatar"
                  valuePropName="fileList"
                  getValueFromEvent={(e) => {
                    if (Array.isArray(e)) {
                      return e;
                    }
                    return e?.fileList;
                  }}
                >
                  <Upload
                    data={{
                      viewType: 1,
                    }}
                    maxCount={1}
                    multiple={false}
                    action="/api/upload/file"
                  >
                    <Button icon={<UploadOutlined />}>上传</Button>
                  </Upload>
                </Form.Item>

                <Form.Item
                  label="appId"
                  name="appId"
                  rules={[
                    {
                      required: true,
                      message: "请输入appId",
                    },
                  ]}
                >
                  <Input placeholder="请输入appId" />
                </Form.Item>

                <Form.Item
                  label="aesKey"
                  name="aesKey"
                  rules={[
                    {
                      required: true,
                      message: "请输入aesKey",
                    },
                  ]}
                >
                  <Input placeholder="请输入aesKey" />
                </Form.Item>

                <Form.Item
                  label="token"
                  name="token"
                  rules={[
                    {
                      required: true,
                      message: "请输入token",
                    },
                  ]}
                >
                  <Input placeholder="请输入aesKey" />
                </Form.Item>

                <Form.Item
                  label="产品类型"
                  name="productType"
                  rules={[
                    {
                      required: true,
                      message: "请选择产品类型",
                    },
                  ]}
                >
                  <Select
                    onChange={productTypeChange}
                    style={{
                      width: "100%",
                    }}
                    placeholder="请选择产品类型"
                    options={productConsumedTypeList}
                  />
                </Form.Item>

                {knowledgeState ? (
                  <Form.Item
                    label="知识库"
                    name="knowledgeId"
                    rules={[
                      {
                        required: true,
                        message: "请选择知识库",
                      },
                    ]}
                  >
                    <Select
                      style={{
                        width: "100%",
                      }}
                      placeholder="请选择知识库"
                      options={knowledgeList}
                    />
                  </Form.Item>
                ) : (
                  ""
                )}

                <Form.Item
                  label="状态"
                  name="state"
                  rules={[
                    {
                      required: true,
                      message: "请选择机器人状态",
                    },
                  ]}
                >
                  <Select
                    style={{
                      width: "100%",
                    }}
                    placeholder="请选择状态"
                    options={[
                      {
                        value: 1,
                        label: "上线",
                      },
                      {
                        value: 0,
                        label: "下线",
                      },
                    ]}
                  />
                </Form.Item>

                <Form.Item label="机器人描述" name="botDesc">
                  <TextArea
                    rows={4}
                    placeholder="请输入机器人描述	"
                    maxLength={50}
                  />
                </Form.Item>
              </Form>
            </div>

            <div className="custom_dialog-bottom">
              <CustomBtn
                width={100}
                height={35}
                onClick={() => submit()}
                fontSize={14}
              >
                确定
              </CustomBtn>
            </div>
          </div>
        </div>
      ) : (
        ""
      )}
    </>
  );
});

export default JoinGroup;
