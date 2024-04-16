/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-04-30 18:04:32
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-14 16:44:19
 */
import request from "@/utils/request";

// 查看卡列表
export function userInfoList(params) {
  return request({
    url: "/api/manager/userInfoList",
    method: "get",
    params,
  })
}

// 会员充值
export function depositMember(data) {
  return request({
    url: "/api/manager/depositMember",
    method: "post",
    data,
  })
}

// 用户充值列表
export function memberExchangeFn(params) {
  return request({
    url: "/api/manager/memberExchange",
    method: "get",
    params,
  })
}

// 充值虚拟币
export function depositCurrency(data) {
  return request({
    url: "/api/manager/depositCurrency",
    method: "post",
    data,
  })
}

// 账户类型
export function accountType() {
  return request({
    url: "/api/dict/accountType",
    method: "get",
  })
}

// 手动修改拉新数量 返佣等级
export function userCommissionUpdate(data) {
  return request({
    url: "/api/manager/userCommissionUpdate",
    method: "post",
    data
  })
}
