<script setup>
import { ref, watch, onMounted } from "vue";
import Sidebar from "~/components/Sidebar.vue";
import AddModal from "~/components/addModal.vue"; // Adăugat AddModal
import {
  DateFormatter,
  getLocalTimeZone,
  today,
} from "@internationalized/date";
// Presupunem că acest composable returnează datele săptămânii
import { useDateWeek } from "~/composable/useDateWeek";
import { useAuth } from "~/composable/useAuth";

import QualitySleep from "~/components/qualitySleep.vue";

const { user } = useAuth();
const { getLastWeekDates } = useDateWeek();

const df = new DateFormatter("en-US", {
  dateStyle: "medium",
});

// --- STATE-URI (Datele) ---
const avgQuality = ref(0);
const avgHoursSlept = ref(0);
const lastQualitySleep = ref(0);
const lastHoursSlept = ref(0); // Adăugat pentru a arăta și ultima durată
const lastStress = ref(0);
const lastMorningEnergy = ref(0);

const modelValueDate = ref(today(getLocalTimeZone()));
const week = ref(getLastWeekDates(modelValueDate.value));

const colors = ["#4A90E2"]; // Albastru (pentru somn)

const series = ref([
  {
    name: "Ore Dormite",
    data: [0, 0, 0, 0, 0, 0, 0],
  },
]);

// --- OPȚIUNI GRAFIC ---
const chartOptions = ref({
  chart: {
    height: 350,
    type: "bar",
    toolbar: { show: false }, // Ascundem toolbar-ul
  },
  colors: colors,
  plotOptions: {
    bar: {
      columnWidth: "60%", // Lățime mai mare a coloanelor
      distributed: false,
      borderRadius: 4, // Colțuri rotunjite
    },
  },
  dataLabels: {
    enabled: true, // Afișăm valorile deasupra barelor
    formatter: function (val) {
      return val + "h";
    },
  },
  legend: {
    show: false,
  },
  title: {
    text: "Durata Somnului (Ore/Noapte)",
    align: "left",
    style: { fontSize: "16px", fontWeight: "bold" },
  },
  xaxis: {
    categories: week.value,
    labels: {
      style: {
        colors: "#6B7280", // Text mai puțin intens
        fontSize: "12px",
      },
    },
  },
  yaxis: {
    title: {
      text: "Ore Dormite",
    },
  },
});

// --- FUNCȚII ASINCRONE (Fetch) ---
async function fetchSleepByDateRange() {
  if (!user.value) return;

  const selectedDate = modelValueDate.value.toString();
  const username = user.value;

  try {
    const response = await fetch(
      `http://localhost:8080/sleep-week/${selectedDate}/${username}`,
      { method: "GET" }
    );

    if (response.ok) {
      const dataSleep = await response.json();
      console.log("data sleep: ", dataSleep);
      if (dataSleep.length === 0) {
        // Resetăm valorile dacă nu sunt date
        avgQuality.value = 0;
        avgHoursSlept.value = 0;
        lastQualitySleep.value = 0;
        lastHoursSlept.value = 0;
        series.value[0].data = Array(7).fill(0);
        return;
      }

      // 1. Calculează Calitatea Medie
      const quality = dataSleep.map((s) => s.quality);
      const sumQuality = quality.reduce((acc, current) => acc + current, 0);
      avgQuality.value = (sumQuality / quality.length).toFixed(1);

      // 2. Calculează Orele Medii Dormite și actualizează seria graficului
      const hoursSlept = dataSleep.map((s) => s.hoursSlept);
      const sumHours = hoursSlept.reduce((acc, hours) => acc + hours, 0);
      avgHoursSlept.value = (sumHours / hoursSlept.length).toFixed(1);

      series.value[0].data = hoursSlept; // Presupunem că datele vin deja în ordinea corectă a săptămânii

      // 3. Setăm ultima valoare
      lastQualitySleep.value = dataSleep[dataSleep.length - 1].quality;
      lastHoursSlept.value = dataSleep[dataSleep.length - 1].hoursSlept;
      lastStress.value = dataSleep[dataSleep.length - 1].stress;
      lastMorningEnergy.value = dataSleep[dataSleep.length - 1].morningEnergy;
    }
  } catch (e) {
    console.error("Eroare la preluarea datelor de somn:", e);
    // Asigură-te că UI-ul nu afișează date false la eroare
    avgQuality.value = 0;
    avgHoursSlept.value = 0;
    lastQualitySleep.value = 0;
    lastHoursSlept.value = 0;
    series.value[0].data = Array(7).fill(0);
  }
}

// --- HOOKS și WATCHERS ---
watch(modelValueDate, (newDate) => {
  week.value = getLastWeekDates(newDate);
  chartOptions.value.xaxis.categories = week.value;
  fetchSleepByDateRange();
});

