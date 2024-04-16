/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-06-26 09:32:37
 * @LastEditors: jl.g
 * @LastEditTime: 2023-06-26 09:54:59
 */
import React, { useEffect } from "react";
import { initCanvas } from "./init";
import "./index.css";
const WaveCanvas = () => {
  useEffect(() => {
    initCanvas("waveCanvas");
  }, []);
  return <div id="waveCanvas"></div>;
};

export default WaveCanvas;
