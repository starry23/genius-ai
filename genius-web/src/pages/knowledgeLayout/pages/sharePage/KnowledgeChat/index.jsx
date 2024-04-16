/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-07-14 09:50:29
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:46:54
 */
import React, { useState, useRef, useEffect } from "react";
import _ from "lodash";
import { copyToClipboardFn, getUrlParameter } from "@/utils";
import SockJS from "sockjs-client";
import Loading from "@/components/Loading";
import { Input, Button, Empty, Tag, Space } from "antd";
import { CopyOutlined, SearchOutlined } from "@ant-design/icons";
import { Markdown } from "@/components/Markdown";
import moment from "moment";
import userImg from "../../../../../../public/assets/imgs/userDeafultImg.png";
import knowlegdeRobot from "../../../../../../public/assets/knowledge/knowlegdeRobot.svg";
import { getWelcomeAndMenus } from "@/api/knowledgeBase";
import styles from "./indx.module.less";
const KnowledgeChat = () => {
  // 实现断线重连
  // 最大重连次数
  var maxReconnectAttempts = useRef(5);
  // 重连间隔时间（毫秒）
  var reconnectInterval = useRef(3000); // 每隔3秒尝试一次重连
  // 当前尝试次数
  var currentAttempt = useRef(0);

  // 消息模板
  const messageTemplate = useRef({
    content:
      "您好,我是您的智能助手💬,有任何问题您都可以询问我,\n 💡 问题 : 请帮我介绍一下当前知识库。",
    reqId: new Date().getTime() + "-" + "question",
    chatRole: "assistant",
    createTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"),
  });
  // 对话详情
  const [sessionDetailList, setSessionDetailList] = useState([]);
  // 对话数据暂存
  const sessionDetailListRef = useRef(null);

  // 对话窗口
  const chatBodyRef = useRef(null);
  // 用户输入的问题
  const [userQuestion, setUserQuestion] = useState("");

  // 提问状态
  const [questionState, setQuestionState] = useState(false);

  // 欢迎语和快捷提问和菜单
  const [welcomeInfo, setWelcomeInfo] = useState(null);
  // 建立消息通道
  const wsRef = useRef(null);

  // 初始化ws链接
  const initWsContent = () => {
    let uuid = getUrlParameter("key");
    let wsUrl = window.location.origin + "/api/ws-genius?uuid=" + uuid;

    wsRef.current = new SockJS(wsUrl);
    // 建立链接
    wsRef.current.onopen = () => {
      console.log("建立连接成功");
    };

    // 消息返回
    wsRef.current.onmessage = (e) => {
      console.log("Received: " + e.data);
      watchEventSource(e);
    };

    // 消息关闭 断线重连
    wsRef.current.onclose = () => {
      console.log("Connection closed");
      if (currentAttempt.current < maxReconnectAttempts.current) {
        setTimeout(initWsContent, reconnectInterval.current); // 延迟一段时间后尝试重新连接
        currentAttempt.current++;
      } else {
        console.log("Max reconnection attempts reached"); // 达到最大重连次数后停止重连并打印错误信息
      }
    };
  };

  // 关闭链接
  const disconnect = () => {
    if (wsRef.current != null) {
      wsRef.current.close();
      wsRef.current = null;
    }
  };
  // 发送消息
  const sendMessage = (fastQuestionValue) => {
    if (wsRef.current != null) {
      wsRef.current.send(fastQuestionValue || userQuestion);
    }
  };

  // 发送消息
  const sendQuestion = (fastQuestionValue) => {
    try {
      if (!userQuestion && !fastQuestionValue) return;
      // 清空输入框
      setUserQuestion("");
      // 发送消息为回复前需要将状态设置为不发发送
      setQuestionState(true);
      // 用户信息
      let useQuestionTemp = {
        content: fastQuestionValue || userQuestion,
        chatRole: "user",
        reqId: new Date().getTime() + "-" + "user",
        createTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"),
      };
      // 消息模板
      messageTemplate.current = {
        content: "",
        reqId: new Date().getTime() + "-" + "question",
        chatRole: "assistant",
        createTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"),
      };
      setSessionDetailList([
        ...sessionDetailList,
        useQuestionTemp,
        messageTemplate.current,
      ]);
      sessionDetailListRef.current = [
        ...sessionDetailList,
        useQuestionTemp,
        messageTemplate.current,
      ];

      // 始终让返回处于底部
      mackSessionScrollToBottom();

      sendMessage(fastQuestionValue);
    } catch (error) {
      setQuestionState(false);
      console.log(error);
    }
  };

  // 监听消息返回
  const watchEventSource = (event) => {
    console.log(event, "eventevent");
    if (event.data === "DONE") {
      setQuestionState(false);
      return;
    } else {
      let resData = JSON.parse(event.data);
      let str = "";
      if (resData.code) {
        str = str + resData?.message || "";
        setQuestionState(false);
      } else {
        str = str + resData?.content || "";
      }

      messageTemplate.current.content = messageTemplate.current.content + str;
      setSessionDetailList([
        ...sessionDetailListRef.current.slice(0, -1),
        messageTemplate.current,
      ]);
    }
    // 始终让返回处于底部
    mackSessionScrollToBottom();
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

  /**
   * @description: 复制会话内容
   * @return {*}
   * @author: jl.g
   */
  const copyToClipboard = (e, data) => {
    e.stopPropagation();
    copyToClipboardFn(data, "复制内容成功");
  };

  // 获取欢迎词和菜单设置
  const getWelcomeAndMenusFn = async () => {
    try {
      let uuid = getUrlParameter("key");
      let res = await getWelcomeAndMenus(uuid);
      if (res.code === 200) {
        const { welcome } = res.result;
        if (welcome) {
          messageTemplate.current.content = welcome;
        }
        setWelcomeInfo(res.result);
        setSessionDetailList([messageTemplate.current]);
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 通过菜单设置 发送消息 此类消息不进行接口调用
  const menusDetailsSendQuestion = (data) => {
    if (questionState) return;
    // 清空输入框
    setUserQuestion("");

    let useQuestion = {
      content: data.prompt,
      chatRole: "user",
      reqId: new Date().getTime() + "-" + "user",
      createTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"),
    };

    // 消息模板
    messageTemplate.current = {
      content: data.content,
      reqId: new Date().getTime() + "-" + "question",
      chatRole: "assistant",
      createTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"),
    };

    setSessionDetailList([
      ...sessionDetailList,
      useQuestion,
      messageTemplate.current,
    ]);
    sessionDetailListRef.current = [
      ...sessionDetailList,
      useQuestion,
      messageTemplate.current,
    ];

    // 始终让返回处于底部
    mackSessionScrollToBottom();
  };

  useEffect(() => {
    async function asyncFn() {
      // 获取欢迎语配置
      await getWelcomeAndMenusFn();
      // 初始化ws
      await initWsContent();
    }
    asyncFn();
    return () => {
      disconnect();
    };
  }, []);

  return (
    <div className={styles.knowledgeChatWarp}>
      <div className="knowledge_content">
        <div className="konwledge_chat-content" ref={chatBodyRef}>
          {sessionDetailList.map((v, index) => (
            <div key={v.reqId}>
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
                        <>
                          <Markdown content={v.content}></Markdown>

                          {index === 0 ? (
                            <div className="quickQusetionList">
                              {welcomeInfo.fastPrompts
                                ? welcomeInfo.fastPrompts.map((v, index) => (
                                    <div
                                      onClick={() => sendQuestion(v)}
                                      key={index}
                                      className="quickQusetion_item"
                                    >
                                      {v}
                                    </div>
                                  ))
                                : ""}
                            </div>
                          ) : (
                            ""
                          )}
                        </>
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

        {welcomeInfo?.menusDetails?.length ? (
          <div className="project_menu">
            {welcomeInfo.menusDetails.map((v, index) => (
              <span
                onClick={() => menusDetailsSendQuestion(v)}
                className="menusDetailsItem"
                key={index}
                color="#55acee"
              >
                {v.prompt}
              </span>
            ))}
          </div>
        ) : (
          ""
        )}
      </div>
      <div className="knowledge_bottom-chat">
        <Input
          disabled={questionState}
          onPressEnter={() => sendQuestion()}
          value={userQuestion}
          onInput={(e) => setUserQuestion(e.target.value)}
          placeholder="请输入搜索内容"
        ></Input>
        <Button
          disabled={questionState}
          style={{ marginLeft: 10 }}
          type="primary"
          shape="circle"
          icon={<SearchOutlined />}
          onClick={() => sendQuestion()}
        />
      </div>
    </div>
  );
};

export default KnowledgeChat;
