<script setup>
const route = useRoute();
const username = route.params.username;
</script>

<template>
  <div class="flex flex-col w-full">
    <div
      class="grid grid-cols-3 items-center p-5 border border-gray-100 shadow-xl w-full"
    >
      <div class="flex justify-start">
        <GoalsCard :targetUsername="username" />
      </div>

      <div class="flex flex-col items-center text-center gap-3">
        <img
          class="w-24 h-24 rounded-full border-2 border-indigo-500 object-cover"
          src="https://via.placeholder.com/150"
          alt="Avatar user"
        />
        <div>
          <h1 class="font-bold text-xl">{{ username }}</h1>
          <div class="flex gap-2 mt-2 justify-center">
            <UButton variant="solid">Follow</UButton>
            <UButton variant="outline">Message</UButton>
          </div>
        </div>
      </div>

      <div class="hidden md:block"></div>
    </div>

    <div class="p-10 mt-5 border border-gray-100 shadow-xl w-full">
      <h1 class="font-bold">Feed</h1>
      <FriendCard />
    </div>
  </div>
</template>
