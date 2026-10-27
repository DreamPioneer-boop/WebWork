package com.fitness.common;

/**
 * 业务异常：业务规则不满足时抛出，由 Servlet 捕获后提示给用户。
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
