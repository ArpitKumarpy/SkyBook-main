import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHead from '../components/PageHead'
import Loader from '../components/Loader'
import Alert from '../components/Alert'
import StatusBadge from '../components/StatusBadge'
import FlapChip from '../components/FlapChip'
import { aircraftApi } from '../api/aircraftApi'
import { userApi } from '../api/userApi'
import { flightApi } from '../api/flightApi'
import { scheduleApi } from '../api/scheduleApi'
import { bookingApi } from '../api/bookingApi'
import { ticketApi } from '../api/ticketApi'
import { formatDateTime } from '../utils/format'

export default function Dashboard() {
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [stats, setStats] = useState(null)
  const [recentBookings, setRecentBookings] = useState([])

  useEffect(() => {
    let alive = true
    async function load() {
      setLoading(true)
      setError(null)
      try {
        const [aircraft, users, flights, schedules, bookings, tickets] = await Promise.all([
          aircraftApi.getAll(),
          userApi.getAll(),
          flightApi.getAll(),
          scheduleApi.getAll(),
          bookingApi.getAll(),
          ticketApi.getAll(),
        ])
        if (!alive) return
        setStats({
          aircraft: aircraft.length,
          users: users.length,
          flights: flights.length,
          schedules: schedules.length,
          bookings: bookings.length,
          tickets: tickets.length,
        })
        setRecentBookings(
          [...bookings]
            .sort((a, b) => new Date(b.bookedAt) - new Date(a.bookedAt))
            .slice(0, 6)
        )
      } catch (err) {
        if (alive) setError(err.message)
      } finally {
        if (alive) setLoading(false)
      }
    }
    load()
    return () => {
      alive = false
    }
  }, [])

  return (
    <div>
      <PageHead
        eyebrow="Gate console"
        title="Operations overview"
        subtitle="Live counts pulled straight from the SkyBook API."
        actions={
          <Link to="/search" className="btn btn-accent">
            ⌕ Search flights
          </Link>
        }
      />

      {error && <Alert>{error}</Alert>}

      {loading ? (
        <Loader label="Pulling the board…" />
      ) : (
        <>
          <div className="stat-grid">
            <StatTile label="Aircraft" value={stats.aircraft} to="/aircraft" />
            <StatTile label="Flights" value={stats.flights} to="/flights" />
            <StatTile label="Schedules" value={stats.schedules} to="/schedules" />
            <StatTile label="Bookings" value={stats.bookings} to="/bookings" />
            <StatTile label="Tickets" value={stats.tickets} to="/tickets" />
            <StatTile label="Users" value={stats.users} to="/users" />
          </div>

          <div className="card" style={{ padding: '18px 20px' }}>
            <h3 style={{ margin: '0 0 14px', fontFamily: 'var(--font-display)', fontSize: 15 }}>
              Recent bookings
            </h3>
            {recentBookings.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', fontSize: 13.5 }}>No bookings yet.</p>
            ) : (
              <div>
                {recentBookings.map((b) => (
                  <div
                    key={b.id}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      padding: '10px 0',
                      borderBottom: '1px solid var(--border)',
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                      <FlapChip>{b.bookingReference}</FlapChip>
                      <span style={{ fontSize: 13, color: 'var(--text-muted)' }}>
                        {formatDateTime(b.bookedAt)}
                      </span>
                    </div>
                    <StatusBadge status={b.bookingStatus} />
                  </div>
                ))}
              </div>
            )}
            <div style={{ marginTop: 14 }}>
              <Link to="/bookings" className="btn btn-ghost btn-sm">
                View all bookings →
              </Link>
            </div>
          </div>
        </>
      )}
    </div>
  )
}

function StatTile({ label, value, to }) {
  return (
    <Link to={to} className="stat-tile" style={{ display: 'block' }}>
      <div className="label">{label}</div>
      <div className="value">{value}</div>
    </Link>
  )
}
