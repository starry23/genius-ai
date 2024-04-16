/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2024-03-28 21:26:15
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-30 21:56:26
 */
import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useHistory } from "react-router-dom";
import _ from "lodash";
import styles from "./index.module.less";
const Workbench = () => {
  const history = useHistory();
  const tabsDataListPc = useSelector((state) => state.tabsDataListPc);
  const [workbenchList, setWorkbenchList] = useState([]);

  // 移动端tab切换
  const onTabChange = (data) => {
    history.replace(data.path);
  };

  useEffect(() => {
    if (tabsDataListPc.length) {
      let copyTabsDataListPc = _.cloneDeep(tabsDataListPc);
      //  去除知识库
      copyTabsDataListPc = copyTabsDataListPc.filter((v) => v.id !== 5);
      if (copyTabsDataListPc.length > 3) {
        let workbenchListData = copyTabsDataListPc.slice(
          3,
          copyTabsDataListPc.length - 1
        );

        setWorkbenchList(workbenchListData);
      }
    }
  }, [tabsDataListPc]);
  return (
    <div className={styles.workbenchWarp}>
      <div className="workbench_item">
        <div className="title">工作台</div>
        <div className="workbench_tools">
          {workbenchList.map((item) => (
            <div
              key={item.id}
              onClick={() => onTabChange(item)}
              className="workbench_tool-item"
            >
              <img
                className="workbench_tool-itemIcon"
                src={item.iconAc}
                alt=""
              />
              <span className="workbench_tool-itemName">{item.label}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Workbench;
