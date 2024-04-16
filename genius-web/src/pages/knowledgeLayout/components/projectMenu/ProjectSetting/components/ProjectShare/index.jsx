/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-08-06 16:00:51
 * @LastEditors: jl.g
 * @LastEditTime: 2023-10-23 17:12:52
 */
import React, { useState, useEffect } from "react";
import { Input, Button } from "antd";
import { Switch } from "antd";
import { shareSetting, getShareInfo } from "@/api/knowledgeBase";
import { messageFn, copyToClipboardFn, unicodeFun } from "@/utils";
import styles from "./index.module.less";
const ProjectShare = ({ projetcInfo }) => {
  const [shareInfo, setShareInfo] = useState(null);

  // h5分享链接
  const [h5ShareUrl, setH5ShareUrl] = useState(null);
  // html分享
  const [htmlShareUrl, setHtmlShareUrl] = useState(null);
  const onChange = (state) => {
    setShareInfo({
      ...shareInfo,
      isEnable: state ? 1 : 0,
    });

    shareSettingFn({
      shareType: 10,
      isEnable: state ? 1 : 0,
      itemId: projetcInfo?.itemId,
    });
  };

  // 获取分享信息
  const getShareInfoFn = async () => {
    try {
      let params = {
        itemId: projetcInfo?.itemId,
        shareType: 10,
      };
      let res = await getShareInfo(params);
      if (res.code === 200) {
        console.log(res.result);
        setShareInfo(res.result);

        const { domain, key } = res.result;
        setH5ShareUrl(
          `${domain}/#/knowledgeShare?key=${key}&projectName=${unicodeFun.toUnicode(projetcInfo?.itemName)}`
        );
        setHtmlShareUrl(
          `<iframe title="智能知识库" src='${domain}/#/knowledgeShare?key=${key}&projectName=${projetcInfo?.itemName}'></iframe>`
        );
      } else {
        messageFn({
          type: "error",
          message: res.message,
        });
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 保存分享信息
  const shareSettingFn = async (data) => {
    try {
      let res = await shareSetting(data);
      if (res.code === 200) {
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

  // 一键复制
  const copyToClipboard = (url) => {
    copyToClipboardFn(url, "复制分享链接成功");
  };

  useEffect(() => {
    if (projetcInfo) {
      getShareInfoFn();
    }
  }, [projetcInfo]);

  return (
    <div className={styles.ProjectShareWarp}>
      <div className="head_setting">
        <Switch
          checked={shareInfo?.isEnable ? true : false}
          onChange={onChange}
        />
        <span className="info">
          开启分享设置后,可将链接分享给其他人,他人可以通过您的链接对当前的知识库内容进行访问。
        </span>
      </div>
      {shareInfo?.isEnable ? (
        <div className="projectShareContent">
          <div className="content_item">
            <div className="content_item-title">Web/H5网页/公众号接入</div>
            <div className="content_item-info">
              用户通过此链接可以直接和您的机器人对话。如需接入公众号，请将链接设置到公众号菜单
            </div>

            <div className="content_item-copy">
              <Input.Group compact>
                <Input
                  value={h5ShareUrl}
                  readOnly
                  style={{ width: "calc(100% - 80px)" }}
                />
                <Button
                  onClick={() => copyToClipboard(h5ShareUrl)}
                  type="primary"
                >
                  复制
                </Button>
              </Input.Group>
            </div>
          </div>
          <div className="content_item">
            <div className="content_item-title">Html代码接入</div>
            <div className="content_item-info">
              在网站的任何位置添加聊天机器人，请将以下代码粘贴到您的Html代码中
            </div>

            <div className="content_item-copy">
              <Input.Group compact>
                <Input
                  value={htmlShareUrl}
                  readOnly
                  style={{ width: "calc(100% - 80px)" }}
                />
                <Button
                  onClick={() => copyToClipboard(htmlShareUrl)}
                  type="primary"
                >
                  复制
                </Button>
              </Input.Group>
            </div>
          </div>
        </div>
      ) : (
        ""
      )}
    </div>
  );
};

export default ProjectShare;
