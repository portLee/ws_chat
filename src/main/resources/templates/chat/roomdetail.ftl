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
        <div>
            <h2>{{room.name}}</h2>
        </div>
        <div class="input-group">
            <div class="input-group-prepend">
                <label class="input-group-text">내용</label>
            </div>
            <input type="text" class="form-control" v-model="message" v-on:keypress.enter="sendMessage">
        </div>
        <div class="input-group-append">
            <button class="btn btn-primary" type="button" @click="sendMessage">보내기</button>
        </div>
        <ul class="list-group">
            <li class="list-group-item" v-for="message in messages">
                <a href="#">{{message.sender}} - {{message.message}}</a>
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
                    room: {}, // 현재 채팅방 정보
                    sender: '', // 메시지 발신자
                    message: '', // 사용자가 입력한 메시지
                    messages: [] // 채팅 메시지 목록
                }
            },
            created() { // Vue 컴포넌트 생성 시 호출되는 lifecycle hook
                this.roomId = localStorage.getItem('wschat.roomId'); // localStorage에서 roomId 가져오기
                this.sender = localStorage.getItem('wschat.sender'); // localStorage에서 sender 가져오기
                this.findRoom(); // 채팅방 정보를 가져오는 메서드 호출
            },
            methods: {
                findRoom() { // 채팅방 정보를 가져오는 메서드
                    axios.get('/chat/room/' + this.roomId) // 서버로 GET 요청 전송
                        .then(response => { this.room = response.data; }) // 응답 데이터를 room에 저장
                        .catch(error => { console.error("방 정보를 가져오는 데 실패했습니다:", error); }); // 오류 발생 시 로그 출력
                },
                sendMessage() { // 사용자가 메시지를 전송할 때 호출되는 메서드
                    ws.send("/pub/chat/message", {}, JSON.stringify({ // STOMP 서버에 메시지 전송
                        type: 'TALK', // 메시지 타입 지정(대화)
                        roomId: this.roomId, // 현재 채팅방 ID
                        sender: this.sender, // 메시지 발신자
                        message: this.message // 전송할 메시지 내용
                    }));
                    this.message = ''; // 메시지를 전송한 후 입력란 초기화
                },
                recvMessage(recv) { // 수신된 메시지를 메시지 목록에 추가하는 메서드
                    this.messages.unshift({ // 메시지를 messages 배열의 앞쪽에 추가
                        type: recv.type, // 메시지 타입
                        sender: recv.type === 'ENTER' ? '[알림]' : recv.sender, // 알림 메시지인지 확인하여 발신자 설정
                        message: recv.message // 메시지 내용
                    });
                }
            }
        });

        // Vue 애플리케이션을 '#app' 요소에 마운트
        const vm = app.mount('#app');

        function connect() { // WebSocket 연결 설정을 위한 함수
            // STOMP 연결 시도
            ws.connect({}, function (frame) { // 연결 성공 시
               ws.subscribe("/sub/chat/room/" + vm.roomId, function (message) { // 서버로부터 메시지 구독
                   const recv = JSON.parse(message.body); // 수신된 메시지를 JSON으로 파싱
                   console.log(recv); // 디버그용 콘솔 출력
                   vm.recvMessage(recv); // 수신된 메시지를 화면에 표시
               });
               ws.send("/pub/chat/message", {}, JSON.stringify({ // 입장 메시지 전송
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