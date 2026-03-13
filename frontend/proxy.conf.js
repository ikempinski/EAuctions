/**
 * Proxy so session cookie works: browser -> localhost:4200 -> proxy -> localhost:8080.
 * - Rewrite Set-Cookie in response so cookie is stored for localhost.
 * - Explicitly forward Cookie header so backend always receives the session.
 */
module.exports = {
  '/api': {
    target: 'http://localhost:8080',
    secure: false,
    changeOrigin: false,
    on: {
      proxyReq: (proxyReq, req) => {
        // Ensure Cookie from browser is forwarded to backend (some proxies drop it)
        if (req.headers.cookie) {
          proxyReq.setHeader('Cookie', req.headers.cookie);
        }
      },
      proxyRes: (proxyRes, req, res) => {
        const setCookie = proxyRes.headers['set-cookie'];
        if (setCookie) {
          proxyRes.headers['set-cookie'] = setCookie.map((cookie) =>
            cookie
              .replace(/;\s*Domain=[^;]+/gi, '; Domain=localhost')
              .replace(/;\s*Secure/gi, '')
          );
        }
      },
    },
  },
};
