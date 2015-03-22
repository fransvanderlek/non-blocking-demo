package org.iisiplusone.nonblockingdemo.endpoint;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.Lifecycle;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ServerStartup {
	
	
	@Autowired
	private HttpClient defaultHttpClient;


	public void start() {
		
		try {
			
			Logger.getGlobal().log( Level.INFO, "running on ip: "+InetAddress.getLocalHost());
			
			String url =  "http://127.0.0.1:"+System.getenv("PORT")+"/non-blocking-demo/mock";
			
			Logger.getGlobal().log(Level.INFO, "Calling :"+url);

			HttpResponse response = defaultHttpClient
			.execute(new HttpGet(url));
			
			System.out.println(response.getAllHeaders());
			Logger.getGlobal().log(Level.INFO, "Response : "+response.getAllHeaders());
			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



}
