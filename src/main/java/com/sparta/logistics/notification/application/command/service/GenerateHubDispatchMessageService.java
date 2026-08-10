package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.AiPromptClient;
import com.sparta.logistics.notification.application.command.model.CompanyInfo;
import com.sparta.logistics.notification.application.command.model.DeliveryRouteInfo;
import com.sparta.logistics.notification.application.command.model.HubInfo;
import com.sparta.logistics.notification.application.command.model.OrderInfo;
import com.sparta.logistics.notification.application.command.model.ProductInfo;
import com.sparta.logistics.notification.application.command.model.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class GenerateHubDispatchMessageService {
    private final AiPromptClient aiPromptClient;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(KST);

    private static final String SYSTEM_INSTRUCTION = """
            당신은 물류 알림 메시지 작성 전문가입니다.
            주어진 주문/배송 정보를 바탕으로 허브 담당자에게 전달할 슬랙 알림 메시지를 작성하세요.
            메시지는 명확하고 간결하게 작성하며, 납품 기한을 고려한 최종 발송 시한을 반드시 포함해야 합니다.
            최종 발송 시한은 납품 기한보다 충분히 여유 있는 시각으로 산정하세요.
            답변은 메시지 본문만 출력하고, 부가 설명은 포함하지 마세요.
            """;

    public String generate(String prompt) {
        return aiPromptClient.promptOne(prompt, SYSTEM_INSTRUCTION, String.class);
    }

    public String buildPrompt(
            OrderInfo order,
            ProductInfo product,
            HubInfo departureHub,
            CompanyInfo receiverCompany,
            UserInfo companyDriver,
            List<DeliveryRouteInfo> routes,
            Map<UUID, HubInfo> hubInfoMap
    ) {
        String dueDateStr = order.dueDate() != null
                ? FORMATTER.format(order.dueDate())
                : "미정";

        String requestMessage = (order.requestMessage() != null && !order.requestMessage().isBlank())
                ? order.requestMessage()
                : "없음";

        // sequence 순서대로 정렬 후 경유 허브명 조합 (출발/도착 제외한 중간 경유지)
        String waypointsStr = routes.stream()
                .sorted((a, b) -> {
                    if (a.sequence() == null) return -1;
                    if (b.sequence() == null) return 1;
                    return Integer.compare(a.sequence(), b.sequence());
                })
                .map(route -> {
                    HubInfo hub = hubInfoMap.get(route.toHubId());
                    return hub != null ? hub.name() : route.toHubId().toString();
                })
                .collect(Collectors.joining(", "));

        if (waypointsStr.isBlank()) {
            waypointsStr = "없음";
        }

        return """
                아래 주문 및 배송 정보를 바탕으로 허브 담당자에게 전달할 슬랙 알림 메시지를 작성해주세요.
                
                주문 번호: %s
                상품 정보: %s %d개
                요청 사항: %s
                납품 기한: %s
                발송지: %s
                경유지: %s
                도착지: %s (%s)
                배송 담당자: %s / %s
                
                위 정보를 바탕으로 슬랙 알림 메시지를 작성하고, 납품 기한을 고려한 최종 발송 시한을 포함해주세요.
                """.formatted(
                order.id(),
                product.name(), order.quantity(),
                requestMessage,
                dueDateStr,
                departureHub.name(),
                waypointsStr,
                receiverCompany.name(), receiverCompany.address(),
                companyDriver.name(), companyDriver.email()
        );
    }

    /*
    > **전달 메시지 예시**

        주문 번호 : 1
        ****주문자 정보 : 김말숙 / msk@seafood.world
        주문 시간 : 2025-12-08 10:00:00
        ****상품 정보 : 마른 오징어 50박스
        요청 사항 : 12월 12일 3시까지는 보내주세요!
        발송지 : 경기 북부 센터
        경유지 : 대전광역시 센터, 부산광역시 센터
        도착지 : 부산시 사하구 낙동대로 1번길 1 해산물월드
        배송담당자 : 고길동 / kdk@sparta.world

        위 내용을 기반으로 도출된 최종 발송 시한은 12월 10일 오전 9시 입니다.
     */
}
