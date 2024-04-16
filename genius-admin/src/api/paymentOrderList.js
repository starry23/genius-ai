/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-04-30 18:04:32
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2023-07-01 11:24:57
 */
import request from "@/utils/request";

// 查看卡列表
export function managerOrders(params) {
  return request({
    url: "/api/manager/orders",
    method: "get",
    params,
  })
}


// 删除无效订单
export function deleteUselessOrders() {
  return request({
    url: "/api/manager/delete/useless/orders",
    method: "DELETE",
  })
}
