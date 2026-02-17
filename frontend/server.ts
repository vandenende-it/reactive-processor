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
    await consumer.connect();
    await consumer.subscribe({ topic: 'dashboard-data' });

    await consumer.run({
        eachMessage: async ({ message }) => {
            if (message.value) {
                // Hier komt de data binnen (later met Protobuf!)
                const rawData = message.value.toString();
                // Broadcast direct naar alle verbonden browsers
                io.emit('metrics', JSON.parse(rawData));
            }
        },
    });

    server.listen(3001, () => {
        console.log('🚀 BFF draait op poort 3001 - Wachten op Kafka data...');
    });
}

startBFF().catch(console.error);