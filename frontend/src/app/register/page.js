import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { motion } from 'framer-motion';
import { Activity, User, Mail, Lock, UserRound, ArrowLeft } from 'lucide-react';
import { useAuthStore } from '@/store/useAuthStore';

export default function RegisterPage() {
  const router = useRouter();
  const setAuth = useAuthStore((state) => state.setAuth);
  const [role, setRole] = useState('ROLE_PATIENT');
  const [loading, setLoading] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    setLoading(true);
    setTimeout(() => {
      setAuth({ 
        username: 'New User', 
        role: role, 
        id: role === 'ROLE_DOCTOR' ? 101 : 201 
      }, 'mock-jwt-token');
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
      {/* Background decoration */}
      <div style={{
        position: 'absolute',
        top: '20%',
        left: '10%',
        width: '300px',
        height: '300px',
        background: 'rgba(79, 70, 229, 0.05)',
        borderRadius: '50%',
        filter: 'blur(60px)',
      }}></div>

      <motion.div 
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="card"
        style={{
          width: '100%',
          maxWidth: '450px',
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
          <h1 style={{ fontSize: '1.75rem', fontWeight: 700, fontFamily: 'Outfit' }}>Create Account</h1>
          <p style={{ color: 'var(--text-muted)' }}>Join MedQueue and skip the line.</p>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {/* Role Selection */}
          <div style={{ display: 'flex', gap: '0.5rem', padding: '0.25rem', background: '#0f172a', borderRadius: 'var(--radius-md)' }}>
            {['ROLE_PATIENT', 'ROLE_DOCTOR'].map((r) => (
              <button
                key={r}
                type="button"
                onClick={() => setRole(r)}
                style={{
                  flex: 1,
                  padding: '0.5rem',
                  borderRadius: '0.5rem',
                  border: 'none',
                  fontSize: '0.875rem',
                  fontWeight: 600,
                  cursor: 'pointer',
                  background: role === r ? 'var(--primary)' : 'transparent',
                  color: role === r ? 'white' : 'var(--text-muted)',
                  transition: 'all 0.2s'
                }}
              >
                {r.split('_')[1]}
              </button>
            ))}
          </div>

          <div style={{ position: 'relative' }}>
            <User style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={18} />
            <input 
              type="text" 
              required
              placeholder="Full Name" 
              className="input-field" 
              style={{ paddingLeft: '3rem' }}
            />
          </div>

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

          <button type="submit" disabled={loading} className="btn-primary" style={{ marginTop: '0.5rem', padding: '1rem' }}>
            {loading ? 'Creating Account...' : 'Sign Up'}
          </button>
        </form>

        <p style={{ textAlign: 'center', marginTop: '1.5rem', color: 'var(--text-muted)', fontSize: '0.875rem' }}>
          Already have an account? <Link href="/login" style={{ color: 'var(--primary-light)', fontWeight: 600 }}>Log In</Link>
        </p>
      </motion.div>
    </div>
  );
}
