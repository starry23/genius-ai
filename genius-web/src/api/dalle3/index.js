/*
 * @Description:
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-11-18 15:20:26
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-11-20 22:19:18
 */
import request from "@/request/index.js";

// 生成图片
export function generateDalle3Api(data) {
  return request({
    url: "/api/openai-dall-e/generate",
    method: "post",
    data,
  });
}

// 获取生成列表
export function getGenerateRecordApi(data) {
  return request({
    url: "/api/openai-dall-e/getGenerateRecord",
    method: "get",
    data,
  });
}

// 删除图片
export function dalleRemoveApi(params) {
  return request({
    url: "/api/openai-dall-e/remove",
    method: "delete",
    params,
  });
}