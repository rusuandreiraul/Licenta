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

console.log("USER", user);

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
    const target = props.receiver;

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
    sender: user.value,
    receiver: props.receiver,
    content: message.value,
  };
  stompClient.send("/app/send",{},JSON.stringify(msg)); //asta se trimite catre backend
  chatMessages.value.push({
    ...msg,
    sender:user.value,
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
  <div class="flex flex-col h-[calc(100vh-120px)] bg-gray-50 rounded-2xl border border-gray-100 shadow-sm overflow-hidden">

    <div class="p-4 bg-white border-b border-gray-100 flex items-center justify-between shadow-sm">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold uppercase">
          {{ props.receiver ? props.receiver.substring(0, 2) : 'AI' }}
        </div>
        <div>
          <h3 class="text-sm font-bold text-gray-800">
            {{  props.receiver }}
          </h3>
        </div>
      </div>
    </div>

    <div class="flex-1 overflow-y-auto p-4 flex flex-col gap-3 bg-slate-50/50">

      <div v-if="chatMessages.length === 0" class="h-full flex flex-col items-center justify-center text-center p-6">
        <UIcon name="i-lucide-message-square" class="w-8 h-8 text-gray-400 mb-2" />
        <p class="text-sm text-gray-500 italic">Începe o conversație nouă. Trimite primul mesaj!</p>
      </div>

      <div
          v-for="(msg, index) in chatMessages"
          :key="index"
           :class="['max-w-[75%] rounded-2xl p-3 text-sm shadow-sm transition-all flex flex-col',

      msg.sender === user
      ? 'bg-blue-600 text-white rounded-tr-none self-end'
      : 'bg-white text-gray-800 rounded-tl-none border border-gray-100 self-start'
      ]"
      >
        <p v-if="msg.sender !== user" class="text-[10px] font-bold text-blue-600 mb-1 uppercase">
          {{ msg.sender }}
        </p>

        <p class="break-words leading-relaxed">{{ msg.content }}</p>

        <span
            :class="[
            'text-[9px] block text-right mt-1 font-light',
            msg.sender === user? 'text-blue-200' : 'text-gray-400'
          ]"
        >
          {{ msg.timestamp ? new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'Acum' }}
        </span>
      </div>
    </div>

    <div class="p-4 bg-white border-t border-gray-100">
      <form @submit.prevent="sendMessage" class="flex gap-2 items-center">
        <div class="flex-1 relative flex items-center">
          <UTextarea
              v-model="message"
              placeholder="Scrie un mesaj..."
              variant="none"
              class="w-full bg-gray-50 border border-gray-200 focus:border-blue-500 rounded-xl px-4 py-2.5 pr-10 text-sm focus:outline-none transition-all"
              @keyup.enter="sendMessage"
          />
        </div>

        <UButton
            type="submit"
            color="primary"
            variant="solid"
            icon="i-lucide-send"
            :disabled="!message.trim()"
            class="rounded-xl px-4 py-2.5 bg-green-600 hover:bg-green-700 shadow-md shadow-green-200 font-semibold text-white flex items-center justify-center transition-all disabled:opacity-50 disabled:cursor-not-allowed disabled:shadow-none"
        >
          Trimite
        </UButton>
      </form>
    </div>

  </div>
</template>
