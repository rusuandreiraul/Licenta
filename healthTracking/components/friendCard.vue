<script setup>
import {useAuth} from "~/composable/useAuth.js";
import {useSocial} from "~/composable/useSocials.js";

const props = defineProps({
  username: String,
  content: String,
  urlImage: String,
  createDate: String,
});

const {token}=useAuth();

const{triggerChallengeUpdate}=useSocial();

const streak=ref();






async function fetchStreak(){
  const response=await fetch(`http://localhost:8080/streak/${props.username}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token.value}`,
    }
  });
  if(response.ok){
    console.log("streak", streak.value);
    streak.value=await response.text();
  }
}

onMounted(async () => {
  fetchStreak();
})


</script>

<template>
  <div
    class="relative bg-neutral-primary-soft max-w-xs w-full p-6 border border-default rounded-base shadow-xs"
  >

    <div class="flex flex-col items-center">
      <div class="absolute left-2 top-2 font-semibold italic">
        🔥 {{streak}}
      </div>
      <img class="w-24 h-24 mb-6 rounded-full" alt="Bonnie image" />
      <h5 class="mb-0.5 text-xl font-semibold tracking-tight text-heading">
        {{ props.username }}
      </h5>
      <span class="text-sm text-body">{{ props.content }}</span>
      <span class="text-sm text-body">{{ props.createDate }}</span>
      <div class="flex mt-4 md:mt-6 gap-4">
        <UButton
          label="Challenge"
          color="success"
          @click="triggerChallengeUpdate(props.username)"
        />

        <USlideover title="Chat">
          <UButton label="Message" color="neutral" variant="outline" />

          <template #body>
            <MessageChat :receiver="props.username" />
          </template>
        </USlideover>
      </div>
    </div>
  </div>
</template>
