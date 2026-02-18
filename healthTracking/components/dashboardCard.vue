<script setup>
import { isEmpty } from "@nuxt/ui/runtime/utils/index.js";
import ProgressBar from "./progressBar.vue";

const props = defineProps({
  title: {
    type: String,
    required: true,
  },
  icon: {
    type: String,
    required: true,
  },
  content: {
    //contet asta trebuie facut altfel in cat sa primeasca diferite tipuri de date
    type: [Array, Object],
    required: true,
    default: () => ({}),
  },
});
</script>
<template>
  <UCard class="shadow-lg rounded-2xl border-none">
    <template #header>
      <div class="flex items-center gap-2 font-bold text-xl">
        <span v-html="props.icon"></span>
        {{ props.title }}
      </div>
    </template>

    <div v-if="props.title === 'Activity'" class="flex flex-col gap-4">
      <div
        v-for="(item, index) in props.content"
        :key="index"
        class="p-4 bg-blue-50 rounded-xl border border-blue-100 hover:shadow-md transition-shadow"
      >
        <div class="flex justify-between items-start mb-2">
          <p class="font-bold text-gray-800">{{ item?.activityType }}</p>
          <span class="text-xs text-gray-400 font-medium">{{
            item?.activityDate
          }}</span>
        </div>

        <div class="grid grid-cols-2 gap-3">
          <div class="flex items-center gap-2 text-sm text-gray-600">
            <UIcon name="i-heroicons-clock" class="text-blue-400" />
            <span>{{ item?.duration }} min</span>
          </div>
          <div class="flex items-center gap-2 text-sm text-gray-600">
            <UIcon name="i-heroicons-fire" class="text-orange-400" />
            <span>{{ item?.calories }} kcal</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="props.title === 'Sleep'">
      <div
        class="p-4 pb-25 bg-purple-50 rounded-xl border border-purple-100 flex flex-col gap-3"
      >
        <div class="flex flex-col justify-between items-center">
          <p class="font-bold text-purple-900">Calitate Somn</p>
          <span class="text-xs text-purple-400">{{
            props.content?.dateSleep
          }}</span>
        </div>

        <div class="grid grid-cols-2 gap-4 text-center">
          <div class="bg-white p-2 rounded-lg">
            <p class="text-xs text-gray-400 uppercase">Scor</p>
            <p class="font-bold text-purple-600 text-lg">
              {{ props.content?.quality }}/5
            </p>
          </div>
          <div class="bg-white p-2 rounded-lg">
            <p class="text-xs text-gray-400 uppercase">Durată</p>
            <p class="font-bold text-purple-600 text-lg">
              {{ props.content?.hoursSlept }}h
            </p>
          </div>
        </div>
      </div>
    </div>

    <div v-else>
      <div
        class="bg-orange-200 border-amber-500 gap-2 m-2 p-3 rounded-xl border grid grid-cols-2 justify-center items-center"
      >
        <div class="flex flex-col justify-center items-center">
          <span class="mb-1"
            ><svg
              class="w-6 h-6 text-gray-800 dark:text-white"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              fill="none"
              viewBox="0 0 24 24"
            >
              <path
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M18.122 17.645a7.185 7.185 0 0 1-2.656 2.495 7.06 7.06 0 0 1-3.52.853 6.617 6.617 0 0 1-3.306-.718 6.73 6.73 0 0 1-2.54-2.266c-2.672-4.57.287-8.846.887-9.668A4.448 4.448 0 0 0 8.07 6.31 4.49 4.49 0 0 0 7.997 4c1.284.965 6.43 3.258 5.525 10.631 1.496-1.136 2.7-3.046 2.846-6.216 1.43 1.061 3.985 5.462 1.754 9.23Z"
              />
            </svg>
          </span>
          <span class="text-xs font-bold uppercase text-orange-800"
            >Calorii</span
          >
          {{ props.content.stats?.calories }} kcal
        </div>
        <div class="flex flex-col items-center justify-center">
          <span class="mb-1"
            ><svg
              class="w-6 h-6 text-gray-800 dark:text-white"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              fill="none"
              viewBox="0 0 24 24"
            >
              <path
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="m4 12 2.66667-1 2.66666 1L12 11l2.6667 1 2.6666-1L20 12m-1 5H5v1c0 1.1046.89543 2 2 2h10c1.1046 0 2-.8954 2-2v-1ZM5 9.00003h14v-1c0-2.20914-1.7909-4-4-4H9c-2.20914 0-4 1.79086-4 4v1ZM18.5 14h-13c-.82843 0-1.5.6716-1.5 1.5 0 .8285.67157 1.5 1.5 1.5h13c.8284 0 1.5-.6715 1.5-1.5 0-.8284-.6716-1.5-1.5-1.5Z"
              />
            </svg>
          </span>
          <span class="text-xs font-bold uppercase text-amber-900"
            >Grăsimi</span
          >
          {{ props.content.stats?.fat }} g
        </div>
      </div>
      <USeparator />
      <div
        class="bg-orange-200 border-amber-500 gap-2 m-2 p-3 rounded-xl border grid grid-cols-2"
      >
        <div class="flex flex-col items-center justify-center">
          <span class="mb-1"
            ><svg
              class="w-6 h-6 text-gray-800 dark:text-white"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              fill="none"
              viewBox="0 0 24 24"
            >
              <path
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 19v-9c-1.5 0-2-1.5-2-2.49997 0-.99997.5-2 2-2.50003 2.36416-.64329 4.2009-.97557 6-.98904 1.7991.01347 3.6358.34575 6 .98904 1.5.50003 2 1.50006 2 2.50003C20 8.5 19.5 10 18 10v9c0 .5523-.4477 1-1 1H7c-.55228 0-1-.4477-1-1Z"
              />
            </svg>
          </span>
          <span class="text-xs font-bold uppercase text-yellow-900">Carbo</span>
          {{ props.content.stats?.carbs }} g
        </div>
        <div class="flex flex-col justify-center items-center">
          <span class="mb-1">
            <svg
              class="w-6 h-6 text-gray-800 dark:text-white"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              fill="none"
              viewBox="0 0 24 24"
            >
              <path
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12.4427 9.88469 9.93591 7.50961c.81449-.81448.80559-2.06903-.02046-2.89511-.82608-.82607-2.08062-.83494-2.89511-.02046-.4791.47911-.71525 1.20118-.56606 1.82948-.6283-.14919-1.35037.08695-1.82948.56606-.81448.81448-.80561 2.06903.02047 2.89511.82607.82611 2.08062.83491 2.8951.02046l2.50233 2.29305m.8063-1.38c1.83-1.8299 5.1241-1.22213 7.1925.8462 2.0684 2.0684 2.3191 4.6228.2978 6.6441-1.0322 1.0321-2.1287 1.6094-3.2302 1.6518.5878-1.3405.2254-2.5874-.8127-3.2811-.918-.6135-2.1806-.7802-3.5479.179-1.10401-2.0578-1.30393-4.6355.1005-6.04Z"
              />
            </svg>
          </span>
          <span class="text-xs font-bold uppercase text-red-900">Proteine</span>
          {{ props.content.stats?.protein }} g
        </div>
      </div>
    </div>

    <template #footer>
      <UButton variant="ghost" block color="gray" class="hover:bg-gray-100">
        Vezi detalii complete
      </UButton>
    </template>
  </UCard>
</template>
