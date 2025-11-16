const express = require('express');
const path = require('path');
const fs = require('fs');
const app = express();
const PORT = process.env.PORT || 3000;

// Log middleware
app.use((req, res, next) => {
  console.log(`📨 ${req.method} ${req.url}`);
  next();
});

// Healthcheck
app.get('/health', (req, res) => {
  console.log('✅ Healthcheck called');
  res.status(200).json({ 
    status: 'OK', 
    message: 'Server is running',
    timestamp: new Date().toISOString()
  });
});

// Verificar se principal.html existe
app.get('/check-files', (req, res) => {
  const files = ['principal.html', 'login.html', 'cadastro.html'];
  const results = {};
  
  files.forEach(file => {
    const filePath = path.join(__dirname, file);
    results[file] = {
      exists: fs.existsSync(filePath),
      path: filePath
    };
  });
  
  res.json(results);
});

// Servir arquivos estáticos com fallback
app.use(express.static(__dirname, {
  index: false,
  dotfiles: 'ignore',
  fallthrough: true
}));


// Rota para arquivos HTML específicos
app.get('*.html', (req, res) => {
  const filePath = path.join(__dirname, req.path);
  console.log('📄 HTML request:', req.path, '->', filePath);
  
  if (!fs.existsSync(filePath)) {
    console.error('❌ HTML file not found:', filePath);
    return res.status(404).send('Page not found');
  }
  
  res.sendFile(filePath);
});

// Fallback para SPA
app.get('*', (req, res) => {
  console.log('🔄 SPA fallback for:', req.path);
  res.sendFile(path.join(__dirname, 'principal.html'));
});

// Error handling
app.use((err, req, res, next) => {
  console.error('💥 Server error:', err);
  res.status(500).json({ error: 'Internal server error' });
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`🚀 Frontend server running on port ${PORT}`);
  console.log(`📁 Current directory: ${__dirname}`);
  console.log(`📋 Files in directory:`, fs.readdirSync(__dirname).join(', '));
});