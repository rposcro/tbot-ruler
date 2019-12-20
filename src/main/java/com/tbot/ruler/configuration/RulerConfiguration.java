package com.tbot.ruler.configuration;

import com.tbot.ruler.broker.ActuatorBroker;
import com.tbot.ruler.broker.MessageBroker;
import com.tbot.ruler.broker.SignalCollectionBroker;
import com.tbot.ruler.broker.SignalEmissionBroker;
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
			SignalEmissionBroker emissionBroker, SignalCollectionBroker collectionBroker, ActuatorBroker actuatorBroker, MessageBroker messageBroker) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(5);
		executor.setDaemon(true);
		executor.setAllowCoreThreadTimeOut(false);
		executor.initialize();

		executor.execute(emissionBroker.brokerRunnable());
		executor.execute(collectionBroker.brokerRunnable());
		executor.execute(actuatorBroker.brokerRunnable());
		executor.execute(messageBroker);
		return executor;
	}
}
