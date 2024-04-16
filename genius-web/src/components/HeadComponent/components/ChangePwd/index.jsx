/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-08 10:06:25
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-04 21:50:11
 */
import React, {
  useState,
  forwardRef,
  useImperativeHandle,
  useRef,
  useEffect,
} from "react";
import { Input } from "antd";
import { useHistory } from "react-router-dom";
import _ from "lodash";
import styles from "./index.module.less";
import { CloseOutlined } from "@ant-design/icons";
import UserAttestation from "@/pages/login/components/UserAttestation";
import {
  messageFn,
  removeCookie,
  getSessionStorage,
  clearSessionStorage,
} from "@/utils";
import { sendSms } from "@/api/login";
import { updatePassword } from "@/api/user";
const ChangePwd = forwardRef((props, ref) => {
  const history = useHistory();
  const [pageState, setPageState] = useState(false);
  // 手机号
  const [phoneNumber, setPhoneNumber] = useState("");
  // 密码
  const [password, setPassword] = useState("");
  // 短信验证码
  const [smsVerificationCode, setSmsVerificationCode] = useState("");

  // 验证码状态
  const [smsState, setSmsState] = useState(false);

  // 验证码倒计时
  const [countdownSmsTimer, setCountdownSmsTimer] = useState(60);

  const countdownSmsTimerRef = useRef(null);
  // 图片验证码
  const [imgVerificationCode, setImgVerificationCode] = useState("");

  const userAttestationRef = useRef(null);

  // 标题类型0 忘记密码  1 修改密码
  const [titleType, setTitleType] = useState(1);

  // 类型 1: 邮箱 2: 手机号
  const loginType = useRef(null);
  // 监听图片验证码变化后 调用短信发送
  useEffect(() => {
    if (imgVerificationCode) {
      getSmsCode();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [imgVerificationCode]);

  useEffect(() => {
    if (countdownSmsTimerRef.current) {
      clearInterval(countdownSmsTimerRef.current);
    }
  }, []);

  useImperativeHandle(ref, () => {
    return {
      getPage,
    };
  });

  /**
   * @description: 弹框展示
   * @return {*}sendType 1 邮箱 2手机号
   * @author: jl.g
   */
  const getPage = (type, sendType) => {
    setTitleType(type);
    setPageState(true);
    loginType.current = sendType;
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
   * @description: 校验人机操作
   * @return {*}
   * @author: jl.g
   */
  const getVerificationCode = () => {
    if (!phoneNumber) {
      messageFn({
        type: "error",
        content: "请输入账号",
      });

      return;
    }
    userAttestationRef.current.getPage();
  };

  /**
   * @description: 获取短信验证码
   * @return {*}
   * @author: jl.g
   */
  const getSmsCode = async () => {
    try {
      let data = {
        sendAccount: phoneNumber,
        type: 2, //1注册 2 修改密码
        sendType: loginType.current,
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

  const submit = async () => {
    if (!phoneNumber) {
      messageFn({
        type: "error",
        content: "请输入账号",
      });
      return;
    } else if (!imgVerificationCode) {
      messageFn({
        type: "error",
        content: "请输入图形验证码",
      });
      return;
    } else if (!smsVerificationCode) {
      messageFn({
        type: "error",
        content: "请输入验证码",
      });
      return;
    } else if (!password) {
      messageFn({
        type: "error",
        content: "请输入密码",
      });
      return;
    } else if (password.length < 6) {
      messageFn({
        type: "error",
        content: "密码长度需大于6位",
      });
      return;
    }

    try {
      let data = {
        accountNum: phoneNumber,
        type: loginType.current,
        password: password,
        code: smsVerificationCode,
      };
      let res = await updatePassword(data);
      if (res.code === 200) {
        hidePage();
        history.replace("/login");
        removeCookie("satoken");
        removeCookie("tokenName");
        removeCookie("tokenValue");
        // 清空session
        clearSessionStorage();
        messageFn({
          type: "success",
          content: "操作成功",
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

  return (
    <>
      {pageState ? (
        <div className={styles.custom_dialog}>
          <div className="custom_dialog-warp animate__animated animate__zoomInDown">
            <div className="custom_dialog-head">
              <span className="title">
                {titleType === 0 ? "忘记密码" : "修改密码"}
              </span>
              <CloseOutlined
                onClick={hidePage}
                className="closeBtn"
                twoToneColor="#fff"
              ></CloseOutlined>
            </div>

            <div className="custom_dialog-content">
              <div className="content-item">
                <Input
                  maxLength={30}
                  value={phoneNumber}
                  onChange={(e) => setPhoneNumber(e.target.value)}
                  placeholder="请输入账号"
                />
              </div>

              <div className="verification_code-item">
                <Input
                  maxLength={30}
                  minLength={6}
                  autoComplete="new-password"
                  value={smsVerificationCode}
                  onChange={(e) => setSmsVerificationCode(e.target.value)}
                  placeholder="请输入验证码"
                />
                {!smsState ? (
                  <span className="code-item" onClick={getVerificationCode}>
                    获取验证码
                  </span>
                ) : (
                  <span className="code-item">{countdownSmsTimer}s</span>
                )}
              </div>
              <div className="content-item">
                <Input.Password
                  autoComplete="new-password"
                  maxLength={30}
                  minLength={6}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="请输入6~30位密码"
                />
              </div>

              <div className="vcerification_code-img-box">
                <div className="user_detail_bottom">
                  <div
                    onClick={_.debounce(submit, 500)}
                    className="submitBtnText"
                  >
                    确定
                  </div>
                </div>
              </div>
            </div>
          </div>

          <UserAttestation
            setImgVerificationCode={setImgVerificationCode}
            phoneNumber={phoneNumber}
            ref={userAttestationRef}
          ></UserAttestation>
        </div>
      ) : (
        ""
      )}
    </>
  );
});

export default ChangePwd;
