/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-05-27 21:02:31
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-11 13:39:21
 */
import React, { useRef, useState, useEffect } from "react";
import Waterfall from "waterfalljs-layout/react";
import currencyIcon from "../../../../public/assets/imgs/currencyIcon.svg";
import { LazyLoadImage } from "react-lazy-load-image-component";
import "react-lazy-load-image-component/src/effects/blur.css";
import { completionData } from "./completionData";
import { Input, Progress, Tooltip, Popconfirm, Modal } from "antd";
import { CloseOutlined } from "@ant-design/icons";
import { useHistory } from "react-router-dom";
import MjLoading from "@/components/MjLoading";
import {
  getImageList,
  generateImgFn,
  getImageTaskState,
  changeGenerateImg,
  deleteMjGenerateImg,
  publishState,
  getMjInfos,
} from "@/api/midjourney";
import { accountChangeAction } from "@/store/actions/home_action";
import { useDispatch, useSelector } from "react-redux";
import { transformImgState } from "../utils";
import {
  messageFn,
  copyToClipboardFn,
  downloadImage,
  isMobileDevice,
} from "@/utils";
import _ from "lodash";
// import { productConsumedTypeConfig } from "@/api/user";
import MjGenerateSetting from "../components/mjGenerateSetting";
import generateImgFail from "../../../../public/assets/imgs/generateImgFail.svg";
import arrow from "../../../../public/assets/imgs/arrow.svg";
import mjNoDataImg from "../../../../public/assets/imgs/mjNoDataImg.svg";
import lazyLoadImg from "../../../../public/assets/mj/lazyLoadImg.png";

import styles from "./index.module.less";
const { confirm } = Modal;

