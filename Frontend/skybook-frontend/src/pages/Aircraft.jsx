import { useState } from 'react'
import PageHead from '../components/PageHead'
import DataTable from '../components/DataTable'
import Modal from '../components/Modal'
import ConfirmDialog from '../components/ConfirmDialog'
import FormField from '../components/FormField'
import Alert from '../components/Alert'
import { useResource } from '../hooks/useResource'
import { aircraftApi } from '../api/aircraftApi'

const EMPTY_FORM = { modelNo: '', totalSeats: '' }

export default function Aircraft() {
  const { data, loading, error, refresh } = useResource(aircraftApi.getAll)

  const [modalOpen, setModalOpen] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(EMPTY_FORM)
  const [formError, setFormError] = useState(null)
  const [saving, setSaving] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState(null)

  function openCreate() {
    setEditingId(null)
    setForm(EMPTY_FORM)
    setFormError(null)
    setModalOpen(true)
  }

  function openEdit(row) {
    setEditingId(row.id)
    setForm({ modelNo: row.modelNo, totalSeats: String(row.totalSeats) })
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
      const payload = { modelNo: form.modelNo, totalSeats: Number(form.totalSeats) }
      if (editingId) {
        await aircraftApi.update(editingId, payload)
      } else {
        await aircraftApi.create(payload)
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
      await aircraftApi.remove(deleteTarget.id)
      setDeleteTarget(null)
      refresh()
    } catch (err) {
      setDeleteTarget(null)
      alert(err.message)
    }
  }

  const columns = [
    { key: 'id', header: 'ID' },
    { key: 'modelNo', header: 'Model' },
    { key: 'totalSeats', header: 'Total seats' },
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
        eyebrow="Fleet"
        title="Aircraft"
        subtitle="The aircraft models available to assign to flights and seat maps."
        actions={
          <button className="btn btn-accent" onClick={openCreate}>
            + Add aircraft
          </button>
        }
      />

      <DataTable
        columns={columns}
        rows={data}
        loading={loading}
        error={error}
        emptyTitle="No aircraft yet"
        emptyDescription="Add an aircraft model to start building flights and seat maps."
        emptyAction={
          <button className="btn btn-primary" onClick={openCreate}>
            + Add aircraft
          </button>
        }
        countLabel={`${data.length} aircraft`}
      />

      <Modal open={modalOpen} title={editingId ? 'Edit aircraft' : 'Add aircraft'} onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit}>
          <Alert>{formError}</Alert>
          <div className="form-grid">
            <FormField
              label="Model number"
              name="modelNo"
              value={form.modelNo}
              onChange={handleChange}
              placeholder="e.g. Boeing 737-800"
              required
              span2
            />
            <FormField
              label="Total seats"
              name="totalSeats"
              type="number"
              value={form.totalSeats}
              onChange={handleChange}
              placeholder="e.g. 180"
              required
            />
          </div>
          <div className="form-actions">
            <button type="button" className="btn btn-ghost" onClick={() => setModalOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : editingId ? 'Save changes' : 'Add aircraft'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete aircraft"
        message={`Delete "${deleteTarget?.modelNo}"? This can't be undone.`}
        confirmLabel="Delete"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  )
}
