"use client"
import Button from "@/components/ui/button"

interface RecommendationCardProps {
  title: string
  content: string
  buttonText: string
  onClick: () => void
}

function RecommendationCard({ title, content, buttonText, onClick }: RecommendationCardProps) {
  return (
    <div className="bg-white border border-primary/30 rounded-lg p-6 w-full shadow-md hover:shadow-lg transition-shadow">
      <h2 className="text-xl font-medium text-primary mb-3">{title}</h2>
      <p className="text-gray-700 mb-4">{content}</p>
      <Button onClick={onClick} className="w-full">
        {buttonText}
      </Button>
    </div>
  )
}

export default function RecommendationPage() {
  const handleEventClick = () => {
    console.log("Event button clicked")
    // In a real app, this would navigate to event registration
  }

  const handlePersonaClick = () => {
    console.log("Persona button clicked")
    // In a real app, this would open contact form
  }

  const handleFundingClick = () => {
    console.log("Funding button clicked")
    // In a real app, this would navigate to application
  }

  return (
    <div className="min-h-[calc(100vh-64px)] bg-background py-12 px-4">
      <div className="max-w-md mx-auto space-y-6">
        <h1 className="text-2xl font-semibold mb-8 text-center text-primary">Empfehlungen</h1>

        <RecommendationCard
          title="Veranstaltung"
          content="Veranstaltungsname: Tech Konferenz 2023 | Datum: 15.-17. November 2023 | Ort: Berlin, Deutschland"
          buttonText="Jetzt teilnehmen"
          onClick={handleEventClick}
        />

        <RecommendationCard
          title="Persona"
          content="Name: Alexander Schmidt | Rolle: Senior Entwickler | Interessen: KI, Cloud Computing, Open Source"
          buttonText="Jetzt kontaktieren"
          onClick={handlePersonaClick}
        />

        <RecommendationCard
          title="Finanzierung"
          content="Quelle: Innovationsfonds | Betrag: 25.000 € | Bewerbungsfrist: 1. Dezember 2023"
          buttonText="Jetzt bewerben"
          onClick={handleFundingClick}
        />
      </div>
    </div>
  )
}

