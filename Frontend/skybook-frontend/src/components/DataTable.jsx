import Loader from './Loader'
import EmptyState from './EmptyState'
import Alert from './Alert'

/**
 * columns: [{ key, header, render?(row) }]
 * rows: array of data objects
 */
export default function DataTable({
  columns,
  rows,
  keyField = 'id',
  loading,
  error,
  emptyTitle = 'Nothing here yet',
  emptyDescription,
  emptyAction,
  toolbar,
  countLabel,
}) {
  return (
    <div className="table-card">
      {toolbar && <div className="table-toolbar">{toolbar}</div>}
      {loading ? (
        <Loader />
      ) : error ? (
        <div style={{ padding: 18 }}>
          <Alert>{error}</Alert>
        </div>
      ) : rows.length === 0 ? (
        <EmptyState title={emptyTitle} description={emptyDescription} action={emptyAction} />
      ) : (
        <div className="table-scroll">
          <table className="data-table">
            <thead>
              <tr>
                {columns.map((col) => (
                  <th key={col.key} style={col.align === 'right' ? { textAlign: 'right' } : undefined}>
                    {col.header}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {rows.map((row) => (
                <tr key={row[keyField]}>
                  {columns.map((col) => (
                    <td key={col.key} style={col.align === 'right' ? { textAlign: 'right' } : undefined}>
                      {col.render ? col.render(row) : row[col.key]}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {countLabel && !loading && !error && rows.length > 0 && (
        <div className="table-toolbar" style={{ borderTop: '1px solid var(--border)', borderBottom: 'none' }}>
          <span className="count">{countLabel}</span>
        </div>
      )}
    </div>
  )
}
