/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-08 10:06:25
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-06 23:51:33
 */
import React, {
  useState,
  forwardRef,
  useImperativeHandle,
  useRef,
} from "react";
import _ from "lodash";
import QRCode from "qrcode.react";
import aliPay from "../../../../../public/assets/imgs/ali-pay.png";
import wxPay from "../../../../../public/assets/imgs/wx-pay.png";
import { accountChangeAction } from "@/store/actions/home_action";
import { useSelector, useDispatch } from "react-redux";
import diamondVipIcon from "../../../../../public/assets/imgs/diamondVipIcon.svg";
import styles from "./index.module.less";
import { CloseOutlined } from "@ant-design/icons";
import { messageFn, isMobileDevice } from "@/utils";
import "./index.css";
import { getBuyMember, paymentQuery } from "@/api/commodity";
import MjLoading from "@/components/MjLoading";
import { isWechatSide } from "../../../../utils";
const DialogBuyProduction = forwardRef((props, ref) => {
  const sysConfig = useSelector((state) => state.sysConfig);
  // 是否点击支付按钮
  const [payBtnClickFlag, setPayBtnClickFlag] = useState(false);
  const dispatch = useDispatch();
  const [pageState, setPageState] = useState(false);
  const productionInfo = useRef(null);
  const productType = useRef(null);
  // 支付状态轮询
  const paymentQueryTimerRef = useRef(null);
  // 是否选择
  const [payMethodInfo, setPayMethodInfo] = useState({
    type: "",
    resultUrl: "",
    params: null,
  });

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
  const getPage = (data, resProductType) => {
    setPayBtnClickFlag(false);
    setPageState(true);
    productionInfo.current = data;
    productType.current = resProductType;
  };

  const onBridgeReady = (data) => {
    window.WeixinJSBridge.invoke(
      "getBrandWCPayRequest",
      {
        appId: data.appId, //公众号ID，由商户传入
        timeStamp: data.timeStamp, //时间戳，自1970年以来的秒数
        nonceStr: data.nonceStr, //随机串
        package: data.package,
        signType: data.signType, //微信签名方式：
        paySign: data.paySign, //微信签名
      },
      function (res) {
        if (res.err_msg == "get_brand_wcpay_request:ok") {
          // 使用以上方式判断前端返回,微信团队郑重提示：
          //res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
          hidePage();
        }
      }
    );
  };

  // 初始化微信公众号支付类
  const initWxConfig = (data) => {
    if (typeof WeixinJSBridge == "undefined") {
      if (document.addEventListener) {
        document.addEventListener("WeixinJSBridgeReady", onBridgeReady, false);
      } else if (document.attachEvent) {
        document.attachEvent("WeixinJSBridgeReady", onBridgeReady);
        document.attachEvent("onWeixinJSBridgeReady", onBridgeReady);
      }
    } else {
      onBridgeReady(data);
    }

    // // https://www.jianshu.com/p/784476623299
    // window.wx.config({
    //   debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
    //   appId: data.appId, // 必填，公众号的唯一标识
    //   timestamp: data.timeStamp, // 必填，生成签名的时间戳
    //   nonceStr: data.nonceStr, // 必填，生成签名的随机串
    //   signature: data.paySign, // 必填，签名
    //   jsApiList: ["chooseWXPay"], // 必填，需要使用的JS接口列表chooseWXPay为支付js接口
    // });

    // window.wx.error(function (err) {
    //   console.log("初始化失败！");
    // });

    // window.wx.ready(() => {
    //   // 调用支付函数
    //   window.wx.chooseWXPay({
    //     // appId: data.appId, // 可不发
    //     nonceStr: data.nonceStr, // 支付签名随机串，不长于 32 位
    //     package: data.package, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=\*\*\*）
    //     signType: data.signType, // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
    //     paySign: data.paySign, // 支付签名
    //     timestamp: data.timeStamp, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
    //     success: (res) => {
    //       // 支付成功后的回调函数
    //       hidePage();
    //     },
    //     fail: (res) => {
    //       alert("支付失败");
    //     },
    //   });
    // });
  };

  // 判断是否运行在微信浏览器中
  const isWeiXin = () => {
    var ua = window.navigator.userAgent.toLowerCase();

    if (ua.indexOf("micromessenger") === -1) {
      return false;
    } else {
      return true;
    }
  };

  /**
   * @description: 弹框隐藏
   * @return {*}
   * @author: jl.g
   */

  const hidePage = () => {
    setPageState(false);

    setPayMethodInfo({
      params: null,
      type: "",
      resultUrl: "",
    });

    // 清除定时器
    if (paymentQueryTimerRef.current) {
      clearInterval(paymentQueryTimerRef.current);
    }
    productionInfo.current = null;
    paymentQueryTimerRef.current = null;
  };

  /**
   * @description: 轮询查询支付状态
   * @return {*}
   * @author: jl.g
   */
  const paymentQueryState = async (orderNo) => {
    try {
      let params = {
        orderNo,
      };
      let res = await paymentQuery(params);
      if (res.result === 1) {
        messageFn({
          type: "success",
          content: "充值成功,感谢您的惠顾，可前往个人信息页面查询提问次数。",
        });
        // 开始查询剩余账户
        dispatch(
          accountChangeAction(new Date().getTime() + "_" + Math.random())
        );
        clearInterval(paymentQueryTimerRef.current);
        hidePage();
      }
    } catch (error) {
      console.log(error);
    }
  };

  /**
   * @description: 选择支付方式
   * @return {*} type	 payType 10 会员卡 20 wx流量包
   * @author: jl.g
   */
  const choosePayMethod = async (type) => {
    try {
      let localTradeType = isMobileDevice();
      let data = {
        productId:
          productionInfo.current?.memberId || productionInfo.current?.id,
        payType: type === "wx" ? 10 : type === "ali" ? 20 : "",
        type: productType.current,
      };

      // 微信支付
      if (type === "wx") {
        // 移动支付
        if (localTradeType) {
          // 微信浏览器支付
          if (isWeiXin()) {
            data.tradeType = "JSAPI";
          } else {
            // 其他浏览器支付
            data.tradeType = "MWEB";
          }
        } else {
          // 网站支付
          data.tradeType = "NATIVE";
        }
        // 支付宝支付
      } else if (type === "ali") {
        if (sysConfig.openFacePay) {
          data.tradeType = "";
        } else {
          data.tradeType = localTradeType ? "MWEB" : "";
        }
      }

      //pc端点击支付需要展示loaddign
      if (!localTradeType) {
        setPayBtnClickFlag(true);
      }
      console.log(data, "data");
      let res = await getBuyMember(data);

      if (res.code === 200) {
        const {
          result: { qrCode, orderNo, params },
        } = res;

        // 移动端微信支付需要跳转手机内部进行支付
        if (localTradeType) {
          if (type === "wx") {
            // 微信内置浏览器
            if (isWeiXin()) {
              initWxConfig(params);
            } else {
              // 其他浏览器
              let openUrl = params.mweb_url;
              window.open(openUrl);
            }
          } else {
            if (!sysConfig.openFacePay) {
              var resData = params || null;
              const div = document.createElement("div");
              div.id = "alipay";
              div.innerHTML = resData;
              document.body.appendChild(div);
              document.querySelector("#alipay").children[0].submit(); // 执行后会唤起支付宝
            } else {
              setPayMethodInfo({
                ...payMethodInfo,
                resultUrl: qrCode,
                type,
              });
            }
          }
          // pc端
        } else {
          if (type === "wx") {
            setPayMethodInfo({
              ...payMethodInfo,
              resultUrl: qrCode,
              type,
            });
          } else {
            if (!sysConfig.openFacePay) {
              var resData = params || null;
              const div = document.createElement("div");
              div.id = "alipay";
              div.innerHTML = resData;
              document.body.appendChild(div);
              document.querySelector("#alipay").children[0].submit(); // 执行后会唤起支付宝
            } else {
              setPayMethodInfo({
                ...payMethodInfo,
                resultUrl: qrCode,
                type,
              });
            }
          }

          // 查询订单状态
          paymentQueryTimerRef.current = setInterval(() => {
            paymentQueryState(orderNo);
          }, 2000);
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

  return (
    <>
      {pageState ? (
        <div className={styles.custom_dialog}>
          <div className="custom_dialog-warp ">
            <div className="custom_dialog-head">
              <CloseOutlined
                onClick={hidePage}
                className="closeBtn"
                twoToneColor="#fff"
              ></CloseOutlined>
            </div>

            <div className="title">购买商品</div>

            <div className="custom_dialog-content">
              <p className="product_info">
                您选择的商品为
                <span className="active">
                  {productType.current === 10 ? "会员卡-" : "流量包"}
                  {productionInfo.current.cardName}
                </span>
                , 金额为
                <span className="active">
                  {productionInfo.current.amount ||
                    productionInfo.current.currencyAmount}
                </span>
                元。
              </p>

              {productType.current === 10 &&
              productionInfo.current.rightList.length ? (
                <div className="rightListWarp">
                  <div className="rightList_warp-title">
                    <img
                      className="diamondVipIcon"
                      src={diamondVipIcon}
                      alt=""
                    />
                    会员权益
                  </div>
                  {productionInfo.current.rightList.map((v, index) => (
                    <div key={index} className="rightListitem">
                      {"√ " + v.rightsDesc}
                    </div>
                  ))}
                </div>
              ) : (
                ""
              )}

              {/* 支付方式选择 */}
              {!payMethodInfo.resultUrl ? (
                <div className="pay_method">
                  {sysConfig.wechatPayEnable ? (
                    <button
                      onClick={_.debounce(() => {
                        choosePayMethod("wx");
                      }, 500)}
                      className="btn-shine"
                    >
                      <span className="pay_box">
                        <img className="payIcon" src={wxPay} alt="" />
                        微信支付
                      </span>
                    </button>
                  ) : (
                    ""
                  )}

                  {sysConfig.aliPayEnable ? (
                    <button
                      onClick={_.debounce(() => {
                        choosePayMethod("ali");
                      }, 500)}
                      className="btn-shine"
                    >
                      <span className="pay_box">
                        <img className="payIcon" src={aliPay} alt="" />
                        支付宝支付
                      </span>
                    </button>
                  ) : (
                    ""
                  )}
                </div>
              ) : (
                ""
              )}

              {/* 二维码方式支付 */}
              {payMethodInfo.resultUrl ? (
                <div className="qrCode_box">
                  <p className="pay_title">
                    {payMethodInfo.type === "wx" ? "微信支付" : "支付宝支付"}
                  </p>
                  <QRCode
                    id="qrCode"
                    value={payMethodInfo.resultUrl}
                    size={200} // 二维码的大小
                    fgColor="#000000" // 二维码的颜色
                  />

                  <div>
                    尊敬的用户您好,支付完成前,请勿关闭页面。 <br />
                    如遇到支付后充值未到账 请联系客服。
                  </div>
                </div>
              ) : (
                ""
              )}

              {/* pc端支付 添加loading样式 */}
              {payBtnClickFlag ? (
                <div className="payLoading">
                  <MjLoading></MjLoading>

                  <p className="payLoading_info">
                    操作成功,等待后续操作,如果您已经完成支付,请忽略此提示。
                  </p>
                </div>
              ) : (
                ""
              )}
            </div>
          </div>
        </div>
      ) : (
        ""
      )}
    </>
  );
});

export default DialogBuyProduction;
