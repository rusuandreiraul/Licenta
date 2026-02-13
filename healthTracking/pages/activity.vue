<script setup>
import { ref, watch, onMounted } from "vue";
import AddModal from "~/components/addModal.vue";
import Sidebar from "~/components/Sidebar.vue";
import { useAuth } from "~/composable/useAuth";
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

const activityData = ref([]);

const activityDataAll = ref({
  calories: 0,
  duration: 0,
});

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

onMounted(() => {
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

        <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
          <div
            class="lg:col-span-1 bg-white p-6 rounded-xl shadow-md border border-gray-100 flex flex-col gap-4"
          >
            <h3 class="font-bold text-xl text-gray-800 mb-2">Sumar Azi</h3>

            <div class="border-b pb-3">
              <p class="text-sm text-gray-500 flex items-center gap-2">
                <UIcon name="i-lucide-flame" class="text-red-500 w-5 h-5" />
                Calorii Arse (Kcal)
              </p>
              <p class="font-extrabold text-4xl text-red-600 mt-1">
                {{ activityDataAll.calories }}
              </p>
            </div>

            <div>
              <p class="text-sm text-gray-500 flex items-center gap-2">
                <UIcon name="i-lucide-clock" class="text-blue-500 w-5 h-5" />
                Timp Total (minute)
              </p>
              <p class="font-extrabold text-4xl text-blue-600 mt-1">
                {{ activityDataAll.duration }}
              </p>
            </div>

            <h4 class="mt-4 font-bold text-lg text-gray-700 border-t pt-4">
              Ultima Sesiune
            </h4>
            <div
              v-if="activityData.length > 0"
              class="bg-gray-50 p-4 rounded-lg flex flex-col gap-3"
            >
              <p class="font-semibold text-base text-gray-800">
                {{
                  activityData[activityData.length - 1].activityType ||
                  "Tip Nespecificat"
                }}
              </p>
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">Calorii:</span>
                <span class="font-bold text-red-500"
                  >{{
                    activityData[activityData.length - 1].calories
                  }}
                  Kcal</span
                >
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">Durată:</span>
                <span class="font-bold text-blue-500"
                  >{{
                    activityData[activityData.length - 1].duration
                  }}
                  min</span
                >
              </div>
            </div>
            <div
              v-else
              class="text-gray-500 italic text-sm p-4 bg-gray-50 rounded-lg"
            >
              Nicio sesiune înregistrată azi.
            </div>
          </div>

          <div
            class="lg:col-span-3 bg-white p-6 rounded-xl shadow-md border border-gray-100"
          >
            <client-only>
              <apexchart
                :key="modelValueDate"
                type="area"
                height="320"
                :options="chartOptions"
                :series="series"
              />
            </client-only>
          </div>

          <div
            class="lg:col-span-4 bg-white p-6 rounded-xl shadow-md border border-gray-100"
          >
            <h2 class="font-bold text-xl text-gray-800 mb-4">
              Detaliile Tuturor Sesiunilor
            </h2>
            <Table
              :content="activityData"
              :headers="['Tip Exercițiu', 'Calorii', 'Durată (min)']"
            />
            <div
              v-if="activityData.length === 0"
              class="text-center text-gray-500 italic mt-4 p-4 border-t"
            >
              Nu există activități înregistrate pentru această dată.
            </div>
          </div>
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
