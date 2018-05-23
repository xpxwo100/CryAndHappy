package com.umfuck.demo.mq;


import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component

public class Receiver implements ChannelAwareMessageListener {
    @RabbitListener(queues = "aperfect.db.topic.test7")
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

    }
}
