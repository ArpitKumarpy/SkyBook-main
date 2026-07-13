import { useState } from 'react'
import PageHead from '../components/PageHead'
import DataTable from '../components/DataTable'
import Modal from '../components/Modal'
import ConfirmDialog from '../components/ConfirmDialog'
import FormField from '../components/FormField'
import Alert from '../components/Alert'
import { useResource } from '../hooks/useResource'
import { userApi } from '../api/userApi'
import { formatDateTime } from '../utils/format'

const EMPTY_FORM = { firstName: '', lastName: '', email: '', phoneNumber: '', password: '' }

export default function Users() {
  const { data, loading, error, refresh } = useResource(userApi.getAll)

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
    setForm({
      firstName: row.firstName,
      lastName: row.lastName,
      email: row.email,
      phoneNumber: row.phoneNumber,
      password: '',
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
      if (editingId) {
        await userApi.update(editingId, form)
      } else {
        await userApi.create(form)
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
      await userApi.remove(deleteTarget.id)
      setDeleteTarget(null)
      refresh()
    } catch (err) {
      setDeleteTarget(null)
      alert(err.message)
    }
  }

  const columns = [
    { key: 'id', header: 'ID' },
    { key: 'name', header: 'Name', render: (row) => `${row.firstName} ${row.lastName}` },
    { key: 'email', header: 'Email' },
    { key: 'phoneNumber', header: 'Phone' },
    { key: 'createdAt', header: 'Joined', render: (row) => formatDateTime(row.createdAt) },
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
        eyebrow="Directory"
        title="Users"
        subtitle="Passengers who can be attached to a booking."
        actions={
          <button className="btn btn-accent" onClick={openCreate}>
            + Add user
          </button>
        }
      />

      <DataTable
        columns={columns}
        rows={data}
        loading={loading}
        error={error}
        emptyTitle="No users yet"
        emptyDescription="Add a user before creating bookings on their behalf."
        emptyAction={
          <button className="btn btn-primary" onClick={openCreate}>
            + Add user
          </button>
        }
        countLabel={`${data.length} users`}
      />

      <Modal open={modalOpen} title={editingId ? 'Edit user' : 'Add user'} onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit}>
          <Alert>{formError}</Alert>
          <div className="form-grid">
            <FormField label="First name" name="firstName" value={form.firstName} onChange={handleChange} required />
            <FormField label="Last name" name="lastName" value={form.lastName} onChange={handleChange} required />
            <FormField label="Email" name="email" type="email" value={form.email} onChange={handleChange} required span2 />
            <FormField label="Phone number" name="phoneNumber" value={form.phoneNumber} onChange={handleChange} required />
            <FormField
              label="Password"
              name="password"
              type="password"
              value={form.password}
              onChange={handleChange}
              required
              hint={editingId ? 'The API requires the password on every update.' : undefined}
            />
          </div>
          <div className="form-actions">
            <button type="button" className="btn btn-ghost" onClick={() => setModalOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : editingId ? 'Save changes' : 'Add user'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete user"
        message={`Delete "${deleteTarget?.firstName} ${deleteTarget?.lastName}"? This can't be undone.`}
        confirmLabel="Delete"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  )
}
