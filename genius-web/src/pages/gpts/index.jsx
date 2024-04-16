/*
 * @Description:
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-12-17 11:32:59
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-09 19:56:04
 */
import React, { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import { getGPTsData } from "@/api/gpts";
import CustomInput from "@/components/CustomInput";

import { RightCircleOutlined } from "@ant-design/icons";
import styles from "./index.module.less";
const Gpts = () => {
  const history = useHistory();
  //   数据列表
  const [dataList, setDateList] = useState([]);
  // 搜索框
  const [searchData, setSearchData] = useState("");

  //   获取数据
  const getData = async () => {
    try {
      let params = {
        query: searchData,
      };
      let res = await getGPTsData(params);
      if (res.code === 200) {
        setDateList(res.result?.gpts || []);
      }
    } catch (error) {
      console.log(error);
    }
  };

  //   点击gpt
  const onHandlerGPTsItemClick = (item) => {
    let roleData = {
      id: -1,
      imageUrl: item.logo,
      roleDesc: item.info,
      roleName: item.name,
    };
    history.replace({
      pathname: "/ai/home",
      state: {
        roleData: roleData,
        productConsumedTypeitem: {
          key: 9,
          value: "gps",
          gpts: item,
        },
      },
    });
  };

  useEffect(() => {
    getData();
  }, []);

  return (
    <div className={styles.gptsWarp}>
      <div className="gpts_header">
        <div className="gpts_header-title">最全的中文GPTs商店</div>
        <div className="gpts_header-subtitle">你想要的这里全部都有</div>

        <CustomInput
          style={{
            height: "40px",
            marginBottom: "25px",
          }}
          showSearch
          onChange={(value) => setSearchData(value)}
          search={getData}
          placeholder="搜索你想要的内容"
        ></CustomInput>
      </div>

      <div className="gpts_content-warp">
        <div className="gpts_content">
          {dataList.map((v, index) => (
            <div
              key={index}
              className="gpts_content-item"
              onClick={() => onHandlerGPTsItemClick(v)}
            >
              <div className="gpts_content-box">
                <img className="gpts_item-img" src={v.logo} alt="" />
                <div className="gpts_item-info">
                  <div className="gpts_item-info-top">
                    <span className="badge" title="新">
                      N
                    </span>
                    <span className="name">{v.name}</span>
                  </div>
                  <div className="gpts_item-info-bottom">{v.info}</div>
                </div>
                <RightCircleOutlined></RightCircleOutlined>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Gpts;
