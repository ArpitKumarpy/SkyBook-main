import { NavLink } from 'react-router-dom'

const NAV = [
  {
    group: 'Overview',
    links: [{ to: '/', label: 'Dashboard', icon: '◆' }],
  },
  {
    group: 'Plan',
    links: [
      { to: '/search', label: 'Search flights', icon: '⌕' },
      { to: '/flights', label: 'Flights', icon: '✈' },
      { to: '/schedules', label: 'Schedules', icon: '▤' },
      { to: '/aircraft', label: 'Aircraft', icon: '⛁' },
      { to: '/seats', label: 'Seats', icon: '▦' },
    ],
  },
  {
    group: 'Sell',
    links: [
      { to: '/bookings', label: 'Bookings', icon: '⎘' },
      { to: '/tickets', label: 'Tickets', icon: '▮' },
      { to: '/users', label: 'Users', icon: '☺' },
    ],
  },
]

export default function Sidebar() {
  return (
    <nav className="sidebar">
      <div className="sidebar-brand">
        <span className="mark">✈</span>
        <span className="name">SkyBook</span>
        <span className="tag">OPS</span>
      </div>
      {NAV.map((section) => (
        <div key={section.group}>
          <div className="nav-group-label">{section.group}</div>
          {section.links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              end={link.to === '/'}
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            >
              <span className="ic">{link.icon}</span>
              {link.label}
            </NavLink>
          ))}
        </div>
      ))}
      <div className="sidebar-foot">SKYBOOK · GATE CONSOLE</div>
    </nav>
  )
}
