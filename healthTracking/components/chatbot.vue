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

  fetchInformationFromAI(); //trimitem mesajul userului catre backend sa gestioneze AI
}

async function fetchInformationFromAI() {
  const lastMessageText = messages.value[messages.value.length - 1].parts[0].text;
  try{
    const response=await fetch("http://localhost:8080/api/ai/chat", {
      method: "POST",
      headers: { "Content-Type": "application/json"},
      body: JSON.stringify({message:lastMessageText})
    });
    const data=await response.json();

    messages.value.push({
      id:crypto.randomUUID(),
      role: "assistant",
      parts: [{ type: "text", text: data.text }],
    })
  }
  catch(err){
    console.error(err);
  }
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
