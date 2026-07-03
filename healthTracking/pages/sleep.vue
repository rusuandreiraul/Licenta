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
import Chatbot from "~/components/chatbot.vue";

const { user, token } = useAuth();
const { getLastWeekDates } = useDateWeek();

const df = new DateFormatter("en-US", {
  dateStyle: "medium",
});

const selectedId = ref(null);
const showModal = ref(false);

function openModal(id) {
  selectedId.value = id;
  showModal.value = true;
}

useSeoMeta({
  title: 'Analiza Somnului - WellSync',
  description: 'Vizualizează istoricul de somn',
  ogTitle: 'Analiza somnului - WellSync',
  ogDescription: 'Vizualizează istoricul de somn.',
});

// --- STATE-URI (Datele) ---
const avgQuality = ref(0);
const avgHoursSlept = ref(0);
const lastQualitySleep = ref(0);
const lastHoursSlept = ref(0); // Adăugat pentru a arăta și ultima durată
const lastStress = ref(0);
const lastMorningEnergy = ref(0);

const sleepDataByDate = ref();

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
    // Generăm o copie inversată locală izolată pentru axa X inițială (14.06 -> 08.06)
    categories: [...Object.values(week.value)].reverse(),
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

async function fetchSleepByDateRange() {
  if (!token.value) return;

  const selectedDate = modelValueDate.value.toString();
  try {
    const response = await fetch(
        `http://localhost:8080/sleep-week/${selectedDate}`,
        { method: "GET",
          headers: { "Content-Type": "application/json",
            "Authorization": `Bearer ${token.value}`,
          },
        }
    );

    if (response.ok) {
      const dataSleep = await response.json();
      console.log("data week sleep", dataSleep);

      if (!dataSleep || dataSleep.length === 0) {
        resetSleepData();
        return;
      }

      // 1. Calcul medii
      const validQuality = dataSleep.map((s) => s.quality).filter(q => q !== undefined && q !== null);
      const sumQuality = validQuality.reduce((acc, current) => acc + current, 0);
      avgQuality.value = validQuality.length ? (sumQuality / validQuality.length).toFixed(1) : 0;

      const validHours = dataSleep.map((s) => s.hoursSlept).filter(h => h !== undefined && h !== null);
      const sumHours = validHours.reduce((acc, hours) => acc + hours, 0);
      avgHoursSlept.value = validHours.length ? (sumHours / validHours.length).toFixed(1) : 0;

      // 2. Facem o copie superficială izolată a zilelor în ordine inversă (14.06 -> 08.06)
      const daysArray = [...Object.values(week.value)].reverse();

      // 3. Aliniem orele de somn cu acest array izolat
      const alignedHoursData = daysArray.map((dateStr) => {
        const targetDate = dateStr.toString().trim();

        const matchingDay = dataSleep.find((s) => {
          // CORECTURĂ: Citim proprietatea corectă din backend (dateSleep)
          if (!s.dateSleep) return false;
          const backendDate = s.dateSleep.toString().split('T')[0].trim();
          return backendDate === targetDate;
        });

        return matchingDay ? matchingDay.hoursSlept : 0;
      });

      // Trimitem datele aliniate corect către grafic
      series.value[0].data = alignedHoursData;
    }
  } catch (e) {
    console.error("Eroare la preluarea datelor de somn:", e);
    resetSleepData();
  }
}

function resetSleepData() {
  avgQuality.value = 0;
  avgHoursSlept.value = 0;
  lastQualitySleep.value = 0;
  lastHoursSlept.value = 0;
  series.value[0].data = Array(7).fill(0);
}

async function fetchSleepByDate(){
  const selectedDate = modelValueDate.value.toString();
  try {
    const response = await fetch(`http://localhost:8080/dashboard-sleep/${selectedDate}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token.value}`
      }
    });
    if (response.ok) {
      sleepDataByDate.value = await response.json();
      console.log(sleepDataByDate.value);
      lastStress.value=sleepDataByDate.value.stress;
      lastQualitySleep.value=sleepDataByDate.value.quality;
      lastHoursSlept.value = sleepDataByDate.value.hoursSlept;
      lastMorningEnergy.value=sleepDataByDate.value.morningEnergy;
    }
    else{
      lastStress.value=0;
      lastQualitySleep.value=0;
      lastHoursSlept.value = 0;
      lastMorningEnergy.value=0;
    }
  }
  catch(e){
    console.error("Eroare la preluarea datelor de somn:", e);
  }
}

