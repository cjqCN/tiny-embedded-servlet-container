package com.github.cjqcn.tiny.embedded.serlvet.container.core.register;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.EnumSet;

public class TinyFilterRegistration extends AbstractRegistration implements javax.servlet.FilterRegistration.Dynamic {

    private final Filter filter;

    public TinyFilterRegistration(String name, String className, ServletContext context, Filter filter) {
        super(name, className, context);
        this.filter = filter;
    }

    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {

    }

    @Override
    public Collection<String> getServletNameMappings() {
        return null;
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {

    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        return null;
    }
}
