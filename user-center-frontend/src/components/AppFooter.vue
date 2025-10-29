<template>
  <footer class="app-footer">
    <div class="footer-content">
      <p>© 2024 用户中心. All rights reserved.</p>
      <div class="footer-links">
        <a href="https://planet.code-nav.cn" target="_blank" rel="noreferrer">
          编程导航知识星球
        </a>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
// AppFooter 通用底部组件
</script>

<style scoped>
.app-footer {
  background: #f0f2f5;
  padding: 24px;
  text-align: center;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

.footer-content p {
  margin: 0 0 8px;
  color: #666;
}

.footer-links a {
  color: #1890ff;
  text-decoration: none;
  margin: 0 8px;
}

.footer-links a:hover {
  text-decoration: underline;
}
</style>