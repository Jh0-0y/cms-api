# Simple CMS REST API

간단한 콘텐츠 관리 시스템 REST API입니다.

---

## 실행 방법

```bash
./gradlew build
./gradlew bootRun
```

### 초기 계정

| 역할 | 이메일 | 비밀번호 |
|------|--------|----------|
| ADMIN | admin@malgn.com | admin123 |
| USER | user@malgn.com | user123 |

### API 문서 (Swagger)

```
http://localhost:8080/swagger-ui/index.html
```

---

## 기술 스택

- Java 25
- Spring Boot 4.0.3
- Spring Security + JWT
- Spring Data JPA + QueryDSL 7.1
- H2 Database (인메모리)
- Lombok
- springdoc-openapi (Swagger)

---

## 구현 기능

### 필수
- 콘텐츠 CRUD (목록 조회 페이징 처리)
- Spring Security 기반 로그인
- ADMIN / USER 역할 분리
- 콘텐츠 작성자 본인 또는 ADMIN만 수정/삭제 가능

### 추가
- JWT 인증 (Access Token 30분 / Refresh Token 7일)
- 회원가입 / 로그아웃 / 토큰 재발급
- 키워드 검색 (제목 + 내용, QueryDSL)
- 소프트 삭제 (`is_deleted`)
- 조회수 증가 (QueryDSL UPDATE, 동시성 고려)
- 공통 응답 포맷 (`CustomResponse`)
- Swagger 문서화

---

## 로그인 방식

JWT 방식을 선택했습니다.

- Stateless 구조로 서버 확장에 유리
- Access Token 만료 시 Refresh Token으로 재발급
- Refresh Token은 DB에 저장하여 로그아웃 시 즉시 무효화

---

## 설계 결정 및 트레이드오프

### `created_by` / `last_modified_by` 타입 변경
과제 스키마에서는 `varchar(50)`으로 명시되어 있으나, 회원 시스템을 추가 구현함에 따라 `bigint` FK로 변경했습니다.

문자열로 저장할 경우 닉네임 변경 시 모든 관련 게시글을 일괄 업데이트해야 하는 문제가 발생합니다. FK로 저장하면 JOIN을 통해 항상 최신 닉네임을 조회할 수 있어 데이터 정합성이 보장됩니다.

### 소프트 삭제
`is_deleted` 컬럼으로 소프트 삭제를 구현했습니다. 데이터를 물리적으로 삭제하지 않아 추후 복구 기능 구현이 가능하며, 조회 시 QueryDSL 조건으로 필터링합니다.

### 조회수 처리
서버에서 값을 읽어 +1 후 저장하는 방식 대신, QueryDSL UPDATE 쿼리로 DB가 직접 계산하도록 구현했습니다.
```sql
UPDATE contents SET view_count = view_count + 1 WHERE id = ?
```
동시 요청 시 Race Condition 없이 정확한 조회수 집계가 가능합니다.

### DDD 스타일 적용
비즈니스 로직을 Service가 아닌 Entity에 위치시켰습니다.
- `Content.delete()` - 삭제 불변식 검증 후 상태 변경
- `Content.validatePermission()` - 소유자/ADMIN 권한 검증
- `Content.update()` - 수정 로직

---

## 트러블슈팅

### `@EnableJpaAuditing` + `@WebMvcTest` 충돌
`@SpringBootApplication`에 `@EnableJpaAuditing`을 선언하면 `@WebMvcTest` 슬라이스 테스트 시 JPA 컨텍스트가 없어 Bean 생성 오류가 발생합니다.
`@Configuration` 클래스로 분리하여 해결했습니다.

### 조회수 동시성 문제
조회수를 서버에서 계산 후 저장하는 방식은 동시 요청 시 Race Condition이 발생할 수 있습니다.
QueryDSL UPDATE 쿼리로 DB에 위임하여 해결했습니다.

---

## AI 활용

- 사용 도구: Claude Code
- 활용 방식: 개발 계획 수립, 아키텍처 설계 논의, 코드 리뷰 및 리팩터링
