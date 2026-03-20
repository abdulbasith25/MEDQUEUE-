'use client';

import Link from 'next/link';
import { motion } from 'framer-motion';
import { Activity, Calendar, Shield, Users, ArrowRight } from 'lucide-react';

export default function LandingPage() {
  return (
    <div style={{ position: 'relative', minHeight: '100vh', overflow: 'hidden' }}>
      {/* Background Blobs */}
      <div style={{
        position: 'absolute',
        top: '-10%',
        right: '-10%',
        width: '500px',
        height: '500px',
        background: 'rgba(79, 70, 229, 0.15)',
        borderRadius: '50%',
        filter: 'blur(80px)',
        zIndex: 0
      }}></div>
      <div style={{
        position: 'absolute',
        bottom: '-10%',
        left: '-10%',
        width: '400px',
        height: '400px',
        background: 'rgba(14, 165, 233, 0.1)',
        borderRadius: '50%',
        filter: 'blur(80px)',
        zIndex: 0
      }}></div>

      {/* Navigation */}
      <nav style={{
        padding: '1.5rem 2rem',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        position: 'relative',
        zIndex: 10
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Activity color="#4f46e5" size={32} />
          <span style={{ fontSize: '1.5rem', fontWeight: 700, fontFamily: 'Outfit' }}>MedQueue</span>
        </div>
        <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'center' }}>
          <Link href="/login" style={{ fontWeight: 500, color: 'var(--text-muted)' }}>Login</Link>
          <Link href="/register" className="btn-primary">Get Started</Link>
        </div>
      </nav>

      {/* Hero Section */}
      <section style={{
        padding: '6rem 2rem',
        textAlign: 'center',
        maxWidth: '1200px',
        margin: '0 auto',
        position: 'relative',
        zIndex: 10
      }}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <h1 style={{
            fontSize: '4rem',
            fontWeight: 800,
            fontFamily: 'Outfit',
            lineHeight: 1.1,
            marginBottom: '1.5rem'
          }}>
            Smart Queue Management <br />
            <span className="gradient-text">For Modern Healthcare</span>
          </h1>
          <p style={{
            fontSize: '1.25rem',
            color: 'var(--text-muted)',
            maxWidth: '700px',
            margin: '0 auto 3rem',
            lineHeight: 1.6
          }}>
            Reduce wait times and improve patient satisfaction with our real-time 
            appointment tracking and live queue monitoring system.
          </p>

          <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem' }}>
            <Link href="/register" className="btn-primary" style={{ padding: '1rem 2rem', fontSize: '1.1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              Create Account <ArrowRight size={20} />
            </Link>
            <Link href="/login" style={{
              background: 'var(--surface)',
              border: '1px solid var(--border)',
              color: 'white',
              padding: '1rem 2rem',
              borderRadius: 'var(--radius-md)',
              fontWeight: 600,
              fontSize: '1.1rem'
            }}>
              View Live Demo
            </Link>
          </div>
        </motion.div>

        {/* Feature Grid */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
          gap: '2rem',
          marginTop: '6rem'
        }}>
          <FeatureCard 
            icon={<Calendar color="var(--primary-light)" />}
            title="Easy Booking"
            desc="Schedule appointments in seconds with any doctor of your choice."
          />
          <FeatureCard 
            icon={<Activity color="var(--primary-light)" />}
            title="Live Tracker"
            desc="Monitor your token status in real-time from anywhere."
          />
          <FeatureCard 
            icon={<Users color="var(--primary-light)" />}
            title="Multi-Role"
            desc="Distinct dashboards for Patients, Doctors, and Admin staff."
          />
          <FeatureCard 
            icon={<Shield color="var(--primary-light)" />}
            title="Secure Data"
            desc="Enterprise-grade security for patient and medical records."
          />
        </div>
      </section>

      {/* Footer */}
      <footer style={{
        padding: '4rem 2rem',
        textAlign: 'center',
        borderTop: '1px solid var(--border)',
        marginTop: '4rem',
        color: 'var(--text-muted)',
        fontSize: '0.9rem'
      }}>
        © 2026 MedQueue Systems. Built for high-performance medical facilities.
      </footer>
    </div>
  );
}

function FeatureCard({ icon, title, desc }) {
  return (
    <motion.div 
      whileHover={{ y: -5 }}
      className="card"
      style={{ textAlign: 'left', padding: '1.5rem' }}
    >
      <div style={{
        background: 'rgba(79, 70, 229, 0.1)',
        width: '48px',
        height: '48px',
        borderRadius: '12px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        marginBottom: '1.25rem'
      }}>
        {icon}
      </div>
      <h3 style={{ marginBottom: '0.5rem', fontSize: '1.25rem' }}>{title}</h3>
      <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>{desc}</p>
    </motion.div>
  );
}
