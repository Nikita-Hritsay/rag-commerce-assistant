import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { isAuthed } from './lib/auth'
import ProtectedRoute from './components/ProtectedRoute'
import Login from './pages/Login'
import Register from './pages/Register'
import Upload from './pages/Upload'
import Profile from './pages/Profile'
import Products from './pages/Products'
import { Link } from 'react-router-dom'

const AppHeader: React.FC = () => (
  <header className="app-header">
    <nav>
      <Link to="/app/upload">Upload</Link>
      <Link to="/app/profile">Profile</Link>
      <Link to="/app/products">Products</Link>
      <button onClick={() => {
        localStorage.removeItem('bizrag.accessToken')
        window.location.href = '/login'
      }}>
        Logout
      </button>
    </nav>
  </header>
)

const ProtectedLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <ProtectedRoute>
    <AppHeader />
    {children}
  </ProtectedRoute>
)

const RootRedirect: React.FC = () => {
  if (isAuthed()) {
    return <Navigate to="/app/upload" replace />
  }
  return <Navigate to="/login" replace />
}

const AppRouter: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<RootRedirect />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/app/upload" element={
          <ProtectedLayout>
            <Upload />
          </ProtectedLayout>
        } />
        <Route path="/app/profile" element={
          <ProtectedLayout>
            <Profile />
          </ProtectedLayout>
        } />
       <Route
          path="/app/products"
          element={
            <ProtectedLayout>
              <Products />
            </ProtectedLayout>
          }
        />
      </Routes>
    </BrowserRouter>
  )
}

export default AppRouter