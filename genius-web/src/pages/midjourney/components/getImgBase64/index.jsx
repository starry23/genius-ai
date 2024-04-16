/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-06-16 08:43:17
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-02-03 23:48:50
 */
import React, { useState } from "react";
import { InboxOutlined, DeleteOutlined,PlusCircleOutlined } from "@ant-design/icons";
import { message, Upload, Image } from "antd";
import { messageFn } from "@/utils";
import styles from "./index.module.less";
const { Dragger } = Upload;

const GetImgBase64 = ({ promptObj }) => {
  const [imageUrl, setImageUrl] = useState(null);
  const [imgInfo, setImgInfo] = useState(null);

  const [imageUrl2, setImageUrl2] = useState(null);
  const [imgInfo2, setImgInfo2] = useState({});
  const beforeUpload = (file) => {
    setImgInfo(file);
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);

      reader.onload = () => {
        console.log(file, "11");
        let fileSize = (file.size / 1024 / 1024).toFixed(2);
        // 限制尺寸
        if (fileSize > 5) {
          messageFn({
            type: "error",
            content: "图片尺寸过大,请重新选择",
          });
          return;
        }

        // 限制图片类型
        console.log(file.type, "file.type");
        if (file.type !== "image/jpeg" && file.type !== "image/png") {
          messageFn({
            type: "error",
            content: "以图生图只支持png格式或者jpg格式的图片",
          });
          return;
        }
        const base64Url = reader.result;
        promptObj.current.base64Array[0] = base64Url;
        setImageUrl(base64Url);
      };

      reader.onerror = (error) => {
        console.error(error);
        message.error("上传失败！");
        reject(error);
      };
    });
  };

  const beforeUpload2 = (file) => {
    setImgInfo2(file);
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);

      reader.onload = () => {
        let fileSize = (file.size / 1024 / 1024).toFixed(2);
        // 限制尺寸
        if (fileSize > 5) {
          messageFn({
            type: "error",
            content: "图片尺寸过大,请重新选择",
          });
          return;
        }

        // 限制图片类型

        if (file.type !== "image/jpeg" && file.type !== "image/png") {
          messageFn({
            type: "error",
            content: "以图生图只支持png格式或者jpg格式的图片",
          });
          return;
        }
        const base64Url = reader.result;
        promptObj.current.base64Array[1] = base64Url;
        setImageUrl2(base64Url);
      };

      reader.onerror = (error) => {
        console.error(error);
        message.error("上传失败！");
        reject(error);
      };
    });
  };

  const props = {
    name: "file",
    fileList: [],
    multiple: false,
    action: "",
    beforeUpload,
  };
  const props2 = {
    name: "file",
    fileList: [],
    multiple: false,
    action: "",
    beforeUpload: beforeUpload2,
  };

  // 清除垫图
  const clearImg = () => {
    setImageUrl(null);
  };

  return (
    <div className={styles.getImgBase64}>
      <div className="title">以图生图</div>
      <p className="upload_info">仅支持PNG或JPG格式的图片,最大5Mb</p>
      <div className="upload_img-content">
        <div className="upload_img-item">
          {imageUrl ? (
            <div className="upload_box">
              <div className="upload_box-left">
                <Image
                  className="base64Img"
                  width={50}
                  height={50}
                  src={imageUrl}
                />
                <div className="upload_box-imgInfo">
                  <div className="upload_box-imgInfo-imgName">
                    {imgInfo.name}
                  </div>
                  <div className="upload_box-imgInfo-size">
                    {(imgInfo.size / 1000 / 100).toFixed(2)}Mb
                  </div>
                </div>
              </div>

              <DeleteOutlined
                twoToneColor="red"
                onClick={clearImg}
              ></DeleteOutlined>
            </div>
          ) : (
            <Dragger className="dargBox" {...props}>
              <p className="ant-upload-drag-icon">
                <PlusCircleOutlined />
              </p>
              {/* <p className="ant-upload-text">图1</p> */}
            </Dragger>
          )}
        </div>
        <div className="upload_img-item">
          {imageUrl2 ? (
            <div className="upload_box">
              <div className="upload_box-left">
                <Image
                  className="base64Img"
                  width="50%"
                  height={50}
                  src={imageUrl2}
                />
                <div className="upload_box-imgInfo">
                  <div className="upload_box-imgInfo-imgName">
                    {imgInfo2.name}
                  </div>
                  <div className="upload_box-imgInfo-size">
                    {(imgInfo2.size / 1000 / 100).toFixed(2)}Mb
                  </div>
                </div>
              </div>

              <DeleteOutlined
                twoToneColor="red"
                onClick={clearImg}
              ></DeleteOutlined>
            </div>
          ) : (
            <Dragger className="dargBox" {...props2}>
              <p className="ant-upload-drag-icon">
                <PlusCircleOutlined />
              </p>
            
            </Dragger>
          )}
        </div>
      </div>
    </div>
  );
};

export default GetImgBase64;
