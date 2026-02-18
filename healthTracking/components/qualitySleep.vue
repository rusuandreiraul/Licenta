<script setup>
import { ref } from "vue";

const props = defineProps({
  quality: {
    type: Number,
    required: true,
    default: 0,
  },
});

//trebuie sa primesc data si numa bine fac fetch
async function fetchLastSleep() {}

const percentage = computed(() => {
  const score = props.quality;
  if (score > 0) {
    return (score / 5) * 100;
  }
  return 0;
});

const series = ref([percentage.value]);

watch(
  percentage,
  (newPercentage) => {
    series.value = [newPercentage];
  },
  { immediate: true }
);

const chartOptions = ref({
  chart: {
    height: 350,
    type: "radialBar",
    offsetY: -10,
  },
  plotOptions: {
    radialBar: {
      startAngle: -135,
      endAngle: 135,
      dataLabels: {
        name: {
          fontSize: "16px",
          color: undefined,
          offsetY: 120,
        },
        value: {
          offsetY: 76,
          fontSize: "22px",
          color: undefined,
          // Funcțiile trebuie definite normal în obiect
          formatter: function (val) {
            return val + "%";
          },
        },
      },
    },
  },
  fill: {
    type: "gradient",
    gradient: {
      shade: "dark",
      shadeIntensity: 0.15,
      inverseColors: false,
      opacityFrom: 1,
      opacityTo: 1,
      stops: [0, 50, 65, 91],
    },
  },
  stroke: {
    dashArray: 4,
  },
  labels: ["Quality Sleep"],
});
</script>

<template>
  <div
    class="radial-chart-container bg-white rounded-xl border shadow-md border-gray-100"
  >
    <client-only>
      <apexchart
        type="radialBar"
        height="350"
        :options="chartOptions"
        :series="series"
      ></apexchart>
    </client-only>
  </div>
</template>
