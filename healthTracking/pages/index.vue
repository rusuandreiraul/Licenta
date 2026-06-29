<script setup>
import { ref, reactive } from "vue";
import { useAuth } from "~/composable/useAuth";

const { login } = useAuth();


useSeoMeta({
  title: 'Login - WellSync',
  description: 'Pagina de autentificare',
  ogTitle: 'Login - WellSync',
  ogDescription: 'Pagina de autentificare.',
});

const remember=ref(false);


const resetEmail = ref('');

const handleSendResetLink = async () => {
  if (!resetEmail.value) {
    alert('Te rog introdu un email valid.')
    return
  }

  try {

    const response = await $fetch(`http://localhost:8080/forgot-password?email=${resetEmail.value}`, {
      method: 'POST'
    })

    alert(response) // se va afisa mesajul din backend
    resetEmail.value = '' // Resetare câmp
  } catch (error) {
    console.error(error)
    alert('A apărut o eroare la trimiterea email-ului.')
  }
}

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
    login(data.username, data.token, remember.value);
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
          src="/wellSync.png"
          alt="Healthy food"
          class="object-contain w-full h-full"
        />
        <div
          class="absolute inset-0 bg-gradient-to-t from-green-400/60 via-green-300/40 to-transparent"
        ></div>
        <div
          class="absolute inset-0 shadow-[0_0_80px_20px_rgba(34,197,94,0.3)]"
        ></div>
      </div>

      <div class="flex flex-col justify-center px-10 py-12">
        <h1 class="text-3xl font-bold text-center text-gray-800 mb-2">Autentificare</h1>
        <p class="text-center text-gray-600 mb-8 text-sm">
          Conectează-te pentru a-ți urmări progresul zilnic.
        </p>

        <form class="space-y-5" method="post" @submit.prevent="loginUser">
          <div>
            <label
              for="email"
              class="block mb-1 text-sm font-medium text-gray-700"
              >Username</label
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
              class="w-full border border-gray-300 rounded-lg p-2.5 focus:ring-green-700 focus:border-green-800"
              required
            />
          </div>

          <div class="flex items-center justify-between text-sm">
            <label class="flex items-center space-x-2">
              <input type="checkbox" v-model="remember" class="w-4 h-4 accent-green-500"  />
              <span class="text-gray-600">Ține-mă minte</span>
            </label>
              <div>
                <UModal>
                  <a
                      class="text-green-700 hover:underline text-sm font-medium"
                  >
                    Ai uitat parola?
                  </a>
                  <template #content>
                  <div class="p-6 space-y-4">
                    <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
                      Recuperare Parolă
                    </h3>

                    <p class="text-sm text-gray-500">
                      Introdu adresa de email asociată contului tău. Ți se va trimite un link pentru resetarea parolei. Verifică și folderul de Spam.
                    </p>

                    <UInput
                        v-model="resetEmail"
                        type="email"
                        placeholder="nume@exemplu.com"
                        icon="i-heroicons-envelope"
                    />

                    <div class="flex justify-end space-x-2 pt-2">
                      <UButton
                          color="green"
                          @click="handleSendResetLink"
                      >
                        Trimite link
                      </UButton>
                    </div>
                  </div>
                  </template>
                </UModal>
              </div>
          </div>

          <button
            type="submit"
            class="w-full bg-green-700 hover:bg-green-800 text-white font-medium py-2.5 rounded-lg transition"
          >
            Autentificare
          </button>

          <div class="flex items-center my-4">
            <hr class="flex-grow border-gray-300" />
            <span class="px-3 text-gray-600 text-sm">SAU</span>
            <hr class="flex-grow border-gray-300" />
          </div>

          <p class="text-center text-sm mt-4 text-gray-600">
            Nu ai cont?
            <NuxtLink to="/registration" class="text-green-700 hover:underline">
              Înregistrează-te
            </NuxtLink>
          </p>
          <p v-if="mess" class="text-red-500">{{ mess }}</p>
        </form>
      </div>
    </div>
  </div>
</template>
