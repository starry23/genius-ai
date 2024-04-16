import request from "@/utils/request";

// 获取支付宝配置
export function getPaySetting() {
  return request({
    url: "/api/manager/config/getPaySetting",
    method: "get",
  })
}

// 保存支付宝配置
export function saveOrUpdatePaySetting(data) {
  return request({
    url: "/api/manager/config/saveOrUpdatePaySetting",
    method: "post",
    data
  })
}
