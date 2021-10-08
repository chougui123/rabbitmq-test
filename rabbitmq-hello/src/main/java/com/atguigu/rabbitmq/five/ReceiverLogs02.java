package com.atguigu.rabbitmq.five;

import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ReceiverLogs02 {
    public static final String EXCHANGE_NAME="logs";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName,EXCHANGE_NAME,"");
        System.out.println("等待接收消息。。。。");
        DeliverCallback deliverCallback=(consumerTag,delivery) ->{
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("ReceiverLogs02控制台打印接收到的消息："+message);
        };
        channel.basicConsume(queueName,true,deliverCallback,consumerTag ->{});
    }
}
