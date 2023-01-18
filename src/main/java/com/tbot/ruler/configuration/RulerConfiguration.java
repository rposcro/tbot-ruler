package com.tbot.ruler.configuration;

import com.tbot.ruler.messages.MessageDeliveryReportBroker;
import com.tbot.ruler.messages.MessagePublishBroker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class RulerConfiguration {

	@Bean(name="clientHttpRequestFactory")
	public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		return factory;
	}

	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskExecutor rulerTaskExecutor(
			MessagePublishBroker messagePublishBroker, MessageDeliveryReportBroker deliveryReportBroker) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(5);
		executor.setDaemon(true);
		executor.setAllowCoreThreadTimeOut(false);
		executor.initialize();

		executor.execute(messagePublishBroker);
		executor.execute(deliveryReportBroker);
		return executor;
	}
}
