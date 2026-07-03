<script setup>
import { ref, computed, watch, onMounted } from "vue";
import Sidebar from "~/components/Sidebar.vue";
import { useAuth } from "~/composable/useAuth";
import { today, getLocalTimeZone } from "@internationalized/date";
import DashboardCard from "~/components/dashboardCard.vue";
import GoalsCard from "~/components/goalsCard.vue";
import { useDateWeek } from "~/composable/useDateWeek";
import { useGoals } from "~/composable/useGoals.js";

// User
const { user, token } = useAuth();

const username = computed(() => user.value);

const { getLastWeekDates } = useDateWeek();

const userValue = ref({});

const date = ref(today(getLocalTimeZone()));

const week = ref(getLastWeekDates(date.value));

const { dataGoals, getGoals } = useGoals();

definePageMeta({
  middleware: 'auth'
});

useSeoMeta({
  title: 'Dashboard Personal - Health Tracker',
  description: 'Vizualizează progresul zilnic pentru activitate fizică, somn și nutriție integrat cu inteligență artificială.',
  ogTitle: 'Dashboard Personal - Health Tracker',
  ogDescription: 'Monitorizare inteligentă a stilului de viață.',
});

const activityData = ref({});
const sleepData = ref({});
const alimentationData = ref([]);

// Grafice
const seriesRadial = ref([0, 0, 0]);
const seriesBar = ref([
  {
    name: "Calorii arse",
    data: [0, 0, 0, 0, 0, 0, 0],
  },
  {
    name: "Durata somnului",
    data: [0, 0, 0, 0, 0, 0, 0],
  },
  {
    name: "Calorii consumate",
    data: [0, 0, 0, 0, 0, 0, 0],
  },
]);

const chartColors = ["#3b82f6", "#8b5cf6", "#f97316"];

const chartOptionsBar = ref({
  chart: {
    type: "bar",
    height: 320,
    toolbar: { show: false },
    animations: {
      enabled: false,
    }
  },
  colors: chartColors,
  plotOptions: {
    bar: {
      horizontal: false,
      columnWidth: "60%",
      borderRadius: 1,
      borderRadiusApplication: "end",
    },
  },
  dataLabels: { enabled: false },
  stroke: { show: true, width: 2, colors: ["transparent"] },
  xaxis: {
    categories: [...week.value],
    labels: { style: { colors: "#374151", fontSize: "12px" } },
  },
  yaxis: [
    {
      seriesName: "Calorii arse",
      title: { text: "Calorii (Arse / Consumate)", style: { color: "#3b82f6" } },
      labels: { style: { colors: "#374151" } }
    },
    {

      seriesName: "Durata somnului",
      opposite: true,
      max: 12,
      min: 0,
      title: { text: "Durata somnului (Ore)", style: { color: "#8b5cf6" } },
      labels: { style: { colors: "#8b5cf6" } }
    },
    {
      seriesName: "Calorii arse",
      show: false
    }
  ],
  fill: { opacity: 1 },
  tooltip: {
    shared: true,
    intersect: false,
    y: { formatter: (val) => val },
  },
});

const chartOptionsRadial = ref({
  chart: {
    height: 300,
    width: 200,
    type: "radialBar",
    animations: { enabled: false }
  },
  colors: chartColors,
  plotOptions: {
    radialBar: {
      dataLabels: {
        name: { fontSize: "18px" },
        value: { fontSize: "14px", color: "#111" },
      },
    },
  },
  labels: ["Activitate", "Somn", "Nutriție"],
});

const icons = {
  activity: `<svg class="w-6 h-6 text-gray-800 dark:text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4.5V19a1 1 0 0 0 1 1h15M7 14l4-4 4 4 5-5m0 0h-3.207M20 9v3.207"/></svg>`,
  sleep: `<svg class="w-6 h-6 text-gray-800 dark:text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 17v2M12 5.5V10m-6 7v2m15-2v-4c0-1.6569-1.3431-3-3-3H6c-1.65685 0-3 1.3431-3 3v4h18Zm-2-7V8c0-1.65685-1.3431-3-3-3H8C6.34315 5 5 6.34315 5 8v2h14Z"/></svg>`,
  alimentation: `<svg class="w-6 h-6 text-gray-800 dark:text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-miterlimit="10" stroke-width="2" d="M16 8c-1.629-1.629-3.901-1.91488-6.066.25057C7.768 10.416 2.822 18.0286 4.451 19.6576c1.629 1.629 9.242-3.318 11.407-5.483 2.093-2.0933 1.772-4.5452.142-6.174Zm0 0 3.26-3.151m-3.26 3.151V4m0 4 4 .00002m-3.798 5.802-2.2-2.2155M8.302 10.211l2.072 2.0421m-1.259 3.184 2.112 2.1633"/></svg>`,
};

const formatDate = (calendarDate) => {
  const year = calendarDate.year;
  const month = String(calendarDate.month).padStart(2, "0");
  const day = String(calendarDate.day).padStart(2, "0");

  return `${year}-${month}-${day}`;
};

const selectedFile = ref(null);

function handleFileChange(event) {
  selectedFile.value = event.target.files[0];
}

