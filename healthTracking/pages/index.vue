<script setup>
import { ref, reactive } from "vue";
import { useAuth } from "~/composable/useAuth";

const { login } = useAuth();

const router = useRouter();
const state = reactive({
  username: undefined,
  password: undefined,
});
const mess = ref("");

async function loginUser() {
  const response = await fetch("http://localhost:8080/login", {
    method: "POST",
    headers: {
      "Content-type": "application/json",
    },
    body: JSON.stringify(state),
  });
  if (response.ok) {
    const data = await response.json();
    login(data.username);
    router.push("/dashboard");
  } else {
    console.log("eroare", "eroare");
    mess.value = "Username sau parola gresita! Mai incerca!";
  }
}
</script>

<template>
  <div class="min-h-screen flex justify-center items-center bg-green-950">
    <div
      class="grid grid-cols-1 md:grid-cols-2 bg-white rounded-2xl overflow-hidden shadow-2xl max-w-4xl w-full"
    >
      <div class="relative">
        <img
          src="/loginImage.jpg"
          alt="Healthy food"
          class="object-cover w-full h-full"
        />
        <div
          class="absolute inset-0 bg-gradient-to-t from-green-400/60 via-green-300/40 to-transparent"
        ></div>
        <div
          class="absolute inset-0 shadow-[0_0_80px_20px_rgba(34,197,94,0.3)]"
        ></div>
      </div>

      <div class="flex flex-col justify-center px-10 py-12">
        <h1 class="text-3xl font-bold text-center text-gray-800 mb-2">Login</h1>
        <p class="text-center text-gray-500 mb-8 text-sm">
          Conectează-te pentru a-ți urmări progresul zilnic.
        </p>

        <form class="space-y-5" method="post" @submit.prevent="loginUser">
          <div>
            <label
              for="email"
              class="block mb-1 text-sm font-medium text-gray-700"
              >Username sau email</label
            >
            <input
              type="text"
              id="email"
              v-model="state.username"
              placeholder="name@example.com or username"
              class="w-full border border-gray-300 rounded-lg p-2.5 focus:ring-green-500 focus:border-green-500"
              required
            />
          </div>

          <div>
            <label
              for="password"
              class="block mb-1 text-sm font-medium text-gray-700"
              >Password</label
            >
            <input
              type="password"
              id="password"
              v-model="state.password"
              class="w-full border border-gray-300 rounded-lg p-2.5 focus:ring-green-500 focus:border-green-500"
              required
            />
          </div>

          <div class="flex items-center justify-between text-sm">
            <label class="flex items-center space-x-2">
              <input type="checkbox" class="w-4 h-4 accent-green-500" />
              <span class="text-gray-600">Remember me</span>
            </label>
            <a href="#" class="text-green-500 hover:underline"
              >Forgot password?</a
            >
          </div>

          <button
            type="submit"
            class="w-full bg-green-500 hover:bg-green-600 text-white font-medium py-2.5 rounded-lg transition"
          >
            Sign in
          </button>

          <div class="flex items-center my-4">
            <hr class="flex-grow border-gray-300" />
            <span class="px-3 text-gray-400 text-sm">OR</span>
            <hr class="flex-grow border-gray-300" />
          </div>

          <p class="text-center text-sm mt-4 text-gray-600">
            Nu ai cont?
            <NuxtLink to="/registration" class="text-green-500 hover:underline">
              Înregistrează-te
            </NuxtLink>
          </p>
          <p v-if="mess" class="text-red-500">{{ mess }}</p>
        </form>
      </div>
    </div>
  </div>
</template>
