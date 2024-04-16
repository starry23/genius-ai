/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-03-28 12:44:28
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:47:01
 */
import React, { useState, useRef, useEffect } from "react";
import { EyeInvisibleOutlined, EyeTwoTone } from "@ant-design/icons";
import { useDispatch } from "react-redux";
import { setMenuDataListAction } from "@/store/actions/home_action";
import styles from "./index.module.less";
import lightBg from "../../../public/assets/login/lightBg.png";
import mobileBg from "../../../public/assets/login/mobile.png";
import shadowIcon from "../../../public/assets/login/shadow.png";
import { Input, Space } from "antd";
import {
  authorizationUrl,
  checkToken,
  userLogin,
  systemConfig,
  wxLoginState,
  getAuthUrlApi,
} from "@/api/login";
import QRCode from "qrcode.react";
import RegisterCopm from "./components/RegisterCopm";
import {
  setCookie,
  getCookie,
  setSessionStorage,
  geneartMenu,
  messageFn,
  isMobileDevice,
} from "@/utils";
import _ from "lodash";
import "./index.css";
import SharkBtn from "@/components/SharkBtn";
import wxLogo from "../../../public/assets/imgs/wx-logo.svg";
import phoneIcon from "../../../public/assets/imgs/phone.svg";
import UserAgreement from "./components/UserAgreement";
import PrivacyStatement from "./components/PrivacyStatement";
import UserRegister from "./components/UserRegister";
import ChangePwd from "@/components/HeadComponent/components/ChangePwd";
import StarsBtn from "@/components/StarsBtn";
const Login = ({ history }) => {
  const videoRef = useRef(null);
  const dispatch = useDispatch();
  const [resultUrl, setResult] = useState("");
  // 手机号
  const [phoneNumber, setPhoneNumber] = useState("");
  // 邮箱账号
  const [mailNumber, setMailNumber] = useState("");
  const [password, setPassword] = useState("");
  // 手机号
  const [phonePassword, setPhonePassword] = useState("");
  // 用户协议 与 隐私声明
  const userAgreementRef = useRef(null);
  const privacyStatementRef = useRef(null);

  // 用户注册
  const userRegisterRef = useRef(null);

  // 忘记密码
  const changePwdRef = useRef(null);

  // 系统配置
  const [sysConfig, setSysConfig] = useState({});
  const sysConfigRef = useRef(null);
  const wxLoginTimer = useRef(null);
  const wxLoginSceneStr = useRef(null);
  const [wxLoginQcodeTimeout, setwxLoginQcodeTimeout] = useState(false);
  // 登录类型  wx or phone
  // 1 邮箱 2手机号 3微信
  const [loginType, setLoginType] = useState(null);

  // 注册状态
  const [registerState, setRegisterState] = useState({
    display: false,
    forgetPasState: false,
  });

  // 获取系统配置
  const systemConfigFn = async () => {
    try {
      let res = await systemConfig();
      if (res.code === 200) {
        let resData = res.result;
        setSysConfig(resData);
        sysConfigRef.current = resData;
        setSessionStorage("sysConfig", JSON.stringify(resData));
        // 设置默认登录设置
        if (resData.loginTypes.length) {
          setLoginType(resData.loginTypes[0]);
          setSessionStorage("loginType", resData.loginTypes[0]);
        }

        // 根据后台设置的菜单id,获取菜单列表
        if (resData.leftMenuIds && resData.leftMenuIds.length) {
          let generateMenuList = geneartMenu(resData.leftMenuIds);
          // 将菜单存储到store
          dispatch(setMenuDataListAction(generateMenuList));
        }

        // 初始化菜单选中
        setSessionStorage("activeMenuItemKey", resData.leftMenuIds[0]);

        var link =
          document.querySelector("link[rel*='icon']") ||
          document.createElement("link");

        link.type = "image/x-icon";

        link.rel = "shortcut icon";

        link.href = resData?.iconUrl || "";

        document.getElementsByTagName("head")[0].appendChild(link);
        document.title = resData?.webName || "";
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 获取微信扫码登录
  const authorizationUrlFn = async () => {
    try {
      setLoginType(3);
      setSessionStorage("loginType", 3);
      setwxLoginQcodeTimeout(false);
      if (wxLoginTimer.current) {
        clearInterval(wxLoginTimer.current);
      }

      let res = await authorizationUrl({});
      if (res.code === 200) {
        let resData = res.result;
        setResult(resData.url);
        wxLoginSceneStr.current = resData.sceneStr;

        wxLoginTimer.current = setInterval(() => {
          pollingCheckWxLoginState();
        }, 1000);
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 微信授权登录
  const wxAuthorizationUrlFn = async () => {
    setLoginType(4);
    setSessionStorage("loginType", 4);
    let res = await getAuthUrlApi();
    if (res.code === 200) {
      let authUrl = res?.result?.url;
      window.location.href = authUrl;
    }
  };

  // 刷新微信验证码
  const refreshWxCode = () => {
    authorizationUrlFn();
  };
  // 登录后页面跳转
  const goNextPage = () => {
    let firstMenuItem = null;

    // 根据id获取菜单
    let generateMenuList = geneartMenu(sysConfigRef.current?.leftMenuIds);

    if (generateMenuList && generateMenuList.length) {
      firstMenuItem = generateMenuList[0];
      history.replace(firstMenuItem.path);
    }
  };

  // 轮询查询微信扫码登录状态
  const pollingCheckWxLoginState = async () => {
    try {
      let params = {
        sceneStr: wxLoginSceneStr.current,
      };
      let res = await wxLoginState(params);
      if (res.code === 200) {
        setCookie("tokenName", "satoken");
        setCookie("tokenValue", res.result);

        goNextPage();
        setSessionStorage("loginType", 3);
      } else if (res.code === 4009) {
        setwxLoginQcodeTimeout(true);
        if (wxLoginTimer.current) {
          clearInterval(wxLoginTimer.current);
        }
      }
    } catch (error) {
      console.log(error);
    }
  };

  /**
   * @description: 监听页面地址发生变化
   * @return {*}
   * @author: jl.g
   */
  history.listen((path) => {
    // console.log("路径变化", path);
  });

  /**
   * @description: 用户协议
   * @param {*} e
   * @return {*}
   * @author: jl.g
   */
  const userAgreementClick = () => {
    userAgreementRef.current.getPage();
  };

  const privacyStatementClick = () => {
    privacyStatementRef.current.getPage();
  };

  /**
   * @description: 邮箱
   * @return {*}
   * @author: jl.g
   */
  const accountLogin = () => {
    setLoginType(1);
    setSessionStorage("loginType", 1);
  };

  /**
   * @description: 校验token是否过期
   * @return {*}
   * @author: jl.g
   */

  const checkTokenFn = async () => {
    try {
      let res = await checkToken();
      if (res.code === 200) {
        goNextPage();
      }
      console.log(res);
    } catch (error) {
      console.log(error);
    }
  };

  //邮箱用户注册
  const mailRegister = () => {
    // userRegisterRef.current.getPage(1);
    setRegisterState(() => {
      return {
        forgetPasState: false,
        display: true,
      };
    });
  };

  /**
   * @description: 邮箱忘记密码
   * @return {*}
   * @author: jl.g
   */
  const forgotPassword = () => {
    // 用于区分是忘记密码0 还是修改密码1
    // 1代表邮箱 2代表手机号
    // changePwdRef.current.getPage(0, 1);

    setRegisterState((data) => {
      return {
        display: true,
        forgetPasState: true,
      };
    });
  };

  /**
   * @description:
   * @return {*} 0代表 忘记密码 1 代表修改密码
   * @author: jl.g
   */
  const phoneForgotPassword = () => {
    // 用于区分是忘记密码0 还是修改密码1
    // 1代表邮箱 2代表手机号
    // changePwdRef.current.getPage(0, 2);

    setRegisterState(() => {
      return {
        display: true,
        forgetPasState: true,
      };
    });
  };

  // 手机用户注册
  const phoneNumberRegister = () => {
    // userRegisterRef.current.getPage(2);

    setRegisterState(() => {
      return {
        forgetPasState: false,
        display: true,
      };
    });
  };

  /**
   * @description: 邮箱登录
   * @return {*}
   * @author: jl.g
   */
  const mailLoginFn = async () => {
    if (!mailNumber) {
      messageFn({
        type: "error",
        content: "请输入账号",
      });
      return;
    } else if (!password) {
      messageFn({
        type: "error",
        content: "请输入密码",
      });

      return;
    }
    try {
      let data = {
        accountNum: mailNumber,
        password: password,
        type: 1,
      };
      let res = await userLogin(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "验证成功",
        });
        setSessionStorage("loginType", 1);
        setCookie("tokenName", "satoken");
        setCookie("tokenValue", res.result);

        goNextPage();

        // setSessionStorage("userInfo", res.result);
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
   * @description: 手机号登录
   * @return {*}
   * @author: jl.g
   */
  const phoneLoginFn = async () => {
    if (!phoneNumber) {
      messageFn({
        type: "error",
        content: "请输入账号",
      });
      return;
    } else if (!phonePassword) {
      messageFn({
        type: "error",
        content: "请输入密码",
      });

      return;
    }
    try {
      let data = {
        accountNum: phoneNumber,
        password: phonePassword,
        type: 2,
      };
      let res = await userLogin(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "验证成功",
        });
        setSessionStorage("loginType", 2);
        setCookie("tokenName", "satoken");
        setCookie("tokenValue", res.result);
        goNextPage();

        // setSessionStorage("userInfo", res.result);
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

  const loginTypeBtnList = useRef([
    {
      type: 1,
      component: (
        <SharkBtn
          onClick={accountLogin}
          icon={phoneIcon}
          name={"邮箱登录"}
        ></SharkBtn>
      ),
    },
    {
      type: 2,
      component: (
        <SharkBtn
          onClick={() => {
            setLoginType(2);
            setSessionStorage("loginType", 2);
          }}
          icon={phoneIcon}
          name={"手机号登录"}
        ></SharkBtn>
      ),
    },
    {
      type: 3,
      component: (
        <SharkBtn
          onClick={authorizationUrlFn}
          icon={wxLogo}
          name={"扫码登录"}
        ></SharkBtn>
      ),
    },

    {
      type: 4,
      component: (
        <SharkBtn
          onClick={wxAuthorizationUrlFn}
          icon={wxLogo}
          name={"授权登录"}
        ></SharkBtn>
      ),
    },
  ]);

  // 页面加载完毕后 获取url中的拉新code
  useEffect(() => {
    async function asyncFn() {
      // 获取系统配置
      await systemConfigFn();

      let tokenValue = getCookie("tokenValue");
      if (tokenValue) {
        checkTokenFn();
      }
    }

    asyncFn();

    if (!isMobileDevice() && videoRef.current) {
      videoRef.current.play();
    }

    // 组件销毁 消除定时器
    return () => {
      clearInterval(wxLoginTimer.current);
    };

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  return (
    <div className={styles.loginWarp}>
      {/* canvas */}

      {sysConfig?.loginViewType === 1 ? (
        <div className="loginVideoWarp">
          <video
            ref={videoRef}
            autoPlay
            muted
            loop
            src={require("../../../public/assets/video/loginVideo.mp4")}
            className="loginVideo"
          ></video>
        </div>
      ) : (
        <>
          {isMobileDevice() ? (
            <img className="loginBg" src={mobileBg} alt="" />
          ) : (
            <img className="loginBg" src={lightBg} alt="" />
          )}
        </>
      )}

      <div className="login-content">
        <div
          className={`typeLoginBox   ${
            sysConfig?.loginViewType === 1 ? "typeLoginBox2" : ""
          } ${!registerState.display ? "displayLogin" : "displayNone"}`}
        >
          {sysConfig?.iconUrl ? (
            <img className="logo" src={sysConfig?.iconUrl} alt="" />
          ) : (
            ""
          )}

          <div className="login_content_webName">{sysConfig?.webName}</div>
          <h1 className="title">{sysConfig?.subTitle}</h1>

          {/* 微信扫码登录 */}
          {loginType === 3 ? (
            <div className="wxLoginBox">
              {resultUrl ? (
                <>
                  {" "}
                  <QRCode
                    id="qrCode"
                    value={resultUrl}
                    size={200} // 二维码的大小
                    fgColor="#000000" // 二维码的颜色
                  />
                  {wxLoginQcodeTimeout ? (
                    <p className="refreshWxCode" onClick={refreshWxCode}>
                      二维码已失效，点击重新获取二维码
                    </p>
                  ) : (
                    ""
                  )}
                </>
              ) : (
                ""
              )}
            </div>
          ) : loginType === 1 ? (
            // 邮箱账号密码登录
            <div className="phoneLogin">
              <div className="phone_login-warp">
                <div className="content-box">
                  <div className="login_info-content">
                    <Space direction="vertical" className="input_space-warp">
                      <Input
                        maxLength={30}
                        value={mailNumber}
                        onChange={(e) => setMailNumber(e.target.value)}
                        onPressEnter={() => {
                          mailLoginFn();
                        }}
                        placeholder="请输入邮箱账号"
                      />
                      <Input.Password
                        onPressEnter={() => {
                          mailLoginFn();
                        }}
                        maxLength={30}
                        minLength={6}
                        placeholder="请输入密码"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        iconRender={(visible) =>
                          visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />
                        }
                      />
                    </Space>
                  </div>

                  <div className="submitBtnText">
                    <StarsBtn
                      text="登录"
                      onClick={_.debounce(mailLoginFn, 500)}
                    ></StarsBtn>
                  </div>
                  <div className="tools_btns">
                    {sysConfig.registerOpen ? (
                      <span onClick={mailRegister} className="btns-item">
                        <span>没有账号? </span>
                        <span className="btns-item-active">立即注册</span>
                      </span>
                    ) : (
                      ""
                    )}

                    <span onClick={forgotPassword} className="btns-item">
                      忘记密码
                    </span>
                  </div>
                </div>
              </div>
            </div>
          ) : (
            // 手机号账号密码登录
            <div className="phoneLogin">
              <div className="phone_login-warp">
                <div className="content-box">
                  <div className="login_info-content">
                    <Space direction="vertical" className="input_space-warp">
                      <Input
                        onPressEnter={() => {
                          phoneLoginFn();
                        }}
                        value={phoneNumber}
                        onChange={(e) => setPhoneNumber(e.target.value)}
                        placeholder="请输入手机号"
                      />
                      <Input.Password
                        onPressEnter={() => {
                          phoneLoginFn();
                        }}
                        maxLength={30}
                        minLength={6}
                        onChange={(e) => setPhonePassword(e.target.value)}
                        placeholder="请输入密码"
                        iconRender={(visible) =>
                          visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />
                        }
                      />
                    </Space>
                  </div>

                  <div className="submitBtnText">
                    <StarsBtn
                      text="登录"
                      onClick={_.debounce(phoneLoginFn, 500)}
                    ></StarsBtn>
                  </div>
                  <div className="tools_btns">
                    {sysConfig.registerOpen ? (
                      <span onClick={phoneNumberRegister} className="btns-item">
                        <span>没有账号? </span>
                        <span className="btns-item-active">立即注册</span>
                      </span>
                    ) : (
                      ""
                    )}

                    <span onClick={phoneForgotPassword} className="btns-item">
                      忘记密码
                    </span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* 其他登录方式 */}
          <div className="other_login-type">
            <span className="line"></span>
            <span>其他登录方式</span>
            <span className="line"></span>
          </div>

          <div className="login_method-box">
            {sysConfig &&
              sysConfig.loginTypes &&
              sysConfig.loginTypes.map((typeItem) => {
                return (
                  <span
                    key={typeItem}
                    className={`${
                      typeItem === loginType ? "activeloginItem" : ""
                    }`}
                  >
                    {
                      loginTypeBtnList.current.find((v) => v.type === typeItem)
                        .component
                    }

                    {typeItem === loginType ? (
                      <img className="shadowIcon" src={shadowIcon} alt="" />
                    ) : (
                      ""
                    )}
                  </span>
                );
              })}
          </div>

          <div className="userAgreementInfoBox">
            <p className="info_item" onClick={userAgreementClick}>
              用户协议
            </p>
            <p className="info_item" onClick={privacyStatementClick}>
              免责声明
            </p>
          </div>
        </div>

        {/* 注册 */}
        <RegisterCopm
          registerState={registerState}
          setRegisterState={setRegisterState}
          sysConfig={sysConfig}
        ></RegisterCopm>
      </div>

      {/* 互联网公安备案号 */}
      {sysConfig?.filingNumber ? (
        <div className="login_bottom-info">
          <a target="_blank" href="https://beian.miit.gov.cn/">
            互联网ICP备案: {sysConfig.filingNumber}
          </a>
        </div>
      ) : (
        ""
      )}

      {/* 用户协议 */}
      <UserAgreement ref={userAgreementRef}></UserAgreement>

      {/* 隐私声明 */}

      <PrivacyStatement ref={privacyStatementRef}></PrivacyStatement>

      {/* 用户注册 */}
      <UserRegister ref={userRegisterRef}></UserRegister>

      {/* 忘记密码 */}
      <ChangePwd ref={changePwdRef}></ChangePwd>
    </div>
  );
};

export default Login;
