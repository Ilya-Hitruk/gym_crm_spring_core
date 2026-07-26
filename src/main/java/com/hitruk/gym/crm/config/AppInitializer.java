package com.hitruk.gym.crm.config;

import com.hitruk.gym.crm.api.filter.TransactionIdFilter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(ApplicationConfig.class, WebConfig.class);

        ServletRegistration.Dynamic servlet =
                servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");

        FilterRegistration.Dynamic filter =
                servletContext.addFilter("transactionIdFilter", TransactionIdFilter.class);
        filter.addMappingForUrlPatterns(null, false, "/*");
    }
}
