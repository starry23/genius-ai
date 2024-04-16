/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-07-05 08:34:45
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:19:12
 */
import React, { useState, useRef, useEffect } from "react";
import Joyride from "react-joyride";
import { Button, Empty, Dropdown, Space, Menu, Modal } from "antd";
import { messageFn, setLocalStorage, getLocalStorage } from "@/utils";
import { resourcesList, deleteResources } from "@/api/knowledgeBase";
import AddFile from "./components/addFile";
import {
  DeleteOutlined,
  PlusCircleOutlined,
  MenuUnfoldOutlined,
  MoreOutlined,
  ExclamationCircleOutlined,
} from "@ant-design/icons";
import FileViewer from "react-file-viewer";
import { useSelector } from "react-redux";
import pdfIcon2 from "../../../../../public/assets/knowledge/pdfIcon.svg";
import docIcon from "../../../../../public/assets/knowledge/docIcon.svg";
import txtIcon from "../../../../../public/assets/knowledge/txtIcon.svg";
import KnowledgeChat from "./components/knowledgeChat";
import TextFilePreview from "@/components/TextFilePreview";
import styles from "./index.module.less";

const Knowledge = () => {
  const [runTutorial, setRunTutorial] = useState(
    getLocalStorage("knowledgeGuidanceState") === "1" ? false : true
  );

  const joyrideLocale = {
    back: "返回",
    close: "关闭",
    last: "完成",
    next: "下一步",
    skip: "跳过",
  };

  // 知识库 新手引导
  const runTutorialSteps = [
    {
      target: ".create_project-btn",
      content: "第一步:新建项目",
      placement: "right",
    },
    {
      target: ".knowledge_proj-btn",
      content: "第二步:上传文档",
      placement: "bottom",
    },

    {
      target: ".knowledge_base-viewPdf",
      content: "第三步:查看文档",
      placement: "right",
    },

    {
      target: ".knowledge_base-chat",
      content:
        "第四步:在此区域发送您的问题,Ai将会结合当前项目下的文档内容进行回答。",
      placement: "left",
    },
  ];

  const joyrideStyles = {
    options: {
      arrowColor: "#fff", // 修改箭头颜色
      primaryColor: "#63e2b7", // 修改主要颜色（步骤和进度指示器）
      textColor: "#000", // 修改文字颜色
      tooltipContainer: {
        backgroundColor: "#63e2b7", // 修改提示框背景颜色
      },
      button: {
        backgroundColor: "#63e2b7", // 修改按钮背景颜色
        color: "#ffffff", // 修改按钮文本颜色
      },
    },
  };

  // 选中的项目
  const selectProjectMenuItem = useSelector(
    (state) => state.selectProjectMenuItem
  );


  // 文件列表
  const [fileList, setFileList] = useState([]);

  // 选中文件
  const [activeFile, setActiveFile] = useState(null);

  // 获取资源列表
  const getResourcesList = async () => {
    try {
      let res = await resourcesList(selectProjectMenuItem?.itemId);
      if (res.code === 200) {
        let resData = res.result || [];
        resData.forEach((v) => {
          if (v.fileName.indexOf(".pdf") !== -1) {
            v.fileType = "pdf";
          } else if (v.fileName.indexOf(".doc") !== -1) {
            v.fileType = "doc";
          } else if (v.fileName.indexOf(".docx") !== -1) {
            v.fileType = "doc";
          } else if (v.fileName.indexOf(".txt") !== -1) {
            v.fileType = "txt";
          }
        });
        if (resData.length) {
          setActiveFile(resData[0]);
          setFileList([...resData]);
        } else {
          setActiveFile(null);
          setFileList([]);
        }
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

  // 选择文件
  const chooseFile = (item) => {
    setActiveFile(item);
  };

  // 绑定添加文档弹窗
  const addFileRef = useRef(null);

  // 添加文档
  const addFileFn = () => {
    if (!selectProjectMenuItem) return;
    addFileRef.current.getPage();
  };

  // 删除文档
  const deleteFileItem = async (data) => {
    console.log(data, "data");
    try {
      let res = await deleteResources([data.itemResourceId]);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "删除成功",
        });
        getResourcesList();
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

  const handleJoyrideCallback = (data) => {
    if (data.status === "finished") {
      setRunTutorial(false);

      setLocalStorage("knowledgeGuidanceState", 1);
      // guidance
    }
  };

  useEffect(() => {
    if (selectProjectMenuItem) {
      // 查询资源列表
      getResourcesList();
    }
  }, [selectProjectMenuItem]);
  return (
    <div className={styles.knowledgeBaseWarp}>
      {/* 项目选择 */}
      <div className="knowledge_base-project">
        <div className="knowledge_proj-head">
          <div className="head_title">文件列表</div>
          <div
            className={`knowledge_proj-btn ${
              !selectProjectMenuItem ? "knowledge_proj-btn-disabled" : ""
            }`}
            icon={<PlusCircleOutlined />}
            size="small"
            onClick={addFileFn}
          >
            添加文档
          </div>
        </div>
        <div className="knowledge_proj-content">
          {fileList.map((v) => (
            <div
              title={v.originalName}
              key={v.itemResourceId}
              onClick={() => chooseFile(v)}
              className={`knowledge_proj-item ${
                v.itemResourceId === activeFile?.itemResourceId
                  ? "activeProjectItem"
                  : ""
              }`}
            >
              {v.fileType === "pdf" ? (
                <img className="fileImg" src={pdfIcon2} alt="" />
              ) : v.fileType === "doc" ? (
                <img className="fileImg" src={docIcon} alt="" />
              ) : v.fileType === "txt" ? (
                <img className="fileImg" src={txtIcon} alt="" />
              ) : (
                ""
              )}

              <span className="fileName">{v.originalName}</span>

              <Dropdown
                overlay={
                  <Menu>
                    <Menu.Item
                      onClick={() =>
                        Modal.confirm({
                          title: "尊敬的用户您好",
                          icon: <ExclamationCircleOutlined />,
                          content: "文件删除后不可恢复,是否确认删除?",
                          okText: "确认",
                          onOk() {
                            deleteFileItem(v);
                          },
                          cancelText: "取消",
                        })
                      }
                    >
                      <DeleteOutlined />
                      删除
                    </Menu.Item>
                  </Menu>
                }
                trigger={["click"]}
              >
                <a onClick={(e) => e.preventDefault()}>
                  <Space>
                    <MoreOutlined className="fileDeleteIcon"></MoreOutlined>
                  </Space>
                </a>
              </Dropdown>
            </div>
          ))}
        </div>
      </div>

      {/* pdf渲染 */}
      <div className="knowledge_base-viewPdf">
        {activeFile && activeFile?.fileType === "pdf" ? (
          <iframe
            title="pdf"
            className="pdfIframe"
            src={activeFile?.fileFullUrl}
          ></iframe>
        ) : activeFile?.fileType === "doc" ? (
          // <FileViewer
          //   fileType="docx"
          //   filePath={activeFile.fileFullUrl}
          //   errorComponent={<div>无法预览此文件</div>}
          // />

          <TextFilePreview
            fileUrl={activeFile.fileFullUrl}
            type="doc"
          ></TextFilePreview>
        ) : activeFile?.fileType === "txt" ? (
          <TextFilePreview
            fileUrl={activeFile.fileFullUrl}
            type="txt"
          ></TextFilePreview>
        ) : (
          <Empty></Empty>
        )}
      </div>

      {/* 知识库内容查询 */}
      <div className="knowledge_base-chat">
        <KnowledgeChat activeFile={activeFile}></KnowledgeChat>
      </div>

      {/* 添加文档 */}
      <AddFile getResourcesList={getResourcesList} ref={addFileRef}></AddFile>

      {runTutorial && (
        <Joyride
          steps={runTutorialSteps}
          run={true}
          continuous={true}
          showProgress={true}
          locale={joyrideLocale}
          styles={joyrideStyles} // 使用自定义样式
          callback={handleJoyrideCallback}
        />
      )}
    </div>
  );
};

export default Knowledge;
