package com.test.tracing;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway
public interface MyEventGateway {

    @Gateway(requestChannel = MyAppChannelsUtils.sampleChannel)
    void sendEvent(TestModel eventDto,
                   @Header("Action") String action,
                   @Header("JMSXGroupID") String jmsxGroupId);

}
