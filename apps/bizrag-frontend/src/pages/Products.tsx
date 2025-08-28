import React, { useEffect, useRef, useState } from 'react'
import { listAllProducts, patchProduct } from '../lib/products'
import type { ProductDto } from '../lib/products'

type RowState = 'idle'|'saving'|'saved'|'error'
const toNum = (v: string): number | undefined => (v.trim() === '' ? undefined : Number(v))

export default function Products() {
  const [rows, setRows] = useState<ProductDto[]>([])
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState<string | null>(null)

  const [rowState, setRowState] = useState<Record<string, RowState>>({})
  const [buf, setBuf] = useState<Record<string, Partial<ProductDto>>>({})
  const timers = useRef<Record<string, ReturnType<typeof setTimeout>>>({})

  async function load() {
    try {
      setLoading(true)
      setLoadError(null)
      const arr = await listAllProducts()
      setRows(Array.isArray(arr) ? arr : [])
    } catch (e:any) {
      setRows([])
      setLoadError(e?.message || 'Failed to load products')
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => { load() }, [])

  function queueSave(row: ProductDto) {
    const id = row.id
    clearTimeout(timers.current[id])
    timers.current[id] = setTimeout(async () => {
      const delta = buf[id]
      if (!delta || Object.keys(delta).length === 0) return
      setRowState(s => ({ ...s, [id]: 'saving' }))
      try {
        const upd = await patchProduct(id, delta)
        setRows(rs => rs.map(r => (r.id === id ? upd : r)))
        setBuf(b => { const { [id]: _omit, ...rest } = b; return rest })
        setRowState(s => ({ ...s, [id]: 'saved' }))
        setTimeout(() => setRowState(s => ({ ...s, [id]: 'idle' })), 1200)
      } catch {
        setRowState(s => ({ ...s, [id]: 'error' }))
      }
    }, 1000)
  }

  function onChange(id: string, field: keyof ProductDto, value: any) {
    setBuf(b => ({ ...b, [id]: { ...(b[id] || {}), [field]: value } }))
  }
  const onBlur = (row: ProductDto) => queueSave(row)

  return (
    <div className="container" style={{display:"flex", flexDirection:"column"}}>
      <h1>Products</h1>

      <div style={{ display:'flex', gap:8, marginBottom:12 }}>
        <button onClick={load}>Refresh</button>
        {loading && <span>Loading…</span>}
        {loadError && <span style={{color:'crimson'}}>{loadError}</span>}
      </div>

      {(!rows || rows.length === 0) && !loading ? (
        <div>No products yet.</div>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>SKU</th>
              <th>Title</th>
              <th>Description</th>
              <th className="num">Price</th>
              <th>Currency</th>
              <th className="num">Qty</th>
              <th>Updated</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {rows.map(row => {
              const b = buf[row.id] || {}
              const st = rowState[row.id] || 'idle'
              return (
                <tr key={row.id}>
                  <td>{row.sku}</td>
                  <td>
                    <input
                      value={b.title ?? row.title ?? ''}
                      onChange={e => onChange(row.id, 'title', e.target.value)}
                      onBlur={() => onBlur(row)}
                    />
                  </td>
                  <td>
                    <input
                      value={b.description ?? row.description ?? ''}
                      onChange={e => onChange(row.id, 'description', e.target.value)}
                      onBlur={() => onBlur(row)}
                    />
                  </td>
                  <td className="num">
                    <input
                      type="number" step="0.01"
                      value={b.price ?? (row.price ?? '')}
                      onChange={e => onChange(row.id, 'price', toNum(e.target.value))}
                      onBlur={() => onBlur(row)}
                    />
                  </td>
                  <td>
                    <select
                      value={b.currency ?? row.currency ?? 'UAH'}
                      onChange={e => onChange(row.id, 'currency', e.target.value)}
                      onBlur={() => onBlur(row)}
                    >
                      <option>UAH</option><option>USD</option><option>EUR</option>
                    </select>
                  </td>
                  <td className="num">
                    <input
                      type="number"
                      value={b.quantity ?? (row.quantity ?? '')}
                      onChange={e => onChange(row.id, 'quantity', toNum(e.target.value))}
                      onBlur={() => onBlur(row)}
                    />
                  </td>
                  <td>{new Date(row.updatedAt).toLocaleString()}</td>
                  <td>
                    {st === 'saving' && 'Saving…'}
                    {st === 'saved' && '✓ Saved'}
                    {st === 'error' && <button onClick={() => queueSave(row)}>Retry</button>}
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      )}
    </div>
  )
}
