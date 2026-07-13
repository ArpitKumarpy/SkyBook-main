import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import FlightSearch from './pages/FlightSearch'
import Flights from './pages/Flights'
import Schedules from './pages/Schedules'
import Aircraft from './pages/Aircraft'
import Seats from './pages/Seats'
import Bookings from './pages/Bookings'
import Tickets from './pages/Tickets'
import Users from './pages/Users'
import NotFound from './pages/NotFound'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<Dashboard />} />
        <Route path="/search" element={<FlightSearch />} />
        <Route path="/flights" element={<Flights />} />
        <Route path="/schedules" element={<Schedules />} />
        <Route path="/aircraft" element={<Aircraft />} />
        <Route path="/seats" element={<Seats />} />
        <Route path="/bookings" element={<Bookings />} />
        <Route path="/tickets" element={<Tickets />} />
        <Route path="/users" element={<Users />} />
        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  )
}
