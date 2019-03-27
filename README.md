# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* http 리퀘스트 에서 첫 번째 줄은 무조건 요청라인이다. ex) GET /user/create HTTP/1.1
* 두 번째 줄 부터 빈 공백 문자열이 나올 때 까지 전부 요청 헤더에 포함된다.(클라이언트->서버)
* 빈 공백 문자열 이후는 요청 본문에 해당된다.
* 공백 문자열 이후 원하는 경로에서 읽어들이고자 하는 문서를 한줄씩 읽어들이면 된다.


### 요구사항 2 - get 방식으로 회원가입
* form.html을 보면 get메소드를 이용하여 http리퀘스트를 보낸다.
* 따라서 회원가입url이 있을 시 파싱해서 map에 저장하면 된다.
* 파싱에 관련된 api는 이미 util.HttpRequestUtils 클래스에 메소드로 완성되어있기 때문에 가져다 사용하면 된다.

### 요구사항 3 - post 방식으로 회원가입
* 우선 이전에 get방식으로 http 리퀘스트를 서버에 전달하였다. 따라서  post형식으로 바꾸기 위해서는 form.html 파일에서 get메소드 방식을 post 방식으로 변경해 주어야 한다.
* post방식으로 변경되었기 때문에 요청 uri에 포함되어있던 쿼리스트링은 http 요청의 본문을 통해서 전달되다.
* post방식으로 데이터를 전달하면서 헤더에 본문 데이터에 대한 길이가 Content-Length라는 필드 이름으로 전달된다.
* 따라서 Content-Length의 값을 구해 본문의  길이를 구하고 본문을 읽은 후  Map 형태로 변환하면 된다. 본문을 읽는 기능은 이미 구현되어있다.

### 요구사항 4 - redirect 방식으로 이동
* 회원가입을 완료하면  ./index.html 페이지로 이동을 해야하는데 현재의 url  상태가 /user/create 이기 때문에 읽어서 전달할 파일이 없다.
* 따라서회원가입 완료후  url에 /user/create 가 아니라 ./index.html 로 변경해주어야 한다.
* 하지만 그렇게 하면 문제가 발생한다.  단순히 url을 ./index.html로 변경하면 브라우저가 이전 요청 정보를 유지하고 있기 때문에 데이터가 중복으로 요청되는 이슈가 발생하게 된다. 이것을 해결하기 위해서는 회원가입을 처리하는 /user/create 요청과 첫화면을 보여주는 요청을 분리하고 http 302 상태코드를 활용하여야 한다. 즉 웹서버는 /user/create 요청을 받아 회원가입을 완료한 후 응답을 보낼 때 클라이언트에게 /index.html로 이동하도록 할 수 있다.
응답을 보낼 때 Location 을 활용하여 응답을 보낸다.
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
