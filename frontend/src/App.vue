<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
// 移除store的使用，简化App.vue
</script>

<style>
#app {
  height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Helvetica Neue', sans-serif;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  background-color: #f5f7fa;
}
</style>