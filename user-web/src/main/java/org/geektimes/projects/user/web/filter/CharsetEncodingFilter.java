package org.geektimes.projects.user.web.filter;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 字符编码 Filter
 */
public class CharsetEncodingFilter implements Filter {

    private String encoding = null;

    private ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.encoding = filterConfig.getInitParameter("encoding");
        this.servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpRequest.setCharacterEncoding(encoding);
            httpResponse.setCharacterEncoding(encoding);
            //servletContext.log("当前编码已设置为：" + encoding);
            // CharsetEncodingFilter -> FrontControllerServlet -> forward -> index.jsp
        }

        //通过 ThreadLocal 传递 Config 数据到JSP //lzm add 2021-03-25 03:36:44
        //ClassLoader classLoader = request.getServletContext().getClassLoader();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        Config config = configProviderResolver.getConfig(classLoader);
        ThreadLocal<Config> configThreadLocal = new ThreadLocal<>();
        configThreadLocal.set(config);
        request.setAttribute("CONFIG", configThreadLocal);

        // 执行过滤链
        chain.doFilter(request,response);

        //
        configThreadLocal.remove();
    }

    @Override
    public void destroy() {

    }
}
