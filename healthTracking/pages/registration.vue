<script setup>
import { ref, reactive } from "vue";

const router = useRouter();
const message = ref("");
const state = reactive({
  username: undefined,
  email: undefined,
  password: undefined,
  height: undefined,
  weight: undefined,
  birthDate: undefined,
});

async function registerUser() {
  try {
    const response = await fetch("http://localhost:8080/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(state),
    });

    if (response.ok) {
      message.value = "Te-ai inregistrat cu success!";
      router.push("/dashboard");
    } else {
      message.value = "Ceva nu a functionat corect, mai incerca!";
    }
  } catch (error) {
    console.error(error);
  }
}
</script>

<template>
  <div
    class="min-h-screen flex justify-center items-center bg-green-50 dark:bg-gray-900"
  >
    <div
      class="bg-white dark:bg-gray-800 shadow-xl rounded-2xl p-10 w-full max-w-md"
    >
      <h1
        class="text-3xl font-bold text-center text-gray-800 dark:text-white mb-2"
      >
        Înregistrare
      </h1>
      <p class="text-center text-gray-500 dark:text-gray-400 mb-8 text-sm">
        Creează-ți un cont pentru a-ți urmări sănătatea.
      </p>

      <form class="space-y-5" @submit.prevent="registerUser" method="POST">
        <div>
          <label
            for="username"
            class="block mb-1 text-sm font-medium text-gray-700 dark:text-gray-300"
            >Username</label
          >
          <input
            type="text"
            id="username"
            v-model="state.username"
            placeholder="Ex: alexfit23"
            class="w-full border border-gray-300 dark:border-gray-600 rounded-lg p-2.5 focus:ring-green-500 focus:border-green-500 dark:bg-gray-700 dark:text-white"
            required
          />
        </div>

        <div>
          <label
            for="email"
            class="block mb-1 text-sm font-medium text-gray-700 dark:text-gray-300"
            >Email</label
          >
          <input
            type="email"
            id="email"
            v-model="state.email"
            placeholder="name@example.com"
            class="w-full border border-gray-300 dark:border-gray-600 rounded-lg p-2.5 focus:ring-green-500 focus:border-green-500 dark:bg-gray-700 dark:text-white"
            required
          />
        </div>

        <div>
          <label
            for="password"
            class="block mb-1 text-sm font-medium text-gray-700 dark:text-gray-300"
            >Parolă</label
          >
          <input
            type="password"
            id="password"
            v-model="state.password"
            placeholder="********"
            class="w-full border border-gray-300 dark:border-gray-600 rounded-lg p-2.5 focus:ring-green-500 focus:border-green-500 dark:bg-gray-700 dark:text-white"
            required
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label
              for="height"
              class="block mb-1 text-sm font-medium text-gray-700 dark:text-gray-300"
              >Înălțime (cm)</label
            >
            <input
              type="number"
              id="height"
              v-model="state.height"
              placeholder="175"
              class="w-full border border-gray-300 dark:border-gray-600 rounded-lg p-2.5 focus:ring-green-500 focus:border-green-500 dark:bg-gray-700 dark:text-white"
              required
            />
          </div>
          <div>
            <label
              for="weight"
              class="block mb-1 text-sm font-medium text-gray-700 dark:text-gray-300"
              >Greutate (kg)</label
            >
            <input
              type="number"
              id="weight"
              v-model="state.weight"
              placeholder="70"
              class="w-full border border-gray-300 dark:border-gray-600 rounded-lg p-2.5 focus:ring-green-500 focus:border-green-500 dark:bg-gray-700 dark:text-white"
              required
            />
          </div>
        </div>
        <div>
          <label
            for="birthdate"
            class="block mb-1 text-sm font-medium text-gray-700 dark:text-gray-300"
            >Data nașterii</label
          >
          <input
            type="date"
            id="birthdate"
            v-model="state.birthDate"
            class="w-full border border-gray-300 dark:border-gray-600 rounded-lg p-2.5 focus:ring-green-500 focus:border-green-500 dark:bg-gray-700 dark:text-white"
            required
          />
        </div>

        <!-- Buton -->
        <button
          type="submit"
          class="w-full bg-green-500 hover:bg-green-600 text-white font-medium py-2.5 rounded-lg transition"
        >
          Creează cont
        </button>

        <!-- Link spre login -->
        <p class="text-center text-sm mt-4 text-gray-600 dark:text-gray-400">
          Ai deja un cont?
          <NuxtLink to="/login" class="text-green-500 hover:underline">
            Autentifică-te
          </NuxtLink>
        </p>
      </form>
    </div>
  </div>
</template>
