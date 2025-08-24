import React, { useState } from 'react'
import axios from '../lib/axios'

const Upload: React.FC = () => {
  const [file, setFile] = useState<File | null>(null)
  const [success, setSuccess] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!file) return

    setError('')
    setSuccess('')
    setLoading(true)

    try {
      const formData = new FormData()
      formData.append('file', file)

      const response = await axios.post('/catalog/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })

      const { jobId } = response.data
      setSuccess(`Upload successful! Job ID: ${jobId}`)
      setFile(null)
      // Reset file input
      const fileInput = document.getElementById('file-input') as HTMLInputElement
      if (fileInput) fileInput.value = ''
    } catch (err: any) {
      setError(err.response?.data?.message || 'Upload failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container">
      <div className="form-container">
        <h1>Upload File</h1>
        <form onSubmit={handleSubmit}>
          <div className="field">
            <label>Select file (.xlsx or .csv):</label>
            <input
              id="file-input"
              type="file"
              accept=".xlsx,.csv"
              onChange={(e) => setFile(e.target.files?.[0] || null)}
              required
            />
          </div>
          {error && <div className="error">{error}</div>}
          {success && <div className="success">{success}</div>}
          <button type="submit" disabled={loading || !file}>
            {loading ? 'Uploading...' : 'Upload'}
          </button>
        </form>
      </div>
    </div>
  )
}

export default Upload