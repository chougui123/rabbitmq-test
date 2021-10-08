package com.atguigu.rabbitmq.six;

import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ReceiverLogs02 {
    public static final String EXCHANGE_NAME="direct_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare("disk",false,false,false,null);
        channel.queueBind("disk",EXCHANGE_NAME,"error");

        DeliverCallback deliverCallback=(consumerTag, delivery) ->{
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("ReceiverLogs02控制台打印接收到的消息："+message);
        };
        channel.basicConsume("disk",true,deliverCallback,consumerTag ->{});
    }
}
