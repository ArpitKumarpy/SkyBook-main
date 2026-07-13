import client from './client'

const BASE = '/api/schedules'

export const scheduleApi = {
  getAll: () => client.get(BASE).then((r) => r.data),
  getById: (id) => client.get(`${BASE}/${id}`).then((r) => r.data),
  create: (payload) => client.post(BASE, payload).then((r) => r.data),
  update: (id, payload) => client.put(`${BASE}/${id}`, payload).then((r) => r.data),
  remove: (id) => client.delete(`${BASE}/${id}`),
  getByFlight: (flightId) => client.get(`${BASE}/flight/${flightId}`).then((r) => r.data),
  getByDate: (departureDate) =>
    client.get(`${BASE}/date`, { params: { departureDate } }).then((r) => r.data),
}
