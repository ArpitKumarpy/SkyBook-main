import { Link } from 'react-router-dom'
import EmptyState from '../components/EmptyState'

export default function NotFound() {
  return (
    <EmptyState
      title="404 — Gate not found"
      description="That route doesn't exist in SkyBook."
      action={
        <Link className="btn btn-primary" to="/">
          Back to dashboard
        </Link>
      }
    />
  )
}
