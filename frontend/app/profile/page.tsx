"use client"

import { useEffect, useState } from "react"
import { useAuth } from "@/hooks/use-auth"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"

interface UserProfile {
  id: number
  username: string
  email: string
}

export default function ProfilePage() {
  const { isLoggedIn, userId, isLoading } = useAuth()
  const [userProfile, setUserProfile] = useState<UserProfile | null>(null)
  const [isLoadingProfile, setIsLoadingProfile] = useState(false)
  const router = useRouter()

  useEffect(() => {
    // If auth is done loading and user is not logged in, redirect to login
    if (!isLoading && !isLoggedIn) {
      router.push("/login")
    }
  }, [isLoggedIn, isLoading, router])

  useEffect(() => {
    const fetchUserProfile = async () => {
      if (!userId) return

      setIsLoadingProfile(true)
      try {
        const response = await fetch(`/api/user?userId=${userId}`)
        if (!response.ok) {
          throw new Error("Failed to fetch user profile")
        }
        const data = await response.json()
        setUserProfile(data)
      } catch (error) {
        console.error("Error fetching user profile:", error)
      } finally {
        setIsLoadingProfile(false)
      }
    }

    if (userId) {
      fetchUserProfile()
    }
  }, [userId])

  // Show loading state while checking auth
  if (isLoading) {
    return (
      <div className="container max-w-4xl py-10 px-4">
        <Card>
          <CardHeader>
            <CardTitle>
              <Skeleton className="h-8 w-[200px]" />
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <Skeleton className="h-6 w-[180px]" />
            <Skeleton className="h-6 w-[250px]" />
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="container max-w-4xl py-10 px-4">
      <Card>
        <CardHeader>
          <CardTitle>Benutzerprofil</CardTitle>
        </CardHeader>
        <CardContent>
          {isLoadingProfile ? (
            <div className="space-y-4">
              <Skeleton className="h-6 w-[180px]" />
              <Skeleton className="h-6 w-[250px]" />
            </div>
          ) : userProfile ? (
            <div className="space-y-4">
              <div>
                <p className="text-sm font-medium text-muted-foreground">Benutzername</p>
                <p>{userProfile.username}</p>
              </div>
              <div>
                <p className="text-sm font-medium text-muted-foreground">E-Mail</p>
                <p>{userProfile.email}</p>
              </div>
            </div>
          ) : (
            <p>Keine Profildaten verfügbar.</p>
          )}
        </CardContent>
      </Card>
    </div>
  )
}