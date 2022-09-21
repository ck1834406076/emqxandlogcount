package com.kfktoexcel.kfktoexcel.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.function.Supplier;

@Configuration
@EnableAsync
@Slf4j
public class ExecutorConfig {


    @Bean(name = "asyncServiceExecutor")
    public ThreadPoolTaskExecutor asyncServiceExecutor() {
        log.info("start asyncServiceExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(17);
        //配置最大线程数
        executor.setMaxPoolSize(17);
        //配置队列大小
        executor.setQueueCapacity(1024);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("emqx-perf");

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            10,
            20,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(200),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("主线程执行任务");
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> System.out.println("子线程1执行：" + Thread.currentThread().getName()), executor);

        //future.get();
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("子线程2执行：" + Thread.currentThread().getName());
            return 20;
        }, executor);
        System.out.println("主线程结束任务,结果：" + future2.get());

    }
}
