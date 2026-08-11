package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.AiPromptClient;
import com.sparta.logistics.notification.application.command.model.CompanyInfo;
import com.sparta.logistics.notification.application.command.model.DeliveryRouteInfo;
import com.sparta.logistics.notification.application.command.model.HubInfo;
import com.sparta.logistics.notification.application.command.model.OrderInfo;
import com.sparta.logistics.notification.application.command.model.ProductInfo;
import com.sparta.logistics.notification.application.command.model.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GenerateHubDispatchMessageServiceTest {

    @InjectMocks
    private GenerateHubDispatchMessageService generateHubDispatchMessageService;

    @Mock
    private AiPromptClient aiPromptClient;

    @Test
    @DisplayName("generate - AiPromptClient 호출 및 결과 반환")
    void generate_success() {
        // given
        String prompt = "프롬프트";
        String expectedMessage = "AI 응답 메시지";
        given(aiPromptClient.promptOne(eq(prompt), anyString(), eq(String.class))).willReturn(expectedMessage);

        // when
        String result = generateHubDispatchMessageService.generate(prompt);

        // then
        assertThat(result).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("buildPrompt - 주문/배송 정보 기반 프롬프트 텍스트 포맷팅 생성")
    void buildPrompt_success() {
        // given
        UUID orderId = UUID.randomUUID();
        OrderInfo order = new OrderInfo(orderId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, UUID.randomUUID(), "PENDING", "조심히 배송해 주세요", Instant.now(), null, null);
        ProductInfo product = new ProductInfo(UUID.randomUUID(), "사과 박스", UUID.randomUUID());
        HubInfo departureHub = new HubInfo(UUID.randomUUID(), "서울 허브", "서울시", 37.5, 127.0, UUID.randomUUID());
        CompanyInfo receiverCompany = new CompanyInfo(UUID.randomUUID(), "쿠팡", "RECEIVER", UUID.randomUUID(), "서울시 송파구");
        UserInfo companyDriver = new UserInfo(UUID.randomUUID(), "기사님", "driver@test.com", "U_DRIVER");

        UUID waypointHubId = UUID.randomUUID();
        HubInfo waypointHub = new HubInfo(waypointHubId, "대전 허브", "대전시", 36.3, 127.3, UUID.randomUUID());
        List<DeliveryRouteInfo> routes = List.of(
                new DeliveryRouteInfo(UUID.randomUUID(), 1, departureHub.id(), waypointHubId, 100.0, 60, null, null, companyDriver.id())
        );
        Map<UUID, HubInfo> hubInfoMap = Map.of(waypointHubId, waypointHub);

        // when
        String prompt = generateHubDispatchMessageService.buildPrompt(order, product, departureHub, receiverCompany, companyDriver, routes, hubInfoMap);

        // then
        assertThat(prompt).contains("사과 박스 5개");
        assertThat(prompt).contains("조심히 배송해 주세요");
        assertThat(prompt).contains("서울 허브");
        assertThat(prompt).contains("대전 허브");
        assertThat(prompt).contains("쿠팡");
        assertThat(prompt).contains("기사님 / driver@test.com");
    }

    @Test
    @DisplayName("buildPrompt - null 혹은 빈 필수값에 대한 기본 문자열 대입 검증 (requestMessage, dueDate, routes 등)")
    void buildPrompt_withNullAndEmptyValues_fallbackToDefaults() {
        // given
        UUID orderId = UUID.randomUUID();
        OrderInfo order = new OrderInfo(orderId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID(), "PENDING", null, null, null, null);
        ProductInfo product = new ProductInfo(UUID.randomUUID(), "바나나", UUID.randomUUID());
        HubInfo departureHub = new HubInfo(UUID.randomUUID(), "인천 허브", "인천시", 37.4, 126.7, UUID.randomUUID());
        CompanyInfo receiverCompany = new CompanyInfo(UUID.randomUUID(), "마트", "RECEIVER", UUID.randomUUID(), "인천시 남동구");
        UserInfo companyDriver = new UserInfo(UUID.randomUUID(), "김기사", "kim@test.com", "U_KIM");

        // when
        String prompt = generateHubDispatchMessageService.buildPrompt(order, product, departureHub, receiverCompany, companyDriver, List.of(), Map.of());

        // then
        assertThat(prompt).contains("요청 사항: 없음");
        assertThat(prompt).contains("납품 기한: 미정");
        assertThat(prompt).contains("경유지: 없음");
    }
}
