'use client';

import { useState, useEffect, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Activity, 
  Users, 
  UserPlus, 
  Play, 
  Settings, 
  Search, 
  AlertCircle,
  Clock
} from 'lucide-react';
import { useAuthStore } from '@/store/useAuthStore';
import { createWebSocketClient } from '@/lib/websocket';

export default function QueuePage() {
  const { user } = useAuthStore();
  const [doctorId, setDoctorId] = useState('');
  const [isMonitoring, setIsMonitoring] = useState(false);
  const [queueData, setQueueData] = useState({
    currentToken: 0,
    nextWaitingToken: 0,
    totalWaiting: 0,
    lastUpdate: null
  });
  const [logs, setLogs] = useState([]);
  const [stompClient, setStompClient] = useState(null);

  const addLog = (message, type = 'info') => {
    setLogs(prev => [{ 
      id: Date.now(), 
      message, 
      type, 
      time: new Date().toLocaleTimeString() 
    }, ...prev].slice(0, 10));
  };

  const handleUpdate = useCallback((data) => {
    if (data.type === 'QUEUE_UPDATE') {
      setQueueData({
        currentToken: data.currentToken,
        nextWaitingToken: data.nextWaitingToken,
        totalWaiting: data.totalWaiting,
        lastUpdate: Date.now()
      });
      addLog(`Queue Advanced: Now serving Token #${data.currentToken}`, 'success');
    }
  }, []);

  const startMonitoring = () => {
    if (!doctorId) return;
    
    if (stompClient) {
      stompClient.deactivate();
    }

    const client = createWebSocketClient(
      () => {
        addLog(`Connected to Doctor #${doctorId} queue`, 'success');
        setIsMonitoring(true);
      },
      handleUpdate,
      doctorId
    );

    client.activate();
    setStompClient(client);
  };

  const callNextPatient = async () => {
    try {
      addLog('Advancing queue...', 'info');
      const response = await fetch('/api/appointments/next', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ doctorId: parseInt(doctorId) })
      });
      
      if (!response.ok) throw new Error('Failed to advance queue');
      
      addLog('Successfully updated queue', 'success');
    } catch (err) {
      addLog(err.message, 'error');
    }
  };

  useEffect(() => {
    return () => {
      if (stompClient) stompClient.deactivate();
    };
  }, [stompClient]);

  // If user is a doctor, we might want to auto-set their ID if available
  useEffect(() => {
    if (user?.role === 'ROLE_DOCTOR' && user?.id) {
       // setDoctorId(user.id.toString());
    }
  }, [user]);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      {/* Control Panel */}
      <div className="card" style={{ padding: '1.5rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <h3 style={{ fontSize: '1.25rem', marginBottom: '0.25rem' }}>Queue Live Control</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
              {isMonitoring ? `Active: Monitoring Doctor #${doctorId}` : 'Enter a Doctor ID to begin monitoring'}
            </p>
          </div>
          
          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <div style={{ position: 'relative' }}>
              <Search size={18} style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
              <input 
                type="number" 
                placeholder="Doctor ID" 
                value={doctorId}
                onChange={(e) => setDoctorId(e.target.value)}
                className="input-field" 
                style={{ width: '150px', paddingLeft: '2.5rem' }}
              />
            </div>
            <button onClick={startMonitoring} className="btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Play size={16} /> {isMonitoring ? 'Reset' : 'Start'}
            </button>
            
            {user?.role === 'ROLE_DOCTOR' && isMonitoring && (
              <button 
                onClick={callNextPatient}
                style={{ 
                  background: 'var(--success)', 
                  color: 'white', 
                  border: 'none', 
                  padding: '0.75rem 1.5rem', 
                  borderRadius: 'var(--radius-md)', 
                  fontWeight: 600,
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                  cursor: 'pointer'
                }}>
                <UserPlus size={18} /> Call Next
              </button>
            )}
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '2rem' }}>
        {/* Main Counter */}
        <div className="card" style={{ textAlign: 'center', position: 'relative', overflow: 'hidden' }}>
          {queueData.lastUpdate && (
            <motion.div 
              key={queueData.lastUpdate}
              initial={{ scale: 1.2, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              style={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(79, 70, 229, 0.05)', pointerEvents: 'none' }}
            />
          )}
          
          <p style={{ textTransform: 'uppercase', letterSpacing: '0.1em', color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '1rem' }}>
            Currently Serving
          </p>
          <motion.h2 
            key={queueData.currentToken}
            initial={{ y: 20, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            style={{ fontSize: '6rem', fontWeight: 800, margin: '1rem 0', fontFamily: 'Outfit' }}
          >
            {queueData.currentToken || '--'}
          </motion.h2>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(255,255,255,0.05)', padding: '0.5rem 1rem', borderRadius: '2rem', fontSize: '0.9rem' }}>
            <Clock size={16} color="var(--primary-light)" />
            Average wait: 12 mins
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {/* Next Token */}
          <div className="card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1.5rem' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Next in Line</p>
              <h4 style={{ fontSize: '2rem', fontWeight: 700 }}>{queueData.nextWaitingToken || '--'}</h4>
            </div>
            <Users size={32} color="var(--primary-light)" />
          </div>

          {/* Total Waiting */}
          <div className="card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1.5rem' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Total Waiting</p>
              <h4 style={{ fontSize: '2rem', fontWeight: 700 }}>{queueData.totalWaiting}</h4>
            </div>
            <Activity size={32} color="var(--success)" opacity={0.8} />
          </div>
        </div>
      </div>

      {/* Activity Log */}
      <div className="card">
        <h3 style={{ marginBottom: '1.5rem', fontSize: '1.25rem' }}>Live Activity Stream</h3>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', minHeight: '200px' }}>
          <AnimatePresence>
            {logs.length === 0 ? (
              <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-muted)', border: '1px dashed var(--border)', borderRadius: '1rem' }}>
                Waiting for system connection...
              </div>
            ) : logs.map((log) => (
              <motion.div 
                key={log.id}
                initial={{ opacity: 0, x: -10 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0 }}
                style={{ 
                  display: 'flex', 
                  alignItems: 'center', 
                  gap: '1rem', 
                  padding: '1rem', 
                  background: 'rgba(0,0,0,0.1)', 
                  borderRadius: 'var(--radius-md)',
                  borderLeft: `4px solid ${log.type === 'success' ? 'var(--success)' : log.type === 'error' ? 'var(--error)' : 'var(--primary)'}`
                }}
              >
                <div style={{ minWidth: '80px', fontSize: '0.75rem', color: 'var(--text-muted)' }}>[{log.time}]</div>
                <div style={{ flex: 1, fontSize: '0.9rem' }}>{log.message}</div>
                {log.type === 'error' && <AlertCircle size={16} color="var(--error)" />}
              </motion.div>
            ))}
          </AnimatePresence>
        </div>
      </div>
    </div>
  );
}
