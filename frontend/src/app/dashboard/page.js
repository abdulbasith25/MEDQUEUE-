'use client';

import { motion } from 'framer-motion';
import { Calendar, Clock, Activity, Users, ArrowUpRight } from 'lucide-react';

export default function DashboardHome() {
  const stats = [
    { label: 'Upcoming', value: '3', icon: Calendar, color: 'var(--primary)' },
    { label: 'Wait Time', value: '12m', icon: Clock, color: 'var(--secondary)' },
    { label: 'Queue Pos', value: '#1', icon: Activity, color: 'var(--success)' },
    { label: 'Doctor', value: 'Dr. Smith', icon: Users, color: 'var(--warning)' },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      {/* Stats Grid */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '1.5rem'
      }}>
        {stats.map((stat, i) => {
          const Icon = stat.icon;
          return (
            <motion.div
              key={stat.label}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.1 }}
              className="card"
              style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div style={{
                  padding: '0.75rem',
                  borderRadius: '1rem',
                  background: `rgba(${stat.color === 'var(--primary)' ? '79, 70, 229' : '14, 165, 233'}, 0.1)`,
                  color: stat.color
                }}>
                  <Icon size={24} />
                </div>
                <button style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>
                  <ArrowUpRight size={18} />
                </button>
              </div>
              <div>
                <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '0.25rem' }}>{stat.label}</p>
                <h3 style={{ fontSize: '1.5rem', fontWeight: 700 }}>{stat.value}</h3>
              </div>
            </motion.div>
          );
        })}
      </div>

      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))',
        gap: '1.5rem'
      }}>
        {/* Active Queue Card */}
        <div className="card" style={{ flex: 2 }}>
          <h3 style={{ marginBottom: '1.5rem', fontSize: '1.25rem' }}>Live Queue Overview</h3>
          <div style={{
            padding: '2rem',
            background: 'rgba(0,0,0,0.2)',
            borderRadius: '1rem',
            textAlign: 'center',
            border: '1px dashed var(--border)'
          }}>
            <Activity size={48} color="var(--primary)" style={{ marginBottom: '1rem' }} />
            <p style={{ color: 'var(--text-muted)' }}>No active monitoring session. </p>
            <button className="btn-primary" style={{ marginTop: '1rem' }}>Open Monitor</button>
          </div>
        </div>

        {/* Upcoming Appointments */}
        <div className="card" style={{ flex: 1 }}>
          <h3 style={{ marginBottom: '1.5rem', fontSize: '1.25rem' }}>Recent Activity</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <ActivityItem 
              title="Appointment Confirmed" 
              time="2h ago" 
              desc="Dr. Ahmad approved your request."
            />
            <ActivityItem 
              title="Profile Updated" 
              time="5h ago" 
              desc="Changed primary contact info."
            />
            <ActivityItem 
              title="System Alert" 
              time="Yesterday" 
              desc="New specialty 'Dermatology' added."
            />
          </div>
        </div>
      </div>
    </div>
  );
}

function ActivityItem({ title, time, desc }) {
  return (
    <div style={{ display: 'flex', gap: '1rem', paddingBottom: '1rem', borderBottom: '1px solid var(--border)' }}>
      <div style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--primary)', marginTop: '0.5rem' }}></div>
      <div style={{ flex: 1 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <h4 style={{ fontSize: '0.95rem', fontWeight: 600 }}>{title}</h4>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{time}</span>
        </div>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{desc}</p>
      </div>
    </div>
  );
}
