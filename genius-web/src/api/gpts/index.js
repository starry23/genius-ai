/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-12-17 13:24:21
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-12-23 11:51:08
 */
import request from "@/request/index.js";

// 获取数据列表
export function getGPTsData(params) {
    return request({
        url: "/api/web/getGpsList",
        method: "get",
        params
    });
}