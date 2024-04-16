import React, { useState, useRef } from "react";
import { messageFn, removeCookie, clearSessionStorage } from "@/utils";
import { updatePassword } from "@/api/user";
import { sendSms } from "@/api/login";
import CustomBtn from "@/components/CustomBtn";
import { useHistory } from "react-router-dom";

const ChangePwd = ({ userInfo }) => {
  const history = useHistory();

  const [newPwd, setNewPwd] = useState("");
  const [smsCode, setSmsCode] = useState("");
  // 验证码状态
  const [smsState, setSmsState] = useState(false);
  // 倒计时定时器
  const countdownSmsTimerRef = useRef(null);
  // 验证码倒计时
  const [countdownSmsTimer, setCountdownSmsTimer] = useState(60);
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

  // 获取验证码
  const getVerificationCode = async () => {
    try {
      // type 1 注册 2 修改密码
      //   sendType 邮箱还是手机号
      let data = {
        sendAccount:
          userInfo?.registerType === 1
            ? userInfo?.email
            : userInfo?.registerType === 2
            ? userInfo?.phone
            : "",
        type: 2,
        sendType: userInfo?.registerType,
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

  //   提交
  const submit = async () => {
    if (!newPwd) {
      messageFn({
        type: "error",
        content: "请输入新密码",
      });
      return;
    }

    if (!smsCode) {
      messageFn({
        type: "error",
        content: "请输入验证码",
      });
      return;
    }

    try {
      let data = {
        accountNum:
          userInfo?.registerType === 1
            ? userInfo?.email
            : userInfo?.registerType === 2
            ? userInfo?.phone
            : "",
        type: userInfo?.registerType,
        code: smsCode,
        password: newPwd,
      };
      let res = await updatePassword(data);

      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "操作成功",
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
  return (
    <div className="changePwd">
      <div className="content_item">
        <div className="label">请输入新密码</div>
        <div className="inputBox">
          <input
            placeholder="请输入新密码"
            value={newPwd}
            onChange={(e) => setNewPwd(e.target.value)}
            className="inputDom"
            type="password"
          />
        </div>
      </div>

      <div className="content_item">
        <div className="label">请输入验证码</div>

        <div className="content_item-warp">
          <div className="inputBox">
            <input
              placeholder="请输入验证码"
              value={smsCode}
              onChange={(e) => setSmsCode(e.target.value)}
              className="inputDom"
              type="text"
            />
          </div>

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
      </div>

      <CustomBtn width={100} height={35} onClick={() => submit()} fontSize={14}>
        提交
      </CustomBtn>
    </div>
  );
};

export default ChangePwd;
