# battlemap-be

백엔드 서버 for Battlemap Project  
(전투 맵 기반 게임 서비스의 백엔드 서버)

---

## ⚙️ Tech Stack
- **Language/Framework**: Java 21, Spring Boot 3
- **Build Tool**: Gradle (Kotlin DSL)
- **DB**: PostgreSQL 16 (Docker)
- **Cache/RT**: Redis 7 (Docker), WebSocket/SSE
- **DB Migration**: Flyway
- **Auth**: (초기 개발 단계) Spring Security 임시 오픈 → 추후 JWT/토큰 전환 예정
- **Infra(로컬)**: Docker Compose

---

## 📂 프로젝트 구조
```bash
src/main/java/org/battlemap/battlemapbe
├── security/       # Spring Security 설정
├── controller/     # API 엔드포인트
├── service/        # 비즈니스 로직
├── repository/     # DB 접근 계층
└── ...

🌱 브랜치 전략

main: 배포용 (안정화된 코드만 병합)

dev: 개발용 (feature 브랜치 병합 대상)

feature/*: 기능 단위 개발 브랜치


