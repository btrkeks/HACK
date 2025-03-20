"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"

export default function Header() {
  const pathname = usePathname()

  const handleSignIn = () => {
    console.log("Sign-in clicked")
  }

  return (
    <header className="w-full py-4 px-6 border-b border-primary/30 flex justify-between items-center bg-background">
      <div className="flex items-center">
        <Link href="/" className="text-xl font-semibold text-primary">
          App Name
        </Link>

        <nav className="ml-10 hidden md:flex space-x-6">
          <Link
            href="/"
            className={`${pathname === "/" ? "text-primary font-medium" : "text-gray-500"} hover:text-primary`}
          >
            Startseite
          </Link>
          <Link
            href="/chat"
            className={`${pathname === "/chat" ? "text-primary font-medium" : "text-gray-500"} hover:text-primary`}
          >
            Chat
          </Link>
          <Link
            href="/recommendation"
            className={`${pathname === "/recommendation" ? "text-primary font-medium" : "text-gray-500"} hover:text-primary`}
          >
            Empfehlungen
          </Link>
        </nav>
      </div>

      <button
        onClick={handleSignIn}
        className="px-4 py-2 text-sm border border-primary rounded-md text-primary hover:bg-primary hover:text-white transition-colors"
      >
        Anmelden
      </button>
    </header>
  )
}

