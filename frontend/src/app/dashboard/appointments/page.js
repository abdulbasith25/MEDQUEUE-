'use client';

import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Calendar, 
  Search, 
  User, 
  Clock, 
  CheckCircle2, 
  MapPin, 
  Stethoscope,
  ChevronRight,
  ArrowRight
} from 'lucide-react';

export default function AppointmentsPage() {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [bookingDate, setBookingDate] = useState(new Date().toISOString().split('T')[0]);
  const [isBooking, setIsBooking] = useState(false);
  const [bookingResult, setBookingResult] = useState(null);

  useEffect(() => {
    fetchDoctors();
  }, []);

  const fetchDoctors = async () => {
    try {
      const res = await fetch('/api/doctors');
      const data = await res.json();
      setDoctors(data);
    } catch (err) {
      console.error('Failed to fetch doctors', err);
    } finally {
      setLoading(false);
    }
  };

  const handleBook = async () => {
    if (!selectedDoctor) return;
    setIsBooking(true);
    try {
      const response = await fetch('/api/appointments/book', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          doctorId: selectedDoctor.id,
          patientId: 1, // Placeholder: in real app, get from AuthStore
          appointmentDate: bookingDate
        })
      });
      
      const result = await response.json();
      if (response.ok) {
        setBookingResult(result);
      } else {
        alert('Booking failed: ' + (result.message || 'Unknown error'));
      }
    } catch (err) {
      alert('Network error during booking');
    } finally {
      setIsBooking(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2.5rem' }}>
      {!bookingResult ? (
        <>
          {/* Header & Filter */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', flexWrap: 'wrap', gap: '1.5rem' }}>
            <div>
              <h1 style={{ fontSize: '2rem', fontWeight: 800, fontFamily: 'Outfit', marginBottom: '0.5rem' }}>Book Appointment</h1>
              <p style={{ color: 'var(--text-muted)' }}>Choose a specialist and secure your token instantly.</p>
            </div>
            
            <div style={{ display: 'flex', gap: '1rem' }}>
              <div className="input-field" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', width: 'auto' }}>
                <Calendar size={18} color="var(--primary-light)" />
                <input 
                  type="date" 
                  value={bookingDate}
                  onChange={(e) => setBookingDate(e.target.value)}
                  style={{ background: 'transparent', border: 'none', color: 'white', outline: 'none' }}
                />
              </div>
            </div>
          </div>

          {/* Doctors Grid */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '1.5rem' }}>
            {loading ? (
              [1, 2, 3].map(i => <div key={i} className="card" style={{ height: '180px', opacity: 0.5 }}>Loading...</div>)
            ) : doctors.map((doctor) => (
              <motion.div
                key={doctor.id}
                whileHover={{ y: -4 }}
                className="card"
                onClick={() => setSelectedDoctor(doctor)}
                style={{ 
                  cursor: 'pointer',
                  borderColor: selectedDoctor?.id === doctor.id ? 'var(--primary)' : 'var(--border)',
                  background: selectedDoctor?.id === doctor.id ? 'rgba(79, 70, 229, 0.05)' : 'var(--surface)',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '1rem'
                }}
              >
                <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-start' }}>
                  <div style={{ 
                    width: '60px', 
                    height: '60px', 
                    borderRadius: '1rem', 
                    background: 'var(--background)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center' 
                  }}>
                    <Stethoscope size={28} color="var(--primary-light)" />
                  </div>
                  <div style={{ flex: 1 }}>
                    <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Dr. {doctor.name} {doctor.degree && `(${doctor.degree})`}</h3>
                    <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>{doctor.specialization}</p>
                    <div style={{ 
                      display: 'inline-block', 
                      marginTop: '0.5rem',
                      padding: '0.25rem 0.5rem', 
                      background: doctor.available ? 'rgba(34, 197, 94, 0.1)' : 'rgba(239, 68, 68, 0.1)',
                      color: doctor.available ? 'var(--success)' : 'var(--error)',
                      borderRadius: '0.5rem',
                      fontSize: '0.7rem',
                      fontWeight: 700
                    }}>
                      {doctor.available ? 'AVAILABLE' : 'BUSY'}
                    </div>
                  </div>
                </div>
                
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '0.5rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: 'var(--text-muted)', fontSize: '0.8rem' }}>
                    <MapPin size={14} /> Main Clinic
                  </div>
                  <ChevronRight size={20} color="var(--border)" />
                </div>
              </motion.div>
            ))}
          </div>

          {/* Action Footer */}
          <AnimatePresence>
            {selectedDoctor && (
              <motion.div
                initial={{ opacity: 0, y: 50 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: 50 }}
                style={{
                  position: 'fixed',
                  bottom: '2rem',
                  left: '50%',
                  transform: 'translateX(-50%)',
                  width: 'calc(100% - 4rem)',
                  maxWidth: '800px',
                  zIndex: 100
                }}
              >
                <div className="glass" style={{
                  padding: '1.25rem 2.5rem',
                  borderRadius: 'var(--radius-xl)',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  boxShadow: '0 20px 40px -10px rgba(0,0,0,0.5)'
                }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
                    <div style={{ textAlign: 'left' }}>
                      <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Selected Doctor</p>
                      <p style={{ fontWeight: 700 }}>Dr. {selectedDoctor.name}</p>
                    </div>
                    <div style={{ width: '1px', height: '30px', background: 'var(--border)' }}></div>
                    <div style={{ textAlign: 'left' }}>
                      <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Date</p>
                      <p style={{ fontWeight: 700 }}>{new Date(bookingDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}</p>
                    </div>
                  </div>
                  <button 
                    onClick={handleBook}
                    disabled={isBooking}
                    className="btn-primary" 
                    style={{ padding: '0.875rem 2rem', fontSize: '1rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}
                  >
                    {isBooking ? 'Processing...' : 'Confirm Appointment'} <ArrowRight size={20} />
                  </button>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </>
      ) : (
        /* Success Screen */
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          className="card"
          style={{ maxWidth: '600px', margin: '4rem auto', textAlign: 'center', padding: '4rem 2rem' }}
        >
          <div style={{ 
            width: '80px', 
            height: '80px', 
            background: 'rgba(34, 197, 94, 0.1)', 
            borderRadius: '50%', 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center',
            margin: '0 auto 2rem'
          }}>
            <CheckCircle2 size={48} color="var(--success)" />
          </div>
          <h2 style={{ fontSize: '2.5rem', fontWeight: 800, fontFamily: 'Outfit', marginBottom: '1rem' }}>Success!</h2>
          <p style={{ color: 'var(--text-muted)', marginBottom: '3rem' }}>
            Your appointment with Dr. {selectedDoctor.name} is confirmed.
          </p>
          
          <div style={{ 
            background: 'var(--background)', 
            padding: '2rem', 
            borderRadius: 'var(--radius-lg)',
            border: '1px dashed var(--border)',
            marginBottom: '3rem'
          }}>
            <p style={{ textTransform: 'uppercase', fontSize: '0.8rem', letterSpacing: '0.1em', color: 'var(--text-muted)' }}>Your Token Number</p>
            <h1 style={{ fontSize: '5rem', fontWeight: 900, color: 'var(--primary-light)', margin: '1rem 0' }}>#{bookingResult.tokenNumber}</h1>
            <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                <Calendar size={16} /> {bookingDate}
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                <Clock size={16} /> 09:00 AM onwards
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
            <button onClick={() => setBookingResult(null)} style={{ background: 'transparent', border: '1px solid var(--border)', color: 'white', padding: '0.75rem 1.5rem', borderRadius: 'var(--radius-md)', fontWeight: 600 }}>Book Another</button>
            <button className="btn-primary">View in Dashboard</button>
          </div>
        </motion.div>
      )}
    </div>
  );
}
