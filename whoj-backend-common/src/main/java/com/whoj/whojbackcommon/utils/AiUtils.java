package com.whoj.whojbackcommon.utils;

import com.alibaba.fastjson.JSON;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AiUtils {
    private static final ClientV4 client = new ClientV4.Builder("b33bb4488f6c416594656e3252b5d834.PuiHEx4p0FIUpHe0")
            .networkConfig(20000, 20000, 20000, 20000, TimeUnit.MILLISECONDS)
            .build();

    /**
     * 异步调用
     * @param message 提问
     * @return ask-id（用于查询结果）
     */
    public static String testAsyncInvoke(String message) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        messages.add(chatMessage);
        String requestIdTemplate = "1234561";
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4Flash)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethodAsync)
                .messages(messages)
                .requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        System.out.println("model output:" + JSON.toJSONString(invokeModelApiResp));
        return invokeModelApiResp.getData().getId();
    }

    /**
     * 同步调用
     * @param message 提问
     * @return 回答
     */
    public static String testInvoke(String message) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        messages.add(chatMessage);
        String requestIdTemplate = "12345612";
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4Flash)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod) //注意这里不同
                .messages(messages)
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        System.out.println("model output:" + JSON.toJSONString(invokeModelApiResp));
        return invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
    }
}