onMounted(() => {
  fetchSleepByDateRange();
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
              Vizualizezi datele de somn săptămânale.
            </p>
          </div>

          <div class="flex gap-3 items-center">
            <p class="text-sm font-semibold text-gray-700 hidden sm:block">
              Înregistrează sesiunea de somn:
            </p>
            <AddModal type="sleep" :user="user" :date="modelValueDate" />
          </div>
        </div>
        <div class="grid grid-cols-2 gap-4 items-stretch">
          <QualitySleep :quality="lastQualitySleep" class="h-full" />

          <div class="grid grid-cols-2 grid-rows-2 gap-3">
            <div
              class="p-4 bg-white border border-gray-100 shadow-md rounded-xl flex flex-col justify-center items-center h-full"
            >
              <svg
                class="w-6 h-6 text-gray-800 dark:text-white"
                aria-hidden="true"
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                fill="none"
                viewBox="0 0 24 24"
              >
                <path
                  fill="currentColor"
                  d="M12 17a2 2 0 0 1 2 2h-4a2 2 0 0 1 2-2Z"
                />
                <path
                  stroke="currentColor"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M13.815 9H16.5a2 2 0 1 0-1.03-3.707A1.87 1.87 0 0 0 15.5 5 1.992 1.992 0 0 0 12 3.69 1.992 1.992 0 0 0 8.5 5c.002.098.012.196.03.293A2 2 0 1 0 7.5 9h3.388m2.927-.985v3.604M10.228 9v2.574M15 16h.01M9 16h.01m11.962-4.426a1.805 1.805 0 0 1-1.74 1.326 1.893 1.893 0 0 1-1.811-1.326 1.9 1.9 0 0 1-3.621 0 1.8 1.8 0 0 1-1.749 1.326 1.98 1.98 0 0 1-1.87-1.326A1.763 1.763 0 0 1 8.46 12.9a2.035 2.035 0 0 1-1.905-1.326A1.9 1.9 0 0 1 4.74 12.9 1.805 1.805 0 0 1 3 11.574V12a9 9 0 0 0 18 0l-.028-.426Z"
                />
              </svg>
              <span class="text-xs p-3"><i>Stress</i></span>
              <span class="text-xs font-bold text-gray-500 uppercase">{{
                lastStress
              }}</span>
            </div>

            <div
              class="p-4 gap-1 bg-white border border-gray-100 shadow-md rounded-xl flex flex-col justify-center items-center h-full"
            >
              <svg
                class="w-6 h-6 text-gray-800 dark:text-white"
                aria-hidden="true"
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                fill="none"
                viewBox="0 0 24 24"
              >
                <path
                  stroke="currentColor"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 8v4l3 3m6-3a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"
                />
              </svg>
              <span class="text-xs p-3"><i>Ore dormite</i></span>
              <span class="text-xs font-bold text-gray-500 uppercase">{{
                lastHoursSlept
              }}</span>
            </div>

            <div
              class="p-4 bg-white border border-gray-100 shadow-md rounded-xl flex flex-col justify-center items-center h-full"
            >
              <svg
                class="w-6 h-6 text-gray-800 dark:text-white"
                aria-hidden="true"
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                fill="none"
                viewBox="0 0 24 24"
              >
                <path
                  stroke="currentColor"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M2.98755 7.97095c0-.55229.44771-1 1-1H16.9253c.5523 0 1 .44771 1 1v7.95855c0 .5522-.4477 1-1 1H3.98755c-.55229 0-1-.4478-1-1V7.97095ZM20.9129 12.9419v-1.9834c0-.5523-.4478-1-1-1h-.9876c-.5523 0-1 .4477-1 1v1.9834c0 .5523.4477 1 1 1h.9876c.5522 0 1-.4477 1-1Z"
                />
                <path
                  stroke="currentColor"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M5.9751 9.9585h8.9627v3.9834H5.9751V9.9585Z"
                />
              </svg>
              <span class="text-xs p-3"><i>Energie dimineata</i></span>
              <span class="text-xs font-bold text-gray-500 uppercase">{{
                lastMorningEnergy
              }}</span>
            </div>

            <div
              class="p-4 bg-white border border-gray-100 shadow-md rounded-xl flex flex-col justify-center items-center h-full"
            >
              <svg
                class="w-6 h-6 text-gray-800 dark:text-white"
                aria-hidden="true"
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                fill="none"
                viewBox="0 0 24 24"
              >
                <path
                  stroke="currentColor"
                  stroke-width="2"
                  d="M11.083 5.104c.35-.8 1.485-.8 1.834 0l1.752 4.022a1 1 0 0 0 .84.597l4.463.342c.9.069 1.255 1.2.556 1.771l-3.33 2.723a1 1 0 0 0-.337 1.016l1.03 4.119c.214.858-.71 1.552-1.474 1.106l-3.913-2.281a1 1 0 0 0-1.008 0L7.583 20.8c-.764.446-1.688-.248-1.474-1.106l1.03-4.119A1 1 0 0 0 6.8 14.56l-3.33-2.723c-.698-.571-.342-1.702.557-1.771l4.462-.342a1 1 0 0 0 .84-.597l1.753-4.022Z"
                />
              </svg>
              <span class="text-xs p-3"><i>Calitate</i></span>
              <span class="text-xs font-bold text-gray-500 uppercase">{{
                lastQualitySleep
              }}</span>
            </div>
          </div>
        </div>
        <div
          class="lg:col-span-4 bg-white p-6 rounded-xl shadow-md border border-gray-100"
        >
          <client-only>
            <apexchart
              :key="modelValueDate"
              type="bar"
              height="350"
              :options="chartOptions"
              :series="series"
            />
          </client-only>
          <div
            v-if="series[0].data.every((v) => v === 0)"
            class="text-center text-gray-500 italic mt-4 p-4 border-t"
          >
            Nu există date de somn pentru această săptămână.
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
