# Stage 1: Build
FROM node:20-slim AS builder
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
# We compileren de server.ts handmatig naar JS
RUN npx tsc server.ts --esModuleInterop --skipLibCheck --target esnext --moduleResolution node --outDir dist

# Bouw de Next.js app
RUN npm run build

# Stage 2: Runtime
FROM node:20-slim AS runner
WORKDIR /app

ENV NODE_ENV=production

# Kopieer alleen wat nodig is
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/node_modules ./node_modules
# Kopieer de gecompileerde server
COPY --from=builder /app/dist/server.js ./server.js

EXPOSE 3000
EXPOSE 3001

# We starten nu een gewoon JS bestand met node. Geen loaders of ts-node meer nodig!
CMD ["sh", "-c", "node server.js & npm run start"]