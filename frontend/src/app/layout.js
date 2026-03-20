import '../styles/globals.css';

export const metadata = {
  title: 'MedQueue | Smart Appointment System',
  description: 'A premium real-time appointment and queue management system.',
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="anonymous" />
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Outfit:wght@400;600;700&display=swap" rel="stylesheet" />
      </head>
      <body>
        <main>{children}</main>
      </body>
    </html>
  );
}
