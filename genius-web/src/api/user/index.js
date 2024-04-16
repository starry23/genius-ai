/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-06 09:30:54
 * @LastEditors: jl.g
 * @LastEditTime: 2023-08-31 09:09:17
 */
import request from "@/request/index.js";

// 邀请有礼
export function inviteGiftUrlInfo() {
    return request({
        url: "/api/activity/inviteGiftUrlInfo",
        method: "get",
    });
}

// 获取用户信息

export function getUserInfo() {
    return request({
        url: "/api/user/userInfo",
        method: "get",
    });
}

// 获取用户商品信息
export function getMemberUserInfo() {
    return request({
        url: "/api/memberCard/memberUserInfo",
        method: "get",
    });
}

// 建议与反馈
export function sendFeedback(data) {
    return request({
        url: "/api/feedback/send",
        method: "put",
        data
    });
}



// 用户信息
export function myAccount() {
    return request({
        url: "/api/account/myAccount",
        method: "get",
    });

}


// 修改密码
export function updatePassword(data) {
    return request({
        url: "/api/user/updatePassword",
        method: "post",
        data
    });
}

// 查询导航列表
export function getNavList() {
    return request({
        url: "/api/topBarConfig/list",
        method: "get",
    });
}

// 获取账户余额
export function accountBalance() {
    return request({
        url: "/api/account/balance",
        method: "get",
    });
}

// 获取每次消耗的token数量
export function userConsumerLog() {
    return request({
        url: "/api/userConsumerLog/last",
        method: "get",
    });
}

// 弹窗送代币
export function alertGiveCurrency() {
    return request({
        url: "/api/activity/alertGiveCurrency",
        method: "post",
    });
}

// 获取用户消耗提示
export function productConsumedTypeConfig(params) {
    return request({
        url: "/api/productConsumedTypeConfig/consumeDesc",
        method: "get",
        params
    });
}

// 卡密兑换
export function cardRedeem(data) {
    return request({
        url: "/api/card/redeem",
        method: "post",
        data
    });
}

// 消费记录
export function userConsumerLogList(params) {
    return request({
        url: "/api/userConsumerLog/list",
        method: "get",
        params
    });
}