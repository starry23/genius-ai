/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-06-28 21:44:52
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-10 14:10:54
 */
import React, { useState, useEffect, useRef } from "react";
import { Popconfirm, Radio } from "antd";
import { useHistory } from "react-router-dom";
import _ from "lodash";
import { getRoleType, getAiRoleList } from "@/api/assistantsList";
import { productConsumedType } from "@/api/gpt";
import { messageFn } from "@/utils";
import WebsiteRegistNum from "@/components/WebsiteRegistNum";
import hot from "../../../public/assets/imgs/hotRole.png";
import tool from "../../../public/assets/imgs/tool.png";
import create from "../../../public/assets/imgs/create.png";
import application from "../../../public/assets/imgs/application.png";
import code from "../../../public/assets/imgs/code.png";
import education from "../../../public/assets/imgs/education.png";
import life from "../../../public/assets/imgs/life.png";
import other from "../../../public/assets/imgs/other.png";
import CustomInput from "@/components/CustomInput";
import { useSelector } from "react-redux";
import "./index.css";
import styles from "./index.module.less";
const AiAssistant = () => {
  const history = useHistory();
  const sysConfig = useSelector((state) => state.sysConfig);

  // 获取模型类型
  const [productConsumedTypeList, setProductConsumedTypeList] = useState([]);

  // 选择的模型
  const [productConsumedTypeitem, setProductConsumedTypeitem] = useState(null);

  // 搜索框
  const [searchData, setSearchData] = useState("");
  // 助手分类
  const [assistantTypeList, setAssistantTypeList] = useState([]);

  // 选中的助手
  const [activeRoleType, setActiveRoleType] = useState(null);
  // 角色分类前端字典
  const navRoleTypeList = useRef([
    {
      key: 1,
      icon: hot,
    },
    {
      key: 2,
      icon: tool,
    },
    {
      key: 3,
      icon: create,
    },
    {
      key: 4,
      icon: application,
    },
    {
      key: 5,
      icon: code,
    },
    {
      key: 6,
      icon: education,
    },
    {
      key: 7,
      icon: life,
    },
    {
      key: 8,
      icon: other,
    },
  ]);

  // 角色列表
  const [roleList, setRoleList] = useState([]);
  const roleListRef = useRef([]);

  // 获取模型类型
  const getProductConsumedTypeList = async () => {
    try {
      let res = await productConsumedType();
      if (res.code === 200) {
        let resData = res.result;
        // 过略掉绘画和知识库的类型

        // 过滤掉3.5知识库问答5 和 知识库上传 6   dalle 7   gpts 9

        //   [
        //     {
        //         "key": 1,
        //         "value": "AI3.5"
        //     },
        //     {
        //         "key": 3,
        //         "value": "mj绘画"
        //     },
        //     {
        //         "key": 2,
        //         "value": "AI4.0"
        //     },
        //     {
        //         "key": 4,
        //         "value": "文心千帆"
        //     },
        //     {
        //         "key": 5,
        //         "value": "3.5知识库问答"
        //     },
        //     {
        //         "key": 6,
        //         "value": "知识库上传"
        //     },
        //     {
        //         "key": 7,
        //         "value": "星火"
        //     },
        //     {
        //         "key": 8,
        //         "value": "DALL_E"
        //     },
        //     {
        //         "key": 9,
        //         "value": "GPTS"
        //     },
        //     {
        //         "key": 10,
        //         "value": "mj快速"
        //     }
        // ]
        resData = resData.filter(
          (v) => v.key === 1 || v.key === 2 || v.key === 4 || v.key === 7
        );
        setProductConsumedTypeList(resData || []);
        if (resData.length) {
          setProductConsumedTypeitem(resData[0]);
        }
      } else {
        setProductConsumedTypeList([]);
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 获取角色类型
  const getRoleTypeFn = async () => {
    try {
      let res = await getRoleType();
      if (res.code === 200) {
        let resData = res.result || [];
        resData.forEach((item) => {
          item.icon = navRoleTypeList.current.find(
            (v) => v.key === item.key
          ).icon;
        });
        setAssistantTypeList(resData);
        setActiveRoleType(resData[0]);
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 获取角色列表
  const getAiRoleListFn = async (isSearch = false) => {
    try {
      let params = {
        roleType: activeRoleType.key,
        roleName: searchData,
      };

      if (isSearch && searchData) {
        delete params.roleType;
      }
      let res = await getAiRoleList(params);
      if (res.code === 200) {
        setRoleList(res.result || []);
        roleListRef.current = res.result || [];
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

  // 搜索
  const hanlderSearchRole = () => {
    getAiRoleListFn(true);
  };

  // 切换角色
  const navItemClick = (data) => {
    setActiveRoleType(data);
  };

  // 点击角色创建会话
  const handlerRoleClick = (data) => {
    history.replace({
      pathname: "/ai/home",
      state: {
        roleData: data,
        productConsumedTypeitem,
      },
    });
  };

  useEffect(() => {
    getRoleTypeFn();
    getProductConsumedTypeList();
  }, []);

  useEffect(() => {
    if (activeRoleType) {
      getAiRoleListFn();
    }
    //eslint-disable-next-line
  }, [activeRoleType]);
  return (
    <div className={styles.aiAssistantWarp}>
      <div className="ai_assistant-warp-head">
        <div className="ai_assistant-head-box">
          <img className="iconUrl" src={sysConfig?.iconUrl} alt="" />
          <div className="ai_assistant-head-info">
            <div className="ai_assistant-head-sysTitle">
              {sysConfig?.webName}
            </div>
            <div className="ai_assistant-head-subTitle">
              你想要的这里全部都有
            </div>
          </div>
        </div>

        <div className="ai_assistant-tools-searchBox">
          <CustomInput
            value={searchData}
            style={{
              height: "40px",
            }}
            showSearch
            onChange={(value) => {
              setSearchData(value);
            }}
            search={hanlderSearchRole}
            placeholder="搜索你想要的内容"
          ></CustomInput>
        </div>
      </div>

      <div className="ai_assistant-warp-content">
        <div className="ai_assistant-content-nav">
          {assistantTypeList.map((v) => (
            <div
              onClick={() => navItemClick(v)}
              className={`content_nav-item ${
                activeRoleType?.key === v.key ? "active_nav-item" : ""
              }`}
              key={v.key}
            >
              <img className="nav_item-icon" src={v.icon} alt="" />
              <span className="nav_item-label">{v.value}</span>
            </div>
          ))}
        </div>

        <div className="ai_assistant-content-warp">
          {roleList && roleList.length
            ? roleList.map((v) => (
                <Popconfirm
                  key={v.id}
                  placement="top"
                  title={
                    <div>
                      <p>模型选择</p>
                      <div>
                        <Radio.Group
                          onChange={(e) => {
                            let value = e.target.value;
                            let activeItem = productConsumedTypeList.find(
                              (v) => v.key === value
                            );
                            setProductConsumedTypeitem(activeItem);
                          }}
                          value={productConsumedTypeitem?.key}
                        >
                          {productConsumedTypeList.map((v) => (
                            <Radio key={v.key} value={v.key}>
                              {v.value}
                            </Radio>
                          ))}
                        </Radio.Group>
                      </div>
                    </div>
                  }
                  onConfirm={() => handlerRoleClick(v)}
                  okText="确认"
                  cancelText="取消"
                >
                  <div className="ai_assistant-warp-item" key={v.id}>
                    {v.imageUrl ? (
                      <img
                        className="ai_assistant-warp-item-img"
                        src={v.imageUrl}
                        alt=""
                      />
                    ) : (
                      ""
                    )}

                    <div className="ai_assistant-warp-item-info">
                      <span className="ai_assistant-info-label">
                        {v.roleName}
                      </span>
                      <span className="ai_assistant-info-desc">
                        {v.roleDesc}
                      </span>
                    </div>
                  </div>
                </Popconfirm>
              ))
            : ""}
        </div>
      </div>
      <div className="ai_websiteRegistNum">
        <WebsiteRegistNum></WebsiteRegistNum>
      </div>
    </div>
  );
};

export default AiAssistant;
