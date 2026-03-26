<script setup>
import { useAuth } from "~/composable/useAuth";

const route = useRoute();
const username = route.params.username;

const { user } = useAuth();

const isChatOpen = ref(false);

const loggedUser = user.value;

const isFollowed = ref(false);

const posts = ref([]);

async function checkFollow() {
  try {
    const response = await fetch(
      `http://localhost:8080/check-follow/${loggedUser}/${username}`
    );
    if (response.ok) {
      isFollowed.value = await response.json();
    }
  } catch (e) {
    console.error(e);
  }
}

async function followUser() {
  try {
    const response = await fetch(
      `http://localhost:8080/follow-user/${loggedUser}/${username}`
    );

    if (response.ok) {
      isFollowed.value = !isFollowed.value;
      const res = await response.text();
      console.log(res);
    }
  } catch (e) {
    console.error(e);
  }
}

async function getPosts() {
  try {
    const response = await fetch(`http://localhost:8080/posts/${username}`);

    if (response.ok) {
      posts.value = await response.json();
    }
  } catch (e) {
    console.error(e);
  }
}

onMounted(() => {
  checkFollow();
  getPosts();
});
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
          <div
            v-if="loggedUser != username"
            class="flex gap-2 mt-2 justify-center"
          >
            <UButton
              v-if="isFollowed === false"
              variant="solid"
              @click="followUser()"
              >Follow</UButton
            >
            <UButton v-else @click="followUser()">Unfollow</UButton>
            <USlideover title="Chat">
              <UButton label="Message" color="neutral" variant="outline" />

              <template #body>
                <MessageChat :receiver="{username}" />
              </template>
            </USlideover>
          </div>
        </div>
      </div>

      <div class="hidden md:block"></div>
    </div>

    <div class="p-10 mt-5 border border-gray-100 shadow-xl w-full">
      <h1 class="font-bold">Feed</h1>
      <friendCard
        v-for="p in posts"
        :key="p.id"
        :username="p.username"
        :content="p.content"
        :urlImage="p.urlImage"
        :createDate="p.publishDate"
      />
    </div>
  </div>
</template>
