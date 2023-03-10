# WafflyTime
## 프로젝트 소개
와브리타임은 에브리타임의 클론 서비스로, 학내 구성원들의 소통 플랫폼입니다. 원하는 주제로 게시판을 생성하여 게시글과 댓글, 좋아요로 의견을 주고받을 수 있으며, 일대일 쪽지 주고받기도 가능합니다.
## 만든 사람들
| [김민중(arkrangian)](https://github.com/arkrangian) | [심우진(wjshim2003)](https://github.com/wjshim2003) | [서지희(seozzi)](https://github.com/seozzi) | [송동엽(eastshine2741)](https://github.com/eastshine2741) |
|:------------------------------------------------:|:------------------------------------------------:|:----------------------------------------:|:------------------------------------------------------:|
|                   회원가입, 소셜로그인                    |                   게시판, 게시글, 댓글                   |               메인페이지, 소셜로그인               |                       마이페이지, 채팅                        |

## UI
<img width="534" alt="제목 없음" src="https://user-images.githubusercontent.com/68140623/216775249-f1e09509-5933-4956-a1e7-267af630bbb1.png">

### 1. 로그인 페이지
  - 앱을 켜면 가장 먼저 나타나는 화면입니다. 이미 등록된 회원 정보로 로그인하거나 회원가입, 소셜로그인을 할 수 있습니다.<br/>
<img src="https://user-images.githubusercontent.com/68140623/216771867-deb6607a-9f63-457f-86cc-7e848c0d1046.jpg" height="30%" width="30%">

### 2. 회원가입 페이지
  - 아이디, 비밀번호, 닉네임을 입력하여 회원가입을 진행할 수 있습니다. 회원가입 후 바로 학교 이메일 페이지로 이동할 수 있으며, 그러지 않을 경우 후에 마이페이지에서 이메일 인증을 할 수 있습니다.<br/>
<img src="https://user-images.githubusercontent.com/68140623/216771909-9994d2aa-3812-4363-bffd-f3a01676509e.jpg" height="30%" width="30%">

### 3. 메인페이지
  - 기존 에타 메인페이지와 마찬가지로 학교 홈, 열람실 현황, 셔틀 버스, 학사 공지, 학사 일정, 도서관에 관한 정보를 바로 url로 접근할 수 있는 버튼을 만들었습니다. 학교 홈은 mysnu 메인페이지로, 열람실 현황 및 공지와 관련된 버튼들은 서울대학교 공식 페이지 내부의 관련 페이지로, 도서관 버튼은 중앙도서관 url로 연결됩니다.
  - best 게시판을 메인 홈 페이지에서 바로 볼 수 있습니다. 이 페이지는 전체 보드 게시판의 일부를 가져온 것으로, 가장 사용자가 많이 사용할 법한 게시판들로 구성해 두었습니다.<br/>
  - 와플 스튜디오의 조그마한 광고입니다.👀<br/>
<img src="https://user-images.githubusercontent.com/68140623/216771910-ca8a7089-2f88-4141-8166-bb8d9f736acd.jpg" height="30%" width="30%">

### 4. 전체 게시판 페이지
  - 게시판들의 목록을 조회할 수 있습니다. 내가 쓴 글, 댓글 단 글 등을 조회할 수 있으며, 게시판들을 검색할 수 있습니다.<br/>
<img src="https://user-images.githubusercontent.com/68140623/216771911-f88690f9-ec1d-4e76-b0c1-d41eb093b957.jpg" height="30%" width="30%">

### 5. 개별 게시판 페이지
  - 게시판을 선택했을 때 나타나는 화면입니다. 해당 게시판의 게시글들을 이미지와 함께 미리볼 수 있습니다. 게시글을 작성 페이지로 이동할 수 있습니다.<br/>
<img src="https://user-images.githubusercontent.com/68140623/216771912-32dc33be-2aaa-4fcd-aee6-39822793378b.jpg" height="30%" width="30%">
                                                                                                                                        
### 6. 게시글 페이지
  - 게시글을 선택했을 때 나타나는 화면입니다. 게시글 제목, 내용, 이미지를 조회할 수 있습니다. 댓글, 대댓글을 작성할 수 있습니다.<br/>
<img src="https://user-images.githubusercontent.com/68140623/216771915-cb5256e1-6925-48b6-a712-83de145cc4d1.jpg" height="30%" width="30%">

### 7. 게시글 작성 페이지
  - 게시글을 작성할 수 있는 페이지입니다. 제목과 내용을 입력할 수 있고 이미지를 첨부할 수 있습니다. 익명 여부와 질문 여부를 선택할 수 있습니다.<br/>
<img src="https://user-images.githubusercontent.com/68140623/216771917-09700eda-98fd-4e3a-b109-77d24a917eec.jpg" height="30%" width="30%">

### 8. 채팅 목록 페이지
  - 채팅 목록을 조회할 수 있는 페이지입니다. 웹소켓을 이용하여 자동으로 새로고침됩니다. 최근 대화가 있었던 순으로 정렬됩니다. 읽지 않은 메시지를 조회할 수 있습니다.<br/>
<img src="https://user-images.githubusercontent.com/68140623/216771919-fe5447d4-16dd-4494-8fc5-f90fb214b9ef.jpg" height="30%" width="30%">

### 9. 채팅방 페이지
  - 채팅 메시지를 보내고 받을 수 있는 페이지입니다. 웹소켓을 이용하여 실시간으로 업데이트됩니다. 상단 앱 바의 메뉴 버튼을 통해 해당 채팅방을 차단/차단해제할 수 있습니다.<br/>
<img src="https://user-images.githubusercontent.com/68140623/216771922-cb8fd750-525a-41f5-b73e-f8cb59fd7983.jpg" height="30%" width="30%">

### 10. 마이페이지
  - 유저 및 앱 관련 설정을 할 수 있는 페이지입니다. 회원가입 시 학교인증을 하지 않았다면 이곳에서 진행할 수 있습니다. 비밀번호 변경, 닉네임 설정, 프로필 이미지 변경, 로그아웃을 할 수 있습니다. 다크모드를 설정할 수 있습니다.<br/>
<img src="https://user-images.githubusercontent.com/68140623/216771924-7ada74e1-f823-4345-8c23-ece744d3c659.jpg" height="30%" width="30%">

## 기능 상세
### 커서기반 페이지네이션
 - 무한스크롤에서 유저가 새로운 정보를 누락없이 제공받을 수 있도록 프로젝트에 커서기반 페이지네이션을 적용시켰습니다.
 - 구현은 주로 Flow, MutableList로 했으나 paging 라이브러리를 사용하기도 하였습니다.(채팅목록)
 
### 소셜로그인(웹뷰 적용)
 - RestApi로 Authorization Code를 넘겨주기 위하여 프로젝트에 웹뷰를 추가하였습니다.
 - Android Sdk를 이용하여 서버쪽으로 Auth Token을 넘겨주는 방법도 있었지만 웹쪽이 이미 Rest Api를 이용하고 있었고 서버 로직이 완성되있는 상황이어서 RestApi를 사용하기로 했습니다.
 - Rest Api 응답이 Intent로 카카오톡을 호출하게끔 만들고 싶었지만 실패했습니다. (웹뷰에 크롬을 popup처럼 띄워서 해결할 수 있다고 듣긴 했습니다.)

### AAC lifecycle 관리
 - 게시판 작업을 할 때 가장 어려웠던 점은 event에 따라 게시판이 의도된 동작을 하도록 만드는것이었습니다. 예를들어 유저가 글쓰기 layout에서 글쓰기 버튼을 눌렀다면, 게시물 목록으로 돌아오는 동시에 유저에게 새롭게 업데이트 된 게시물 목록이 보여야 합니다. 하지만 글쓰기 layout에서 취소를 눌렀다면 굳이 게시물 목록이 업데이트 될 필요가 없습니다. 이처럼 세밀한 동작로직을 구현하는 과정에서 많은 버그에 시달렸습니다.
 - 따라서 복잡성을 줄이기 위하여 ViewModel에서 Fragment로의 데이터 전달을 자동화 하였고 소통창구도 하나로 통일하였습니다.(일부 Fragment/ViewModel 에만 적용함)
 - 데이터 전달을 자동화하기 위해서 StateFlow, SharedFlow를 사용하였습니다.
 - 소통 창구를 통일시키기 위하여 StatusCode, ErrorCode, ErrorMessage, Data를 담는 DataClass를 만들고 이것만을 이용해 ViewModel에서 Fragment로의 데이터 전달이 이뤄지도록 했습니다.

### Network Interceptor
 - Accesss Token refresh 작업을 자동화하기 위해 okHttp3의 Network Interceptor를 사용하였습니다.
 - 모든 response 패킷을 검사해 Token에 오류가 있다는 응답을 받은 경우, refresh를 진행한 뒤 다시 해당 request를 보내도록 구현했습니다.
 <img src="https://square.github.io/okhttp/assets/images/interceptors%402x.png">

### Web Socket
- okHttp3의 웹소켓 기능을 이용하였습니다.
- okHttp3 클라이언트를 생성하고, client.newWebSocket()을 실행합니다. 이때 인자로 추상클래스 WebSocketListener를 구현한 object를 생성하여 인자로 넘겨줍니다.
- WebSocketListener의 onOpen()에서 webSocket 객체를 받아 저장해 둡니다.
- 메시지의 송신, 연결 종료는 받아둔 webSocket 객체의 send() 메서드를 이용합니다.
- 메시지의 수신과 에러처리는 WebSocketListener의 onMessage()와 onFailure()를 이용합니다.
<img src="https://miro.medium.com/max/1400/1*lwkkKGoVhdYpZhMfOo6hAA.png" height="80%" width="80%">

### Preference
- Androidx에서 지원하는 기능으로, 앱의 환경설정 화면을 별도의 레이아웃 지정 없이 빠르게 만들 수 있게 햬주며 key-value 쌍을 SharedPreference에 자동으로 저장할 수 있습니다.
- 다른 화면들과 달리 별도의 activity로 실행됩니다.
- 항목을 클릭하면 관련된 fragment로 이동하여 설정할 수 있습니다.
<img src="https://developer.android.com/static/images/hero-assets/android-jetpack.svg">


