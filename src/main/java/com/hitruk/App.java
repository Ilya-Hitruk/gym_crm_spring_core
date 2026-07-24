package com.hitruk;

import com.hitruk.gym.crm.config.WebConfig;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class App {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.setBaseDir(createTempDir());

        tomcat.getConnector();

        Context context = tomcat.addContext("", new File(".").getAbsolutePath());

        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(WebConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(appContext);

        Tomcat.addServlet(context, "dispatcher", dispatcherServlet)
                .setLoadOnStartup(1);
        context.addServletMappingDecoded("/*", "dispatcher");

        tomcat.start();
        tomcat.getServer().await();
    }

    private static String createTempDir() {
        try {
            File tempDir = File.createTempFile("tomcat-embedded", "");
            tempDir.delete();
            tempDir.mkdir();
            tempDir.deleteOnExit();
            return tempDir.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
