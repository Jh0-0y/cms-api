# Simple CMS REST API

간단한 콘텐츠 관리 시스템 REST API입니다.

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

---

## 구현 기능

### 필수 기능
- 콘텐츠 CRUD (목록 조회 페이징 처리)
- Spring Security 기반 로그인
- ADMIN / USER 역할 분리
- 콘텐츠 작성자 본인 또는 ADMIN만 수정/삭제 가능

### 추가 기능
- 회원가입
- 로그인 : JWT 인증 (Access Token 30분 / Refresh Token 7일)
  - Access Token 만료 시 Refresh Token으로 재발급
  - Refresh Token은 DB에 저장하여 로그아웃 시 즉시 무효화
- 로그아웃(Refrash 즉시 무효화)
- 토큰 재발급
- 키워드 검색 (제목 + 내용, QueryDSL)
- 소프트 삭제 (`is_deleted`)
- 식제된 콘텐츠 복구
- 조회수 증가 (QueryDSL UPDATE, 동시성 고려)
  - 조회수를 서버에서 계산 후 저장하는 방식은 동시 요청 시 Race Condition이 발생할 수 있었음,
    QueryDSL UPDATE 쿼리로 DB에 위임하여 해결

### 공통 응답 포맷

모든 API 응답은 `CustomResponse<T>` 형태로 통일되어 있습니다.

| 필드 | 타입 | 설명 |
|---|---|---|
| `success` | boolean | 요청 성공 여부 |
| `data` | T | 응답 데이터 (실패 시 `null`) |
| `message` | String | 에러 메시지 (성공 시 `null`) |

#### 성공 응답 예시

```json
HTTP 200 OK

{
  "success": true,
  "data": {
    "id": 1,
    "title": "제목"
  },
  "message": null
}
```

#### 실패 응답 예시

```json
HTTP 401 Unauthorized

{
  "success": false,
  "data": null,
  "message": "인증 정보가 만료되었습니다."
}
```

---

## API 문서 (Swagger)

Swagger UI를 통해 전체 API 명세를 확인하고 직접 테스트할 수 있습니다.

```
http://localhost:8080/swagger-ui/index.html
```

---

## DB 설계

#### members

| 컬럼 | 타입 | 설명                |
|---|---|-------------------|
| id | bigint PK | 고유 아이디            |
| email | varchar(100) UNIQUE | 이메일 (로그인 ID 대체)   |
| username | varchar(50) | 이름                |
| nickname | varchar(50) UNIQUE | 닉네임 (콘텐츠 작성자 표시용) |
| password | varchar(255) | BCrypt 암호화 비밀번호   |
| role | varchar(10) | 권한 (ADMIN / USER) |
| created_date | timestamp | 가입일               |
| last_modified_date | timestamp | 마지막 수정일           |

> `email`을 로그인 ID로 사용합니다. `username`(이름)은 중복될 수 있어 콘텐츠 작성자를 명확히 식별하기 위해 `nickname`(UNIQUE) 컬럼을 별도로 두었습니다.

#### refresh_tokens

| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | bigint PK | 고유 아이디 |
| token | varchar(255) UNIQUE | Refresh Token 값 |
| member_id | bigint FK → members | 토큰 소유 회원 |

> Refresh Token을 DB에 저장하여 로그아웃 시 즉시 무효화할 수 있도록 했습니다. 재발급 요청 시 DB의 토큰과 일치 여부를 검증합니다.

#### contents

| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | bigint PK | 고유 아이디 |
| title | varchar(100) | 제목 |
| description | text | 내용 |
| view_count | bigint | 조회수 |
| is_deleted | boolean | 소프트 삭제 여부 |
| created_date | timestamp | 생성일 |
| created_by | bigint FK → members | 생성자 |
| last_modified_date | timestamp | 마지막 수정일 |
| last_modified_by | bigint FK → members | 마지막 수정자 |

> 과제 스키마에서 `created_by` / `last_modified_by`는 `varchar(50)`으로 명시되어 있으나, `bigint` FK로 변경했습니다. 닉네임 변경 기능을 추가함에 따라 문자열로 저장하면 관련 게시글을 전부 업데이트해야 하지만, FK로 저장하면 JOIN으로 항상 최신 닉네임을 조회할 수 있어 데이터 정합성이 보장됩니다.

> `is_deleted` 컬럼을 추가해 소프트 삭제를 구현했습니다. 데이터를 물리적으로 삭제하지 않아 추후 복구가 가능하며, 조회 시 QueryDSL 조건으로 필터링합니다.


---

## 테스트

```bash
./gradlew test
```

| 테스트 클래스 | 종류 | 주요 테스트 케이스                                                            |
|---|---|-----------------------------------------------------------------------|
| `AuthServiceTest` | 단위 테스트 (Mockito) | 회원가입 성공/이메일·닉네임 중복, 로그인 성공/이메일 없음/비밀번호 불일치, 토큰 재발급 성공/토큰 없음·불일치, 로그아웃 |
| `ContentServiceTest` | 단위 테스트 (Mockito) | 단건 조회 성공/없는 콘텐츠/삭제된 콘텐츠, 생성, 수정·삭제 성공(본인/ADMIN)/실패(없음·삭제됨·권한 없음)      |
| `ContentControllerTest` | 슬라이스 테스트 (@WebMvcTest) | 콘텐츠 생성·수정 요청 시 제목 공백·101자 초과 유효성 검사                                   |

---

## 트러블슈팅

### 조회수 동시성 문제


---

## AI 활용

- 사용 도구: Claude Code
- 활용 방식: 개발 계획 수립, 아키텍처 설계 논의, 코드 리뷰
