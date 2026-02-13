<script setup>
import { onMounted } from "vue";
import { initFlowbite } from "flowbite";
import { useRoute } from "vue-router";
import { useAuth } from "~/composable/useAuth";
import { today, getLocalTimeZone } from "@internationalized/date";

const route = useRoute();

const user = useAuth();

const date = ref(today(getLocalTimeZone()));
// initialize components based on data attribute selectors
onMounted(() => {
  console.log("usee", user);
  initFlowbite();
});

const isActive = (path) => route.path === path;
</script>

<template>
  <button
    data-drawer-target="logo-sidebar"
    data-drawer-toggle="logo-sidebar"
    aria-controls="logo-sidebar"
    type="button"
    class="fixed top-4 left-4 z-50 inline-flex items-center p-2 text-sm text-gray-500 rounded-lg sm:hidden hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-gray-200 dark:text-gray-400 dark:hover:bg-gray-700 dark:focus:ring-gray-600"
  >
    <span class="sr-only">Open sidebar</span>
    <svg
      class="w-6 h-6"
      aria-hidden="true"
      fill="currentColor"
      viewBox="0 0 20 20"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        clip-rule="evenodd"
        fill-rule="evenodd"
        d="M2 4.75A.75.75 0 012.75 4h14.5a.75.75 0 010 1.5H2.75A.75.75 0 012 4.75zm0 10.5a.75.75 0 01.75-.75h7.5a.75.75 0 010 1.5h-7.5a.75.75 0 01-.75-.75zM2 10a.75.75 0 01.75-.75h14.5a.75.75 0 010 1.5H2.75A.75.75 0 012 10z"
      ></path>
    </svg>
  </button>
  <aside
    id="logo-sidebar"
    class="fixed top-0 left-0 z-40 w-64 h-screen pt-10 transition-transform -translate-x-full bg-white border-r border-gray-200 sm:translate-x-0 dark:bg-gray-800 dark:border-gray-700"
    aria-label="Sidebar"
    data-drawer-backdrop="false"
    data-drawer-placement="left"
  >
    <div class="h-full px-3 pb-4 overflow-y-auto bg-white dark:bg-gray-800">
      <a href="/dashboard#summary" class="flex items-center ps-2.5 mb-5">
        <img
          src="https://flowbite.com/docs/images/logo.svg"
          class="h-6 me-3 sm:h-7"
          alt="Flowbite Logo"
        />
        <span
          class="self-center text-xl font-semibold whitespace-nowrap dark:text-white"
          >HealthCare</span
        >
      </a>
      <ul class="space-y-2 font-medium">
        <li>
          <a
            href="/dashboard"
            :class="[
              'flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 hover:text-black dark:hover:bg-gray-700 group',
              isActive('/dashboard')
                ? 'bg-green-500 text-white dark:bg-green-600'
                : '',
            ]"
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
                d="M9.143 4H4.857A.857.857 0 0 0 4 4.857v4.286c0 .473.384.857.857.857h4.286A.857.857 0 0 0 10 9.143V4.857A.857.857 0 0 0 9.143 4Zm10 0h-4.286a.857.857 0 0 0-.857.857v4.286c0 .473.384.857.857.857h4.286A.857.857 0 0 0 20 9.143V4.857A.857.857 0 0 0 19.143 4Zm-10 10H4.857a.857.857 0 0 0-.857.857v4.286c0 .473.384.857.857.857h4.286a.857.857 0 0 0 .857-.857v-4.286A.857.857 0 0 0 9.143 14Zm10 0h-4.286a.857.857 0 0 0-.857.857v4.286c0 .473.384.857.857.857h4.286a.857.857 0 0 0 .857-.857v-4.286a.857.857 0 0 0-.857-.857Z"
              />
            </svg>

            <span class="ms-3">Summary</span>
          </a>
        </li>
        <li>
          <a
            href="/activity"
            :class="[
              'flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group',
              isActive('/activity')
                ? 'bg-green-500 text-white dark:bg-green-600'
                : '',
            ]"
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
                d="M4 4.5V19a1 1 0 0 0 1 1h15M7 14l4-4 4 4 5-5m0 0h-3.207M20 9v3.207"
              />
            </svg>
            <span class="flex-1 ms-3 whitespace-nowrap">Activity</span>
          </a>
        </li>
        <li>
          <a
            href="/sleep"
            :class="[
              'flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group',
              isActive('/sleep')
                ? 'bg-green-500 text-white dark:bg-green-600'
                : '',
            ]"
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
                d="M18 17v2M12 5.5V10m-6 7v2m15-2v-4c0-1.6569-1.3431-3-3-3H6c-1.65685 0-3 1.3431-3 3v4h18Zm-2-7V8c0-1.65685-1.3431-3-3-3H8C6.34315 5 5 6.34315 5 8v2h14Z"
              />
            </svg>
            <span class="flex-1 ms-3 whitespace-nowrap">Sleep</span>
          </a>
        </li>
        <li>
          <a
            href="/alimentation"
            :class="[
              'flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group',
              isActive('/alimentation')
                ? 'bg-green-500 text-white dark:bg-green-600'
                : '',
            ]"
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
                stroke-miterlimit="10"
                stroke-width="2"
                d="M16.0001 7.99997c-1.629-1.629-3.9007-1.91488-6.06614.25057C7.76851 10.416 2.82151 18.0286 4.4505 19.6576c1.629 1.629 9.2416-3.318 11.4071-5.4834 2.0932-2.0933 1.7715-4.54524.1425-6.17423Zm0 0 3.2595-3.15144m-3.2595 3.15144V4m0 3.99997 4 .00002m-3.7978 5.80211-2.1999-2.2155M8.30216 10.211l2.07194 2.0421m-1.25873 3.1838 2.11173 2.1633"
              />
            </svg>

            <span class="flex-1 ms-3 whitespace-nowrap">Alimentation</span>
          </a>
        </li>
        <li>
          <a
            href="#"
            :class="[
              'flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group',
              isActive('/friends')
                ? 'bg-green-500 text-white dark:bg-green-600'
                : '',
            ]"
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
                stroke-width="2"
                d="M4.5 17H4a1 1 0 0 1-1-1 3 3 0 0 1 3-3h1m0-3.05A2.5 2.5 0 1 1 9 5.5M19.5 17h.5a1 1 0 0 0 1-1 3 3 0 0 0-3-3h-1m0-3.05a2.5 2.5 0 1 0-2-4.45m.5 13.5h-7a1 1 0 0 1-1-1 3 3 0 0 1 3-3h3a3 3 0 0 1 3 3 1 1 0 0 1-1 1Zm-1-9.5a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0Z"
              />
            </svg>

            <span class="flex-1 ms-3 whitespace-nowrap">Friend</span>
          </a>
        </li>
      </ul>
    </div>
  </aside>
</template>
