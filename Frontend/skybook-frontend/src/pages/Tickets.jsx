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
import { ticketApi } from '../api/ticketApi'
import { bookingApi } from '../api/bookingApi'
import { formatDateTime } from '../utils/format'

export default function Tickets() {
  const { data: tickets, loading, error, refresh } = useResource(ticketApi.getAll)
  const { data: bookings } = useResource(bookingApi.getAll)

  const [modalOpen, setModalOpen] = useState(false)
  const [bookingId, setBookingId] = useState('')
  const [formError, setFormError] = useState(null)
  const [saving, setSaving] = useState(false)
  const [cancelTarget, setCancelTarget] = useState(null)

  const [lookupNumber, setLookupNumber] = useState('')
  const [lookupResult, setLookupResult] = useState(null)
  const [lookupError, setLookupError] = useState(null)
  const [lookingUp, setLookingUp] = useState(false)

  const bookingMap = useMemo(() => new Map(bookings.map((b) => [b.id, b])), [bookings])

  // Only confirmed, not-yet-ticketed bookings make sense to issue a ticket for.
  const ticketedBookingIds = useMemo(() => new Set(tickets.map((t) => t.bookingId)), [tickets])
  const bookingOptions = bookings
    .filter((b) => b.bookingStatus === 'CONFIRMED' && !ticketedBookingIds.has(b.id))
    .map((b) => ({ value: String(b.id), label: `${b.bookingReference} (#${b.id})` }))

  function openCreate() {
    setBookingId('')
    setFormError(null)
    setModalOpen(true)
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setSaving(true)
    setFormError(null)
    try {
      await ticketApi.create({ bookingId: Number(bookingId) })
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
      await ticketApi.cancel(cancelTarget.id)
      setCancelTarget(null)
      refresh()
    } catch (err) {
      setCancelTarget(null)
      alert(err.message)
    }
  }

  async function handleLookup(e) {
    e.preventDefault()
    setLookingUp(true)
    setLookupError(null)
    setLookupResult(null)
    try {
      const result = await ticketApi.getByNumber(lookupNumber.trim())
      setLookupResult(result)
    } catch (err) {
      setLookupError(err.message)
    } finally {
      setLookingUp(false)
    }
  }

  const columns = [
    { key: 'ticketNumber', header: 'Ticket', render: (row) => <FlapChip>{row.ticketNumber}</FlapChip> },
    { key: 'booking', header: 'Booking', render: (row) => bookingMap.get(row.bookingId)?.bookingReference || `#${row.bookingId}` },
    { key: 'ticketStatus', header: 'Status', render: (row) => <StatusBadge status={row.ticketStatus} /> },
    { key: 'issueDate', header: 'Issued', render: (row) => formatDateTime(row.issueDate) },
    {
      key: 'actions',
      header: '',
      align: 'right',
      render: (row) => (
        <div className="table-actions">
          {row.ticketStatus !== 'CANCELLED' && (
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
        eyebrow="Documents"
        title="Tickets"
        subtitle="Tickets issued against confirmed bookings."
        actions={
          <button className="btn btn-accent" onClick={openCreate}>
            + Issue ticket
          </button>
        }
      />

      <div className="card" style={{ padding: 18, marginBottom: 20 }}>
        <h3 style={{ margin: '0 0 12px', fontFamily: 'var(--font-display)', fontSize: 14.5 }}>
          Look up a ticket by number
        </h3>
        <form onSubmit={handleLookup} style={{ display: 'flex', gap: 10, alignItems: 'flex-end', flexWrap: 'wrap' }}>
          <div style={{ flex: '1 1 240px' }}>
            <FormField
              name="lookupNumber"
              label="Ticket number"
              value={lookupNumber}
              onChange={(_, v) => setLookupNumber(v)}
              placeholder="e.g. TCK-000123"
            />
          </div>
          <button className="btn btn-primary" type="submit" disabled={lookingUp || !lookupNumber.trim()}>
            {lookingUp ? 'Looking up…' : 'Look up'}
          </button>
        </form>
        {lookupError && (
          <div style={{ marginTop: 12 }}>
            <Alert>{lookupError}</Alert>
          </div>
        )}
        {lookupResult && (
          <div
            style={{
              marginTop: 12,
              display: 'flex',
              alignItems: 'center',
              gap: 14,
              padding: '12px 14px',
              background: 'var(--surface-sunken)',
              borderRadius: 'var(--radius-md)',
            }}
          >
            <FlapChip>{lookupResult.ticketNumber}</FlapChip>
            <StatusBadge status={lookupResult.ticketStatus} />
            <span style={{ fontSize: 13, color: 'var(--text-muted)' }}>
              Booking {bookingMap.get(lookupResult.bookingId)?.bookingReference || `#${lookupResult.bookingId}`} · Issued{' '}
              {formatDateTime(lookupResult.issueDate)}
            </span>
          </div>
        )}
      </div>

      <DataTable
        columns={columns}
        rows={tickets}
        loading={loading}
        error={error}
        emptyTitle="No tickets yet"
        emptyDescription="Issue a ticket for a confirmed booking."
        emptyAction={
          <button className="btn btn-primary" onClick={openCreate}>
            + Issue ticket
          </button>
        }
        countLabel={`${tickets.length} tickets`}
      />

      <Modal open={modalOpen} title="Issue ticket" onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit}>
          <Alert>{formError}</Alert>
          <div className="form-grid">
            <FormField
              label="Booking"
              name="bookingId"
              type="select"
              value={bookingId}
              onChange={(_, v) => setBookingId(v)}
              options={bookingOptions}
              placeholder={bookingOptions.length ? 'Select a confirmed booking…' : 'No eligible bookings'}
              required
              span2
              hint="Only confirmed bookings without an existing ticket are listed."
            />
          </div>
          <div className="form-actions">
            <button type="button" className="btn btn-ghost" onClick={() => setModalOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving || !bookingId}>
              {saving ? 'Issuing…' : 'Issue ticket'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!cancelTarget}
        title="Cancel ticket"
        message={`Cancel ticket "${cancelTarget?.ticketNumber}"? This can't be undone.`}
        confirmLabel="Cancel ticket"
        onConfirm={handleCancel}
        onCancel={() => setCancelTarget(null)}
      />
    </div>
  )
}
