export function formatDateTime(value) {
  if (!value) return '—'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value
  return d.toLocaleString(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  })
}

export function formatDate(value) {
  if (!value) return '—'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value
  return d.toLocaleDateString(undefined, { dateStyle: 'medium' })
}

export function formatTime(value) {
  if (!value) return '—'
  // LocalTime from Spring serializes as "HH:mm:ss"
  return value.slice(0, 5)
}

export function formatCurrency(value) {
  if (value === null || value === undefined || value === '') return '—'
  const n = Number(value)
  if (Number.isNaN(n)) return value
  return n.toLocaleString(undefined, { style: 'currency', currency: 'USD' })
}

export function titleCase(value) {
  if (!value) return '—'
  return String(value)
    .toLowerCase()
    .split('_')
    .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
    .join(' ')
}

export const FLIGHT_STATUSES = ['SCHEDULED', 'DELAYED', 'CANCELLED', 'COMPLETED']
export const SEAT_CLASSES = ['ECONOMY', 'PREMIUM_ECONOMY', 'BUSINESS', 'FIRST']
export const SEAT_TYPES = ['WINDOW', 'MIDDLE', 'AISLE']
export const BOOKING_STATUSES = ['PENDING', 'CONFIRMED', 'CANCELLED']
export const TICKET_STATUSES = ['ISSUED', 'CANCELLED', 'USED']
