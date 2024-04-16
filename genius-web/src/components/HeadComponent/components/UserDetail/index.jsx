import React, {
  useState,
  forwardRef,
  useImperativeHandle,
  useRef,
} from "react";

import { useHistory } from "react-router-dom";
import { useDispatch } from "react-redux";
import styles from "./index.module.less";
import {
  CloseOutlined,
  AliwangwangOutlined,
  ExclamationCircleOutlined,
} from "@ant-design/icons";
import { Button, Tag, Tooltip } from "antd";
import { getMemberUserInfo, getNavList } from "@/api/user";
import CardRedeemDialog from "./cardRedeem";
import InviteGiftUrlInfo from "@/components/InviteGiftUrlInfo";
import { logout } from "@/api/login";
import {
  removeCookie,
  messageFn,
  getSessionStorage,
  clearSessionStorage,
  formatAmount,
} from "@/utils";

import { userInfoAction } from "@/store/actions/home_action";
import { accountBalance, getUserInfo } from "@/api/user";

import ChangePwd from "../ChangePwd/index";
import userDeafultImg from "../../../../../public/assets/imgs/userDeafultImg.png";

import Propose from "../Propose";
import SwitchTheme from "@/components/SwitchTheme";
import JoinGroup from "@/components/HeadComponent/components/JoinGroup";
import { makeCashbackInfoDom } from "./utils";
import ConsumptionRecords from "@/components/PromptsTemplate/ConsumptionRecords";

