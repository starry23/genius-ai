/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-06-18 00:23:07
 * @LastEditors: jl.g
 * @LastEditTime: 2023-07-22 22:10:06
 */
import request from "@/utils/request";

// 查看列表
export function memberList() {
  return request({
    url: "/api/manager/productConsumeConfigs",
    method: "get",
  })
}

// 商品列表
export function getProductTypeList() {
  return request({
    url: "/api/dict/productType",
    method: "get",
  })
}

// 新增或者修改
export function memberConfigSaveOrUpdate(data) {
  return request({
    url: "/api/manager/productConsumeConfigSaveOrUpdate",
    method: "post",
    data,
  })
}

export function memberConfigDelete(id) {
  return request({
    url: `/api/manager/productConsumeConfig/${id}`,
    method: "delete",
  })
}


// 消费产品上下限
export function productConsumeConfigOnOff(data) {
  return request({
    url: "/api/manager/productConsumeConfig/onOff",
    method: "post",
    data
  })
}
