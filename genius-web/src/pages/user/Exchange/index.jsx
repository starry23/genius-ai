/*
 * @Description:
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2024-03-09 21:34:41
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-09 22:11:27
 */
import React, { useState } from "react";
import CustomInput from "@/components/CustomInput";
import CustomBtn from "@/components/CustomBtn";
import _ from "lodash";
import { useDispatch } from "react-redux";
import { accountChangeAction } from "@/store/actions/home_action";
import { messageFn } from "@/utils";
import { cardRedeem } from "@/api/user";

const Exchange = () => {
  const dispatch = useDispatch();

  const [code, setCode] = useState("");

  /**
   * @description: 兑换卡密
   * @return {*}
   * @author: jl.g
   */
  const cardRedeemFn = async () => {
    if (!code) {
      messageFn({
        type: "error",
        content: "卡密不能为空",
      });
      return;
    }
    try {
      let data = {
        code: code,
      };
      let res = await cardRedeem(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "卡密兑换成功",
        });
        // 开始查询剩余账户
        dispatch(
          accountChangeAction(new Date().getTime() + "_" + Math.random())
        );
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
    <div className="exchangeWarp">
      <div className="title">卡密兑换</div>

      <CustomInput
        placeholder="请输入卡密"
        value={code}
        onChange={(value) => setCode(value)}
        style={{ height: "40px", margin: "20px 0 20px 0" }}
      ></CustomInput>

      <CustomBtn
        width={100}
        height={35}
        onClick={_.debounce(() => cardRedeemFn(), 500)}
        fontSize={14}
      >
        兑换
      </CustomBtn>
    </div>
  );
};

export default Exchange;
