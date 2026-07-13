export default function EmptyState({ title, description, action }) {
  return (
    <div className="state-block">
      <span className="glyph">＋</span>
      <div className="title">{title}</div>
      {description && <div className="desc">{description}</div>}
      {action}
    </div>
  )
}
