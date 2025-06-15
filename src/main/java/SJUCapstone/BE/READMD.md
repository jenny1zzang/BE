<a href="https://e4u-dev.netlify.app/" target="_blank">
<img src="https://github.com/user-attachments/assets/6bf7a16e-cdc2-4297-9dfb-d7da8fdeee9b" alt="배너 이미지" width="100%"/>
</a>

<br/>
<br/>

# 0. 시작하기

[E4U 서비스 링크](https://e4u-dev.netlify.app/)

E4U는 PWA(Progressive Web App) 웹 애플리케이션입니다<br/>
E4U 서비스 링크를 클릭해서 직접 실행해보실 수 있습니다 <br/>
현재 모델 서버를 활용한 검진 기능은 이용이 불가할 수 있습니다


<br/>
<br/>

# 1. 프로젝트 개요

- 프로젝트 제목: E4U - AI 기반 구강 건강 점검 서비스
  <br/>
- 프로젝트 설명: AI를 활용하여 스마트폰으로 간편하게 구강 건강을 점검할 수 있는 서비스입니다.

<br/>
<br/>

# 2. 팀원 소개

| <img src="https://avatars.githubusercontent.com/u/113101019?v=4" width="100" height="100"/> | <img src="https://avatars.githubusercontent.com/u/108577676?v=4" width="100" height="100"/> | <img src="https://avatars.githubusercontent.com/u/63639963?v=4" width="100" height="100"/> | <img src="https://avatars.githubusercontent.com/u/80903685?v=4" width="100" height="100"/> |
|:-------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------:|
|                       AI<br/>이규섭 <br>[@9sub](https://github.com/9sub)                       |                  BE<br/>김해린 [@jenny1zzang](https://github.com/jenny1zzang)                  |                    BE<br/>한경준 [@hkjbrian](https://github.com/hkjbrian)                     |                      FE<br/>강은우 [@euoonw](https://github.com/euoonw)                       |




# 3. 기술 스택


본 프로젝트는 AI 기반 웹 애플리케이션으로, **프론트엔드**, **백엔드**, **AI 모델**로 구성된 아키텍처를 사용합니다.

| 분류           | 사용 기술                             |
|--------------|-----------------------------------|
| **Backend**  | Spring Boot (Java 17)             |
| **Frontend** | React                        |
| **DB**       | MySQL                             |
| **Cloud**    | AWS (EC2, S3)                     |
| **AI 모델 아키텍처**    | YOLOv5(이미지 탐지), ET5(텍스트 분석)            |
| **API 문서**   | Swagger (Springdoc OpenAPI 2.2.0) |

# 4. 주요 기능

<img src="https://github.com/user-attachments/assets/51e45fb7-4c0e-4f24-94c7-21d953b39a80" alt="주요 기능 이미지" width="100%"/>

- **회원가입**

    - 사용자는 이메일, 비밀번호, 성별, 출생연도를 입력해 새로운 계정을 생성할 수 있습니다.

- **로그인**

    - 이메일과 비밀번호를 입력하여 계정에 로그인할 수 있습니다.

- **구강 촬영**

    - 구강 사진 촬영 기능을 통해 사용자는 자신의 구강 상태를 기록할 수 있습니다.
    - 촬영 시 제공되는 가이드를 통해 정확한 구강 이미지를 촬영할 수 있습니다.

- **추가 증상 입력**

    - 사용자는 치아, 잇몸, 입술 등의 부위를 선택하고 통증 정도를 입력할 수 있습니다.
    - 증상 텍스트를 입력하여 더 구체적인 정보를 제공할 수 있습니다.

- **구강 검사 보고서 제공**

    - 분석된 구강 상태를 점수, 위험도, 질환 위치를 포함한 시각화 자료로 확인할 수 있습니다.
    - 분석 결과, 세부 분석, 치료 방법과 관리 방법이 포함된 상세 보고서를 제공합니다.

- **구강 리포트 관리**

    - 과거 구강 검사 기록을 확인할 수 있습니다.
    - 그래프와 이미지 형태로 구강 상태의 점진적 변화를 파악할 수 있습니다.
    - 치아별 증상을 확인할 수 있습니다.

- **주변 치과 정보 제공**
    - 사용자의 위치를 기반으로 근처의 치과 정보를 지도 형태로 제공합니다.

<br/>
<br/>

# 5. 시스템 구조

<img src="https://github.com/user-attachments/assets/822ed2aa-b9e9-485d-bc3d-b26cb1dd6c7c" alt="주요 기능 이미지" width="100%"/>

<br/>
<br/>
