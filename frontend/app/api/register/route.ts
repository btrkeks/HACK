import { NextResponse } from 'next/server';

interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

interface RegisterResponse {
  userId: number;
}

export async function POST(request: Request) {
  try {
    // Parse the request body
    const body: RegisterRequest = await request.json();
    
    // Make request to the backend
    const backendUrl = process.env.BACKEND_URL || 'http://localhost:8080';
    const response = await fetch(`${backendUrl}/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      // Get error message from response
      const errorData = await response.json();
      return NextResponse.json(
        { error: errorData.message || 'Registration failed' },
        { status: 400 }
      );
    }

    const data: RegisterResponse = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    console.error('Error in register API:', error);
    return NextResponse.json(
      { error: 'Failed to process registration' },
      { status: 500 }
    );
  }
}