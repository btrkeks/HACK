import { NextResponse } from 'next/server';

export async function GET(request: Request) {
  // Get URL parameter from the request
  const { searchParams } = new URL(request.url);
  const url = searchParams.get('url');

  if (!url) {
    return NextResponse.json(
      { error: 'URL parameter is required' },
      { status: 400 }
    );
  }

  try {
    // Format URL if needed
    let processUrl = url;
    if (!processUrl.startsWith('http://') && !processUrl.startsWith('https://')) {
      processUrl = 'https://' + processUrl;
    }

    // Make request to the actual backend
    const response = await fetch(
      `http://localhost:8080/process-webpage?url=${encodeURIComponent(processUrl)}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );

    if (!response.ok) {
      throw new Error(`Error: ${response.status}`);
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    console.error('Error processing webpage:', error);
    return NextResponse.json(
      { error: 'Failed to process URL' },
      { status: 500 }
    );
  }
} 