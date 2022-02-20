<img src="https://capsule-render.vercel.app/api?type=waving&color=0:5b8dd2,50:81b4ef,100:81b4cf&height=300&section=header&text=Surf.&fontColor=fff&fontSize=70&fontAlignY=40&desc=my%20own%20growth%20curve%20service&descAlignY=60" width="100%"/>



# **내 인생 성장곡선 사이트 - _**Surf.**_ 🏄🏻‍♂️**

인생은 surfing 을 타는 것처럼 즐겁지만, suffering 또한 피할 수 없다.

피할 수 없다면 기록하고 공유하자! Surf 를 통해 🌊🏄‍♀️🏄🏄🏻‍♂️

**Surf의 백엔드 레포입니다 😊**

---

## 👨‍💻팀원 소개

| [최승은](https://github.com/cse0518) | [박수빈](https://github.com/suebeen) | [박정미](https://github.com/Jummi10) | [전효희](https://github.com/kwhyo) |
| :---: | :---: | :---: | :---: |
| <img src="https://avatars.githubusercontent.com/u/60170616?v=4" width="150" height="150"> | <img src="https://user-images.githubusercontent.com/56287836/146503568-f24e194a-ff54-4340-89d5-66a042cd5d02.png" width="150" height="150"> | <img src="https://user-images.githubusercontent.com/56287836/146504190-28688cf8-0435-421f-808a-ff97f12c25a8.png" width="150" height="150"> | <img src="https://user-images.githubusercontent.com/60170616/146600241-4f634fce-7845-4a54-bd2f-7f6410fecffa.png" width="150" height="150"> |
| 팀장, 개발자 | 스크럼 마스터, 개발자 | 개발자 | 개발자 |

<br>

## 📍프로젝트 목표 및 상세 설명

열심히 달려온 나 자신! 열심히는 하고 있는데 **내가 얼마나 발전했는지** 기록하는 공간은 없을까? 그냥 일기는 메모장에라도 적을 수 있고, 블로그는 이미 무수히 존재하고, **색다른 방법**으로 동기부여 받고 기록하고 공유하는
그런 공간이 필요해! 🙆‍♀️

- **성장곡선**으로 한눈에 내 인생을 돌아보기
- 남들의 성장곡선을 보며 **동기부여**도 받기
- 곡선의 특정 구간마다 기록도 남기기
- 곡선이 아닌 기록들만 모아서 보기
- 필요하다면 **포트폴리오**로도 사용 가능하기

<br>

## 🛠️개발 언어 및 활용 기술

**개발 환경**

- **Springboot** 로 웹 어플리케이션 서버를 구축했어요.
- 빌드도구는 **Gradle**을 사용했어요.
- 다양한 기능과 안정성을 위해 LTS 버전인 **Java 17** 버전을 사용했어요.
- **Spring Data JPA(Hibernate)** 로 객체 지향 데이터 로직을 작성했어요.
- **QueryDSL** 로 컴파일 시점에 SQL 오류를 감지해요. JPA 인터페이스로 해결하기 힘든 동적이고 복잡한 query를 보완하고 더 가독성 높은 코드를 작성할 수 있어요.
- 데이터베이스는 **MySQL**을 사용했어요.

**Infrastructure**

- **AWS EC2**를 사용해 서버를 구축했어요.
- **S3** 로 파일을 업로드하고 보관해요.

**협업 관리**

- **Github Issue** 으로 이슈를 관리해요.
- **Git-flow 전략**을 사용하여 브랜치를 관리해요.
- **Slack / Gather / Notion** 으로 소통해요.
- **Postman** 으로 작성한 API 문서를 통해 클라이언트와 소통해요.

**CI/CD**

- **Github Actions** 로 빌드와 테스트를 검사해요.
- **Jenkins** 로 백엔드 코드의 지속적인 배포를 진행해요.
- **Codacy** 로 지속적인 코드 퀄리티 개선을 진행해요.
- **JACOCO** 로 테스트 커버리지를 검사해요.
- **Flyway** 로 데이터베이스 버전을 관리해요.

**Security**

- **Spring Security** 를 사용했어요.
- 로그인 시에는 **JWT** 토큰을 발행하여 서버의 별도 저장소 없이 로그인을 유지할 수 있어요.
- CertBot 으로 Let’s Encrypt **SSL** 인증서를 발급받았어요.
- **Nginx** 가 프록시로 8080 포트를 바라보게 설정했어요.

<br>

## ⚙시스템 아키텍처
![최종](https://user-images.githubusercontent.com/55528172/147193318-77fd4086-33a1-4e71-aa46-2f36a474eff1.png)

<br>

## 🏗️설계
### ERD 설계
![Untitled](https://user-images.githubusercontent.com/55528172/147193431-1410ff56-67b9-4eee-ba16-1b0a3a60c447.png)


### 설계 문서
[🐄MoSCoW 구경가기](https://www.notion.so/MoSCoW-4f7d9e241bc24e84ac7c8213ef1d2c85)<br>
[🔍SURF API 설계 구경가기](https://www.notion.so/6785f7446eba4a0b82d384d025cb28a6)<br>
[📑Postman API 명세서](https://documenter.getpostman.com/view/15409285/UVRAJnUD#50ff4a3f-1d02-4f50-9870-9c0b22fa2a6f)<br>

<br>

## 🤳데모 화면
| **로그인** | **메인 화면** - Surf 첫 페이지 | **메인 화면** - 특정 category 선택 |
| :---: | :---: | :---: |
| ![로그인](https://user-images.githubusercontent.com/55528172/147193938-07d0547f-740b-428c-8ea6-25c8a6e85f3f.gif) | ![메인 페이지 - 첫 화면](https://user-images.githubusercontent.com/55528172/147193958-a062bdb3-a82a-41a2-8d2c-dd4ecd9882ba.gif) | ![메인 페이지 - 카테고리 선택](https://user-images.githubusercontent.com/55528172/147193999-6313d4d4-fe2b-4842-9b07-f3fa86835d56.gif) |

| **게시글 작성** | **무한 스크롤** | **마이 페이지** - 내 정보 수정 |
| :---: | :---: | :---: |
| ![포스트 생성](https://user-images.githubusercontent.com/55528172/147194169-b8d17790-bb44-4275-87d1-77156fa48667.gif) | ![무한 스크롤](https://user-images.githubusercontent.com/55528172/147194204-14e4475b-dc85-41b4-8995-8d91b7fe286a.gif) | ![마이 페이지 - 정보 수정](https://user-images.githubusercontent.com/55528172/147194226-f3ae8cf6-1894-4420-88a1-e340d426fd25.gif) |

| **대시보드** | **카드 페이지** | **카드 페이지** - 해당 월별 기록 리스트 |
| :---: | :---: | :---: |
| ![대시보드](https://user-images.githubusercontent.com/55528172/147194386-80912927-d4a4-4901-aea2-e241f62c775f.gif) | ![카드 페이지](https://user-images.githubusercontent.com/55528172/147194395-060842b6-9ad4-4ef5-a5ba-7d3904906833.gif) | ![카드 페이지 - 월별 리스트](https://user-images.githubusercontent.com/55528172/147194403-0f9236bb-3ce1-445d-aca1-775cb26d8737.gif) |
| 마이 페이지에서 이동 | 연도별 필터링, 해당 달의 작성 일수 확인 가능 | 카드 선택시 |

___

## 🌻프론트 깃 레포

[👨‍💻**SURF** Front Git Repository](https://github.com/prgrms-web-devcourse/Team_Ahpuh_Surf_FE)

## 🍁팀 노션

[🔍**SURF** 팀 노션 구경가기](https://www.notion.so/8-Ah-puh-Surf-ccc0a5922b8e4f638d6e897b4eb575a6)
