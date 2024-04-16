/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-04-05 13:48:26
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-01-06 20:45:35
 */
import request from "@/request/index.js";
export function getProducts() {
    return request({
        url: "/api/memberCard/members",
        method: "get",
    });
};

// 获取流量列表
export function currencyConfigList() {
    return request({
        url: "/api/currencyConfig/list",
        method: "get",
    });
};


// 获取支付宝二维码
export function getBuyMember(data) {
    return request({
        url: "/api/paymentInfo/buy",
        method: "post",
        data
    });
}


// 查询支付状态

export function paymentQuery(params) {
    return request({
        url: "/api/paymentInfo/paymentQuery",
        method: "get",
        params
    });
}