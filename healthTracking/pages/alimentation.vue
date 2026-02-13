<script setup>
import { ref } from "vue";
import Sidebar from "~/components/Sidebar.vue";
import {
  DateFormatter,
  getLocalTimeZone,
  today,
} from "@internationalized/date";
import { useDateWeek } from "~/composable/useDateWeek";
import { useAuth } from "~/composable/useAuth";
import { useGoals } from "~/composable/useGoals";
import AlimentationCard from "~/components/alimentationCard.vue";

const { user } = useAuth();

const { dataGoals, getGoals } = useGoals();

const { getLastWeekDates } = useDateWeek();

const df = new DateFormatter("en-US", {
  dateStyle: "medium",
});

const icons = {
  calories: `<svg class="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.122 17.645a7.185 7.185 0 0 1-2.656 2.495 7.06 7.06 0 0 1-3.52.853 6.617 6.617 0 0 1-3.306-.718 6.73 6.73 0 0 1-2.54-2.266c-2.672-4.57.287-8.846.887-9.668A4.448 4.448 0 0 0 8.07 6.31 4.49 4.49 0 0 0 7.997 4c1.284.965 6.43 3.258 5.525 10.631 1.496-1.136 2.7-3.046 2.846-6.216 1.43 1.061 3.985 5.462 1.754 9.23Z"/>
</svg>
`,
  proteins: `<svg class="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.011 13H20c-.367 2.5551-2.32 4.6825-4.9766 5.6162V20H8.97661v-1.3838C6.31996 17.6825 4.36697 15.5551 4 13h14.011Zm0 0c1.0995-.0059 1.989-.8991 1.989-2 0-.8637-.5475-1.59948-1.3143-1.87934M18.011 13H18m0-3.99997c.2409 0 .4718.04258.6857.12063m0 0c.8367-1.0335.7533-2.67022-.2802-3.50694-1.0335-.83672-2.5496-.6772-3.3864.35631-.293-1.50236-1.7485-2.15377-3.2509-1.8607-1.5023.29308-2.48263 1.74856-2.18956 3.25092C8.9805 6.17263 7.6182 5.26418 6.15462 6.00131 4.967 6.59945 4.45094 8.19239 5.04909 9.38002m0 0C4.37083 9.66467 4 10.3357 4 11.1174 4 12.1571 4.84288 13 5.88263 13m-.83354-3.61998c.2866-.12029 1.09613-.40074 2.04494.3418m5.27497-.89091c1.0047-.4589 2.1913-.01641 2.6502.98832"/>
</svg>
`,
  fat: `<svg class="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m11.5 8.00003 1-1M11.5 12.5l1-1m-1 5.5 1-1M9 20V4.00003h-.5c-1.933 0-3.5 1.567-3.5 3.5V16.5C5 18.433 6.567 20 8.5 20H9Zm6 0V4.00003h.5c1.933 0 3.5 1.567 3.5 3.5V16.5c0 1.933-1.567 3.5-3.5 3.5H15Zm0-2V6.00003c0-1.65685-1.3431-3-3-3s-3 1.34315-3 3V18c0 1.6569 1.3431 3 3 3s3-1.3431 3-3Z"/>
</svg>
`,
  carbos: `<svg class="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 19v-9c-1.5 0-2-1.5-2-2.49997 0-.99997.5-2 2-2.50003 2.36416-.64329 4.2009-.97557 6-.98904 1.7991.01347 3.6358.34575 6 .98904 1.5.50003 2 1.50006 2 2.50003C20 8.5 19.5 10 18 10v9c0 .5523-.4477 1-1 1H7c-.55228 0-1-.4477-1-1Z"/>
</svg>
`,
};

const modelValueDate = ref(today(getLocalTimeZone()));

const receivedData = ref([]);

const allProteins = ref(0);
const allFat = ref(0);
const allCarbos = ref(0);
const allCalories = ref(0);
const breakfast = ref([]);
const lunch = ref([]);
const dinner = ref([]);

async function fetchAlimentationByDate() {
  const selectedDate = modelValueDate.value.toString();
  const username = user.value;
  const response = await fetch(
    `http://localhost:8080/alimentation-data/${username}/${selectedDate}`,
    {
      method: "GET",
    }
  );
  if (response.ok) {
    receivedData.value = await response.json();
    allCalories.value = 0;
    allFat.value = 0;
    allCarbos.value = 0;
    allProteins.value = 0;

    breakfast.value = [];
    lunch.value = [];
    dinner.value = [];

    receivedData.value.forEach((item) => {
      allCalories.value += Number(item.calories) || 0;
      allFat.value += parseFloat(item.fat) || 0;
      allCarbos.value += parseFloat(item.carbohydrates) || 0;
      allProteins.value += Number(item.proteins) || 0;

      if (item.type === "Mic Dejun") {
        breakfast.value.push(item.nameProduct);
      } else if (item.type === "Pranz") {
        lunch.value.push(item.nameProduct);
      } else if (item.type === "Cina") {
        dinner.value.push(item.nameProduct);
      }
    });

    console.log("date alimentare", receivedData);
  }
}

