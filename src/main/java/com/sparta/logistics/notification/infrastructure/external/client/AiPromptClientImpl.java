package com.sparta.logistics.notification.infrastructure.external.client;

import com.sparta.logistics.notification.application.command.client.AiPromptClient;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiPromptClientImpl implements AiPromptClient {
    private final ChatClient chatClient;

    @Override
    public <T> T promptOne(String prompt, String systemInstruction, Class<T> clazz) {
        return chatClient.prompt()
                .user(prompt)
                .system(systemInstruction != null ? systemInstruction : "")
                .call()
                .entity(clazz);
    }

    @Override
    public <T> List<T> promptList(String prompt, String systemInstruction, Class<T> clazz) {
        BeanOutputConverter<List<T>> converter = new BeanOutputConverter<>(
                ParameterizedTypeReference.forType(
                        ResolvableType.forClassWithGenerics(List.class, clazz).getType()
                )
        );

        return chatClient.prompt()
                .user(prompt)
                .system(systemInstruction != null ? systemInstruction : "")
                .call()
                .entity(converter);
    }
}
