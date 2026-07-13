import client from './client'

const BASE = '/api/seats'

export const seatApi = {
  getAll: () => client.get(BASE).then((r) => r.data),
  getById: (id) => client.get(`${BASE}/${id}`).then((r) => r.data),
  create: (payload) => client.post(BASE, payload).then((r) => r.data),
  update: (id, payload) => client.put(`${BASE}/${id}`, payload).then((r) => r.data),
  remove: (id) => client.delete(`${BASE}/${id}`),
  getByAircraft: (aircraftId) => client.get(`${BASE}/aircraft/${aircraftId}`).then((r) => r.data),
}
