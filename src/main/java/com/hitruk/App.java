package com.hitruk;

import com.hitruk.gym.crm.api.filter.TransactionIdFilter;
import com.hitruk.gym.crm.config.ApplicationConfig;
import com.hitruk.gym.crm.config.WebConfig;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.nio.file.Files;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        log.info("Starting Gym CRM application");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        String docBase = Files.createTempDirectory("gym-tomcat").toFile().getAbsolutePath();
        Context ctx = tomcat.addContext("", docBase);

        AnnotationConfigWebApplicationContext springCtx = new AnnotationConfigWebApplicationContext();
        springCtx.register(ApplicationConfig.class, WebConfig.class);

        DispatcherServlet dispatcher = new DispatcherServlet(springCtx);
        var wrapper = Tomcat.addServlet(ctx, "dispatcher", dispatcher);
        wrapper.setLoadOnStartup(1);
        ctx.addServletMappingDecoded("/*", "dispatcher");

        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("transactionId");
        filterDef.setFilter(new TransactionIdFilter());
        ctx.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("transactionId");
        filterMap.addURLPattern("/*");
        ctx.addFilterMap(filterMap);

        tomcat.start();
        log.info("Gym CRM started on http://localhost:8080");
        tomcat.getServer().await();
    }
}
