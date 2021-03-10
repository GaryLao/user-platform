package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.service.UserServiceImpl;
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

/**
 * 输出 “注册” Controller
 */
@Path("/register")
public class RegisterController implements PageController {

    @Resource(name = "bean/UserService")
    private volatile UserService userService;

    @GET
    @POST
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        System.out.println("--"+request.getMethod()+"--");
        if (request.getMethod().equals(HttpMethod.POST)) {
            try {
                String username = request.getParameter("username");
                String userpassword = request.getParameter("userpassword");
                String email = request.getParameter("email");
                String phonenumber = request.getParameter("phonenumber");

                User user = userService.queryUserByName(username);
                if (user.getName() != null) {
                    return "register-user-exists.jsp";
                } else {
                    user.setName(username);
                    user.setPassword(userpassword);
                    user.setEmail(email);
                    user.setPhoneNumber(phonenumber);

                    if (!userService.register(user)) {
                        return "register-false.jsp";
                    } else {
                        return "register-success.jsp";
                    }
                }
            } catch (ConstraintViolationException throwable) {
                request.setAttribute("REGISTER_FAIL_REASON", throwable.getConstraintViolations());
                request.setAttribute("REGISTER_FAIL_MESSAGE", "填写格式错误");

                throwable.printStackTrace();
                return "register-false.jsp";
            } catch (Exception throwable) {
                if (throwable.getMessage() == null){
                    request.setAttribute("REGISTER_FAIL_MESSAGE", "未知原因");
                }else{
                    request.setAttribute("REGISTER_FAIL_MESSAGE", throwable.getMessage());
                }

                throwable.printStackTrace();
                return "register-false.jsp";
            }

        }else {
            return "register-form.jsp";
        }
    }
}
