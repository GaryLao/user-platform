package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.service.UserServiceImpl;
import org.geektimes.web.mvc.annotation.ValidatorProcessor;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.IOException;
import java.util.Objects;

import static org.geektimes.web.mvc.util.Utils.resolveParameter;

/**
 * 输出 “注册” Controller
 */
@Path("/user")
public class RegisterController implements PageController {

    @Resource(name = "bean/UserService")
    private volatile UserService userService;

    @GET
    @POST
    @ValidatorProcessor(clazz = User.class, validateMethod = "POST")
    @Path("/register")
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        System.out.println("--"+request.getMethod()+"--");
        if (request.getMethod().equals(HttpMethod.POST)) {
            ;
            if (request.getAttribute("REGISTER_FAIL_REASON") != null){
                return "register-false.jsp";
            }else {
                try {
                    User user = resolveParameter(request, User.class);
                    Objects.requireNonNull(user, "parameter resolve error");
                    if (!userService.register(user)) {
                        request.setAttribute("REGISTER_FAIL_MESSAGE", "更新数据库失败");
                        return "register-false.jsp";
                    } else {
                        return "register-success.jsp";
                    }

                    //String username = request.getParameter("name");
                    //String userpassword = request.getParameter("password");
                    //String email = request.getParameter("email");
                    //String phonenumber = request.getParameter("phonenumber");
                    //
                    //User user = userService.queryUserByName(username);
                    //if (user.getName() != null) {
                    //    return "register-user-exists.jsp";
                    //} else {
                    //    user.setName(username);
                    //    user.setPassword(userpassword);
                    //    user.setEmail(email);
                    //    user.setPhoneNumber(phonenumber);
                    //
                    //    if (!userService.register(user)) {
                    //        request.setAttribute("REGISTER_FAIL_MESSAGE", "更新数据库失败");
                    //        return "register-false.jsp";
                    //    } else {
                    //        return "register-success.jsp";
                    //    }
                    //}
                } catch (ConstraintViolationException throwable) {
                    request.setAttribute("REGISTER_FAIL_REASON", throwable.getConstraintViolations());
                    request.setAttribute("REGISTER_FAIL_MESSAGE", "校验失败");

                    //throwable.printStackTrace();
                    return "register-false.jsp";
                } catch (Exception throwable) {
                    if (throwable.getMessage() == null) {
                        request.setAttribute("REGISTER_FAIL_MESSAGE", "未知原因");
                    } else {
                        request.setAttribute("REGISTER_FAIL_MESSAGE", throwable.getMessage());
                    }

                    //throwable.printStackTrace();
                    return "register-false.jsp";
                }
            }

        }else {
            return "register-form.jsp";
        }
    }
}
