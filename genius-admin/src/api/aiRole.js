import request from "@/utils/request";

// 角色新建
export function saveOrUpdateAiRole(data) {
  return request({
    url: "/api/manager/saveOrUpdateAiRole",
    method: "post",
    data
  });
}

// 获取角色列表
export function getAiRolesList(params) {
  return request({
    url: "/api/manager/aiRoles",
    method: "get",
    params
  });
}

// 删除角色
export function aiRoleDelete(id) {
  return request({
    url: `/api/manager/aiRole/${id}`,
    method: "DELETE",
  });
}

// 获取角色类型
export function getAiRoleType() {
  return request({
    url: "/api/dict/roleType",
    method: "get",
  });
}
