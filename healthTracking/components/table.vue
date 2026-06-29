<script setup>

import {useAuth} from "~/composable/useAuth.js";
import {ref} from "vue";


const props = defineProps({
  content: {
    type: Array,
    default: () => [],
  },
  headers: {
    type: Array,
    default: () => [],
  },
  keys:{
    type: Array,
    default: () => [],
  }
});

const emit=defineEmits(["deleted"]);

const {token} = useAuth();

const showModal = ref(false);
const selectedId = ref(null);

function openModal(id) {
  selectedId.value = id;
  showModal.value = true;
}


async function deleteActivity() {
  try {
    const response=await fetch(`http://localhost:8080/activity/delete-activity/${selectedId.value}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token.value}`
      }
    });

    if(response.ok){
      alert("activitate stearsa cu succes");
    }

    showModal.value = false;
    emit('deleted');
  } catch (error) {
    console.error(error);
  }
}



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
          <th class="px-6 py-3 font-semibold tracking-wide">Stergere</th>
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
          <td v-for="col in props.keys" :key="col" class="px-6 py-3">
            {{ row[col]}}
          </td>
          <td>
            <UModal v-model="showModal">
              <UButton label="Sterge" color="error" @click="openModal(row.id)"/>
              <template #content>
              <div class="p-4">
                <p>Sigur vrei să ștergi această activitate?</p>
                <div class="flex gap-3 justify-end mt-4">
                  <button @click="showModal = false" class="px-4 py-2 bg-gray-300 rounded">
                    Anulează
                  </button>

                  <button @click="deleteActivity" class="px-4 py-2 bg-red-500 hover:translate-1 text-white rounded">
                    Șterge
                  </button>
                </div>
              </div>
              </template>
            </UModal>
          </td>

        </tr>
        <tr v-if="!props.content.length">
          <td
            :colspan="props.headers.length"
            class="px-6 py-4 text-gray-600 italic"
          >
            No data available
          </td>
        </tr>
      </tbody>
    </table>
  </div>

</template>
