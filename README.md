# fhk-security-server

FHK MSA의 중앙 로그인 서버입니다. 계정 생성, 로그인, JWT 발급, refresh token rotation, 사용자 인증 정보 조회를 담당합니다.

## Role

- 일반 계정 회원가입과 로그인
- RSA 기반 access token / refresh token 발급
- refresh token rotation 처리
- refresh token 해시 저장과 재사용 방지
- Redis 기반 token version 캐시
- Spring Security whitelist 기반 공개 API 관리

## Auth Flow

```text
Client
  -> BFF
  -> security-server /auth/login
  -> access token + refresh token
  -> BFF HttpOnly cookie 저장
```

로그인 시 계정의 `tokenVersion`을 증가시키고, 새 access token과 refresh token을 발급합니다. refresh 요청에서는 기존 refresh token의 jti, 해시, 교체 여부를 확인한 뒤 새 토큰으로 회전합니다.

## API Scope

| Area | Endpoints |
| --- | --- |
| Auth | `POST /auth/login`, `POST /auth/refresh`, `GET /auth/me`, `POST /auth/logout` |
| Account | `POST /accounts`, `GET /accounts`, `PATCH /accounts/modify`, `PATCH /accounts/withdraw` |
| Availability | `GET /accounts/availability?loginId=...`, `GET /accounts/availability?nickname=...` |

OAuth 관련 endpoint는 확장 예정 상태이며, 현재 포털에서는 일반 로그인 흐름을 중심으로 사용합니다.

## Tech Stack

- Java 17
- Spring Boot 3.5
- Spring Security
- JPA, MariaDB
- Redis
- JJWT
- Docker, k3s

## Run Locally

```bash
./gradlew bootRun
```

Windows에서는 다음 명령을 사용할 수 있습니다.

```bash
gradlew.bat bootRun
```

대표 환경 변수입니다.

```env
SERVER_PORT=9000
DB_URL=jdbc:mariadb://localhost:3306/fhk_secu_db
DB_USER=root
DB_PW=
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PW=
```
