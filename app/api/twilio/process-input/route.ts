import { NextResponse } from 'next/server';

export async function POST(request: Request) {
  // This is a proxy endpoint that forwards the request to the backend
  try {
    // Forward the request to the Java backend
    const formData = await request.formData();
    
    const response = await fetch('http://localhost:8080/twilio/process-input', {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) {
      throw new Error(`Backend returned ${response.status}`);
    }

    // Get the TwiML response from the backend
    const twiml = await response.text();
    
    // Return the TwiML response with the correct content type
    return new NextResponse(twiml, {
      headers: {
        'Content-Type': 'application/xml',
      },
    });
  } catch (error) {
    console.error('Error in Twilio process-input webhook:', error);
    
    // Return a basic TwiML response in case of error
    const errorTwiml = `<?xml version="1.0" encoding="UTF-8"?>
      <Response>
        <Say>Es gab ein Problem mit dem Service. Bitte versuchen Sie es später erneut.</Say>
        <Hangup/>
      </Response>`;
    
    return new NextResponse(errorTwiml, {
      headers: {
        'Content-Type': 'application/xml',
      },
    });
  }
}