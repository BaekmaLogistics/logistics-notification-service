package com.sparta.logistics.notification.infrastructure.external.client;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiClientImpl {
    private final ChatClient chatClient;

    public void some(){
        int asd = 0;
        chatClient.prompt()
                .user("Prompt")
                .system("<asd>")
                .templateRenderer(StTemplateRenderer.builder()
                        .startDelimiterToken('<')
                        .endDelimiterToken('>')
                        .build())
                .call()
                .entity();
    }
}
