import { useCallback, useEffect, useState } from 'react'

/**
 * Generic list-loading hook. Pass a loader function (usually api.getAll)
 * and it manages data/loading/error state and exposes a refresh() to re-fetch.
 */
export function useResource(loader, deps = []) {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const refresh = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const result = await loader()
      setData(result ?? [])
    } catch (err) {
      setError(err.message || 'Failed to load data.')
    } finally {
      setLoading(false)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps)

  useEffect(() => {
    refresh()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [refresh])

  return { data, loading, error, refresh, setData }
}
