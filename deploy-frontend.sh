#!/bin/bash
echo "🏗️ Building Frontend..."
cd frontend && docker build -t vandenende/reactive-frontend:latest .

echo "🔄 Restarting Pod in Kubernetes..."
kubectl rollout restart deployment frontend -n vandenende-reactive

echo "✅ Done! Dashboard is updating..."
sleep 5
kubectl logs -f -l app=frontend -n vandenende-reactive
