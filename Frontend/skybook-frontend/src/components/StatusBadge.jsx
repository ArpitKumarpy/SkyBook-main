import { titleCase } from '../utils/format'

const TONE_MAP = {
  // Flight status
  SCHEDULED: 'info',
  DELAYED: 'warn',
  CANCELLED: 'danger',
  COMPLETED: 'neutral',
  // Booking status
  PENDING: 'warn',
  CONFIRMED: 'success',
  // Ticket status
  ISSUED: 'success',
  USED: 'neutral',
}

export default function StatusBadge({ status }) {
  if (!status) return <span className="badge badge-neutral">—</span>
  const tone = TONE_MAP[status] || 'neutral'
  return <span className={`badge badge-${tone}`}>{titleCase(status)}</span>
}
