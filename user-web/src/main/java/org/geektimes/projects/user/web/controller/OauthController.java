package org.geektimes.projects.user.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 输出 OauthController
 */
@Path("/oauth")
public class OauthController implements PageController {

    @GET
    @POST
    @Path("/redirect") // /oauth/redirect -> OauthController
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        // http://localhost:8080/oauth/redirect?code=e925ce5c82423a091a16&state=javademo
        System.out.println(request.getRequestURI());
        RestTemplate restTemplate = new RestTemplate();
        // MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
        MyMappingJackson2HttpMessageConverter converter = new MyMappingJackson2HttpMessageConverter();
        // converter.setObjectMapper(new ObjectMapper());
        restTemplate.getMessageConverters().add(converter);

        Map<String, String> map = new HashMap<>();
        map.put("client_id", "0e30270bcec0b4e2b6a7");
        map.put("client_secret", "b36ef4c522d2fd6677c94aed84453d0950b5aa67");
        map.put("state", request.getParameter("state"));
        map.put("code", request.getParameter("code"));
        map.put("redirect_uri", "http://localhost:8080/oauth/redirect");
        Map<String,String> resp = null;
        // Map resp = null;
        resp = restTemplate.postForObject("https://github.com/login/oauth/access_token", map, Map.class);
        HttpHeaders httpheaders = new HttpHeaders();
        httpheaders.add("Authorization", "token " + resp.get("access_token"));
        HttpEntity<?> httpEntity = new HttpEntity<>(httpheaders);
        ResponseEntity<Map> exchange = restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, httpEntity, Map.class);
        System.out.println("exchange.getBody() = " + exchange.getBody());

        return "redirect.jsp";
    }
}
