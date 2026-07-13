import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export const client = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Normalizes Spring's ErrorResponse { timestamp, status, error, message }
// into a plain string so components don't need to know the shape.
client.interceptors.response.use(
  (response) => response,
  (error) => {
    const data = error.response?.data
    const message =
      data?.message || data?.error || error.message || 'Something went wrong. Please try again.'
    return Promise.reject(new Error(message))
  }
)

export default client
