// frontend/server-simple.js
const express = require('express');
const path = require('path');
const app = express();
const PORT = process.env.PORT || 3000;

// Healthcheck
app.get('/health', (req, res) => {
  res.json({ status: 'OK' });
});

// Servir arquivos estáticos
app.use(express.static(__dirname));

// Todas as rotas vão para principal.html
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'principal.html'));
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server running on port ${PORT}`);
});