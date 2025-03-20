"use client"

import { useState, useEffect, createContext, useContext, ReactNode } from 'react'
import { useRouter } from 'next/navigation'

interface AuthContextType {
  isLoggedIn: boolean
  userId: string | null
  login: (userId: string) => void
  logout: () => void
  isLoading: boolean
}

interface SessionResponse {
  valid: boolean
  userId?: string
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [userId, setUserId] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const router = useRouter()

  // Verify session with backend
  const verifySession = async (): Promise<SessionResponse> => {
    const storedUserId = localStorage.getItem('userId')
    
    // If no userId in storage, clearly not logged in
    if (!storedUserId) {
      return { valid: false }
    }
    
    // Since we don't have a proper verification endpoint yet,
    // we'll just trust the localStorage value for now.
    // In a production app, we would make a request to validate the session.
    return { valid: true, userId: storedUserId }
  }

  // Check auth status on mount and when window gets focus
  useEffect(() => {
    const checkAuth = async () => {
      setIsLoading(true)
      const { valid, userId: validatedId } = await verifySession()
      
      setIsLoggedIn(valid)
      setUserId(valid ? validatedId || null : null)
      
      // If not valid, clean up localStorage
      if (!valid && localStorage.getItem('userId')) {
        localStorage.removeItem('userId')
        localStorage.removeItem('companyData')
      }
      
      setIsLoading(false)
    }

    // Check immediately
    checkAuth()

    // Check when window gets focus
    const handleFocus = () => checkAuth();
    window.addEventListener('focus', handleFocus);
    
    return () => {
      window.removeEventListener('focus', handleFocus);
    }
  }, [])

  const login = (newUserId: string) => {
    localStorage.setItem('userId', newUserId)
    setIsLoggedIn(true)
    setUserId(newUserId)
  }

  const logout = () => {
    localStorage.removeItem('userId')
    localStorage.removeItem('companyData')
    setIsLoggedIn(false)
    setUserId(null)
    router.push('/login')
  }

  return (
    <AuthContext.Provider value={{ isLoggedIn, userId, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}