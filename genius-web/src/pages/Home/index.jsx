/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2022-10-12 13:45:29
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-04-08 14:47:56
 */
import React, { useState, useRef, useEffect } from "react";
import moment from "moment";
import { useSpeechSynthesis } from "react-speech-kit";
import styles from "./index.module.less";
import gpt3 from "../../../public/assets/imgs/gpt3.png";
import sparkDeskIcon from "../../../public/assets/imgs/sparkDesk.svg";
import gpt4 from "../../../public/assets/imgs/gpt4.png";
import gptsIcon from "../../../public/assets/gpts/gptsIcon.png";
import Send from "@/assets/imgs/icons/send.svg";
import { Markdown } from "@/components/Markdown";
import AssistantsList from "@/components/AssistantsList";
import WebsiteRegistNum from "@/components/WebsiteRegistNum";
import ConsumeAmount from "@/components/ConsumeAmount";
import EditSession from "./EditSession/index";
import createSessionIcon from "@/assets/home/createSessionIcon.png";
import {
  sessionRecordSidebar,
  createSession,
  getSessionDetail,
  removeSession,
  getAdvertiseList,
  productConsumedType,
  removeAllSession,
  downloadReqSession,
  updateSessionInfoByReqIdApi,
  clearSessionFile,
} from "@/api/gpt";

