package com.github.cjqcn.tiny.embedded.serlvet.container.core.register;

import javax.servlet.FilterConfig;
import javax.servlet.Registration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

abstract class AbstractRegistration implements Registration, Registration.Dynamic, ServletConfig, FilterConfig {

    private final String name;
    private final String className;
    private final ServletContext context;
    protected boolean asyncSupported;

    protected AbstractRegistration(String name, String className, ServletContext context) {
        this.name = name;
        this.className = className;
        this.context = context;
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        asyncSupported = isAsyncSupported;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getFilterName() {
        return name;
    }

    @Override
    public String getServletName() {
        return name;
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }


    @Override
    public boolean setInitParameter(String name, String value) {
        return false;
    }

    @Override
    public String getInitParameter(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.emptyEnumeration();
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        return Collections.emptySet();
    }

    @Override
    public Map<String, String> getInitParameters() {
        return Collections.emptyMap();
    }
}
