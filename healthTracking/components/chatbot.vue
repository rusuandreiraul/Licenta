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

  <div class="flex flex-col h-full relative">

    <div class="flex-1 overflow-y-auto p-2">
      <UChatMessages :messages="messages" should-auto-scroll />
    </div>


    <div class="p-2 border-t bg-white">
      <input
          class="border rounded-2xl bg-gray-50 w-full p-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          type="text"
          placeholder="Intreaba-ma ceva...."
          v-model="userMessage"
          @keyup.enter="sendMessage"
      />
    </div>
  </div>
</template>
