/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-08-06 13:41:54
 * @LastEditors: jl.g
 * @LastEditTime: 2023-09-19 10:19:48
 */
import request from "@/request/index.js";

// 创建项目
export function knowledgeCreateItem(data) {
    return request({
        url: "/api/knowledge/createItem",
        method: "put",
        data
    });
}

// 项目列表
export function knowledgeProJectList() {
    return request({
        url: "/api/knowledge/items",
        method: "get",
    });
}

// 删除项目
export function deleteProjectItemFn(id) {
    return request({
        url: `/api/knowledge/item/${id}`,
        method: "delete",
    });
}

// 资源列表
export function resourcesList(itemId) {
    return request({
        url: `/api/knowledge/resources/${itemId}`,
        method: "get",
    });
}
// 上传资源
export function updateResources(data) {
    return request({
        url: "/api/knowledge/updateResources",
        method: "post",
        headers: {
            "Content-Type": "multipart/form-data",
        },
        data
    });
}

// 删除资源
export function deleteResources(data) {
    return request({
        url: `/api/knowledge/deleteResources`,
        method: "delete",
        data
    });
}

// 保存项目设置
export function knowledgeBaseSetting(data) {
    return request({
        url: "/api/knowledge/baseSetting",
        method: "post",
        data
    });
}

// 获取分享信息
export function getShareInfo(params) {
    return request({
        url: "/api/share/info",
        method: "get",
        params
    });
}

// 分享信息设置
export function shareSetting(data) {
    return request({
        url: "/api/share/setting",
        method: "post",
        data
    });
}


// 获取docx
export function downloadOssProxy(params) {
    return request({
        url: "/api/download/ossProxy",
        method: "get",
        params,
        headers: {
            Accept: "application/octet-stream",
        },
        responseType: 'blob'
    });
}

// 欢迎词设置
export function welcomeSetting(data) {
    return request({
        url: "/api/knowledge/welcomeSetting",
        method: "post",
        data
    });
}

// 查询欢迎词
export function getWelcomeSetting(itemId) {
    return request({
        url: `/api/knowledge/welcomeSetting/${itemId}`,
        method: "get",
    });
}
// 获取欢迎设置和菜单设置
export function getWelcomeAndMenus(uuid) {
    return request({
        url: `/api/knowledge/welcomeAndMenus/${uuid}`,
        method: "get",
    });
}


// 知识库分享页面菜单栏设置
export function saveMenusSetting(data) {
    return request({
        url: "/api/knowledge/menusSetting",
        method: "post",
        data
    });
}
// 查询菜单设置
export function findMenusSetting(itemId) {
    return request({
        url: `/api/knowledge/menusSetting/${itemId}`,
        method: "post",
    });
}
// 