/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-08 10:06:25
 * @LastEditors: jl.g
 * @LastEditTime: 2023-06-02 10:26:02
 */
import React, {
  useState,
  forwardRef,
  useImperativeHandle,
  useRef,
} from "react";
import { Input, Button } from "antd";
import styles from "./index.module.less";
import { getAiRoleList } from "@/api/assistantsList";
import { CloseOutlined } from "@ant-design/icons";
import { useSelector } from "react-redux";
const AssistantsList = forwardRef((props, ref) => {
  // 创建会话
  const storeCreateSessionFn = useSelector(
    (store) => store.storeCreateSessionFn
  );
  const [templateList, setTemplateList] = useState([]);
  const templateListRef = useRef([]);
  const templateSearchRef = useRef();
  const [pageState, setPageState] = useState(false);

  useImperativeHandle(ref, () => {
    return {
      getPage,
    };
  });

  // 选择角色
  const chooseRole = (data) => {
    storeCreateSessionFn(data,);
    setPageState(false);
  };

  /**
   * @description: 搜索框内容
   * @return {*}
   * @author: jl.g
   */
  const templateSearch = (e) => {
    templateSearchRef.current = e.target.value;
    if (!templateSearchRef.current) {
      setTemplateList(templateListRef.current);
    }
  };

  // 获取角色列表
  const getAiRoleListFn = async () => {
    try {
      let res = await getAiRoleList();
      if (res.code === 200) {
        setTemplateList(res.result || []);
        templateListRef.current = res.result || [];
      }
    } catch (error) {
      console.log(error);
    }
  };

  /**
   * @description: 弹框展示
   * @return {*}
   * @author: jl.g
   */
  const getPage = () => {
    setPageState(true);
    getAiRoleListFn();
  };

  /**
   * @description: 弹框隐藏
   * @return {*}
   * @author: jl.g
   */

  const hidePage = () => {
    setPageState(false);
  };

  /**
   * @description: 搜索数据过滤
   * @return {*}
   * @author: jl.g
   */
  const searchTemplateFn = () => {
    if (!templateSearchRef.current) {
      setTemplateList(templateListRef.current);
    } else {
      let filterDataList = templateList.filter(
        (v) =>
          v.roleName.indexOf(templateSearchRef.current) !== -1 ||
          v.roleDesc.indexOf(templateSearchRef.current) !== -1
      );
      setTemplateList(filterDataList);
    }
  };

  return (
    <>
      {pageState ? (
        <div className={styles.assistantsList_custom_dialog}>
          <div className="assistantsList_custom_dialog-warp animate__animated animate__backInLeft">
            <div className="custom_dialog-head">
              <span className="title">助手列表</span>
              <CloseOutlined
                onClick={hidePage}
                className="closeBtn"
                twoToneColor="#fff"
              ></CloseOutlined>
            </div>

            <div className="custom_dialog-content">
              <div className="search-item">
                <Input.Group compact>
                  <Input
                    style={{
                      width: "calc(100% - 64px)",
                    }}
                    onPressEnter={() => {
                      searchTemplateFn();
                    }}
                    onChange={templateSearch}
                    placeholder="请输入您想使用的模板"
                  />
                  <Button onClick={searchTemplateFn} type="primary">
                    搜索
                  </Button>
                </Input.Group>
              </div>

              <div className="template_warp">
                {templateList.map((v) => (
                  <div
                    onClick={() => chooseRole(v)}
                    key={v.roleName}
                    className="template_warp-item"
                  >
                    <div className="template_title">{v.roleName}</div>
                    <div className="template_desc">{v.roleDesc}</div>
                  </div>
                ))}
              </div>

            </div>
          </div>
        </div>
      ) : (
        ""
      )}
    </>
  );
});

export default AssistantsList;
