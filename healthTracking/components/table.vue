<script setup>
const props = defineProps({
  content: {
    type: Array,
    default: () => [],
  },
  headers: {
    type: Array,
    default: () => [],
  },
});
</script>

<template>
  <div class="relative overflow-x-auto rounded-2xl shadow-lg bg-white">
    <table class="w-full text-sm text-center text-gray-700">
      <!-- Header -->
      <thead class="text-xs uppercase bg-gray-100 text-gray-700">
        <tr>
          <th
            v-for="(header, index) in props.headers"
            :key="index"
            scope="col"
            class="px-6 py-3 font-semibold tracking-wide"
          >
            {{ header }}
          </th>
        </tr>
      </thead>

      <!-- Body -->
      <tbody>
        <tr
          v-for="(row, index) in props.content"
          :key="index"
          class="border-b hover:bg-gray-50 transition-colors"
          :class="{ 'bg-gray-50': index % 2 === 0 }"
        >
          <td v-for="(header, i) in props.headers" :key="i" class="px-6 py-3">
            {{ row[Object.keys(row)[i]] }}
          </td>
        </tr>

        <!-- Fallback dacă nu există date -->
        <tr v-if="!props.content.length">
          <td
            :colspan="props.headers.length"
            class="px-6 py-4 text-gray-400 italic"
          >
            No data available
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
