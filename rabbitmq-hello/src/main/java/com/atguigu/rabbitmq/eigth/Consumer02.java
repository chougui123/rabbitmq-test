package com.atguigu.rabbitmq.eigth;

import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

public class Consumer02 {

    public static final String DEAD_QUEUE="dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        DeliverCallback deliverCallback=(consumerTag, delivery) ->{
            System.out.println(new String(delivery.getBody(), "UTF-8"));
            System.out.println("Consumer02消费消息");
        };
        channel.basicConsume(DEAD_QUEUE,true,deliverCallback,consumerTag ->{});
    }
}
