/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jinglin.gao
 * @Date: 2023-07-02 17:08:55
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-01-06 11:30:04
 */
import request from '@/utils/request'


// 网站配置保存
export function getSetting() {
  return request({
    url: '/api/manager/config/getSetting',
    method: 'get',

  })
}
// 网站配置保存
export function getOssSetting() {
  return request({
    url: '/api/manager/config/getOssSetting',
    method: 'get',

  })
}

// 网站配置保存
export function saveSetting(data) {
  return request({
    url: '/api/manager/config/saveSetting',
    method: 'post',
    data
  })
}
// 网站OSS配置保存
export function saveOssSetting(data) {
  return request({
    url: '/api/manager/config/saveOssSetting',
    method: 'post',
    data
  })
}
// 查询菜单列表
export function getLeftMenuType() {
  return request({
    url: '/api/dict/leftMenuType',
    method: 'get',
  })
}

// 租户信息查询
export function getTenantInfo() {
  return request({
    url: '/api/mj/manager/tenantInfo',
    method: 'get',
  })
}


// 向量数据库配置保存
export function saveOrUpdateMilvus(data) {
  return request({
    url: '/api/manager/config/saveOrUpdateMilvus',
    method: 'post',
    data
  })
}
// 查询配置
export function milvusConfig() {
  return request({
    url: '/api/manager/config/milvus',
    method: 'get',
  })
}


// 向量数据库修改密码
export function updatePassword(data) {
  return request({
    url: '/api/manager/config/updatePassword',
    method: 'post',
    data
  })
}