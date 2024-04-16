/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-06 08:57:51
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-09 22:10:22
 */
import React, {
  useState,
  forwardRef,
  useImperativeHandle,
  useRef,
} from "react";
import styles from "./index.module.less";
import { CloseOutlined } from "@ant-design/icons";
import _ from "lodash";

import { Input, Button } from "antd";
import { cardRedeem } from "@/api/user";
import { messageFn } from "@/utils";
import { accountChangeAction } from "@/store/actions/home_action";
import { useDispatch } from "react-redux";

const InviteGiftUrlInfo = forwardRef(({ hidePage: parentHidePage }, ref) => {
  const dispatch = useDispatch();

  const [pageState, setPageState] = useState(false);
  // 兑换码
  const code = useRef("");
  useImperativeHandle(ref, () => {
    return {
      getPage,
    };
  });
  /**
   * @description: 弹框展示
   * @return {*}
   * @author: jl.g
   */
  const getPage = () => {
    setPageState(true);
    parentHidePage();
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
   * @description: 兑换卡密
   * @return {*}
   * @author: jl.g
   */
  const cardRedeemFn = async () => {
    if (!code.current) {
      messageFn({
        type: "error",
        content: "卡密不能为空",
      });
      return;
    }
    try {
      let data = {
        code: code.current,
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
        hidePage();
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

  return pageState ? (
    <div className={styles.invite_giftUrl_info}>
      <div className="user_detail-warp">
        <div className="user_detail-head">
          <span className="title">卡密兑换</span>
          <CloseOutlined
            onClick={hidePage}
            className="closeBtn"
            twoToneColor="#fff"
          ></CloseOutlined>
        </div>
        <div className="user_detail-content">
          <div className="invite_url_warp">
            <Input.Group compact>
              <Input
                onChange={(e) => (code.current = e.target.value)}
                placeholder="请输入卡密"
                style={{ width: "calc(100% - 200px)" }}
              />
              <Button
                onClick={_.debounce(() => cardRedeemFn(), 500)}
                type="primary"
              >
                兑换
              </Button>
            </Input.Group>
          </div>

          <div className="mobile_invite_url_warp">
            <Input.Group compact>
              <Input
                onChange={(e) => (code.current = e.target.value)}
                placeholder="请输入卡密"
              />
            </Input.Group>
            <Button
              onClick={_.debounce(() => cardRedeemFn(), 500)}
              type="primary"
            >
              兑换
            </Button>
          </div>
        </div>
      </div>
    </div>
  ) : (
    ""
  );
});

export default InviteGiftUrlInfo;
