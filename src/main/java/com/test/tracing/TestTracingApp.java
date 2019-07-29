package com.test.tracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TestTracingApp {
    public static void main(final String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(TestTracingApp.class, args);
        MyEventGateway gateway = context.getBean(MyEventGateway.class);
        gateway.sendEvent(new TestModel("Alex"),"create", "123");
    }
}
