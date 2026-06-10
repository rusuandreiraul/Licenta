<script setup>
import {useAuth} from "~/composable/useAuth.js";
import MarkdownIt from 'markdown-it'

const userMessage = ref("");
const messages = ref([]);
const isLoading = ref(false);


const md = new MarkdownIt()


const {token}=useAuth();

function sendMessage() {
  if (!userMessage.value.trim()) return;

  const textToSend = userMessage.value;

  messages.value.push({
    id: crypto.randomUUID(),
    role: "user",
    parts: [{ type: "text", text: textToSend }],
  });

  userMessage.value = "";


  fetchInformationFromAI(textToSend);
}

async function fetchInformationFromAI() {
  const lastMessageText = messages.value[messages.value.length - 1].parts[0].text;
  isLoading.value = true;

  try {
    const response = await fetch("http://localhost:8080/ai/chat", {
      method: "POST",
      headers: { "Content-Type": "text/plain",
        "Authorization": `Bearer ${token.value}`,
      },
      body: lastMessageText
    });

    if (!response.ok) throw new Error("Eroare la server");

    const textFromAi = await response.text();

    messages.value.push({
      id: crypto.randomUUID(),
      role: "assistant",
      parts: [{ type: "text", text: textFromAi }],
    });
  } catch (err) {
    console.error("AI Error:", err);
    messages.value.push({
      id: crypto.randomUUID(),
      role: "assistant",
      parts: [{ type: "text", text: "Momentan nu pot răspunde. Încearcă mai târziu." }],
    });
  } finally {
    isLoading.value = false;
  }
}
/*async function getOverview(){
  try {
    const response = await fetch("http://localhost:8080/ai/daily-overview", {
      method: "GET",
      headers: { "Content-Type": "text/plain",
        "Authorization": `Bearer ${token.value}`},
    });

    if (!response.ok) throw new Error("Eroare la server");

    const textFromAi = await response.text();

    messages.value.push({
      id: crypto.randomUUID(),
      role: "assistant",
      parts: [{ type: "text", text: textFromAi }],
    });
  } catch (err) {
    console.error("AI Error:", err);
  }
  finally {
    isLoading.value = false;
  }
}

const raspunsRenderAI = computed(() => {
  return md.render(raspunsAI.value)
})

onMounted(() => {
  getOverview();
})
*/
</script>

<template>
  <div class="flex flex-col h-full relative">
    <div class="flex-1 overflow-y-auto p-2">
      <UChatMessages :messages="messages" should-auto-scroll />
      <!-- Indicator de încărcare discret -->
      <div v-if="isLoading" class="text-xs text-gray-400 animate-pulse p-2">
        Gemini analizează datele tale...
      </div>
    </div>

    <div class="p-2 border-t bg-white">
      <input
          :disabled="isLoading"
          class="border rounded-2xl bg-gray-50 w-full p-2 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50"
          type="text"
          placeholder="Intreaba-ma ceva...."
          v-model="userMessage"
          @keyup.enter="sendMessage"
      />
    </div>
  </div>
</template>