<script setup>
import { ref, onMounted } from "vue";
import {useAuth} from "~/composable/useAuth.js";
import Chatbot from "~/components/chatbot.vue";

const props = defineProps({
  type: { type: String, required: true },
  date: { type: String, required: true },
  modalId: { type: String, default: "default-modal" },
});

const {token}=useAuth();

const items = ref(["Mic dejun", "Pranz", "Cina"]);

const exerciseOptions = [
  // Cele inițiale corectate/păstrate
  "Alergare",
  "Antrenament de mare intensitate (HIIT)",
  "Mers pe jos",
  "Ciclism",
  "Înot",
  "Antrenament Core (Musculatură lombară și abdominală)",
  "Antrenament cu greutăți",
  "Aerobic",
  "Pilates",
  "Yoga",
  "Stretching",
  "Drumeție montană",
  "Mers pe bandă",
  "Patinaj (cu role / pe gheață)",
  "Schi / Snowboard",
  "Fotbal",
  "Baschet",
  "Volei",
  "Tenis de câmp",
  "Tenis de masă",
  "Box / Kickboxing",
  "Dans",
];

const form = ref({
  calories: "",
  duration: "",
  exerciseType: "",
  quality: "",
  hoursSlept: "",
  stress:0,
  morningEnergy: 0,
  type: "",
  caloriesMeal: 0,
  fat: "",
  proteins: 0,
  carbohydrates: "",
  name: "",
});

const productsFound = ref([]);
const isSearching = ref(false);

const selectedProduct = ref({
  kcal: 0,
  proteins: 0,
  fat: "",
  carbohydrates: "",
  name: "",
});

async function fetchSearch() {
  if (!form.value.name) return;

  isSearching.value = true;
  productsFound.value = []; // Resetăm lista înainte de căutare

  try {

    const response = await fetch(`http://localhost:8080/products/search?query=${encodeURIComponent(form.value.name)}`,
        {
          method: "GET",
          headers: {
            'Authorization': `Bearer ${token.value}`,
            'Content-Type': "application/json",

          }
        }
    );

    if (!response.ok) {
      throw new Error("Eroare la comunicarea cu serverul.");
    }

    const data = await response.json();

    if (data && data.products && data.products.length > 0) {
      productsFound.value = data.products;
    } else {
      productsFound.value = [];
      alert("Nu am găsit niciun produs.");
    }
  } catch (err) {
    console.error("Eroare la fetch:", err);
    alert("A apărut o eroare la căutarea produsului.");
  } finally {
    isSearching.value = false;
  }
}



function selectProduct(p) {
  // 1. Salvăm produsul selectat (folosind denumirea corectă din OpenFoodFacts)
  selectedProduct.value = {
    name: p.product_name_ro || p.product_name || 'Produs fără nume',
    brands: p.brands || 'Brand necunoscut'
  };

  // 2. Mapăm automat datele în obiectul tău 'form' ca să plece corect spre backend
  form.value.name = p.product_name_ro || p.product_name || 'Produs fără nume';

  // OpenFoodFacts trimite nutrienții în obiectul 'nutriments' la 100g
  if (p.nutriments) {
    form.value.caloriesMeal = Math.round(p.nutriments['energy-kcal_100g'] || p.nutriments['energy-kcal'] || 0);
    form.value.proteins = p.nutriments.proteins_100g || p.nutriments.proteins || 0;
    form.value.carbohydrates = p.nutriments.carbohydrates_100g || p.nutriments.carbohydrates || 0;
    form.value.fat = p.nutriments.fat_100g || p.nutriments.fat || 0;
  }

  // 3. Resetăm lista pentru a ascunde dropdown-ul de sugestii
  productsFound.value = [];
}

async function handleSubmit() {
  let url = "";
  let body = {};

  console.log("data din props", props.date);

  if (props.type === "Adăugare activitate") {
    url = `http://localhost:8080/dashboard-activity/${props.date}`;
    body = {
      calories: form.value.calories,
      duration: form.value.duration,
      activityType: form.value.exerciseType,
    };
  } else if (props.type === "sleep") {
    console.log("stress din form", form.value.stress);
    url = `http://localhost:8080/dashboard-sleep/${props.date}`;
    body = {
      quality: form.value.quality,
      hoursSlept: form.value.hoursSlept,
      morningEnergy: form.value.morningEnergy,
      stress: form.value.stress,
    };
    console.log("Corp cerere trimis direct la fetch:", JSON.stringify(body));
  } else if (props.type === "alimentation") {
    url = `http://localhost:8080/dashboard-alimentation/${props.date}`;
    body = {
      carbohydrates: form.value.carbohydrates,
      calories: form.value.caloriesMeal,
      type: form.value.type,
      fat: form.value.fat,
      proteins: form.value.proteins,
      productName: form.value.name,
    };
  }

  try {
    const res = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        'Authorization': `Bearer ${token.value}` //luam tokenul
      },
      body: JSON.stringify(body),
    });

    if (res.ok) {
      alert("Datele au fost adăugate cu succes!");
      form.value = {
        calories: "",
        duration: "",
        exerciseType: "",
        quality: "",
        hoursSlept: "",
        stress:0,
        morningEnergy: 0,
        type: "",
        caloriesMeal: 0,
        fat: "",
        proteins: 0,
        carbohydrates: "",
        name: "",
      }; // resetează form-ul
    } else {
      alert("Eroare la trimiterea datelor!");
    }
  } catch (err) {
    console.error(err);
  }
}
</script>

