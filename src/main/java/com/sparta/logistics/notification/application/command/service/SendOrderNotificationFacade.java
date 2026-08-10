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
import com.sparta.logistics.notification.application.command.usecase.SendOrderNotificationUseCase;
import com.sparta.logistics.notification.domain.entity.AiHistory;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
class SendOrderNotificationFacade implements SendOrderNotificationUseCase {
    private final GenerateHubDispatchMessageService generateHubDispatchMessageService;
    private final SlackMessageCommandService commandService;
    private final TransmitSlackMessageEventProducer transmitSlackMessageEventProducer;
    private final HubServiceClient hubServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final ProductServiceClient productServiceClient;
    private final CompanyServiceClient companyServiceClient;
    private final DeliveryServiceClient deliveryServiceClient;
    private final UserServiceClient userServiceClient;
    private final AiPromptLogCommandService aiPromptLogCommandService;

    private static final int MAX_RETRY_COUNT = 3;

    @Override
    public void send(SendOrderNotificationCommand command) {
        // 알림 메시지 생성에 필요한 외부 서비스 데이터 수집
        HubInfo departureHub = hubServiceClient.getHubInfo(command.departureHubId());
        OrderInfo order = orderServiceClient.getOrderInfo(command.orderId());
        ProductInfo product = productServiceClient.getProductInfo(command.productId());
        CompanyInfo receiverCompany = companyServiceClient.getCompanyInfo(command.receiverCompanyId());
        DeliveryInfo delivery = deliveryServiceClient.getDeliveryInfo(command.deliveryId());
        UserInfo companyDriver = userServiceClient.searchUserSlackInfos(List.of(delivery.userId()))
                .get(delivery.userId());

        // 배송 경유지 조회 및 경유 허브 정보 맵 구성 (bulk 조회)
        List<DeliveryRouteInfo> routes = deliveryServiceClient.getDeliveryRoutes(command.deliveryId());
        Map<UUID, HubInfo> hubInfoMap = resolveWaypointHubInfos(routes, command.departureHubId());

        // 수집한 데이터를 바탕으로 AI 프롬프트 문자열 생성
        String prompt = generateHubDispatchMessageService.buildPrompt(
                order, product, departureHub, receiverCompany, companyDriver, routes, hubInfoMap
        );

        // 호출 이력 PENDING 상태로 저장 (별도 트랜잭션 커밋 — AI 호출 실패 시에도 기록 보존)
        AiHistory aiHistory = aiPromptLogCommandService.append(prompt);

        // AI 메시지 생성 (실패 시 최대 MAX_RETRY_COUNT회 재시도)
        String generatedMessage = null;
        int retryCount = 0;

        while (true) {
            try {
                // AI 호출 — 성공 시 이력 SUCCESS 업데이트 후 루프 탈출
                generatedMessage = generateHubDispatchMessageService.generate(prompt);
                aiPromptLogCommandService.updateStatusToSuccess(aiHistory, generatedMessage);
                break;
            } catch (Exception e) {
                retryCount++;
                log.warn("AI 메시지 생성 실패 ({}회차): {}", retryCount, e.getMessage());

                if (retryCount < MAX_RETRY_COUNT) {
                    // 재시도 가능 횟수 남음 — 이력 RETRYING 업데이트 후 재시도
                    aiPromptLogCommandService.updateStatusToRetrying(aiHistory, retryCount, e.getMessage());
                } else {
                    // 최대 재시도 횟수 소진 — 이력 FAILED 업데이트 후 예외 전파
                    aiPromptLogCommandService.updateStatusToFailed(aiHistory, e.getMessage());
                    break;
                }
            }
        }

        // AI 생성 메시지로 SlackMessage 저장 (발신자: 허브 매니저, 수신자: 시스템 발신)
        SlackMessage slackMessage = commandService.append(
                departureHub.managerId(),
                null,
                generatedMessage
        );

        // Slack 전송 이벤트 발행 — 비동기 전송 처리 위임
        transmitSlackMessageEventProducer.produce(slackMessage.id(), null);
    }

    private Map<UUID, HubInfo> resolveWaypointHubInfos(List<DeliveryRouteInfo> routes, UUID departureHubId) {
        List<UUID> waypointHubIds = routes.stream()
                .flatMap(route -> Stream.of(route.fromHubId(), route.toHubId()))
                .filter(hubId -> hubId != null && !hubId.equals(departureHubId))
                .distinct()
                .toList();

        return waypointHubIds.isEmpty()
                ? Map.of()
                : hubServiceClient.getHubInfos(waypointHubIds);
    }
}
