package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.AiPromptClient;
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
import com.sparta.logistics.notification.application.command.usecase.SendOrderNotificationUseCase;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.model.SlackMessageStatus;
import com.sparta.logistics.notification.domain.repository.SlackMessageCommandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class SendOrderNotificationFacadeTest {

    // ── 실제 인스턴스 ──────────────────────────────────────────
    @Autowired
    private SendOrderNotificationUseCase sendOrderNotificationUseCase;

    @Autowired
    private SlackMessageCommandRepository slackMessageCommandRepository;

    @Autowired
    private ApplicationContext applicationContext;

    // ── Mocking (외부 의존성) ──────────────────────────────────
    @MockitoBean
    private HubServiceClient hubServiceClient;

    @MockitoBean
    private OrderServiceClient orderServiceClient;

    @MockitoBean
    private ProductServiceClient productServiceClient;

    @MockitoBean
    private CompanyServiceClient companyServiceClient;

    @MockitoBean
    private DeliveryServiceClient deliveryServiceClient;

    @MockitoBean
    private UserServiceClient userServiceClient;

    @MockitoBean
    private AiPromptClient aiPromptClient;

    @MockitoBean
    private TransmitSlackMessageEventProducer transmitSlackMessageEventProducer;

    // ── 테스트 픽스처 ─────────────────────────────────────────
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
        orderId           = UUID.randomUUID();
        departureHubId    = UUID.randomUUID();
        receiverCompanyId = UUID.randomUUID();
        productId         = UUID.randomUUID();
        deliveryId        = UUID.randomUUID();
        driverUserId      = UUID.randomUUID();
        hubManagerId      = UUID.randomUUID();

        Instant dueDate = Instant.now().plus(5, ChronoUnit.DAYS);

        command = new SendOrderNotificationCommand(
                orderId,
                departureHubId,
                receiverCompanyId,
                productId,
                10,
                deliveryId,
                "PENDING",
                "최대한 빨리 보내주세요.",
                dueDate,
                null,
                null,
                Instant.now()
        );

        // HubServiceClient 단건 Mock
        HubInfo departureHub = new HubInfo(departureHubId, "경기 북부 센터", "경기도 의정부시", 37.7, 127.0, hubManagerId);
        given(hubServiceClient.getHubInfo(departureHubId)).willReturn(departureHub);

        // HubServiceClient bulk Mock (경유지 허브)
        UUID waypointHubId = UUID.randomUUID();
        HubInfo waypointHub = new HubInfo(waypointHubId, "대전광역시 센터", "대전광역시 유성구", 36.3, 127.3, UUID.randomUUID());
        given(hubServiceClient.getHubInfos(anyList())).willReturn(Map.of(waypointHubId, waypointHub));

        // OrderServiceClient Mock
        given(orderServiceClient.getOrderInfo(orderId)).willReturn(new OrderInfo(
                orderId, departureHubId, receiverCompanyId, productId,
                10, deliveryId, "PENDING", "최대한 빨리 보내주세요.",
                dueDate, null, null
        ));

        // ProductServiceClient Mock
        given(productServiceClient.getProductInfo(productId)).willReturn(
                new ProductInfo(productId, "마른 오징어", receiverCompanyId)
        );

        // CompanyServiceClient Mock
        given(companyServiceClient.getCompanyInfo(receiverCompanyId)).willReturn(
                new CompanyInfo(receiverCompanyId, "해산물월드", "RECEIVER", departureHubId, "부산시 사하구 낙동대로 1번길 1")
        );

        // DeliveryServiceClient Mock
        given(deliveryServiceClient.getDeliveryInfo(deliveryId)).willReturn(
                new DeliveryInfo(deliveryId, driverUserId, "HUB_TO_COMPANY", departureHubId, 1)
        );
        given(deliveryServiceClient.getDeliveryRoutes(deliveryId)).willReturn(List.of(
                new DeliveryRouteInfo(UUID.randomUUID(), 1, departureHubId, waypointHubId, 150.0, 120, null, null, driverUserId)
        ));

        // UserServiceClient Mock
        UserInfo driver = new UserInfo(driverUserId, "고길동", "kdk@sparta.world", "U0DRIVER01");
        UserInfo hubManager = new UserInfo(hubManagerId, "허브매니저", "manager@sparta.world", "U0MANAGER01");
        given(userServiceClient.searchUserSlackInfos(List.of(driverUserId))).willReturn(
                Map.of(driverUserId, driver)
        );
        given(userServiceClient.searchUserSlackInfos(List.of(hubManagerId))).willReturn(
                Map.of(hubManagerId, hubManager)
        );

        // AiPromptClient Mock
        given(aiPromptClient.promptOne(anyString(), anyString(), any())).willReturn(
                "[AI 생성 메시지] 주문 번호: " + orderId + " | 최종 발송 시한: " + dueDate
        );
    }

    @Test
    @DisplayName("주문 알림 발송 - SlackMessage가 PENDING 상태로 DB에 저장되고 이벤트가 발행된다")
    void send_shouldSaveSlackMessageAsPendingAndProduceEvent() {
        // when
        sendOrderNotificationUseCase.send(command);

        // then: TransmitSlackMessageEventProducer.produce()에 넘어간 ID 캡처
        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        then(transmitSlackMessageEventProducer).should(times(1)).produce(idCaptor.capture(), any());
        UUID producedId = idCaptor.getValue();

        // then: 캡처한 ID로 DB 조회하여 PENDING 상태 및 내용 검증
        SlackMessage saved = slackMessageCommandRepository.findById(producedId)
                .orElseThrow(() -> new AssertionError("저장된 SlackMessage를 찾을 수 없습니다."));

        assertThat(saved.status()).isEqualTo(SlackMessageStatus.PENDING);
        assertThat(saved.senderId()).isNull(); // 시스템 발신 = senderId null
        assertThat(saved.content()).contains("[AI 생성 메시지]");
    }

    @Test
    @DisplayName("주문 알림 발송 - AI 메시지 생성 시 주문/상품/허브 정보가 프롬프트에 사용된다")
    void send_shouldCallAiPromptWithOrderContext() {
        // when
        sendOrderNotificationUseCase.send(command);

        // then: AiPromptClient가 정확히 1회 호출됐는지 검증
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        then(aiPromptClient).should(times(1)).promptOne(promptCaptor.capture(), anyString(), any());

        String capturedPrompt = promptCaptor.getValue();
        assertThat(capturedPrompt).contains("마른 오징어");       // 상품명
        assertThat(capturedPrompt).contains("경기 북부 센터");     // 발송지
        assertThat(capturedPrompt).contains("해산물월드");          // 도착지 업체명
        assertThat(capturedPrompt).contains("고길동");             // 배송 담당자
    }

    @Test
    @Disabled("실제 OpenAI API 호출 — 수동 확인 시에만 실행 (비용 발생)")
    @DisplayName("주문 알림 발송 - 실제 OpenAI API로 AI 메시지가 생성되고 DB에 저장된다")
    void send_withRealAiPrompt_shouldGenerateMessageAndSave() {
        // given: AiPromptClient stub 없이 실제 OpenAI 호출로 위임
        given(aiPromptClient.promptOne(anyString(), anyString(), any()))
                .willAnswer(invocation -> {
                    // Spring AI ChatClient 직접 생성하여 실제 호출
                    org.springframework.ai.chat.client.ChatClient chatClient =
                            applicationContext.getBean(org.springframework.ai.chat.client.ChatClient.class);
                    String prompt = invocation.getArgument(0);
                    String systemInstruction = invocation.getArgument(1);
                    return chatClient.prompt()
                            .user(prompt)
                            .system(systemInstruction)
                            .call()
                            .content();
                });

        // when
        sendOrderNotificationUseCase.send(command);

        // then: produce() 호출 여부로 ID 캡처
        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        then(transmitSlackMessageEventProducer).should(times(1)).produce(idCaptor.capture(), any());

        // then: DB에서 실제 AI 생성 메시지 내용 확인
        SlackMessage saved = slackMessageCommandRepository.findById(idCaptor.getValue())
                .orElseThrow(() -> new AssertionError("저장된 SlackMessage를 찾을 수 없습니다."));

        assertThat(saved.status()).isEqualTo(SlackMessageStatus.PENDING);
        assertThat(saved.content()).isNotBlank();
        System.out.println("▶ AI 생성 메시지:\n" + saved.content());
    }
}
