import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import PageHead from '../components/PageHead'
import DataTable from '../components/DataTable'
import Modal from '../components/Modal'
import ConfirmDialog from '../components/ConfirmDialog'
import FormField from '../components/FormField'
import Alert from '../components/Alert'
import FlapChip from '../components/FlapChip'
import { useResource } from '../hooks/useResource'
import { scheduleApi } from '../api/scheduleApi'
import { flightApi } from '../api/flightApi'
import { formatDate, formatTime } from '../utils/format'

const EMPTY_FORM = { departureDate: '', departureTime: '', arrivalTime: '', flightId: '' }

export default function Schedules() {
  const [searchParams, setSearchParams] = useSearchParams()
  const flightFilter = searchParams.get('flightId') || ''
  const dateFilter = searchParams.get('date') || ''

  const { data: flights } = useResource(flightApi.getAll)
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  async function loadRows() {
    setLoading(true)
    setError(null)
    try {
      let result
      if (flightFilter) {
        result = await scheduleApi.getByFlight(flightFilter)
      } else if (dateFilter) {
        result = await scheduleApi.getByDate(dateFilter)
      } else {
        result = await scheduleApi.getAll()
      }
      setRows(result ?? [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadRows()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [flightFilter, dateFilter])

  const [modalOpen, setModalOpen] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(EMPTY_FORM)
  const [formError, setFormError] = useState(null)
  const [saving, setSaving] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState(null)

  const flightOptions = flights.map((f) => ({ value: String(f.id), label: `${f.flightNumber} · ${f.source} → ${f.destination}` }))

  function openCreate() {
    setEditingId(null)
    setForm({ ...EMPTY_FORM, flightId: flightFilter || '' })
    setFormError(null)
    setModalOpen(true)
  }

  function openEdit(row) {
    setEditingId(row.id)
    setForm({
      departureDate: row.departureDate,
      departureTime: row.departureTime?.slice(0, 5) || '',
      arrivalTime: row.arrivalTime?.slice(0, 5) || '',
      flightId: String(row.flightId),
    })
    setFormError(null)
    setModalOpen(true)
  }

  function handleChange(name, value) {
    setForm((f) => ({ ...f, [name]: value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setSaving(true)
    setFormError(null)
    try {
      const payload = {
        departureDate: form.departureDate,
        departureTime: form.departureTime,
        arrivalTime: form.arrivalTime,
        flightId: Number(form.flightId),
      }
      if (editingId) {
        await scheduleApi.update(editingId, payload)
      } else {
        await scheduleApi.create(payload)
      }
      setModalOpen(false)
      loadRows()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete() {
    try {
      await scheduleApi.remove(deleteTarget.id)
      setDeleteTarget(null)
      loadRows()
    } catch (err) {
      setDeleteTarget(null)
      alert(err.message)
    }
  }

  function clearFilters() {
    setSearchParams({})
  }

  const columns = [
    { key: 'flightNumber', header: 'Flight', render: (row) => <FlapChip>{row.flightNumber}</FlapChip> },
    { key: 'departureDate', header: 'Date', render: (row) => formatDate(row.departureDate) },
    { key: 'departureTime', header: 'Departs', render: (row) => formatTime(row.departureTime) },
    { key: 'arrivalTime', header: 'Arrives', render: (row) => formatTime(row.arrivalTime) },
    { key: 'availableSeats', header: 'Seats left' },
    {
      key: 'actions',
      header: '',
      align: 'right',
      render: (row) => (
        <div className="table-actions">
          <button className="btn btn-ghost btn-sm" onClick={() => openEdit(row)}>
            Edit
          </button>
          <button className="btn btn-danger btn-sm" onClick={() => setDeleteTarget(row)}>
            Delete
          </button>
        </div>
      ),
    },
  ]

  return (
    <div>
      <PageHead
        eyebrow="Timetable"
        title="Schedules"
        subtitle="Departures for each flight, with live seat availability."
        actions={
          <button className="btn btn-accent" onClick={openCreate}>
            + Add schedule
          </button>
        }
      />

      <DataTable
        columns={columns}
        rows={rows}
        loading={loading}
        error={error}
        emptyTitle="No schedules found"
        emptyDescription={
          flightFilter || dateFilter
            ? 'Nothing matches this filter.'
            : 'Add a departure date and time for one of your flights.'
        }
        emptyAction={
          <button className="btn btn-primary" onClick={openCreate}>
            + Add schedule
          </button>
        }
        countLabel={`${rows.length} schedule${rows.length === 1 ? '' : 's'}`}
        toolbar={
          <>
            <FormField
              name="filterFlight"
              type="select"
              value={flightFilter}
              onChange={(_, v) => setSearchParams(v ? { flightId: v } : {})}
              options={flightOptions}
              placeholder="Filter by flight…"
            />
            <FormField
              name="filterDate"
              type="date"
              value={dateFilter}
              onChange={(_, v) => setSearchParams(v ? { date: v } : {})}
            />
            {(flightFilter || dateFilter) && (
              <button className="btn btn-ghost btn-sm" onClick={clearFilters}>
                Clear filters
              </button>
            )}
          </>
        }
      />

      <Modal open={modalOpen} title={editingId ? 'Edit schedule' : 'Add schedule'} onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit}>
          <Alert>{formError}</Alert>
          <div className="form-grid">
            <FormField
              label="Flight"
              name="flightId"
              type="select"
              value={form.flightId}
              onChange={handleChange}
              options={flightOptions}
              placeholder={flightOptions.length ? 'Select flight…' : 'Add a flight first'}
              required
              span2
            />
            <FormField label="Departure date" name="departureDate" type="date" value={form.departureDate} onChange={handleChange} required />
            <FormField label="Departure time" name="departureTime" type="time" value={form.departureTime} onChange={handleChange} required />
            <FormField label="Arrival time" name="arrivalTime" type="time" value={form.arrivalTime} onChange={handleChange} required />
          </div>
          <div className="form-actions">
            <button type="button" className="btn btn-ghost" onClick={() => setModalOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : editingId ? 'Save changes' : 'Add schedule'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete schedule"
        message="Delete this schedule? This can't be undone."
        confirmLabel="Delete"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  )
}