import { productConsumedTypeConfig } from "@/api/user";
import { Button, Popconfirm, Radio, Switch, Tag } from "antd";
import {
  CloseOutlined,
  NotificationOutlined,
  CopyOutlined,
  MenuUnfoldOutlined,
  RedoOutlined,
  CloudDownloadOutlined,
  FormOutlined,
  PaperClipOutlined,
  FileTextOutlined,
} from "@ant-design/icons";
import {
  getCookie,
  copyToClipboardFn,
  messageFn,
  backLogoinInfo,
  setSessionStorage,
} from "@/utils";
import _ from "lodash";
import Loading from "@/components/Loading";
import { useSelector, useDispatch } from "react-redux";
import { useLocation } from "react-router-dom";
import userDeafultImg from "../../../public/assets/imgs/userDeafultImg.png";
import wenxinRobin from "../../../public/assets/imgs/robin.png";
import UploadFile from "./UploadFile";
import {
  createSessionFnAction,
  accountChangeAction,
  setActiveMenuItemKey,
} from "@/store/actions/home_action";
import Marquee from "react-fast-marquee";
import { autoAnswerQuestion, rganizeResultsByCode } from "./events";
const Home = () => {
  // 消息模板
  let messageObj = {
    reqId: "",
    content: "",
    chatRole: "",
    createTime: "",
  };
  // 会话名称编辑
  const editSessionRef = useRef(null);
  // 账户消耗ref
  const consumeAmountRef = useRef(null);
  // 用户消耗额度
  const userConsumer = useSelector((state) => state.userConsumer);
  // 是否联网
  const [internetState, setInternetState] = useState(false);

  // 用户信息列表
  const userInfo = useSelector((state) => state.userInfo);
  const sysConfig = useSelector((state) => state.sysConfig);
  const dispatch = useDispatch();
  const location = useLocation();

  const [productInfo, setProductInfo] = useState("");

  // 上传附件
  const uploadFileRef = useRef(null);

  // 角色数据
  const resAiAssistantRoleData = useRef(location.state?.roleData);

  // 应用广场携带过来的模型类型
  const productTypeFromAiAssistant = useRef(
    location.state?.productConsumedTypeitem
  );

  // 会话列表
  const [sessionList, setSessionList] = useState([]);
  const sessionListRef = useRef(null);
  // 当前会话
  const [activeSession, setActiveSessions] = useState(null);

  // 消息列表
  const [messageList, setMessageList] = useState([]);
  const messageRefList = useRef([]);

  // 消息回答状态
  const [messageState, setMessageState] = useState(false);

  // 提问框文本
  const userQuestionInputRef = useRef(null);
  // 当前提问下标
  const activeChatIndex = useRef(0);

  // 问题回答提示框ref
  const chatBodyRef = useRef(null);

  // 长连接EventSource ref
  const eventSourceRef = useRef(null);

  // 移动端兼容适配

  // 回答选择和会话详情显示
  // 0代表选择会话 1代表会话详情
  const [sessionType, setSessionType] = useState("1");

  // 广告列表
  const [advList, setAdvList] = useState([]);

  // 小助手
  const assistantsListRef = useRef(null);

  // 朗读
  const { speak, cancel, speaking } = useSpeechSynthesis();

  // 获取模型类型
  const [productConsumedTypeList, setProductConsumedTypeList] = useState([]);

  const productConsumedTypeListRef = useRef([]);

  // 选择的模型
  const [productConsumedTypeitem, setProductConsumedTypeitem] = useState(null);

  // radio类型
  const [radioTypeKey, setRadioTypeKey] = useState(null);

  // 获取商品消耗提示语字典
  const consumptionPrompt = useSelector((state) => state.consumptionPrompt);

  /**
   * @description: 回车提问
   * @param {*} e
   * @return {*}
   * @author: jl.g
   */
  document.onkeydown = (e) => {
    if (e.keyCode === 13) {
      e.preventDefault(); //禁止回车的默认换行
    }
    const enterAnswer = () => {
      // 回车提交表单
      // 兼容FF和IE和Opera
      var theEvent = window.event || e;
      var code = theEvent.keyCode || theEvent.which || theEvent.charCode;
      if (code === 13 && e.altKey) {
        e.preventDefault();
        let textAreaValue = userQuestionInputRef.current.value;
        let newTextAreaValue = textAreaValue + "\n";
        // 设置换行
        userQuestionInputRef.current.value = newTextAreaValue;
      } else if (e.keyCode === 13) {
        userAnswer();
      }
    };

    _.debounce(enterAnswer, 500)();
  };

  // 获取模型类型
  const getProductConsumedTypeList = async () => {
    try {
      let res = await productConsumedType();
      if (res.code === 200) {
        let resData = res.result;
        // 问答模型 1 3.5  2 gpt4  4 文心  7 星火
        resData = resData.filter(
          (v) => v.key === 1 || v.key === 2 || v.key === 4 || v.key === 7
        );
        setProductConsumedTypeList(resData || []);

        productConsumedTypeListRef.current = res.result;
        if (resData.length) {
          // 设置默认的模型类型
          setRadioTypeKey(resData[0].key);

          // 判断是应用广场跳转还是聊天模块创建的
          if (location.state?.productConsumedTypeitem) {
            setProductConsumedTypeitem(location.state?.productConsumedTypeitem);
            productTypeFromAiAssistant.current =
              location.state?.productConsumedTypeitem;

            console.log(
              location.state?.productConsumedTypeitem,
              "productTypeFromAiAssistantproductTypeFromAiAssistant"
            );
          } else {
            setProductConsumedTypeitem(resData[0]);
            productTypeFromAiAssistant.current = resData[0];
          }
        }
      } else {
        setProductConsumedTypeList([]);
      }
    } catch (error) {
      console.log(error);
    }
  };

  /**
   * @description:
   * @return {*}
   * @author: jl.g
   */
  // getAdvertiseList
  const getAdvList = async () => {
    try {
      let res = await getAdvertiseList();
      if (res.code === 200) {
        setAdvList(res.result || []);
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 删除文件
  const handlerOnDeleteFile = async (e) => {
    e.preventDefault();

    try {
      let res = await clearSessionFile(
        activeSession?.reqId,
        activeSession?.fileId
      );
      if (res.code === 200) {
        updateSessionInfoByReqId();
      }
    } catch (error) {}
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
   * @description: 提问
   * @return {*}
   * @author: jl.g
   */
  const userAnswer = () => {
    if (messageState) {
      // messageF
      messageFn({
        type: "info",
        content: "正在回答您的问题,请稍后在提问,或停止当前的回答,再次提问",
      });
      return;
    }

    setMessageState(true);

    let userQuestion = userQuestionInputRef.current.value;

    if (!userQuestion) return;

    // 文心模型需要限制发送文字2000
    if (productConsumedTypeitem?.key === 4) {
      if (userQuestion.length >= 2000) {
        messageFn({
          type: "info",
          content:
            "文心千帆模型支持的最大token长度为2000,请修改提问内容长度后再次尝试.",
        });
        return;
      }
    }

    // 如果当前的模型被下线其使用当前模型的会话不允许进行提问
    if (!productTypeFromAiAssistant.current) {
      messageFn({
        type: "info",
        content: "当前模型异常，请新建会话后进行使用",
      });
      return;
    }

    // 设置滚动条在页面最底部
    mackSessionScrollToBottom();
    // 用户提问
    let userQuestionData = _.cloneDeep(messageObj);
    userQuestionData.content = userQuestion;
    userQuestionData.reqId = new Date().getTime() + "_user";
    userQuestionData.createTime = moment(new Date()).format(
      "YYYY-MM-DD HH:mm:ss"
    );
    userQuestionData.chatRole = "user";
    messageRefList.current.push(userQuestionData);
    // ai 回答
    let assistantAnswer = _.cloneDeep(messageObj);
    assistantAnswer.reqId = new Date().getTime() + "_assistantAnswer";
    assistantAnswer.createTime = moment(new Date()).format(
      "YYYY-MM-DD HH:mm:ss"
    );
    assistantAnswer.chatRole = "assistant";
    messageRefList.current.push(assistantAnswer);
    setMessageList([...messageRefList.current]);

    // 找到当前需要回答问题的数据 更新数据
    activeChatIndex.current = messageRefList.current.length - 1;

    let str = "";
    let tokenName = getCookie("tokenName");
    let tokenValue = getCookie("tokenValue");

    // 清空提问框
    userQuestionInputRef.current.value = "";
    // 联网
    let internetUrl = "";
    if (
      productTypeFromAiAssistant.current?.key === 1 ||
      productTypeFromAiAssistant.current?.key === 2
    ) {
      internetUrl = `&internet=${internetState}`;
    }

    let questionUrl = window.location.origin;
    // gpts
    let model =
      productTypeFromAiAssistant.current?.gpts?.gid ||
      activeSession?.model ||
      "";
    // productConsumedTypeitem
    eventSourceRef.current = new EventSource(
      `${questionUrl}/api/chat/questions?prompt=${encodeURIComponent(
        userQuestion
      )}&reqId=${
        activeSession?.reqId
      }&${tokenName}=Bearer ${tokenValue}&productType=${
        productTypeFromAiAssistant.current?.key
      }${internetUrl}&model=${model}&logType=CHAT`
    );

    try {
      eventSourceRef.current.onmessage = function (event) {
        setMessageState(true);
        // 设置滚动条在页面最底部
        mackSessionScrollToBottom();
        if (event.data === "DONE") {
          setMessageState(false);
          closeAnswer();

          // 对话延迟一秒后在刷新
          setTimeout(() => {
            getSessionRecordSidebar();
          }, 1000);

          // 提问接触开始查询剩余账户
          dispatch(
            accountChangeAction(new Date().getTime() + "_" + Math.random())
          );
        } else {
          let resData = JSON.parse(event.data);

          // 提问异常处理
          rganizeResultsByCode(resData, setMessageState, closeAnswer);

          if (resData.code) {
            str = str + resData?.message || "";
          } else {
            str = str + resData?.content || "";
          }

          let activeChat = messageRefList.current[activeChatIndex.current];

          if (activeChat) {
            activeChat.content = str ? str : "";
            messageRefList.current.splice(
              activeChatIndex.current,
              1,
              activeChat
            );

            setMessageList([...messageRefList.current]);
          }
        }
      };

      eventSourceRef.current.onerror = (event) => {
        console.log(event, "出错了");
        let resData = {
          code: 500,
          message: "",
        };
        // 提问异常处理
        rganizeResultsByCode(resData, setMessageState, closeAnswer);

        fillChatMessage(resData, str);
      };
    } catch (error) {
      console.log(error, "500");
      let resData = {
        code: 500,
        message: "",
      };
      // 提问异常处理
      rganizeResultsByCode(resData, setMessageState, closeAnswer);

      fillChatMessage(resData, str);
    }
  };

  // 对话数据填充
  const fillChatMessage = (resData, str) => {
    if (resData.code) {
      str = str + resData?.message || "";
    } else {
      str = str + resData?.content || "";
    }

    let activeChat = messageRefList.current[activeChatIndex.current];

    if (activeChat) {
      activeChat.content = str ? str : "";
      messageRefList.current.splice(activeChatIndex.current, 1, activeChat);

      setMessageList([...messageRefList.current]);
    }
  };

  /**
   * @description: 停止回答问题
   * @return {*}
   * @author: jl.g
   */

  const closeAnswer = (handlerType) => {
    if (eventSourceRef.current) {
      eventSourceRef.current.close();

      if (handlerType) {
        messageFn({
          type: "success",
          content: "Genius 已停止当前会话的回复",
        });
        setMessageState(false);
      }
    }
  };

  /**
   * @description: 获取会话列表
   * @return {*}
   * @author: jl.g
   */
  const getSessionRecordSidebar = async () => {
    try {
      let params = {
        logType: "CHAT",
      };
      let res = await sessionRecordSidebar(params);

      if (res.code === 200) {
        // 第一次刷新左侧列表要默认展示一条会话详细内容
        if (res.result.length) {
          setSessionList(res.result || []);
          sessionListRef.current = res.result || [];

          if (!activeSession) {
            let firstItem = res.result[0];
            setActiveSessions(firstItem);

            if (!location.state?.productConsumedTypeitem) {
              // 查询当前会话的模型
              let activeModel = productConsumedTypeListRef.current.find(
                (v) => v.key === firstItem.productType
              );

              setProductConsumedTypeitem(activeModel);
              productTypeFromAiAssistant.current = activeModel;
            }

            // 如果是从应用广场进行的会话创建 则不需需要默认查询第一个会话的具体详情
            if (!resAiAssistantRoleData.current) {
              getSessionDetailFn(res.result[0]);
            }
          }
        } else {
          setSessionList([]);
          sessionListRef.current = []; // 清空列表展示选择展示选项选择的选项卡选项卡选择选项卡
          // 如果查询会话列表为空则需要清除右侧对话
          setMessageList([]);

          messageRefList.current = [];
        }

        await createSessionByAiAssistant();
      }
    } catch (error) {
      console.log(error);
    }
  };

  /**
   * @description: 新建会话
   * @return {*}
   * @author: jl.g
   */
  const createSessionFn = async (roleData) => {
    try {
      let data = {
        productType: productTypeFromAiAssistant.current?.key,
        sessionName:
          roleData?.roleName || "新的会话" + (sessionList?.length + 1),
        model: productTypeFromAiAssistant.current?.gpts?.gid || "",
        sessionDesc: roleData?.roleDesc,
        logType: "CHAT",
      };
      // 只有gpt传roleId
      if (
        productTypeFromAiAssistant.current?.key === 1 ||
        productTypeFromAiAssistant.current?.key === 2
      ) {
        data.roleId = roleData?.id;
      }

      // 新增规则 增加传参用于增强gpts 与 角色
      // private String sessionName;
      // private String model;
      // private String sessionDesc;

      let res = await createSession(data);
      if (res.code === 200) {
        const { result } = res;
        let sessionItem = {
          reqId: result,
          sessionName:
            roleData?.roleName || "新的会话" + (sessionList?.length + 1),
          productType: productTypeFromAiAssistant.current?.key,
          roleId: roleData?.id || -1,
        };

        // 停止消息接收
        closeAnswer();
        // 关闭停止会话按钮
        setMessageState(false);
        // 清空消息列表
        messageRefList.current = [];
        // 初次创建会话默认发送固定消息
        messageRefList.current.push(autoAnswerQuestion(roleData, messageObj));
        setMessageList([...messageRefList.current]);

        // 设置会话高亮
        setActiveSessions(sessionItem);

        let sessionListCopyData = _.cloneDeep(sessionListRef.current || []);
        sessionListRef.current = [sessionItem, ...sessionListCopyData];
        setSessionList([sessionItem, ...sessionListCopyData]);

        setSessionType("1");
      } else if (res.code === 4002) {
        backLogoinInfo();
      }
      console.log(res);
    } catch (error) {
      console.log(error);
    }
  };

  // 删除全部会话
  const removeAllSessionFn = async () => {
    if (messageList.length === 1 && messageRefList.current.length === 1) return;
    try {
      let res = await removeAllSession();
      if (res.code === 200) {
        // 停止消息接收
        closeAnswer();
        // 关闭停止会话按钮
        setMessageState(false);
        // 清空消息列表
        messageRefList.current = [];
        setMessageList([]);

        // 清空会话列表
        setSessionList([]);
        sessionListRef.current = [];
      }
    } catch (error) {
      console.log(error);
    }
  };

  /**
   * @description: 选择会话查询
   * @return {*}
   * @author: jl.g
   */

  const chooseSession = (v) => {
    // 禁止同一个会话每次点击查询详情
    if (v.reqId === activeSession.reqId) return;

    setActiveSessions(v);
    getSessionDetailFn(v);
    setSessionType("1");

    // 设置模型
    let activeModel = productConsumedTypeListRef.current.find(
      (item) => item.key === v.productType
    );

    setProductConsumedTypeitem(activeModel);
    productTypeFromAiAssistant.current = activeModel;
  };

  /**
   * @description: 查询当前会话的具体内容
   * @return {*}
   * @author: jl.g
   */
  const getSessionDetailFn = async ({ reqId }) => {
    try {
      let params = {
        reqId,
      };
      let res = await getSessionDetail(params);
      if (res.code === 200) {
        setMessageList(res.result || []);
        messageRefList.current = res.result || [];
        mackSessionScrollToBottom();
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 编辑会话名称
  const editSessionFn = (e, data) => {
    // updateSessionApi
    editSessionRef.current.getPage(data);
  };

  /**
   * @description: 删除会话
   * @return {*}
   * @author: jl.g
   */
  const deleteSessionFn = async (e, { reqId }) => {
    e.stopPropagation();
    try {
      let params = {
        reqId,
      };
      let res = await removeSession(params);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "会话删除成功",
        });

        let deletedSessionList = sessionList.filter(
          (v) => v.reqId !== reqId && v.chatRole !== "user"
        );

        if (deletedSessionList.length) {
          setActiveSessions(deletedSessionList[0]);
          getSessionDetailFn(deletedSessionList[0]);

          // 删除数据后 设置模型类型
          let activeItem = productConsumedTypeList.find(
            (v) => v.key === deletedSessionList[0].productType
          );

          // productTypeFromAiAssistant
          setProductConsumedTypeitem(activeItem);
          productTypeFromAiAssistant.current = activeItem;
        } else {
          setActiveSessions(null);
          setSessionList([]);

          // 如果删除会话列表全部内容 则重置为空
          setProductConsumedTypeitem(null);
          productTypeFromAiAssistant.current = null;
        }
        getSessionRecordSidebar();
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

  /**
   * @description: 复制会话内容
   * @return {*}
   * @author: jl.g
   */
  const copyToClipboard = (e, data) => {
    e.stopPropagation();
    copyToClipboardFn(data, "复制内容成功");
  };

  // 用户重试
  const userRetry = (e, data) => {
    console.log(data, "222");
    userQuestionInputRef.current.value = data;
    userAnswer();
  };

  // 打开广告
  const openAdv = (data) => {
    window.open(data.advertiseLink);
  };

  // 朗读文本
  const readAloudText = (chatItem) => {
    // 如果播放则关闭
    if (speaking) {
      cancel();
      return;
    }
    speak({
      text: chatItem.content,
    });
  };

  // 通过应用广场创建会话
  const createSessionByAiAssistant = async () => {
    if (!resAiAssistantRoleData.current) return;
    await createSessionFn(resAiAssistantRoleData.current);
    // 设置菜单选中聊天模块
    dispatch(setActiveMenuItemKey(1));
    setSessionStorage("activeMenuItemKey", 1);

    // 创建会话完成 删除从应用广场携带过来的数据
    resAiAssistantRoleData.current = null;
  };

  // 导出聊天记录
  const downloadReqSessionFn = async (data) => {
    try {
      let params = {
        reqId: activeSession.reqId,
      };
      let response = await downloadReqSession(params);
      const url = window.URL.createObjectURL(new Blob([response]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `${activeSession.content}.md`); // 设置要保存的文件名及扩展名
      document.body.appendChild(link);
      link.click();
    } catch (error) {
      console.log(error);
    }
  };

  // 获取商品消耗提示
  const getProductConsumedTypeConfig = async () => {
    try {
      let params = {
        productType: productTypeFromAiAssistant.current?.key,
      };
      let res = await productConsumedTypeConfig(params);
      if (res.code === 200) {
        setProductInfo(res.result);
        // dispatch(consumptionPromptAction(res.result));
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 上传附件
  const handlerUploadSessionFile = () => {
    uploadFileRef.current.getPage();
  };
  // 更新当前会话
  const updateSessionInfoByReqId = async () => {
    try {
      let res = await updateSessionInfoByReqIdApi(activeSession?.reqId);
      if (res.code === 200) {
        let newData = res.result;
        let copySessionList = _.cloneDeep(sessionList);
        let sessionIndex = copySessionList.findIndex(
          (v) => v.reqId === newData.reqId
        );

        copySessionList.splice(sessionIndex, 1, newData);
        setSessionList(copySessionList);
        setActiveSessions(newData);
      } else {
        messageFn({
          type: "error",
          content: res.message,
        });
      }
    } catch (error) {}
  };
  // 修改文件id
  const changeSessionUploadFileId = async () => {
    await updateSessionInfoByReqId();
  };

  // 修改联网状态
  const chooseInternetState = (state) => {
    /* 只有不带任何角色的 且为gpt3 或者 gpt4的才可以联网 */
    if (
      activeSession?.roleId === -1 &&
      (activeSession?.productType === 1 || activeSession?.productType === 2)
    ) {
      setInternetState(state);
    } else {
      messageFn({
        type: "error",
        content: "联网功能只支持自定会话且只支持AI3.5和AI4",
      });
    }
  };
  useEffect(() => {
    // 如果会话列表为空
    // 且不是从应用广场跳转过来的
    if (
      sessionList?.length === 0 &&
      sessionListRef.current &&
      !resAiAssistantRoleData.current
    ) {
      createSessionFn();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sessionList]);

  useEffect(() => {
    async function asyncFn() {
      try {
        // 获取模型类型列表
        await getProductConsumedTypeList();

        // 查询左侧会话
        await getSessionRecordSidebar();

        // 查询广告
        await getAdvList();
        // 将创建会话方法注入到store中
        dispatch(createSessionFnAction(createSessionFn));
      } catch (error) {}
    }
    asyncFn();

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // useEffect(() => {
  //   if (productTypeFromAiAssistant.current) {
  //     getProductConsumedTypeConfig();
  //   }
  // }, [productTypeFromAiAssistant.current]);

  useEffect(() => {
    if (activeSession) {
      setInternetState(false);
    }
  }, [activeSession]);

  useEffect(() => {
    if (userConsumer?.realAmount) {
      consumeAmountRef.current.consumeAmountFn(userConsumer?.realAmount);
    }
  }, [userConsumer?.realAmount]);

  return (
    <div className={styles.home_container_warp}>
      <div className="home_container_pc">
        {/* 对话区域 */}

        <div className="home_container-warp">
          <div className="home_container">
            {/* 移动端蒙层 */}
            {sessionType === "0" ? (
              <div
                onClick={() => setSessionType("1")}
                className="mobile-smegma"
              ></div>
            ) : (
              ""
            )}

            <div
              className={`home_sidebar ${
                sessionType === "0" ? "sessionShow" : ""
              }`}
            >
              {/* 移动端会话关闭按钮 */}
              {sessionType === "0" ? (
                <CloseOutlined
                  onClick={() => setSessionType("1")}
                  className="mobile_smegma-close"
                  twoToneColor="#fff"
                ></CloseOutlined>
              ) : (
                ""
              )}

              <div className="home_sidebar-header">
                <div className="home_sidebar-title">选择会话</div>

                <Popconfirm
                  size="small"
                  placement="top"
                  title={
                    <div>
                      <p>模型选择</p>
                      <div>
                        <Radio.Group
                          onChange={(e) => {
                            // let value = e.target.value;
                            // let activeItem = productConsumedTypeList.find(
                            //   (v) => v.key === value
                            // );
                            // setProductConsumedTypeitem(activeItem);

                            // productTypeFromAiAssistant.current = activeItem;

                            setRadioTypeKey(e.target.value);
                          }}
                          value={radioTypeKey}
                        >
                          {productConsumedTypeList.map((v) => (
                            <Radio key={v.key} value={v.key}>
                              {v.value}
                            </Radio>
                          ))}
                        </Radio.Group>
                      </div>
                    </div>
                  }
                  onConfirm={() => {
                    let activeItem = productConsumedTypeList.find(
                      (v) => v.key === radioTypeKey
                    );
                    setProductConsumedTypeitem(activeItem);

                    productTypeFromAiAssistant.current = activeItem;

                    createSessionFn();
                  }}
                  okText="确认"
                  cancelText="取消"
                >
                  <div className="home-chatSession-create">
                    <img
                      className="createSessionIcon"
                      src={createSessionIcon}
                      alt=""
                    />
                    新建会话
                  </div>
                </Popconfirm>
              </div>

              <div className="home_sidebar-body">
                {sessionList.map((v) => (
                  <div
                    onClick={() => chooseSession(v)}
                    key={v.reqId}
                    className={`home_chat-item ${
                      activeSession?.reqId === v.reqId
                        ? "home_chat-item-selected"
                        : ""
                    }`}
                  >
                    <div className="home_chat-item-title-warp">
                      {v?.productType === 4 ? (
                        <img
                          className="home_chat-item-icon"
                          src={wenxinRobin}
                          alt=""
                        />
                      ) : v?.productType === 7 ? (
                        <img
                          className="home_chat-item-icon"
                          src={sparkDeskIcon}
                          alt=""
                        />
                      ) : v?.productType === 1 ? (
                        <img
                          className="home_chat-item-icon"
                          src={gpt3}
                          alt=""
                        />
                      ) : v?.productType === 2 ? (
                        <img
                          className="home_chat-item-icon"
                          src={gpt4}
                          alt=""
                        />
                      ) : v?.productType === 9 ? (
                        <img
                          className="home_chat-item-icon"
                          src={gptsIcon}
                          alt=""
                        />
                      ) : (
                        ""
                      )}

                      <div className="home_chat-item-title">
                        {v.sessionName}
                      </div>
                    </div>

                    <div className="home_chat-item-info">
                      {/* <div>{v.reqCount ? v.reqCount : 0} 条对话</div> */}
                      <div>
                        {v.createTime
                          ? v.createTime
                          : moment(new Date()).format("YYYY-MM-DD HH:mm:ss")}
                      </div>
                      {/* {
                        // 只有gpt才能上传文件,没有文件才能上传
                        (v.productType === 1 || v.productType === 2) &&
                        !v.fileId ? (
                          <CloudUploadOutlined
                            onClick={handlerUploadSessionFile}
                            style={{ fontSize: 16 }}
                          />
                        ) : (
                          ""
                        ) 
                      } */}
                    </div>

                    {/* 最后一条会话禁止删除 */}

                    {sessionList.length > 1 ? (
                      <div className="home_chat-item-delete">
                        <CloseOutlined
                          onClick={(e) => deleteSessionFn(e, v)}
                        ></CloseOutlined>
                      </div>
                    ) : (
                      ""
                    )}

                    <div className="home_chat-item-edit">
                      <FormOutlined
                        onClick={(e) => editSessionFn(e, v)}
                      ></FormOutlined>
                    </div>
                  </div>
                ))}
              </div>
              <div className="home_sidebar-tail">
                <Popconfirm
                  title="是否删除全部会话?"
                  onConfirm={() => removeAllSessionFn()}
                >
                  <div className="clearSession">清空会话</div>
                </Popconfirm>
              </div>
            </div>
            <div className="home_window-content">
              <div className="home_chat">
                <div className="home_window-header">
                  <div className="home_window-header-title">
                    <MenuUnfoldOutlined
                      className="mobile_goback-sesion"
                      onClick={() => setSessionType("0")}
                    ></MenuUnfoldOutlined>

                    <div className="home_window-header-main-title">
                      {activeSession?.sessionName}
                    </div>
                  </div>
                </div>
                <div className="home_chat-body" ref={chatBodyRef}>
                  {messageList.map((chatItem, index) => (
                    <div key={chatItem.reqId + "_" + index}>
                      {chatItem.chatRole === "assistant" ? (
                        <div className="home_chat-message__ai">
                          <div className="home_chat-message-container">
                            <div className="home_chat-message-avatar">
                              {productConsumedTypeitem?.key === 4 ? (
                                <img
                                  className="home_user-avtar"
                                  src={wenxinRobin}
                                  alt=""
                                />
                              ) : productConsumedTypeitem?.key === 7 ? (
                                <img
                                  className="home_user-avtar"
                                  src={sparkDeskIcon}
                                  alt=""
                                />
                              ) : productConsumedTypeitem?.key === 1 ? (
                                <img
                                  className="home_user-avtar"
                                  src={gpt3}
                                  alt=""
                                />
                              ) : productConsumedTypeitem?.key === 2 ? (
                                <img
                                  className="home_user-avtar"
                                  src={gpt4}
                                  alt=""
                                />
                              ) : productConsumedTypeitem?.key === 9 ? (
                                <img
                                  className="home_user-avtar"
                                  src={gptsIcon}
                                  alt=""
                                />
                              ) : (
                                ""
                              )}
                            </div>
                            <div className="home_chat-message-top-actions">
                              <div
                                onClick={() => downloadReqSessionFn(chatItem)}
                                className="home_chat-message-top-action"
                              >
                                <CloudDownloadOutlined />
                                <span className="home_chat-message-top-action-text">
                                  下载
                                </span>
                              </div>

                              <div
                                onClick={() => readAloudText(chatItem)}
                                className="home_chat-message-top-action"
                              >
                                <NotificationOutlined />
                                <span className="home_chat-message-top-action-text">
                                  朗读
                                </span>
                              </div>
                              <div
                                onClick={(e) =>
                                  copyToClipboard(e, chatItem.content)
                                }
                                className="home_chat-message-top-action"
                              >
                                <CopyOutlined />
                                <span className="home_chat-message-top-action-text">
                                  复制
                                </span>
                              </div>
                            </div>
                            <div
                              className="home_chat-message-item"
                              style={{ minWidth: 100 }}
                            >
                              <div className="markdown-body">
                                {chatItem.content ? (
                                  <Markdown
                                    content={
                                      chatItem.content ? chatItem.content : ""
                                    }
                                  ></Markdown>
                                ) : (
                                  <Loading></Loading>
                                )}
                              </div>
                            </div>

                            <div className="home_chat-message-actions">
                              <div className="home_chat-message-action-date">
                                {chatItem.createTime}
                              </div>
                            </div>
                          </div>
                        </div>
                      ) : chatItem.chatRole === "user" ? (
                        <div className="home_chat-message-user">
                          <div className="home_chat-message-container">
                            <div className="home_chat-message-avatar">
                              <img
                                className="home_user-avtar"
                                src={userInfo?.headImgUrl || userDeafultImg}
                                alt=""
                              />
                            </div>

                            <div className="home_chat-message-top-actions">
                              <div
                                onClick={(e) =>
                                  copyToClipboard(e, chatItem.content)
                                }
                                className="home_chat-message-top-action"
                              >
                                <CopyOutlined />
                                <span className="home_chat-message-top-action-text">
                                  复制
                                </span>
                              </div>

                              <div
                                onClick={(e) => userRetry(e, chatItem.content)}
                                className="home_chat-message-top-action"
                              >
                                <RedoOutlined />
                                <span className="home_chat-message-top-action-text">
                                  重试
                                </span>
                              </div>
                            </div>
                            <div className="home_chat-message-item">
                              <div className="home_chat-user-message">
                                {chatItem.content}
                              </div>
                            </div>
                          </div>
                        </div>
                      ) : (
                        ""
                      )}
                    </div>
                  ))}
                </div>
                <div className="home_chat-input-panel">
                  <div
                    style={{ opacity: messageState ? 1 : 0 }}
                    className="home_chat-bottom-tools"
                  >
                    <Button onClick={() => closeAnswer(true)}>停止回答</Button>
                  </div>

                  <div className="home_chat-input-panel-inner">
                    {activeSession?.fileId ? (
                      <div className="sessionFileWarp">
                        <Tag
                          onClose={handlerOnDeleteFile}
                          closable
                          icon={<FileTextOutlined />}
                          color="#55acee"
                        >
                          {activeSession?.fileName}
                        </Tag>
                      </div>
                    ) : (
                      ""
                    )}

                    {/* 金币消耗 */}
                    <ConsumeAmount ref={consumeAmountRef}></ConsumeAmount>

                    <textarea
                      ref={userQuestionInputRef}
                      className="home_chat-input"
                      placeholder="输入消息，点击发送按钮,或按Alt+Enter文本换行,Enter键发送"
                      rows="2"
                    ></textarea>

                    <div
                      className="button_icon-button home_chat-input-send no-dark"
                      onClick={_.debounce(() => userAnswer(), 500)}
                    >
                      <div className="button_icon-button-icon">
                        <img src={Send} alt="" />
                      </div>

                      <div className="button_icon-button-text">发送</div>
                    </div>

                    {/* 自定义会话且gpt3或gpt4 */}
                    {sysConfig?.searchApiEnable &&
                    Number(activeSession?.roleId) === -1 &&
                    (activeSession?.productType === 1 ||
                      activeSession?.productType === 2) ? (
                      <div className="chooseInternet">
                        <Switch
                          onChange={(e) => chooseInternetState(e)}
                          checked={internetState}
                          checkedChildren="关闭"
                          unCheckedChildren="联网"
                        ></Switch>
                      </div>
                    ) : (
                      ""
                    )}

                    {/* gpts 文件上传 */}
                    {activeSession?.productType === 9 &&
                    !activeSession?.fileId ? (
                      <PaperClipOutlined
                        onClick={handlerUploadSessionFile}
                        className="gptsSessionUploadFile"
                      />
                    ) : (
                      ""
                    )}
                  </div>
                  <WebsiteRegistNum></WebsiteRegistNum>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* 广告位 */}
        {/* {advList.length ? (
          <div className="home_container-adv">
            <Carousel className="" autoplay dotPosition="top">
              {advList.map((v) => {
                return (
                  <div key={v.id} className="adv-item">
                    <div className="adv-info">
                      <p className="adv-info-title">{v.advertiseName}</p>
                      <img
                        onClick={() => openAdv(v)}
                        className="advImg"
                        src={v.imgLink}
                        alt=""
                      />
                    </div>
                  </div>
                );
              })}
            </Carousel>
          </div>
        ) : (
          ""
        )} */}
      </div>

      <AssistantsList ref={assistantsListRef} />

      {/* 会话名称编辑 */}
      <EditSession
        getSessionList={getSessionRecordSidebar}
        ref={editSessionRef}
      ></EditSession>

      {/* 上传附件 */}
      <UploadFile
        activeSession={activeSession}
        changeSessionUploadFileId={changeSessionUploadFileId}
        ref={uploadFileRef}
      ></UploadFile>
    </div>
  );
};

export default Home;
