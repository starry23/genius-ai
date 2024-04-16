/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-07-02 17:08:55
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-07-16 12:08:04
 */
import request from "@/utils/request";

// 查询模型
export function chatModelList() {
  return request({
    url: "/api/manager/chatModel",
    method: "get",
  });
}
// 保存配置
export function saveOrUpdateChatConfig(data) {
  return request({
    url: "/api/manager/config/saveOrUpdateChatConfig",
    method: "post",
    data
  });
}

// 获取已经保存的配置
export function getChatConfig() {
  return request({
    url: "/api/manager/config/chatConfig",
    method: "get"
  });
}
