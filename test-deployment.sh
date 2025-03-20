#!/bin/sh

echo "Testing containerized application deployment"

# Stop any existing containers
echo "Stopping any existing containers..."
docker compose down

# Build and start the application
echo "Building and starting containers..."
docker compose build
docker compose up -d

# Wait for services to be ready
echo "Waiting for services to start..."
sleep 10

# Check if containers are running
echo "Checking container status..."
docker compose ps

# Check database connection
echo "Testing database connection..."
docker compose exec database psql -U hack -d hack -c "SELECT 1"

# Check backend logs
echo "Checking backend logs..."
docker compose logs backend --tail 20

# Check frontend logs
echo "Checking frontend logs..."
docker compose logs frontend --tail 20

echo "===================================="
echo "Application URLs:"
echo "Frontend: http://localhost:3000"
echo "Backend API: http://localhost:8080"
echo "===================================="
echo "Use the following command to stop the application:"
echo "docker compose down"