<script setup>
import { ref } from "vue";
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
  <div class="flex flex-col h-full relative bg-white dark:bg-gray-900 rounded-xl overflow-hidden">

    <div class="flex-1 overflow-y-auto p-4 space-y-4 scrollbar-thin">

      <div
          v-for="msg in messages"
          :key="msg.id"
          class="flex flex-col w-full"
          :class="msg.role === 'user' ? 'items-end' : 'items-start'"
      >
        <div
            class="max-w-[85%] rounded-2xl px-3.5 py-2.5 text-sm shadow-2xs leading-relaxed"
            :class="msg.role === 'user'
            ? 'bg-green-600 text-white rounded-tr-xs'
            : 'bg-gray-100 dark:bg-gray-800 text-gray-800 dark:text-gray-100 rounded-tl-xs prose dark:prose-invert max-w-none text-xs'"
        >
          <!-- Utilizatorul vede text simplu, asistentul AI vede textul rândat prin MarkdownIt -->
          <span v-if="msg.role === 'user'">{{ msg.parts[0].text }}</span>
          <div v-else v-html="md.render(msg.parts[0].text)"></div>
        </div>
      </div>

      <!-- Indicator de încărcare discret și elegant -->
      <div v-if="isLoading" class="flex items-center gap-2 p-2">
        <div class="flex space-x-1.5 items-center justify-center">
          <div class="w-1.5 h-1.5 bg-green-500 rounded-full animate-bounce [animation-delay:-0.3s]"></div>
          <div class="w-1.5 h-1.5 bg-green-500 rounded-full animate-bounce [animation-delay:-0.15s]"></div>
          <div class="w-1.5 h-1.5 bg-green-500 rounded-full animate-bounce"></div>
        </div>
        <span class="text-[11px] font-medium text-gray-400 dark:text-gray-500 italic">
          Asistentul analizează datele tale...
        </span>
      </div>
    </div>

    <!-- Zonă de Input stilizată -->
    <div class="p-3 border-t border-gray-100 dark:border-gray-800 bg-white dark:bg-gray-900">
      <div class="relative flex items-center">
        <input
            :disabled="isLoading"
            class="w-full bg-gray-50 dark:bg-gray-800 border border-gray-200 dark:border-gray-700 focus:border-green-500 focus:bg-white focus:ring-4 focus:ring-indigo-500/10 transition-all rounded-xl pl-4 pr-11 py-3 text-sm text-gray-800 dark:text-gray-100 placeholder-gray-400 outline-none disabled:opacity-50"
            type="text"
            placeholder="Întreabă-mă ceva despre progresul tău..."
            v-model="userMessage"
            @keyup.enter="sendMessage"
        />


        <button
            @click="sendMessage"
            :disabled="isLoading || !userMessage.trim()"
            class="absolute right-2 p-1.5 text-gray-400 hover:text-green-600 disabled:hover:text-gray-400 disabled:opacity-40 transition-colors rounded-lg outline-none"
        >
          <svg class="w-5 h-5 transform rotate-45" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>