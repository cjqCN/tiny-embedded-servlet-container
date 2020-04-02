package com.github.cjqcn.tiny.embedded.serlvet.container.core.context;

import com.github.cjqcn.tiny.embedded.serlvet.container.core.register.TinyFilterRegistration;
import com.github.cjqcn.tiny.embedded.serlvet.container.core.register.TinyServletRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class TinyServletContext implements ServletContext {
    private static final Logger logger = LoggerFactory.getLogger(TinyServletContext.class.getName());
    private final String contextPath;
    private final ClassLoader classLoader;
    private volatile boolean initialized;

    private final Map<String, ServletRegistration> servlets = new HashMap<>();
    private final Map<String, FilterRegistration> filters = new HashMap<>();
    private final Map<String, String> servletMappings = new HashMap<>();
    private final Hashtable<String, Object> attributes = new Hashtable<>();

    /**
     * 默认构造方法
     *
     * @param contextPath contextPath
     * @param classLoader classLoader
     */
    public TinyServletContext(String contextPath, ClassLoader classLoader) {
        if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }
        this.contextPath = contextPath;
        this.classLoader = classLoader;
    }


    /**
     * SpringBoot只有一个Context，我觉得直接返回this就可以了
     */
    @Override
    public ServletContext getContext(String uripath) {
        return this;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public int getMajorVersion() {
        return 3;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 3;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    @Override
    public String getMimeType(String file) {
        return null;
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        Set<String> thePaths = new HashSet<>();
        if (!path.endsWith("/")) {
            path += "/";
        }
        String basePath = getRealPath(path);
        if (basePath == null) {
            return thePaths;
        }
        File theBaseDir = new File(basePath);
        if (!theBaseDir.exists() || !theBaseDir.isDirectory()) {
            return thePaths;
        }
        String theFiles[] = theBaseDir.list();
        if (theFiles == null) {
            return thePaths;
        }
        for (String filename : theFiles) {
            File testFile = new File(basePath + File.separator + filename);
            if (testFile.isFile()) {
                thePaths.add(path + filename);
            } else if (testFile.isDirectory()) {
                thePaths.add(path + filename + "/");
            }
        }
        return thePaths;
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        if (!path.startsWith("/")) {
            throw new MalformedURLException("Path '" + path + "' does not start with '/'");
        }
        URL url = new URL(getClassLoader().getResource(""), path.substring(1));
        try {
            url.openStream();
        } catch (FileNotFoundException e) {
            url = new URL(getClassLoader().getResource(""), "static/" + path.substring(1));
            try {
                url.openStream();
            } catch (IOException e1) {
                logger.error("Throwing exception when getting InputStream of " + path + " in /static", e1);
                url = null;
            }
        } catch (Throwable t) {
            logger.error("Throwing exception when getting InputStream of " + path + " in /", t);
            url = null;
        }
        return url;
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        try {
            return getResource(path).openStream();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        return null;
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        return null;
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        return Collections.emptyEnumeration();
    }

    @Override
    public Enumeration<String> getServletNames() {
        return Collections.emptyEnumeration();
    }

    @Override
    public void log(String msg) {
        logger.info(msg);
    }

    @Override
    public void log(Exception exception, String msg) {
        logger.error(msg, exception);
    }

    @Override
    public void log(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    @Override
    public String getRealPath(String path) {
        if (!path.startsWith("/")) {
            return null;
        }
        try {
            File f = new File(getResource(path).toURI());
            return f.getAbsolutePath();
        } catch (Throwable t) {
            logger.error("Throwing exception when getting real path of " + path, t);
            return null;
        }
    }

    @Override
    public String getServerInfo() {
        return "tiny@spring";
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
    public boolean setInitParameter(String name, String value) {
        return false;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return attributes.keys();
    }

    @Override
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public String getServletContextName() {
        return getContextPath().toUpperCase(Locale.ENGLISH);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return addServlet(servletName, className, null);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return addServlet(servletName, servlet.getClass().getName(), servlet);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return addServlet(servletName, servletClass.getName());
    }

    private ServletRegistration.Dynamic addServlet(String servletName, String className, Servlet servlet) {
        return new TinyServletRegistration(servletName, className, this, servlet);
    }


    @Override
    public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Throwing exception when creating instance of " + c.getName(), e);
        }
        return null;
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    @Override
    public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return addFilter(filterName, className, null);
    }

    @Override
    public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return addFilter(filterName, filter.getClass().getName(), filter);
    }

    private javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className, Filter filter) {
        logger.info("{} {} {}", filterName, className, filter);
        return new TinyFilterRegistration(filterName, className, this, filter);
    }

    @Override
    public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return addFilter(filterName, filterClass.getName());
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public javax.servlet.FilterRegistration getFilterRegistration(String filterName) {
        return filters.get(filterName);
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }


    @Override
    public void addListener(String className) {

    }

    @Override
    public <T extends EventListener> void addListener(T t) {

    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {

    }

    @Override
    public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
        return null;
    }

    @Override
    public void declareRoles(String... roleNames) {

    }

    @Override
    public String getVirtualServerName() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }
}
