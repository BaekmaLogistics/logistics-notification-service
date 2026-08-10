package com.sparta.logistics.notification.infrastructure.messaging.config;

import com.sparta.logistics.notification.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {
    private final ConnectionFactory connectionFactory;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter()); // 수동 세팅
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        // 공통 예외 처리 전략 등록
        factory.setErrorHandler(new ConditionalRejectingErrorHandler(new CustomFatalExceptionStrategy()));

        return factory;
    }

    // 커스텀 Fatal 예외 전략 정의
    public static class CustomFatalExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {
        @Override
        public boolean isFatal(Throwable t) {
            // 1. JSON 역직렬화 실패, Payload 변환 실패 등 기본 Fatal 에러 체크
            if (super.isFatal(t)) {
                return true;
            }

            // 2. 비즈니스 예외 (DB 메시지 없음, 파라미터 오류 등)도 Fatal로 지정하여 재시도 없이 Reject
            Throwable cause = t.getCause();
            return cause instanceof ApiException
                    || cause instanceof IllegalArgumentException;
        }
    }
}