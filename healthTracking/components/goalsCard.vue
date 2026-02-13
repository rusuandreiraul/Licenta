<script setup>
import { ref } from "vue";
import { useAuth } from "~/composable/useAuth";

const { user } = useAuth();

const username = user.value;
const goals = ref([]);

console.log(username);

const emit = defineEmits(["goalsUpdated"]); //emit de trimis catre dashboard

const state = ref({
  activity: undefined,
  sleep: undefined,
  alimentation: undefined,
});
async function fetchGoals() {
  try {
    const response = await fetch(`http://localhost:8080/goals/${username}`, {
      method: "GET",
    });

    if (response.ok) {
      goals.value = await response.json();
      console.log("goals", goals);
    }
  } catch (e) {
    console.error(e);
  }
}

async function setGoals() {
  const sendGoals = [
    {
      type: "Activity",
      targetValue: state.value.activity,
    },
    {
      type: "Sleep",
      targetValue: state.value.sleep,
    },
    {
      type: "Alimentation",
      targetValue: state.value.alimentation,
    },
  ];

  try {
    const response = await fetch(
      `http://localhost:8080/set-goals/${username}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(sendGoals),
      }
    );
    if (response.ok) {
      alert("Obiective setate cu success!!");
      fetchGoals();
      emit("goalsUpdated");
    } else {
      alert("Datele nu au fost adaugate");
    }
  } catch (e) {
    console.error(e);
  }
}

const getGoalTarget = (type) => {
  const goal = goals.value.find((g) => g.type === type);

  return goal ? goal.targetValue : "-";
};

onMounted(() => {
  fetchGoals();
});
</script>

<template>
  <UCard>
    <template #header>
      <h5
        class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white"
      >
        Goals
      </h5>
    </template>

    <div class="grid grid-cols-3 justify-items-center">
      <div class="flex flex-col gap-2">
        <svg
          class="w-6 h-6 text-gray-800 dark:text-white"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <path
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M4 4.5V19a1 1 0 0 0 1 1h15M7 14l4-4 4 4 5-5m0 0h-3.207M20 9v3.207"
          />
        </svg>
        <p>{{ getGoalTarget("Activity") }} min</p>
      </div>
      <div class="flex flex-col gap-2">
        <svg
          class="w-6 h-6 text-gray-800 dark:text-white"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <path
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M18 17v2M12 5.5V10m-6 7v2m15-2v-4c0-1.6569-1.3431-3-3-3H6c-1.65685 0-3 1.3431-3 3v4h18Zm-2-7V8c0-1.65685-1.3431-3-3-3H8C6.34315 5 5 6.34315 5 8v2h14Z"
          />
        </svg>
        <p>{{ getGoalTarget("Sleep") }} h</p>
      </div>
      <div class="flex flex-col gap-2">
        <svg
          class="w-6 h-6 text-gray-800 dark:text-white"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <path
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-miterlimit="10"
            stroke-width="2"
            d="M16 8c-1.629-1.629-3.901-1.91488-6.066.25057C7.768 10.416 2.822 18.0286 4.451 19.6576c1.629 1.629 9.242-3.318 11.407-5.483 2.093-2.0933 1.772-4.5452.142-6.174Zm0 0 3.26-3.151m-3.26 3.151V4m0 4 4 .00002m-3.798 5.802-2.2-2.2155M8.302 10.211l2.072 2.0421m-1.259 3.184 2.112 2.1633"
          />
        </svg>
        <p>{{ getGoalTarget("Alimentation") }} kcal</p>
      </div>
    </div>

    <template #footer>
      <UModal>
        <UButton label="Edit Goals" />

        <template #content>
          <form @submit.prevent="setGoals" class="p-4 space-y-4">
            <label for="activity">Activity (h/min)</label>
            <UInput id="activity" v-model="state.activity" type="number" />

            <label for="sleep">Sleep (h)</label>
            <UInput id="sleep" v-model="state.sleep" type="number" />

            <label for="alimentation">Alimentation (kcal)</label>
            <UInput
              id="alimentation"
              v-model="state.alimentation"
              type="number"
            />

            <UButton type="submit" label="Salvează Obiective" class="mt-4" />
          </form>
        </template>
      </UModal>
    </template>
  </UCard>
</template>
