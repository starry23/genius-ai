/*
 * @Description:
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-11-18 14:01:29
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:45:16
 */
import React, { useState, useEffect, useRef } from "react";
import { useDispatch } from "react-redux";
import _ from "lodash";
import { Input, Progress, Select, Modal } from "antd";
import {
  generateDalle3Api,
  getGenerateRecordApi,
  dalleRemoveApi,
} from "@/api/dalle3";
import {
  messageFn,
  isMobileDevice,
  copyToClipboardFn,
  downloadImage,
} from "@/utils";
import { accountChangeAction } from "@/store/actions/home_action";
import { ExclamationCircleOutlined } from "@ant-design/icons";
import dalleGen from "../../../public/assets/imgs/dalleGen.svg";
import mjNoDataImg from "../../../public/assets/imgs/mjNoDataImg.svg";
import copyIcon from "../../../public/assets/dalle/copy.svg";
import downLoadIcon from "../../../public/assets/dalle/downLoad.svg";
import deleteIcon from "../../../public/assets/dalle/delete.svg";
import lazyLoadImg from "../../../public/assets/dalle/dalle3LoadImg.jpg";
import { LazyLoadImage } from "react-lazy-load-image-component";
import "react-lazy-load-image-component/src/effects/blur.css";
import styles from "./index.module.less";
const { Option } = Select;

