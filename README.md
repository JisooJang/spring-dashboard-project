# spring-dashboard-project
spring-dashboard-project

Spring framework를 이용하여 앱을 통해 들어오는 육아용 3가지 디바이스(체온, 배변, 수유) 데이터를 통해 실시간으로 대시보드에 수치와 업데이트 시간을 한눈에 보여주는 서비스
대시보드에서 당일 날짜로 셋팅되어있을 경우, 실시간 데이터를 한눈에 확인
캘린더로 이전 날짜를 간편하게 이동하여 과거의 데이터 히스토리를 확인
회원 간 그룹을 형성하여 대시보드 데이터를 공유다이어리 메뉴에서 육아일기와 육아일정을 작성, 그룹간 공유 가능이 외에도 기본 로그인, 회원가입, 배송지 관리, 갤러리, 커뮤니티 기능 구현


DB : MariaDB, AWS DynamoDB(디바이스 데이터 실시간 저장 및 연산, 로딩)
AWS : EC2, ELB, ACM, S3, SES, DynamoDB 사용

#담당 기능
혼자서 모든 백엔드 개발과 AWS 클라우드 서버 구축 및 프로젝트 배포 담당
웹소켓, Oauth 로그인, 트위터와 페이스북 공유 기능 <클라이언트 + 서버 코드> 작성

#프로젝트 기간
2018.01 ~ 2019.01 (약 1년)

#Database  
MariaDB – 기본 정형화된 데이터 저장. (회원, 아이, 게시글, 배송지, 파일 정보 등)
DynamoDB(AWS) - 하드웨어로부터 초 단위 및 이벤트 단위로 데이터를 빠른 속도로 저장 및 로딩
(온도,수유, 배변 데이터)

#Websocket 
(Stomp 프로토콜 + SockJS) 

#Session-Management
spring-session-data-redis를 이용한 세션 관리

