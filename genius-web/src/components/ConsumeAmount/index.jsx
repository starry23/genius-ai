/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-08-30 09:04:48
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-12-25 17:56:39
 */
import React, {
  useState,
  forwardRef,
  useImperativeHandle,
  useRef,
} from "react";
import { useDispatch } from "react-redux";
import { userConsumerAction } from "@/store/actions/home_action";
import consumeAmountIcon from "../../../public/assets/imgs/consumeAmountIcon.svg";
import "./index.css";
const ConsumeAmount = forwardRef((props, ref) => {
  const dispatch = useDispatch();

  const timer = useRef(null);
  //   展示动画
  const [anmateState, setAnmateState] = useState(false);

  // 金额
  const [count, setCount] = useState(0);
  useImperativeHandle(ref, () => {
    return {
      consumeAmountFn,
    };
  });
  // 初始化消耗动画
  const consumeAmountFn = (data) => {
    if (timer.current) {
      clearTimeout(timer.current);
    }
    setCount(data);
    setAnmateState(true);
    timer.current = setTimeout(() => {
      setAnmateState(false);

      // 清空查询好的余额
      dispatch(userConsumerAction(null));
    }, 1000);
  };

  return (
    <div
      className={`consumeAmount ${anmateState ? "consumeAmountAnimate" : ""}`}
    >
      <div className="consumeAmountBox">
        <img className="consumeAmountIcon" src={consumeAmountIcon} alt="" />
        消耗 {count}
      </div>
    </div>
  );
});

export default ConsumeAmount;
