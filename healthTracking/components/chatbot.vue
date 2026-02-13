<script setup>
const userMessage = ref("");
const messages = ref([]);

function sendMessage() {
  if (!userMessage.value.trim()) return;

  // adăugăm mesajul utilizatorului
  messages.value.push({
    id: crypto.randomUUID(),
    role: "user",
    parts: [{ type: "text", text: userMessage.value }],
  });

  userMessage.value = "";

  // apel backend / AI
  fetchInformationFromAI();
}

async function fetchInformationFromAI() {
  const lastMessage = messages.value[messages.value.length - 1];

  // simulăm răspuns AI (în realitate aici apelezi backend-ul)
  const aiResponse = {
    id: crypto.randomUUID(),
    role: "assistant",
    parts: [{ type: "text", text: "Aceasta este o răspuns simulată." }],
  };

  // adăugăm răspunsul AI după un delay
  setTimeout(() => {
    messages.value.push(aiResponse);
    scrollToBottom();
  }, 500);
}
</script>

<template>
  <div>
    <UChatMessages :messages="messages" should-auto-scroll />
    <input
      class="border bg-amber-50 w-full relative pb-2"
      type="text"
      v-model="userMessage"
      @keyup.enter="sendMessage"
    />
  </div>
</template>
