"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Calendar, MapPin, ExternalLink, User, Award, Building } from "lucide-react"
import RequireAuth from "@/components/require-auth"
import Button from "@/components/ui/button"

interface Person {
  id: number
  name: string
  [key: string]: any
}

interface Event {
  id: number
  name: string
  zeitPunkt: string
  Adresse: string
  link: string
  beschreibung: string
  branche: string
  [key: string]: any
}

interface Foerderung {
  id: number
  name: string
  beschreibung: string
  date: string
  branche: string
  linkWebsite: string
  linkFormular: string
  [key: string]: any
}

interface Recommendations {
  person: Person | null
  event: Event | null
  foerderung: Foerderung | null
}

export default function RecommendationsPage() {
  const router = useRouter()
  const [recommendations, setRecommendations] = useState<Recommendations>({
    person: null,
    event: null,
    foerderung: null
  })
  const [userName, setUserName] = useState<string>("")

  useEffect(() => {
    // Get recommendations from localStorage
    const storedRecommendations = localStorage.getItem('recommendations')
    if (storedRecommendations) {
      try {
        setRecommendations(JSON.parse(storedRecommendations))
      } catch (e) {
        console.error("Error parsing recommendations:", e)
      }
    }

    // Try to get user info for email signature
    const userData = localStorage.getItem('userData')
    if (userData) {
      try {
        const parsedData = JSON.parse(userData)
        if (parsedData.username) {
          setUserName(parsedData.username)
        }
      } catch (e) {
        console.error("Error parsing user data:", e)
      }
    }
  }, [])

  const formatDate = (dateString: string) => {
    try {
      const date = new Date(dateString)
      return date.toLocaleDateString('de-DE', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    } catch (e) {
      return dateString
    }
  }

  const handleBackClick = () => {
    router.push('/chat')
  }

  // Create email subject and body for the contact request
  const createEmailContent = (personName: string) => {
    const subject = "Anfrage nach Beratung zu KI-Innovation"
    const body = `Sehr geehrte(r) ${personName},

Ich wurde über die Innovationsberater-Plattform des Kanton St.Gallen auf Sie aufmerksam gemacht und würde gerne mehr über Ihre Expertise im Bereich künstlicher Intelligenz erfahren.

Aktuell suche ich nach Möglichkeiten, KI-Technologien in meinem Unternehmen zu implementieren und würde mich über einen Austausch mit Ihnen freuen.

Wäre es möglich, einen Termin für ein Gespräch zu vereinbaren?

Mit freundlichen Grüßen,
${userName || ""}
`

    // Encode the subject and body for use in mailto link
    return `?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`
  }

  return (
      <RequireAuth>
        <div className="container max-w-4xl mx-auto py-8 px-4">
          <h1 className="text-3xl font-bold text-primary mb-8 text-center">Ihre Empfehlungen</h1>

          <div className="grid gap-8 mb-8">
            {recommendations.person && (
                <div className="bg-white rounded-lg shadow-md p-6 border border-primary/20">
                  <h2 className="text-xl font-semibold mb-4 flex items-center text-primary">
                    <User className="mr-2 h-5 w-5" /> Empfohlener Kontakt
                  </h2>
                  <div className="space-y-2">
                    <p className="text-lg font-medium">{recommendations.person.name}</p>
                    {Object.entries(recommendations.person).map(([key, value]) => {
                      if (key !== 'id' && key !== 'name') {
                        // Check if the value contains an email address
                        const isEmail = typeof value === 'string' && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);

                        return (
                            <p key={key} className="text-gray-700">
                              <span className="font-medium">{key}:</span>{' '}
                              {isEmail ? (
                                  <a
                                      href={`mailto:${value}${createEmailContent(recommendations.person.name)}`}
                                      className="text-primary hover:underline"
                                  >
                                    {value}
                                  </a>
                              ) : (
                                  value
                              )}
                            </p>
                        )
                      }
                      return null
                    })}
                  </div>
                </div>
            )}

            {recommendations.event && (
                <div className="bg-white rounded-lg shadow-md p-6 border border-primary/20">
                  <h2 className="text-xl font-semibold mb-4 flex items-center text-primary">
                    <Calendar className="mr-2 h-5 w-5" /> Empfohlene Veranstaltung
                  </h2>
                  <div className="space-y-3">
                    <p className="text-lg font-medium">{recommendations.event.name}</p>
                    <p className="text-gray-700">{recommendations.event.beschreibung}</p>

                    <div className="flex items-start">
                      <Calendar className="h-5 w-5 mr-2 mt-0.5 text-gray-500" />
                      <p>{formatDate(recommendations.event.zeitPunkt)}</p>
                    </div>

                    {recommendations.event.Adresse && (
                        <div className="flex items-start">
                          <MapPin className="h-5 w-5 mr-2 mt-0.5 text-gray-500" />
                          <p>{recommendations.event.Adresse}</p>
                        </div>
                    )}

                    {recommendations.event.branche && (
                        <div className="flex items-start">
                          <Building className="h-5 w-5 mr-2 mt-0.5 text-gray-500" />
                          <p>{recommendations.event.branche}</p>
                        </div>
                    )}

                    {recommendations.event.link && (
                        <div className="mt-4">
                          <a
                              href={recommendations.event.link}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="inline-flex items-center text-primary hover:underline"
                          >
                            <ExternalLink className="h-4 w-4 mr-1" /> Zur Veranstaltung
                          </a>
                        </div>
                    )}
                  </div>
                </div>
            )}

            {recommendations.foerderung && (
                <div className="bg-white rounded-lg shadow-md p-6 border border-primary/20">
                  <h2 className="text-xl font-semibold mb-4 flex items-center text-primary">
                    <Award className="mr-2 h-5 w-5" /> Empfohlene Förderung
                  </h2>
                  <div className="space-y-3">
                    <p className="text-lg font-medium">{recommendations.foerderung.name}</p>
                    <p className="text-gray-700">{recommendations.foerderung.beschreibung}</p>

                    {recommendations.foerderung.date && (
                        <div className="flex items-start">
                          <Calendar className="h-5 w-5 mr-2 mt-0.5 text-gray-500" />
                          <p>Frist: {formatDate(recommendations.foerderung.date)}</p>
                        </div>
                    )}

                    {recommendations.foerderung.branche && (
                        <div className="flex items-start">
                          <Building className="h-5 w-5 mr-2 mt-0.5 text-gray-500" />
                          <p>{recommendations.foerderung.branche}</p>
                        </div>
                    )}

                    <div className="mt-4 space-y-2">
                      {recommendations.foerderung.linkWebsite && (
                          <div>
                            <a
                                href={recommendations.foerderung.linkWebsite}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="inline-flex items-center text-primary hover:underline"
                            >
                              <ExternalLink className="h-4 w-4 mr-1" /> Zur Website
                            </a>
                          </div>
                      )}

                      {recommendations.foerderung.linkFormular && (
                          <div>
                            <a
                                href={recommendations.foerderung.linkFormular}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="inline-flex items-center text-primary hover:underline"
                            >
                              <ExternalLink className="h-4 w-4 mr-1" /> Zum Antragsformular
                            </a>
                          </div>
                      )}
                    </div>
                  </div>
                </div>
            )}
          </div>

          {(!recommendations.person && !recommendations.event && !recommendations.foerderung) && (
              <div className="bg-white rounded-lg shadow-md p-6 border border-primary/20 text-center">
                <p className="text-lg text-gray-700">Keine Empfehlungen gefunden.</p>
              </div>
          )}

          <div className="text-center mt-6">
            <Button onClick={handleBackClick}>
              Zurück zum Chat
            </Button>
          </div>
        </div>
      </RequireAuth>
  )
}