import Modal from './Modal'

export default function ConfirmDialog({ open, title, message, confirmLabel = 'Confirm', onConfirm, onCancel, danger = true }) {
  return (
    <Modal open={open} title={title} onClose={onCancel}>
      <p style={{ margin: '0 0 4px', fontSize: 14, color: 'var(--text-muted)' }}>{message}</p>
      <div className="form-actions">
        <button className="btn btn-ghost" onClick={onCancel}>
          Cancel
        </button>
        <button className={danger ? 'btn btn-danger' : 'btn btn-primary'} onClick={onConfirm}>
          {confirmLabel}
        </button>
      </div>
    </Modal>
  )
}
