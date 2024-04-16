/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-06-28 17:24:24
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-09 19:47:21
 */
import React, { useEffect, useState, useRef } from "react";
import { imagesSquareList } from "@/api/midjourney";

import Waterfall from "waterfalljs-layout/react";
import { LazyLoadImage } from "react-lazy-load-image-component";
import "react-lazy-load-image-component/src/effects/blur.css";
import _ from "lodash";
import {
  copyToClipboardFn,
  messageFn,
  downloadImage,
  isMobileDevice,
} from "@/utils";
import { Tag, Space } from "antd";
import { CopyOutlined, DownloadOutlined } from "@ant-design/icons";
import lazyLoadImg from "../../../../public/assets/mj/lazyLoadImg.png";
import styles from "./index.module.less";
const PictureSquare = () => {
  const waterfallWidth = useRef(isMobileDevice() ? 350 : 320);
  const ulMaxHRef = useRef(0);

  // 图片列表
  const [squareImgList, setSquareImgList] = useState([]);
  // 分页
  const pagination = useRef({
    size: 12,
    current: 1,
  });
  const downloadImageFn = (v) => {
    let url = `/api/download/image?fileId=${v.fileId}&type=2`;
    messageFn({
      type: "success",
      content: "下载成功,请稍后",
    });
    downloadImage(url);
  };
  // 获取广场数据
  const getImagesSquareList = async () => {
    try {
      let res = await imagesSquareList(pagination.current);
      if (res.code === 200) {
        let resData = res.result.records || [];
        if (resData.length) {
          pagination.current.current++;
        }
        setSquareImgList([...squareImgList, ...resData]);
      } else {
        setSquareImgList([]);
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

  const loadData = _.debounce(() => {
    getImagesSquareList();
  }, 1000);

  useEffect(() => {
    getImagesSquareList();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  return (
    <div className={styles.pictureSquareWarp}>
      <div
        className="picture_square-content"
        onScroll={(e) => {
          const scrollTop = e.target.scrollTop;

          const scrollHeight = e.target.scrollHeight;

          if (scrollTop + 50 > scrollHeight - ulMaxHRef.current) {
            loadData();
          }
        }}
      >
        <Waterfall
          mode="grid"
          el="#waterfall-grid"
          columnWidth={waterfallWidth.current}
          columnGap={24}
          rowGap={12}
          delay={300}
          onChangeUlMaxH={(h) => (ulMaxHRef.current = h)}
        >
          {squareImgList.map((item) => (
            <li key={item.fileId}>
              <div className="lazyLoadImageItem">
                <LazyLoadImage
                  effect="blur"
                  {...lazyLoadImageSize(item)}
                  src={item.cosUrl}
                  placeholderSrc={lazyLoadImg}
                  alt="Image Alt"
                />

                <div className="image_item-tools">
                  <div className="filePrompt">{item.filePrompt}</div>
                  <div className="tools">
                    <Space>
                      <Tag
                        color="#70D9FF"
                        onClick={() =>
                          copyToClipboardFn(item.filePrompt, "复制成功")
                        }
                        className="mj-generate-tools_btn"
                        icon={<CopyOutlined />}
                        size="small"
                      >
                        复制咒语
                      </Tag>
                      <Tag
                        color="#70D9FF"
                        onClick={() => downloadImageFn(item)}
                        className="mj-generate-tools_btn"
                        icon={<DownloadOutlined />}
                        size="small"
                      >
                        下载
                      </Tag>
                    </Space>
                  </div>
                </div>
              </div>
            </li>
          ))}
        </Waterfall>
      </div>
    </div>
  );
};

export default PictureSquare;
