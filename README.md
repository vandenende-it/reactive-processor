# 🚀 Reactive Processor - Enterprise GitOps Stack

Dit project is een volledige end-to-end Reactive Data Pipeline, gebouwd met een focus op schaalbaarheid, real-time verwerking en GitOps principes.

## 🏗️ Architectuur
- **Generator**: Python script dat miljoenen (gesimuleerde) IoT data-punten naar Kafka stuurt.
- **Message Broker**: Redpanda (Kafka compatibel) draaiend in Kubernetes.
- **Stream Processor**: Spring Boot (Kotlin) met Project Reactor voor windowed aggregatie.
- **BFF (Backend For Frontend)**: Node.js/TypeScript service die Kafka-data vertaalt naar WebSockets.
- **Frontend**: Next.js dashboard met real-time Chart.js visualisatie.
- **Infrastructure**: Terraform (Infra-as-Code) & ArgoCD (GitOps).

## 🛠️ Technologieën
- **Backend**: Kotlin, Spring Boot, Project Reactor
- **Frontend**: Next.js 14, TailwindCSS, Socket.io, Chart.js
- **Infra**: Kubernetes, Helm, Terraform, ArgoCD, Redpanda

## 🚀 Snel Starten

### 1. Prereqs
- Docker Desktop (met Kubernetes ingeschakeld)
- Terraform installeert
- kubectl & helm

### 2. Infrastructuur opzetten
```bash
cd terraform
terraform init
terraform apply
```

### 3. ArgoCD Dashboard
Open de tunnel naar ArgoCD:

```Bash
kubectl port-forward svc/argocd-server -n argocd 8082:443
```

Login met admin en het wachtwoord uit je K8s secrets.

### 4. Applicatie Uitrollen (GitOps)
ArgoCD pakt automatisch de wijzigingen op uit deze repo zodra je pusht naar GitHub. De applicatie wordt gedeployed in de namespace vandenende-reactive.

### 5. Lokaal Frontend Development
   
```Bash
cd frontend
npm install
# Start de BFF (Kafka Bridge)
npx ts-node server.ts
# Start de Next.js UI
npm run dev
```

### 📈 Monitoring

Bekijk de logs van de stream processor:

```bash
kubectl logs -f -l app=reactive-processor -n vandenende-reactive
```
