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

### ✅ Fase 1: De Basis (Nu mee bezig)

- [ ] Laatste Push & Sync: De herstelde Frontend-image naar GitHub/ArgoCD pushen.
- [ ] Connectiviteit Check: Verifiëren of de BFF in Kubernetes de ✅ Verbonden met Kafka log geeft.
- [ ] Eerste Pixels: De browser openen op http://localhost:3000 en kijken of de Chart.js grafiek beweegt als de generator aanstaat.

### 🛠️ Fase 2: Infrastructure & Bereikbaarheid (Short-term)

- [ ] Ingress Controller: De Nginx Ingress Controller installeren via Terraform.
- [ ] Host-file: reactive.local koppelen aan 127.0.0.1 op je laptop.
- [ ] SSL/HTTPS: (Optioneel) Een zelfondertekend certificaat toevoegen zodat je geen "onveilig" melding krijgt.
- [ ] Redpanda Poort 9092: Controleren of de poort-configuratie in Terraform nu definitief stabiel is voor alle interne services.

### 🚀 Fase 3: Performance & Architectuur (Mid-term)

- [ ] Protocol Buffers (Protobuf): De JSON-berichten vervangen door Protobuf. Dit verlaagt de payload grootte met wel 70% en is essentieel voor "miljoenen berichten".
- [ ] Schema Registry: Redpanda's Schema Registry gebruiken om de Protobuf-definities centraal te beheren.
- [ ] Horizontal Pod Autoscaling (HPA): Instellen dat je Kotlin-app automatisch opschaalt (meer pods) als de Kafka-topic te vol loopt (Consumer Lag).
- [ ] Persistentie: Een database (bijv. PostgreSQL of TimescaleDB) toevoegen om de meetwaarden ook op te slaan voor "historical view" in je dashboard.

### 📊 Fase 4: Dashboard & UI Optimalisatie (Nice-to-have)

- [ ] Throttling in de BFF: De WebSocket-updates beperken tot max. 30 of 60 per seconde om de browser-CPU te ontlasten.
- [ ] Dark Mode / UI Design: TailwindCSS gebruiken om het dashboard een echte "Cyberpunk" of "Enterprise" look te geven.
- [ ] Status Indicatoren: Lampjes in de UI die groen/rood worden op basis van de verbinding met Kafka.

### 📜 Fase 5: ALM & DevOps (Professionalisering)

- [ ] GitHub Actions: Een echte CI-pipeline maken die je Docker images bouwt en pusht zodra je een commit doet (zodat je niet meer handmatig docker build hoeft te typen).
- [ ] Health Checks: Liveness en Readiness probes toevoegen in je Helm charts zodat Kubernetes kapotte containers automatisch herstart.