export default function Loader({ label = 'Loading…' }) {
  return (
    <div className="state-block">
      <span className="glyph">···</span>
      <div className="desc">{label}</div>
    </div>
  )
}
