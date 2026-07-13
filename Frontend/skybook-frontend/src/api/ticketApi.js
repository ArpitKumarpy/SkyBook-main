import client from './client'

const BASE = '/api/tickets'

export const ticketApi = {
  getAll: () => client.get(BASE).then((r) => r.data),
  getById: (id) => client.get(`${BASE}/${id}`).then((r) => r.data),
  getByNumber: (ticketNumber) => client.get(`${BASE}/number/${ticketNumber}`).then((r) => r.data),
  create: (payload) => client.post(BASE, payload).then((r) => r.data),
  cancel: (id) => client.delete(`${BASE}/${id}`),
}
