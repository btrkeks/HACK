// app/api/process-webpage/route.ts
import { NextRequest, NextResponse } from 'next/server';

export async function GET(request: NextRequest) {
  const searchParams = request.nextUrl.searchParams;
  const url = searchParams.get('url');
  const userId = searchParams.get('userId');

  if (!url) {
    return NextResponse.json({ error: 'URL is required' }, { status: 400 });
  }

  if (!userId) {
    return NextResponse.json({ error: 'User ID is required' }, { status: 400 });
  }

  try {
    // Call the backend API passing both url and userId
    const backendUrl = process.env.BACKEND_URL || 'http://localhost:8080';
    const response = await fetch(`${backendUrl}/process-webpage?url=${encodeURIComponent(url)}&userId=${userId}`);

    if (!response.ok) {
      return NextResponse.json(
          { error: `Backend service error: ${response.status}` },
          { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    console.error('Error processing webpage:', error);
    return NextResponse.json({ error: 'Failed to process URL' }, { status: 500 });
  }
}