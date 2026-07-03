<script setup>
import { ref, onMounted } from "vue";
import friendCard from "~/components/friendCard.vue";
import { useAuth } from "~/composable/useAuth";
import {useSocial} from "~/composable/useSocials.js";
const friendName = ref("");
const friendList = ref([]);

const {user, token } = useAuth();

const {triggerChallengeUpdate}=useSocial();

const posts = ref([]);

const postContent = ref("");

const leaderboard=ref({})

const usernameBestStreak=ref("");


const selectedAttachment = ref(null);

const groupedOptions = ref([]);

async function fetchTodayActivities() {
  try {
    const today = new Date().toISOString().split('T')[0];

    const response = await fetch(`http://localhost:8080/dashboard-daily/${today}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token.value}`,
      }
    });

    if (response.ok) {
      const data = await response.json();
      const options = [];

      if (data.activityDetails && data.activityDetails.length > 0) {
        options.push({
          groupLabel: '🏋️‍♂️ Activități Fizice',
          items: data.activityDetails.map(act => ({
            label: `${act.activityType} (${act.duration} min)`,
            type: 'ACTIVITY',
            id: act.id,
            text: `${act.activityType} - ${act.duration} min`
          }))
        });
      }


      if (data.sleepDetails && data.totalHoursSleep > 0) {
        options.push({
          groupLabel: '🌙 Somn',
          items: [{
            label: `Somn: ${data.totalHoursSleep} ore`,
            type: 'SLEEP',
            id: data.sleepDetails.id,
            text: `Somn: ${data.totalHoursSleep} ore`
          }]
        });
      }


      if (data.alimentationName && data.alimentationName.length > 0) {
        options.push({
          groupLabel: '🍏 Alimentație',
          items: data.alimentationName.map((foodName, index) => ({
            label: foodName,
            type: 'ALIMENTATION',
            id: index,
            text: `Consumat: ${foodName}`
          }))
        });
      }

      groupedOptions.value = options;
    }
  } catch (e) {
    console.error("Eroare la preluarea datelor din dashboard:", e);
  }
}



async function getBestStreak(){
  try{
    const response=await fetch(`http://localhost:8080/best-streak`,{
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token.value}`,
      }
    });

    if(response.ok){
      const data = await response.json();
      console.log("best streak", data);
      usernameBestStreak.value=data.receiver;
    }
  }
  catch(e){
    console.error(e);
  }
}

async function getLeaderboard(){
  try {

    const response = await fetch(`http://localhost:8080/leaderboard`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token.value}`,
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
      friendList.value = [data];
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
      console.log("posts: ", posts);
    }
  } catch (e) {
    console.error(e);
  }
}

