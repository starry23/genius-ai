/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-05 13:15:13
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-06 23:23:38
 */
import React, { useState, useEffect, useRef } from "react";
import { Modal } from "antd";
import { useSelector } from "react-redux";
import { getProducts, currencyConfigList } from "@/api/commodity";
import { messageFn, formatAmount } from "@/utils";
import hot from "../../../public/assets/commodity/hot.svg";
import diamondVipIcon from "../../../public/assets/imgs/diamondVipIcon.svg";
import vipIcon from "../../../public/assets/commodity/vip.svg";
import trafficIcon from "../../../public/assets/commodity/traffic.svg";
import PayBox from "./components/PayBox";
import styles from "./index.module.less";
import "./index.css";
const Commodity = () => {
  // 用户信息列表
  const sysConfig = useSelector((state) => state.sysConfig);
  // 商品列表
  const [commodityList, setCommodityList] = useState([]);
  const [activeCommodityitem, setActiveCommodityitem] = useState(null);
  // 流量商品列表
  const [currencyList, setCurrencyList] = useState([]);
  const [activecurrencyItem, setActivecurrencyItem] = useState(null);

  // 商品类型
  const [shopTypeList, setShopTypeList] = useState([
    {
      label: "会员卡",
      value: 1,
      info: "",
      icon: vipIcon,
    },
    {
      label: "流量包",
      value: 2,
      info: "(此类商品购买后永久有效)",
      icon: trafficIcon,
    },
  ]);

  const [activeItem, setActiveItem] = useState(1);

  // 支付弹窗
  const payBoxRef = useRef(null);

  // 选择会员卡
  const changeActiveCommodityitem = (data) => {
    setActiveCommodityitem(data);
  };

  // 选择流量包
  const changeActivecurrencyItem = (data) => {
    setActivecurrencyItem(data);
  };

  /**
   * @description: 选中商品
   * @return {*}
   * @author: jl.g
   */
  const chooseCard = (data, productType) => {
    if (sysConfig.aliPayEnable || sysConfig.wechatPayEnable) {
      payBoxRef.current.getPage(data, productType);
    } else {
      Modal.info({
        title: "尊敬的用户您好",
        content: (
          <div
            dangerouslySetInnerHTML={{
              __html: sysConfig.closePayNotice,
            }}
          ></div>
        ),
      });
    }
  };

  /**
   * @description: 获取商品列表
   * @return {*}
   * @author: jl.g
   */

  const getProductsFn = async () => {
    try {
      let res = await getProducts();
      if (res.code === 200) {
        const { result } = res;
        setCommodityList(result);
        if (result.length) {
          setActiveCommodityitem(result[0]);
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

  // 获取流量商品列表
  const currencyConfigListFn = async () => {
    try {
      let res = await currencyConfigList();
      if (res.code === 200) {
        const { result } = res;
        setCurrencyList(result);
        if (result.length) {
          setActivecurrencyItem(result[0]);
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

  const changeTabItem = (data) => {
    setActiveItem(data.value);
  };

  useEffect(() => {
    getProductsFn();
    currencyConfigListFn();
  }, []);
  return (
    <div className={styles.commodity}>
      <div className="content">
        <div className="custom_tabs-warp">
          {shopTypeList.map((item) => (
            <div
              className={`tab-item ${
                item.value === activeItem ? "active_tab-item" : ""
              }`}
              onClick={() => changeTabItem(item)}
              key={item.value}
            >
              <img className="icon" src={item.icon} alt="" />
              <span className="tab_item-name">{item.label}</span>
              <span className="tab_item-info">{item.info}</span>
            </div>
          ))}
        </div>

        {activeItem === 1 ? (
          <div className="content_item">
            <div className="content_item-warp">
              {commodityList.map((v) => (
                <div
                  onClick={() => changeActiveCommodityitem(v)}
                  key={v.memberId}
                  className={`content_item-warp-item  ${
                    activeCommodityitem.memberId === v.memberId
                      ? "content_item-warp-item-active"
                      : ""
                  }`}
                >
                  <div
                    className={`contentWarpHotBg ${
                      v.recommend ? "hotBgImage" : ""
                    } `}
                  >
                    {v.recommend ? <div className="hotIconBox">hot</div> : ""}

                    <div className="count">{v.cardName}</div>
                    <div className="priceBpx" style={{ marginBottom: 20 }}>
                      <span className="newPrice">
                        ￥{formatAmount(v.amount)}
                      </span>
                      {v.lineAmount ? (
                        <span className="oldPrice">
                          ￥{formatAmount(v.lineAmount)}
                        </span>
                      ) : (
                        ""
                      )}
                    </div>

                    <div className="content_item-info-warp">
                      <div className="content_item-info-box">
                        <div className="content_item-info-title">
                          <img
                            className="diamondVipIcon"
                            src={diamondVipIcon}
                            alt=""
                          />
                          尊享权益
                        </div>

                        <div className="content_item-info-context">
                          {v.rightList.map((infoItem, index) => (
                            <div key={index}>{"√ " + infoItem.rightsDesc}</div>
                          ))}
                        </div>
                      </div>
                    </div>

                    <div
                      onClick={() => chooseCard(v, 10)}
                      className="buy_shop-btn"
                    >
                      立即购买
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : (
          ""
        )}

        {activeItem === 2 ? (
          <div className="content_item">
            <div className="content_item-warp ">
              {/* <div className="hotBgImage"></div> */}
              {currencyList.map((v) => (
                <div
                  onClick={() => changeActivecurrencyItem(v)}
                  key={v.id}
                  className={`content_item-warp-item ${
                    activecurrencyItem.id === v.id
                      ? "content_item-warp-item-active"
                      : ""
                  }`}
                >
                  <div
                    className={`contentWarpHotBg ${
                      v.recommend ? "hotBgImage" : ""
                    } `}
                  >
                    {v.recommend ? <div className="hotIconBox">hot</div> : ""}

                    <div className="infoWarp">
                      <div className="coinName">
                        {sysConfig?.currencyName || ""}
                      </div>
                      <div className="count">
                        {formatAmount(v.currencyCount)} 个
                      </div>
                    </div>
                    <div className="priceBpx">
                      <span className="newPrice">￥{v.currencyAmount}</span>
                      {v.lineAmount ? (
                        <span className="oldPrice">￥{v.lineAmount}</span>
                      ) : (
                        ""
                      )}
                    </div>

                    <div
                      onClick={() => chooseCard(v, 20)}
                      className="buy_shop-btn"
                    >
                      立即购买
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : (
          ""
        )}
      </div>

      <PayBox ref={payBoxRef}></PayBox>
    </div>
  );
};

export default Commodity;