<template>
  <UModal
    :title="props.type"
    :close="{
      color: 'primary',
      variant: 'outline',
      class: 'rounded-full',
    }"
  >
    <!-- Buton de deschidere modal -->
    <UButton label="+" color="neutral" variant="subtle" />

    <!-- BODY FORM -->
    <template #body>
      <!-- Activity -->
      <form
        v-if="props.type === 'Adăugare activitate'"
        @submit.prevent="handleSubmit"
        class="space-y-4"
      >
        <div>
          <label>Calorii</label>
          <input
            type="number"
            v-model="form.calories"
            placeholder="Calorii"
            class="w-full border rounded px-3 py-2"
          />
        </div>
        <div>
          <label>Durată (min.)</label>
          <input
            type="text"
            v-model="form.duration"
            min="0"
            placeholder="Durată (ex: 45 min)"
            class="w-full border rounded px-3 py-2"
          />
        </div>
        <div>
          <label>Tip exercițiu</label>
          <select
            v-model="form.exerciseType"
            class="w-full border rounded px-3 py-2"
          >
            <option disabled value="">Selectează tipul</option>
            <option
              v-for="option in exerciseOptions"
              :key="option"
              :value="option"
            >
              {{ option }}
            </option>
          </select>
        </div>
        <button type="submit" class="bg-green-500 text-white px-4 py-2 rounded">
          Adaugă
        </button>
      </form>

      <!-- Sleep -->
      <form
        v-else-if="props.type === 'sleep'"
        @submit.prevent="handleSubmit"
        class="space-y-4"
      >
        <div>
          <label>Calitatea somnului</label>
          <input
            type="number"
            min="1"
            max="5"
            v-model="form.quality"
            placeholder="1-slab / 5-perfect"
            class="w-full border rounded px-3 py-2"
          />
        </div>
        <div>
          <label>Ora Dormite</label>
          <input
            type="number"
            placeholder="Ex: 8 ore, 6 ore..."
            v-model="form.hoursSlept"
            class="w-full border rounded px-3 py-2"
          />
        </div>
        <div>
          <label>Energie Dimineața</label>
          <input
              type="number"
              placeholder="Între 0 și 5"
              v-model="form.morningEnergy"
              max="5"
              min="0"
              class="w-full border rounded px-3 py-2"
          />
        </div>
        <div>
          <label>Stress</label>
          <input
              type="number"
              placeholder="Între 0 și 5"
              v-model="form.stress"
              max="5"
              min="0"
              class="w-full border rounded px-3 py-2"
          />
        </div>

        <button type="submit" class="bg-green-500 text-white px-4 py-2 rounded">
          Adaugă
        </button>
      </form>

      <!-- Alimentation -->
      <form
          v-else-if="props.type === 'alimentation'"
          @submit.prevent="handleSubmit"
          class="space-y-4"
      >
        <div>
          <label>Tip masă</label>
          <USelectMenu
              v-model="form.type"
              :items="items"
              class="w-full border rounded px-3 py-2"
          />
        </div>

        <div>
          <label>
            Caută produs
            <span v-if="isSearching" class="text-xs text-gray-400"> (Se caută...)</span>
          </label>
          <input
              type="text"
              v-model="form.name"
              @blur="fetchSearch"
              placeholder="Ex: Lapte, Mar..."
              class="w-full border rounded px-3 py-2"
          />

          <div
              v-if="productsFound.length > 0"
              class="mt-2 p-2 border bg-gray-50 max-h-60 overflow-y-auto"
          >
            <p class="font-semibold text-xs text-gray-500 mb-1">Alege varianta exactă din listă:</p>
            <div
                v-for="p in productsFound"
                :key="p.code"
                @mousedown="selectProduct(p)"
                class="text-sm p-1 hover:bg-green-500 hover:text-white cursor-pointer rounded transition-colors"
            >
              {{ p.product_name_ro || p.product_name || 'Produs fără nume' }} - {{ p.brands || 'Brand necunoscut' }}
            </div>
          </div>
        </div>

        <div v-if="selectedProduct" class="p-2 bg-green-50 border border-green-200 rounded text-sm text-green-800">
          Produs selectat: <strong>{{ selectedProduct.name }}</strong>
        </div>

        <button type="submit" class="bg-green-500 text-white px-4 py-2 rounded">
          Adaugă
        </button>
      </form>
      <div v-else-if="props.type === 'AI'" class="flex flex-col w-full bg-white h-[600px]">
        <h2 class="font-bold text-lg mb-4 text-gray-800 border-b pb-2">
          Asistentul tău Personal
        </h2>

        <div class="flex-1 bg-gray-50 rounded-lg overflow-hidden relative">
          <Chatbot />
        </div>
      </div>
    </template>
  </UModal>
</template>
