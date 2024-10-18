<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Websocket ChatRoom</title>
    <link rel="stylesheet" href="/webjars/bootstrap/4.5.3/dist/css/bootstrap.min.css">
    <style>
        [v-cloak] {
            display: none;
        }
    </style>
</head>
<body>
    <div class="container" id="app" v-cloak>
        <div class="row">
            <div class="col-md-6">
                <h2>{{roomName}}</h2>
            </div>
            <div class="col-md-6 text-right">
                <a class="btn btn-primary btn-sm" href="/logout">로그아웃</a>
            </div>
        </div>
        <div class="input-group">
            <div class="input-group-prepend">
                <label class="input-group-text">내용</label>
            </div>
            <input type="text" class="form-control" v-model="message" v-on:keypress.enter="sendMessage('TALK')">
            <div class="input-group-append">
                <button class="btn btn-primary" type="button" @click="sendMessage('TALK')">보내기</button>
            </div>
        </div>
        <ul class="list-group">
            <li class="list-group-item" v-for="message in messages">
                {{message.sender}} - {{message.message}}
            </li>
        </ul>
        <div></div>
    </div>

    <script src="/webjars/vue/3.5.10/dist/vue.global.js"></script>
    <script src="/webjars/axios/1.7.2/dist/axios.min.js"></script>
    <script src="/webjars/sockjs-client/1.1.2/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/2.3.3-1/stomp.min.js"></script>
    <script>
        // SockJS를 사용하여 WebSocket 연결 생성
        var sock = new SockJS("/ws-stomp");
        // STOMP 프로토콜을 통해 WebSocket 통신 설정
        var ws = Stomp.over(sock);
        // 재연결 시도 횟수를 위한 변수 초기화
        var reconnect = 0;

        // Vue 애플리케이션 생성
        const app = Vue.createApp({
            data() { // data 함수로 컴포넌트의 상태 데이터 반환
                return {
                    roomId: '', // 현재 채팅방 ID
                    roomName: '', // 현재 채팅방 이름
                    message: '', // 사용자가 입력한 메시지
                    messages: [], // 채팅 메시지 목록
                    token: '' // 사용자 토큰
                }
            },
            created() { // Vue 컴포넌트 생성 시 호출되는 lifecycle hook
                this.roomId = localStorage.getItem('wschat.roomId'); // localStorage에서 roomId 가져오기
                this.roomName = localStorage.getItem('wschat.roomName'); // localStorage에서 roomName 가져오기
                const _this = this; // 현재 컴포넌트의 참조를 유지하기 위한 변수
                axios.get('/chat/user')
                    .then(response => {
                        _this.token = response.data.token; // 사용자 토큰 설정
                        // STOMP를 통해 WebSocket 연결 설정
                        ws.connect({"token": _this.token}, function (frame) {
                            // 채팅방 구독: 해당 채팅방의 메시지를 수신
                            ws.subscribe("/sub/chat/room/" + _this.roomId, function (message) {
                                const recv = JSON.parse(message.body); // 수신된 메시지를 처리
                                _this.recvMessage(recv); // 수신한 메시지를 처리
                            });
                            _this.sendMessage('ENTER'); // 채팅방 입장 메시지 전송
                        }, function (error) { // 연결 실패 시
                            alert("서버 연결에 실패하였습니다. 다시 접속해 주십시요.");
                            location.href = "/chat/room"; // 채팅방 목록으로 이동
                        });
                    });
            },
            methods: {
                sendMessage(type) { // 사용자가 메시지를 전송할 때 호출되는 메서드
                    // STOMP 서버에 메시지 전송
                    ws.send("/pub/chat/message", {"token": this.token}, JSON.stringify({ // STOMP 서버에 메시지 전송
                        type: type, // 메시지 타입 지정(대화)
                        roomId: this.roomId, // 현재 채팅방 ID
                        message: this.message // 전송할 메시지 내용
                    }));
                    this.message = ''; // 메시지를 전송한 후 입력란 초기화
                },
                recvMessage(recv) { // 수신된 메시지를 메시지 목록에 추가하는 메서드
                    this.messages.unshift({ // 메시지를 목록의 앞쪽에 추가
                        type: recv.type, // 메시지 타입
                        sender: recv.sender, // 메시지 발신자
                        message: recv.message // 메시지 내용
                    });
                }
            }
        });

        // Vue 애플리케이션을 '#app' 요소에 마운트
        const vm = app.mount('#app');

        function connect() { // WebSocket 연결 설정을 위한 함수
            // STOMP 연결 시도
            ws.connect({"token": this.token}, function (frame) { // 연결 성공 시
               ws.subscribe("/sub/chat/room/" + vm.roomId, function (message) { // 서버로부터 메시지 구독
                   const recv = JSON.parse(message.body); // 수신된 메시지를 JSON으로 파싱
                   console.log(recv); // 디버그용 콘솔 출력
                   vm.recvMessage(recv); // 수신된 메시지를 화면에 표시
               });
               ws.send("/pub/chat/message", {"token": this.token}, JSON.stringify({ // 입장 메시지 전송
                   type: 'ENTER', // 메시지 타입을 '입장'으로 설정
                   roomId: vm.roomId, // 현재 채팅방 ID
                   sender: vm.sender // 입장하는 사용자 이름
               }));
            }, function (error) { // 연결 실패 시
                if (reconnect++ <= 5) { // 재연결 시도 횟수가 5번 이하일 때
                    setTimeout(function () { // 10초 후 재연결 시도
                        console.log("connection reconnect"); // 재연결 로그 출력
                        sock = new SockJS("/ws-stomp"); // 새로운 SockJS 인스턴스 생성
                        ws = Stomp.over(sock); // 새로운 STOMP 인스턴스 설정
                        connect(); // 재연결 함수 호출
                    }, 10 * 1000); // 10초 지연 시간 설정
                }
            });
        }

        connect(); // WebSocket 연결 설정 함수 호출
    </script>
</body>
</html>