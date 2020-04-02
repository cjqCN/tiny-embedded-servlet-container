package com.github.cjqcn.tiny.embedded.serlvet.container.core.register;

import javax.servlet.*;
import java.util.Collection;
import java.util.Set;

public class TinyServletRegistration extends AbstractRegistration implements ServletRegistration.Dynamic {
    private final Servlet servlet;

    public TinyServletRegistration(String name, String className, ServletContext context, Servlet servlet) {
        super(name, className, context);
        this.servlet = servlet;
    }


    @Override
    public void setLoadOnStartup(int loadOnStartup) {

    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        return null;
    }

    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {

    }

    @Override
    public void setRunAsRole(String roleName) {

    }

    @Override
    public Set<String> addMapping(String... urlPatterns) {
        return null;
    }

    @Override
    public Collection<String> getMappings() {
        return null;
    }

    @Override
    public String getRunAsRole() {
        return null;
    }
}
