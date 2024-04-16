/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2024-03-09 10:28:03
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-09 20:10:39
 */
/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2024-03-09 10:28:03
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-09 20:09:33
 */
import React, { useState, useEffect } from "react";
import { inviteGiftUrlInfo } from "@/api/user";
import { copyToClipboardFn } from "@/utils";
import CustomBtn from "@/components/CustomBtn";
import CustomInput from "@/components/CustomInput";

import styles from "./index.module.less";
const InviteGiftUrlInfo = () => {
  const [inviteDesc, setInviteDesc] = useState("");
  const [inviteUrl, setInviteUrl] = useState("");
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
  /**
   * @description: 一键复制
   * @return {*}
   * @author: jl.g
   */
  const copyToClipboard = () => {
    copyToClipboardFn(inviteUrl, "邀请链接复制成功,快去分享吧");
  };
  useEffect(() => {
    inviteGiftUrlInfoFn();
  }, []);
  return (
    <div className={styles.inviteGiftUrlInfoWarp}>
      <div className="inviteInfo">{inviteDesc}</div>
      <p className="invite_url_title">您的专属邀请链接</p>

      <CustomInput
        value={inviteUrl}
        disabled
        style={{ height: "40px", margin: "10px 0 20px 0" }}
      ></CustomInput>


      <CustomBtn
        width={100}
        height={35}
        onClick={() => copyToClipboard()}
        fontSize={14}
      >
        一键复制
      </CustomBtn>
    </div>
  );
};

export default InviteGiftUrlInfo;
