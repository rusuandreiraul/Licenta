<script setup>
import { ref, onMounted } from "vue";

const props = defineProps({
  type: { type: String, required: true },
  user: { type: String, required: true, default: "" },
  date: { type: String, required: true },
  modalId: { type: String, default: "default-modal" },
});

const items = ref(["Mic dejun", "Pranz", "Cina"]);

const exerciseOptions = [
  "Alergare",
  "High Intensity Training",
  "Mers",
  "Bicicleta",
  "Inot",
  "Core Training",
];

const form = ref({
  calories: "",
  duration: "",
  exerciseType: "",
  quality: "",
  hoursSlept: "",
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
  if (!form.value.produs) return;

  isSearching.value = true;
  try {
    const response = await fetch(
      `https://world.openfoodfacts.org/cgi/search.pl?search_terms=${form.value.produs}&search_simple=1&action=process&json=1&page_size=10&page=1`,
      { headers: { "User-Agent": "HealthCare - Web - Version 0.1" } }
    );

    const data = await response.json();

    if (data.products && data.products.length > 0) {
      productsFound.value = data.products;
    } else {
      productsFound.value = [];
      alert("Nu am găsit niciun produs.");
    }
  } catch (err) {
    console.error("Eroare la fetch:", err);
  } finally {
    isSearching.value = false;
  }
}

function selectProduct(p) {
  console.log("produs selectat: ", p);
  selectedProduct.value = {
    kcal: p.nutriments["energy-kcal"] || 0,
    proteins: p.nutriments["proteins"] || 0,
    fat: p.nutriments["fat"] || 0,
    carbohydrates: p.nutriments["carbohydrates"],
    name: p.product_name,
  };
  console.log("produs selectat: ", selectedProduct.value);
  form.value.caloriesMeal = selectedProduct.value.kcal;
  form.value.fat = selectedProduct.value.fat;
  form.value.proteins = selectedProduct.value.proteins;
  form.value.carbohydrates = selectedProduct.value.carbohydrates;
  form.value.name = selectedProduct.value.name;

  productsFound.value = [];
}

async function handleSubmit() {
  let url = "";
  let body = {};

  if (props.type === "activity") {
    url = `http://localhost:8080/dashboard-activity/${props.user}/${props.date}`;
    body = {
      calories: form.value.calories,
      duration: form.value.duration,
      activityType: form.value.exerciseType,
    };
  } else if (props.type === "sleep") {
    url = `http://localhost:8080/dashboard-sleep/${props.user}/${props.date}`;
    body = {
      quality: form.value.quality,
      hoursSlept: form.value.hoursSlept,
    };
  } else if (props.type === "alimentation") {
    url = `http://localhost:8080/dashboard-alimentation/${props.user}/${props.date}`;
    body = {
      carbohydrates: form.value.carbohydrates,
      calories: form.value.caloriesMeal,
      type: form.value.type,
      fat: form.value.fat,
      proteins: form.value.proteins,
      productName: form.value.productName,
    };
  }

  try {
    const res = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    if (res.ok) {
      alert("Datele au fost adăugate cu succes!");
      closeModal();
      form.value = {}; // resetează form-ul
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
        v-if="props.type === 'activity'"
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
          <label>Durata</label>
          <input
            type="text"
            v-model="form.duration"
            placeholder="Durata (ex: 45 min)"
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
        @submit="handleSubmit"
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
          <label>Caută produs (scrie și apasă în afara câmpului)</label>
          <input
            type="text"
            v-model="form.produs"
            @blur="fetchSearch"
            placeholder="Ex: Lapte, Mar..."
            class="w-full border rounded px-3 py-2"
          />
          <div
            v-if="productsFound.length > 0"
            class="mt-2 p-2 border bg-gray-50"
          >
            <p>Alege una dintre urmatoarele variante:</p>
            <div
              v-for="p in productsFound"
              :key="p.code"
              @mousedown="selectProduct(p)"
              class="text-sm hover:bg-green-500 cursor-pointer rounded"
            >
              {{ p.product_name }} - {{ p.brands }}
            </div>
          </div>
        </div>
        <button type="submit" class="bg-green-500 text-white px-4 py-2 rounded">
          Adaugă
        </button>
      </form>
    </template>
  </UModal>
</template>
