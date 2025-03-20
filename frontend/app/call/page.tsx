"use client"

import type React from "react"

import { useState, useEffect } from "react"
import Button from "@/components/ui/button"
import { Phone, MicOff, Mic, PhoneOff } from "lucide-react"

type CallHistory = {
  id: number
  title: string
  date: Date
  duration: string
}

export default function CallPage() {
  const [isCallActive, setIsCallActive] = useState(false)
  const [isMuted, setIsMuted] = useState(false)
  const [callDuration, setCallDuration] = useState(0)
  const [showHistory, setShowHistory] = useState(false)

  const [callHistory, setCallHistory] = useState<CallHistory[]>([
    {
      id: 1,
      title: "Beratungsgespräch zur Website",
      date: new Date(Date.now() - 1000 * 60 * 60 * 24 * 1),
      duration: "12:34",
    },
    {
      id: 2,
      title: "Marketingstrategie Besprechung",
      date: new Date(Date.now() - 1000 * 60 * 60 * 24 * 3),
      duration: "08:15",
    },
    {
      id: 3,
      title: "Technische Unterstützung",
      date: new Date(Date.now() - 1000 * 60 * 60 * 24 * 6),
      duration: "04:47",
    },
  ])

  useEffect(() => {
    let interval: NodeJS.Timeout | null = null;

    if (isCallActive) {
      interval = setInterval(() => {
        setCallDuration(prev => prev + 1);
      }, 1000);
    } else {
      setCallDuration(0);
    }

    return () => {
      if (interval) clearInterval(interval);
    };
  }, [isCallActive]);

  const formatDuration = (seconds: number) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  const formatDate = (date: Date) => {
    return date.toLocaleDateString("de-DE", { day: "2-digit", month: "2-digit", year: "numeric" });
  };

  // This function is kept but won't be accessible directly from the UI
  const handleStartCall = () => {
    setIsCallActive(true);
  };

  const handleEndCall = () => {
    setIsCallActive(false);
  };

  const toggleMute = () => {
    setIsMuted(!isMuted);
  };

  return (
      <div className="flex h-[calc(100vh-64px)] bg-background">
        {/* Call History Sidebar */}
        <div
            className={`${showHistory ? "w-80" : "w-0"} transition-all duration-300 h-full bg-gray-50 border-r border-gray-200 overflow-hidden`}
        >
          <div className="p-4 border-b border-gray-200">
            <h2 className="font-medium text-lg text-primary">Anruf-Verlauf</h2>
          </div>

          <div className="overflow-y-auto h-[calc(100%-57px)]">
            {callHistory.map((call) => (
                <div key={call.id} className="p-4 border-b border-gray-100 hover:bg-gray-100 cursor-pointer">
                  <h3 className="font-medium text-gray-800">{call.title}</h3>
                  <div className="flex justify-between mt-1">
                    <p className="text-sm text-gray-500">{formatDate(call.date)}</p>
                    <p className="text-sm text-gray-500">Dauer: {call.duration}</p>
                  </div>
                </div>
            ))}
          </div>
        </div>

        {/* Main Call Area */}
        <div className="flex-1 flex flex-col">
          <div className="p-4 border-b border-gray-200 flex justify-between items-center">
            <button onClick={() => setShowHistory(!showHistory)} className="text-primary hover:text-primary-dark">
              {showHistory ? "« Verlauf ausblenden" : "Verlauf anzeigen »"}
            </button>
            <h2 className="font-medium">KI Berater Anruf</h2>
            <div className="w-[100px]"></div> {/* Spacer for centering */}
          </div>

          <div className="flex-1 flex flex-col items-center justify-center p-4">
            <div className="text-center mb-8">
              <div className="space-y-4">
                <div className="w-32 h-32 rounded-full bg-primary/10 flex items-center justify-center mx-auto">
                  <Phone className="h-16 w-16 text-primary" />
                </div>
                <h2 className="text-2xl font-medium text-gray-800">KI Berater Telefon</h2>
                <p className="text-gray-600">Rufen Sie unseren KI Berater direkt an:</p>
                <p className="text-2xl font-bold text-primary">+1 951 350 1935</p>
                <div className="mt-4 max-w-md mx-auto">
                  <p className="text-sm text-gray-600">
                    Rufen Sie diese Nummer an, um mit unserem KI-Berater zu sprechen.
                    Das Gespräch wird über Twilio und ElevenLabs vermittelt.
                  </p>
                </div>
              </div>
            </div>

            {isCallActive && (
                <div className="border-t border-gray-200 pt-8 mt-4 text-center">
                  <p className="text-gray-600 mb-2">Aktiver Anruf</p>
                  <p className="text-xl font-medium text-primary mb-4">{formatDuration(callDuration)}</p>
                  <Button
                      className="rounded-full bg-red-500 hover:bg-red-600"
                      onClick={handleEndCall}
                  >
                    Anruf beenden <PhoneOff className="h-5 w-5 ml-2" />
                  </Button>
                </div>
            )}
          </div>
        </div>
      </div>
  );
}