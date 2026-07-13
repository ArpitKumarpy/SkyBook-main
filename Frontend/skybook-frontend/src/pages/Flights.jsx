import { useState } from 'react'
import { Link } from 'react-router-dom'
import PageHead from '../components/PageHead'
import DataTable from '../components/DataTable'
import Modal from '../components/Modal'
import ConfirmDialog from '../components/ConfirmDialog'
import FormField from '../components/FormField'
import Alert from '../components/Alert'
import StatusBadge from '../components/StatusBadge'
import FlapChip from '../components/FlapChip'
import { useResource } from '../hooks/useResource'
import { flightApi } from '../api/flightApi'
import { aircraftApi } from '../api/aircraftApi'
import { formatCurrency, FLIGHT_STATUSES } from '../utils/format'

const EMPTY_FORM = {
  flightNumber: '',
  airlineName: '',
  source: '',
  destination: '',
  price: '',
  status: 'SCHEDULED',
  aircraftId: '',
}

export default function Flights() {
  const { data, loading, error, refresh } = useResource(flightApi.getAll)
  const { data: aircraftList } = useResource(aircraftApi.getAll)

  const [modalOpen, setModalOpen] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(EMPTY_FORM)
  const [formError, setFormError] = useState(null)
  const [saving, setSaving] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState(null)

  const aircraftOptions = aircraftList.map((a) => ({ value: String(a.id), label: `${a.modelNo} (#${a.id})` }))
  const statusOptions = FLIGHT_STATUSES.map((s) => ({ value: s, label: s }))

  function openCreate() {
    setEditingId(null)
    setForm(EMPTY_FORM)
    setFormError(null)
    setModalOpen(true)
  }

  function openEdit(row) {
    setEditingId(row.id)
    setForm({
      flightNumber: row.flightNumber,
      airlineName: row.airlineName,
      source: row.source,
      destination: row.destination,
      price: String(row.price),
      status: row.status,
      aircraftId: String(row.aircraftId),
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
        flightNumber: form.flightNumber,
        airlineName: form.airlineName,
        source: form.source,
        destination: form.destination,
        price: Number(form.price),
        status: form.status,
        aircraftId: Number(form.aircraftId),
      }
      if (editingId) {
        await flightApi.update(editingId, payload)
      } else {
        await flightApi.create(payload)
      }
      setModalOpen(false)
      refresh()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete() {
    try {
      await flightApi.remove(deleteTarget.id)
      setDeleteTarget(null)
      refresh()
    } catch (err) {
      setDeleteTarget(null)
      alert(err.message)
    }
  }

  const columns = [
    { key: 'flightNumber', header: 'Flight', render: (row) => <FlapChip>{row.flightNumber}</FlapChip> },
    { key: 'airlineName', header: 'Airline' },
    { key: 'route', header: 'Route', render: (row) => `${row.source} → ${row.destination}` },
    { key: 'aircraftModel', header: 'Aircraft' },
    { key: 'price', header: 'Price', render: (row) => formatCurrency(row.price) },
    { key: 'status', header: 'Status', render: (row) => <StatusBadge status={row.status} /> },
    {
      key: 'actions',
      header: '',
      align: 'right',
      render: (row) => (
        <div className="table-actions">
          <Link className="btn btn-ghost btn-sm" to={`/schedules?flightId=${row.id}`}>
            Schedules
          </Link>
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
        eyebrow="Network"
        title="Flights"
        subtitle="Routes flown, the aircraft assigned to them, and their base fare."
        actions={
          <button className="btn btn-accent" onClick={openCreate}>
            + Add flight
          </button>
        }
      />

      <DataTable
        columns={columns}
        rows={data}
        loading={loading}
        error={error}
        emptyTitle="No flights yet"
        emptyDescription="Add a flight and assign it an aircraft to start scheduling departures."
        emptyAction={
          <button className="btn btn-primary" onClick={openCreate}>
            + Add flight
          </button>
        }
        countLabel={`${data.length} flights`}
      />

      <Modal open={modalOpen} title={editingId ? 'Edit flight' : 'Add flight'} onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit}>
          <Alert>{formError}</Alert>
          <div className="form-grid">
            <FormField label="Flight number" name="flightNumber" value={form.flightNumber} onChange={handleChange} placeholder="e.g. SK101" required />
            <FormField label="Airline" name="airlineName" value={form.airlineName} onChange={handleChange} placeholder="e.g. SkyBook Air" required />
            <FormField label="Source" name="source" value={form.source} onChange={handleChange} placeholder="e.g. DEL" required />
            <FormField label="Destination" name="destination" value={form.destination} onChange={handleChange} placeholder="e.g. BOM" required />
            <FormField label="Price" name="price" type="number" step="0.01" value={form.price} onChange={handleChange} placeholder="e.g. 89.99" required />
            <FormField
              label="Status"
              name="status"
              type="select"
              value={form.status}
              onChange={handleChange}
              options={statusOptions}
              required
            />
            <FormField
              label="Aircraft"
              name="aircraftId"
              type="select"
              value={form.aircraftId}
              onChange={handleChange}
              options={aircraftOptions}
              placeholder={aircraftOptions.length ? 'Select aircraft…' : 'Add an aircraft first'}
              required
              span2
            />
          </div>
          <div className="form-actions">
            <button type="button" className="btn btn-ghost" onClick={() => setModalOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : editingId ? 'Save changes' : 'Add flight'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete flight"
        message={`Delete flight "${deleteTarget?.flightNumber}"? This can't be undone.`}
        confirmLabel="Delete"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  )
}
