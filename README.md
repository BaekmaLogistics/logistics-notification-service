# 📦 Logistics Notification Service (알림 서비스)

> Baekma Logistics의 슬랙(Slack) 메시지 및 알림 처리를 담당하는 마이크로서비스입니다.

---

## 📌 담당 기능

* **슬랙(Slack) 메시지 관리 및 발송**: 단건 메시지 생성, 수정, 삭제, 상세 조회 및 조건별 페이징 검색
* **비동기 메시지 전송 및 재시도**: RabbitMQ 메시지 큐를 활용한 슬랙 전송 이벤트를 비동기로 발행/소비하며, 전송 실패 시 상태 전이 및 재시도 관리
* **AI 기반 알림 문구 생성**: Spring AI(`ChatClient`)를 활용해 상황별 알림 메시지(단건/다건)를 자동 생성하는 인터페이스 지원
* **사용자 슬랙 정보 연동**: OpenFeign Client를 통해 User Service와 연동하여 수신자/발신자 Slack ID 및 사용자 정보 매핑

---

## 🛠 Tech Stack

* **Language** : Java 17
* **Framework** : Spring Boot 3.5.14
* **Database** : PostgreSQL, Spring Data JPA, QueryDSL 5.1.0
* **Cache** : Redis
* **Messaging** : RabbitMQ (Spring AMQP)
* **Communication** : OpenFeign, Spring AI, Micrometer Tracing (Zipkin)

---

## ✨ 주요 구현 내용

### 1. 헥사고날 아키텍처 (Hexagonal Architecture) 및 CQRS 기반 설계
* **계층 간 관심사 분리**: Presentation, Application, Domain, Infrastructure 계층을 명확히 분리하여 도메인 규칙이 인프라 기술(JPA, RabbitMQ, Feign 등)에 직접 의존하지 않도록 수동 마핑 및 포트/어댑터 패턴 적용.
* **Command / Query 분리**: CUD 명령 유스케이스(`SlackMessageCommandService`, `SendSlackMessageFacade`)와 R 조회 유스케이스(`SearchSlackMessagesService`)를 분리하여 가독성과 유지보수성 향상.

### 2. 이벤트 기반 비동기 아키텍처 (Event-Driven Architecture)
* **결합도 완화 및 응답 속도 향상**: 메시지 발송 요청 시 DB 저장 후 `TransmitSlackMessageEventProducer`를 통해 RabbitMQ 이벤트를 비동기로 발행하고 즉시 응답을 반환하여 시스템 병목 방지.
* **메시지 규격 표준화 (`EventEnvelope`)**: 이벤트 헤더(UUID, Actor ID, Event Type, Timestamp, Version)와 페이로드를 포함하는 공통 래퍼 객체를 도입하여 추적성 확보.

### 3. 상태 전이 모델(State Machine) 및 멱등성 보장
* **도메인 내 상태 제어**: `SlackMessageStatus` (PENDING, PROCESSING, SUCCESS, FAILED, RETRYING) 내부에 `canTransitionTo()` 메서드를 두어 비즈니스적으로 유효하지 않은 상태 전이(예: SUCCESS 후 처리)를 원천 차단.
* **PROCESSING 락 상태 도입**: 비동기 전송 시작 시 `PROCESSING` 상태로 전환하여 동시 요청 시 중복 발송 방지 및 멱등성 보장.

### 4. AI Prompt Client 추상화
* **AI 모델 유연성 확보**: `AiPromptClient` 인터페이스를 정의하고 `AiPromptClientImpl`에서 `ChatClient`를 활용하여 단건(`promptOne`) 및 리스트(`promptList`) 형태의 AI 알림 메시지 생성을 지원. AI 연동 구현체 변경 시 도메인 로직 수정 최소화.

### 5. 분산 트레이싱 및 관측 가능성 (Observability)
* Micrometer Tracing 및 Zipkin 연동을 통해 MSA 환경에서 서비스 간 요청 흐름 및 메시지 큐 트레이싱 가능.

---

## 💡 기술적 고민 및 해결

### 1. 분산 비동기 환경에서의 메시지 중복 발송 및 동시성 충돌

**문제**
* 비동기 큐 소비(Consumer) 과정에서 동일한 메시지 전송 이벤트가 여러 번 수신되거나, 동시 요청이 들어올 경우 동일 메시지가 중복 발송되거나 DB 데이터 상태 변경 충돌 발생 위험.

**해결**
* JPA `@Version` 필드를 이용한 **낙관적 락(Optimistic Lock)**을 도입하여 동시 수정 시 데이터 충돌 제어.
* 도메인 상태 전이에 **`PROCESSING`** 상태를 도입하여 전송 처리 시작 시 선점 락 역할을 수행하도록 설계.

**결과**
* 별도의 레디스 분산 락 등 무거운 인프라 추가 없이 도메인 상태 제어와 낙관적 락만으로 중복 발송 차단 및 데이터 무결성 확보.

---

### 2. 메시지 큐 소비 시 비즈니스 예외에 의한 무한 재시도 및 병목 방지

**문제**
* RabbitMQ Consumer에서 메시지를 처리하는 중 DB 조회 실패나 잘못된 파라미터 등 회복 불가능한 비즈니스 예외(`ApiException` 등)가 발생할 때, 기본 큐 정책에 의해 메시지가 계속 재시도(Requeue)되어 큐 병목 현상이 일어나는 문제 발생.

**해결**
* `ConditionalRejectingErrorHandler.DefaultFatalExceptionStrategy`를 상속받은 **`CustomFatalExceptionStrategy`**를 구현.
* 역직렬화 실패 에러뿐만 아니라 비즈니스 예외 원인까지 검사하여 치명적인(Fatal) 오류로 판단되면 메시지를 즉시 Reject(Drop/DLQ 이동) 처리하도록 설정.

**결과**
* 불필요한 무한 재시도 루프를 방지하고 큐의 안정적인 소비 성능 유지.

---

## 🚀 실행 방법

```bash
./gradlew bootRun
```

필요한 환경 변수 및 외부 인프라 설정은 프로젝트 공통 README를 참고해주세요.

---

## 🔗 Project

전체 프로젝트의 아키텍처, ERD, 서비스 구성 및 팀원 역할은 Organization README에서 확인할 수 있습니다.
