/*
 * @Description: 
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-07-04 09:19:13
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 22:45:20
 */
import moment from "moment";
import _ from "lodash";
// 创建会话 或者手动选择角色时自动创建一条消息
export const autoAnswerQuestion = (roleData, messageObj) => {
    let assistantAnswer = _.cloneDeep(messageObj);
    assistantAnswer.reqId = new Date().getTime() + "_assistantAnswer";
    assistantAnswer.createTime = moment(new Date()).format(
        "YYYY-MM-DD HH:mm:ss"
    );
    assistantAnswer.chatRole = "assistant";

    // 此处需要进行判断 是通过新增会话按钮创建的万能对话,还是通过助手创建的指定角色对话，因为回复的内容不同
    if (roleData) {
        assistantAnswer.content = `${roleData.roleDesc}`;
    } else {
        assistantAnswer.content = `您好,我是您的万能助手,需要我做什么，您可以直接发送问题。`;
    }

    return assistantAnswer
}



/**
 * @description: 提问异常处理
 * @return {*}
 * @author: jl.g
 */
export const rganizeResultsByCode = (res, setMessageState, closeAnswer) => {
    let loginUrl =
        window.location.origin + process.env.REACT_APP_PUBLIC_PATH + "#/login";
    let commodityUrl =
        window.location.origin +
        process.env.REACT_APP_PUBLIC_PATH +
        "#/ai/commodity";
    const {
        code,
        message
    } = res;
    if (!code) return true;

    switch (code) {
        // 登录失败~
        case 4001:
            res.message = message + `[点我登录](${loginUrl})`;
            setMessageState(false);
            closeAnswer();
            break;
            // 请登录后再进行使用
        case 4002:
            res.message = message + `[点我登录](${loginUrl})`;
            setMessageState(false);
            closeAnswer();
            break;
            //没次数
        case 4003:
            res.message = message + `[点我购买](${commodityUrl})`;
            setMessageState(false);
            closeAnswer();
            break;
            // 您的会员已过期，请充值
        case 4004:
            res.message = message + `[点我购买](${commodityUrl})`;
            setMessageState(false);
            closeAnswer();
            break;
            // 您的提问次数不足，请充值
        case 4005:
            res.message = message + `[点我购买](${commodityUrl})`;
            setMessageState(false);
            closeAnswer();
            break;

            // 提问报错
        case 500:
            res.message = "网络出现波动，请您稍后重试。";
            setMessageState(false);
            closeAnswer();
            break;

            //敏感词
        case 2002:
            setMessageState(false);
            closeAnswer();
            break;
        default:
            break;
    }
};