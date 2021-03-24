package org.geektimes.web.mvc;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.apache.commons.lang.StringUtils;
import org.geektimes.context.ClassicComponentContext;
import org.geektimes.web.mvc.annotation.ValidatorProcessor;
//import org.geektimes.web.mvc.context.ComponentContext;
import org.geektimes.web.mvc.controller.Controller;
import org.geektimes.web.mvc.controller.PageController;
import org.geektimes.web.mvc.controller.RestController;
import org.geektimes.web.mvc.header.CacheControlHeaderWriter;
import org.geektimes.web.mvc.header.annotation.CacheControl;
import org.geektimes.web.mvc.util.Utils;
import org.geektimes.web.mvc.validator.ValidatorFactoryBean;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.substringAfter;

public class FrontControllerServlet extends HttpServlet {
    //不能通过注入的方式，因为新的请求会产生新的HttpServlet
    //@Resource(name="bean/ValidatorFactoryBean")
    //private ValidatorFactoryBean validatorFactoryBean;

    /**
     * 请求路径和 Controller 的映射关系缓存
     */
    private Map<String, Controller> controllersMapping = new HashMap<>();

    /**
     * 请求路径和 {@link HandlerMethodInfo} 映射关系缓存
     */
    private Map<String, HandlerMethodInfo> handleMethodInfoMapping = new HashMap<>();

    /**
     * 初始化 Servlet
     *
     * @param servletConfig
     */
    public void init(ServletConfig servletConfig) {
        initHandleMethods();
    }

    /**
     * 读取所有的 RestController 的注解元信息 @Path
     * 利用 ServiceLoader 技术（Java SPI）
     */
    private void initHandleMethods() {
        //lzm modify 2021-03-11 05:39:29
        //for (Controller controller : ClassicComponentContext.getInstance().getControllers()) {  //从context.xml获取Controller
        for (Controller controller : ServiceLoader.load(Controller.class)) {  //从import的类获取Controller
            Class<?> controllerClass = controller.getClass();
            Path pathFromClass = controllerClass.getAnnotation(Path.class);
            if (pathFromClass != null) {
                String requestPath = pathFromClass.value();
                Method[] publicMethods = controllerClass.getMethods();
                // 处理方法支持的 HTTP 方法集合
                for (Method method : publicMethods) {
                    Set<String> supportedHttpMethods = findSupportedHttpMethods(method);
                    Path pathFromMethod = method.getAnnotation(Path.class);
                    //if (pathFromMethod != null) {
                    //    requestPath += pathFromMethod.value();
                    //}
                    if (!method.isAnnotationPresent(Path.class)) {
                        continue;
                    }
                    requestPath+=method.getAnnotation(Path.class).value();

                    //lzm modify 2021-03-16 17:25:20
                    //handleMethodInfoMapping.put(requestPath, new HandlerMethodInfo(requestPath, method, supportedHttpMethods));
                    HandlerMethodInfo handlerMethodInfo = findValidatorSupport(requestPath, method, supportedHttpMethods);
                    handleMethodInfoMapping.put(requestPath, handlerMethodInfo);
                }
                controllersMapping.put(requestPath, controller);
            }
        }
    }

    /**
     * 获取处理方法中标注的 HTTP方法集合
     *
     * @param method 处理方法
     * @return
     */
    private Set<String> findSupportedHttpMethods(Method method) {
        Set<String> supportedHttpMethods = new LinkedHashSet<>();
        for (Annotation annotationFromMethod : method.getAnnotations()) {
            HttpMethod httpMethod = annotationFromMethod.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethod != null) {
                supportedHttpMethods.add(httpMethod.value());
            }
        }

        if (supportedHttpMethods.isEmpty()) {
            supportedHttpMethods.addAll(asList(HttpMethod.GET, HttpMethod.POST,
                    HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS));
        }

