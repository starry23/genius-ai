package cn.apeto.geniusai.server.exception;

/**
 * @author wanmingyu
 * @create 2024/2/1 8:03 下午
 */
public class MjException extends ServiceException{
    public MjException(String message) {
        super(500, message);
    }
}
