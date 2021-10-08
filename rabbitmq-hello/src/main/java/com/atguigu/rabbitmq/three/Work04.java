package com.atguigu.rabbitmq.three;

import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.atguigu.rabbitmq.utils.SleepUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Work04 {
    public static final String TASK_QUEUE_NAME="ack";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("C2处理消息处理时间较长");
        DeliverCallback deliverCallback=(consumerTag, delivery)->{
            SleepUtils.sleep(30);
            System.out.println("接收到的消息："+new String(delivery.getBody()));

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
        };
        Boolean autoAck=false;
        //不公平分发 int preFetchCount=1;
        //设置预期值
        int prefetchCount=5;
        channel.basicQos(prefetchCount);
        channel.basicConsume(TASK_QUEUE_NAME,autoAck,deliverCallback, consumerTag->{
            System.out.println(consumerTag+"消费者取消消费");
        });

    }
}
