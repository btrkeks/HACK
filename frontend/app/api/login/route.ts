import { NextResponse } from 'next/server';

interface LoginRequest {
  username: string;
  password: string;
}

interface AppUser {
  id: number;
  username: string;
  email: string;
}

export async function POST(request: Request) {
  try {
    // Parse the request body
    const body: LoginRequest = await request.json();
    
    // Make request to the backend
    const backendUrl = process.env.BACKEND_URL || 'http://localhost:8080';
    const response = await fetch(`${backendUrl}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      return NextResponse.json(
        { error: 'Invalid username or password' },
        { status: 401 }
      );
    }

    const data: AppUser = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    console.error('Error in login API:', error);
    return NextResponse.json(
      { error: 'Failed to process login' },
      { status: 500 }
    );
  }
}