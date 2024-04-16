/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-09-16 16:55:05
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-12-23 17:20:38
 */
// api/prompt/promptsTemplate

import request from "@/request/index.js";

// 获取提问信息
export function getPromptsTemplate() {
    return request({
        url: "/api/prompt/promptsTemplate",
        method: "get",
    });
}

// 编辑会话名称
export function updateSessionApi(data) {
    return request({
        url: "/api/session/updateSession",
        method: "post",
        data
    });
}