async function sendPost() {

  if (!postContent.value.trim() && !selectedAttachment.value) return;

  try {
    let finalContent = postContent.value;


    if (selectedAttachment.value) {
      // \n\n adaugă spațiu între mesajul utilizatorului și activitate
      finalContent += `\n\n${selectedAttachment.value.text}`;
    }


    const postPayload = {
      username: user.value,
      content: finalContent // Trimitem textul gata combinat
    };

    console.log("Se trimite payload-ul combinat:", postPayload);

    const response = await fetch(`http://localhost:8080/add-post`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token.value}`
      },
      body: JSON.stringify(postPayload),
    });

    if (response.ok) {
      alert("Postarea a fost făcută cu succes");
      postContent.value = "";
      selectedAttachment.value = null;
      await getPosts();
    }
  } catch (e) {
    console.error(e);
  }
}
onMounted(() => {
  getPosts();
  getLeaderboard();
  getBestStreak();
  fetchTodayActivities();
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

            <img class="w-8 h-8 p-1 mb-1 rounded-full ring-2 ring-default" :src="leaderboard[1]?.urlProfileImage"  alt="Bordered avatar">
                {{leaderboard[1]?.username }}
            <div
              class="bg-gray-300 w-16 sm:w-20 h-20 sm:h-24 flex items-center justify-center font-bold text-white rounded-t-lg hover:-translate-y-2 hover:shadow-2xl hover:shadow-gray-500"
            >
              2
            </div>
            <span class="text-sm font-medium mt-1">Argint</span>
          </div>

          <div class="flex flex-col items-center">

            <img class="w-8 h-8 p-1 mb-1 rounded-full ring-2 ring-default" :src="leaderboard[0]?.urlProfileImage"  alt="Bordered avatar">
              {{leaderboard[0]?.username}}
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

            <img class="w-8 h-8 p-1 mb-1 rounded-full ring-2 ring-default" :src="leaderboard[2]?.urlProfileImage"  alt="Bordered avatar">
              {{leaderboard[2]?.username}}

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
            🏆 Eu vs {{usernameBestStreak}}
          </div>
          <div class="flex justify-center gap-3">
            <UButton variant="outline" class="flex-1" @click="triggerChallengeUpdate(usernameBestStreak.value)">Challenge</UButton>
            <USlideover title="Chat">
              <UButton label="Message" class="flex-1" color="success" />

              <template #body>
                <MessageChat :receiver="usernameBestStreak" />
              </template>
            </USlideover>
          </div>
        </div>
        <div class="flex flex-col gap-2 w-full lg:w-64 lg:order-3">
          <i>Caută prieteni</i>
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
                  label="Adaugă Postare"
                  color="green"
                  variant="subtle"
                  icon="i-lucide-plus"
                  class="font-semibold transition-all duration-200 hover:scale-105 active:scale-95 shadow-sm"
              />

              <template #content>
                <div class="p-6 bg-white dark:bg-gray-900 rounded-xl max-w-md w-full mx-auto">

                  <div class="flex items-center gap-2 mb-4 pb-3 border-b border-gray-100 dark:border-gray-800">
                    <div>
                      <h3 class="text-lg font-bold text-gray-900 dark:text-white leading-none">
                        Creează o postare nouă
                      </h3>
                      <p class="text-xs text-gray-400 dark:text-gray-500 mt-1">
                        Împărtășește progresul tău cu comunitatea
                      </p>
                    </div>
                  </div>

                  <div class="space-y-4">
                    <UTextarea
                        v-model="postContent"
                        placeholder="Ce ai reușit astăzi? Împărtășește o realizare, un obicei sau un nou record..."
                        :rows="4"
                        resize="none"
                        autofocus
                        class="w-full text-sm placeholder:text-gray-400 dark:placeholder:text-gray-500"
                        :ui="{
            color: { white: 'focus:ring-2 focus:ring-green-500 dark:focus:ring-green-500' },
            rounded: 'rounded-xl'
          }"
                    />

                    <div class="space-y-1.5 text-left w-full">
                      <label class="text-xs font-semibold text-gray-500 dark:text-gray-400 flex items-center gap-1.5">
                        <UIcon name="i-lucide-paperclip" class="w-3.5 h-3.5 text-green-500" />
                        Atașează un progres de astăzi (Opțional)
                      </label>

                      <select
                          v-model="selectedAttachment"
                          class="w-full bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-700 rounded-xl px-3 py-2 text-sm text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-green-500"
                      >
                        <option :value="null">Alege o activitate, somn sau aliment...</option>

                        <optgroup
                            v-for="group in groupedOptions"
                            :key="group.groupLabel"
                            :label="group.groupLabel"
                            class="font-semibold text-gray-500"
                        >
                          <option
                              v-for="item in group.items"
                              :key="item.label"
                              :value="item"
                              class="text-gray-900 dark:text-white font-normal"
                          >
                            {{ item.label }}
                          </option>
                        </optgroup>
                      </select>
                    </div>
                  </div>

                  <UButton
                      class="mt-5 w-full justify-center py-2.5 font-semibold text-sm transition-all shadow-md hover:shadow-green-500/20 active:translate-y-px rounded-xl"
                      color="green"
                      icon="i-lucide-send"
                      @click="sendPost"
                  >
                    Postează
                  </UButton>
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
