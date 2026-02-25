<script setup>
const route = useRoute();
const username = route.params.username;
</script>

<template>
  <div class="flex flex-col">
    <div
      class="flex justify-center p-5 gap-3 border border-gray-100 shadows-xl"
    >
      <div>imagine</div>
      <div>
        <h1 class="font-bold">{{ username }}</h1>
      </div>
    </div>
    <div class="border border-gray-100 shadow-xl">
      aici vor fii goalurile userului streakul pe care il ai cu el si care sunt
      mancarurile lui preferate
    </div>
  </div>
</template>
