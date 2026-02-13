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

        <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
          <div class="bg-white p-6 rounded-xl shadow-md border border-gray-100">
            <p
              class="text-sm text-gray-500 font-medium flex items-center gap-2"
            >
              <UIcon name="i-lucide-bed-double" class="text-blue-500 w-5 h-5" />
              Ore Dormite (Medie)
            </p>
            <p
              class="font-extrabold text-5xl mt-2"
              :class="{
                'text-green-600': avgHoursSlept >= 7,
                'text-yellow-500': avgHoursSlept >= 5 && avgHoursSlept < 7,
                'text-red-600': avgHoursSlept < 5,
              }"
            >
              {{ avgHoursSlept }}
              <span class="text-xl font-normal text-gray-500">h</span>
            </p>
            <p v-if="avgHoursSlept >= 7" class="text-sm text-green-500 mt-2">
              Excelent! Menține ritmul.
            </p>
            <p
              v-else-if="avgHoursSlept > 0"
              class="text-sm text-yellow-500 mt-2"
            >
              Recomandat 7-9 ore.
            </p>
          </div>

          <div
            class="lg:col-span-1 bg-white p-6 rounded-xl shadow-md border border-gray-100"
          >
            <p
              class="text-sm text-gray-500 font-medium flex items-center gap-2"
            >
              <UIcon name="i-lucide-star" class="text-yellow-500 w-5 h-5" />
              Calitate Somn (Medie)
            </p>
            <p
              class="font-extrabold text-5xl mt-2"
              :class="{
                'text-green-600': avgQuality >= 4,
                'text-yellow-500': avgQuality >= 3 && avgQuality < 4,
                'text-red-600': avgQuality < 3,
              }"
            >
              {{ avgQuality }}
              <span class="text-xl font-normal text-gray-500">/5</span>
            </p>
            <p v-if="avgQuality >= 4" class="text-sm text-green-500 mt-2">
              Foarte bine!
            </p>
            <p v-else-if="avgQuality > 0" class="text-sm text-yellow-500 mt-2">
              Se poate îmbunătăți.
            </p>
          </div>

          <div
            class="lg:col-span-2 bg-white p-6 rounded-xl shadow-md border border-gray-100 flex flex-col items-center"
          >
            <h4
              class="font-bold text-lg text-gray-800 mb-4 border-b w-full text-center pb-2"
            >
              Calitatea Ultimului Somn
            </h4>
            <div class="flex items-center justify-center gap-6 w-full">
              <div class="flex-shrink-0">
                <QualitySleep :quality="lastQualitySleep" />
              </div>

              <div class="flex flex-col gap-2">
                <div class="text-lg font-bold text-gray-700">
                  {{ lastQualitySleep }} / 5
                </div>
                <div class="text-sm text-gray-600">
                  <UIcon name="i-lucide-clock" class="w-4 h-4 mr-1" />
                  Durată:
                  <span class="font-semibold">{{ lastHoursSlept }} ore</span>
                </div>
                <p
                  v-if="lastQualitySleep >= 4"
                  class="text-sm text-green-500 font-semibold mt-1"
                >
                  Ai dormit excelent!
                </p>
                <p
                  v-else-if="lastQualitySleep > 0"
                  class="text-sm text-red-500 font-semibold mt-1"
                >
                  Încearcă să te odihnești mai bine.
                </p>
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
