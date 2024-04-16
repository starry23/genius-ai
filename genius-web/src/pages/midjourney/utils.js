/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-06-28 21:25:24
 * @LastEditors: jl.g
 * @LastEditTime: 2023-07-02 11:56:22
 */
// 转换图片的执行状态
export const transformImgState = (code) => {
    let resData = '未启动'
    switch (code) {
        case "NOT_START":
            resData = '未启动'
            break;
        case "SUBMITTED":
            resData = '已提交'
            break;
        case "IN_PROGRESS":
            resData = '执行中'
            break;
        case "FAILURE":
            resData = '生成失败,次数已返还'
            break;
        case "EXPIRE":
            resData = '图片已过期'
            break;
        case "SUCCESS":
            resData = '成功'
            break;
        default:
            break;
    }
    return resData
}