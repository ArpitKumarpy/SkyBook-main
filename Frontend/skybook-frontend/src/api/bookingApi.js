import client from './client'

const BASE = '/api/bookings'

export const bookingApi = {
  getAll: () => client.get(BASE).then((r) => r.data),
  getById: (id) => client.get(`${BASE}/${id}`).then((r) => r.data),
  create: (payload) => client.post(BASE, payload).then((r) => r.data),
  cancel: (id) => client.patch(`${BASE}/${id}/cancel`).then((r) => r.data),
}
