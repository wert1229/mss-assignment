## README
### 실행
```shell
git clone https://github.com/wert1229/mss-assignment.git

cd mss-assignment

./gradlew build

java -Dfile.encoding=UTF-8 -jar ./build/libs/assignment-0.0.1-SNAPSHOT.jar
```
### 사용 기술
* Java17, Spring Boot 3.3.3, H2 DB, JdbcTemplate
### 코드 아키텍처
* 레이어드 아키텍처에 DDD로 구성했는데 DDD 적용시 JPA가 필수는 아니라 판단해 JdbcTemplate을 사용했습니다.
* 패키지 구조는 아래와 같습니다.
```bash
└── src
    ├── common (공통)
    ├── config
    └── product
        ├── application (어플리케이션 로직 레이어)
        │   ├── contract (인프라와 통신하는 인터페이스)
        │   ├── dto
        │   ├── exception
        │   └── listener (이벤트 리스너)
        ├── domain (도메인 엔티티 및 도메인 서비스)
        ├── infra (contract에서 정의한 인프라의 구현부)
        └── presentation (요청 응답 등 웹 계층)
``` 
### 기본 흐름
* **조회 되는 통계성 데이터가 있고 상품 추가,변경이 일어나면 통계성 데이터에 반영이 되어야한다** 로 요구사항 정리
* **스케쥴러나 배치를 통해 주기적으로 데이터를 계산해 cache, nosql 등에 적재하는 방식**을 생각했으나 실시간성이 떨어질 것으로 판단 
* **상품 및 브랜드 CUD 로직 -> 이벤트 발행 -> 캐시 반영 로직 수행** 으로 구성
### 기타
* 과제 설명의 **브랜드의 카테고리에는 1개의 상품은 존재** 부분을 유지하기 위해 브랜드는 모든 카테고리 상품과 함께 등록해야 함
* 프론트 페이지는 없지만 **resources/http/cud.http** 와 **resources/http/r.http** 로 간단하게 API 테스트 가능합니다.
### 아쉬운 점 
* 이벤트 리스너 부분 로직을 공통화시켜 간소하게 하려다보니 쿼리가 느리고 복잡함
* 캐시 역시 구조화하지 않은 단순한 형태로 유지하려다보니 조회하는 부분에서 로직이 많음
* 시간 관계상 해피패스로 통과하는 통합 테스트 정도 밖에 작성하지 못함