async function fetchUser() {
  try {
    const response = await fetch(`http://localhost:8080/profile`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token.value}`,
      }
    });
    if (response.ok) {
      userValue.value = await response.json();
    }
  } catch (e) {
    console.error(e);
  }
}

async function fetchChanges() {
  const formData = new FormData();
  formData.append("height", userValue.value.height);
  formData.append("weight", userValue.value.weight);

  if (selectedFile.value) {
    formData.append("profileImage", selectedFile.value);
  }

  try {
    const response = await fetch(
        `http://localhost:8080/user-change`,
        {
          method: "POST",
          headers: {
            "Authorization": `Bearer ${token.value}`,
          },
          body: formData,
        }
    );
    if (response.ok) {
      const data = await response.json();
      userValue.value = data;
      console.log("user modificat ", userValue.value);
    }
  } catch (e) {
    console.error(e);
  }
}

async function fetchWeekData() {
  const selectedDate = formatDate(date.value);
  try {
    const response = await fetch(
        `http://localhost:8080/dashboard-week/${selectedDate}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token.value}`,
          }
        }
    );

    if (response.ok) {
      const data = await response.json();
      console.log("data week", data);
      seriesBar.value[0].data = data.calories;
      seriesBar.value[1].data = data.qualitySleep;
      seriesBar.value[2].data = data.caloriesConsumed;
    }
  } catch (e) {
    console.error(e);
  }
}

