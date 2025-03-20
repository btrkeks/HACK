"use client"

import type React from "react"

import { useState } from "react"
import Input from "@/components/ui/input"
import Button from "@/components/ui/button"

type Message = {
  id: number
  text: string
  isUser: boolean
  timestamp: Date
}

type ChatHistory = {
  id: number
  title: string
  date: Date
  preview: string
}

export default function ChatPage() {
  const [input, setInput] = useState("")
  const [messages, setMessages] = useState<Message[]>([
    {
      id: 1,
      text: "Hallo! Wie kann ich Ihnen heute helfen?",
      isUser: false,
      timestamp: new Date(Date.now() - 1000 * 60 * 5),
    },
    {
      id: 2,
      text: "Ich brauche Informationen über die Website, die ich angegeben habe.",
      isUser: true,
      timestamp: new Date(Date.now() - 1000 * 60 * 4),
    },
    {
      id: 3,
      text: "Ich analysiere die Website jetzt. Welche spezifischen Informationen suchen Sie?",
      isUser: false,
      timestamp: new Date(Date.now() - 1000 * 60 * 3),
    },
  ])

  const [chatHistory, setChatHistory] = useState<ChatHistory[]>([
    {
      id: 1,
      title: "Website Analyse",
      date: new Date(Date.now() - 1000 * 60 * 60 * 24 * 2),
      preview: "Analyse der E-Commerce-Website...",
    },
    {
      id: 2,
      title: "Marketing Strategie",
      date: new Date(Date.now() - 1000 * 60 * 60 * 24 * 5),
      preview: "Diskussion über Content-Marketing...",
    },
    {
      id: 3,
      title: "SEO Optimierung",
      date: new Date(Date.now() - 1000 * 60 * 60 * 24 * 7),
      preview: "Verbesserung der Suchmaschinenoptimierung...",
    },
  ])

  const [showHistory, setShowHistory] = useState(false)

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault()
    if (!input.trim()) return

    console.log("Message sent:", input)

    // Add user message
    const newUserMessage: Message = {
      id: messages.length + 1,
      text: input,
      isUser: true,
      timestamp: new Date(),
    }

    // Add mock response
    const newResponseMessage: Message = {
      id: messages.length + 2,
      text: "Dies ist eine automatische Antwort auf Ihre Nachricht. In einer echten Anwendung würde diese vom Backend kommen.",
      isUser: false,
      timestamp: new Date(Date.now() + 1000),
    }

    setMessages([...messages, newUserMessage, newResponseMessage])
    setInput("")
  }

  const formatTime = (date: Date) => {
    return date.toLocaleTimeString("de-DE", { hour: "2-digit", minute: "2-digit" })
  }

  const formatDate = (date: Date) => {
    return date.toLocaleDateString("de-DE", { day: "2-digit", month: "2-digit", year: "numeric" })
  }

  return (
    <div className="flex h-[calc(100vh-64px)] bg-background">
      {/* Chat History Sidebar */}
      <div
        className={`${showHistory ? "w-80" : "w-0"} transition-all duration-300 h-full bg-gray-50 border-r border-gray-200 overflow-hidden`}
      >
        <div className="p-4 border-b border-gray-200">
          <h2 className="font-medium text-lg text-primary">Chat-Verlauf</h2>
        </div>

        <div className="overflow-y-auto h-[calc(100%-57px)]">
          {chatHistory.map((chat) => (
            <div key={chat.id} className="p-4 border-b border-gray-100 hover:bg-gray-100 cursor-pointer">
              <h3 className="font-medium text-gray-800">{chat.title}</h3>
              <p className="text-sm text-gray-500 mt-1">{formatDate(chat.date)}</p>
              <p className="text-sm text-gray-700 mt-2 truncate">{chat.preview}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Main Chat Area */}
      <div className="flex-1 flex flex-col">
        <div className="p-4 border-b border-gray-200 flex justify-between items-center">
          <button onClick={() => setShowHistory(!showHistory)} className="text-primary hover:text-primary-dark">
            {showHistory ? "« Verlauf ausblenden" : "Verlauf anzeigen »"}
          </button>
          <h2 className="font-medium">Aktuelle Unterhaltung</h2>
          <div className="w-[100px]"></div> {/* Spacer for centering */}
        </div>

        <div className="flex-1 overflow-y-auto p-4">
          <div className="max-w-3xl mx-auto space-y-4">
            {messages.map((message) => (
              <div key={message.id} className={`flex ${message.isUser ? "justify-end" : "justify-start"}`}>
                <div
                  className={`max-w-[80%] p-3 rounded-lg ${
                    message.isUser ? "bg-primary text-white" : "bg-white border border-gray-200 text-gray-800"
                  }`}
                >
                  <div className="flex justify-between items-start mb-1">
                    <span className={`text-xs ${message.isUser ? "text-white/80" : "text-gray-500"}`}>
                      {message.isUser ? "Sie" : "Assistent"}
                    </span>
                    <span className={`text-xs ${message.isUser ? "text-white/80" : "text-gray-500"} ml-4`}>
                      {formatTime(message.timestamp)}
                    </span>
                  </div>
                  {message.text}
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="border-t border-gray-200 p-4 bg-white">
          <form onSubmit={handleSend} className="max-w-3xl mx-auto flex gap-2">
            <Input
              type="text"
              placeholder="Nachricht eingeben..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              className="flex-1 border-primary/30 focus:border-primary"
            />
            <Button type="submit">Senden</Button>
          </form>
        </div>
      </div>
    </div>
  )
}

