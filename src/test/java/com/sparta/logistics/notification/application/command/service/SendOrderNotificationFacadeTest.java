package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.CompanyServiceClient;
import com.sparta.logistics.notification.application.command.client.DeliveryServiceClient;
import com.sparta.logistics.notification.application.command.client.HubServiceClient;
import com.sparta.logistics.notification.application.command.client.OrderServiceClient;
import com.sparta.logistics.notification.application.command.client.ProductServiceClient;
import com.sparta.logistics.notification.application.command.client.UserServiceClient;
import com.sparta.logistics.notification.application.command.dto.SendOrderNotificationCommand;
import com.sparta.logistics.notification.application.command.model.CompanyInfo;
import com.sparta.logistics.notification.application.command.model.DeliveryInfo;
import com.sparta.logistics.notification.application.command.model.DeliveryRouteInfo;
import com.sparta.logistics.notification.application.command.model.HubInfo;
import com.sparta.logistics.notification.application.command.model.OrderInfo;
import com.sparta.logistics.notification.application.command.model.ProductInfo;
import com.sparta.logistics.notification.application.command.model.UserInfo;
import com.sparta.logistics.notification.application.command.producer.TransmitSlackMessageEventProducer;
import com.sparta.logistics.notification.domain.entity.AiHistory;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SendOrderNotificationFacadeTest {

    @InjectMocks
    private SendOrderNotificationFacade sendOrderNotificationFacade;

    @Mock
    private GenerateHubDispatchMessageService generateHubDispatchMessageService;

    @Mock
    private SlackMessageCommandService commandService;

    @Mock
    private TransmitSlackMessageEventProducer transmitSlackMessageEventProducer;

    @Mock
    private HubServiceClient hubServiceClient;

    @Mock
    private OrderServiceClient orderServiceClient;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private CompanyServiceClient companyServiceClient;

    @Mock
    private DeliveryServiceClient deliveryServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private AiPromptLogCommandService aiPromptLogCommandService;

    private UUID orderId;
    private UUID departureHubId;
    private UUID receiverCompanyId;
    private UUID productId;
    private UUID deliveryId;
    private UUID driverUserId;
    private UUID hubManagerId;
    private SendOrderNotificationCommand command;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        departureHubId = UUID.randomUUID();
        receiverCompanyId = UUID.randomUUID();
        productId = UUID.randomUUID();
        deliveryId = UUID.randomUUID();
        driverUserId = UUID.randomUUID();
        hubManagerId = UUID.randomUUID();

        Instant dueDate = Instant.now().plus(5, ChronoUnit.DAYS);

        command = new SendOrderNotificationCommand(
                orderId, departureHubId, receiverCompanyId, productId, 10,
                deliveryId, "PENDING", "빠른 배송 부탁드립니다.", dueDate, null, null, Instant.now()
        );

        HubInfo departureHub = new HubInfo(departureHubId, "경기 센터", "경기도 의정부시", 37.7, 127.0, hubManagerId);
        given(hubServiceClient.getHubInfo(departureHubId)).willReturn(departureHub);

        OrderInfo orderInfo = new OrderInfo(orderId, departureHubId, receiverCompanyId, productId, 10, deliveryId, "PENDING", "빠른 배송 부탁드립니다.", dueDate, null, null);
        given(orderServiceClient.getOrderInfo(orderId)).willReturn(orderInfo);

        ProductInfo productInfo = new ProductInfo(productId, "오징어", receiverCompanyId);
        given(productServiceClient.getProductInfo(productId)).willReturn(productInfo);

        CompanyInfo companyInfo = new CompanyInfo(receiverCompanyId, "해산물월드", "RECEIVER", departureHubId, "부산시");
        given(companyServiceClient.getCompanyInfo(receiverCompanyId)).willReturn(companyInfo);

        DeliveryInfo deliveryInfo = new DeliveryInfo(deliveryId, driverUserId, "HUB_TO_COMPANY", departureHubId, 1);
        given(deliveryServiceClient.getDeliveryInfo(deliveryId)).willReturn(deliveryInfo);

        UserInfo driver = new UserInfo(driverUserId, "홍길동", "hong@test.com", "U12345");
        given(userServiceClient.searchUserSlackInfos(List.of(driverUserId))).willReturn(Map.of(driverUserId, driver));
    }

    @Test
    @DisplayName("주문 알림 발송 - AI 메시지 생성 성공 시 Slack 메시지 저장 및 이벤트 발행")
    void send_success() {
        // given
        UUID waypointHubId = UUID.randomUUID();
        given(deliveryServiceClient.getDeliveryRoutes(deliveryId)).willReturn(List.of(
                new DeliveryRouteInfo(UUID.randomUUID(), 1, departureHubId, waypointHubId, 100.0, 60, null, null, driverUserId)
        ));
        given(hubServiceClient.getHubInfos(List.of(waypointHubId))).willReturn(Map.of(waypointHubId, mock(HubInfo.class)));

        String prompt = "생성된 프롬프트";
        String generatedMessage = "AI가 생성한 메시지";
        AiHistory aiHistory = mock(AiHistory.class);
        SlackMessage slackMessage = mock(SlackMessage.class);
        UUID slackMessageId = UUID.randomUUID();

        given(generateHubDispatchMessageService.buildPrompt(any(), any(), any(), any(), any(), any(), any())).willReturn(prompt);
        given(aiPromptLogCommandService.append(prompt)).willReturn(aiHistory);
        given(generateHubDispatchMessageService.generate(prompt)).willReturn(generatedMessage);
        given(slackMessage.id()).willReturn(slackMessageId);
        given(commandService.append(eq(hubManagerId), eq(null), eq(generatedMessage))).willReturn(slackMessage);

        // when
        sendOrderNotificationFacade.send(command);

        // then
        then(aiPromptLogCommandService).should().updateStatusToSuccess(aiHistory, generatedMessage);
        then(transmitSlackMessageEventProducer).should().produce(slackMessageId, null);
    }

    @Test
    @DisplayName("주문 알림 발송 - AI 메시지 생성 1회 실패 후 2회차 재시도 시 성공 케이스")
    void send_aiRetryThenSuccess() {
        // given
        given(deliveryServiceClient.getDeliveryRoutes(deliveryId)).willReturn(List.of());

        String prompt = "생성된 프롬프트";
        String generatedMessage = "AI 재시도 생성 성공 메시지";
        AiHistory aiHistory = mock(AiHistory.class);
        SlackMessage slackMessage = mock(SlackMessage.class);
        UUID slackMessageId = UUID.randomUUID();

        given(generateHubDispatchMessageService.buildPrompt(any(), any(), any(), any(), any(), any(), any())).willReturn(prompt);
        given(aiPromptLogCommandService.append(prompt)).willReturn(aiHistory);
        given(generateHubDispatchMessageService.generate(prompt))
                .willThrow(new RuntimeException("1회차 생성 실패"))
                .willReturn(generatedMessage);

        given(slackMessage.id()).willReturn(slackMessageId);
        given(commandService.append(eq(hubManagerId), eq(null), eq(generatedMessage))).willReturn(slackMessage);

        // when
        sendOrderNotificationFacade.send(command);

        // then
        then(aiPromptLogCommandService).should(times(1)).updateStatusToRetrying(eq(aiHistory), eq(1), anyString());
        then(aiPromptLogCommandService).should().updateStatusToSuccess(aiHistory, generatedMessage);
        then(transmitSlackMessageEventProducer).should().produce(slackMessageId, null);
    }

    @Test
    @DisplayName("주문 알림 발송 - 경유지 경로 목록이 없는 경우(empty)도 정상적으로 처리")
    void send_emptyRoutes_success() {
        // given
        given(deliveryServiceClient.getDeliveryRoutes(deliveryId)).willReturn(List.of());

        String prompt = "생성된 프롬프트";
        String generatedMessage = "AI 메시지";
        AiHistory aiHistory = mock(AiHistory.class);
        SlackMessage slackMessage = mock(SlackMessage.class);
        UUID slackMessageId = UUID.randomUUID();

        given(generateHubDispatchMessageService.buildPrompt(any(), any(), any(), any(), any(), any(), any())).willReturn(prompt);
        given(aiPromptLogCommandService.append(prompt)).willReturn(aiHistory);
        given(generateHubDispatchMessageService.generate(prompt)).willReturn(generatedMessage);
        given(slackMessage.id()).willReturn(slackMessageId);
        given(commandService.append(eq(hubManagerId), eq(null), eq(generatedMessage))).willReturn(slackMessage);

        // when
        sendOrderNotificationFacade.send(command);

        // then
        then(hubServiceClient).should().getHubInfo(any());
        then(transmitSlackMessageEventProducer).should().produce(slackMessageId, null);
    }

    @Test
    @DisplayName("주문 알림 발송 - AI 메시지 생성 실패 후 재시도 실패 시 Slack 전송 이벤트 발행 안 함")
    void send_aiFailed_shouldNotProduceEvent() {
        // given
        given(deliveryServiceClient.getDeliveryRoutes(deliveryId)).willReturn(List.of());

        String prompt = "생성된 프롬프트";
        AiHistory aiHistory = mock(AiHistory.class);

        given(generateHubDispatchMessageService.buildPrompt(any(), any(), any(), any(), any(), any(), any())).willReturn(prompt);
        given(aiPromptLogCommandService.append(prompt)).willReturn(aiHistory);
        given(generateHubDispatchMessageService.generate(prompt)).willThrow(new RuntimeException("AI Error"));

        // when
        sendOrderNotificationFacade.send(command);

        // then
        then(aiPromptLogCommandService).should(times(2)).updateStatusToRetrying(eq(aiHistory), anyInt(), anyString());
        then(aiPromptLogCommandService).should().updateStatusToFailed(eq(aiHistory), anyString());
        then(commandService).should(never()).append(any(), any(), any());
        then(transmitSlackMessageEventProducer).should(never()).produce(any(), any());
    }
}
