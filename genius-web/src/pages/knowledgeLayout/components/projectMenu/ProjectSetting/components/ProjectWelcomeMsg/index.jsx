/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-09-18 14:50:33
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:46:25
 */
import React, { useState, useEffect } from "react";
import { Input, Button } from "antd";
import { welcomeSetting, getWelcomeSetting } from "@/api/knowledgeBase";
import _ from "lodash";
import { messageFn } from "@/utils";
import styles from "./index.module.less";
const { TextArea } = Input;
const { Search } = Input;

const ProjectWelcomeMsg = ({ projetcInfo }) => {
  const [welcomeInfo, setWelcomeInfo] = useState({
    welcome:
      "您好,我是您的智能助手💬,有任何问题您都可以询问我, 💡 问题 : 请帮我介绍一下当前知识库。",
    fastPrompts: [],
  });

  const changeWcInfo = (e) => {
    setWelcomeInfo((data) => {
      return {
        ...data,
        welcome: e,
      };
    });
  };

  const addQuestion = () => {
    setWelcomeInfo((data) => {
      return {
        ...data,
        fastPrompts: [...data.fastPrompts, ""],
      };
    });
  };

  const changeQuestion = (value, index) => {
    const copyData = _.cloneDeep(welcomeInfo.fastPrompts);
    copyData.splice(index, 1, value);
    setWelcomeInfo((data) => ({
      ...data,
      fastPrompts: copyData,
    }));
  };

  const deleteQuestion = (index) => {
    console.log(index);
    const copyData = _.cloneDeep(welcomeInfo.fastPrompts);
    copyData.splice(index, 1);

    setWelcomeInfo((data) => ({
      ...data,
      fastPrompts: copyData,
    }));
  };

  //   保存配置
  const welcomeSettingFn = async () => {
    try {
      let data = {
        ...welcomeInfo,
        itemId: projetcInfo.itemId,
      };
      let res = await welcomeSetting(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "操作成功",
        });
      } else {
        messageFn({
          type: "error",
          content: "操作失败",
        });
      }
    } catch (error) {
      console.log(error);
    }
  };

  const getWelcomeSettingFn = async () => {
    try {
      let res = await getWelcomeSetting(projetcInfo.itemId);
      if (res.code === 200) {
        let { welcome, fastPrompts } = res.result;
        setWelcomeInfo({
          welcome,
          fastPrompts:fastPrompts||[],
        });
      }
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    getWelcomeSettingFn();
  }, []);
  return (
    <div className={styles.projectWelcomeMsgWarp}>
      <TextArea
        value={welcomeInfo.welcome}
        showCount
        rows={3}
        onChange={(e) => changeWcInfo(e.target.value)}
        maxLength={200}
        placeholder="您好,我是您的智能助手💬,有任何问题您都可以询问我, 💡 问题 : 请帮我介绍一下当前知识库。"
      />

      <div className="content_qs-menu">
        {welcomeInfo.fastPrompts.map((v, index) => (
          <Search
            key={index}
            className="qs_menu-item"
            placeholder="请输入问题"
            value={v}
            onChange={(e) => changeQuestion(e.target.value, index)}
            enterButton={
              <Button
                onClick={() => deleteQuestion(index)}
                type="primary"
                danger
              >
                删除
              </Button>
            }
            style={{
              width: "100%",
            }}
          />
        ))}
      </div>

      <Button className="addQuestion" onClick={addQuestion} type="dashed">
        添加快捷提问
      </Button>

      <Button className="addQuestion" onClick={welcomeSettingFn} type="primary">
        保存
      </Button>
    </div>
  );
};

export default ProjectWelcomeMsg;
