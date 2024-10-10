<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Websocket Chat</title>
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
            <div class="col-md-12">
                <h3>채팅방 리스트</h3>
            </div>
        </div>
        <div class="input-group">
            <div class="input-group-prepend">
                <label class="input-group-text">방제목</label>
            </div>
            <input type="text" class="form-control" v-model="room_name" v-on:keyup.enter="createRoom">
            <div class="input-group-append">
                <button class="btn btn-primary" type="button" @click="createRoom">채팅방 개설</button>
            </div>
        </div>
        <ul class="list-group">
            <li class="list-group-item list-group-item-action" v-for="item in chatrooms" v-bind:key="item.roomId" v-on:click="enterRoom(item.roomId)">
                {{item.name}}
            </li>
        </ul>
    </div>

    <script src="/webjars/vue/3.5.10/dist/vue.global.js"></script>
    <script src="/webjars/axios/1.7.2/dist/axios.min.js"></script>
    <script>
        // Vue 3 애플리케이션 생성
        const app = Vue.createApp({
            data() { // data() 함수로 컴포넌트의 상태 데이터 반환
                return {
                    room_name : '', // 사용자가 입력할 채팅방 이름을 저장
                    chatrooms: [] // 서버에서 가져온 채팅방 목록을 저장
                }
            },
            created() { // 컴포넌트 생성 시 호출되는 lifecycle hook
                this.findAllRoom(); // 컴포넌트가 생성되면 모든 채팅방을 불러오는 메서드 호출
            },
            methods: {
                findAllRoom() { // 서버에서 모든 채팅방을 가져오는 메서드
                    axios.get('/chat/rooms') // '/chat/rooms' 엔드포인트로 GET 요청
                        .then(response => { // 성공적으로 응답이 돌아오면
                            this.chatrooms = response.data; // 응답 데이터를 chatrooms 배열에 할당
                        })
                        .catch(error => { // 오류가 발생하면
                            console.log("방 목록을 가져오는데 실패했습니다: ", error); // 오류 메시지 출력
                        });
                },
                createRoom() { // 새로운 채팅방을 생성하는 메서드
                    if (this.room_name === "") { // 방 제목이 비어있는지 확인
                        alert("방 제목을 입력해 주십시요."); //  방 제목이 없으면 경고창 표시
                        return; // 방 제목이 비어있을 경우 함수 종료
                    } else {
                        const params = new URLSearchParams(); // POST 요청에 사용할 URLSearchParams 객체 생성
                        params.append("name", this.room_name); // room_name을 'name' 파라미터로 추가
                        axios.post('/chat/room', params) // '/chat/room' 엔드포인트로 POST 요청 전송
                            .then(response => { // 성공적으로 응답이 돌아오면
                                alert(response.data.name + "방 개설에 성공하였습니다."); // 방 개설 성공 메시지 표시
                                this.room_name = ''; // 입력 필드 초기화
                                this.findAllRoom(); // 채팅방 목록 갱신
                            })
                            .catch(response => { // 오류가 발생하면
                                alert("채팅방 개설에 실패하였습니다."); // 오류 메시지 표시
                            });
                    }
                },
                enterRoom(roomId) { // 특정 채팅방에 입장하는 메서드
                    const sender = prompt('대화명을 입력해 주세요.'); // 사용자에게 대화명 입력 요청
                    if (sender !== "") { // 대화명이 비어있지 않으면
                        localStorage.setItem('wschat.sender', sender); // localStorage에 대화명 저장
                        localStorage.setItem('wschat.roomId', roomId); // localStorage에 roomId 저장
                        location.href = "/chat/room/enter/" + roomId; // 선택한 채팅방으로 이동
                    }
                }
            }
        });

        app.mount('#app'); // '#app' 요소에 Vue 애플리케이션 마운트

        // 아래는 Vue 2 코드로, Vue 3과의 차이점을 보여주기 위해 주석처리
        // var vm = new Vue({
        //     el: '#app',
        //     data: {
        //         room_name : '',
        //         chatrooms: []
        //     },
        //     created() {
        //         this.findAllRoom();
        //     },
        //     methods: {
        //         findAllRoom: function () {
        //             axios.get('/chatroom').then(response => { this.chatrooms = response.data; });
        //         },
        //         createRoom: function () {
        //             if ("" == this.room_name) {
        //                 alert("방 제목을 입력해 주십시요.");
        //                 return;
        //             } else {
        //                 var params = new URLSearchParams();
        //                 params.append("name", this.room_name);
        //                 axios.post('/chat/room', params)
        //                     .then(
        //                         response => {
        //                             alert(response.data.name + "방 개설에 성공하였습니다.");
        //                             this.room_name = '';
        //                             this.findAllRoom();
        //                         }
        //                     )
        //                     .catch( response => { alert("채팅방 개설에 실패하였습니다."); } );
        //             }
        //         },
        //         enterRoom: function (roomId) {
        //             var sender = prompt('대화명을 입력해 주세요.');
        //             if (sender != "") {
        //                 localStorage.setItem('wschat.sender', sender);
        //                 localStorage.setItem('wschat.roomId', roomId);
        //                 location.href = "/chat/room/enter/" + roomId;
        //             }
        //         }
        //     }
        // })
    </script>
</body>
</html>