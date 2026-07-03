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

const emit = defineEmits(["success"]);

async function fetchSearch() {
  if (!form.value.name) return;

  isSearching.value = true;
  productsFound.value = [];

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
  selectedProduct.value = {
    name: p.product_name_ro || p.product_name || 'Produs fără nume',
    brands: p.brands || 'Brand necunoscut'
  };

  form.value.name = p.product_name_ro || p.product_name || 'Produs fără nume';

  if (p.nutriments) {
    form.value.caloriesMeal = Math.round(p.nutriments['energy-kcal_100g'] || p.nutriments['energy-kcal'] || 0);
    form.value.proteins = p.nutriments.proteins_100g || p.nutriments.proteins || 0;
    form.value.carbohydrates = p.nutriments.carbohydrates_100g || p.nutriments.carbohydrates || 0;
    form.value.fat = p.nutriments.fat_100g || p.nutriments.fat || 0;
  }

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
      nameProduct: form.value.name,
    };
  }

  try {
    const res = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        'Authorization': `Bearer ${token.value}`
      },
      body: JSON.stringify(body),
    });

    if (res.ok) {
      alert("Datele au fost adăugate cu succes!");
      emit("success");
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
      };
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
      ui="{ width: 'sm:max-w-md' }"
  >
    <UButton label="+" color="neutral" variant="subtle" class="shadow-sm" />

    <template #body>
      <form
          v-if="props.type === 'Adăugare activitate'"
          @submit.prevent="handleSubmit"
          class="space-y-5 p-1"
      >
        <div class="flex flex-col gap-1.5">
          <label class="text-xs font-semibold text-gray-600 tracking-wide uppercase">Calorii consumate</label>
          <input
              type="number"
              v-model="form.calories"
              placeholder="Ex: 350 kcal"
              class="w-full bg-gray-50 border border-gray-200 focus:border-emerald-500 focus:bg-white focus:ring-2 focus:ring-emerald-100 transition-all rounded-xl px-3.5 py-2.5 text-sm text-gray-800 placeholder-gray-400 outline-none"
          />
        </div>
        <div class="flex flex-col gap-1.5">
          <label class="text-xs font-semibold text-gray-600 tracking-wide uppercase">Durată activitate</label>
          <input
              type="text"
              v-model="form.duration"
              min="0"
              placeholder="Ex: 45 min"
              class="w-full bg-gray-50 border border-gray-200 focus:border-emerald-500 focus:bg-white focus:ring-2 focus:ring-emerald-100 transition-all rounded-xl px-3.5 py-2.5 text-sm text-gray-800 placeholder-gray-400 outline-none"
          />
        </div>
        <div class="flex flex-col gap-1.5">
          <label class="text-xs font-semibold text-gray-600 tracking-wide uppercase">Tip exercițiu</label>
          <select
              v-model="form.exerciseType"
              class="w-full bg-gray-50 border border-gray-200 focus:border-emerald-500 focus:bg-white focus:ring-2 focus:ring-emerald-100 transition-all rounded-xl px-3.5 py-2.5 text-sm text-gray-800 outline-none appearance-none cursor-pointer"
          >
            <option disabled value="">Alege tipul de activitate</option>
            <option
                v-for="option in exerciseOptions"
                :key="option"
                :value="option"
            >
              {{ option }}
            </option>
          </select>
        </div>
        <button type="submit" class="w-full bg-emerald-500 hover:bg-emerald-600 active:scale-[0.99] text-white font-medium text-sm px-4 py-3 rounded-xl shadow-sm shadow-emerald-100 transition-all mt-2">
          Salvează activitatea
        </button>
      </form>

      <form
          v-else-if="props.type === 'Adăugare sesiune de somn'"
          @submit.prevent="handleSubmit"
          class="space-y-5 p-1"
      >
        <div class="grid grid-cols-2 gap-4">
          <div class="flex flex-col gap-1.5">
            <label class="text-xs font-semibold text-gray-600 tracking-wide uppercase">Calitate (1-5)</label>
            <input
                type="number"
                min="1"
                max="5"
                v-model="form.quality"
                placeholder="1 (slab) - 5 (excelent)"
                class="w-full bg-gray-50 border border-gray-200 focus:border-emerald-500 focus:bg-white focus:ring-2 focus:ring-emerald-100 transition-all rounded-xl px-3.5 py-2.5 text-sm text-gray-800 placeholder-gray-400 outline-none"
            />
          </div>
          <div class="flex flex-col gap-1.5">
            <label class="text-xs font-semibold text-gray-600 tracking-wide uppercase">Ore dormite</label>
            <input
                type="number"
                placeholder="Ex: 7.5 ore"
                v-model="form.hoursSlept"
                class="w-full bg-gray-50 border border-gray-200 focus:border-emerald-500 focus:bg-white focus:ring-2 focus:ring-emerald-100 transition-all rounded-xl px-3.5 py-2.5 text-sm text-gray-800 placeholder-gray-400 outline-none"
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="flex flex-col gap-1.5">
            <label class="text-xs font-semibold text-gray-600 tracking-wide uppercase">Energie dimineața</label>
            <input
                type="number"
                placeholder="Nivel 0 - 5"
                v-model="form.morningEnergy"
                max="5"
                min="0"
                class="w-full bg-gray-50 border border-gray-200 focus:border-emerald-500 focus:bg-white focus:ring-2 focus:ring-emerald-100 transition-all rounded-xl px-3.5 py-2.5 text-sm text-gray-800 placeholder-gray-400 outline-none"
            />
          </div>
          <div class="flex flex-col gap-1.5">
            <label class="text-xs font-semibold text-gray-600 tracking-wide uppercase">Nivel stres</label>
            <input
                type="number"
                placeholder="Nivel 0 - 5"
                v-model="form.stress"
                max="5"
                min="0"
                class="w-full bg-gray-50 border border-gray-200 focus:border-emerald-500 focus:bg-white focus:ring-2 focus:ring-emerald-100 transition-all rounded-xl px-3.5 py-2.5 text-sm text-gray-800 placeholder-gray-400 outline-none"
            />
          </div>
        </div>

        <button type="submit" class="w-full bg-emerald-500 hover:bg-emerald-600 active:scale-[0.99] text-white font-medium text-sm px-4 py-3 rounded-xl shadow-sm shadow-emerald-100 transition-all mt-2">
          Înregistrează somnul
        </button>
      </form>

      <form
          v-else-if="props.type === 'Adăugare produs'"
          @submit.prevent="handleSubmit"
          class="space-y-5 p-1"
      >
        <div class="flex flex-col gap-1.5">
          <label class="text-xs font-semibold text-gray-600 tracking-wide uppercase">Tip masă</label>
          <USelectMenu
              v-model="form.type"
              :items="items"
              class="w-full shadow-2xs"
              ui="{ rounded: 'rounded-xl', padding: 'py-2.5 px-3.5' }"
          />
        </div>

        <div class="flex flex-col gap-1.5">
          <label class="text-xs font-semibold text-gray-600 tracking-wide uppercase flex items-center justify-between">
            Caută aliment în baza de date
            <span v-if="isSearching" class="text-[10px] text-emerald-600 font-medium lowercase animate-pulse">Căutare activă...</span>
          </label>
          <div class="relative">
            <input
                type="text"
                v-model="form.name"
                @blur="fetchSearch"
                placeholder="Ex: Lapte Pilos, Banane..."
                class="w-full bg-gray-50 border border-gray-200 focus:border-emerald-500 focus:bg-white focus:ring-2 focus:ring-emerald-100 transition-all rounded-xl px-3.5 py-2.5 text-sm text-gray-800 placeholder-gray-400 outline-none"
            />
          </div>

          <div
              v-if="productsFound.length > 0"
              class="mt-1 border border-gray-100 bg-white rounded-xl max-h-56 overflow-y-auto shadow-xl z-50 divide-y divide-gray-50"
          >
            <div class="p-2 bg-gray-50/50 text-[10px] font-bold text-gray-400 uppercase tracking-wider sticky top-0 backdrop-blur-xs">Rezultate găsite:</div>
            <div
                v-for="p in productsFound"
                :key="p.code"
                @mousedown="selectProduct(p)"
                class="text-xs p-3 hover:bg-emerald-50 text-gray-700 hover:text-emerald-900 cursor-pointer transition-colors flex flex-col gap-0.5"
            >
              <span class="font-medium">{{ p.product_name_ro || p.product_name || 'Produs fără nume' }}</span>
              <span class="text-[10px] text-gray-400">{{ p.brands || 'Brand nesunoscut' }}</span>
            </div>
          </div>
        </div>

        <div v-if="selectedProduct && selectedProduct.name" class="p-3.5 bg-emerald-50/60 border border-emerald-100 rounded-xl text-xs text-emerald-800 flex flex-col gap-1 shadow-2xs">
          <div class="flex items-center gap-1.5">
            <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
            <span>Aliment selectat: <strong class="font-semibold text-emerald-900">{{ selectedProduct.name }}</strong></span>
          </div>
          <div class="text-[10px] text-emerald-600/90 pl-3">
            Valori nutriționale estimate la 100g extrapolate în formular.
          </div>
        </div>

        <button type="submit" class="w-full bg-emerald-500 hover:bg-emerald-600 active:scale-[0.99] text-white font-medium text-sm px-4 py-3 rounded-xl shadow-sm shadow-emerald-100 transition-all mt-2">
          Adaugă în jurnalul de astăzi
        </button>
      </form>

      <div v-else-if="props.type === 'AI'" class="flex flex-col w-full bg-white h-[600px] p-1">
        <h2 class="font-bold text-base text-gray-800 border-b border-gray-100 pb-3 mb-4 flex items-center gap-2">
          <span class="w-2 h-2 rounded-full bg-indigo-500 animate-pulse"></span>
          Asistentul tău Personal WellSync
        </h2>

        <div class="flex-1 bg-gray-50 rounded-xl overflow-hidden relative border border-gray-100">
          <Chatbot />
        </div>
      </div>
    </template>
  </UModal>
</template>