package org.geektimes.web.mvc.util;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Utils {
    private Utils(){}
    public static <T> T resolveParameter(HttpServletRequest request, Class<T> tClass) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(tClass, Object.class);
        Constructor<T> constructor = tClass.getConstructor();
        T t = constructor.newInstance();//实例
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            //参数名称
            String name = propertyDescriptor.getName().toLowerCase();
            String parameter = request.getParameter(name);
            //set方法
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (!StringUtils.isEmpty(parameter)) {
                writeMethod.invoke(t,parameter);
            }
        }
        return t;
    }
}
