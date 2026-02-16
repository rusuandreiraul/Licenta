<script setup>
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
    type: Number,
    required: true,
    default: 0,
  },
});
</script>

<template>
  <UCard>
    <template #header class="flex items-center">
      <div v-html="icon" class="flex-shrink-0"></div>
      <h1 class="text-bold">{{ props.title }}</h1>
    </template>

    <p class="text-center">{{ props.content }}</p>
  </UCard>
</template>
