"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import Input from "@/components/ui/input"
import Button from "@/components/ui/button"
import InteractiveBackground from "@/components/interactive-background"
import ChoiceDialog from "@/components/choice-dialog"

interface CompanyData {
  companyName: string | null;
  numberOfEmployees: number | null;
}

export default function MainPage() {
  const [url, setUrl] = useState("")
  const [dialogOpen, setDialogOpen] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [companyData, setCompanyData] = useState<CompanyData | null>(null)
  const [error, setError] = useState<string | null>(null)
  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)
    
    // Make sure URL has http/https prefix
    let processUrl = url;
    if (!processUrl.startsWith('http://') && !processUrl.startsWith('https://')) {
      processUrl = 'https://' + processUrl;
    }
    
    try {
      // Call our internal Next.js API route instead of the backend directly
      const response = await fetch(`/api/process-webpage?url=${encodeURIComponent(processUrl)}`);
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || `Error: ${response.status}`);
      }
      
      const data = await response.json();
      setCompanyData(data);
      setDialogOpen(true);
    } catch (err) {
      console.error('Error fetching data:', err);
      setError(err instanceof Error ? err.message : 'Failed to process URL');
    } finally {
      setIsLoading(false);
    }
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

          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? 'Wird verarbeitet...' : 'Starten'}
          </Button>
          
          {error && (
            <p className="text-red-500 text-sm text-center mt-2">{error}</p>
          )}
        </form>
      </div>

      {/* Pass company data to the choice dialog */}
      <ChoiceDialog 
        open={dialogOpen} 
        onOpenChange={setDialogOpen} 
        companyData={companyData} 
        onCompanyDataChange={setCompanyData} 
      />
    </div>
  )
}

