import request from "@/request/index.js";

export function updateBot(data) {
  return request({
    url: "/api/wechat/bot/bot/saveOrUpdate",
    method: "post",
    data,
  });
}

// 查询机器人
export function getBotDataApi(params) {
  return request({
    url: "/api/wechat/bot/bot/page",
    method: "get",
    params,
  });
}

// 机器人类型
export function getBotProductTypeApi() {
  return request({
    url: "/api/dict/botProductType",
    method: "get",
  });
}

export function botOnlineChangeApi(data) {
  return request({
    url: "/api/wechat/bot/bot/online",
    method: "post",
    data,
  });
}

// 删除
export function deleteBotItemApi(id) {
  return request({
    url: `/api/wechat/bot/bot/${id}`,
    method: "delete",
  });
}
