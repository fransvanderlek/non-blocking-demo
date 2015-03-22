package org.iisiplusone.nonblockingdemo.endpoint;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import scala.Tuple2;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.Promise;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.dispatch.OnComplete;

@Controller
public class DemoEndpointController {

	@Autowired
	private HttpAsyncClient httpClient;

	@Autowired
	private HttpClient defaultHttpClient;

	@RequestMapping(value = "invoke1", method = GET)
	@ResponseBody
	public DeferredResult<String> invoke1() {

		System.out.println("Started in thread: "
				+ Thread.currentThread().getName());

		final DeferredResult<String> deferredResult = new DeferredResult<String>();
		final long invocationTime = System.currentTimeMillis();

		httpClient.execute(new HttpGet("http://192.168.56.1:8082/demo/mock"),
				new FutureCallback<HttpResponse>() {

					public void failed(Exception exception) {
						deferredResult.setErrorResult(exception);
					}

					public void completed(HttpResponse response) {
						System.out.println("Resonse in: "
								+ Thread.currentThread().getName());
						deferredResult.setResult("Done in" + invocationTime
								+ ":" + response.getStatusLine().toString());
					}

					public void cancelled() {
						deferredResult.setResult("cancelled");
					}
				});

		return deferredResult;
	}

	@RequestMapping(value = "block", method = GET)
	@ResponseBody
	public String block() throws ClientProtocolException, IOException {

		return defaultHttpClient
				.execute(new HttpGet("http://192.168.56.1:8082/demo/mock"))
				.getStatusLine().toString();

	}

	@RequestMapping(value = "mock", method = GET)
	@ResponseBody
	public DeferredResult<String> mock() throws InterruptedException {

		long sleepTimeMs = 1000;

		System.out.println("Sleeping for " + sleepTimeMs + " ms.");
		Thread.sleep(sleepTimeMs);

		final DeferredResult<String> deferredResult = new DeferredResult<String>();

		deferredResult.setResult("Processing time:" + sleepTimeMs+" - "+(new Date()).getTime());

		return deferredResult;
	}

	@RequestMapping(value = "invoke2", method = GET)
	@ResponseBody
	public DeferredResult<String> invoke2() {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		ExecutionContext ec = ExecutionContexts.fromExecutorService(executor);

		final DeferredResult<String> deferredResult = new DeferredResult<String>();

		Future<HttpResponse> future1 = invokeHttpGet("http://www.scala-lang.org/");
		Future<HttpResponse> future2 = invokeHttpGet("http://camel.apache.org");
		Future<HttpResponse> future3 = invokeHttpGet("http://www.slashdot.org");

		future1.zip(future2).map(concatContentLength(), ec).zip(future3)
				.map(appendContentLength(), ec)
				.onComplete(toDeferredResult(deferredResult), ec);

		return deferredResult;
	}

	private Future<HttpResponse> invokeHttpGet(String url) {
		final Promise<HttpResponse> promise1 = Futures.promise();

		httpClient.execute(new HttpGet(url),
				new FutureCallback<HttpResponse>() {

					public void failed(Exception exception) {
						promise1.failure(exception);
					}

					public void completed(HttpResponse response) {
						promise1.success(response);
					}

					public void cancelled() {
						promise1.failure(new Exception("Cancelled"));
					}
				});

		Future<HttpResponse> future1 = promise1.future();
		return future1;
	}

	private Mapper<Tuple2<HttpResponse, HttpResponse>, String> concatContentLength() {
		return new Mapper<Tuple2<HttpResponse, HttpResponse>, String>() {

			@Override
			public String apply(scala.Tuple2<HttpResponse, HttpResponse> zipped) {
				return "(" + zipped._1().getEntity().getContentLength() + ","
						+ zipped._2().getEntity().getContentLength() + ")";
			}

		};
	}

	private Mapper<Tuple2<String, HttpResponse>, String> appendContentLength() {
		return new Mapper<Tuple2<String, HttpResponse>, String>() {

			@Override
			public String apply(scala.Tuple2<String, HttpResponse> zipped) {
				return "(" + zipped._1() + ","
						+ zipped._2().getEntity().getContentLength() + ")";
			}
		};
	}

	private OnComplete<String> toDeferredResult(
			final DeferredResult<String> deferredResult) {
		return new OnComplete<String>() {

			@Override
			public void onComplete(Throwable arg0, String arg1)
					throws Throwable {
				if (arg0 == null) {
					deferredResult.setResult(arg1);
				} else {
					deferredResult.setErrorResult(arg0);
				}
			}
		};
	}

}
