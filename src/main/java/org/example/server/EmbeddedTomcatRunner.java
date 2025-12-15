package org.example.server;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class EmbeddedTomcatRunner {
    public static final int PORT = 8080;
    public static final String CONTEXT_PATH = "/api/v1";

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);
        tomcat.getConnector();

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(org.example.config.WebConfig.class);

        String docBase = new File(".").getAbsolutePath();
        Context tomcatContext = tomcat.addContext(CONTEXT_PATH, docBase);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        Tomcat.addServlet(tomcatContext, "dispatcher", dispatcherServlet);
        tomcatContext.addServletMappingDecoded("/*", "dispatcher");

        tomcat.start();
        System.out.println("Server started at http://localhost:" + PORT + CONTEXT_PATH);
        tomcat.getServer().await();
    }
}