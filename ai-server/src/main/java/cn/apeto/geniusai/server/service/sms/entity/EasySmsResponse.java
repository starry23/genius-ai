package cn.apeto.geniusai.server.service.sms.entity;

import lombok.Data;

@Data
public class EasySmsResponse<T> {
    /**
     * 通讯状态
     */
    private Integer Code;
    /**
     * 通讯描述
     */
    private String Message;
    /**
     * 平台返回信息
     */
    private T platformData;

    public EasySmsResponse(Integer code, String message, T platformData) {
        Code = code;
        Message = message;
        this.platformData = platformData;
    }

    public static <T> EasySmsResponse<T> success(T platformData){
        return new EasySmsResponse<>(200,"请求成功", platformData);
    }
}
