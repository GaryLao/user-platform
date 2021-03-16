package org.geektimes.web.mvc;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 处理方法信息类
 *
 * @since 1.0
 */
public class HandlerMethodInfo {

    private final String requestPath;

    private Method handlerMethod;

    private Set<String> supportedHttpMethods;

    private Class<?> type;

    private boolean isValidator;

    private String validateMethod;

    public HandlerMethodInfo(String requestPath, Method handlerMethod, Set<String> supportedHttpMethods) {
        this.requestPath = requestPath;
        this.handlerMethod = handlerMethod;
        this.supportedHttpMethods = supportedHttpMethods;
    }

    public HandlerMethodInfo(String requestPath, Method method, Set<String> supportHttpMethods, Class type, String validateMethod) {
        this.requestPath = requestPath;
        this.handlerMethod = method;
        this.supportedHttpMethods = supportHttpMethods;
        this.type=type;
        this.isValidator=true;
        this.validateMethod=validateMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public Set<String> getSupportedHttpMethods() {
        return supportedHttpMethods;
    }

    public Method getMethod() {
        return handlerMethod;
    }

    public void setMethod(Method method) {
        this.handlerMethod = method;
    }

    public void setSupportHttpMethods(Set<String> supportHttpMethods) {
        this.supportedHttpMethods = supportHttpMethods;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isValidator() {
        return isValidator;
    }

    public void setValidator(boolean validator) {
        isValidator = validator;
    }

    public String getValidateMethod() {
        return validateMethod;
    }

    public void setValidateMethod(String validateMethod) {
        this.validateMethod = validateMethod;
    }
}
