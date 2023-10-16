package com.c88.member.common.config;

import com.c88.amqp.AuthToken;
import com.c88.member.service.IMemberSessionService;
import com.c88.member.service.IVipService;
import com.c88.member.service.impl.MemberServiceImpl;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;
import java.util.Map;

import static com.c88.amqp.BroadcastConfig.TOPIC_LOGIN_ERROR_QUEUE;
import static com.c88.amqp.BroadcastConfig.TOPIC_LOGIN_SUCCESS_QUEUE;

@Slf4j
@Configuration
public class QueueConfig {

    @Autowired
    MemberServiceImpl memberService;

    @Autowired
    IMemberSessionService memberSessionService;

    @Autowired
    IVipService iVipService;

    @RabbitListener(queues = {TOPIC_LOGIN_SUCCESS_QUEUE})
    public void receiveMessageFromTopicLoginSuccess(Message message, Channel channel, @Payload AuthToken authToken, @Headers Map<String, Object> headers) throws IOException {
        // public void receiveMessageFromTopicLoginSuccess(AuthToken message) {
        try {
            memberService.resetErrorPasswordCount(authToken);
            memberSessionService.saveMemberSession(authToken);
            iVipService.doVipLevelUpAction(authToken.getId());
        } catch (Exception e) {
            log.error("handle login success event error:" + e.getMessage(), e);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    @RabbitListener(queues = {TOPIC_LOGIN_ERROR_QUEUE})
    public void receiveMessageFromTopicLoginError(Message message, Channel channel, @Payload AuthToken authToken, @Headers Map<String, Object> headers) throws IOException {
        // public void receiveMessageFromTopicLoginError(AuthToken message) {
        try {
            memberService.increaseErrorPasswordCount(authToken);
        } catch (Exception e) {
            log.error("handle login error password event error:" + e.getMessage(), e);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

}
