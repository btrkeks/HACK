"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from "@/components/ui/dropdown-menu"
import { ChevronDown } from "lucide-react"

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
          
          {/* Dropdown menu for Vergangene Gespräche */}
          <DropdownMenu>
            <DropdownMenuTrigger className={`${pathname === "/chat" || pathname === "/call" ? "text-primary font-medium" : "text-gray-500"} hover:text-primary flex items-center gap-1`}>
              Vergangene Gespräche <ChevronDown className="h-4 w-4" />
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem asChild>
                <Link
                  href="/chat"
                  className={`${pathname === "/chat" ? "bg-accent" : ""} w-full`}
                >
                  Chat
                </Link>
              </DropdownMenuItem>
              <DropdownMenuItem asChild>
                <Link
                  href="/call"
                  className={`${pathname === "/call" ? "bg-accent" : ""} w-full`}
                >
                  Telefonieren
                </Link>
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
          
          <Link
            href="/recommendations"
            className={`${pathname === "/recommendations" ? "text-primary font-medium" : "text-gray-500"} hover:text-primary`}
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

