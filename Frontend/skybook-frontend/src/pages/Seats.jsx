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
import { seatApi } from '../api/seatApi'
import { aircraftApi } from '../api/aircraftApi'
import { titleCase, SEAT_CLASSES, SEAT_TYPES } from '../utils/format'

const EMPTY_FORM = { seatNumber: '', seatClass: '', seatType: '', aircraftId: '' }

export default function Seats() {
  const [searchParams, setSearchParams] = useSearchParams()
  const aircraftFilter = searchParams.get('aircraftId') || ''

  const { data: aircraftList } = useResource(aircraftApi.getAll)
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  async function loadRows() {
    setLoading(true)
    setError(null)
    try {
      const result = aircraftFilter ? await seatApi.getByAircraft(aircraftFilter) : await seatApi.getAll()
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
  }, [aircraftFilter])

  const [modalOpen, setModalOpen] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(EMPTY_FORM)
  const [formError, setFormError] = useState(null)
  const [saving, setSaving] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState(null)

  const aircraftOptions = aircraftList.map((a) => ({ value: String(a.id), label: `${a.modelNo} (#${a.id})` }))
  const classOptions = SEAT_CLASSES.map((c) => ({ value: c, label: titleCase(c) }))
  const typeOptions = SEAT_TYPES.map((t) => ({ value: t, label: titleCase(t) }))

  function openCreate() {
    setEditingId(null)
    setForm({ ...EMPTY_FORM, aircraftId: aircraftFilter || '' })
    setFormError(null)
    setModalOpen(true)
  }

  function openEdit(row) {
    setEditingId(row.id)
    setForm({
      seatNumber: row.seatNumber,
      seatClass: row.seatClass,
      seatType: row.seatType,
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
        seatNumber: form.seatNumber,
        seatClass: form.seatClass,
        seatType: form.seatType,
        aircraftId: Number(form.aircraftId),
      }
      if (editingId) {
        await seatApi.update(editingId, payload)
      } else {
        await seatApi.create(payload)
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
      await seatApi.remove(deleteTarget.id)
      setDeleteTarget(null)
      loadRows()
    } catch (err) {
      setDeleteTarget(null)
      alert(err.message)
    }
  }

  const columns = [
    { key: 'seatNumber', header: 'Seat', render: (row) => <FlapChip>{row.seatNumber}</FlapChip> },
    { key: 'seatClass', header: 'Class', render: (row) => titleCase(row.seatClass) },
    { key: 'seatType', header: 'Type', render: (row) => titleCase(row.seatType) },
    { key: 'aircraftModel', header: 'Aircraft' },
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
        eyebrow="Cabin layout"
        title="Seats"
        subtitle="The seat map for each aircraft — class, type, and position."
        actions={
          <button className="btn btn-accent" onClick={openCreate}>
            + Add seat
          </button>
        }
      />

      <DataTable
        columns={columns}
        rows={rows}
        loading={loading}
        error={error}
        emptyTitle="No seats found"
        emptyDescription={aircraftFilter ? 'This aircraft has no seats yet.' : 'Add seats to an aircraft to build its cabin layout.'}
        emptyAction={
          <button className="btn btn-primary" onClick={openCreate}>
            + Add seat
          </button>
        }
        countLabel={`${rows.length} seat${rows.length === 1 ? '' : 's'}`}
        toolbar={
          <>
            <FormField
              name="filterAircraft"
              type="select"
              value={aircraftFilter}
              onChange={(_, v) => setSearchParams(v ? { aircraftId: v } : {})}
              options={aircraftOptions}
              placeholder="Filter by aircraft…"
            />
            {aircraftFilter && (
              <button className="btn btn-ghost btn-sm" onClick={() => setSearchParams({})}>
                Clear filter
              </button>
            )}
          </>
        }
      />

      <Modal open={modalOpen} title={editingId ? 'Edit seat' : 'Add seat'} onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit}>
          <Alert>{formError}</Alert>
          <div className="form-grid">
            <FormField label="Seat number" name="seatNumber" value={form.seatNumber} onChange={handleChange} placeholder="e.g. 12A" required />
            <FormField
              label="Aircraft"
              name="aircraftId"
              type="select"
              value={form.aircraftId}
              onChange={handleChange}
              options={aircraftOptions}
              placeholder={aircraftOptions.length ? 'Select aircraft…' : 'Add an aircraft first'}
              required
            />
            <FormField label="Class" name="seatClass" type="select" value={form.seatClass} onChange={handleChange} options={classOptions} required />
            <FormField label="Type" name="seatType" type="select" value={form.seatType} onChange={handleChange} options={typeOptions} required />
          </div>
          <div className="form-actions">
            <button type="button" className="btn btn-ghost" onClick={() => setModalOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : editingId ? 'Save changes' : 'Add seat'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete seat"
        message={`Delete seat "${deleteTarget?.seatNumber}"? This can't be undone.`}
        confirmLabel="Delete"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  )
}
