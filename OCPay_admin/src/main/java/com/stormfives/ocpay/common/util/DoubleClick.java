package com.stormfives.ocpay.common.util;

import com.stormfives.ocpay.common.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by zxb on 03/03/2017.
 */
@Component
public class DoubleClick {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

//    @Autowired
//    private MessageSourceUtil messageSourceUtil;

    /**
     * 所有参数是为英文    默认超时时间是30秒
     * 校检数据是否存在
     *
     * @param bussiness  当前业务描述
     * @param actionName 当前请求动作描述
     * @param message    当前要区分的消息
     * @return true:已存在  false:不存在，并插入成功
     */
    public boolean compareAndSetRequest(String bussiness, String actionName, String message) throws InvalidArgumentException {
        return compareAndSetRequest(bussiness, actionName, message, 30);
    }

    /**
     * 所有参数是为英文
     * 校检数据是否存在
     *
     * @param bussiness  当前业务描述
     * @param actionName 当前请求动作描述
     * @param message    当前要区分的消息
     * @param time       消息排斥时间(在这段时间内,同样的消息不会执行)
     * @return true:已存在  false:不存在，并插入成功
     */
    public boolean compareAndSetRequest(String bussiness, String actionName, String message, Integer time) throws InvalidArgumentException {
        String key = bussiness + actionName + message;
        long value = redisTemplate.opsForValue().increment(key, 1);

        if (value > 1) {
            throw new InvalidArgumentException("Can’t submit again. please try later");
        } else {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        }

    }
}
