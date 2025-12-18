package org.example;

import jakarta.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

@Slf4j
public class WebApp {

    public static void main(String[] args) {
        log.info("Starting Gym Management REST API with embedded Tomcat...");

        try {
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8080);
            tomcat.getConnector();

            AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
            context.scan("org.example");

            DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

            Context ctx = tomcat.addContext("", null);

            Tomcat.addServlet(ctx, "dispatcher", dispatcherServlet);
            ctx.addServletMappingDecoded("/", "dispatcher");

            log.info("Registering Spring Security filter chain...");

            DelegatingFilterProxy springSecurityFilterChain = new DelegatingFilterProxy("springSecurityFilterChain");
            springSecurityFilterChain.setContextAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher");

            org.apache.tomcat.util.descriptor.web.FilterDef filterDef = new org.apache.tomcat.util.descriptor.web.FilterDef();
            filterDef.setFilterName("springSecurityFilterChain");
            filterDef.setFilter(springSecurityFilterChain);
            ctx.addFilterDef(filterDef);

            org.apache.tomcat.util.descriptor.web.FilterMap filterMap = new org.apache.tomcat.util.descriptor.web.FilterMap();
            filterMap.setFilterName("springSecurityFilterChain");
            filterMap.addURLPattern("/*");
            filterMap.setDispatcher(DispatcherType.REQUEST.name());
            filterMap.setDispatcher(DispatcherType.ERROR.name());
            filterMap.setDispatcher(DispatcherType.ASYNC.name());
            filterMap.setDispatcher(DispatcherType.FORWARD.name());
            ctx.addFilterMap(filterMap);

            log.info("✅ Spring Security filter chain registered successfully");

            tomcat.start();
            log.info("✓ Tomcat server started successfully on http://localhost:8080");
            log.info("✓ API documentation available at: http://localhost:8080/swagger-ui.html");
            log.info("✓ Spring Security JWT authentication is ACTIVE");
            log.info("✓ Press CTRL+C to stop the server");

            tomcat.getServer().await();

        } catch (LifecycleException e) {
            log.error("Failed to start Tomcat server", e);
            System.exit(1);
        }
    }
}