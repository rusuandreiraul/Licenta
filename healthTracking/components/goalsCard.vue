<script setup>
import { ref, onMounted, watch } from "vue";
import { useAuth } from "~/composable/useAuth";

const { user, token } = useAuth();

const props = defineProps({
  targetUsername: String
});

const username = user.value;
const goals = ref([]);

console.log(username);

const emit = defineEmits(["goalsUpdated"]);

const state = ref({
  activity: undefined,
  sleep: undefined,
  alimentation: undefined,
});

async function fetchGoals() {
  try {
    if (!token.value) {
      return;
    }
    const response = await fetch(
        `http://localhost:8080/goals/${props.targetUsername}`,
        {
          method: "GET",
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token.value}`,
          }
        }
    );

    if (response.ok) {
      goals.value = await response.json();
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
        `http://localhost:8080/set-goals`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            'Authorization': `Bearer ${token.value}`,
          },
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

watch(
    () => token.value,
    (newVal) => {
      if (newVal) fetchGoals();
    },
    { immediate: true }
);

onMounted(() => {
  fetchGoals();
});
</script>

<template>
  <UCard>
    <template #header>
      <h1
          class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white"
      >
        Obiective
      </h1>
    </template>

    <div class="grid grid-cols-3 justify-items-center text-center">
      <div class="flex flex-col items-center gap-2">
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
        <p class="font-medium text-sm text-gray-700 dark:text-gray-300">
          {{ getGoalTarget("Activity") }} min
        </p>
      </div>

      <div class="flex flex-col items-center gap-2">
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
        <p class="font-medium text-sm text-gray-700 dark:text-gray-300">
          {{ getGoalTarget("Sleep") }} h
        </p>
      </div>

      <div class="flex flex-col items-center gap-2">
        <!-- MODIFICAT: SVG Măr perfect centrat, clar și modern pentru Nutriție -->
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
              d="M12 22c4.97 0 9-4.03 9-9 0-2.12-.74-4.07-1.97-5.61C17.64 5.75 15.42 5 13 5c-.34 0-.68.02-1 .06-.32-.04-.66-.06-1-.06-2.42 0-4.64.75-6.03 2.39C3.74 8.93 3 10.88 3 13c0 4.97 4.03 9 9 9Z"
          />
          <path
              stroke="currentColor"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 5c0-1.66 1-3 3-3"
          />
        </svg>
        <p class="font-medium text-sm text-gray-700 dark:text-gray-300">
          {{ getGoalTarget("Alimentation") }} kcal
        </p>
      </div>
    </div>

    <template #footer>
      <UModal v-if="user === props.targetUsername">
        <UButton label="Modificare obiective" class="text-black" />

        <template #content>
          <div class="p-5">
            <h3 class="text-base font-bold text-gray-900 dark:text-white mb-4">Setează-ți noile obiective</h3>

            <form @submit.prevent="setGoals" class="space-y-4">
              <div class="flex flex-col gap-1.5">
                <label for="activity" class="text-xs font-semibold text-gray-500 tracking-wide uppercase">
                  Activitate zilnică (min)
                </label>
                <UInput
                    id="activity"
                    v-model="state.activity"
                    type="number"
                    placeholder="Ex: 45"
                    class="shadow-2xs"
                    ui="{ rounded: 'rounded-xl', padding: 'py-2.5 px-3.5' }"
                />
              </div>

              <div class="flex flex-col gap-1.5">
                <label for="sleep" class="text-xs font-semibold text-gray-500 tracking-wide uppercase">
                  Țintă Somn (ore)
                </label>
                <UInput
                    id="sleep"
                    v-model="state.sleep"
                    type="number"
                    placeholder="Ex: 8"
                    class="shadow-2xs"
                    ui="{ rounded: 'rounded-xl', padding: 'py-2.5 px-3.5' }"
                />
              </div>

              <div class="flex flex-col gap-1.5">
                <label for="alimentation" class="text-xs font-semibold text-gray-500 tracking-wide uppercase">
                  Țintă Nutriție (kcal)
                </label>
                <UInput
                    id="alimentation"
                    v-model="state.alimentation"
                    type="number"
                    placeholder="Ex: 2000"
                    class="shadow-2xs"
                    ui="{ rounded: 'rounded-xl', padding: 'py-2.5 px-3.5' }"
                />
              </div>

              <UButton
                  type="submit"
                  label="Salvează Obiectivele"
                  block
                  size="lg"
                  class="mt-6 font-semibold shadow-sm transition-all active:scale-[0.99]"
                  color="emerald"
                  ui="{ rounded: 'rounded-xl', padding: 'py-3' }"
              />
            </form>
          </div>
        </template>
      </UModal>
      <p v-else class="text-xs italic text-center text-gray-500">
        Vizualizezi obiectivele lui {{ props.targetUsername }}
      </p>
    </template>
  </UCard>
</template>