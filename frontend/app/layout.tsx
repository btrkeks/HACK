import type React from "react"
import "./globals.css"
import type { Metadata } from "next"
import { Inter } from "next/font/google"
import Header from "@/components/header"
import AuthProviderWrapper from "@/components/auth-provider-wrapper"

const inter = Inter({ subsets: ["latin"] })

export const metadata: Metadata = {
  title: "React Frontend-Anwendung",
  description: "Eine moderne, minimalistische React-Anwendung",
  generator: 'v0.dev'
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="de">
      <body className={inter.className}>
        <AuthProviderWrapper>
          <Header />
          <main className="min-h-[calc(100vh-64px)]">{children}</main>
        </AuthProviderWrapper>
      </body>
    </html>
  )
}

import './globals.css'