<script setup>
import {useAuth} from "~/composable/useAuth.js";

import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const chatMessages = ref([]);
const message=ref("");

const props=defineProps({
  receiver: String,
})

const { user, token } = useAuth();

let stompClient=null;

function connect() {
  const socket=new SockJS("http://localhost:8080/gs-guide-websocket");
  stompClient=Stomp.over(socket);

  const headers={
    Authorization: `Bearer ${token.value}`
  }

  stompClient.connect(headers, () => { //ne  abonam pentru mesajele noi
    stompClient.subscribe("/user/queue/messages", (message) => {
      if(message.body) {
        const receivedMessage = JSON.parse(message.body); //message este mesajul primit din backend
        chatMessages.value.push(receivedMessage); //userul care primeste trebuie sa se "aboneze" sa primeasca mesajul
      }
      })
  });

}

async function loadHistory() {
  try {
    if(!token.value){
      console.error("Nu avem token! Utilizatorul nu este logat.");
    }
    const target = typeof props.receiver === 'object' ? props.receiver.username : props.receiver;

    const url = `http://localhost:8080/chat-history/${target}`;

    console.log(url);

    const response = await fetch(url,
        {
          method: "GET",
          headers:{
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token.value}`
          }
        });
    if (response.ok) {
      const data = await response.json();
      console.log(data);
      chatMessages.value = data;
    }
  } catch (error) {
    console.error("Nu am putut încărca istoricul:", error);
  }
}

function sendMessage() {
  const msg = {
    receiver: props.receiver.username,
    content: message.value,
  };
  stompClient.send("/app/send",{},JSON.stringify(msg)); //asta se trimite catre backend
  chatMessages.value.push({
    ...msg,
    sender:user.value.username,
    timestamp:Date.now(),
  });
  message.value = "";
}


onMounted(() => {
  loadHistory();
  connect();
})

onUnmounted(() => { //inchide socketul cand iesim
  if (stompClient) stompClient.disconnect();
});

</script>

<template>
  <div class="min-h-screen flex flex-col relative">
  <UChatMessages :messages="chatMessages"/>

    <div class="absolute bottom-0 center bg-white">
      <form @submit.prevent="sendMessage">
        <UInput v-model="message" placeholder="Scrie un mesaj..." />
        <Button type="submit">Trimite</Button>
      </form>
    </div>
  </div>
</template>
