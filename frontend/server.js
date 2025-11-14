const express = require('express');
const path = require('path');
const app = express();
const PORT = process.env.PORT || 3000;

// Servir arquivos estáticos da pasta frontend
app.use(express.static(__dirname));

// Healthcheck
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'OK', timestamp: new Date().toISOString() });
});

// Rota principal - serve principal.html
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'principal.html'));
});

// Para todas as outras rotas, tenta servir o arquivo HTML correspondente
app.get('*', (req, res) => {
  const requestedPath = req.path;
  // Se a rota termina com .html, tenta servir o arquivo
  if (requestedPath.endsWith('.html')) {
    res.sendFile(path.join(__dirname, requestedPath));
  } else {
    // Caso contrário, redireciona para principal.html (SPA behavior)
    res.sendFile(path.join(__dirname, 'principal.html'));
  }
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`🚀 Frontend server running on port ${PORT}`);
});