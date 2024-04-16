/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-08 10:06:25
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:46:35
 */
import React, {
  useState,
  forwardRef,
  useImperativeHandle, 
  useRef,
} from "react";
import { Modal, Form, message, Upload, Progress } from "antd";
import { useSelector } from "react-redux";
import { updateResources } from "@/api/knowledgeBase";
import { InboxOutlined } from "@ant-design/icons";
import { accountChangeAction } from "@/store/actions/home_action";
import { useDispatch } from "react-redux";
import { messageFn } from "@/utils";
import styles from "./index.module.less";
const { Dragger } = Upload;

const AddOrEdit = forwardRef(({ getResourcesList }, ref) => {
  const dispatch = useDispatch();
  // 选中的项目
  const selectProjectMenuItem = useSelector(
    (state) => state.selectProjectMenuItem
  );

  // 提交文件上传进度
  const [uploadPercent, setUploadPercent] = useState(0);
  const uploadPercentRef = useRef(0);

  // 判断是否上传完毕
  const [upLoadState, setUpLoadState] = useState(false);

  const uploadPercentTimer = useRef(null);
  const [baseForm] = Form.useForm();
  const [pageState, setPageState] = useState(false);

  // 文件上传状态
  const [exceptionState, setExceptionState] = useState("active");
  useImperativeHandle(ref, () => {
    return {
      getPage,
    };
  });

  /**
   * @description: 弹框展示
   * @return {*}
   * @author: jl.g
   */
  const getPage = () => {
    setPageState(true);
    baseForm.resetFields();
    initState();
  };

  // 初始化状态
  const initState = () => {
    setUpLoadState(false);
    setUploadPercent(0);
    uploadPercentRef.current = 0;
    setExceptionState("active");
    if (uploadPercentTimer.current) {
      clearInterval(uploadPercentTimer.current);
    }
  };

  // 上传失败展示状态
  const uploadFaildInitState = () => {
    baseForm.resetFields();
    // 设置进度为100
    setUploadPercent(100);
    uploadPercentRef.current = 100;
    // 执行完毕恢复上传
    setUpLoadState(false);
    // 清除定时器
    if (uploadPercentTimer.current) {
      clearInterval(uploadPercentTimer.current);
    }
  };

  // 上传完毕后 模拟上传进度
  const uploadPercentFn = () => {
    uploadPercentTimer.current = setInterval(() => {
      console.log(1);
      uploadPercentRef.current++;
      if (exceptionState === "exception" || uploadPercentRef.current === 100) {
        clearInterval(uploadPercentTimer.current);
        uploadPercentRef.current = 100;
      }

      setUploadPercent(uploadPercentRef.current);
    }, 500);
  };

  /**
   * @description: 弹框隐藏
   * @return {*}
   * @author: jl.g
   */

  const hidePage = () => {
    setPageState(false);
    initState();
  };

  //   点击确定
  const handleOk = () => {
    if (upLoadState) {
      messageFn({
        type: "error",
        content: "当前文件正在上传中,请稍后",
      });
      return;
    }

    baseForm
      .validateFields()
      .then((values) => {

        if (!determineFileSize(values.file[0])) return false;
        updateResourcesFn(values);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  // 资源上传
  // selectProjectMenuItem
  const updateResourcesFn = async (resData) => {
    try {
      // 禁止上传过程中继续上传
      setUpLoadState(true);
      uploadPercentFn();
      let formData = new FormData();
      formData.append("file", resData.file[0].originFileObj);
      formData.append("itemId", selectProjectMenuItem.itemId);
      let res = await updateResources(formData);
      // 展示进度调

      if (res.code === 200) {
        hidePage();
        messageFn({
          type: "success",
          content: "文件上传成功",
        });
        getResourcesList();

        // 开始查询剩余账户
        dispatch(
          accountChangeAction(new Date().getTime() + "_" + Math.random())
        );
      } else {
        messageFn({
          type: "error",
          content: res.message,
        });
        setExceptionState("exception");
      }
      uploadFaildInitState();
    } catch (error) {
      // 异常情况下展示进度异常样式
      setExceptionState("exception");
      uploadFaildInitState();
      console.log(error);
    }
  };

  const normFile = (e) => {
    if (Array.isArray(e)) {
      return e;
    }
    return e && e.fileList;
  };

  // 判断文件大小
  const determineFileSize = (file) => {
    let limitSize = (file.size / 1024 / 1024).toFixed(0);
    if (limitSize > 20) {
      message.error(`${file.name} 文件过大,请上传20MB内的文件`);
      return false;
    }
    return true;
  };

  const beforeUpload = (file) => {
    setTimeout(() => {
      initState();
    });

    const flag = true;
    if (!flag) {
      message.error(`${file.name} 不是PDF文件`);

      return false;
    }

    if (!determineFileSize(file)) return false;

    return flag || Upload.LIST_IGNORE;
  };

  // 自定义上传
  const customRequest = (option) => {
    initState();
    option.onProgress({ percent: 50 });
    setTimeout(() => {
      option.onProgress({ percent: 100 });
      option.onSuccess();
    }, 1000);
  };

  return (
    <Modal
      title="添加文档"
      width={550}
      visible={pageState}
      onOk={handleOk}
      onCancel={hidePage}
    >
      <p className={styles.uploadFileInfo}>
        上传文档资源后,Ai将会整理文档的内容,建立您的专属知识库
      </p>
      <Form form={baseForm}>
        <Form.Item
          name="file"
          valuePropName="fileList"
          getValueFromEvent={normFile}
          rules={[
            {
              required: true,
              message: "请上传文件",
            },
          ]}
        >
          <Dragger
            disabled={upLoadState}
            accept={".pdf,.docx,.doc,.txt"}
            beforeUpload={beforeUpload}
            multiple={false}
            maxCount={1}
            customRequest={customRequest} //覆盖默认的上传行为，自定义上传实现
          >
            <p className="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p className="ant-upload-text">点击选择文件，或将文件拖拽到这里</p>
            <p className="ant-upload-hint">支持.pdf,.doc,.txt文件</p>
          </Dragger>
        </Form.Item>
      </Form>

      {uploadPercent > 0 ? (
        <div className={styles.uploadProcessWarp}>
          <Progress
            status={exceptionState}
            type="circle"
            percent={uploadPercent}
          />
          <p
            className={`${exceptionState === "active" ? "info" : "errorInfo"}`}
          >
            {exceptionState === "active"
              ? "上传过程中,请勿关闭页面"
              : "文件解析失败,请重新选择文件进行上传"}
          </p>
        </div>
      ) : (
        ""
      )}
    </Modal>
  );
});

export default AddOrEdit;
