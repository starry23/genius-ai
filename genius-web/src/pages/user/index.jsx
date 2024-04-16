import React, { useState, useEffect } from "react";
import { userInfoAction } from "@/store/actions/home_action";
import { useDispatch } from "react-redux";
import { useHistory } from "react-router-dom";
import InviteGiftUrlInfo from "./InviteGiftUrlInfo";
import {
  formatAmount,
  messageFn,
  removeCookie,
  clearSessionStorage,
} from "@/utils";
import { logout } from "@/api/login";
import SwitchTheme from "@/components/SwitchTheme";
import { accountBalance, getUserInfo, getMemberUserInfo } from "@/api/user";
import userDeafultImg from "../../../public/assets/imgs/userDeafultImg.png";
import CustomBtn from "@/components/CustomBtn";
import Propose from "./Propose";
import Equity from "./Equity";
import ConsumptionLog from "./ConsumptionLog";
import ChangePwd from "./ChangePwd";
import CustomInput from "@/components/CustomInput";
import Exchange from "./Exchange";
import styles from "./index.module.less";
const User = (props) => {
  const [userInfo, setUserInfo] = useState(null);
  const [memberUserInfo, setMemberUserInfo] = useState(null);
  const [account, setAccount] = useState(0);
  const history = useHistory();
  const dispatch = useDispatch();
  // 导航菜单
  const [tabsList, setTabsList] = useState([
    { id: 0, label: "总览" },
    { id: 1, label: "权益" },
    { id: 3, label: "记录" },
    { id: 2, label: "密码" },
    { id: 4, label: "邀新" },
    { id: 6, label: "兑换" },
    { id: 5, label: "反馈" },
  ]);
  const [activeTab, setActiveTab] = useState(
    props.location?.search ? { id: 3, label: "记录" } : { id: 0, label: "总览" }
  );
  //   页签点击
  const tabClick = (item) => {
    setActiveTab(item);
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

        if (resData?.registerType !== 1 && resData?.registerType !== 2) {
          setTabsList((data) => {
            let newTabs = data.filter((v) => v.id !== 2);
            return newTabs;
          });
        }
      } else {
        dispatch(userInfoAction(null));
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

  /**
   * @description: 获取用户信息
   * @return {*}
   * @author: jl.g
   */

  const getMemberUserInfoFn = async () => {
    try {
      let res = await getMemberUserInfo();
      if (res.code === 200) {
        setMemberUserInfo(res.result);
      } else if (res.code === 500) {
        setMemberUserInfo({
          deadline: 0,
          surplusCount: 0,
        });
      }
    } catch (error) {
      console.log(error);
    }
  };

  /**
   * @description:退出登录
   * @return {*}
   * @author: jl.g
   */
  const backLogin = async () => {
    try {
      let res = await logout();
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "退出登录成功",
        });
        history.replace("/login");
        removeCookie("satoken");
        removeCookie("tokenName");
        removeCookie("tokenValue");
        clearSessionStorage();
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
    getUserInfoFn();
    getMemberUserInfoFn();
    getAccountBalance();
  }, []);
  return (
    <div className={styles.userWarp}>
      <div className="title">个人中心</div>
      <div className="head_tabs">
        <div className="mj-nav_box">
          {tabsList.map((v) => (
            <div
              onClick={() => tabClick(v)}
              key={v.id}
              className={`mj-nav_item ${
                activeTab.id === v.id ? "mj-nav_selected" : ""
              }`}
            >
              {v.label}
            </div>
          ))}
        </div>
      </div>

      {/* 总览 */}
      {activeTab.id === 0 ? (
        <div className="content">
          <div className="content_item">
            <img
              className="user_avatar"
              src={userInfo?.headImgUrl || userDeafultImg}
              alt=""
            />
          </div>

          <div className="switchTheme">
            <SwitchTheme></SwitchTheme>
          </div>
          <div className="content_item">
            <div className="label">昵称</div>

            <CustomInput
              value={userInfo?.nikeName || ""}
              disabled
              style={{ height: "40px", marginTop: "10px" }}
            ></CustomInput>
          </div>

          <div className="content_item">
            <div className="label">会员剩余时间(天)</div>

            <CustomInput
              value={memberUserInfo?.deadline || 0}
              disabled
              style={{ height: "40px", marginTop: "10px" }}
            ></CustomInput>
          </div>

          <div className="content_item">
            <div className="label">余额</div>
            <CustomInput
              value={formatAmount(account?.tokenBalance) || 0}
              disabled
              style={{ height: "40px", marginTop: "10px" }}
            ></CustomInput>
          </div>

          <div className="content_item">
            <div className="label">注册时间</div>
            <CustomInput
              value={userInfo?.createTime || ""}
              disabled
              style={{ height: "40px", marginTop: "10px" }}
            ></CustomInput>
          </div>

          {/* 退出 */}
          <CustomBtn onClick={backLogin} width={100} height={35}>
            {userInfo ? "退出登录" : "去登录"}
          </CustomBtn>
        </div>
      ) : (
        ""
      )}

      {/* 邀新 */}
      {activeTab.id === 4 ? (
        <div className="content">
          <InviteGiftUrlInfo></InviteGiftUrlInfo>
        </div>
      ) : (
        ""
      )}

      {/* 反馈 */}
      {activeTab.id === 5 ? (
        <div className="content">
          {activeTab.id === 5 ? <Propose></Propose> : ""}
        </div>
      ) : (
        ""
      )}

      {/* 权益 */}
      {activeTab.id === 1 ? (
        <div className="content">
          <Equity memberUserInfo={memberUserInfo}></Equity>
        </div>
      ) : (
        ""
      )}

      {/* 消费记录 */}
      {activeTab.id === 3 ? (
        <div className="content">
          <ConsumptionLog></ConsumptionLog>
        </div>
      ) : (
        ""
      )}

      {/* 修改密码 */}
      {activeTab.id === 2 ? (
        <div className="content">
          <ChangePwd userInfo={userInfo}></ChangePwd>
        </div>
      ) : (
        ""
      )}

      {/* 兑换 */}
      {activeTab.id === 6 ? (
        <div className="content">
          <Exchange></Exchange>
        </div>
      ) : (
        ""
      )}
    </div>
  );
};

export default User;
