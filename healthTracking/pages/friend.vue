<script setup>
import { ref, onMounted } from "vue";
import friendCard from "~/components/friendCard.vue";
import { useAuth } from "~/composable/useAuth";
const friendName = ref("");
const friendList = ref([]);

const {user, token } = useAuth();

const posts = ref([]);

const postContent = ref("");

const leaderboard=ref({})

async function getLeaderboard(){
  try {

    const response = await fetch(`http://localhost:8080/leaderboard`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      }

    });
    if (response.ok) {
      const data = await response.json();
      leaderboard.value = data;
      console.log("leaderdboard", leaderboard.value);
    }
  }
  catch (error) {
    console.log(error);
  }
}

async function getUsers() {
  try {
    const response = await fetch(
      `http://localhost:8080/search-user/${friendName.value}`,
      {
        method: "GET",
        headers: { "Content-Type": "application/json",
        "Authorization": `Bearer ${token.value}`
        },
      }
    );
    if (response.ok) {
      const data = await response.json();
      friendList.value = [data]; //aici va trebuie sa vad cum sa fac sa returnez daca caut pop sa returneze tot cei care incep cu pop
    }
  } catch (e) {
    console.error(e);
  }
}

async function sendPost() {
  try {
    const postPayload = {
      username: user.value,
      content: postContent.value,
    };

    const response = await fetch(`http://localhost:8080/add-post`, {
      method: "POST",
      headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token.value}`
        },

      body: JSON.stringify(postPayload),
    });

    if (response.ok) {
      postContent.value = "";
      await getPosts();
    }
  } catch (e) {
    console.error(e);
  }
}

async function getPosts() {
  try {
    const response = await fetch(
      `http://localhost:8080/get-posts`,{
        method: "GET",
          headers: {
          "Content-Type": "application/json",
            "Authorization": `Bearer ${token.value}`
          }
        }
    );
    if (response.ok) {
      posts.value = await response.json();
    }
  } catch (e) {
    console.error(e);
  }
}

onMounted(() => {
  getPosts();
  getLeaderboard();
});
</script>

<template>
  <Sidebar />
  <main class="flex-1 p-4 sm:ml-64 flex gap-6 bg-gray-100 min-h-screen">
    <div class="flex flex-col gap-2 p-2 bg-white w-full">
      <div
        class="flex flex-col lg:flex-row p-4 gap-6 bg-white w-full items-center lg:items-end justify-between border border-green-500 rounded-xl shadow-sm"
      >
        <div
          class="flex gap-2 sm:gap-4 items-end justify-center p-2 w-full lg:w-auto"
        >
          <div class="flex flex-col items-center">
            <div
              class="bg-gray-300 w-16 sm:w-20 h-20 sm:h-24 flex items-center justify-center font-bold text-white rounded-t-lg hover:-translate-y-2 hover:shadow-2xl hover:shadow-gray-500"
            >
              2
            </div>
            <span class="text-sm font-medium mt-1">Argint</span>
          </div>

          <div class="flex flex-col items-center">
            <div
              class="bg-yellow-400 w-16 sm:w-20 h-28 sm:h-32 flex items-center justify-center font-bold text-white rounded-t-lg shadow-md hover:-translate-y-2 hover:shadow-2xl  hover:shadow-amber-400"
            >
              1
            </div>
            <span class="text-sm font-bold mt-1 uppercase text-yellow-600"
              >Aur</span
            >
          </div>

          <div class="flex flex-col items-center">
            <div
              class="bg-orange-400 w-16 sm:w-20 h-16 sm:h-20 flex items-center justify-center font-bold text-white rounded-t-lg hover:-translate-y-2 hover:shadow-2xl  hover:shadow-amber-700"
            >
              3
            </div>
            <span class="text-sm font-medium mt-1">Bronz</span>
          </div>
        </div>
        <div
          class="flex flex-col gap-4 p-4 bg-gray-50 border border-gray-200 rounded-2xl w-full lg:max-w-xs shadow-sm transition-all hover:-translate-y-2 hover:shadow-xl"
        >
          <div class="text-center font-semibold text-gray-700">
            🏆 Eu vs PopescuMaria
          </div>
          <div class="flex justify-center gap-3">
            <UButton variant="outline" class="flex-1">Share</UButton>
            <UButton class="flex-1">Message</UButton>
          </div>
        </div>
        <div class="flex flex-col gap-2 w-full lg:w-64 lg:order-3">
          <i>Cauta prieteni</i>
          <UInput
            icon="i-lucide-search"
            size="md"
            @keyup.enter="getUsers"
            v-model="friendName"
            variant="outline"
            placeholder="Search..."
          />
          <div v-if="friendList">
            <!--informatii despre userii gasiti sa le pot da follow-->
            <ul>
              <li v-for="f in friendList" :key="f.id" class="text-center">
                <div class="flex justify-between">
                  <ULink :to="`/${f.username}`">
                    {{ f.username }}
                  </ULink>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div>
        <div class="mb-6 flex flex-col relative">
          <h1 class="font-bold text-2xl p-3 text-center">My Feed</h1>
          <div class="absolute top-2 right-0">
            <UModal>
              <UButton
                label="Adauga Postare"
                color="green"
                variant="subtle"
                icon="i-lucide-plus"
              />
              <template #content>
                <div class="p-4">
                  <h3 class="mb-4 font-bold">Creează o postare nouă</h3>
                  <UTextarea
                    v-model="postContent"
                    placeholder="Ce ai reușit astăzi?"
                  />
                  <UButton class="mt-4 w-full" @click="sendPost"
                    >Postează</UButton
                  >
                </div>
              </template>
            </UModal>
          </div>
        </div>
        <div
          class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 justify-items-center mx-auto px-4"
        >
          <friendCard
            v-for="p in posts"
            :key="p.id"
            :username="p.username"
            :content="p.content"
            :urlImage="p.urlImage"
            :createDate="p.publishDate"
          />
          <div
            v-if="posts.length === 0"
            class="col-span-full py-10 text-gray-400 italic"
          >
            Nu sunt postari de afisat pentru ziua de azi.
          </div>
        </div>
      </div>
    </div>
  </main>
</template>
