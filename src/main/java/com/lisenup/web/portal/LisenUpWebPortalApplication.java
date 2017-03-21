package com.lisenup.web.portal;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.lisenup.web.portal.config.EmailProperties;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties(EmailProperties.class)
public class LisenUpWebPortalApplication extends AsyncConfigurerSupport {

	public static void main(String[] args) {
		SpringApplication.run(LisenUpWebPortalApplication.class, args);
	}

	// limits the number of concurrent threads to 2
	// and limit the size of the queue to 500
	// uses SimpleAsyncTaskExecutor
	// see: http://docs.spring.io/spring-framework/docs/current/spring-framework-reference/htmlsingle/#scheduling-task-executor
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("LisenUpExecutor-");
        executor.initialize();
        return executor;
    }

}
