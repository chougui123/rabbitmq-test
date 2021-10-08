package com.atguigu.rabbitmq.one;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class Productor {
    public static final String QUEUE_NAME="mirrior_hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.221.128");
        factory.setUsername("admin");
        factory.setPassword("123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-max-priority",10);
        channel.queueDeclare(QUEUE_NAME,true,false,false,map);
        String message="hello world";
        /*for(int i=1;i<11;i++){
            String message="info"+i;
            if(i==5){
                AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().priority(5).build();
                channel.basicPublish("",QUEUE_NAME,basicProperties,message.getBytes(StandardCharsets.UTF_8));
            }else {
                channel.basicPublish("",QUEUE_NAME,null,message.getBytes(StandardCharsets.UTF_8));
            }
        }*/
        channel.basicPublish("",QUEUE_NAME,null,message.getBytes(StandardCharsets.UTF_8));
        System.out.println("消息发送成功");
    }
}
