/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-06-01 10:55:32
 * @LastEditors: jl.g
 * @LastEditTime: 2023-07-01 23:32:09
 */
import request from "@/request/index.js";

// 角色列表
export function getAiRoleList(params) {
    return request({
        url: "/api/ai-role/roles",
        method: "get",
        params
    });
}


// 角色分类
export function getRoleType() {
    return request({
        url: "/api/dict/roleType",
        method: "get",
    });
}