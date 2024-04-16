/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-02 10:33:59
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-10 13:55:17
 */
import request from "@/request/index.js";

// 生成图片
export function generateImgFn(data) {
  return request({
    url: "/api/mj/imagine",
    method: "post",
    data,
  });
}

// 图片列表
export function getImageList(data) {
  return request({
    url: "/api/mj/images",
    method: "post",
    data,
  });
}

// 获取生成图片任务状态
export function getImageTaskState(params) {
  return request({
    url: "/api/mj/getImage",
    method: "get",
    params,
  });
}

// 图片微调
export function changeGenerateImg(data) {
  return request({
    url: "/api/mj/change",
    method: "post",
    data,
  });
}

// 删除已经绘制好的图片
export function deleteMjGenerateImg(fileId) {
  return request({
    url: `/api/mj/remove/${fileId}`,
    method: "DELETE",
  });
}

// 画图广场
export function imagesSquareList(data) {
  return request({
    url: "/api/mj/imagesSquare",
    method: "post",
    data,
  });
}

// 发布到广场
export function publishState(data) {
  return request({
    url: "/api/mj/publishState",
    method: "post",
    data,
  });
}

//mj消耗info
export function getMjInfos() {
  return request({
    url: "/api/productConsumedTypeConfig/infos",
    method: "get",
  });
}
