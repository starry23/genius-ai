/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-07-12 09:29:03
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-30 23:25:26
 */
import React, { useRef, useState, useEffect } from "react";
import { messageFn, formatAmount } from "@/utils";
import {
  PlusCircleOutlined,
  SettingOutlined,
  ExclamationCircleOutlined,
  DeleteOutlined,
  MoreOutlined,
} from "@ant-design/icons";
import { Dropdown, Space, Menu, Modal } from "antd";
import { knowledgeProJectList, deleteProjectItemFn } from "@/api/knowledgeBase";
import { setProjectMenuItem } from "@/store/actions/home_action";
import { useDispatch, useSelector } from "react-redux";
import AddOrEdit from "./AddOrEdit";
import ProjectSetting from "./ProjectSetting";
import projectIcon from "../../../../../public/assets/knowledge/projectIcon.svg";
import { accountBalance } from "@/api/user";
import styles from "./index.module.less";
const ProjectMenu = () => {
  const dispatch = useDispatch();
  // 账户余额变动
  const accountChange = useSelector((state) => state.accountChange);
  // 账户余额
  const [account, setAccount] = useState(null);

  // 新建项目
  const addOrEditRef = useRef(null);

  // 项目设置
  const projectSettingRef = useRef(null);
  // const selectProjectMenuItem = useSelector(
  //   (state) => state.selectProjectMenuItem
  // );

  // 选中的项目
  const [activeMenu, setActiveMenu] = useState(null);

  // 项目列表
  const [projeList, setProjeList] = useState([]);

  // 选中项目
  const chooseProject = (item) => {
    setActiveMenu(item);
    dispatch(setProjectMenuItem(item));
  };

  /**
   * @description: 新建项目
   * @return {*}
   * @author: jl.g
   */
  const createProjectItem = () => {
    addOrEditRef.current.getPage();
  };

  // 项目设置
  const projectItemSetting = () => {
    projectSettingRef.current.getPage(activeMenu);
  };

  // 查询项目列表
  const getKnowledgeProJectList = async () => {
    try {
      let res = await knowledgeProJectList();
      if (res.code === 200) {
        let resData = res.result || [];
        setProjeList(resData);
        if (resData.length) {
          setActiveMenu(resData[0]);
          dispatch(setProjectMenuItem(resData[0]));
        } else {
          dispatch(setProjectMenuItem(null));
        }
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

  // 获取账户余额
  const getAccountBalance = async () => {
    try {
      let res = await accountBalance();
      if (res.code === 200) {
        setAccount(res.result);
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 删除项目
  const deleteProjectItem = async (data) => {
    try {
      let res = await deleteProjectItemFn(data.itemId);
      if (res.code === 200) {
        setAccount(res.result);
        messageFn({
          type: "success",
          content: "删除成功",
        });
        getKnowledgeProJectList();
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

  useEffect(() => {
    getKnowledgeProJectList();

    // 提问接触开始查询剩余账户
    getAccountBalance();
  }, []);

  useEffect(() => {
    setTimeout((v) => {
      getAccountBalance();
    }, 1 * 1000);
  }, [accountChange]);

  return (
    <div className={styles.projectMenuWarp}>
      <div className="create_project-btn" onClick={createProjectItem}>
        <PlusCircleOutlined></PlusCircleOutlined>
        <span className="label">新建项目</span>
      </div>

      <div className="project_menu-content">
        {projeList.map((v) => (
          <div
            title={v.itemName}
            onClick={() => chooseProject(v)}
            key={v.itemId}
            className={`project_menu-item ${
              activeMenu?.itemId === v.itemId
                ? "project_menu-item-selected"
                : ""
            }`}
          >
            <img className="omit_name" src={projectIcon} alt="" />
            <span className="project_menu-item-title">{v.itemName}</span>

            <Dropdown
              overlay={
                <Menu>
                  <Menu.Item
                    onClick={() =>
                      Modal.confirm({
                        title: "尊敬的用户您好",
                        icon: <ExclamationCircleOutlined />,
                        content: "项目删除后不可恢复,是否确认删除?",
                        okText: "确认",
                        onOk() {
                          deleteProjectItem(v);
                        },
                        cancelText: "取消",
                      })
                    }
                  >
                    <DeleteOutlined />
                    删除
                  </Menu.Item>
                </Menu>
              }
              trigger={["click"]}
            >
              <a onClick={(e) => e.preventDefault()}>
                <Space>
                  <MoreOutlined className="fileDeleteIcon"></MoreOutlined>
                </Space>
              </a>
            </Dropdown>
          </div>
        ))}
      </div>

      <div className="bottom_tools-warp">
        {/* 项目设置 */}
        {activeMenu ? (
          <>
            <div onClick={projectItemSetting} className="bottom_tools-btn">
              <SettingOutlined></SettingOutlined>
              <span className="label">项目设置</span>
            </div>
          </>
        ) : (
          ""
        )}

        {/* <div className="knowledgeBalance_box">
          <span
            className="
              knowledgeBalance"
          >
            余额:
          </span>
          <span
            className="
              knowledgeBalance"
          >
            {formatAmount(
              account?.tokenBalance >= 0 ? account?.tokenBalance : 0
            )}
          </span>
        </div> */}

        {/* <div className="knowledgeBalance_box">
          <span
            className="
              knowledgeBalance"
          >
            上传余额:
          </span>
          <span
            className="
              knowledgeBalance"
          >
            {account?.knowledgeBalance || 0}次
          </span>
        </div> */}
      </div>

      <AddOrEdit
        getKnowledgeProJectList={getKnowledgeProJectList}
        ref={addOrEditRef}
      ></AddOrEdit>

      {/*项目设置 */}
      <ProjectSetting
        projetcInfo={activeMenu}
        ref={projectSettingRef}
      ></ProjectSetting>
    </div>
  );
};

export default ProjectMenu;
