'use client';
import { useEffect, useState } from 'react';
import { io } from 'socket.io-client';
import { Line } from 'react-chartjs-2';

import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
} from 'chart.js';

// Registreer alles wat we nodig hebben voor een line chart
ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

export default function Dashboard() {
  const [dataPoints, setDataPoints] = useState<number[]>([]);

  useEffect(() => {
      const socket = io("http://reactive.local", {
          path: "/socket.io", // De Ingress stuurt dit pad door naar poort 3001
          transports: ["websocket"]
      });

    socket.on('metrics', (data) => {
      setDataPoints(prev => [...prev.slice(-20), data.temp]); // Houd laatste 20 punten
    });

    return () => { socket.disconnect(); };
  }, []);

    const chartData = {
        // Zorg dat er altijd minimaal 1 label is
        labels: dataPoints.length > 0 ? dataPoints.map((_, i) => i) : [0],
        datasets: [{
            label: 'Real-time Temperatuur',
            data: dataPoints.length > 0 ? dataPoints : [0],
            borderColor: 'rgb(75, 192, 192)',
            tension: 0.1
        }]
    };

  return (
      <main className="p-10">
        <h1 className="text-2xl font-bold mb-5">Enterprise IoT Dashboard</h1>
        <div className="bg-white p-5 rounded-lg shadow">
          <Line data={chartData} options={{ animation: false }} />
        </div>
      </main>
  );
}