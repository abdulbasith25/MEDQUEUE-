/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  // We'll add rewrites if we need to proxy the backend to avoid CORS during local dev
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'https://appointment-system-fdcp.onrender.com/api/:path*',
      },
      {
        source: '/ws/:path*',
        destination: 'https://appointment-system-fdcp.onrender.com/ws/:path*',
      },
    ];
  },
};

module.exports = nextConfig;
