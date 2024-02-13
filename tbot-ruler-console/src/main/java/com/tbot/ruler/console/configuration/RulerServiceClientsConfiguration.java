package com.tbot.ruler.console.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.console.clients.ActuatorsAdminApi;
import com.tbot.ruler.console.clients.BindingsAdminApi;
import com.tbot.ruler.console.clients.PluginsAdminApi;
import com.tbot.ruler.console.clients.StencilsAdminApi;
import com.tbot.ruler.console.clients.ThingsAdminApi;
import com.tbot.ruler.console.clients.WebhooksAdminApi;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
@Configuration
public class RulerServiceClientsConfiguration {

    @Autowired
    private RulerServiceClientsProperties clientsProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private OkHttpClient httpClient;

    @PostConstruct
    public void init() {
        log.info("Ruler clients configuration, baseurl: {}, timeout: {}, maxIdles: {}",
                clientsProperties.getBaseUrl(),
                clientsProperties.getTimeoutInMilliseconds(),
                clientsProperties.getMaxIdleConnections());
        this.httpClient = buildOkHttp3();
    }

    @Bean
    public PluginsAdminApi pluginsAdminApi() {
        return retrofit().create(PluginsAdminApi.class);
    }

    @Bean
    public ThingsAdminApi thingsAdminApi() {
        return retrofit().create(ThingsAdminApi.class);
    }

    @Bean
    public ActuatorsAdminApi actuatorsAdminApi() {
        return retrofit().create(ActuatorsAdminApi.class);
    }

    @Bean
    public WebhooksAdminApi webhooksAdminApi() {
        return retrofit().create(WebhooksAdminApi.class);
    }

    @Bean
    public BindingsAdminApi bindingsAdminApi() {
        return retrofit().create(BindingsAdminApi.class);
    }

    @Bean
    public StencilsAdminApi stencilsAdminApi() {
        return retrofit().create(StencilsAdminApi.class);
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
