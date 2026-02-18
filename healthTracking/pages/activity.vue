<script setup>
import { ref, watch, onMounted } from "vue";
import AddModal from "~/components/addModal.vue";
import Sidebar from "~/components/Sidebar.vue";
import { useAuth } from "~/composable/useAuth";
import { useGoals } from "~/composable/useGoals";
import {
  CalendarDate,
  DateFormatter,
  getLocalTimeZone,
  today,
} from "@internationalized/date";
import Table from "~/components/table.vue";
import Chatbot from "~/components/chatbot.vue";

// --- STATE-URI (Datele) ---
const series = ref([
  {
    name: "Calorii", // Nume mai explicit pentru serie
    data: [0, 0, 0, 0, 0, 0, 0],
  },
]);

const modelValueDate = ref(today(getLocalTimeZone()));

const { user } = useAuth();

const {dataGoals,getGoals} =useGoals();

const activityData = ref([]);
const activityHeaders = ["Activitate", "Durată (min)", "Dată", "Calorii (kcal)"];

const activityDataAll = ref({
  calories: 0,
  duration: 0,
});

const progress=ref(null);

const df = new DateFormatter("en-US", {
  dateStyle: "medium",
});

// --- FUNCȚII DE CALCUL ---
function getLastWeekDates(selectedDate) {
  const jsDate = selectedDate.toDate(getLocalTimeZone());
  const days = [];

  for (let i = 6; i >= 0; i--) {
    const d = new Date(jsDate);
    d.setDate(jsDate.getDate() - i);
    const dayString =
      d.getFullYear() +
      "-" +
      String(d.getMonth() + 1).padStart(2, "0") +
      "-" +
      String(d.getDate()).padStart(2, "0");
    days.push(dayString);
  }
  return days;
}

// --- OPȚIUNI GRAFIC (Mutate aici pentru reacție) ---
const chartOptions = ref({
  chart: {
    type: "area",
    height: 350,
    zoom: { enabled: false },
    toolbar: { show: false },
  }, // Ascundem toolbar-ul
  dataLabels: { enabled: false },
  stroke: { curve: "smooth", width: 3 }, // Curba mai fină
  title: {
    text: "Evoluția Caloriilor",
    align: "left",
    style: { fontSize: "16px", fontWeight: "bold" },
  },
  subtitle: {
    text: "Monitorizează caloriile arse săptămânal",
    align: "left",
    style: { color: "#6B7280" },
  },
  labels: getLastWeekDates(modelValueDate.value), // ISO format
  xaxis: { type: "datetime" },
  yaxis: { opposite: false, title: { text: "Calorii (Kcal)" } }, // Mutăm Y-axis la stânga (mai intuitiv)
  legend: { horizontalAlign: "left" },
  colors: ["#EF4444"], // Culoare roșie specifică pentru calorii
});

// --- FUNCȚII ASINCRONE (Fetch) ---
async function fetchActivitiesByDate() {
  activityDataAll.value.calories = 0;
  activityDataAll.value.duration = 0;

  if (!user.value) return;

  const username = user.value;
  const selectedDate = modelValueDate.value.toString(); // Asigurăm formatul string

  try {
    const response = await fetch(
      `http://localhost:8080/dashboard-activity/${username}/${selectedDate}`,
      {
        method: "GET",
      }
    );

    if (response.ok) {
      activityData.value = await response.json();

      // Calculăm sumarul total
      let totalCalories = 0;
      let totalDuration = 0;

      if (activityData.value.length > 0) {
        for (let i = 0; i < activityData.value.length; i++) {
          totalCalories += activityData.value[i].calories || 0;
          totalDuration += activityData.value[i].duration || 0;
        }
      }
      activityDataAll.value.calories = totalCalories;
      activityDataAll.value.duration = totalDuration;
      progress.value=Math.min(100, (activityDataAll.value.duration/goals.value[0].targetValue)*100);
    } else {
      // Dacă răspunsul nu este OK (de ex., 404), resetăm datele
      activityData.value = [];
      activityDataAll.value.calories = 0;
      activityDataAll.value.duration = 0;
    }
  } catch (error) {
    console.error("Eroare la preluarea activităților:", error);
    activityData.value = [];
    activityDataAll.value.calories = 0;
    activityDataAll.value.duration = 0;
  }
}

