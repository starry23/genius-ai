import React, { useState, useRef, useEffect } from "react";
import { Table, Button, Space } from "antd";
import AddOrEdit from "./AddOrEdit";
import _ from "lodash";
import { saveMenusSetting, findMenusSetting } from "@/api/knowledgeBase";
import { messageFn } from "@/utils";
import styles from "./index.module.less";
const ProjectMenu = ({ projetcInfo }) => {
  const addOrEditRef = useRef(null);
  const [tableData, setTableData] = useState([]);

  const addMenu = () => {
    addOrEditRef.current.getPage();
  };

  const onTableDelete = (data, index) => {
    addOrEditRef.current.getPage(data, index);
  };

  const onTableDataDelete = (index) => {
    setTableData((showData) => {
      let copyData = _.cloneDeep(showData);
      copyData.splice(index, 1);
      return [...copyData];
    });
  };

  //  保存菜单配置
  const saveMenusSettingFn = async () => {
    try {
      let data = {
        itemId: projetcInfo.itemId,
        menusDetails: tableData,
      };
      let res = await saveMenusSetting(data);
      if (res.code === 200) {
        messageFn({
          type: "success",
          content: "操作成功",
        });
      } else {
        messageFn({
          type: "error",
          content: "操作失败",
        });
      }
    } catch (error) {
      console.log(error);
    }
  };

  //   查询菜单设置
  const findMenusSettingFn = async () => {
    try {
      let res = await findMenusSetting(projetcInfo.itemId);
      if (res.code === 200) {
        let resData = res.result;
        setTableData(resData.menusDetails || []);
      }
    } catch (error) {
      console.log(error);
    }
  };
  const columns = [
    {
      title: "关键词",
      dataIndex: "prompt",
      key: "prompt",
    },
    {
      title: "回复内容",
      dataIndex: "content",
      key: "content",
    },
    {
      title: "操作",
      dataIndex: "",
      width: 150,
      fixed: "right",
      render: (t, r, index) => {
        return (
          <Space>
            <Button onClick={() => onTableDelete(r, index)} type="link">
              编辑
            </Button>
            <Button onClick={() => onTableDataDelete(index)} type="link">
              删除
            </Button>
          </Space>
        );
      },
    },
  ];

  useEffect(() => {
    findMenusSettingFn();
  }, []);
  return (
    <div className={styles.projectMenuWarp}>
      <Table
        pagination={false}
        rowKey={(record) => record.prompt + "_" + record.content}
        scroll={{ y: "220px" }}
        columns={columns}
        dataSource={tableData}
      />
      <Button onClick={addMenu} className="btnTools" type="dashed">
        添加菜单栏
      </Button>

      <Button onClick={saveMenusSettingFn} className="btnTools" type="primary">
        保存
      </Button>

      <AddOrEdit
        tableData={tableData}
        setTableData={setTableData}
        ref={addOrEditRef}
      ></AddOrEdit>
    </div>
  );
};

export default ProjectMenu;
