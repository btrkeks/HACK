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
    const body = await request.json();

    // Forward the request to the backend
    const response = await fetch(`${process.env.BACKEND_URL || 'http://localhost:8080'}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    const data = await response.json();

    if (!response.ok) {
      return NextResponse.json(
          { error: 'Authentication failed' },
          { status: response.status }
      );
    }

    // Important: Pass through the 'userId' property exactly as received from backend
    return NextResponse.json(data);
  } catch (error) {
    console.error('Login error:', error);
    return NextResponse.json(
        { error: 'An error occurred during login' },
        { status: 500 }
    );
  }
}