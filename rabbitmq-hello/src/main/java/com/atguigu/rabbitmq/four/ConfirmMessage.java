package com.atguigu.rabbitmq.four;

import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConfirmMessage {
    public static final int MESSAGE_COUNT=1000;
    public static void main(String[] args) throws Exception {
//        publishIndiMessage(); 500ms
//        publishBatchMessage(); 80ms
        publishAsyncMessage(); //30ms
    }
    
    public static void publishIndiMessage() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
        channel.confirmSelect();
        long begin = System.currentTimeMillis();
        for (int i=0 ;i<MESSAGE_COUNT;i++){
            String message=i+"";
            channel.basicPublish("",queueName,null,message.getBytes(StandardCharsets.UTF_8));
            boolean flag = channel.waitForConfirms();
            if(flag){
                System.out.println("消息发送成功");
            }

        }
        long end = System.currentTimeMillis();
        System.out.println("发送"+MESSAGE_COUNT+"个单独消息，耗时："+(end-begin)+"ms");

    }


    public static void publishBatchMessage() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
        channel.confirmSelect();
        long begin = System.currentTimeMillis();
        Integer batchSize=100;
        for (int i=0 ;i<MESSAGE_COUNT;i++){
            String message=i+"";
            channel.basicPublish("",queueName,null,message.getBytes(StandardCharsets.UTF_8));

            if(i%batchSize==0){
                boolean flag = channel.waitForConfirms();
                if(flag){
                    System.out.println("100条消息发送成功");
                }
            }

        }
        long end = System.currentTimeMillis();
        System.out.println("发送"+MESSAGE_COUNT+"批量消息，耗时："+(end-begin)+"ms");

    }


    public static void publishAsyncMessage() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
        channel.confirmSelect();

        ConcurrentSkipListMap<Long, String> outstandindConfirms = new ConcurrentSkipListMap<>();

        ConfirmCallback ackCallback=(deliveryTay,multiple)-> {
            System.out.println("确认的消息："+deliveryTay);
            if(multiple){
                ConcurrentNavigableMap<Long, String> map = outstandindConfirms.headMap(deliveryTay);
                map.clear();
            }else {
                outstandindConfirms.remove(deliveryTay);
            }
        };

         ConfirmCallback nackCallback=(deliveryTay,multiple)->{
             String message = outstandindConfirms.get(deliveryTay);
             System.out.println("未确认的消息："+message+"tag:"+deliveryTay);
            
        };
        channel.addConfirmListener(ackCallback,nackCallback);

        long begin = System.currentTimeMillis();

        for (int i=0;i<MESSAGE_COUNT;i++){
            String message=i+"";
            channel.basicPublish("",queueName,null,message.getBytes(StandardCharsets.UTF_8));
            outstandindConfirms.put(channel.getNextPublishSeqNo(),message);
        }

        long end = System.currentTimeMillis();
        System.out.println("发送"+MESSAGE_COUNT+"批量异步消息，耗时："+(end-begin)+"ms");

    }
}
