/*
 * @Description:
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2024-01-06 10:53:26
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-01-06 15:03:45
 */
import request from "@/utils/request";
// 获取数据
export function getDataApi(params) {
  return request({
    url: "/api/manager/open-api/list",
    method: "get",
    params,
  });
}

// 生成密匙
export function generateKeyPairApi(data) {
  return request({
    url: "/api/manager/open-api/generateKeyPair",
    method: "post",
    data,
  });
}

// 生成商户号
export function generateMchIdApi(data) {
  return request({
    url: "/api/manager/open-api/generateMchId",
    method: "post",
    data,
  });
}

// 保存更新
export function saveOrUpdateApi(data) {
  return request({
    url: "/api/manager/open-api/saveOrUpdate",
    method: "post",
    data,
  });
}

// 复制公私钥
export function copyKeyApi(data) {
  return request({
    url: "/api/manager/open-api/copy",
    method: "post",
    data,
  });
}
// 删除
export function deleteDataApi(id) {
  return request({
    url: `/api/manager/open-api/delete/${id}`,
    method: "DELETE",
  });
}
