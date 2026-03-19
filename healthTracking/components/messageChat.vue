<script setup>
//const messages = ref([
  //{
    //id, role parts
  //},
  //}
//])

const message=ref("");




</script>

<template>
  <div class="min-h-screen flex flex-col relative">
  <UChatMessages :messages="messages"/>

    <div class="absolute bottom-0 center bg-white">
      <form>
        <UInput v-model="message"/>
      </form>
    </div>
  </div>
</template>
