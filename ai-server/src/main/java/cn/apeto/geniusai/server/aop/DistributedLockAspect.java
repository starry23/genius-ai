package cn.apeto.geniusai.server.aop;

import cn.dev33.satoken.stp.StpUtil;
import cn.apeto.geniusai.server.annotations.DistributedLock;
import cn.apeto.geniusai.server.domain.CommonRespCode;
import cn.apeto.geniusai.server.exception.ServiceException;
import cn.apeto.geniusai.server.utils.RedissonUtils;
import cn.apeto.geniusai.server.utils.SpringElUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * <p>
 * 分布式锁拦截：自定义注解支持SPEL
 * </p>
 *
 * @author apeto
 */
@Aspect
@Component
public class DistributedLockAspect {

  @Around("@annotation(cn.apeto.geniusai.server.annotations.DistributedLock)")
  public Object around (ProceedingJoinPoint p) throws Throwable {
    String totalKey = null;

    MethodSignature sign = (MethodSignature) p.getSignature();
    Method method = sign.getMethod();

    DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
    try {
      String keyBySpEL = SpringElUtil.generateKeyBySpEL(distributedLock.key(), p);
      boolean reqUserId = distributedLock.isReqUserId();
      totalKey = distributedLock.prefix().getKey(keyBySpEL, (reqUserId ? StpUtil.getLoginIdAsLong() : ""));
    } catch (Exception ignored) {
    }
    if (StringUtils.isBlank(totalKey)) {
      return p.proceed();
    }

    try {
      boolean lock = RedissonUtils.tryLockBoolean(totalKey, distributedLock.waitFor(), distributedLock.expire());
      if (!lock) {
        throw new ServiceException(CommonRespCode.REQUEST_FREQUENTLY);
      }
      return p.proceed();
    } finally {
      RedissonUtils.unlock(totalKey);
    }
  }

}
