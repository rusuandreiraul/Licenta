<script setup>
import {useAuth} from "~/composable/useAuth.js";

const chatMessages = ref([]);
const message=ref("");

const props=defineProps({
  receiver: String,
})

const { user } = useAuth();

let stompClient=null;

function connect() {
  const socket=new SockJS("http://localhost:8080/gs-guide-websocket");
  stompClient=new Stomp.over(socket);
  stompClient.connect(() => { //ne  abonam pentru mesajele noi
    stompClient.subscribe("/user/queue/messages", (message) => {
      const receivedMessage = JSON.parse(message.body); //message este mesajul primit din backend
      chatMessage.push(receivedMessage); //userul care primeste trebuie sa se "aboneze" sa primeasca mesajul
    })
  });

}

async function loadHistory() {
  try {
    const response = await fetch(`http://localhost:8080/get-messages/${user.value.username}/${props.receiver}`);
    if (response.ok) {
      const data = await response.json();
      chatMessages.value = data;
    }
  } catch (error) {
    console.error("Nu am putut încărca istoricul:", error);
  }
}

function sendMessage() {
  const msg = {
    sender:user.value.username,
    receiver: props.receiver,
    content: message.value,
  };
  stompClient.send("/app/send",{},JSON.stringify(msg)); //asta se trimite catre backend
  chatMessages.value.push(msg);
  message.value = "";
}


onMounted(async() => {
  await loadHistory();
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
      <form>
        <UInput v-model="message"/>  <!-- verifica partea de mesaje si cum functioneaza cu componenta-->
      </form>
    </div>
  </div>
</template>
