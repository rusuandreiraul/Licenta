<script setup>
import { ref } from "vue";
const friendName = ref("");
const friendList = ref([]);

async function getUsers() {
  try {
    const response = await fetch(
      `http://localhost:8080/search-user/${friendName.value}`,
      {
        method: "GET",
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
</script>

<template>
  <Sidebar />
  <main class="flex-1 p-4 sm:ml-64 flex gap-6 bg-gray-100">
    <div class="flex flex-col gap-2 bg-white w-full">
      <div class="p-2 gap-2 flex flex-row justify-between items-start">
        <div class="flex gap-4 p-3 items-end">
          <div class="flex flex-col items-center">
            <div class="bg-gray-300 h-24 w-20">2</div>
            <span>Argint</span>
          </div>
          <div class="flex flex-col items-center">
            <div class="bg-yellow-400 h-32 w-20">1</div>
            <span>aur</span>
          </div>
          <div class="flex items-center flex-col">
            <div class="bg-orange-400 h-20 w-20">3</div>
            <span>bronz</span>
          </div>
        </div>
        <div class="flex flex-col gap-2">
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
              <li v-for="f in friendList" :key="f.id">
                <ULink :to="`/${f.username}`">
                  {{ f.username }}
                </ULink>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div>
        lista de prieteni cu care pot sa dau un high five, sa veririfc streakul
        si sa pot trimitr mesaje
      </div>
    </div>
  </main>
</template>
