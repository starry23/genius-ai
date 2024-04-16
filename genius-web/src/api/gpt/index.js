/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-03-29 17:36:11
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-02-01 15:49:15
 */
import request from "@/request/index.js";

export function questions(params) {
  return request({
    url: "/api/chat/questions",
    method: "get",
    params,
  });
}
// 查询会话
export function sessionRecordSidebar(params) {
  return request({
    url: "/api/session/sessionRecordSidebar",
    method: "get",
    params,
  });
}

// 新建会话

export function createSession(data) {
  return request({
    url: "/api/session/createSession",
    method: "post",
    data,
  });
}

// 查询会话当前内容
export function getSessionDetail(params) {
  return request({
    url: "/api/session/sessionDetail",
    method: "get",
    params,
  });
}

// 删除会话
export function removeSession(params) {
  return request({
    url: "/api/session/removeSession",
    method: "delete",
    params,
  });
}

// 获取广告列表
export function getAdvertiseList() {
  return request({
    url: "/api/advertise/list",
    method: "get",
  });
}

// 获取模型类型
export function productConsumedType() {
  return request({
    url: "/api/dict/productConsumedType",
    method: "get",
  });
}

// 删除全部会话
export function removeAllSession() {
  return request({
    url: "/api/session/removeAllSession",
    method: "DELETE",
  });
}
// 导出聊天记录
export function downloadReqSession(params) {
  return request({
    url: "/api/download/reqSession",
    method: "get",
    params,
    responseType: "blob", // 指定响应类型为blob（二进制）
  });
}
// 文件上传
export function gptSessionUploadFile(data) {
  return request({
    url: "/api/upload/file",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data",
    },
    data,
  });
}

// get会话与文件绑定
export function gptSessionBingFileApi(data) {
  return request({
    url: "/api/session/sessionRecordSidebar/binding/file",
    method: "post",
    data,
  });
}

// 查询当前会话的最新信息
export function updateSessionInfoByReqIdApi(reqId) {
  return request({
    url: `/api/session/sessionRecordSidebar/info/${reqId}`,
    method: "get",
  });
}

// 清空附件
export function clearSessionFile(reqId, fileIds) {
  return request({
    url: `/api/session/sessionRecordSidebar/empty/file/${reqId}/${fileIds}`,
    method: "delete",
  });
}