async function fetchActivitySeries() {
  if (!user.value) return;

  try {
    const username = user.value;
    const selectedDate = modelValueDate.value.toString();

    const response = await fetch(
      `http://localhost:8080/chart-series/${username}/${selectedDate}`,
      { method: "GET" }
    );

    if (response.ok) {
      const data = await response.json();

      // 1️⃣ Obținem cele 7 zile din săptămână (ex: ["2025-10-05", ..., "2025-10-11"])
      const week = getLastWeekDates(modelValueDate.value).map(
        (d) => d.split("T")[0]
      );

      // 2️⃣ Inițializăm toate valorile cu 0
      const weekValues = Array(7).fill(0);

      // 3️⃣ Parcurgem datele primite din backend
      for (let i = 0; i < data.length; i++) {
        // Presupunând că data este deja în format 'YYYY-MM-DD'
        const day = data[i].date;
        const value = data[i].value;

        // 4️⃣ Vedem dacă ziua e în lista săptămânii
        const index = week.indexOf(day);
        if (index !== -1) {
          // dacă ziua e în săptămână, punem valoarea la poziția corectă
          weekValues[index] = value;
        }
      }

      // 5️⃣ Actualizăm graficul
      chartOptions.value = {
        ...chartOptions.value,
        labels: week,
      };
      series.value[0].data = weekValues;
    }
  } catch (e) {
    console.error("Eroare la preluarea seriei de date pentru grafic:", e);
  }
}

// --- HOOKS și WATCHERS ---
watch([modelValueDate, user], () => {
  if (user.value) {
    fetchActivitiesByDate();
    fetchActivitySeries();
  }
});

const goals=ref(null);

onMounted(async () => {
  await getGoals();
  goals.value=dataGoals.value;
  if (user.value) {
    fetchActivitiesByDate();
    fetchActivitySeries();
  }
});
</script>

<template>
  <div class="min-h-screen flex bg-gray-50">
    <Sidebar />

    <main class="flex-1 p-4 sm:ml-64 flex gap-6">
      <div class="flex-1 flex flex-col gap-6">
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
            <AddModal type="activity" :user="user" :date="modelValueDate" />
          </div>
        </div>
        <div class="grid grid-cols-2 justify-center items-center bg-white rounded-xl shadow-sm border border-gray-100"">
          <div class="flex-1 flex-col p-3">
            <svg class="w-6 h-6 text-red-600 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.122 17.645a7.185 7.185 0 0 1-2.656 2.495 7.06 7.06 0 0 1-3.52.853 6.617 6.617 0 0 1-3.306-.718 6.73 6.73 0 0 1-2.54-2.266c-2.672-4.57.287-8.846.887-9.668A4.448 4.448 0 0 0 8.07 6.31 4.49 4.49 0 0 0 7.997 4c1.284.965 6.43 3.258 5.525 10.631 1.496-1.136 2.7-3.046 2.846-6.216 1.43 1.061 3.985 5.462 1.754 9.23Z"/>
</svg>

            <span class="font-bold">Calorii Arse:</span>
            {{ activityDataAll.calories }}
            <div v-if="activityDataAll.calories>300">
              <i class="text-green-500">Foarte bine, azi ai facut show!</i>
            </div>
            <div v-else>
              <i class="text-red-500">Continua tot asa si treci peste obiectiv</i>
            </div>
          </div>
          <div class="flex-1 flex-col p-3">
            <svg class="w-6 h-6 text-blue-600 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
</svg>

            <span class="font-bold">
              Durata totala:
            </span>
            {{ activityDataAll.duration }}
            <div class="mt-2">
              <i>Progres: {{ progress }} %</i>
              <UProgress v-model="progress" color="info"/>
            </div>

          </div>
          
        </div>
       
        <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
          <client-only>
            <apexchart
              width="100%"
              height="350"
              :options="chartOptions"
              :series="series"
            ></apexchart>
          </client-only>
        </div>
        <div>
          <Table  :headers="activityHeaders" :content="activityData"/>
        </div>
      </div>

      <div
        class="hidden lg:flex flex-col w-[350px] bg-white p-4 rounded-xl shadow-md sticky self-start top-4 h-[calc(100vh-32px)]"
      >
        <h2 class="font-bold text-lg mb-4 text-gray-800 border-b pb-2">
          🤖 Asistentul tău Personal
        </h2>
        <div class="h-full bg-gray-50 rounded-lg flex-1 overflow-y-auto">
          <Chatbot />
        </div>
      </div>

      <div class="fixed bottom-4 right-4 lg:hidden z-50">
        <AddModal type="AI" :user="user" :date="modelValueDate" />
      </div>
    </main>
  </div>
</template>
