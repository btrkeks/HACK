"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import Input from "@/components/ui/input"
import Button from "@/components/ui/button"
import { Award, ExternalLink, Clock, RefreshCw } from "lucide-react"

// Define response type from the backend
interface ChatResponse {
  aiMessage: string;
  isQuestionPhase: boolean;
  questionCount: number;
  recommendedPerson: any | null;
  recommendedEvent: any | null;
  recommendedFoerderung: any | null;
}

interface Recommendations {
  person: any | null
  event: any | null
  foerderung: any | null
}

// Update Message type to include role
type Message = {
  id: number
  text: string
  isUser: boolean
  timestamp: Date
}

// Add type for the chat history response from the API
interface ChatHistoryMessage {
  id: number;
  role: string;
  content: string;
}

type ChatHistory = {
  id: number
  title: string
  date: Date
  preview: string
  messages: ChatHistoryMessage[]
}

export default function ChatPage() {
  const router = useRouter()
  const [input, setInput] = useState("")
  const [messages, setMessages] = useState<Message[]>([
    {
      id: 1,
      text: "Hallo! Wie kann ich Ihnen heute helfen?",
      isUser: false,
      timestamp: new Date(),
    }
  ])
  const [isLoading, setIsLoading] = useState(false)
  const [isHistoryLoading, setIsHistoryLoading] = useState(false)
  const [userId, setUserId] = useState<number>(1) // Default userId
  const [chatHistory, setChatHistory] = useState<ChatHistory[]>([])
  const [showHistory, setShowHistory] = useState(false)
  const [hasRecommendations, setHasRecommendations] = useState(false)
  const [recommendations, setRecommendations] = useState<Recommendations>({
    person: null,
    event: null,
    foerderung: null
  })
  
  // Initialize userId based on localStorage and fetch chat history
  useEffect(() => {
    // Generate a random userId if none exists
    const storedUserId = localStorage.getItem('userId');
    if (storedUserId) {
      setUserId(parseInt(storedUserId, 10));
    } else {
      const newUserId = Math.floor(Math.random() * 1000000);
      localStorage.setItem('userId', newUserId.toString());
      setUserId(newUserId);
    }
    
    // Get company data from localStorage if available
    const companyData = localStorage.getItem('companyData');
    if (companyData) {
      try {
        const parsedData = JSON.parse(companyData);
        // Add initial context message about the company, now including industry if available
        const industryInfo = parsedData.industry ? ` in der Branche "${parsedData.industry}"` : '';
        
        setMessages(prev => [
          ...prev,
          {
            id: prev.length + 1,
            text: `Ich bin hier, um Ihnen mit Innovationsfragen zu helfen. Sie haben die Firma "${parsedData.companyName || 'Ihre Firma'}"${industryInfo} angegeben.`,
            isUser: false,
            timestamp: new Date(Date.now() + 500),
          }
        ]);
      } catch (e) {
        console.error("Error parsing company data:", e);
      }
    }
  }, []);
  
  // Fetch chat history when userId is available
  useEffect(() => {
    if (userId) {
      fetchChatHistory();
    }
  }, [userId]);

  // Function to fetch chat history
  const fetchChatHistory = async () => {
    setIsHistoryLoading(true);
    try {
      const response = await fetch(`/api/chat-history?userId=${userId}`);
      
      if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
      }
      
      const data = await response.json() as ChatHistoryMessage[];
      
      // Process history data into ChatHistory objects
      if (Array.isArray(data) && data.length > 0) {
        const processedHistory: ChatHistory[] = [];
        
        // Group messages into conversations by day or session
        // This is a simplified approach - you might want to adjust based on your actual data
        let currentGroup: ChatHistoryMessage[] = [];
        let groupId = 1;
        
        for (let i = 0; i < data.length; i++) {
          currentGroup.push(data[i]);
          
          // Create a new group after 10 messages or at the end
          if (currentGroup.length >= 10 || i === data.length - 1) {
            const title = generateTitleFromMessages(currentGroup);
            const date = new Date(Date.now() - (groupId * 24 * 60 * 60 * 1000)); // Just for demonstration
            
            processedHistory.push({
              id: groupId,
              title: title,
              date: date,
              preview: currentGroup[0]?.content || "Empty conversation",
              messages: [...currentGroup]
            });
            
            currentGroup = [];
            groupId++;
          }
        }
        
        setChatHistory(processedHistory);
      }
    } catch (err) {
      console.error('Error fetching chat history:', err);
    } finally {
      setIsHistoryLoading(false);
    }
  };
  
  // Helper function to generate a title from messages
  const generateTitleFromMessages = (messages: ChatHistoryMessage[]): string => {
    if (!messages || messages.length === 0) return "Neue Unterhaltung";
    
    // Find the first user message for the title
    const firstUserMessage = messages.find(msg => msg.role === 'user');
    const content = firstUserMessage?.content || messages[0]?.content || "Neue Unterhaltung";
    
    // Truncate to ~30 characters for the title
    return content.length > 30 
      ? content.substring(0, 30) + "..." 
      : content;
  };
  
  // Load the selected chat history
  const loadChatHistory = (history: ChatHistory) => {
    // Convert the chat history messages to Message objects for display
    const loadedMessages: Message[] = [];
    
    history.messages.forEach((message, index) => {
      loadedMessages.push({
        id: index + 1,
        text: message.content,
        isUser: message.role === 'user',
        timestamp: new Date(history.date.getTime() + (index * 60 * 1000)) // Fake timestamps
      });
    });
    
    setMessages(loadedMessages);
  };

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!input.trim() || isLoading) return

    setIsLoading(true)
    
    // Add user message to chat
    const userMessageId = messages.length + 1;
    const newUserMessage: Message = {
      id: userMessageId,
      text: input,
      isUser: true,
      timestamp: new Date(),
    }
    
    setMessages(prev => [...prev, newUserMessage])
    const currentInput = input;
    setInput("")
    
    try {
      // Send message to backend
      const response = await fetch('/api/chat', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: userId,
          message: currentInput
        }),
      });
      
      if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
      }
      
      const data: ChatResponse = await response.json();
      
      // Add AI response to chat
      const newResponseMessage: Message = {
        id: userMessageId + 1,
        text: data.aiMessage,
        isUser: false,
        timestamp: new Date(),
      }
      
      setMessages(prev => [...prev, newResponseMessage])
      
      // Store recommendations if any, but don't display them in the chat
      if (data.recommendedPerson || data.recommendedEvent || data.recommendedFoerderung) {
        const updatedRecommendations = {
          person: data.recommendedPerson,
          event: data.recommendedEvent,
          foerderung: data.recommendedFoerderung
        };
        
        setRecommendations(updatedRecommendations);
        localStorage.setItem('recommendations', JSON.stringify(updatedRecommendations));
        setHasRecommendations(true);
        
        // Add a message with a button to view recommendations
        setTimeout(() => {
          setMessages(prev => [
            ...prev,
            {
              id: prev.length + 1,
              text: "Basierend auf unserem Gespräch habe ich einige Empfehlungen für Sie zusammengestellt.",
              isUser: false,
              timestamp: new Date(),
            }
          ]);
        }, 500);
      }
      
      // Refresh chat history after sending a message
      fetchChatHistory();
    } catch (err) {
      console.error('Error sending message:', err);
      
      // Add error message
      setMessages(prev => [
        ...prev, 
        {
          id: userMessageId + 1,
          text: "Es tut mir leid, es gab ein Problem bei der Verarbeitung Ihrer Nachricht. Bitte versuchen Sie es später noch einmal.",
          isUser: false,
          timestamp: new Date(),
        }
      ]);
    } finally {
      setIsLoading(false);
    }
  }
  
  const handleViewRecommendations = () => {
    router.push('/recommendations');
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
        <div className="p-4 border-b border-gray-200 flex justify-between items-center">
          <h2 className="font-medium text-lg text-primary">Chat-Verlauf</h2>
          <button 
            onClick={fetchChatHistory} 
            className="text-gray-500 hover:text-primary transition-colors"
            disabled={isHistoryLoading}
            title="Verlauf aktualisieren"
          >
            <RefreshCw className={`h-4 w-4 ${isHistoryLoading ? 'animate-spin' : ''}`} />
          </button>
        </div>

        <div className="overflow-y-auto h-[calc(100%-57px)]">
          {isHistoryLoading ? (
            <div className="p-4 text-center text-gray-500">
              <Clock className="h-5 w-5 mx-auto mb-2 animate-pulse" />
              Lade Verlauf...
            </div>
          ) : (
            <>
              {chatHistory.map((chat) => (
                <div 
                  key={chat.id} 
                  className="p-4 border-b border-gray-100 hover:bg-gray-100 cursor-pointer"
                  onClick={() => loadChatHistory(chat)}
                >
                  <h3 className="font-medium text-gray-800">{chat.title}</h3>
                  <p className="text-sm text-gray-500 mt-1">{formatDate(chat.date)}</p>
                  <p className="text-sm text-gray-700 mt-2 truncate">{chat.preview}</p>
                </div>
              ))}
              {chatHistory.length === 0 && (
                <div className="p-4 text-center text-gray-500">
                  Noch keine Chat-Historie vorhanden
                </div>
              )}
            </>
          )}
        </div>
      </div>

      {/* Main Chat Area */}
      <div className="flex-1 flex flex-col">
        <div className="p-4 border-b border-gray-200 flex justify-between items-center">
          <button onClick={() => setShowHistory(!showHistory)} className="text-primary hover:text-primary-dark">
            {showHistory ? "« Verlauf ausblenden" : "Verlauf anzeigen »"}
          </button>
          <h2 className="font-medium">Aktuelle Unterhaltung</h2>
          
          {hasRecommendations ? (
            <button 
              onClick={handleViewRecommendations}
              className="flex items-center text-primary hover:underline"
            >
              <Award className="h-4 w-4 mr-1" /> Empfehlungen
            </button>
          ) : (
            <div className="w-[100px]"></div> /* Spacer for centering */
          )}
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
                  
                  {/* Show recommendations button after AI message about recommendations */}
                  {!message.isUser && 
                   message.text === "Basierend auf unserem Gespräch habe ich einige Empfehlungen für Sie zusammengestellt." && (
                    <div className="mt-3">
                      <Button 
                        onClick={handleViewRecommendations}
                        className="flex items-center text-xs py-1 px-3 h-auto"
                      >
                        <ExternalLink className="h-3 w-3 mr-1" /> Empfehlungen anzeigen
                      </Button>
                    </div>
                  )}
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
              disabled={isLoading}
            />
            <Button type="submit" disabled={isLoading}>
              {isLoading ? 'Sendet...' : 'Senden'}
            </Button>
          </form>
        </div>
      </div>
    </div>
  )
}
