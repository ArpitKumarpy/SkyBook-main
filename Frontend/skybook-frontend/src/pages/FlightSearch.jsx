import { useState } from 'react'
import { Link } from 'react-router-dom'
import PageHead from '../components/PageHead'
import FormField from '../components/FormField'
import Alert from '../components/Alert'
import Loader from '../components/Loader'
import EmptyState from '../components/EmptyState'
import StatusBadge from '../components/StatusBadge'
import FlapChip from '../components/FlapChip'
import { flightApi } from '../api/flightApi'
import { formatDate, formatTime, formatCurrency } from '../utils/format'

const TODAY = new Date().toISOString().slice(0, 10)

export default function FlightSearch() {
  const [form, setForm] = useState({ source: '', destination: '', departureDate: TODAY })
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [searched, setSearched] = useState(false)

  function handleChange(name, value) {
    setForm((f) => ({ ...f, [name]: value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setSearched(true)
    try {
      const data = await flightApi.search(form.source.trim(), form.destination.trim(), form.departureDate)
      setResults(data)
    } catch (err) {
      setError(err.message)
      setResults(null)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <PageHead eyebrow="Book a trip" title="Search flights" />

      <div className="search-hero">
        <p className="page-eyebrow">Where to</p>
        <h2 className="page-title" style={{ fontSize: 20 }}>
          Find a departure by route and date
        </h2>
        <form className="search-form" onSubmit={handleSubmit}>
          <FormField label="From" name="source" value={form.source} onChange={handleChange} placeholder="e.g. DEL" required />
          <FormField label="To" name="destination" value={form.destination} onChange={handleChange} placeholder="e.g. BOM" required />
          <FormField label="Departure date" name="departureDate" type="date" value={form.departureDate} onChange={handleChange} required />
          <button className="btn btn-accent" type="submit" disabled={loading}>
            {loading ? 'Searching…' : '⌕ Search'}
          </button>
        </form>
      </div>

      {error && <Alert>{error}</Alert>}

      {loading ? (
        <Loader label="Checking the board…" />
      ) : searched && results && results.length === 0 ? (
        <EmptyState
          title="No flights found"
          description="Try a different route or date — or add a flight and schedule for it."
        />
      ) : results && results.length > 0 ? (
        <div className="table-card">
          <div className="table-toolbar">
            <span className="count">{results.length} matching departures</span>
          </div>
          {results.map((r) => (
            <div className="result-row" key={`${r.flightId}-${r.departureDate}-${r.departureTime}`}>
              <div>
                <div className="airline">{r.airlineName}</div>
                <div className="route">
                  {r.source} → {r.destination}
                </div>
              </div>
              <div>
                <FlapChip>{r.flightNumber}</FlapChip>
              </div>
              <div className="route">
                {formatDate(r.departureDate)}
                <br />
                {formatTime(r.departureTime)} – {formatTime(r.arrivalTime)}
              </div>
              <div>
                <StatusBadge status={r.status} />
              </div>
              <div className="route">{r.availableSeats} seats left</div>
              <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <Link className="btn btn-primary btn-sm" to={`/schedules?flightId=${r.flightId}`}>
                  View & book
                </Link>
              </div>
              <div className="price" style={{ gridColumn: '1 / -1', textAlign: 'right', marginTop: -4 }}>
                {formatCurrency(r.price)}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <EmptyState
          title="Search for a route"
          description="Enter a source, destination, and date to see available departures."
        />
      )}
    </div>
  )
}