async function fetchDailyData() {
  const selectedDate = formatDate(date.value);
  try {
    const response = await fetch(
        `http://localhost:8080/dashboard-daily/${selectedDate}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            'Authorization': `Bearer ${token.value}`,
          }
        }
    );
    if (response.ok) {
      const data = await response.json();
      console.log("data per zi", data);
      activityData.value = data.activityDetails || [];
      sleepData.value = data.sleepDetails || null;
      alimentationData.value = {
        names: data.alimentationName || [],
        stats: {
          calories: data.totalCaloriesConsumed || 0,
          protein: data.totalProteinConsumed || 0,
          carbs: data.totalCarbosConsumed || 0,
          fat: data.totalFatConsumed || 0,
        },
      };

      const activityTarget = dataGoals.value?.[0]?.targetValue || 1;
      const sleepTarget = dataGoals.value?.[1]?.targetValue || 1;
      const foodTarget = dataGoals.value?.[2]?.targetValue || 1;

      const activitySerial = Math.min(100, (data.totalActivityDuration / activityTarget) * 100);
      const sleepSerial = Math.min(100, (data.totalHoursSleep / sleepTarget) * 100);
      const alimetationSerial = Math.min(100, (data.totalCaloriesConsumed / foodTarget) * 100);

      seriesRadial.value = [
        activitySerial || 0,
        sleepSerial || 0,
        alimetationSerial || 0,
      ];
    }
  } catch (e) {
    console.error(e);
  }
}

async function refreshFetch() {
  try {
    await Promise.all([
      fetchUser(),
      fetchDailyData(),
      fetchWeekData()
    ]);
  } catch (error) {
    console.error("Eroare la încărcarea paralelă a datelor:", error);
  }
}

watch(date, (newDate) => {
  week.value = getLastWeekDates(newDate);
  chartOptionsBar.value.xaxis.categories = [...week.value]
  fetchDailyData();
  fetchWeekData();
});

onMounted(async () => {
  await getGoals();
  refreshFetch();
});
</script>

<template>
  <div
      class="min-h-screen w-full flex bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-gray-100 relative"
  >
    <Sidebar />
    <client-only>
      <main v-if="user" class="flex-1 p-6 sm:pl-72 pt-5">
        <div class="flex flex-col md:flex-row gap-6 min-h-screen items-start">
          <div class="flex-1 bg-gray-100 h-full rounded-lg p-4">
            <div>
              <h1 class="text-bold text-2xl">Salut, {{ username }} 👋</h1>
            </div>
            <div class="grid grid-cols-2 p-6 gap-2">
              <div class="bg-white rounded-2xl text-center transition-all hover:-translate-y-2 hover:shadow-xl">
                <client-only>
                  <apexchart
                      type="radialBar"
                      :height="300"
                      :options="chartOptionsRadial"
                      :series="seriesRadial"
                  />
                </client-only>
                Tot progresul într-un singur grafic
              </div>
              <div class="bg-white rounded-2xl transition-all hover:-translate-y-2 hover:shadow-xl">
                <client-only>
                  <apexchart
                      type="bar"
                      :key="date"
                      :height="320"
                      :options="chartOptionsBar"
                      :series="seriesBar"
                  />
                </client-only>
              </div>
            </div>

            <div class="grid grid-cols-3 p-2 gap-2 ">
              <DashboardCard
                  title="Activitate fizică"
                  :icon="icons.activity"
                  :content="activityData"
              />
              <DashboardCard
                  title="Somn"
                  :icon="icons.sleep"
                  :content="sleepData"
              />
              <DashboardCard
                  title="Nutriție"
                  :icon="icons.alimentation"
                  :content="alimentationData"
              />
            </div>
          </div>
          <div class="w-full md:w-[300px] h-full flex flex-col gap-4">
            <!-- Card user info -->
            <div
                class="bg-white rounded-2xl shadow-md p-6 flex flex-col items-center space-y-4"
            >
              <!-- Avatar și nume -->
              <div class="flex flex-col items-center space-y-2">
                <NuxtImg
                    class="w-20 h-20 rounded-full border-2 border-green-500 object-cover"
                    :src="userValue.urlProfileImage"
                    alt="Avatar user"
                    width="80"
                    height="80"
                    loading="lazy"
                />
                <h2 class="text-xl font-semibold text-gray-800 dark:text-white">
                  {{ username }}
                </h2>
              </div>

              <div class="grid grid-cols-3 gap-2 text-center w-full">
                <div class="flex flex-col items-center">
                  <span class="text-gray-700 text-sm">Greutate</span>
                  <span class="text-lg font-medium text-gray-800 dark:text-white">
                  {{ userValue.weight || "-" }} kg
                </span>
                </div>
                <div class="flex flex-col items-center">
                  <span class="text-gray-700 text-sm">Înalțime</span>
                  <span class="text-lg font-medium text-gray-800 dark:text-white">
                  {{ userValue.height || "-" }} cm
                </span>
                </div>
                <div class="flex flex-col items-center">
                  <span class="text-gray-700 text-sm">Data de naștere</span>
                  <span class="text-lg font-medium text-gray-800 dark:text-white">
                  {{ userValue.birthDate || "-" }}
                </span>
                </div>
              </div>

              <UModal title="Modificare profil">
                <UButton
                    class="mt-2 px-4 py-2 bg-green-500 hover:bg-green-600 text-black rounded-full text-sm font-medium transition-colors"
                >
                  Modificare profil
                  <svg class="w-6 h-6 text-white-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="square" stroke-linejoin="round" stroke-width="2" d="M10 19H5a1 1 0 0 1-1-1v-1a3 3 0 0 1 3-3h2m10 1a3 3 0 0 1-3 3m3-3a3 3 0 0 0-3-3m3 3h1m-4 3a3 3 0 0 1-3-3m3 3v1m-3-4a3 3 0 0 1 3-3m-3 3h-1m4-3v-1m-2.121 1.879-.707-.707m5.656 5.656-.707-.707m-4.242 0-.707.707m5.656-5.656-.707.707M12 8a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"/>
                  </svg>
                </UButton>

                <template #body>
                  <div class="p-5">
                    <form @submit.prevent="fetchChanges" class="space-y-5">


                      <div class="grid grid-cols-2 gap-4">
                        <div class="flex flex-col gap-1.5">
                          <label for="height" class="text-xs font-semibold text-gray-500 tracking-wide uppercase">
                            Înălțime (cm)
                          </label>
                          <UInput
                              id="height"
                              type="number"
                              v-model="userValue.height"
                              placeholder="Ex: 180"
                              class="shadow-2xs"
                              ui="{ rounded: 'rounded-xl', padding: 'py-2.5 px-3.5' }"
                          />
                        </div>
                        <div class="flex flex-col gap-1.5">
                          <label for="weight" class="text-xs font-semibold text-gray-500 tracking-wide uppercase">
                            Greutate (kg)
                          </label>
                          <UInput
                              id="weight"
                              type="number"
                              v-model="userValue.weight"
                              placeholder="Ex: 75"
                              class="shadow-2xs"
                              ui="{ rounded: 'rounded-xl', padding: 'py-2.5 px-3.5' }"
                          />
                        </div>
                      </div>

                      <div class="flex flex-col gap-1.5">
                        <label class="text-xs font-semibold text-gray-500 tracking-wide uppercase">
                          Noua Imagine de profil
                        </label>
                        <UInput
                            type="file"
                            @change="handleFileChange"
                            class="shadow-2xs cursor-pointer"
                            ui="{
                            rounded: 'rounded-xl',
                            file: {
                              base: 'file:bg-gray-100 file:text-gray-700 file:border-0 file:rounded-lg file:hover:bg-gray-200 file:cursor-pointer file:font-medium file:text-xs file:py-1 file:px-2 file:mr-2'
                            }
                          }"
                        />
                      </div>

                      <UButton
                          type="submit"
                          label="Salvează modificările profilului"
                          block
                          size="lg"
                          class="mt-4 font-semibold shadow-sm transition-all active:scale-[0.99]"
                          color="indigo"
                          ui="{ rounded: 'rounded-xl', padding: 'py-3' }"
                      />
                    </form>
                  </div>
                </template>
              </UModal>
            </div>

            <div>
              <GoalsCard
                  :targetUsername="username"
                  @goalsUpdated="refreshFetch"
              />
            </div>

            <!-- Calendar separat jos -->
            <client-only>
              <div class="bg-gray-100 rounded-2xl p-4 flex-1">
                <UCalendar v-model="date" />
              </div>
            </client-only>
          </div>
        </div>
      </main>
      <template #fallback>
        <div class="flex-1 p-6 sm:ml-64 pt-5 text-gray-500">Se încarcă datele...</div>
      </template>
    </client-only>
  </div>
</template>