        return supportedHttpMethods;
    }

    private HandlerMethodInfo findValidatorSupport(String requestPath,Method method,Set<String> methodSupport) {
        if (method.isAnnotationPresent(ValidatorProcessor.class)) {
            ValidatorProcessor annotation = method.getAnnotation(ValidatorProcessor.class);
            Class<?> clazz = annotation.clazz();
            String validateMethod = annotation.validateMethod();
            return new HandlerMethodInfo(requestPath, method, methodSupport, clazz, validateMethod);
        }else {
            return new HandlerMethodInfo(requestPath, method, methodSupport);
        }
    }

    /**
     * SCWCD
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //通过 ServletContext 获取 Config 数据 //lzm add 2021-03-25 03:36:44
        ClassLoader classLoader = request.getServletContext().getClassLoader();
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        Config config = configProviderResolver.getConfig(classLoader);
        String application_name = config.getConfigValue("application.name").getValue();
        System.out.println("---------ServletContext get config-----------");
        System.out.println("application.name=" + application_name);
        System.out.println("=============================================");

        // 建立映射关系
        // requestURI = /a/hello/world
        String requestURI = request.getRequestURI();
        // contextPath  = /a or "/" or ""
        String servletContextPath = request.getContextPath();
        String prefixPath = servletContextPath;
        // 映射路径（子路径）
        String requestMappingPath = substringAfter(requestURI,
                StringUtils.replace(prefixPath, "//", "/"));
        // 映射到 Controller
        Controller controller = controllersMapping.get(requestMappingPath);

        if (controller != null) {

            HandlerMethodInfo handlerMethodInfo = handleMethodInfoMapping.get(requestMappingPath);
            try {
                if (handlerMethodInfo != null) {

                    String httpMethod = request.getMethod();

                    if (!handlerMethodInfo.getSupportedHttpMethods().contains(httpMethod)) {
                        // HTTP 方法不支持
                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        return;
                    }

                    //lzm add 2021-03-16 17:44:34
                    if (handlerMethodInfo.getValidateMethod() != null && handlerMethodInfo.getValidateMethod().equals(httpMethod)) {
                        Set<? extends ConstraintViolation<?>> constraintViolations = validatorProcessor(request, handlerMethodInfo);
                        //判断是否验证出错
                        if (constraintViolations.size() != 0) {
                            //writeValidResult(response, constraintViolations);
                            //return;
                            //throw new ConstraintViolationException(constraintViolations);
                            request.setAttribute("REGISTER_FAIL_REASON", constraintViolations);
                            request.setAttribute("REGISTER_FAIL_MESSAGE", "校验失败");
                        }
                    }

                    if (controller instanceof PageController) {
                        PageController pageController = PageController.class.cast(controller);
                        String viewPath = pageController.execute(request, response);

                        // 页面请求 forward
                        // request -> RequestDispatcher forward
                        // RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewPath);
                        // ServletContext -> RequestDispatcher forward
                        // ServletContext -> RequestDispatcher 必须以 "/" 开头
                        ServletContext servletContext = request.getServletContext();
                        if (!viewPath.startsWith("/")) {
                            viewPath = "/" + viewPath;
                        }
                        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(viewPath);
                        requestDispatcher.forward(request, response);
                    } else if (controller instanceof RestController) {
                        // TODO
                    }

                }
            } catch (Throwable throwable) {
                if (throwable.getCause() instanceof IOException) {
                    throw (IOException) throwable.getCause();
                } else {
                    throw new ServletException(throwable.getCause());
                }
            }
        }

    }

    private void writeValidResult(HttpServletResponse response, Set<? extends ConstraintViolation<?>> constraintViolations) throws IOException {
        PrintWriter writer = response.getWriter();
        constraintViolations.forEach(error->{
            writer.println(error.getMessageTemplate());
        });
        writer.flush();
        writer.close();
    }

    private Set<? extends ConstraintViolation<?>>  validatorProcessor(HttpServletRequest request, HandlerMethodInfo handlerMethodInfo) throws Exception {
        if (handlerMethodInfo.isValidator()){
            ValidatorFactoryBean validatorFactoryBean = ClassicComponentContext.getInstance().getComponent("bean/ValidatorFactoryBean");

            //try {
                return validatorFactoryBean.validate(Utils.resolveParameter(request, handlerMethodInfo.getType()));
            //} catch (Exception e) {
                //e.printStackTrace();
                //throw new Exception(e);
            //}
        }
        return Collections.emptySet();
    }

//    private void beforeInvoke(Method handleMethod, HttpServletRequest request, HttpServletResponse response) {
//
//        CacheControl cacheControl = handleMethod.getAnnotation(CacheControl.class);
//
//        Map<String, List<String>> headers = new LinkedHashMap<>();
//
//        if (cacheControl != null) {
//            CacheControlHeaderWriter writer = new CacheControlHeaderWriter();
//            writer.write(headers, cacheControl.value());
//        }
//    }
}
