package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
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

            tomcat.start();
            log.info("✓ Tomcat server started successfully on http://localhost:8080");
            log.info("✓ API documentation available at: http://localhost:8080/swagger-ui.html");
            log.info("✓ Press CTRL+C to stop the server");

            tomcat.getServer().await();

        } catch (LifecycleException e) {
            log.error("Failed to start Tomcat server", e);
            System.exit(1);
        }
    }
}