package com.tbot.ruler.console.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.console.clients.PluginsAdminApi;
import jakarta.annotation.PostConstruct;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class RulerServiceClientsConfiguration {

    @Autowired
    private RulerServiceClientsProperties clientsProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private OkHttpClient httpClient;

    @PostConstruct
    public void init() {
        this.httpClient = buildOkHttp3();
    }

    @Bean
    public PluginsAdminApi pluginsAdminApi() {
        return retrofit().create(PluginsAdminApi.class);
    }

    private Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl(clientsProperties.getBaseUrl())
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
    }

    private OkHttpClient buildOkHttp3() {
        return new okhttp3.OkHttpClient.Builder()
                .connectTimeout(clientsProperties.getTimeoutInMilliseconds(), MILLISECONDS)
                .readTimeout(clientsProperties.getTimeoutInMilliseconds(), MILLISECONDS)
                .writeTimeout(clientsProperties.getTimeoutInMilliseconds(), MILLISECONDS)
                .retryOnConnectionFailure(false)
                .connectionPool(new ConnectionPool(
                        clientsProperties.getMaxIdleConnections(),
                        clientsProperties.getTimeoutInMilliseconds(),
                        MILLISECONDS
                ))
                .cache(null)
                .build();
    }
}
