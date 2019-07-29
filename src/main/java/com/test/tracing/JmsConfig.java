package com.test.tracing;

import io.opentracing.Tracer;
import io.opentracing.contrib.jms.spring.TracingJmsTemplate;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import java.util.UUID;

@Configuration
public class JmsConfig {

    private final SingleConnectionFactory singleConnectionFactory;

    @Bean
    public TracingJmsTemplate tracingJmsTemplate(final Tracer tracer, final ConnectionFactory connectionFactory, final MessageConverter messageConverter) {
        final TracingJmsTemplate template = new TracingJmsTemplate(connectionFactory, tracer);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter(final Jackson2ObjectMapperBuilder jacksonMapperBuilder) {
        final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_class");
        converter.setObjectMapper(jacksonMapperBuilder.build());
        return converter;
    }

    public JmsConfig(final ConnectionFactory connectionFactory) {
        singleConnectionFactory = new SingleConnectionFactory(connectionFactory);
        singleConnectionFactory.setClientId("41323534");
    }

    @Bean
    public Topic calcPremTopic() {
        return new ActiveMQTopic("my-simple.event.v1");
    }


    @Bean
    public IntegrationFlow outCalcEventFlow(final TracingJmsTemplate jmsTemplate) {
        return IntegrationFlows.from(MessageChannels.direct(MyAppChannelsUtils.sampleChannel))
                .enrichHeaders(h -> h.header("SYSXContentType", "application/json")
                                     .headerFunction("SYSXIdempotencyKey", (message) -> UUID.randomUUID().toString()))
                .handle(Jms.outboundAdapter(jmsTemplate).destination(calcPremTopic()))
                .get();
    }

}