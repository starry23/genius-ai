/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-04-30 18:04:32
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-06-18 01:50:07
 */
import request from "@/utils/request";

// 查看卡列表
export function dataList(params) {
  return request({
    url: "/api/manager/accounts",
    method: "get",
    params,
  })
}


// 查询账户详情

export function accountLogs(params) {
  return request({
    url: "/api/manager/accountLogs",
    method: "get",
    params,
  })
}

// 操作类型
export function directionType() {
  return request({
    url: "/api/dict/directionType",
    method: "get",

  })
}


// 日志操作类型

export function logDescriptionType() {
  return request({
    url: "/api/dict/logDescriptionType",
    method: "get",

  })
}

// 获取账户类型
export function getAccountTypeList() {
  return request({
    url: "/api/dict/accountType",
    method: "get",

  })
}


// 返佣
export function commissionWithdraw(data) {
  return request({
    url: "/api/manager/commissionWithdraw",
    method: "post",
    data
  })
}
