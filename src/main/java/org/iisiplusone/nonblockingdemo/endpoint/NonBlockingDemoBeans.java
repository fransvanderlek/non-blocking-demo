package org.iisiplusone.nonblockingdemo.endpoint;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan(basePackages = "org.iisiplusone.nonblockingdemo")
@EnableWebMvc
public class NonBlockingDemoBeans extends WebMvcConfigurerAdapter {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false).favorParameter(true);
    }   
    
    @Bean
    public HttpClient httpDefaultClient(){
    	RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000).build();
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();    
    }
    
    @Bean( initMethod="start", destroyMethod="close" )
    public HttpAsyncClient httpClient(){
    	RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000).build();
        return HttpAsyncClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();    	
    }
    
    @Bean
    public ServerStartup serverStartup(){
    	
    	return new ServerStartup();
    	
    	
    }
    
    @Bean 
    public MethodInvokingFactoryBean methodInvoker(){
    	
    	MethodInvokingFactoryBean invokingBean = new MethodInvokingFactoryBean();
    	invokingBean.setTargetObject(serverStartup());
    	invokingBean.setTargetMethod("start");
    	
    	
    	return invokingBean;
    	
    }
  
}
