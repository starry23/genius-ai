import React, { useState, useRef, useEffect } from "react";
import { Transformer } from "markmap-lib";
import { Button, Dropdown, Space, Menu } from "antd";
import _ from "lodash";
import { DownloadOutlined } from "@ant-design/icons";
import html2canvas from "html2canvas";
import { saveAs } from "file-saver";
import { Markmap } from "markmap-view";
import "markmap-toolbar/dist/style.css";
import styles from "./index.module.less";
const transformer = new Transformer();
export default function ReactMarkMap({ resultMarkdown }) {
  const baseTemp = useRef(`# 通过文本成思维导图
  - 发送对内容的描述:例如 小说《活着》的主要内容
  - 生成思维导图
  - 将生成好的内容进行导出
    - png
    - svg
    - html
  `);
  // Ref for SVG element
  const refSvg = useRef();
  // Ref for markmap object
  const refMm = useRef();
  const initMarkMap = () => {
    // 1.获取svgRef 通过Markmap.create创建示例
    refMm.current = Markmap.create(refSvg.current);
    analysisMarkDown(baseTemp.current);
  };

  //   解析markDown内容
  const analysisMarkDown = (markValue) => {
    //2.解析Markdown并创建一个节点树，返回根节点和一个包含解析期间活动特征的特征对象。
    const { root } = transformer.transform(markValue);
    refMm.current.setData(root);
    refMm.current.fit();
  };

  // 导出为img
  const exportAsImage = () => {
    if (refSvg.current) {
      try {
        const canvas = document.createElement("canvas");
        // 设置画布大小以适应SVG尺寸
        canvas.height = refSvg.current.height;
        canvas.width = refSvg.current.width;

        html2canvas(document.querySelector("#svgWarp")).then((canvas) => {
          const dataUrl = canvas.toDataURL("image/png");
          // 创建一个隐藏的链接
          const link = document.createElement("a");
          link.href = dataUrl;
          link.download = "markmap.png";
          // 模拟用户点击链接以下载PNG文件
          link.click();
        });
      } catch (error) {
        console.error("Failed to convert SVG to PNG:", error);
      }
    }
  };
  // 导出作为svg
  const exportAsSVG = () => {
    const svgElement = refSvg.current;
    const serialize = new XMLSerializer();
    const etl = serialize.serializeToString(svgElement);
    // 创建一个新的Blob对象，并将SVG内容写入其中
    const svgData = new Blob([etl], { type: "image/svg+xml" });

    // 使用FileSaver保存Blob对象为SVG文件
    saveAs(svgData, "markmap.svg");
  };

  // 导出为html
  const exportAsHTML = () => {
    const svgElement = refSvg.current;
    if (svgElement) {
      const serializer = new XMLSerializer();
      const svgEl = serializer.serializeToString(svgElement);

      // 创建一个 Blob 对象
      const blob = new Blob([svgEl], { type: "text/html;charset=utf-8" });
      // 创建 URL 并模拟点击下载链接
      const url = URL.createObjectURL(blob);
      //创建一个<a> 元素的
      const linkElement = document.createElement("a");
      //把url赋值给a元素的href
      linkElement.href = url;
      linkElement.download = "markmap.html";
      linkElement.click();
      linkElement.remove();
      // 清理创建的 URL
      URL.revokeObjectURL(url);
    }
  };

  useEffect(() => {
    initMarkMap();
  }, []);

  useEffect(() => {
    const debouncedFunction = _.debounce(() => {
      analysisMarkDown(resultMarkdown || baseTemp.current);
    }, 150);

    debouncedFunction(); // 调用防抖函数
    return () => {
      debouncedFunction.cancel(); // 清除防抖函数
    };
  }, [resultMarkdown]);

  return (
    <div className={styles.reactMarkMapWarp}>
      <div className="reactMarkMapTools">
        <Dropdown
          overlay={
            <Menu>
              <Menu.Item key="Image" onClick={exportAsImage}>
                Image
              </Menu.Item>
              <Menu.Item key="SVG" onClick={exportAsSVG}>
                SVG
              </Menu.Item>
              <Menu.Item key="HTML" onClick={exportAsHTML}>
                HTML
              </Menu.Item>
            </Menu>
          }
          trigger={["click"]}
        >
          <a onClick={(e) => e.preventDefault()}>
            <Space>
              <Button
                type="primary"
                shape="round"
                icon={<DownloadOutlined />}
                size="small"
              />
            </Space>
          </a>
        </Dropdown>
      </div>

      <div id="svgWarp">
        <svg
          style={{ height: "100%", width: "100%" }}
          className="reactMarkMapSvg"
          ref={refSvg}
        />
      </div>
    </div>
  );
}
