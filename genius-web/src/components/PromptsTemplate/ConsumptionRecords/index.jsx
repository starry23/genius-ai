/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-08 10:06:25
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-05 23:54:45
 */
import React, {
  useState,
  forwardRef,
  useImperativeHandle,
  useEffect,
} from "react";
import { Modal, Table } from "antd";
import { userConsumerLogList } from "@/api/user";
import { messageFn } from "@/utils";
const ConsumptionRecords = forwardRef((props, ref) => {
  const [pageState, setPageState] = useState(false);
  const [tableParams, setTableParams] = useState({
    size: 10,
    current: 1,
    total: 0,
    tableData: [],
  });
  useImperativeHandle(ref, () => {
    return {
      getPage,
    };
  });

  const columns = [
    {
      title: "商品类型",
      dataIndex: "productTypeDesc",
      key: "productTypeDesc",
    },
    {
      title: "原价",
      dataIndex: "originalAmount",
      key: "originalAmount",
    },
    {
      title: "会员价",
      dataIndex: "memberAmount",
      key: "memberAmount",
    },

    {
      title: "实付",
      dataIndex: "realAmount",
      key: "realAmount",
    },

    {
      title: "优惠",
      dataIndex: "discountAmount",
      key: "discountAmount",
    },
  ];

  /**
   * @description: 弹框展示
   * @return {*}
   * @author: jl.g
   */
  const getPage = () => {
    setPageState(true);
    userConsumerLogListFn();
    setTableParams({
      size: 10,
      current: 1,
      total: 0,
      tableData: [],
    });
  };

  //   查询消费记录
  const userConsumerLogListFn = async () => {
    try {
      let params = {
        size: tableParams.size,
        current: tableParams.current,
      };
      let res = await userConsumerLogList(params);
      if (res.code === 200) {
        setTableParams((data) => ({
          ...data,
          total: res.result.total,
          tableData: res.result.records || [],
        }));
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

  //   关闭
  const handlerClose = () => {
    setPageState(false);
  };

  //   分页切换
  const handleTableChange = ({ current }) => {
    setTableParams((data) => ({
      ...data,
      current,
    }));
  };

  useEffect(() => {
    pageState && userConsumerLogListFn();
  }, [tableParams.current]);

  return (
    <Modal
      onCancel={handlerClose}
      footer={null}
      title="消费日志"
      visible={pageState}
    >
      <Table
        rowKey={(record, index) => index}
        pagination={{
          //分页
          pageSize: tableParams.size, //显示几条一页
          current: tableParams.current,
          total: tableParams.total,
          showTotal: (total, range) => {
            return "共 " + total + " 条";
          },
        }}
        onChange={handleTableChange}
        scroll={{
          y: 300,
        }}
        columns={columns}
        dataSource={tableParams.tableData}
      />
    </Modal>
  );
});

export default ConsumptionRecords;
