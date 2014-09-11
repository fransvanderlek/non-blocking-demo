package org.iisiplusone.nonblockingdemo.endpoint;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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
    
    @Bean( initMethod="start", destroyMethod="close" )
    public HttpAsyncClient httpClient(){
    	RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000).build();
        return HttpAsyncClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();    	
    }
  
}
