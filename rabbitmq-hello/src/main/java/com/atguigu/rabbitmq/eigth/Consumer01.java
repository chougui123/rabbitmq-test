package com.atguigu.rabbitmq.eigth;

import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

public class Consumer01 {
    public static final String NORMAL_EXCHANGE="normal_exchange";
    public static final String DEAD_EXCHANGE="dead_exchange";
    public static final String NORMAL_QUEUE="normal_queue";
    public static final String DEAD_QUEUE="dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        map.put("x-dead-letter-routing-key","lisi");
//        map.put("x-max-length",6);
        channel.queueDeclare(NORMAL_QUEUE,false,false,false,map);
        channel.queueDeclare(DEAD_QUEUE,false,false,false,null);

        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"zhangsan");
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");
        System.out.println("Consumer01等待消费消息");

        DeliverCallback deliverCallback=(consumerTag, delivery) ->{
            String msg = new String(delivery.getBody(), "UTF-8");
            if("info5".equals(msg)){
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(),false);
                System.out.println("Consumer01拒绝消费消息:"+msg);
            }else {
                System.out.println("Consumer01消费消息:"+msg);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            }

        };
        channel.basicConsume(NORMAL_QUEUE,false,deliverCallback,consumerTag ->{});
    }
}
