const { createServer } = require('http')
const { readFileSync } = require('fs')

const server = createServer((req, res) => {
  if (req.url === '/') {
    const html = readFileSync('index.html', 'utf8')
    res.writeHead(200, { 'Content-Type': 'text/html' })
    res.end(html)
  } else {
    res.writeHead(404)
    res.end('Not Found')
  }
})

server.listen(3000, () => {
  console.log('Server running on http://localhost:3000')
})