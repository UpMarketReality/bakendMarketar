package com.upc.webmarketar.config;

import com.upc.webmarketar.infrastructure.ControllerLoggingInterceptor;
import com.upc.webmarketar.infrastructure.MediaStorage;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final MediaStorage media;
    private final ControllerLoggingInterceptor logging;

    public WebConfig(MediaStorage m, ControllerLoggingInterceptor logging) {
        media = m;
        this.logging = logging;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logging);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry r) {
        String uri = media.root().resolve("products").toUri().toString();
        r.addResourceHandler("/media/products/**")
                .addResourceLocations(uri.endsWith("/") ? uri : uri + "/");
    }
}
