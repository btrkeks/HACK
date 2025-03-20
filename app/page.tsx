"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import Input from "@/components/ui/input"
import Button from "@/components/ui/button"
import InteractiveBackground from "@/components/interactive-background"
import ChoiceDialog from "@/components/choice-dialog"

export default function MainPage() {
  const [url, setUrl] = useState("")
  const [dialogOpen, setDialogOpen] = useState(false)
  const router = useRouter()

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    console.log("URL submitted:", url)
    // Instead of directly navigating, open the dialog
    setDialogOpen(true)
  }

  return (
    <div className="relative flex items-center justify-center min-h-[calc(100vh-64px)] bg-background overflow-hidden">
      <InteractiveBackground />

      <div className="w-full max-w-md p-8 bg-white/90 backdrop-blur-md rounded-lg shadow-lg z-10 border border-primary/20">
        <h1 className="text-2xl font-semibold mb-6 text-center text-primary">Willkommen beim Innovationsberater des Kanton St.Gallen</h1>
        <p className="text-sm text-center mb-6 text-gray-700">Füge die Website deiner Firma ein um uns ein Onbaording zu erleichtern und starte den Prozess!</p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            type="text"
            placeholder="URL hier eingeben..."
            value={url}
            onChange={(e) => setUrl(e.target.value)}
            fullWidth
            className="border-primary/30 focus:border-primary"
          />

          <Button type="submit" className="w-full">
            Starten
          </Button>
        </form>
      </div>

      {/* Add the choice dialog */}
      <ChoiceDialog open={dialogOpen} onOpenChange={setDialogOpen} />
    </div>
  )
}

