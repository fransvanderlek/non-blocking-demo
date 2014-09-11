package org.iisiplusone.nonblockingdemo.bootstrap;

import org.iisiplusone.nonblockingdemo.endpoint.NonBlockingDemoBeans;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class NonBlockingDemoBootstrap implements WebApplicationInitializer {
	
    public void onStartup(ServletContext container) throws ServletException {
        final AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(NonBlockingDemoBeans.class);
        final ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(webContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
        dispatcher.setAsyncSupported(true);      
    }

}
