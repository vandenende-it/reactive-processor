'use client';
import { useEffect, useState } from 'react';
import { io } from 'socket.io-client';
import { Line } from 'react-chartjs-2';
// ... Chart.js imports ...

export default function Dashboard() {
  const [dataPoints, setDataPoints] = useState<number[]>([]);

  useEffect(() => {
    const socket = io('http://localhost:3001');

    socket.on('metrics', (data) => {
      setDataPoints(prev => [...prev.slice(-20), data.temp]); // Houd laatste 20 punten
    });

    return () => { socket.disconnect(); };
  }, []);

  const chartData = {
    labels: dataPoints.map((_, i) => i),
    datasets: [{ label: 'Real-time Temperatuur', data: dataPoints, borderColor: 'rgb(75, 192, 192)' }]
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