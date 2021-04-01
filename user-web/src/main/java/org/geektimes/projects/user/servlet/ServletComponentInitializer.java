package org.geektimes.projects.user.servlet;

import org.geektimes.projects.user.web.listener.TestingListener;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class ServletComponentInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        servletContext.addListener(TestingListener.class);
    }
}
