import { NextResponse } from 'next/server';

// Define response type from the backend
interface ChatResponse {
  aiMessage: string;
  isQuestionPhase: boolean;
  questionCount: number;
  recommendedPerson: any | null;
  recommendedEvent: any | null;
  recommendedFoerderung: any | null;
}

export async function POST(request: Request) {
  try {
    // Parse the request body
    const body = await request.json();
    
    // Make request to the actual backend
    const response = await fetch('http://localhost:8080/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      throw new Error(`Error: ${response.status}`);
    }

    const data: ChatResponse = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    console.error('Error in chat API:', error);
    return NextResponse.json(
      { error: 'Failed to process chat message' },
      { status: 500 }
    );
  }
} 