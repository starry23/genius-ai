/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-09 18:14:13
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 20:37:41
 */
import React, { useState, useEffect, useRef } from "react";
import { Dropdown, Space, Menu } from "antd";
import { DownOutlined } from "@ant-design/icons";
import {
  setSessionStorage,
  formatAmount,
  getSessionStorage,
  geneartMenu,
  getLocalStorage,
  setLocalStorage,
  getCookie,
} from "@/utils";
import { useHistory } from "react-router-dom";
import {
  accountBalance,
  getNavList,
  alertGiveCurrency,
  userConsumerLog,
  getUserInfo,
} from "@/api/user";
import { systemConfig } from "@/api/login";
import { useSelector, useDispatch } from "react-redux";
import { Modal } from "antd";

import CustomNotic from "@/components/CustomNotic";
import {
  setActiveMenuItemKey,
  accountInfoAction,
  userConsumerAction,
  userInfoAction,
  createSysConfig,
  setMenuDataListAction,
  systemThemAction,
} from "@/store/actions/home_action";
import ConsumptionRecords from "./ConsumptionRecords";
import UserDetail from "@/components/HeadComponent/components/UserDetail";
import JoinGroup from "@/components/HeadComponent/components/JoinGroup";
import currencyIcon from "../../../public/assets/imgs/currencyIcon.svg";
import userDeafultImg from "../../../public/assets/imgs/userDeafultImg.png";
import messageGifIcon from "@/assets/menu/message.gif";
import crownIcon from "@/assets/home/crownIcon.svg";
import styles from "./index.module.less";
const PromptsTemplate = () => {
  // 消费记录ref
  const consumptionRecordsRef = useRef(null);
  const history = useHistory();
  const dispatch = useDispatch();
  let activeMenuItemKey = useSelector((state) => state.activeMenuItemKey);
  const [selectMenuKey, setSelectMenuKey] = useState(Number(activeMenuItemKey));
  const [navList, setNavList] = useState([]);
  const [userInfo, setUserInfo] = useState(null);
  const userDetailRef = useRef(null);
  const joinGroupRef = useRef(null);


  // 公告状态
  const [noticeState, setNoticeState] = useState(
    window.localStorage.getItem("noticeState") === "true" ? true : false
  );
  // 导航列表
  const tabsDataListPc = useSelector((state) => state.tabsDataListPc);

  // 账户余额变动
  const accountChange = useSelector((state) => state.accountChange);

  // 系统配置
  const [sysConfig, setSysConfig] = useState({});
  // 账户余额
  const [account, setAccount] = useState(0);

  // 获取系统配置
  const systemConfigFn = async () => {
    try {
      let res = await systemConfig();
      if (res.code === 200) {
        let resData = res.result;
        setSysConfig(resData);
        dispatch(createSysConfig(resData));
        setSessionStorage("sysConfig", JSON.stringify(resData));
        let activeMenuItemKey = getSessionStorage("activeMenuItemKey");
        // 根据后台设置的菜单id,获取菜单列表
        if (resData.leftMenuIds && resData.leftMenuIds.length) {
          let generateMenuList = geneartMenu(resData.leftMenuIds);
          // 将菜单存储到store
          dispatch(setMenuDataListAction(generateMenuList));

          // 初始化菜单选中
          if (!activeMenuItemKey) {
            dispatch(setActiveMenuItemKey(resData.leftMenuIds[0]));
            setSessionStorage("activeMenuItemKey", resData.leftMenuIds[0]);
            history.replace(generateMenuList[0].path);
          } else {
            dispatch(setActiveMenuItemKey(activeMenuItemKey));
          }
        }

        // 设置网站主题
        // 如果用户自己没设置过则设置为网站默认的
        let localSysTheme = getLocalStorage("theme");
        if (!localSysTheme) {
          setLocalStorage("theme", resData.sysTheme);
          document.body.setAttribute("theme-mode", resData.sysTheme);
          // 设置全局主题
          dispatch(systemThemAction(resData.sysTheme));
        } else {
          document.body.setAttribute("theme-mode", localSysTheme);
          dispatch(systemThemAction(localSysTheme));
        }

        // 设置网站图标
        var link =
          document.querySelector("link[rel*='icon']") ||
          document.createElement("link");

        link.type = "image/x-icon";

        link.rel = "shortcut icon";

        link.href = resData?.iconUrl || "";

        document.getElementsByTagName("head")[0].appendChild(link);
        document.title = resData.webName;

        // 设置平台提示语
        // let loginType = getSessionStorage("loginType");
        // if (resData.indexPopup && !loginType) {
        //   notification.open({
        //     duration: null,
        //     icon: <InfoCircleOutlined style={{ color: "#108ee9" }} />,
        //     message: "尊敬的用户您好",
        //     description: (
        //       <div
        //         dangerouslySetInnerHTML={{
        //           __html: resData.indexPopup,
        //         }}
        //       ></div>
        //     ),
        //   });
        // }
        // notification
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 获取导航列表
  const getNavListFn = async () => {
    try {
      let res = await getNavList();
      if (res.code === 200) {
        setNavList(res.result || []);
      }
    } catch (error) {
      console.log(error);
    }
  };

  /**
   * @description: 获取用户信息
   * @return {*}
   * @author: jl.g
   */

  const getUserInfoFn = async () => {
    try {
      let res = await getUserInfo();
      if (res.code === 200) {
        let resData = res.result;
        setUserInfo(resData);
        dispatch(userInfoAction(resData));
      } else {
        dispatch(userInfoAction(null));
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 菜单点击
  const mentItemClick = ({ id, path }) => {
    // 知识库需要打开新页面
    // if (id === 5) {
    //   let origin = window.location.origin;
    //   let url = origin + "/#" + path;
    //   window.open(url);
    //   return;
    // }

    // mj需要根据登录进行区别展示
    // 登录后直接跳转文生图
    // 未登录跳转风格广场
    if (id === 2) {
      let tokenValue = getCookie("tokenValue");
      if (!tokenValue) {
        history.replace("/ai/mj/pictureSquare");
      } else {
        history.replace("/ai/mj/generate");
      }
    } else {
      history.replace(path);
    }

    if (path) {
      setSelectMenuKey(id);
      setSessionStorage("activeMenuItemKey", id);
      dispatch(setActiveMenuItemKey(id));
    }
  };

  // 获取账户余额
  const getAccountBalance = async () => {
    try {
      let res = await accountBalance();
      if (res.code === 200) {
        setAccount(res.result);
        dispatch(accountInfoAction(res.result));
        if (res.result?.tokenBalance <= 0) {
          alertGiveCurrencyFn();
        }
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 获取当前消费具体数量
  const userConsumerLogFn = async () => {
    try {
      let res = await userConsumerLog();
      if (res.code === 200) {
        let resData = res.result || null;
        dispatch(userConsumerAction(resData));
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 账户为0的时候如果有送金币活动，弹窗送金币
  const alertGiveCurrencyFn = async () => {
    try {
      let res = await alertGiveCurrency();
      if (res.code === 200) {
        let tokenNums = res.result || 0;
        Modal.success({
          title: "尊敬的用户您好~",
          content: `为感谢您使用本平台，今日免费赠送您 ${tokenNums},已发放到您的账户，请注意查收`,
        });
        setTimeout(() => {
          getAccountBalance();
        }, 1000);
      }
    } catch (error) {
      console.log(error);
    }
  };

  /**
   * @description: 购买vip
   * @return {*}
   * @author: jl.g
   */
  const buyVip = () => {
    history.replace("/ai/commodity");
  };

  // 自定义弹窗展示
  const customDialog = (data) => {
    if (data.buttonType === 10) {
      joinGroupRef.current.getPage(data);
    } else {
      window.open(data.jumpUrl);
    }
  };

  // 用户详情
  const getUserDetail = () => {
    // userDetailRef.current.getPage();
    history.replace("/ai/user");
  };

  // 展示站点公告
  const showWebMessage = () => {
    setNoticeState(true);
  };

  // 跳转账户明细
  const showUserAccount = () => {
    history.replace("/ai/user?type=account");
  };

  const onHandlerClose = () => {
    window.localStorage.setItem("noticeState", false);
    setNoticeState(false);
  };

  useEffect(() => {
    getUserInfoFn();
    systemConfigFn();
    getAccountBalance();
    getNavListFn();

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (accountChange) {
      setTimeout((v) => {
        async function asyncFn() {
          await getAccountBalance();
          // 查询本次具体消耗额度
          await userConsumerLogFn();
        }

        asyncFn();
      }, 1 * 1000);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [accountChange]);

  useEffect(() => {
    if (activeMenuItemKey) {
      setSelectMenuKey(Number(activeMenuItemKey));
    }
  }, [activeMenuItemKey]);
  return (
    <div className={styles.promptsTemplateWarp}>
      <div className="logoBox">
        {sysConfig?.iconUrl ? (
          <img className="logo" src={sysConfig?.iconUrl} alt="" />
        ) : (
          ""
        )}

        <p className="title">{sysConfig?.webName}</p>
      </div>

      <div className="prompts_template-box">
        {tabsDataListPc.map((v) => (
          <div
            key={v.id}
            onClick={() => mentItemClick(v)}
            className={`prompts-menu_item`}
          >
            {selectMenuKey === Number(v.id) ? (
              <img className="menu-item-icon" src={v.iconAc} alt="" />
            ) : (
              <>
                <img
                  className="menu-item-icon menuLightDisplay"
                  src={v.icon}
                  alt=""
                />
                <img
                  className="menu-item-icon menuDarkDisplay"
                  src={v.iconDark}
                  alt=""
                />
              </>
            )}

            <div className="menu-item-label">{v.label}</div>
          </div>
        ))}
      </div>

      <div className="bottomWarp">
        <div className="nav_bar-list">
          <div onClick={buyVip} className="propose">
            购买会员
          </div>
          {/* 超过4个展示更多福利做成下拉 */}
          {navList.length >= 4 ? (
            <Dropdown
              className="propose"
              overlay={
                <Menu>
                  {navList.map((v) => (
                    <Menu.Item key={v.id} onClick={() => customDialog(v)}>
                      {v.buttonName}
                    </Menu.Item>
                  ))}
                </Menu>
              }
              trigger={["click"]}
            >
              <Space>
                <a
                  className="ant-dropdown-link"
                  onClick={(e) => e.preventDefault()}
                >
                  更多福利
                  <DownOutlined />
                </a>
              </Space>
            </Dropdown>
          ) : (
            navList.map((v) => (
              <div
                key={v.id}
                onClick={() => customDialog(v)}
                className="propose"
              >
                {v.buttonName}
              </div>
            ))
          )}
        </div>
        <img
          onClick={showWebMessage}
          className="messageGifIcon"
          src={messageGifIcon}
          alt=""
        />

        <div className="account_box" onClick={showUserAccount}>
          <div className="label">
            <img className="currencyIcon" src={currencyIcon} alt="" />
          </div>
          <div className="account_num">
            {formatAmount(
              account?.tokenBalance >= 0 ? account.tokenBalance : 0
            )}
          </div>
        </div>

        {/* 用户头像 */}
        {userInfo?.memberFlag ? (
          <div className="user_avatar-box">
            <img
              onClick={getUserDetail}
              className="user_avatar"
              src={userInfo?.headImgUrl || userDeafultImg}
              alt=""
            />

            <img className="crownIcon" src={crownIcon} alt="" />
          </div>
        ) : (
          <img
            onClick={getUserDetail}
            className="user_avatar-noVip"
            src={userInfo?.headImgUrl || userDeafultImg}
            alt=""
          />
        )}

        <div className="webVersion">版本号:v:{sysConfig?.webVersion}</div>

        {/* 站点公告 */}
        <CustomNotic
          visible={noticeState}
          handlerClose={() => onHandlerClose()}
        ></CustomNotic>
      </div>

      {/* 通用弹窗 */}
      <JoinGroup ref={joinGroupRef} />
      {/* 用户信息 */}
      <UserDetail ref={userDetailRef}></UserDetail>
      {/* 消费记录 */}
      <ConsumptionRecords ref={consumptionRecordsRef}></ConsumptionRecords>
    </div>
  );
};

export default PromptsTemplate;
