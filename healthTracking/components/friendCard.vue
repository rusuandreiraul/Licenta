<script setup>
import { ref, onMounted } from "vue";
import { useAuth } from "~/composable/useAuth.js";
import { useSocial } from "~/composable/useSocials.js";

const props = defineProps({
  username: String,
  content: String,
  urlImage: String,
  createDate: String,
});

const { token } = useAuth();
const { triggerChallengeUpdate } = useSocial();
const streak = ref();

async function fetchStreak() {
  const response = await fetch(`http://localhost:8080/streak/${props.username}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token.value}`,
    }
  });
  if (response.ok) {
    streak.value = await response.text();
  }
}

async function onChallengeClick() {
  await triggerChallengeUpdate(props.username);
  await fetchStreak(); // reapelare pentru update la streak
}

onMounted(async () => {
  fetchStreak();
});
</script>
<template>
  <div
      class="relative bg-neutral-primary-soft max-w-xs w-full p-6 border border-default rounded-xl shadow-md transition-all duration-200 hover:shadow-lg flex flex-col justify-between min-h-[380px]"
  >
    <!-- Streak Badge -->
    <div
        class="absolute right-3 top-3 flex items-center gap-1 bg-orange-500/10 text-orange-600 dark:text-orange-400 text-xs font-bold px-2.5 py-1 rounded-full border border-orange-500/20 shadow-xs"
    >
      <span>🔥</span>
      <span>{{ streak || 0 }}</span>
    </div>

    <!-- Partea de sus: Detalii utilizator și text postare -->
    <div class="flex flex-col items-center text-center w-full">
      <!-- Avatar -->
      <div class="relative mb-4">
        <img
            class="w-24 h-24 rounded-full object-cover ring-4 ring-default/50 shadow-sm"
            :src="props.urlImage"
            :alt="props.username"
        />
      </div>

      <!-- Username -->
      <h5 class="text-lg font-bold tracking-tight text-heading mb-1">
        {{ props.username }}
      </h5>

      <!-- Mesajul Utilizatorului -->
      <p class="text-sm text-body/90 font-medium px-2 min-h-[2.5rem] whitespace-pre-line line-clamp-3">
        {{ props.content.split('\n\n')[0] }}
      </p>

      <!-- BADGE DINAMIC PENTRU ACTIVITATE -->
      <div v-if="props.content.includes('\n\n')" class="w-full mt-3 px-2">
        <div
            class="flex items-center justify-center gap-1.5 px-3 py-2 rounded-xl text-xs font-semibold border transition-colors"
            :class="{
            'bg-green-500/10 text-green-600 border-green-500/20': props.content.split('\n\n')[1]?.includes('Activitate'),
            'bg-blue-500/10 text-blue-600 border-blue-500/20': props.content.split('\n\n')[1]?.includes('Somn'),
            'bg-amber-500/10 text-amber-600 border-amber-500/20': props.content.split('\n\n')[1]?.includes('Consumat')
          }"
        >
          <UIcon v-if="props.content.split('\n\n')[1]?.includes('Activitate')" name="i-lucide-dumbbell" class="w-4 h-4 shrink-0" />
          <UIcon v-else-if="props.content.split('\n\n')[1]?.includes('Somn')" name="i-lucide-moon" class="w-4 h-4 shrink-0" />
          <UIcon v-else-if="props.content.split('\n\n')[1]?.includes('Consumat')" name="i-lucide-apple" class="w-4 h-4 shrink-0" />

          <span class="truncate">{{ props.content.split('\n\n')[1] }}</span>
        </div>
      </div>
    </div>

    <!-- Partea de jos: Dată și Butoane (Rămân mereu lipite de bază) -->
    <div class="flex flex-col items-center text-center w-full mt-4">
      <span class="text-xs text-body/60 font-light italic mb-4">
        {{ props.createDate }}
      </span>

      <div class="flex gap-3 w-full justify-center">
        <UButton
            label="Challenge"
            color="success"
            variant="solid"
            class="px-4 py-2 font-semibold shadow-xs flex-1 justify-center"
            @click="onChallengeClick"
        />

        <USlideover title="Chat" class="flex-1">
          <UButton
              label="Message"
              color="neutral"
              variant="outline"
              class="px-4 py-2 font-semibold w-full justify-center"
          />

          <template #body>
            <MessageChat :receiver="props.username" />
          </template>
        </USlideover>
      </div>
    </div>
  </div>
</template>