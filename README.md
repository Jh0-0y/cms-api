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

| 역할 | 이메일 | 비밀번호 | 이름 | 닉네임  |
|------|--------|----------|------|------|
| ADMIN | admin@malgn.com | admin123 | 김관리 | 관리자  |
| USER | user@malgn.com | user123 | 김맑은 | 맑은사원 |
| USER | user2@malgn.com | user456 | 이하늘 | 하늘사원 |

### 초기 콘텐츠

| ID | 제목 | 내용 | 작성자 | 비고 |
|----|------|------|--------|------|
| 1 | 공지사항 | Spring Boot 기반 CMS API 서버가 정상적으로 실행되었습니다. | 관리자 | |
| 2 | 맑은 사원의 자기소개 | 안녕하세요 저는 맑은사원입니다. | 맑은사원 | |
| 3 | 사용자 작성 게시글 | 일반 사용자가 작성한 게시글입니다. 본인만 수정·삭제할 수 있습니다. | 맑은사원 | |
| 4 | 삭제된 게시글 (복구 테스트용) | 삭제된 게시글입니다. PATCH /api/contents/{id}/restore 로 복구할 수 있습니다. | 맑은사원 | 소프트 삭제 상태 |
| 5 | 하늘사원의 첫 콘텐츠 | 두 번째 유저인 저 하늘사원입니다. 다른 유저 계정으로 수정·삭제를 시도해보세요. | 하늘사원 | |

---

## API 설계 문서 및  테스트 시나리오

편한 방법을 선택해 아래 시나리오를 순서대로 따라하며 기능을 검증할 수 있습니다.

| 방법 | 설명                                                                        |
|------|---------------------------------------------------------------------------|
| **Swagger UI** | 브라우저에서 바로 사용 가능. 서버 실행 후 `http://localhost:8080/swagger-ui/index.html` 접속 |
| **Postman** | docs/의 `cms-api.postman.json`을 임포트. 시나리오 폴더가 미리 구성되어 있어 순서대로 실행 가능        |


### 시나리오 1 — 권한 검증 (수정/삭제)

1. `user@malgn.com`으로 로그인
2. ID 1번 콘텐츠(관리자 작성) 수정 시도 → **403** 확인
3. ID 3번 콘텐츠(본인 작성) 수정 → **200** 확인
4. `user2@malgn.com`으로 로그인
5. ID 3번 콘텐츠(맑은사원 작성) 수정 시도 → **403** 확인
6. `admin@malgn.com`으로 로그인
7. ID 3번, 5번 콘텐츠(타인 작성) 수정 → **200** 확인

### 시나리오 2 — 소프트 삭제 & 복구

1. `user@malgn.com`으로 로그인
2. ID 4번 콘텐츠는 이미 소프트 삭제 상태 → 단건 조회 시 **404** 확인
3. `PATCH /api/contents/4/restore` 복구 요청 → **200** 확인
4. 목록 조회에서 ID 4번 콘텐츠 다시 노출 확인

### 시나리오 3 — 닉네임 변경 즉시 반영

1. `user@malgn.com`으로 로그인
2. ID 2번 콘텐츠 단건 조회 → 작성자 `맑은사원` 확인
3. `PATCH /api/members/me/nickname`으로 닉네임 변경
4. ID 2번 콘텐츠 다시 조회 → 변경된 닉네임으로 즉시 반영 확인

---

## 구현 기능

### 필수 기능
- 콘텐츠 CRUD (목록 조회 페이징 처리)
- Spring Security 기반 로그인
- ADMIN / USER 역할 분리
- 콘텐츠 작성자 본인 또는 ADMIN만 수정/삭제 가능

### 추가 기능
- 회원가입 (이메일, 닉네임, 이름, 패스워드, 패스워드 확인)
- 로그인 : JWT 인증 (Access Token 30분 / Refresh Token 7일)
  - Refresh Token은 DB에 저장하여 상태 추척
  - 두 토큰 모두 HttpOnly 쿠키를 사용(만료 시 브라우저 자동 제거)
- 로그아웃
  - Refresh Token은 DB에 저장하여 로그아웃 시 즉시 무효화
  - 서버에서 로그아웃 응답 시 두 쿠키 모두 만료 처리(Max-Age=0)
- 토큰 재발급
  - Access Token 만료 시 Refresh Token으로 재발급
- 키워드 검색 (제목 + 내용, QueryDSL)
- 소프트 삭제 (`is_deleted`)
- 삭제된 콘텐츠 복구 (별도의 삭제 목록 조회 API는 구현하지 않았으며, 초기 데이터 ID 4번이 소프트 삭제 상태로 세팅되어 있어 복구 동작을 바로 테스트할 수 있습니다.)
- 내 정보 조회
- 닉네임 변경 (중복 불가)
- 조회수 증가 (QueryDSL UPDATE, 동시성 고려)

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

> 과제 스키마에서 `created_by` / `last_modified_by`는 `varchar(50)`으로 명시되어 있으나, `bigint` FK로 변경했습니다.
> 닉네임을 문자열로 저장하면 닉네임 변경 시 해당 회원의 모든 게시글을 일괄 UPDATE해야 하지만, FK로 저장하면 조회 시 JOIN으로 항상 최신 닉네임을 가져올 수 있어 별도의 UPDATE 없이 변경사항이 즉시 반영됩니다.
> 닉네임 변경 기능은 이 설계의 동작을 검증하기 위한 목적도 겸합니다.

> `is_deleted` 컬럼을 추가해 소프트 삭제를 구현했습니다. 데이터를 물리적으로 삭제하지 않아 추후 복구가 가능하며, 조회 시 QueryDSL 조건으로 필터링합니다.


---

## 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 클래스만 실행
./gradlew test --tests "*.ContentServiceTest"
```

테스트 완료 후 아래 경로에서 HTML 리포트를 확인할 수 있습니다.

```
build/reports/tests/test/index.html
```

| 테스트 클래스 | 종류 | 주요 테스트 케이스                                                            |
|---|---|-----------------------------------------------------------------------|
| `AuthServiceTest` | 단위 테스트 (Mockito) | 회원가입 성공/이메일·닉네임 중복, 로그인 성공/이메일 없음/비밀번호 불일치, 토큰 재발급 성공/토큰 없음·불일치, 로그아웃 |
| `ContentServiceTest` | 단위 테스트 (Mockito) | 단건 조회 성공/없는 콘텐츠/삭제된 콘텐츠, 생성, 수정·삭제·복구 성공(본인/ADMIN)/실패(없음·삭제됨·권한 없음) |
| `ContentControllerTest` | 슬라이스 테스트 (@WebMvcTest) | 콘텐츠 생성·수정 요청 시 제목 공백·101자 초과 유효성 검사 |
| `MemberServiceTest` | 단위 테스트 (Mockito) | 내 정보 조회 성공, 닉네임 변경 성공/닉네임 중복 |
| `MemberControllerTest` | 슬라이스 테스트 (@WebMvcTest) | 닉네임 변경 요청 시 빈 값·최소(1자)·최대(51자 초과) 유효성 검사 |

---

## AI 활용

- 사용 도구: Claude Code
- 활용 방식: 개발 계획 수립, 아키텍처 설계 논의, 코드 리뷰
