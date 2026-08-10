package com.sparta.logistics.notification.application.command.client;

import java.util.List;

public interface AiPromptClient {
    <T> T promptOne(String prompt, String systemInstruction, Class<T> clazz);

    <T> List<T> promptList(String prompt, String systemInstruction, Class<T> clazz);
}
