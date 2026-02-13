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
        <div class="flex justify-between items-center">
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

    <template #footer>
      <UButton variant="ghost" block color="gray" class="hover:bg-gray-100">
        Vezi detalii complete
      </UButton>
    </template>
  </UCard>
</template>
