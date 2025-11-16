const express = require('express');
const path = require('path');
const app = express();
const PORT = process.env.PORT || 3000;

// Middleware importante para Railway
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Servir arquivos estáticos
app.use(express.static(__dirname, {
  index: false, // Não servir index.html automaticamente
  dotfiles: 'ignore'
}));

// Healthcheck endpoint
app.get('/health', (req, res) => {
  console.log('Healthcheck called');
  res.status(200).json({ 
    status: 'OK', 
    message: 'Frontend server is healthy',
    timestamp: new Date().toISOString()
  });
});

// Rota raiz - serve principal.html
app.get('/', (req, res) => {
  console.log('Root route accessed');
  res.sendFile(path.join(__dirname, 'principal.html'), (err) => {
    if (err) {
      console.error('Error serving principal.html:', err);
      res.status(500).send('Error loading application');
    }
  });
});

// Para arquivos HTML específicos
app.get('*.html', (req, res) => {
  const filePath = path.join(__dirname, req.path);
  res.sendFile(filePath, (err) => {
    if (err) {
      console.error('Error serving HTML:', req.path, err);
      res.status(404).send('Page not found');
    }
  });
});

// Para todas as outras rotas, servir principal.html (SPA)
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'principal.html'));
});

// Error handling
app.use((err, req, res, next) => {
  console.error('Server error:', err);
  res.status(500).json({ error: 'Internal server error' });
});

// Iniciar servidor
app.listen(PORT, '0.0.0.0', () => {
  console.log(`🚀 Frontend server running on port ${PORT}`);
  console.log(`📍 Healthcheck: http://0.0.0.0:${PORT}/health`);
  console.log(`📍 Main app: http://0.0.0.0:${PORT}/`);
});