/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-06-14 13:09:28
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-04 19:59:59
 */
import React, { useState, useRef } from "react";
import { Select, Row, Col, Tag } from "antd";
// mj 模型选择
import mjModelImg from "../../../../../public/assets/imgs/mj.png";
import nijiModelImg from "../../../../../public/assets/imgs/niji.png";
import styles from "./index.module.less";
import GetImgBase64 from "../getImgBase64";
import {
  angleList,
  characterShotsList,
  lightingList,
  creameList,
} from "./detialData";
import { useDispatch, useSelector } from "react-redux";
import { isMobileDevice } from "@/utils";
import { DeleteOutlined } from "@ant-design/icons";

const MjGenerateSetting = ({
  promptObj,
  generateImgSize,
  generateImgModel,
  detailSettings,
}) => {
  // 账户信息
  const accountInfo = useSelector((state) => state.accountInfo);
  //   选中的尺寸
  const [selectedSize, setSelectedSize] = useState(generateImgSize.current);
  let backUpDetialSetting = {
    // 风格
    style: null,
    // 视角
    angle: null,
    // 人物镜头
    characterShots: null,
    // 相机:"",
    creame: null,
    // 灯光
    lighting: null,

    // 画质
    pictureQuality: null,
    // 艺术程度
    artisticLevel: null,
  };
  // 详细设置
  const [defaultDetailData, setDefaultDetailData] =
    useState(backUpDetialSetting);

  // 尺寸数据
  const sizeList = useRef([
    {
      label: "1:1",
      id: 1,
      value: " --ar 1:1",
      icon: <span className="proportion11"></span>,
    },

    {
      label: "4:3",
      id: 2,
      value: " --ar 4:3",
      icon: <span className="proportion43"></span>,
    },

    {
      label: "4:4",
      id: 3,
      value: " --ar 4:4",
      icon: <span className="proportion44"></span>,
    },
    {
      label: "16:9",
      id: 4,
      value: " --ar 16:9",
      icon: <span className="proportion169"></span>,
    },

    {
      label: "9:16",
      id: 5,
      value: " --ar 9:16",
      icon: <span className="proportion916"></span>,
    },
  ]);

  // 选中的的模型
  const [selectedModel, setSelectedModel] = useState(generateImgModel.current);
  // 模型类型
  const modelList = useRef([
    {
      id: 0,
      title: "MJ",
      info: "通用写实风格MJ",
      value: "",
      icon: mjModelImg,
    },

    {
      id: 1,
      title: "NIJI",
      info: "偏动漫风格、适用于二次元模型",
      value: " --niji 5",
      icon: nijiModelImg,
    },
  ]);
  // 清空所有设置
  const handlerClearSetting = () => {
    setDefaultDetailData(backUpDetialSetting);
    detailSettings.current = {
      ...backUpDetialSetting,
    };
  };
  // 选择风格
  const onStylesChange = (data) => {
    detailSettings.current.style = data;

    setDefaultDetailData((settings) => {
      return {
        ...settings,
        style: data,
      };
    });
  };

  // 选择视角
  const onAngleListChange = (data) => {
    detailSettings.current.angle = data;
    setDefaultDetailData((settings) => {
      return {
        ...settings,
        angle: data,
      };
    });
  };

  // 选择人物镜头
  const onCharacterShotsListChange = (data) => {
    detailSettings.current.characterShots = data;
    setDefaultDetailData((settings) => {
      return {
        ...settings,
        characterShots: data,
      };
    });
  };

  // 选择相机
  const onCreameListChange = (data) => {
    detailSettings.current.creame = data;
    setDefaultDetailData((settings) => {
      return {
        ...settings,
        creame: data,
      };
    });
  };

  // 选择灯光
  const onLightingListChange = (data) => {
    detailSettings.current.lighting = data;
    setDefaultDetailData((settings) => {
      return {
        ...settings,
        lighting: data,
      };
    });
  };

  // 选择画质
  const onPictureQualityListChange = (data) => {
    detailSettings.current.pictureQuality = data;
    setDefaultDetailData((settings) => {
      return {
        ...settings,
        pictureQuality: data,
      };
    });
  };

  // 选择艺术程度
  const onArtisticLevelListChange = (data) => {
    detailSettings.current.artisticLevel = data;
    setDefaultDetailData((settings) => {
      return {
        ...settings,
        artisticLevel: data,
      };
    });
  };

  //   选择尺寸
  const selectSizeItem = (data) => {
    generateImgSize.current = data;
    setSelectedSize(data);
  };

  return (
    <div className={styles.mjGenerateSetting}>
      <div className="base_setting">
        <div className="setting_box">
          <div className="title">尺寸选择</div>
          <div className="content">
            {sizeList.current.map((v) => (
              <div
                key={v.id}
                onClick={() => selectSizeItem(v)}
                className={`size_setting-item ${
                  selectedSize.id === v.id ? "selected_size_setting-item" : ""
                }`}
              >
                {v.icon}
                <div>{v.label}</div>
              </div>
            ))}
          </div>
        </div>

        <div className="setting_box-img">
          <div className="title">模型选择</div>

          <div className="content">
            {modelList.current.map((v) => (
              <div
                key={v.id}
                onClick={() => {
                  generateImgModel.current = v;
                  setSelectedModel(v);
                }}
                className={`settingBox-item ${
                  v.id === selectedModel.id ? "selectedModel" : ""
                }`}
              >
                <div className="settingBox_item-shwoBox">
                  <img className="modelImg" src={v.icon} alt="" />
                  <span className="modelTitle">{v.title}</span>
                </div>
                {/* <div className="settingBox_item-info">{v.info}</div> */}
              </div>
            ))}
          </div>
        </div>

        <div className="setting_box-img">
          <div className="title flex_title">
            <span className="title">细节设置</span>
            <Tag
              style={{
                cursor: "pointer",
              }}
              onClick={handlerClearSetting}
              icon={<DeleteOutlined></DeleteOutlined>}
              color="#108ee9"
            >
              清空
            </Tag>
          </div>

          <div className="setting_content">
            <Row
              className="setting_content-item"
              justify="space-between"
              align="center"
            >
              <Col>人物</Col>
              <Col>
                <Select
                  value={defaultDetailData.characterShots}
                  allowClear
                  options={characterShotsList}
                  style={{ width: 120 }}
                  size="small"
                  placeholder="请选择"
                  onChange={onCharacterShotsListChange}
                ></Select>
              </Col>
            </Row>

            <Row
              className="setting_content-item"
              justify="space-between"
              align="center"
            >
              <Col>视角</Col>
              <Col>
                <Select
                  value={defaultDetailData.angle}
                  allowClear
                  options={angleList}
                  style={{ width: 120 }}
                  size="small"
                  placeholder="请选择"
                  onChange={onAngleListChange}
                ></Select>
              </Col>
            </Row>

            <Row
              className="setting_content-item"
              justify="space-between"
              align="center"
            >
              <Col>光线</Col>
              <Col>
                <Select
                  value={defaultDetailData.lighting}
                  allowClear
                  options={lightingList}
                  style={{ width: 120 }}
                  size="small"
                  placeholder="请选择"
                  onChange={onLightingListChange}
                ></Select>
              </Col>
            </Row>

            <Row
              className="setting_content-item"
              justify="space-between"
              align="center"
            >
              <Col>相机</Col>
              <Col>
                <Select
                  value={defaultDetailData.creame}
                  allowClear
                  options={creameList}
                  style={{ width: 120 }}
                  size="small"
                  placeholder="请选择"
                  onChange={onCreameListChange}
                ></Select>
              </Col>
            </Row>

            <Row>
              其他参数： <br />1 --no 忽略 --no car 图中不出现车 <br />2 --seed
              可先获取种子--seed 123456 <br />3 --chaos 10 混合(范围：0-100){" "}
              <br />4 --tile 碎片化
            </Row>
          </div>
        </div>

        <GetImgBase64 promptObj={promptObj}></GetImgBase64>
      </div>

      {/* pc端账户余额 */}
      {/* {isMobileDevice() ? (
        ""
      ) : (
        <div className="account_info">
          <span className="residue"> 剩余绘画次数: {accountInfo?.mjBalance.toFixed(0) || 0} 次</span>
          <span className="buy">充值</span>
        </div>
      )} */}
    </div>
  );
};

export default MjGenerateSetting;
