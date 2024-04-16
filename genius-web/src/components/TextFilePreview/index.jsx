/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-09-03 09:27:39
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-04 21:50:17
 */
import React, { useState, useEffect } from "react";
import mammoth from "mammoth";
import Buffer from "buffer";
import { downloadOssProxy } from "@/api/knowledgeBase";
import styles from "./index.module.less";
const RenderDocx = ({ fileUrl, type }) => {
  const [renderedText, setRenderedText] = useState("");

  useEffect(() => {
    if (fileUrl) {
      setRenderedText("");
      fetchDocxData();
    }
  }, [fileUrl]);

  const fetchDocxData = async () => {
    let params = {
      url: fileUrl,
    };
    const response = await downloadOssProxy(params);
    const blob = response;

    if (type === "doc") {
      renderDocx(blob);
    } else if (type === "txt") {
      readTxt(blob);
    }

    try {
    } catch (error) {
      console.log(error);
    }
  };

  const readTxt = async (blob) => {
    const data = await blob.arrayBuffer();

    // 将二进制文本内容转换为文字
    const decoder = new TextDecoder();
    const text = decoder.decode(data);
    setRenderedText(text);
  };

  const displayResult = (result) => {
    let html = result.value;
    let newHTML = html
      .replace(//g, "")
      .replace("<h1>", '<h1 style="text-align: center;">')
      .replace(/<table>/g, '<table style="border-collapse: collapse;">')
      .replace(/<tr>/g, '<tr style="height: 30px;">')
      .replace(/<td>/g, '<td style="border: 1px solid pink;">')
      .replace(/<p>/g, '<p style="text-indent: 20px">');
    setRenderedText(newHTML);
  };

  const renderDocx = async (blob) => {
    const arrayBuffer = await blob.arrayBuffer();
    const buffer = Buffer.Buffer.from(arrayBuffer);


    mammoth
      .convertToHtml({
        arrayBuffer: buffer,
      })
      .then(displayResult)
      .done();
  };

  return (
    <div className={styles.textFilePreview}>
      {renderedText ? (
        <>
          {type === "doc" ? (
            <div
              className="text-file-preview-doc"
              dangerouslySetInnerHTML={{ __html: renderedText }}
            />
          ) : (
            <div className="txt-file-preview">
              <pre>{renderedText}</pre>
            </div>
          )}
        </>
      ) : (
        ""
      )}
    </div>
  );
};

export default RenderDocx;
