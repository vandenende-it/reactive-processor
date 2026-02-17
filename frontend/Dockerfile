FROM node:20-slim AS builder
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

FROM node:20-slim AS runner
WORKDIR /app

ENV NODE_ENV=production

COPY --from=builder /app/package*.json ./
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/server.ts ./server.ts

RUN npm install -g ts-node typescript @types/node

EXPOSE 3000
EXPOSE 3001

# Start script: We draaien de BFF en Next.js tegelijk
# dit is tijdelijke alles-in-één oplossing.
CMD ["sh", "-c", "ts-node server.ts & npm run start"]