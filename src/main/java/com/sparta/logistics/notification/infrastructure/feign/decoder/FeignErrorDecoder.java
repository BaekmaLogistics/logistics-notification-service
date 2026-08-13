package com.sparta.logistics.notification.infrastructure.feign.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.logistics.notification.infrastructure.feign.dto.FeignErrorResponse;
import com.sparta.logistics.notification.infrastructure.feign.exception.FeignApiException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        String bodyString = "";
        try {
            if (response.body() != null) {
                bodyString = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
            }
            FeignErrorResponse error =
                    objectMapper.readValue(
                            bodyString,
                            FeignErrorResponse.class
                    );

            return new FeignApiException(
                    error.errorCode(),
                    error.message(),
                    response.status()
            );
        } catch (Exception e) {
            log.error("[Feign Error Parsing Failed] Status: {}, Method: {}, Body: {}",
                    response.status(), methodKey, bodyString, e);

            return new FeignApiException(
                    "COMMON_0003",
                    "Feign Client Error",
                    response.status()
            );
        }
    }
}