const Dalle3 = () => {
  const dispatch = useDispatch();

  // 用户提示词
  const [userPrompt, setUserPrompt] = useState("");

  // 图片尺寸
  const [imgSize, setImgSize] = useState("1024x1024");
  //   生成历史
  const [historyList, setHistoryList] = useState([]);

  // 进度条
  const [progressObj, setProgressObj] = useState({
    show: false,
    percent: 0,
  });

  //   当前的展示的图
  const [activeImgItem, setActiveImgItem] = useState(null);

  // 定时器
  const progressTimer = useRef(null);

  // 展示进度条
  const showProgress = () => {
    if (progressTimer.current) {
      clearInterval(progressTimer.current);
      progressTimer.current = null;
    }
    setProgressObj((data) => {
      return {
        ...data,
        show: true,
      };
    });
    let percent = 0;
    progressTimer.current = setInterval(() => {
      percent++;
      if (percent >= 100) {
        clearInterval(progressTimer.current);
        progressTimer.current = null;
        percent = 100;
      }
      setProgressObj((data) => {
        return {
          ...data,
          percent,
        };
      });
    }, 500);
  };

  // 关闭进度条
  const closeProgress = () => {
    if (progressTimer.current) {
      clearInterval(progressTimer.current);
      progressTimer.current = null;
    }
    setProgressObj({
      show: false,
      percent: 0,
    });
  };
  //   生成
  const generateImg = async () => {
    try {
      if (!userPrompt) {
        messageFn({
          type: "error",
          content: "请输入绘画咒语",
        });
        return;
      }
      if (progressTimer.current) {
        messageFn({
          type: "error",
          content: "当前存在绘画任务,请耐心等候。",
        });
        return;
      }
      let data = {
        prompt: userPrompt,
        size: imgSize,
      };
      showProgress();
      let res = await generateDalle3Api(data);
      if (res.code === 200) {
        closeProgress();
        getGenerateRecordFn();
        // 扣除账户余额
        dispatch(
          accountChangeAction(new Date().getTime() + "_" + Math.random())
        );
      } else {
        closeProgress();
        messageFn({
          type: "error",
          content: res.message,
        });
      }
    } catch (error) {
      closeProgress();
      console.log(error);
    }
  };

  // 删除图片
  const deleteImgItem = async (data) => {
    try {
      let res = await dalleRemoveApi({
        fileId: data.fileId,
      });
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "删除成功",
        });
        closeProgress();
        getGenerateRecordFn();
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

  // 下载图片
  const downLoadImg = (data) => {
    let url = `/api/download/image?fileId=${data.fileId}&type=7`;
    messageFn({
      type: "success",
      content: "下载成功,请稍后",
    });
    downloadImage(url);
  };

  // 复制提示语
  const copyPrompt = (data) => {
    copyToClipboardFn(data.prompt, "咒语复制成功");
  };
  //   获取生成列表
  const getGenerateRecordFn = async () => {
    try {
      let res = await getGenerateRecordApi();
      if (res.code === 200) {
        let resData = res.result || [];
        if (resData.length) {
          setActiveImgItem(resData[0]);
        }
        setHistoryList(resData);
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 编写提示词
  const setPrompt = (e) => {
    setUserPrompt(e.target.value);
  };

  //   选择图片
  const chooseImg = (item) => {
    closeProgress();
    setActiveImgItem(item);
  };

  // 尺寸选择
  const handleChange = (e) => {
    setImgSize(e);
  };
  useEffect(() => {
    getGenerateRecordFn();
  }, []);
  return (
    <div className={styles.dalle3Warp}>
      <div className="dalle3-box">
        <div className="dalle3-tools">
          <Input.TextArea
            rows={2}
            maxLength={300}
            onChange={setPrompt}
            value={userPrompt}
            className="dalle3-tools-input"
            placeholder="请输入咒语"
            resize="none"
            style={{ resize: "none" }}
          />

          <div className="dalle3-tools_setting">
            <Select value={imgSize} size="samll" onChange={handleChange}>
              <Option value="1024x1024">1024x1024</Option>
              <Option value="1792x1024">1792x1024</Option>
              <Option value="1024x1792">1024x1792</Option>
            </Select>
            <span
              onClick={_.debounce(generateImg, 500)}
              className="dalle3-tools-btn"
            >
              <img className="icon" src={dalleGen} alt="" />
              生成
            </span>
          </div>
        </div>
      </div>
      <div className="dalle3-content">
        <div className="dalle3-history">
          {historyList.map((v) => (
            <div
              title={v.prompt}
              key={v.fileId}
              onClick={() => chooseImg(v)}
              className={`dalle3-img_item ${
                activeImgItem?.fileId === v.fileId
                  ? "dalle3-img_activeItem"
                  : ""
              }`}
            >
              <div className="prompt">
                <div className="prompt_span">{v.prompt}</div>
                <div className="generate_tools">
                  <img
                    onClick={() => copyPrompt(v)}
                    title="复制咒语"
                    className="generate_tools-icon"
                    src={copyIcon}
                    alt=""
                  />
                  <img
                    onClick={() => downLoadImg(v)}
                    className="generate_tools-icon"
                    src={downLoadIcon}
                    alt=""
                  />

                  <img
                    onClick={() =>
                      Modal.confirm({
                        title: "尊敬的用户您好",
                        icon: <ExclamationCircleOutlined />,
                        content: "文件删除后不可恢复,是否确认删除?",
                        okText: "确认",
                        onOk() {
                          deleteImgItem(v);
                        },
                        cancelText: "取消",
                      })
                    }
                    className="generate_tools-icon"
                    src={deleteIcon}
                    alt=""
                  />
                </div>
              </div>
            </div>
          ))}
        </div>
        <div className="dalle3-generate">
          {historyList.length ? (
            progressObj.show ? (
              <div className="generate-info">
                <p className="generate-info_lable">
                  正在由Ai绘制您的想法,请稍等
                </p>
                <Progress
                  strokeColor={{
                    from: "#108ee9",
                    to: "#87d068",
                  }}
                  percent={progressObj.percent}
                  status="active"
                />
              </div>
            ) : (
              // <Image
              //   width={isMobileDevice() ? "300px" : "65%"}
              //   height={isMobileDevice() ? "auto" : "95%"}
              //   src={activeImgItem?.url}
              //   alt=""
              // />

              <LazyLoadImage
                effect="blur"
                width={isMobileDevice() ? "100%" : "90%"}
                height={"100%"}
                src={activeImgItem?.url}
                placeholderSrc={lazyLoadImg}
                alt="Image Alt"
              />
            )
          ) : (
            <div className="dall3-noData">
              <img className="dall3-nodeData-img" src={mjNoDataImg} alt="" />
              <div className="dall3-nodeData-info">
                当前您尚未进行图像绘制,请尝试输入"一只玩耍的橘猫"进行创作。
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dalle3;
