import client from './client'

const BASE = '/api/flights'

export const flightApi = {
  getAll: () => client.get(BASE).then((r) => r.data),
  getById: (id) => client.get(`${BASE}/${id}`).then((r) => r.data),
  create: (payload) => client.post(BASE, payload).then((r) => r.data),
  update: (id, payload) => client.put(`${BASE}/${id}`, payload).then((r) => r.data),
  remove: (id) => client.delete(`${BASE}/${id}`),
  search: (source, destination, departureDate) =>
    client
      .get(`${BASE}/search`, { params: { source, destination, departureDate } })
      .then((r) => r.data),
}
