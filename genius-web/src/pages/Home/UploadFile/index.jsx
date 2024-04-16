/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-08 10:06:25
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:45:55
 */
import React, {
  useState,
  forwardRef,
  useImperativeHandle,
  useRef,
} from "react";
import { Modal, Form, message, Upload, Progress } from "antd";
import { gptSessionUploadFile, gptSessionBingFileApi } from "@/api/gpt";
import { InboxOutlined } from "@ant-design/icons";
import { messageFn } from "@/utils";
import CustomDialog from "@/components/CustomDialog";
import styles from "./index.module.less";
const { Dragger } = Upload;

const UploadFile = forwardRef(
  ({ changeSessionUploadFileId, activeSession }, ref) => {
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
        uploadPercentRef.current++;
        if (
          exceptionState === "exception" ||
          uploadPercentRef.current === 100
        ) {
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

    // 会话与文件绑定
    const gptSessionBingFileFn = async (fileObj) => {
      try {
        let data = {
          fileId: fileObj?.fileId,
          reqId: activeSession?.reqId,
        };
        let res = await gptSessionBingFileApi(data);
        if (res.code === 200) {
          changeSessionUploadFileId();
          hidePage();
        }
      } catch (error) {
        console.log(error);
      }
    };
    // 资源上传
    const updateResourcesFn = async (resData) => {
      try {
        // 禁止上传过程中继续上传
        setUpLoadState(true);
        uploadPercentFn();
        let formData = new FormData();
        formData.append("file", resData.file[0].originFileObj);
        formData.append(" viewType", 1);
        let res = await gptSessionUploadFile(formData);
        // 展示进度调

        if (res.code === 200) {
          // hidePage();
          messageFn({
            type: "success",
            content: "文件上传成功",
          });
          let resData = res.result;

          gptSessionBingFileFn(resData);
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
              accept={"*"}
              beforeUpload={beforeUpload}
              multiple={false}
              maxCount={1}
              customRequest={customRequest} //覆盖默认的上传行为，自定义上传实现
            >
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">
                点击选择文件，或将文件拖拽到这里
              </p>
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
              className={`${
                exceptionState === "active" ? "info" : "errorInfo"
              }`}
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
  }
);

export default UploadFile;
