import React, { useState, useEffect } from "react";
import { Table } from "antd";
import { userConsumerLogList } from "@/api/user";
import { messageFn } from "@/utils";
const ConsumptionLog = () => {
  const [tableParams, setTableParams] = useState({
    size: 10,
    current: 1,
    total: 0,
    tableData: [],
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

  //   分页切换
  const handleTableChange = ({ current }) => {
    setTableParams((data) => ({
      ...data,
      current,
    }));
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

  useEffect(() => {
    userConsumerLogListFn();
  }, [tableParams.current]);

  return (
    <div className="consumptionLogWarp">
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
          y: 400,
        }}
        columns={columns}
        dataSource={tableParams.tableData}
      />
    </div>
  );
};

export default ConsumptionLog;