const UserDetail = forwardRef((props, ref) => {
  const dispatch = useDispatch();
  const [pageState, setPageState] = useState(false);
  const inviteGiftUrlInfoRef = useRef(null);
  const changePwdRef = useRef(null);
  const history = useHistory();
  const [userInfo, setUserInfo] = useState(null);
  const [memberUserInfo, setMemberUserInfo] = useState(null);
  const proposeRef = useRef(null);
  const cardRedeemRef = useRef(null);
  const consumptionRecordsRef = useRef(null);
  // 导航列表
  const [navList, setNavList] = useState([]);
  // 加入我们弹窗
  const joinGroupRef = useRef(null);
  const [account, setAccount] = useState(0);
  const [cashbackInfo, setCashbackInfo] = useState("");
  // 获取登录类型
  const loginType = useRef(getSessionStorage("loginType"));

  useImperativeHandle(ref, () => {
    return {
      getPage,
    };
  });

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
   * @description: 弹框展示
   * @return {*}
   * @author: jl.g
   */
  const getPage = () => {
    setPageState(true);
    getUserInfoFn();
    getMemberUserInfoFn();
    getAccountBalance();
    // 获取导航信息
    getNavListFn();
    console.log(userInfo, "userInfouserInfo");
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

  /**
   * @description: 弹框隐藏
   * @return {*}
   * @author: jl.g
   */

  const hidePage = () => {
    setPageState(false);
  };

  /**
   * @description: 邀请有礼
   * @return {*}
   * @author: jl.g
   */

  const inviteUser = () => {
    inviteGiftUrlInfoRef.current.getPage();
    hidePage();
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
   * @description: 修改密码
   * @return {*}
   * @author: jl.g
   */

  const changePassword = () => {
    hidePage();
    let loginType = getSessionStorage("loginType");
    changePwdRef.current.getPage(1, loginType);
  };

  /**
   * @description: 反馈与建议
   * @return {*}
   * @author: jl.g
   */
  const proposeFn = () => {
    proposeRef.current.getPage();
  };

  // 自定义弹窗
  const customDialog = (data) => {
    if (data.buttonType === 10) {
      joinGroupRef.current.getPage(data);
    } else {
      window.open(data.jumpUrl);
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

  // 卡密兑换
  const cardRedeemFn = () => {
    cardRedeemRef.current.getPage();
  };

  // 消费记录
  const consumptionRecordsFn = ()=>{
    consumptionRecordsRef.current.getPage();
  }

  return (
    <>
      {pageState ? (
        <div className={styles.user_detail_mask}>
          <div className="user_detail-warp animate__animated animate__fadeInDown">
            <div className="user_detail-head">
              <div className="head_tools">
                <span className="title">个人信息</span>
                <SwitchTheme></SwitchTheme>
              </div>

              <CloseOutlined
                onClick={hidePage}
                className="closeBtn"
                twoToneColor="#fff"
              ></CloseOutlined>
            </div>
            <div className="user_detail-content">
              <img
                className="user_avatar"
                src={userInfo?.headImgUrl || userDeafultImg}
                alt=""
              />
              <p className="detail_info">{userInfo?.nikeName}</p>

              <div className="invite_btn">
                <Button size="small" onClick={inviteUser} type="primary">
                  邀请有礼
                </Button>

                <Button
                  style={{ marginLeft: 10 }}
                  onClick={proposeFn}
                  type="primary"
                  size="small"
                >
                  问题反馈
                </Button>

                <Button
                  style={{ marginLeft: 10 }}
                  onClick={cardRedeemFn}
                  type="primary"
                  size="small"
                >
                  卡密兑换
                </Button>

                <Button
                  style={{ marginLeft: 10 }}
                  onClick={consumptionRecordsFn}
                  type="primary"
                  size="small"
                >
                  消费记录
                </Button>
              </div>

              <div className="navBox">
                {navList.map((v) => (
                  <div
                    key={v.id}
                    onClick={() => customDialog(v)}
                    className="propose"
                  >
                    <Tag icon={<AliwangwangOutlined />} color="#2196f3">
                      {v.buttonName}
                    </Tag>
                  </div>
                ))}
              </div>

              <div className="accent_info">
                <ul className="accent_info_item">
                  <li>会员剩余时间</li>
                  <li>余额</li>
                  <li>注册时间</li>
                </ul>
                <ul className="accent_info_content">
                  <li>{memberUserInfo?.deadline || 0}天</li>
                  <li>{formatAmount(account?.tokenBalance) || 0}</li>
                  <li style={{ fontSize: "12px" }}>{userInfo?.createTime}</li>
                </ul>
              </div>

              <div className="accent_info">
                <ul className="accent_info_item">
                  <li>
                    我的等级
                    {
                      <Tooltip placement="top" title={makeCashbackInfoDom()}>
                        <ExclamationCircleOutlined />
                      </Tooltip>
                    }
                  </li>
                  <li>邀新人数</li>
                  <li>累计佣金</li>
                </ul>
                <ul className="accent_info_content">
                  <li>{userInfo?.rebateLevel || 1}</li>
                  <li>{userInfo?.inviteCount || 0}人</li>
                  <li>{account.rmbBalance || 0}元</li>
                </ul>
              </div>

              {userInfo?.memberFlag ? (
                <div className="vip_content">
                  <div className="title">
     
                    会员权益
                  </div>
                  <div className="content">
                    {memberUserInfo?.rightConfigVoList &&
                      memberUserInfo?.rightConfigVoList.map((v, index) => (
                        <div className="content_item" key={index}>
                          {index + 1 + "." + v.rightsDesc}
                        </div>
                      ))}
                  </div>
                </div>
              ) : (
                ""
              )}

              <div className="user_detail_bottom">
                <span onClick={backLogin} className="tolls_btn">
                  {userInfo ? "退出登录" : "去登录"}
                </span>

                {userInfo && loginType.current !== "3" ? (
                  <span onClick={changePassword} className="tolls_btn">
                    修改密码
                  </span>
                ) : (
                  ""
                )}
              </div>
            </div>
          </div>
        </div>
      ) : (
        ""
      )}

      {/* 邀请有礼 */}
      <InviteGiftUrlInfo ref={inviteGiftUrlInfoRef}></InviteGiftUrlInfo>

      {/* 修改密码 */}
      <ChangePwd ref={changePwdRef}></ChangePwd>

      {/* 反馈与建议 */}
      <Propose ref={proposeRef}></Propose>

      {/* 加入我们 */}
      <JoinGroup ref={joinGroupRef} />

      {/* 兑换卡密 */}
      <CardRedeemDialog hidePage={hidePage} ref={cardRedeemRef} />

      {/* 消费记录 */}
      <ConsumptionRecords ref={consumptionRecordsRef}></ConsumptionRecords>
    </>
  );
});

export default UserDetail;
