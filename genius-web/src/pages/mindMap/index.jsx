/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-08-24 11:21:22
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-02-28 22:04:56
 */
import React, { useState, useRef, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import templateIcon from "../../../public/assets/mindMap/template.svg";
import sendIcon from "../../../public/assets/mindMap/send.svg";
import { getCookie, backLogoinInfo } from "@/utils";
import ReactMarkMap from "./ReactMarkMap";
import { accountChangeAction } from "@/store/actions/home_action";
import { rganizeResultsByCode } from "@/pages/Home/events";
import { createSession } from "@/api/gpt";
import ConsumeAmount from "@/components/ConsumeAmount";
import _ from "lodash";
import styles from "./index.module.less";
const MindMap = () => {
  const dispatch = useDispatch();
  // 基础问题模板
  const qsTmp = useRef(
    "接下来我将发送给你一个主题,请以我的主题作为标题,详细的生成思维导图,导图的内容必须进行详细的分析每一个节点,请注意结果要用Markdown语法进行返回。接下来我的第一个主题是:"
  );
  // 用户消耗额度
  const userConsumer = useSelector((state) => state.userConsumer);

  // 提问状态
  const [questionState, setQuestionState] = useState(false);
  // 提问的内容
  const [userQuestion, setUserQuestion] = useState("");
  // 消息流返回数据存储
  const eventSourceRef = useRef(null);
  //   markDown结果
  const [resultMarkdown, setResultMarkdown] = useState("");
  const resultMarkdownRef = useRef("");

  // 账户消耗ref
  const consumeAmountRef = useRef(null);
  // 发送问题
  const sendQuestion = () => {
    if (!userQuestion) return;
    createSessionFn();
  };

  // 创建会话
  /**
   * @description: 新建会话
   * @return {*}
   * @author: jl.g
   */
  const createSessionFn = async () => {
    try {
      let data = {
        roleId: -1,
        productType: 1,
      };
      let res = await createSession(data);
      if (res.code === 200) {
        const { result } = res;

        initEventSource(result);
        watchEventSource();
      } else if (res.code === 4002) {
        backLogoinInfo();
      }
      console.log(res);
    } catch (error) {
      console.log(error);
    }
  };

  // 建立消息流
  const initEventSource = (reqId) => {
    // 清空上一次的markDown内容
    setResultMarkdown("");
    // 设置禁止发送
    setQuestionState(true);
    // 清空消息模板
    resultMarkdownRef.current = "";

    let tokenName = getCookie("tokenName");
    let tokenValue = getCookie("tokenValue");
    let questionUrl = window.location.origin;
    // let questionUrl = "https://chat.huanbodadi.com";
    eventSourceRef.current = new EventSource(
      `${questionUrl}/api/chat/questions?prompt=${encodeURIComponent(
        qsTmp.current + userQuestion
      )}&reqId=${reqId}&${tokenName}=Bearer ${tokenValue}&productType=${1}&logType=MIND_MAP`
    );

    // 建立消息通道后 清空输入框
    setUserQuestion("");
  };

  // 监听消息返回
  const watchEventSource = () => {
    eventSourceRef.current.onmessage = function (event) {
      if (event.data === "DONE") {
        closeAnswer();
        // 回复完毕开发发送
        setQuestionState(false);

        // 回答完毕开始查询剩余账户
        dispatch(
          accountChangeAction(new Date().getTime() + "_" + Math.random())
        );
      } else {
        let resData = JSON.parse(event.data);
        let str = "";
        // 提问异常处理
        rganizeResultsByCode(resData, setQuestionState, closeAnswer);

        if (resData.code) {
          str = `# ${str + resData?.message || ""}`;
        } else {
          str = str + resData?.content || "";
        }
        resultMarkdownRef.current = resultMarkdownRef.current + str;

        setResultMarkdown(resultMarkdownRef.current);
      }
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
      setQuestionState(false);
    }
  };

  //   回车
  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      // 处理输入值...
      sendQuestion();
    }
  };

  useEffect(()=>{
    if(userConsumer?.realAmount){
      consumeAmountRef.current.consumeAmountFn(userConsumer?.realAmount);
    }
  },[userConsumer?.realAmount])

  return (
    <div className={styles.mindMapWarp}>
      <div className="mindMap_content">
        <ReactMarkMap
          questionState={questionState}
          resultMarkdown={resultMarkdown}
        ></ReactMarkMap>
      </div>

      <div className="mindMap_answer">
       
        {questionState ? (
          <div className="geneTextBox">Ai正在努力创作中,请稍后...</div>
        ) : (
          <input
            onKeyDown={_.debounce(handleKeyPress, 500)}
            disabled={questionState}
            placeholder="输入您的想法,Ai将会自动生成思维导图"
            className="answer_input"
            type="text"
            name=""
            value={userQuestion}
            onChange={(e) => setUserQuestion(e.target.value)}
          />
        )}

        <img
          disabled={questionState}
          onClick={_.debounce(sendQuestion, 500)}
          className="answer_send"
          src={sendIcon}
          alt=""
        />

        {/* 金币消耗 */}
        <ConsumeAmount ref={consumeAmountRef}></ConsumeAmount>
      </div>
    </div>
  );
};

export default MindMap;
