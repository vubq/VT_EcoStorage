<script setup lang="ts">
import { useAppStore } from '@/store'

const router = useRouter()
const appStore = useAppStore()

const name = import.meta.env.VITE_APP_NAME
</script>

<template>
  <div
    class="h-60px text-xl flex-center cursor-pointer gap-2 p-x-2"
    @click="router.push('/')"
  >
    <img src="/src/assets/vt_ecostorage_logo.png" alt="" class="text-1.5em" style="width: 50px;" />
    <span
      v-show="!appStore.collapsed"
      class="text-ellipsis overflow-hidden whitespace-nowrap"
    >{{ name }}</span>
  </div>
</template>

<style scoped></style>
