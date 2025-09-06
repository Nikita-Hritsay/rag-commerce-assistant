import React, { useEffect, useRef, useState, useMemo } from 'react'
import { listAllProducts, patchProduct } from '../lib/products'
import type { ProductDto } from '../lib/products'

type RowState = 'idle'|'saving'|'saved'|'error'
const toNum = (v: string): number | undefined => (v.trim() === '' ? undefined : Number(v))
const toInt = (v: string): number | undefined => (v.trim() === '' ? undefined : parseInt(v, 10))

// Проста інференс-типізація для атрибутів
function parseAttrInput(prev: any, raw: string) {
  const v = raw.trim()
  if (v === '') return '' // дозволяємо очищати як порожній рядок
  if (typeof prev === 'number') {
    const n = Number(v.replace(',', '.'))
    return Number.isFinite(n) ? n : v
  }
  if (typeof prev === 'boolean') {
    if (/^(true|false)$/i.test(v)) return /^true$/i.test(v)
    return v
  }
  // спробуємо число
  if (/^-?\d+(\.\d+)?$/.test(v.replace(',', '.'))) {
    const n = Number(v.replace(',', '.'))
    if (Number.isFinite(n)) return n
  }
  // спробуємо boolean
  if (/^(true|false)$/i.test(v)) return /^true$/i.test(v)
  return v
}

export default function Products() {
  const [rows, setRows] = useState<ProductDto[]>([])
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState<string | null>(null)

  const [rowState, setRowState] = useState<Record<string, RowState>>({})
  // delta-буфер: дозволяє часткові оновлення і для core, і для attributes
  const [buf, setBuf] = useState<Record<string, Partial<ProductDto> & { attributes?: Record<string, any> }>>({})
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

  // зібрати унікальні ключі атрибутів з усіх рядків (можеш додати denylist якщо потрібно)
  const attributeKeys = useMemo(() => {
    const set = new Set<string>()
    for (const r of rows) {
      const attrs = (r as any).attributes || {}
      Object.keys(attrs).forEach(k => set.add(k))
    }
    return Array.from(set).sort()
  }, [rows])

  function queueSave(row: ProductDto) {
    const id = row.id
    clearTimeout(timers.current[id])
    timers.current[id] = setTimeout(async () => {
      const delta = buf[id]
      if (!delta || Object.keys(delta).length === 0) return
      setRowState(s => ({ ...s, [id]: 'saving' }))
      try {
        // ВАЖЛИВО: надсилаємо тільки дельту; якщо є часткові attributes — відправляємо їх як частковий об’єкт
        const upd = await patchProduct(id, delta as any)
        setRows(rs => rs.map(r => (r.id === id ? upd : r)))
        setBuf(b => { const { [id]: _omit, ...rest } = b; return rest })
        setRowState(s => ({ ...s, [id]: 'saved' }))
        setTimeout(() => setRowState(s => ({ ...s, [id]: 'idle' })), 1200)
      } catch {
        setRowState(s => ({ ...s, [id]: 'error' }))
      }
    }, 600) // трохи зменшив дебаунс, щоб UI жвавіше реагував
  }

  function onChangeCore(id: string, field: keyof ProductDto, value: any) {
    setBuf(b => ({ ...b, [id]: { ...(b[id] || {}), [field]: value } }))
  }
  function onChangeAttr(id: string, key: string, value: any) {
    setBuf(b => ({
      ...b,
      [id]: {
        ...(b[id] || {}),
        attributes: { ...((b[id] && b[id]!.attributes) || {}), [key]: value }
      }
    }))
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
              {/* динамічні атрибути */}
              {attributeKeys.map(k => <th key={`head-${k}`}>{k}</th>)}
              <th>Updated</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {rows.map(row => {
              const b = buf[row.id] || {}
              const st = rowState[row.id] || 'idle'
              const attrsRow = (row as any).attributes || {}
              const attrsBuf = (b as any).attributes || {}

              return (
                <tr key={row.id}>
                  <td>{row.sku}</td>

                  <td>
                    <input
                      value={(b as any).title ?? (row as any).title ?? ''}
                      onChange={e => onChangeCore(row.id, 'title', e.target.value)}
                      onBlur={() => onBlur(row)}
                    />
                  </td>

                  <td>
                    <input
                      value={(b as any).description ?? (row as any).description ?? ''}
                      onChange={e => onChangeCore(row.id, 'description', e.target.value)}
                      onBlur={() => onBlur(row)}
                    />
                  </td>

                  <td className="num">
                    <input
                      type="number" step="0.01"
                      value={(b as any).price ?? ((row as any).price ?? '')}
                      onChange={e => onChangeCore(row.id, 'price', toNum(e.target.value))}
                      onBlur={() => onBlur(row)}
                    />
                  </td>

                  <td>
                    <select
                      value={(b as any).currency ?? (row as any).currency ?? 'UAH'}
                      onChange={e => onChangeCore(row.id, 'currency', e.target.value)}
                      onBlur={() => onBlur(row)}
                    >
                      <option>UAH</option><option>USD</option><option>EUR</option>
                    </select>
                  </td>

                  <td className="num">
                    <input
                      type="number"
                      value={(b as any).quantity ?? ((row as any).quantity ?? '')}
                      onChange={e => onChangeCore(row.id, 'quantity', toInt(e.target.value))}
                      onBlur={() => onBlur(row)}
                    />
                  </td>

                  {/* динамічні атрибути */}
                  {attributeKeys.map(key => {
                    const original = attrsRow[key]
                    const val = (key in attrsBuf) ? attrsBuf[key] : original
                    const typ = typeof (key in attrsBuf ? attrsBuf[key] : original)

                    // Підберемо контроль:
                    // - boolean -> checkbox
                    // - number  -> number input
                    // - string/undefined -> text input
                    if (typ === 'boolean') {
                      return (
                        <td key={`${row.id}-${key}`} style={{textAlign:'center'}}>
                          <input
                            type="checkbox"
                            checked={Boolean(val)}
                            onChange={e => onChangeAttr(row.id, key, e.target.checked)}
                            onBlur={() => onBlur(row)}
                          />
                        </td>
                      )
                    }

                    const isNumber = typeof val === 'number'
                    return (
                      <td key={`${row.id}-${key}`}>
                        <input
                          type={isNumber ? 'number' : 'text'}
                          step={isNumber ? '0.01' : undefined}
                          value={val ?? ''}
                          onChange={e => onChangeAttr(row.id, key, parseAttrInput(original, e.target.value))}
                          onBlur={() => onBlur(row)}
                        />
                      </td>
                    )
                  })}

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
