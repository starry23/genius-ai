/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-04-30 18:04:32
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-07-09 17:11:34
 */
import request from "@/utils/request";

// 查看列表
export function getDataList(params) {
  return request({
    url: "/api/manager/card/list",
    method: "get",
    params,
  })
}

// 新建
export function createRedemptionCode(data) {
  return request({
    url: "/api/manager/card/create",
    method: "post",
    data,
  })
}

// 作废兑换码
export function cancelInvalidateCode(id) {
  return request({
    url: "/api/manager/card/invalidate/" + id,
    method: "DELETE",

  })
}

// 导出兑换码
export function exportByBatchNo(batchNo) {
  return request({
    url: `/api/manager/card/export/${batchNo}`,
    method: "get",
    responseType: 'blob', // 指定响应类型为blob（二进制）
  })
}
