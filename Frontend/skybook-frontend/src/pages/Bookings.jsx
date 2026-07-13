import { useMemo, useState } from 'react'
import PageHead from '../components/PageHead'
import DataTable from '../components/DataTable'
import Modal from '../components/Modal'
import ConfirmDialog from '../components/ConfirmDialog'
import FormField from '../components/FormField'
import Alert from '../components/Alert'
import StatusBadge from '../components/StatusBadge'
import FlapChip from '../components/FlapChip'
import { useResource } from '../hooks/useResource'
import { bookingApi } from '../api/bookingApi'
import { userApi } from '../api/userApi'
import { scheduleApi } from '../api/scheduleApi'
import { flightApi } from '../api/flightApi'
import { seatApi } from '../api/seatApi'
import { formatDateTime, formatCurrency } from '../utils/format'

const EMPTY_FORM = { userId: '', scheduleId: '', seatId: '' }

export default function Bookings() {
  const { data: bookings, loading, error, refresh } = useResource(bookingApi.getAll)
  const { data: users } = useResource(userApi.getAll)
  const { data: schedules } = useResource(scheduleApi.getAll)
  const { data: flights } = useResource(flightApi.getAll)
  const { data: seats } = useResource(seatApi.getAll)

  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(EMPTY_FORM)
  const [formError, setFormError] = useState(null)
  const [saving, setSaving] = useState(false)
  const [cancelTarget, setCancelTarget] = useState(null)

  const userMap = useMemo(() => new Map(users.map((u) => [u.id, u])), [users])
  const scheduleMap = useMemo(() => new Map(schedules.map((s) => [s.id, s])), [schedules])
  const flightMap = useMemo(() => new Map(flights.map((f) => [f.id, f])), [flights])
  const seatMap = useMemo(() => new Map(seats.map((s) => [s.id, s])), [seats])

  const userOptions = users.map((u) => ({ value: String(u.id), label: `${u.firstName} ${u.lastName} (#${u.id})` }))
  const scheduleOptions = schedules.map((s) => ({
    value: String(s.id),
    label: `${s.flightNumber} · ${s.departureDate} ${s.departureTime?.slice(0, 5)} · ${s.availableSeats} left`,
  }))

  // Narrow the seat picker to seats belonging to the aircraft flying the selected schedule.
  const seatOptions = useMemo(() => {
    if (!form.scheduleId) return []
    const schedule = scheduleMap.get(Number(form.scheduleId))
    if (!schedule) return []
    const flight = flightMap.get(schedule.flightId)
    if (!flight) return []
    return seats
      .filter((s) => s.aircraftId === flight.aircraftId)
      .map((s) => ({ value: String(s.id), label: `${s.seatNumber} · ${s.seatClass} · ${s.seatType}` }))
  }, [form.scheduleId, scheduleMap, flightMap, seats])

  function openCreate() {
    setForm(EMPTY_FORM)
    setFormError(null)
    setModalOpen(true)
  }

  function handleChange(name, value) {
    setForm((f) => {
      const next = { ...f, [name]: value }
      if (name === 'scheduleId') next.seatId = ''
      return next
    })
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setSaving(true)
    setFormError(null)
    try {
      await bookingApi.create({
        userId: Number(form.userId),
        scheduleId: Number(form.scheduleId),
        seatId: Number(form.seatId),
      })
      setModalOpen(false)
      refresh()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleCancel() {
    try {
      await bookingApi.cancel(cancelTarget.id)
      setCancelTarget(null)
      refresh()
    } catch (err) {
      setCancelTarget(null)
      alert(err.message)
    }
  }

  const columns = [
    { key: 'bookingReference', header: 'Reference', render: (row) => <FlapChip>{row.bookingReference}</FlapChip> },
    { key: 'user', header: 'Passenger', render: (row) => {
      const u = userMap.get(row.userId)
      return u ? `${u.firstName} ${u.lastName}` : `User #${row.userId}`
    } },
    { key: 'flight', header: 'Flight', render: (row) => {
      const sch = scheduleMap.get(row.scheduleId)
      return sch ? sch.flightNumber : `Schedule #${row.scheduleId}`
    } },
    { key: 'seat', header: 'Seat', render: (row) => {
      const seat = seatMap.get(row.seatId)
      return seat ? seat.seatNumber : `Seat #${row.seatId}`
    } },
    { key: 'totalAmount', header: 'Amount', render: (row) => formatCurrency(row.totalAmount) },
    { key: 'bookingStatus', header: 'Status', render: (row) => <StatusBadge status={row.bookingStatus} /> },
    { key: 'bookedAt', header: 'Booked', render: (row) => formatDateTime(row.bookedAt) },
    {
      key: 'actions',
      header: '',
      align: 'right',
      render: (row) => (
        <div className="table-actions">
          {row.bookingStatus !== 'CANCELLED' && (
            <button className="btn btn-danger btn-sm" onClick={() => setCancelTarget(row)}>
              Cancel
            </button>
          )}
        </div>
      ),
    },
  ]

  return (
    <div>
      <PageHead
        eyebrow="Reservations"
        title="Bookings"
        subtitle="Every seat reserved against a schedule, and who reserved it."
        actions={
          <button className="btn btn-accent" onClick={openCreate}>
            + New booking
          </button>
        }
      />

      <DataTable
        columns={columns}
        rows={bookings}
        loading={loading}
        error={error}
        emptyTitle="No bookings yet"
        emptyDescription="Create a booking by pairing a user with a scheduled seat."
        emptyAction={
          <button className="btn btn-primary" onClick={openCreate}>
            + New booking
          </button>
        }
        countLabel={`${bookings.length} bookings`}
      />

      <Modal open={modalOpen} title="New booking" onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit}>
          <Alert>{formError}</Alert>
          <div className="form-grid">
            <FormField
              label="User"
              name="userId"
              type="select"
              value={form.userId}
              onChange={handleChange}
              options={userOptions}
              placeholder={userOptions.length ? 'Select user…' : 'Add a user first'}
              required
              span2
            />
            <FormField
              label="Schedule"
              name="scheduleId"
              type="select"
              value={form.scheduleId}
              onChange={handleChange}
              options={scheduleOptions}
              placeholder={scheduleOptions.length ? 'Select a departure…' : 'Add a schedule first'}
              required
              span2
            />
            <FormField
              label="Seat"
              name="seatId"
              type="select"
              value={form.seatId}
              onChange={handleChange}
              options={seatOptions}
              placeholder={form.scheduleId ? (seatOptions.length ? 'Select seat…' : 'No seats for this aircraft') : 'Pick a schedule first'}
              required
              disabled={!form.scheduleId}
              span2
            />
          </div>
          <div className="form-actions">
            <button type="button" className="btn btn-ghost" onClick={() => setModalOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Booking…' : 'Create booking'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!cancelTarget}
        title="Cancel booking"
        message={`Cancel booking "${cancelTarget?.bookingReference}"? The seat will be released.`}
        confirmLabel="Cancel booking"
        onConfirm={handleCancel}
        onCancel={() => setCancelTarget(null)}
      />
    </div>
  )
}
