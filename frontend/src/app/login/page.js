import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { motion } from 'framer-motion';
import { Activity, UserRound, Lock, ArrowLeft } from 'lucide-react';
import { useAuthStore } from '@/store/useAuthStore';

export default function LoginPage() {
  const router = useRouter();
  const setAuth = useAuthStore((state) => state.setAuth);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    // Mock login for demo purposes
    setTimeout(() => {
      setAuth({ username: 'Demo User', role: 'ROLE_PATIENT', id: 1 }, 'mock-jwt-token');
      router.push('/dashboard');
    }, 1000);
  };
  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '2rem',
      position: 'relative'
    }}>
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="card"
        style={{
          width: '100%',
          maxWidth: '400px',
          padding: '2.5rem',
          position: 'relative',
          zIndex: 10
        }}
      >
        <Link href="/" style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem',
          color: 'var(--text-muted)',
          fontSize: '0.875rem',
          marginBottom: '2rem'
        }}>
          <ArrowLeft size={16} /> Back to Home
        </Link>

        <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
          <Activity color="var(--primary)" size={40} style={{ margin: '0 auto 1rem' }} />
          <h1 style={{ fontSize: '1.75rem', fontWeight: 700, fontFamily: 'Outfit' }}>Welcome Back</h1>
          <p style={{ color: 'var(--text-muted)' }}>Log in to manage your appointments.</p>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          <div style={{ position: 'relative' }}>
            <UserRound style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={18} />
            <input 
              type="text" 
              required
              placeholder="Username" 
              className="input-field" 
              style={{ paddingLeft: '3rem' }}
            />
          </div>

          <div style={{ position: 'relative' }}>
            <Lock style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={18} />
            <input 
              type="password" 
              required
              placeholder="Password" 
              className="input-field" 
              style={{ paddingLeft: '3rem' }}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Link href="#" style={{ fontSize: '0.875rem', color: 'var(--primary-light)' }}>Forgot Password?</Link>
          </div>

          <button type="submit" disabled={loading} className="btn-primary" style={{ marginTop: '0.5rem', padding: '1rem' }}>
            {loading ? 'Authenticating...' : 'Log In'}
          </button>
        </form>

        <p style={{ textAlign: 'center', marginTop: '1.5rem', color: 'var(--text-muted)', fontSize: '0.875rem' }}>
          Don't have an account? <Link href="/register" style={{ color: 'var(--primary-light)', fontWeight: 600 }}>Sign Up</Link>
        </p>
      </motion.div>
    </div>
  );
}
