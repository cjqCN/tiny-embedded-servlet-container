package com.github.cjqcn.tiny.embedded.servlet.container.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@EnableWebMvc
@SpringBootApplication
public class TestWebApp extends WebMvcConfigurerAdapter  {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello world";
    }

    public static void main(String[] args) {
        SpringApplication.run(TestWebApp.class, args);
    }

//    @Bean
//    public ServletRegistrationBean servletRegistrationBeanServletRegistration() {
//        return new ServletRegistrationBean(new TestServlet(), "/test/*");
//    }
//
//    private class TestServlet extends HttpServlet {
//        @Override
//        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//            resp.getOutputStream().print("Null Servlet Test");
//            resp.getOutputStream().flush();
//            resp.getOutputStream().close();
//        }
//    }

}
