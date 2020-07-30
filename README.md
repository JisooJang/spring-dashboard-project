## spring-dashboard-project
**프로젝트 소개**
- Spring framework를 이용하여 앱을 통해 들어오는 육아용 3가지 디바이스(체온, 배변, 수유) 데이터를 통해 실시간으로 대시보드에 수치와 업데이트 시간을 한눈에 보여주는 서비스
- 대시보드에서 당일 날짜로 셋팅되어있을 경우, 앱에 연결된 블루투스 디바이스로부터 전송되는 실시간 아이의 생체 데이터를 한눈에 확인할 수 있다. 
- 캘린더로 이전 날짜를 간편하게 이동하여 과거의 데이터 히스토리를 확인할 수 있다. 
- 회원 간 그룹 기능을 제공하여 아이의 대시보드 데이터를 공유
- 대시보드에서 추가로 다이어리 기능을 제공하여 육아일기와 육아일정을 작성, 그룹간 공유 가능
- 이 외에도 기본 로그인, 회원가입, 배송지 관리, 갤러리, 커뮤니티 기능 구현

## Technology Used
java 8
Spring boot 1.5.9 (Spring framework 4.3.2)
JPA 
Stomp protocol for websocket communication
## Database  
MariaDB – 기본 정형화된 데이터 저장. (회원, 아이, 게시글, 배송지, 파일 정보 등)
DynamoDB(AWS) - 하드웨어로부터 초 단위 및 이벤트 단위로 데이터를 빠른 속도로 저장 및 로딩
(온도,수유, 배변 데이터)
#크# AWS
EC2(웹서버), ELB(Application-load-balancer), ACM(https 연동), S3(파일 업로드), SES(메일 전송 서비스), DynamoDB(대용량 디바이스 데이터) 연동

## 담당 기능
모든 백엔드 개발과 AWS 클라우드 서버 구축 및 프로젝트 배포 담당
웹소켓 통신, Oauth 가입 및 로그인, 대시보드 기능, 갤러리 및 커뮤니티 기능, 트위터와 페이스북 공유 기능 구현

## 프로젝트 기간
2018.01 ~ 2019.01 (약 1년)

## Websocket 
(Stomp 프로토콜 + SockJS)를 이용하여 웹 대시보드 페이지에서 실시간 디바이스 데이터 공유가 가능하도록 구현하였다.

## Session-Management
spring-session-data-redis를 이용한 유저 세션 관리

## 보안  
- Spring security, AWS ACM(certificate manager) SSL 연동 
- AWS ELB(Elastic Load Balancer) 포트포워딩 http -> https
- EC2 및 RDS 보안 그룹 설정 (IP 제한)
- 모든 요청(ajax 요청 포함)에 csrf 토큰 인증
- 앱에서 요청된 Websocket 연결 전, HandShakeInterceptor를 통해 헤더의 보안값을 검사 후에 연결 허용.

## API
Oauth 로그인(카카오, 네이버, 구글), 구글 Recaptcha, 카카오 알림톡 메시지 전송, AWS 연동 등...

## Front-end 
Javascript - 웹소켓 연동(SockJS),  handlebars.js(게시판 댓글, 갤러리 페이징)

## Template Engine 
thymeleaf 3.0.9 version

## 협업도구 
Git, Slack