watch([modelValueDate, user], () => {
  if (user.value) {
    fetchAlimentationByDate();
  }
});

const goals = ref(null);

onMounted(async () => {
  await getGoals();
  goals.value = dataGoals.value;
  console.log("Obiective încărcate:", dataGoals.value);
});

const caloriesGoal = computed(() => {
  if (!goals.value) return 0;

  const calorieGoal = goals.value.find((g) => g.type === "Alimentation");

  return calorieGoal ? calorieGoals.targetValue : 0;
});

const progressPercent = computed(() => {
  if (caloriesGoal.value === 0) return 0;
  const percent = (allCalories.value / caloriesGoal.value) * 100;
  return Math.min(percent, 100);
});
</script>

<template>
  <div class="min-h-screen flex">
    <Sidebar />

    <main class="flex-1 p-4 sm:ml-64 flex gap-4">
      <div class="flex-1 flex flex-col gap-4">
        <div
          class="flex items-center justify-between p-4 bg-white rounded-xl shadow-sm border border-gray-100"
        >
          <div class="flex gap-4 items-center">
            <UPopover>
              <UButton color="gray" variant="solid" icon="i-lucide-calendar">
                {{
                  modelValueDate
                    ? df.format(modelValueDate.toDate(getLocalTimeZone()))
                    : "Selectează o dată"
                }}
              </UButton>
              <template #content>
                <UCalendar v-model="modelValueDate" class="p-2" />
              </template>
            </UPopover>
            <p class="text-gray-500 text-sm hidden sm:block">
              Vizualizezi activitatea pentru data selectată.
            </p>
          </div>

          <div class="flex gap-3 items-center">
            <p class="text-sm font-semibold text-gray-700 hidden sm:block">
              Înregistrează o activitate:
            </p>
            <AddModal
              type="alimentation"
              :user="user"
              :date="modelValueDate.toString()"
            />
          </div>
        </div>

        <div class="grid grid-cols-4 p-3 gap-2">
          <div>
            <AlimentationCard
              title="Calories"
              :icon="icons.calories"
              :content="allCalories.toString()"
            />
          </div>
          <div>
            <AlimentationCard
              title="Proteins"
              :icon="icons.proteins"
              :content="allProteins.toString() + 'g'"
            />
          </div>
          <div>
            <AlimentationCard
              title="Fat"
              :icon="icons.fat"
              :content="allFat.toFixed(1) + 'g'"
            />
          </div>
          <div>
            <AlimentationCard
              title="Carbos"
              :icon="icons.carbos"
              :content="allCarbos.toFixed(1) + 'g'"
            />
          </div>
        </div>
        <div class="bg-gray-100 border-lg rounded-2xl p-4">
          Goal: {{ caloriesGoal }} kcal
          <UProgress :value="progressPercent" status />
        </div>
        <div>
          <h1 class="p-4 font-bold text-2xl text-center">Mesele tale</h1>
        </div>
        <div class="bg-green-200 p-4 flex justify-between items-center">
          <div>
            <h1 class="font-bold font-2xl p-3">Mic dejun</h1>
            <img src="/micdejun.jpeg" class="h-20 rounded-2xl" />
          </div>
          <div class="flex flex-col">
            <div>Recomandat: 400-600 kcal</div>
            <div v-for="b in breakfast">{{ b }}</div>
          </div>
          <div>
            <AddModal type="alimentation" :user="user" :date="modelValueDate" />
          </div>
        </div>
        <div class="bg-red-200 p-4 flex justify-between items-center">
          <div>
            <h1 class="font-bold font-2xl p-3">Pranz</h1>
            <img src="/pranz.jpg" class="h-20 rounded-2xl" />
          </div>
          <div class="flex flex-col">
            <div>Recomandat: 600-800 kcal</div>
            <div v-for="l in lunch">{{ l }}</div>
          </div>
          <div>
            <AddModal type="alimentation" :user="user" :date="modelValueDate" />
          </div>
        </div>
        <div class="bg-blue-200 p-4 flex justify-between items-center">
          <div>
            <h1 class="font-bold font-2xl p-3">Cina</h1>
            <img src="/cina.jpeg" class="h-20 rounded-2xl" />
          </div>
          <div class="flex flex-col">
            <div>Recomandat: 300-500 kcal</div>
            <div v-for="d in dinner">{{ d }}</div>
          </div>
          <div>
            <AddModal type="alimentation" :user="user" :date="modelValueDate" />
          </div>
        </div>
      </div>

      <div
        class="hidden md:flex flex-col w-[350px] bg-gray-200 p-4 rounded-lg sticky top-0 h-screen"
      >
        <h2 class="text-white font-bold mb-2">Asistent AI</h2>
        <div class="h-full bg-white rounded-lg">
          <Chatbot />
        </div>
      </div>

      <!-- Mobile bubble AI -->
      <div class="fixed bottom-4 right-4 md:hidden">
        <AddModal type="AI" :user="user" :date="modelValueDate" />
      </div>
    </main>
  </div>
</template>
