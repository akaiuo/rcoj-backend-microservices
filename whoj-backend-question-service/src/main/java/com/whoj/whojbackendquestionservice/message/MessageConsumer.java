package com.whoj.whojbackendquestionservice.message;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.whoj.whojbackendmodel.model.message.JudgeSubmit;
import com.whoj.whojbackendquestionservice.service.QuestionService;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class MessageConsumer {

    @Resource
    private QuestionService questionService;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"judge_queue"}, ackMode = "MANUAL")
    public void receive(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            JudgeSubmit judgeSubmit = JSONUtil.toBean(message, JudgeSubmit.class);
            questionService.updateQuestionAccepted(judgeSubmit.getQuestionId(), judgeSubmit.getIsAccepted());
            channel.basicAck(deliveryTag, false);
        }catch (Exception e) {
            channel.basicNack(deliveryTag, false, true);
        }
    }
}
