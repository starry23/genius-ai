/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-06 08:57:51
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:44:47
 */
import React, { useState, forwardRef, useImperativeHandle } from "react";
import styles from "./index.module.less";
import { CloseOutlined } from "@ant-design/icons";
import { Input, Button } from "antd";
import { inviteGiftUrlInfo } from "@/api/user";
import { copyToClipboardFn } from "@/utils";
import CustomBtn from "@/components/CustomBtn";
const InviteGiftUrlInfo = forwardRef((props, ref) => {
  const [pageState, setPageState] = useState(false);
  const [inviteUrl, setInviteUrl] = useState("");
  const [inviteDesc, setInviteDesc] = useState("");
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
    inviteGiftUrlInfoFn();
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
   * @description: 一键复制
   * @return {*}
   * @author: jl.g
   */
  const copyToClipboard = () => {
    copyToClipboardFn(inviteUrl, "邀请链接复制成功,快去分享吧");
  };

  /**
   * @description: 获取分享链接
   * @return {*}
   * @author: jl.g
   */

  const inviteGiftUrlInfoFn = async () => {
    try {
      let res = await inviteGiftUrlInfo();
      if (res.code === 200) {
        const { code, url, desc } = res.result;
        let inviteUrl = `${url}/#/login?code=${code}`;
        setInviteUrl(inviteUrl);

        const tranformDesc =
          desc.indexOf("-1") !== -1 ? desc.replace("-1天", "无限期") : desc;

        setInviteDesc(tranformDesc);
      }
    } catch (error) {
      console.log(error);
    }
  };
  return pageState ? (
    <div className={styles.invite_giftUrl_info}>
      <div className="user_detail-warp">
        <div className="user_detail-head">
          <CloseOutlined
            onClick={hidePage}
            className="closeBtn"
            twoToneColor="#fff"
          ></CloseOutlined>
        </div>
        <div className="title">邀请有礼</div>
        <div className="user_detail-content">
          <div className="content">{inviteDesc}</div>
          <p className="invite_url_title">您的专属邀请链接</p>

          <div className="invite_url_warp">
            <Input.Group compact>
              <Input
                readOnly
                style={{ width: "calc(100% - 200px)" }}
                value={inviteUrl}
              />
              <CustomBtn
                width={100}
                onClick={() => copyToClipboard()}
                fontSize={14}
              >
                一键复制
              </CustomBtn>
            </Input.Group>
          </div>

          <div className="mobile_invite_url_warp">
            <Input.Group compact style={{ marginBottom: 10 }}>
              <Input readOnly value={inviteUrl} />
            </Input.Group>

            <CustomBtn
              width={100}
              height={35}
              onClick={() => copyToClipboard()}
              fontSize={14}
            >
              一键复制
            </CustomBtn>
          </div>
        </div>
      </div>
    </div>
  ) : (
    ""
  );
});

export default InviteGiftUrlInfo;