async function deleteSleep(){
  try {
    const response = await fetch(`http://localhost:8080/delete-sleep/${selectedId.value}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token.value}`
      }
    });
    if (response.ok) {
      alert("Datele despre somn din data selectata au fost sterse cu success");
      showModal.value=false;
      sleepDataByDate.value = null;
      fetchSleepByDate();
      fetchSleepByDateRange();
    }
  }
  catch(e){
    console.error("Eroare la stergere datelor de somn:", e);
  }
}

watch(modelValueDate, (newDate) => {
  week.value = getLastWeekDates(newDate);
  // CORECTURĂ: Folosim operatorul spread pentru a clona, evitând modificarea directă a lui week.value
  chartOptions.value.xaxis.categories = [...Object.values(week.value)].reverse();
  fetchSleepByDateRange();
  fetchSleepByDate();
});

async function fetchAllSleep(){
  await fetchSleepByDateRange();
  await fetchSleepByDateRange();
}

onMounted(() => {
  fetchSleepByDateRange();
  fetchSleepByDate();
});
</script>

<template>
  <div class="min-h-screen flex bg-gray-50 relative">
    <Sidebar />

    <main class="flex-1 p-4 sm:pl-72 flex gap-6">
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
           <div v-if="sleepDataByDate">
             <UModal
                 title="Confirmă ștergerea"
                 :close="{
                            color: 'primary',
                            variant: 'outline',
                            class: 'rounded-full'
                              }">
               <UButton label="Sterge" color="error" @click="openModal(sleepDataByDate.id)"/>
               <template #body>
                 <div class="p-4">
                   <p>Sigur vrei să ștergi această sesiune de somn?</p>
                   <div class="flex gap-3 justify-end mt-4">

                     <button @click="deleteSleep" class="px-4 py-2 bg-red-500 hover:translate-1 text-white rounded">
                       Șterge
                     </button>
                   </div>
                 </div>
               </template>
             </UModal>
           </div>
            <p v-else class="italic text-sm text-gray-700">
              Nu au fost gasite date despre somn pentru aceasta zi
            </p>
          </div>

          <div class="flex gap-3 items-center">
            <p class="text-sm font-semibold text-gray-700 hidden sm:block">
              Înregistrează sesiunea de somn:
            </p>
            <AddModal type="Adăugare sesiune de somn" :user="user" :date="modelValueDate" @success="fetchAllSleep" />
          </div>
        </div>
        <div class="grid grid-cols-2 gap-4 items-stretch">
          <QualitySleep :quality="lastQualitySleep" class="h-full transition-all hover:-translate-y-2 hover:shadow-xl" />

          <div class="grid grid-cols-2 grid-rows-2 gap-3">
            <div
              class="p-4 bg-white border border-gray-100 shadow-md rounded-xl flex flex-col justify-center items-center h-full transition-all hover:-translate-y-2 hover:shadow-xl"
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
              class="p-4 gap-1 bg-white border border-gray-100 shadow-md rounded-xl flex flex-col justify-center items-center h-full transition-all hover:-translate-y-2 hover:shadow-xl"
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
              class="p-4 bg-white border border-gray-100 shadow-md rounded-xl flex flex-col justify-center items-center h-full transition-all hover:-translate-y-2 hover:shadow-xl"
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
              class="p-4 bg-white border border-gray-100 shadow-md rounded-xl flex flex-col justify-center items-center h-full transition-all hover:-translate-y-2 hover:shadow-xl"
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
            class="text-center text-gray-700 italic mt-4 p-4 border-t"
          >
            Nu există date de somn pentru această săptămână.
          </div>
        </div>
      </div>

      <div
          class="hidden md:flex flex-col w-[350px] bg-gray-200 p-4 rounded-lg sticky top-0 h-screen"
      >
        <h2 class="text-gray-700 font-bold mb-2">Asistent AI</h2>
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
