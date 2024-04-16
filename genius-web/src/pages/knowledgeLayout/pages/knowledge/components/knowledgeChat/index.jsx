/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-07-14 09:50:29
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:46:45
 */
import React, { useState, useRef, useEffect } from "react";
import _ from "lodash";
import { getSessionDetail } from "@/api/gpt";
import { copyToClipboardFn } from "@/utils";
import { useSelector, useDispatch } from "react-redux";
import Loading from "@/components/Loading";
import { Input, Button, Empty } from "antd";
import { CopyOutlined, SearchOutlined } from "@ant-design/icons";
import { accountChangeAction } from "@/store/actions/home_action";
import { Markdown } from "@/components/Markdown";
import moment from "moment";
import { getCookie } from "@/utils";
import userImg from "../../../../../../../public/assets/imgs/userDeafultImg.png";
import knowlegdeRobot from "../../../../../../../public/assets/knowledge/knowlegdeRobot.svg";
import folder from "../../../../../../../public/assets/knowledge/projectIcon.svg";
import styles from "./indx.module.less";
const KnowledgeChat = ({ activeFile }) => {
  const dispatch = useDispatch();

  // 选中的项目
  const selectProjectMenuItem = useSelector(
    (state) => state.selectProjectMenuItem
  );

  // 对话详情
  const [sessionDetailList, setSessionDetailList] = useState([]);
  // 对话数据暂存
  const sessionDetailListRef = useRef(null);

  // 对话窗口
  const chatBodyRef = useRef(null);
  // 用户输入的问题
  const [userQuestion, setUserQuestion] = useState("");
  const userQuestionRef = useRef("");
  // 提问状态
  const [questionState, setQuestionState] = useState(false);

  // 消息流返回数据存储
  const eventSourceRef = useRef(null);

  /**
   * @description: 查询当前会话的具体内容
   * @return {*}
   * @author: jl.g
   */
  const getSessionDetailFn = async () => {
    try {
      let params = {
        reqId: selectProjectMenuItem?.reqId,
      };
      let res = await getSessionDetail(params);
      if (res.code === 200) {
        let resData = res.result || [];
        setSessionDetailList(resData);
        sessionDetailListRef.current = resData;
        mackSessionScrollToBottom();
      } else {
        setSessionDetailList([]);
        sessionDetailListRef.current = [];
      }
    } catch (error) {
      console.log(error);
    }
  };

  /**
   * @description: 设置会话滚动条处于页面底部
   * @return {*}
   * @author: jl.g
   */
  const mackSessionScrollToBottom = () => {
    let ele = chatBodyRef.current;

    setTimeout(() => {
      if (ele) {
        if (ele.scrollHeight > ele.clientHeight) {
          ele.scrollTop = ele.scrollHeight + 50;
        }
      }
    }, 50);
  };

  // 建立消息流
  const initEventSource = () => {
    let tokenName = getCookie("tokenName");
    let tokenValue = getCookie("tokenValue");
    let questionUrl = window.location.origin;
    eventSourceRef.current = new EventSource(
      `${questionUrl}/api/chat/questions?prompt=${encodeURIComponent(
        userQuestionRef.current
      )}&reqId=${
        selectProjectMenuItem?.reqId
      }&${tokenName}=Bearer ${tokenValue}&productType=${5}&logType=KNOWLEDGE`
    );
  };

  // 监听消息返回
  const watchEventSource = (messageTemp) => {
    eventSourceRef.current.onmessage = function (event) {
      if (event.data === "DONE") {
        closeAnswer();
        setQuestionState(false);
        userQuestionRef.current='';
        // 开始查询剩余账户
        dispatch(
          accountChangeAction(new Date().getTime() + "_" + Math.random())
        );
      } else {
        let resData = JSON.parse(event.data);
        let str = "";
        if (resData.code) {
          str = str + resData?.message || "";
        } else {
          str = str + resData?.content || "";
        }

        messageTemp.content = messageTemp.content + str;
        setSessionDetailList([
          ...sessionDetailListRef.current.slice(0, -1),
          messageTemp,
        ]);
      }

      // 始终让返回处于底部
      mackSessionScrollToBottom();
    };

    eventSourceRef.current.onerror = function (event) {
      setQuestionState(false);
      closeAnswer();
    };
  };

  /**
   * @description: 停止消息流
   * @return {*}
   * @author: jl.g
   */

  const closeAnswer = () => {
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
    }
  };

  // 发送消息
  const sendQuestion = () => {
    try {
      if (!userQuestionRef.current || questionState) return;
      // 清空输入框
      setUserQuestion("");

      // 发送消息为回复前需要将状态设置为不发发送
      setQuestionState(true);
      // 用户信息
      let useQuestion = {
        content: userQuestionRef.current,
        chatRole: "user",
        reqId: new Date().getTime() + "-" + "user",
        createTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"),
      };
      // 消息模板
      const messageTemplate = {
        content: "",
        reqId: new Date().getTime() + "-" + "question",
        chatRole: "assistant",
        createTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"),
      };
      setSessionDetailList([
        ...sessionDetailList,
        useQuestion,
        messageTemplate,
      ]);
      sessionDetailListRef.current = [
        ...sessionDetailList,
        useQuestion,
        messageTemplate,
      ];

      // 始终让返回处于底部
      mackSessionScrollToBottom();
      initEventSource();
      watchEventSource(messageTemplate);
    } catch (error) {
      setQuestionState(false);
      userQuestionRef.current='';
      console.log(error);
    }
  };
  /**
   * @description: 复制会话内容
   * @return {*}
   * @author: jl.g
   */
  const copyToClipboard = (e, data) => {
    e.stopPropagation();
    copyToClipboardFn(data, "复制内容成功");
  };
  useEffect(() => {
    if (selectProjectMenuItem) {
      getSessionDetailFn();
    }
  }, [selectProjectMenuItem]);

  return (
    <div className={styles.knowledgeChatWarp}>
      <div className="knowledge_content">
        <div className="konwledge-project_info_box">
          <img className="konwledge-project_icon" src={folder} alt="" />
          <div className="konwledge-project_info">
            <div
              title={selectProjectMenuItem?.itemName}
              className="konwledge-project_info-title"
            >
              {selectProjectMenuItem?.itemName}
            </div>
            <div
              title={selectProjectMenuItem?.itemDesc}
              className="konwledge-project_info-desc"
            >
              {selectProjectMenuItem?.itemDesc}
            </div>
          </div>
        </div>

        {activeFile ? (
          <div className="knowledge_content-file-info">
            <div className="knowledge_content-info-outline">
              {activeFile?.summaryDesc}
            </div>
            <div
              onClick={_.debounce(() => {
                userQuestionRef.current = activeFile?.q1;
                return sendQuestion();
              }, 1000)}
              className="knowledge_content-info-question"
            >
              {activeFile?.q1}
            </div>

            <div
              onClick={_.debounce(() => {
                userQuestionRef.current = activeFile?.q2;
                return sendQuestion();
              }, 1000)}
              className="knowledge_content-info-question"
            >
              {activeFile?.q2}
            </div>
          </div>
        ) : (
          ""
        )}

        <div className="konwledge_chat-content" ref={chatBodyRef}>
          {sessionDetailList.map((v, index) => (
            <div key={v.reqId + "_" + index}>
              {v.chatRole === "user" ? (
                <div className="chat_user">
                  <div className="chat_user-content">
                    <div className="chat_user-question">{v.content}</div>
                    <div className="chat_img-div">
                      <img className="chat-img" src={userImg} alt="" />
                    </div>
                  </div>
                  <div className="chat_tools">
                    <span
                      className="copy"
                      onClick={(e) => copyToClipboard(e, v.content)}
                    >
                      <CopyOutlined></CopyOutlined>
                      复制
                    </span>
                  </div>
                </div>
              ) : v.chatRole === "assistant" ? (
                <div className="chat_ai">
                  <div className="chat_ai-content">
                    <div className="chat_img-div">
                      <img className="chat-img" src={knowlegdeRobot} alt="" />
                    </div>

                    <div className="chat_robit-answer markdown-body">
                      {v.content ? (
                        <Markdown content={v.content}></Markdown>
                      ) : (
                        <Loading></Loading>
                      )}
                    </div>
                  </div>
                  <div className="chat_tools">
                    <span
                      className="copy"
                      onClick={(e) => copyToClipboard(e, v.content)}
                    >
                      <CopyOutlined></CopyOutlined>
                      复制
                    </span>
                  </div>
                </div>
              ) : (
                ""
              )}
            </div>
          ))}

          {/* 数据为空时展示 */}
          {sessionDetailList.length === 0 ? (
            <div className="konwledge_chat-empty">
              <Empty />;
            </div>
          ) : (
            ""
          )}
        </div>
      </div>
      {/* // sendQuestion */}
      <div className="knowledge_bottom-chat">
        <textarea
          style={{ resize: "none" }}
          className="knowledge_chat-textarea"
          disabled={questionState || !activeFile}
          value={userQuestion}
          onInput={(e) => {
            userQuestionRef.current = e.target.value;
            setUserQuestion(e.target.value);
          }}
          placeholder="请输入搜索内容"
        ></textarea>
        <div className="knowledge_chat-search" onClick={sendQuestion}>
          搜索
        </div>
      </div>
    </div>
  );
};

export default KnowledgeChat;
