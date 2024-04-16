/*
 * @Description:
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2024-02-20 22:38:40
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-06 21:51:10
 */
import React, { useRef, useState, useEffect } from "react";
import { useHistory } from "react-router-dom";

import { Input } from "antd";
import { EyeInvisibleOutlined, EyeTwoTone } from "@ant-design/icons";
import {
  imageVerificationCode,
  checkImageCode,
  sendSms,
  smsSignUp,
} from "@/api/login";
import { updatePassword } from "@/api/user";

import { messageFn, queryString, setCookie } from "@/utils";

import styles from "./index.module.less";
const RegisterCopm = ({ registerState, sysConfig, setRegisterState }) => {
  const history = useHistory();

  // 1 邮箱 2手机号
  const [activeRegistType, setActiveRegistType] = useState(null);
  const [registList, setRegistList] = useState([]);
  // 注册列表
  const registTypeList = useRef([
    {
      value: 1,
      name: "邮箱",
    },
    {
      value: 2,
      name: "手机号",
    },
  ]);
  // 拉新code
  const inviteCode = useRef("");
  // 账号
  const [account, setAccount] = useState("");

  // 密码
  const [password, setPassword] = useState("");

  // 图形验证码
  const [imgCode, setImgCode] = useState("");

  // 图形验证码验证状态
  const [validateImgCode, setValidateImgCode] = useState(false);

  // 图形验证码图片
  const [vcerificationCodeImg, setVcerificationCodeImg] = useState(null);
  // 验证码
  const [verificationCode, setVerificationCode] = useState("");
  // 验证码状态
  const [smsState, setSmsState] = useState(false);
  // 验证码倒计时
  const [countdownSmsTimer, setCountdownSmsTimer] = useState(60);
  // 倒计时定时器
  const countdownSmsTimerRef = useRef(null);

  // 初始化输入状态
  const initRegisterFn = () => {
    if (countdownSmsTimerRef.current) {
      clearInterval(countdownSmsTimerRef.current);
    }
    setAccount("");
    setPassword("");
    setImgCode("");
    setVcerificationCodeImg(null);
    setVerificationCode("");
    setSmsState(false);
    setValidateImgCode(false);
  };

  /**
   * @description: 获取图形验证码
   * @return {*}
   * @author: jl.g
   */
  const getImageVerificationCode = async () => {
    if (!account) return;

    try {
      let params = {
        phone: account,
      };
      let res = await imageVerificationCode(params);
      if (res) {
        let resImg = window.URL.createObjectURL(res);
        setVcerificationCodeImg(resImg);
      }
    } catch (error) {
      console.log(error);
    }
  };

  /**
   * @description: 校验图片验证码
   * @return {*}
   * @author: jl.g
   */
  const checkImgCodeFn = async () => {
    if (!imgCode || !account) return;
    try {
      let data = {
        accountNum: account,
        code: imgCode,
      };
      let res = await checkImageCode(data);
      if (res.code === 200) {
        setValidateImgCode(true);
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

  // 切换验证码
  const changeVcerificationCode = () => {
    getImageVerificationCode();
  };

  // 获取验证码
  const getVerificationCode = async () => {
    if (!imgCode) {
      messageFn({
        type: "error",
        content: "请输入图片验证码",
      });
      return;
    }

    if (!validateImgCode) {
      messageFn({
        type: "error",
        content: "图形验证码验证失败",
      });
      return;
    }
    try {
      // type 1 注册 2 修改密码
      let data = {
        sendAccount: account,
        type: registerState.forgetPasState ? 2 : 1,
        sendType: activeRegistType,
      };
      let res = await sendSms(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "验证码发送成功",
        });
        setSmsState(true);
        // 60S打倒计时
        countdownSmsTimerFn();
      } else {
        messageFn({
          type: "error",
          content: res.message,
        });
        setSmsState(false);
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 倒计时开始
  const countdownSmsTimerFn = () => {
    if (countdownSmsTimerRef.current) {
      clearInterval(countdownSmsTimerRef.current);
    }
    let count = 60;
    setCountdownSmsTimer(60);
    countdownSmsTimerRef.current = setInterval(() => {
      count--;

      if (count <= 0) {
        setSmsState(false);
        if (countdownSmsTimerRef.current) {
          clearInterval(countdownSmsTimerRef.current);
        }
      }
      setCountdownSmsTimer(count);
    }, 1000);
  };

  const submit = () => {
    if (!account) {
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
    } else if (!imgCode) {
      messageFn({
        type: "error",
        content: "请输入图片验证码",
      });
      return;
    } else if (!verificationCode) {
      messageFn({
        type: "error",
        content: "请输入验证码",
      });
      return;
    } else if (password.length < 6) {
      messageFn({
        type: "error",
        content: "密码长度需大于6位",
      });
      return;
    }
    // 修改密码
    if (registerState.forgetPasState) {
      handlerForgetPas();
    } else {
      // 注册
      handlerRegister();
    }
  };

  // 修改密码
  const handlerForgetPas = async () => {
    try {
      let data = {
        accountNum: account,
        type: activeRegistType,
        code: verificationCode,
        password: password,
      };
      let res = await updatePassword(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "操作成功",
        });
        setRegisterState(() => {
          return {
            display: false,
            forgetPasState: false,
          };
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

  // 注册账号
  const handlerRegister = async () => {
    try {
      let data = {
        accountNum: account,
        type: activeRegistType,
        imageVerificationCode: imgCode,
        smsCode: verificationCode,
        password: password,
        inviteCode: inviteCode.current,
      };
      let res = await smsSignUp(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "恭喜您，注册成功,2s后自动登录",
        });
        setCookie("tokenName", "satoken");
        setCookie("tokenValue", res.result);
        setTimeout(() => {
          history.replace("/ai/home");
        }, 2 * 1000);
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
    if (sysConfig?.loginTypes) {
      let resData = [];
      sysConfig?.loginTypes.forEach((v) => {
        let activeItem = registTypeList.current.find(
          (item) => item.value === v
        );
        if (activeItem) {
          resData.push(activeItem);
        }
      });

      setRegistList(resData);

      if (resData.length) {
        setActiveRegistType(resData[0].value);
      }
    }
  }, [sysConfig?.loginTypes]);

  useEffect(() => {
    let urlInviteCode = queryString("code");
    if (urlInviteCode) {
      inviteCode.current = urlInviteCode;
    }
  }, []);

  const backLogin = () => {
    setRegisterState(() => {
      return {
        display: false,
        forgetPasState: false,
      };
    });
  };

  // 修改注册类型
  const changeRegistType = (item) => {
    setActiveRegistType(item.value);
    initRegisterFn();
  };
  return (
    <div
      className={`${styles.typeLoginBox} ${
        sysConfig?.loginViewType === 1 ? "typeLoginBox2" : ""
      } ${registerState.display ? "displayRegister" : "displayNone"}`}
    >
      {sysConfig?.iconUrl ? (
        <img className="logo" src={sysConfig?.iconUrl} alt="" />
      ) : (
        ""
      )}

      <div className="login_content_webName">{sysConfig?.webName}</div>

      <div className="title">
        {registerState.forgetPasState ? "修改密码" : "注册账号"}
      </div>

      {/* 注册方式 */}
      {registList.length > 1 ? (
        <div className="regist_type-warp">
          {registList.map((item) => (
            <div
              onClick={() => changeRegistType(item)}
              key={item?.value}
              className={`regist_item  ${
                item?.value === activeRegistType ? "activeItem " : ""
              }`}
            >
              {item?.name}
            </div>
          ))}
        </div>
      ) : (
        ""
      )}

      <div className="login_info-content">
        <div className="verificationWarp">
          <Input
            className="verificationInput"
            autoComplete="new-user"
            maxLength={30}
            value={account}
            onBlur={getImageVerificationCode}
            onChange={(e) => setAccount(e.target.value)}
            placeholder={
              activeRegistType === 2 ? "请输入手机号码" : "请输入邮箱账号"
            }
          />
        </div>

        <div className="verificationWarp">
          <Input.Password
            className="verificationInput"
            autoComplete="new-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            maxLength={30}
            minLength={6}
            placeholder="请输入密码"
            iconRender={(visible) =>
              visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />
            }
          />
        </div>

        {/* 图形验证码 */}
        {vcerificationCodeImg && account ? (
          <div className="verificationWarp">
            <Input
              value={imgCode}
              onChange={(e) => setImgCode(e.target.value)}
              onBlur={checkImgCodeFn}
              className="verificationInput"
              maxLength={30}
              placeholder="请输入图形验证码"
            />

            {vcerificationCodeImg ? (
              <img
                onClick={changeVcerificationCode}
                className="vcerification_code-img"
                src={vcerificationCodeImg}
                alt=""
              />
            ) : (
              ""
            )}
          </div>
        ) : (
          ""
        )}

        <div className="verificationWarp">
          <Input
            value={verificationCode}
            onChange={(e) => setVerificationCode(e.target.value)}
            className="verificationInput"
            maxLength={30}
            placeholder="请输入验证码"
          />

          <div className="verificationCode">
            {!smsState ? (
              <span className="code-item" onClick={() => getVerificationCode()}>
                获取验证码
              </span>
            ) : (
              <span className="code-item">{countdownSmsTimer}s</span>
            )}
          </div>
        </div>

        <div onClick={submit} className="registBtn">
          {registerState.forgetPasState ? "确定" : "注册"}
        </div>

        <div className="backLogin" onClick={backLogin}>
          返回登录
        </div>
      </div>
    </div>
  );
};

export default RegisterCopm;
