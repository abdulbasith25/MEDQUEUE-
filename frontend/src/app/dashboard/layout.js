'use client';

import { useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { 
  LayoutDashboard, 
  Calendar, 
  Users, 
  User, 
  Settings, 
  LogOut, 
  Activity,
  Menu,
  X
} from 'lucide-react';
import { useAuthStore } from '@/store/useAuthStore';

export default function DashboardLayout({ children }) {
  const [collapsed, setCollapsed] = useState(false);
  const pathname = usePathname();
  const logout = useAuthStore((state) => state.logout);
  const user = useAuthStore((state) => state.user);

  const navItems = [
    { name: 'Dashboard', href: '/dashboard', icon: LayoutDashboard },
    { name: 'Appointments', href: '/dashboard/appointments', icon: Calendar },
    { name: 'Queue Monitor', href: '/dashboard/queue', icon: Activity },
    { name: 'Profile', href: '/dashboard/profile', icon: User },
  ];

  return (
    <div style={{ display: 'flex', minHeight: '100vh', background: 'var(--background)' }}>
      {/* Sidebar */}
      <aside style={{
        width: collapsed ? '80px' : '260px',
        borderRight: '1px solid var(--border)',
        height: '100vh',
        position: 'fixed',
        left: 0,
        top: 0,
        zIndex: 50,
        background: 'var(--surface)',
        transition: 'width 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        display: 'flex',
        flexDirection: 'column'
      }}>
        {/* Logo */}
        <div style={{
          padding: '2rem 1.5rem',
          display: 'flex',
          alignItems: 'center',
          gap: '0.75rem',
          overflow: 'hidden'
        }}>
          <Activity color="var(--primary)" size={32} style={{ flexShrink: 0 }} />
          {!collapsed && <span style={{ fontSize: '1.25rem', fontWeight: 700, fontFamily: 'Outfit' }}>MedQueue</span>}
        </div>

        {/* Links */}
        <nav style={{ flex: 1, padding: '0.5rem' }}>
          {navItems.map((item) => {
            const Icon = item.icon;
            const active = pathname === item.href;
            return (
              <Link
                key={item.href}
                href={item.href}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '1rem',
                  padding: '0.875rem 1rem',
                  borderRadius: '0.75rem',
                  color: active ? 'white' : 'var(--text-muted)',
                  background: active ? 'var(--primary)' : 'transparent',
                  marginBottom: '0.25rem',
                  transition: 'all 0.2s',
                  overflow: 'hidden',
                  whiteSpace: 'nowrap'
                }}
              >
                <Icon size={22} style={{ flexShrink: 0 }} />
                {!collapsed && <span>{item.name}</span>}
              </Link>
            );
          })}
        </nav>

        {/* Footer info */}
        <div style={{ padding: '1rem', borderTop: '1px solid var(--border)' }}>
          <button 
            onClick={() => setCollapsed(!collapsed)}
            style={{
              width: '100%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: collapsed ? 'center' : 'flex-start',
              gap: '1rem',
              padding: '0.75rem',
              borderRadius: '0.5rem',
              border: 'none',
              background: 'transparent',
              color: 'var(--text-muted)',
              cursor: 'pointer'
            }}
          >
            {collapsed ? <Menu size={20} /> : <X size={20} />}
            {!collapsed && <span>Collapse</span>}
          </button>
          
          <button 
            onClick={logout}
            style={{
              width: '100%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: collapsed ? 'center' : 'flex-start',
              gap: '1rem',
              padding: '0.75rem',
              borderRadius: '0.5rem',
              border: 'none',
              background: 'transparent',
              color: 'var(--error)',
              cursor: 'pointer',
              marginTop: '0.5rem'
            }}
          >
            <LogOut size={20} />
            {!collapsed && <span>Sign Out</span>}
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main style={{
        flex: 1,
        marginLeft: collapsed ? '80px' : '260px',
        transition: 'margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        padding: '2rem'
      }}>
        <header style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '2.5rem'
        }}>
          <div>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 600 }}>
              {navItems.find(i => i.href === pathname)?.name || 'Dashboard'}
            </h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Welcome back, {user?.username || 'User'}</p>
          </div>
          <div style={{
            width: '40px',
            height: '40px',
            borderRadius: '50%',
            background: 'var(--primary)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontWeight: 700
          }}>
            {user?.username?.charAt(0).toUpperCase() || 'U'}
          </div>
        </header>
        {children}
      </main>
    </div>
  );
}