const MjGenerate = () => {
  const sysConfig = useSelector((state) => state.sysConfig);

  /* ------------- */
  // 瀑布流组件容器
  const waterfallContent = useRef(null);
  const ulMaxHRef = useRef(0);
  const [renderWaterfall, setRenderWaterfall] = useState(true);
  const waterfallWidth = useRef(350);
  /* ------------- */
  const history = useHistory();
  const dispatch = useDispatch();
  const [productInfo, setProductInfo] = useState("");

  // 获取商品消耗提示语字典
  const consumptionPrompt = useSelector((state) => state.consumptionPrompt);

  const [mjConsumeList, setMjConsumeList] = useState({});
  // 账户信息
  const accountInfo = useSelector((state) => state.accountInfo);

  // 图片目前又三种情况
  // UPSCALE 单个图片放大
  // VARIATION 选择其中一种图片生成四张
  // IMAGINE 通过关键次生成

  // 生成图片对象
  const promptObj = useRef({
    prompt: "",
    base64: "",
    base64Array: [],
  });

  // 触发补全指令
  const [completionState, setCompletionState] = useState(false);
  const [userPrompt, setUserPrompt] = useState("");

  // 生成图片尺寸
  const generateImgSize = useRef({
    label: "1:1",
    id: 1,
    value: " --ar 1:1",
  });

  // 选择模型
  const generateImgModel = useRef({
    id: 0,
    title: "MJ",
    info: "通用写实风格MJ",
    value: "",
  });

  // 细节设置
  const detailSettings = useRef({
    // 风格
    style: "",
    // 视角
    angle: "",
    // 人物镜头
    characterShots: "",
    // 灯光
    lighting: "",
    // 相机:"",
    creame: "",
    // 画质
    pictureQuality: "",
    // 艺术程度
    artisticLevel: "",
  });

  const pagination = useRef({
    total: 0,
    size: 10,
    current: 1,
  });

  // 任务状态查询定时器
  const genearteImgTaskTimer = useRef([]);

  // 定时器调用时间

  const timeOut = useRef(2 * 1000);
  // 生成图片列表
  const [generateImgList, setGenerateImgList] = useState([]);
  const generateImgListRef = useRef(null);
  // 编写提示词
  const setPrompt = (e) => {
    promptObj.current.prompt = e.target.value;
    setUserPrompt(e.target.value);

    if (e.target.value[0] === "/") {
      setCompletionState(true);
    } else {
      setCompletionState(false);
    }
  };

  // 选择不全的模板
  const handlerChooseCompletion = (data) => {
    promptObj.current.prompt = data.desc;
    setUserPrompt(data.desc);
    setCompletionState(false);
  };

  // 移动端兼容 打开配置
  // 0关闭 1 打开
  const [mobileConfigState, setMobileConfigState] = useState(0);

  // 删除已经绘制好的图
  const deleteMjGenerateImgFn = async (id) => {
    try {
      let res = await deleteMjGenerateImg(id);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "删除成功",
        });

        let copyGenerateImgList = _.cloneDeep(generateImgList);
        let deleteItemIndex = copyGenerateImgList.findIndex(
          (v) => v.fileId === id
        );
        copyGenerateImgList.splice(deleteItemIndex, 1);
        setGenerateImgList(copyGenerateImgList);
        reRenderWaterfall();
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

  // 动态展示瀑布流内容高度
  const lazyLoadImageSize = (item) => {
    let filePrompt = item.filePrompt;
    let sizeObj = {};
    if (filePrompt.indexOf("1:1") !== -1) {
      sizeObj = {
        width: waterfallWidth.current,
        height: waterfallWidth.current,
      };
    } else if (filePrompt.indexOf("4:3") !== -1) {
      sizeObj = {
        width: waterfallWidth.current,
        height: waterfallWidth.current / (4 / 3).toFixed(2),
      };
    } else if (filePrompt.indexOf("4:4") !== -1) {
      sizeObj = {
        width: waterfallWidth.current,
        height: waterfallWidth.current,
      };
    } else if (filePrompt.indexOf("9:16") !== -1) {
      sizeObj = {
        width: waterfallWidth.current,
        height: waterfallWidth.current / (9 / 16).toFixed(2),
      };
    } else if (filePrompt.indexOf("16:9") !== -1) {
      sizeObj = {
        width: waterfallWidth.current,
        height: waterfallWidth.current / (16 / 9).toFixed(2),
      };
    }

    return sizeObj;
  };

  // 图片下载
  const downloadImageFn = (v) => {
    let url = `/api/download/image?fileId=${v.fileId}&type=2`;
    messageFn({
      type: "success",
      content: "下载成功,请稍后",
    });
    downloadImage(url);
  };

  // 创建绘画任务并查询状态
  const queryAllTaskState = () => {
    // 每次查询数据需要清空所有的轮询数据
    genearteImgTaskTimer.current.forEach((v) => {
      if (v.timer) {
        clearInterval(v.timer);
      }
    });
    // 清空任务队列
    genearteImgTaskTimer.current = [];

    generateImgListRef.current.forEach((v) => {
      if (
        v.fileStatus === "NOT_START" ||
        v.fileStatus === "SUBMITTED" ||
        v.fileStatus === "IN_PROGRESS"
      ) {
        addTaskToTimerTask(v.fileId);
        // 查询任务状态
        getGenearteImgTaskState(v.fileId);
      }
    });
  };

  // 更新任务
  const getGenearteImgTask = async (fileId) => {
    // 每次生成时 查询出最近的16个任务，判断是否有加载中的任务
    let data = {
      size: 16,
      current: 1,
    };
    let res = await getImageList(data);
    if (res.code === 200) {
      let resData = res.result.records || [];

      let nowTask = resData.find((v) => v.fileId === fileId);
      let nowImageList = [nowTask, ...generateImgListRef.current];
      console.log(nowImageList, "nowImageList");
      // 默认任务状态
      nowImageList.forEach((v) => {
        if (!v.fileStatus) {
          v.fileStatus = "NOT_START";
        }
      });
      setGenerateImgList(nowImageList);

      generateImgListRef.current = nowImageList;

      // 查询任务状态
      queryAllTaskState();

      // 重置瀑布组件
      reRenderWaterfall();
    }
  };
  // 重新渲染瀑布流组件

  const reRenderWaterfall = () => {
    setRenderWaterfall(false);
    setTimeout(() => {
      setRenderWaterfall(true);
    }, 300);
  };

  // 获取已经生成的图片列表
  const getImageListFn = async () => {
    try {
      let data = {
        size: pagination.current.size,
        current: pagination.current.current,
      };
      let res = await getImageList(data);
      if (res.code === 200) {
        let resData = res.result.records || [];
        /* ----------- */

        if (resData.length) {
          pagination.current.current++;
        }
        let newArr = [...generateImgList, ...resData];

        newArr.forEach((v) => {
          if (!v.fileStatus) {
            v.fileStatus = "NOT_START";
          }
        });

        setGenerateImgList(newArr);
        generateImgListRef.current = newArr;

        // 查询任务状态
        queryAllTaskState();
      }
    } catch (error) {
      console.log(error);
    }
  };

  /**
   * @description: 生成图片
   * @return {*}
   * @author: jl.g
   */
  const generateImg = async () => {
    if (!userPrompt) {
      messageFn({
        type: "error",
        content: "咒语不能为空",
      });

      return;
    }

    setUserPrompt("");
    try {
      let data = _.cloneDeep(promptObj.current);

      // 细节设置
      let detialSetting = "";
      for (let i in detailSettings.current) {
        if (detailSettings.current[i]) {
          detialSetting = detialSetting + " " + detailSettings.current[i];
        }
      }

      if (detialSetting) {
        data.prompt = data.prompt + " " + detialSetting;
      }

      // 如果手动指定了图片大小 则无需走图片配置
      if (data.prompt.indexOf("--ar") === -1) {
        data.prompt = data.prompt + generateImgSize.current.value;
      }

      // 二次元风格
      if (data.prompt.indexOf("--niji") === -1) {
        data.prompt = data.prompt + generateImgModel.current.value;
      }
      data.productType = 3;
      let res = await generateImgFn(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "任务创建成功,正在拼命绘制中,请耐心等候。",
        });
        getGenearteImgTask(res.result);

        // 扣除账户余额
        dispatch(
          accountChangeAction(new Date().getTime() + "_" + Math.random())
        );
      } else if (res.code === 4003) {
        // 余额不足
        confirm({
          title: "余额不足",
          content: "尊敬的用户您好,您的账户余额不足，点击确定购买商品。",
          onOk() {
            history.replace("/ai/commodity");
          },
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

  // 快速生成

  const generateImgQuick = async () => {
    if (!userPrompt) {
      messageFn({
        type: "error",
        content: "咒语不能为空",
      });

      return;
    }

    setUserPrompt("");
    try {
      let data = _.cloneDeep(promptObj.current);

      // 细节设置
      let detialSetting = "";
      for (let i in detailSettings.current) {
        if (detailSettings.current[i]) {
          detialSetting = detialSetting + " " + detailSettings.current[i];
        }
      }

      if (detialSetting) {
        data.prompt = data.prompt + " " + detialSetting;
      }

      // 如果手动指定了图片大小 则无需走图片配置
      if (data.prompt.indexOf("--ar") === -1) {
        data.prompt = data.prompt + generateImgSize.current.value;
      }

      // 二次元风格
      if (data.prompt.indexOf("--niji") === -1) {
        data.prompt = data.prompt + generateImgModel.current.value;
      }
      data.productType = 10;
      let res = await generateImgFn(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "任务创建成功,正在拼命绘制中,请耐心等候。",
        });
        getGenearteImgTask(res.result);

        // 扣除账户余额
        dispatch(
          accountChangeAction(new Date().getTime() + "_" + Math.random())
        );
      } else if (res.code === 4003) {
        // 余额不足
        confirm({
          title: "余额不足",
          content: "尊敬的用户您好,您的账户余额不足，点击确定购买商品。",
          onOk() {
            history.replace("/ai/commodity");
          },
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

  // 向任务队列添加任务
  const addTaskToTimerTask = (fileId) => {
    genearteImgTaskTimer.current.push({
      fileId,
      timer: null,
      // 创建时间
      createTime: new Date().getTime(),
    });
  };

  // 定时查询任务状态
  const getGenearteImgTaskState = (fileId) => {
    //  每3S查询一下当前任务的状态
    // 支持多个绘画任务并发地计算状态
    genearteImgTaskTimer.current.forEach((v) => {
      if (v.fileId === fileId) {
        v.timer = setInterval(() => {
          getImageTaskStateFn(fileId);
        }, timeOut.current);
      }
    });
  };

  // 发布到广场
  const publishStateFn = async (itemData, state) => {
    try {
      let data = {
        fileId: itemData.fileId,
        publishState: state,
      };
      let res = await publishState(data);
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

  // 当某个任务调用成功删除对应的轮询调用
  const clearTimerTask = (fileId) => {
    if (genearteImgTaskTimer.current.length) {
      // 找到对应的任务
      let activeIndex = null;
      genearteImgTaskTimer.current.forEach((v, index) => {
        if (v.fileId === fileId) {
          clearInterval(v.timer);
          activeIndex = index;
        }
      });

      if (activeIndex !== null) {
        // 从任务列表删除任务
        genearteImgTaskTimer.current.splice(activeIndex, 1);
      }
    }
  };

  // 查询生成任务状态
  const getImageTaskStateFn = async (fileId) => {
    try {
      let params = {
        fileId,
      };
      let res = await getImageTaskState(params);
      if (res.code === 200) {
        let resData = res.result;
        // 查询当前需要更改状态的任务
        let generateImgListCopy = _.cloneDeep(generateImgListRef.current);

        // 查询正在执行中的任务
        let findInProgressTaskIndex = generateImgListCopy.findIndex(
          (v) => v.fileId === fileId
        );
        // 每次将当前带生成任务的状态修改
        generateImgListCopy.splice(findInProgressTaskIndex, 1, resData);

        setGenerateImgList([...generateImgListCopy]);
        generateImgListRef.current = [...generateImgListCopy];

        if (
          resData.fileStatus === "SUCCESS" ||
          resData.fileStatus === "FAILURE"
        ) {
          // 清除对应任务
          clearTimerTask(fileId);
          reRenderWaterfall();
        }
      } else {
        // 清除对应任务
        clearTimerTask(fileId);
        messageFn({
          type: "error",
          content: res.message,
        });
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 生成图片微调
  const changeGenerateImgFn = async (itemData, type, index) => {
    if (itemData?.changeButtonInfo[type + (index + 1)]) {
      return;
    }
    try {
      let data = {
        fileId: itemData.fileId,
        action: type,
        index: index + 1,
      };

      let res = await changeGenerateImg(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "操作成功,请耐心等候",
        });
        getGenearteImgTask(res.result);
      } else if (res.code === 4003) {
        // 余额不足
        confirm({
          title: "余额不足",
          content: "尊敬的用户您好,您的账户余额不足，点击确定购买商品。",
          onOk() {
            history.replace("/ai/commodity");
          },
        });
      } else {
        messageFn({
          type: "error",
          content: res.message || "操作失败,请稍后再试",
        });
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 获取商品消耗
  const getMjInfosFn = async () => {
    try {
      let res = await getMjInfos();
      if (res.code === 200) {
        setMjConsumeList(res.result);
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 获取商品消耗提示
  // const getProductConsumedTypeConfig = async () => {
  //   try {
  //     let params = {
  //       productType: 3,
  //     };
  //     let res = await productConsumedTypeConfig(params);
  //     if (res.code === 200) {
  //       setProductInfo(res.result);
  //       // dispatch(consumptionPromptAction(res.result));
  //     }
  //   } catch (error) {
  //     console.log(error);
  //   }
  // };

  /* ------------------- */

  const loadData = _.debounce(() => {
    getImageListFn();
  }, 1000);

  useEffect(() => {
    getImageListFn();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (genearteImgTaskTimer.current.length) {
      genearteImgTaskTimer.current.forEach((v) => {
        clearInterval(v.timer);
      });
    }

    getMjInfosFn();

    // getProductConsumedTypeConfig();
  }, []);
  return (
    <div className={styles.mjGenerate}>
      <div
        className={`mj-generate_setting ${
          mobileConfigState === 1 ? "mobileSettingShow" : ""
        }`}
      >
        <div className="title-box">
          <span className="label">配置详情</span>
          <CloseOutlined
            onClick={() => setMobileConfigState(0)}
          ></CloseOutlined>
        </div>
        <MjGenerateSetting
          generateImgModel={generateImgModel}
          generateImgSize={generateImgSize}
          promptObj={promptObj}
          detailSettings={detailSettings}
        ></MjGenerateSetting>
      </div>

      <div className="mj-generate_content">
        {isMobileDevice() ? (
          <div className="mj_content-head-info">
            <div className="mj_balance">
              剩余绘画次数:{accountInfo?.mjBalance || 0}
            </div>
          </div>
        ) : (
          ""
        )}

        <div className="mj-content_head">
          <div className="mj_head-tools">
            <Input
              style={{
                height: 40,
              }}
              onChange={setPrompt}
              value={userPrompt}
              className="mj_head-tools-input"
              placeholder="Enter换行, 输入 /  触发预设模板选择"
              resize="none"
            />

            <div className="mj_generate-box">
              <div
                onClick={_.debounce(generateImg, 500)}
                className="mj_head-tools-btn"
              >
                <span className="label">慢速</span>

                <span className="value"> {mjConsumeList[3] || ""}</span>

                <img className="logo" src={currencyIcon} alt="" />
              </div>

              <div
                onClick={_.debounce(generateImgQuick, 500)}
                className="mj_head-tools-btn"
              >
                <span className="label">快速</span>
                <span className="value"> {mjConsumeList[10] || ""}</span>
                <img className="logo" src={currencyIcon} alt="" />

              </div>
            </div>

            {completionState ? (
              <div className="completionWarp">
                {completionData.map((v) => (
                  <div
                    key={v.name}
                    onClick={() => handlerChooseCompletion(v)}
                    className="completion-item"
                  >
                    <div className="title">{v.name}</div>
                    {/* <div className="desc">{v.desc}</div> */}
                  </div>
                ))}
              </div>
            ) : (
              ""
            )}
          </div>

          <div
            className="mj_head-settings"
            onClick={() => setMobileConfigState(1)}
          >
            <span className="label">展开配置</span>
            <img className="img" src={arrow} alt="" />
          </div>
        </div>

        <div
          ref={waterfallContent}
          className="mj-generate_infiniteScroll"
          onScroll={(e) => {
            const scrollTop = e.target.scrollTop;

            const scrollHeight = e.target.scrollHeight;

            if (scrollTop + 50 > scrollHeight - ulMaxHRef.current) {
              loadData();
            }
          }}
        >
          {renderWaterfall ? (
            <Waterfall
              mode="grid"
              el="#waterfall-grid"
              columnWidth={waterfallWidth.current}
              columnGap={24}
              rowGap={12}
              delay={300}
              onChangeUlMaxH={(h) => (ulMaxHRef.current = h)}
            >
              {generateImgList.length
                ? generateImgList.map((v) => (
                    <li key={v.fileId}>
                      <div className="mj-generate_info">
                        {v.fileStatus === "SUCCESS" ? (
                          <>
                            <div className="mj_content-top-tools">
                              <div className="mj-content-bottom_info">
                                {v.createTime}
                              </div>

                              <div className="mj-content-item_tools">
                                {/* <div className="mj-generate-img-state">
                                  {transformImgState(v.fileStatus)}
                                </div> */}

                                <div className="tools">
                                  <Tooltip
                                    placement="right"
                                    title={v.filePrompt}
                                  >
                                    <span
                                      onClick={() =>
                                        copyToClipboardFn(
                                          v.filePrompt,
                                          "复制成功"
                                        )
                                      }
                                      className="mj-generate-tools_btn"
                                    >
                                      咒语
                                    </span>
                                  </Tooltip>

                                  <span
                                    onClick={() => downloadImageFn(v, 2)}
                                    className="mj-generate-tools_btn"
                                  >
                                    下载
                                  </span>
                                  {v.fileAction === "UPSCALE" ? (
                                    <>
                                      {v.publishState === "NOT_PUBLISH" ? (
                                        <span
                                          onClick={() =>
                                            publishStateFn(v, "PUBLISHED")
                                          }
                                          className="mj-generate-tools_btn"
                                        >
                                          发布
                                        </span>
                                      ) : (
                                        <span
                                          className="mj-generate-tools_btn"
                                          onClick={() =>
                                            publishStateFn(v, "NOT_PUBLISH")
                                          }
                                        >
                                          取消
                                        </span>
                                      )}
                                    </>
                                  ) : (
                                    ""
                                  )}

                                  <Popconfirm
                                    title="数据删除后不可恢复,是否确认删除?"
                                    okText="确认"
                                    cancelText="取消"
                                    onConfirm={() =>
                                      deleteMjGenerateImgFn(v.fileId)
                                    }
                                  >
                                    <span className="deleteImg mj-generate-tools_btn">
                                      删除
                                    </span>
                                  </Popconfirm>
                                </div>
                              </div>
                            </div>
                            <div className="mj-generate_info_warp">
                              <div className="mj-content-item">
                                <div
                                  className={`mj-generate_img ${
                                    v.fileAction === "UPSCALE"
                                      ? "mj-generate_img_upscale"
                                      : ""
                                  }`}
                                >
                                  <LazyLoadImage
                                    effect="blur"
                                    {...lazyLoadImageSize(v)}
                                    src={v.cosUrl}
                                    placeholderSrc={lazyLoadImg}
                                    alt="Image Alt"
                                  />
                                </div>
                              </div>
                            </div>

                            <div className="mj-content-bottom_tools">
                              {v.fileAction !== "UPSCALE" ? (
                                <div className="mj-content-bottom_config">
                                  <div className="mj-content-bottom_config_U">
                                    <span className="mj-item_bottom_U_label">
                                      放大:
                                    </span>

                                    <div className="mj-item_bottom_U_btns">
                                      {new Array(4)
                                        .fill(null)
                                        .map((item, index) => (
                                          <span
                                            key={index}
                                            onClick={_.debounce(
                                              () =>
                                                changeGenerateImgFn(
                                                  v,
                                                  "UPSCALE",
                                                  index
                                                ),
                                              500
                                            )}
                                            className={`mj-item_bottom_U_btns_item ${
                                              v?.changeButtonInfo&&v?.changeButtonInfo[
                                                "UPSCALE" + (index + 1)
                                              ]
                                                ? "mj-item_bottom_U_btns_item_disabled"
                                                : ""
                                            }`}
                                          >
                                            图{index + 1}
                                          </span>
                                        ))}
                                    </div>
                                  </div>

                                  <div className="mj-content-bottom_config_U">
                                    <span className="mj-item_bottom_U_label">
                                      生成:
                                    </span>

                                    <div className="mj-item_bottom_U_btns">
                                      {new Array(4)
                                        .fill(null)
                                        .map((item, index) => (
                                          <span
                                            key={index}
                                            onClick={_.debounce(
                                              () =>
                                                changeGenerateImgFn(
                                                  v,
                                                  "VARIATION",
                                                  index
                                                ),
                                              500
                                            )}
                                            className={`mj-item_bottom_U_btns_item ${
                                              v?.changeButtonInfo&&v?.changeButtonInfo[
                                                "VARIATION" + (index + 1)
                                              ]
                                                ? "mj-item_bottom_U_btns_item_disabled"
                                                : ""
                                            }`}
                                          >
                                            图{index + 1}
                                          </span>
                                        ))}
                                    </div>
                                  </div>
                                </div>
                              ) : (
                                ""
                              )}
                            </div>
                          </>
                        ) : (
                          <div className="mj-content-item">
                            <div className="mj_content-top-tools">
                              <div
                                className={`mj-generate-img-state ${
                                  v.fileStatus === "FAILURE" ||
                                  v.fileStatus === "EXPIRE"
                                    ? "mj-generate-img-failure"
                                    : ""
                                }`}
                              >
                                {transformImgState(v.fileStatus)}
                              </div>

                              {/* 只有成功和失败状态 才可以进行删除 */}
                              {v.fileStatus === "FAILURE" ? (
                                <div className="tools">
                                  <Popconfirm
                                    className="deleteImg"
                                    title="数据删除后不可恢复,是否确认删除?"
                                    okText="确认"
                                    cancelText="取消"
                                    onConfirm={() =>
                                      deleteMjGenerateImgFn(v.fileId)
                                    }
                                  >
                                    <span className="deleteImg   mj-generate-tools_btn">
                                      删除
                                    </span>
                                  </Popconfirm>
                                </div>
                              ) : (
                                ""
                              )}
                            </div>

                            <div className="mj-generate_img">
                              {v.fileStatus === "FAILURE" ? (
                                <div className="mj-generate_img_loadingBox">
                                  <img
                                    style={{ width: 360 }}
                                    className="generateImgFail"
                                    src={generateImgFail}
                                    alt=""
                                  />
                                </div>
                              ) : (
                                <div className="mj-generate_img_loadingBox">
                                  <div className="mj-generate_img_loading">
                                    <MjLoading></MjLoading>
                                    <p className="mj-generate_img_loading_label">
                                      生成中预计耗时60s,请耐心等候。
                                    </p>

                                    <Progress
                                      type="circle"
                                      percent={Number(
                                        v.progress.replace("%", "")
                                      )}
                                      strokeColor={{
                                        "0%": "#108ee9",
                                        "100%": "#87d068",
                                      }}
                                    />
                                  </div>
                                </div>
                              )}
                            </div>
                          </div>
                        )}
                      </div>
                    </li>
                  ))
                : ""}
            </Waterfall>
          ) : (
            ""
          )}

          {/* {randomImg ? (
           
          ) : (
            ""
          )} */}
        </div>

        {generateImgList.length === 0 ? (
          <div className="mj_content-nodeData-box">
            <img className="mj_content-nodeData-img" src={mjNoDataImg} alt="" />
            <p className="mj_content-nodeData-info">
              当前您尚未进行图像绘制,快输入描述进行图像创作吧!
            </p>
          </div>
        ) : (
          ""
        )}
      </div>
      {/* 移动端遮罩层 */}
      {mobileConfigState === 1 ? (
        <div
          className="mobile_mask"
          onClick={() => setMobileConfigState(0)}
        ></div>
      ) : (
        ""
      )}
    </div>
  );
};

export default MjGenerate;
