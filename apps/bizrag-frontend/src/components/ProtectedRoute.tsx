import React, { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { getToken } from '../lib/auth'
import axios from '../lib/axios'

interface ProtectedRouteProps {
  children: React.ReactNode
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const [isValid, setIsValid] = useState<boolean | null>(null)

  useEffect(() => {
    const validateToken = async () => {
      const token = getToken()
      if (!token) {
        setIsValid(false)
        return
      }

      try {
        await axios.get('/users/me')
        setIsValid(true)
      } catch {
        setIsValid(false)
      }
    }

    validateToken()
  }, [])

  if (isValid === null) {
    return <div>Loading...</div>
  }

  if (!isValid) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}

export default ProtectedRoute
