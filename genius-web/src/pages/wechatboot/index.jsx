/*
 * @Description:
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2024-03-29 20:42:18
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:25:49
 */
import React, { useState, useRef, useEffect } from "react";
import {
  PlusCircleOutlined,
  ExclamationCircleOutlined,
  EllipsisOutlined,
  CheckCircleFilled,
  QuestionCircleOutlined,
} from "@ant-design/icons";
import {
  Col,
  Row,
  Dropdown,
  Menu,
  Space,
  Modal,
  Pagination,
  Tooltip,
} from "antd";
import robotIcon from "@/assets/wxBoot/robot.svg";
import CustomInput from "@/components/CustomInput";
import AddOrEdit from "./AddOrEdit/index";
import {
  getBotDataApi,
  botOnlineChangeApi,
  deleteBotItemApi,
} from "@/api/wechatbot";
import { messageFn, isMobileDevice } from "@/utils";
import styles from "./index.module.less";
const Wechatboot = () => {
  const [filterData, setFilterData] = useState({
    size: 20,
    current: 1,
    total: 0,
    botName: "",
  });

  const [botList, setBotList] = useState([]);
  const addOrEditRef = useRef(null);

  // 新增机器人
  const addWechatboot = () => {
    addOrEditRef.current.getPage();
  };

  // 编辑机器人
  const editBotData = (data) => {
    addOrEditRef.current.getPage(data);
  };

  //   获取数据
  const getData = async () => {
    try {
      let res = await getBotDataApi(filterData);
      if (res.code === 200) {
        let resData = res?.result;
        setBotList(resData?.records || []);

        setFilterData((oldData) => {
          return {
            ...oldData,
            total: resData.total,
          };
        });
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 删除
  const deletBot = async (data) => {
    try {
      let res = await deleteBotItemApi(data.id);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "操作成功",
        });
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

  // 展示客服
  const showCustomerService = (data) => {
    window.open(data.h5Url);
  };

  // 上下限
  const botLineStateChange = async (data, state) => {
    try {
      let sendData = {
        id: data.id,
        state,
      };
      let res = await botOnlineChangeApi(sendData);
      if (res.code === 200) {
        messageFn({
          type: "error",
          content: "操作成功",
        });

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

  // 分页修改
  const onPageChange = (page, pageSize) => {
    setFilterData((oldData) => {
      return {
        ...oldData,
        size: pageSize,
        current: page,
      };
    });
  };

  useEffect(() => {
    getData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filterData.size, filterData.current, filterData.botName]);

  return (
    <div className={styles.wxServiceWarp}>
      <div className="gpts_header">
        <div className="gpts_header-title">对话即服务</div>
        <div className="gpts_header-subtitle">5分钟搭建,专属只能对话机器人</div>

        <CustomInput
          style={{
            height: "40px",
            marginBottom: "25px",
          }}
          showSearch
          onChange={(value) =>
            setFilterData((oldData) => {
              return {
                ...oldData,
                botName: value,
              };
            })
          }
          placeholder="搜索你想要的内容"
        ></CustomInput>
      </div>
      <div className="content">
        <Row gutter={[20, 20]}>
          <Col span={isMobileDevice() ? 24 : 4}>
            <div className="wechatboot_item-add" onClick={addWechatboot}>
              <PlusCircleOutlined className="wechatboot_item-addIcon"></PlusCircleOutlined>

              <Tooltip
                placement="right"
                title={
                  <div>
                    <div style={{ fontWeight: "bold" }}>使用方法</div>
                    <div>
                      1.登录新建机器人:
                      <a href="https://chatbot.weixin.qq.com" target="_blank">
                        传送门
                      </a>
                    </div>

                    <div>2. 点击应用绑定开放API配置回调地址</div>
                    <div>3. 绑定及上线机器人</div>

                    <div>
                      4. 微信官方教程:
                      <a
                        href="https://developers.weixin.qq.com/doc/aispeech/platform/get-start.html"
                        target="_blank"
                      >
                        传送门
                      </a>
                    </div>

                    <div>
                      <a
                        href="https://www.yuque.com/apetoo/qri3fg/fvomg9tgk2cubigt/edit"
                        target="_blank"
                      >
                        注: 本站图文参考
                      </a>
                    </div>
                  </div>
                }
              >
                <QuestionCircleOutlined className="tip"></QuestionCircleOutlined>
              </Tooltip>
            </div>
          </Col>

          {botList?.map((v) => (
            <Col key={v.id} span={isMobileDevice() ? 24 : 4}>
              <div className="wechatboot_content-item">
                {v?.kefuAvatarView ? (
                  <img
                    className="wechatboot_item-img"
                    src={v.kefuAvatarView}
                    alt=""
                  />
                ) : (
                  <img className="wechatboot_item-img" src={robotIcon} alt="" />
                )}

                <div className="wechatboot_item-info">
                  <div className="wechatboot_item-info-top">{v.botName}</div>
                  <div className="wechatboot_item-info-bottom">{v.botDesc}</div>
                </div>

                {/* 上线 下线状态 */}
                {v?.state === 1 ? (
                  <CheckCircleFilled className="botState online"></CheckCircleFilled>
                ) : (
                  <CheckCircleFilled className="botState offline"></CheckCircleFilled>
                )}

                {/* 更多选项 */}
                <Dropdown
                  className="more_tools"
                  overlay={
                    <Menu>
                      <Menu.Item
                        onClick={() =>
                          Modal.confirm({
                            title: "尊敬的用户您好",
                            icon: <ExclamationCircleOutlined />,
                            content: "删除后不可恢复,是否确认删除?",
                            okText: "确认",
                            onOk() {
                              deletBot(v);
                            },
                            cancelText: "取消",
                          })
                        }
                      >
                        删除
                      </Menu.Item>

                      <Menu.Item onClick={() => editBotData(v)}>编辑</Menu.Item>

                      <Menu.Item onClick={() => showCustomerService(v)}>
                        客服
                      </Menu.Item>

                      {v?.state === 0 ? (
                        <Menu.Item onClick={() => botLineStateChange(v, 1)}>
                          上线
                        </Menu.Item>
                      ) : (
                        <Menu.Item onClick={() => botLineStateChange(v, 0)}>
                          下线
                        </Menu.Item>
                      )}
                    </Menu>
                  }
                  trigger={["click", "hover"]}
                >
                  <a onClick={(e) => e.preventDefault()}>
                    <Space>
                      <EllipsisOutlined className="fileDeleteIcon"></EllipsisOutlined>
                    </Space>
                  </a>
                </Dropdown>
              </div>
            </Col>
          ))}
        </Row>
      </div>
      <div className="bot_bottom">
        <Pagination
          onChange={onPageChange}
          size="small"
          pageSize={filterData.size}
          current={filterData.current}
          total={filterData.total}
        />
      </div>
      <AddOrEdit getData={getData} ref={addOrEditRef}></AddOrEdit>;
    </div>
  );
};

export default Wechatboot;
