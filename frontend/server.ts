import { Kafka } from 'kafkajs';
import { Server } from 'socket.io';
import http from 'http';

const server = http.createServer();
const io = new Server(server, {
    cors: { origin: "*" } // In productie specifieker instellen
});

const kafka = new Kafka({
    brokers: [process.env.KAFKA_BROKERS || 'redpanda:9092']
});
const consumer = kafka.consumer({ groupId: 'bff-ui-group' });

async function startBFF() {
    try {
        console.log("🔄 Verbinding maken met Kafka...");
        await consumer.connect();
        await consumer.subscribe({ topic: 'dashboard-data', fromBeginning: false });
        console.log("✅ Verbonden met Kafka");

        await consumer.run({
            eachMessage: async ({ message }) => {
                if (message.value) {
                    io.emit('metrics', JSON.parse(message.value.toString()));
                }
            },
        });
    } catch (err) {
        const errorMessage = err instanceof Error ? err.message : String(err);
        console.error(`❌ Kafka fout: ${errorMessage}. Nieuwe poging over 5 seconden...`);

        // Wacht 5 seconden en probeer het opnieuw
        setTimeout(startBFF, 5000);
    }
}

// Start de HTTP/WebSocket server los van de Kafka connectie
server.listen(3001, () => {
    console.log('🚀 BFF server luistert op poort 3001');
    startBFF(); // Start de Kafka loop
});
startBFF().catch(console.error);