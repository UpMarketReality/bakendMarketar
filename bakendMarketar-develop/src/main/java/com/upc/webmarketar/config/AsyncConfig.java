package com.upc.webmarketar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncConfig {
    @Bean(name = "prototypeExecutor", destroyMethod = "shutdown")
    public ExecutorService prototypeExecutor() {
        return new ThreadPoolExecutor(
                2,
                2,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(20),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
