/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-25 12:07:37
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-06-25 12:07:59
 */
import request from "@/utils/request";

// 查看卡列表
export function dataList(params) {
  return request({
    url: "/api/mj/manager/list",
    method: "get",
    params,
  })
}
