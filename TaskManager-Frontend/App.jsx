import React, { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";
import Header from "./components/Header.jsx";
import CpuCard from "./components/CpuCard.jsx";
import MemoryCard from "./components/MemoryCard.jsx";
import ProcessList from "./components/ProcessList.jsx";
import OsCard from "./components/OsCard.jsx";
import DiskCard from "./components/DiskCard.jsx";
import NetworkCard from "./components/NetworkCard.jsx";
import GpuCard from "./components/GpuCard.jsx";

const WEBSOCKET_URL = "ws://localhost:8080/ws";

function App() {
  const [latestMetrics, setLatestMetrics] = useState(null);
  const [history, setHistory] = useState({
    cpu: [],
    memory: [],
  });
  const [connectionStatus, setConnectionStatus] = useState("Connecting...");

  useEffect(() => {
    const MAX_HISTORY_LENGTH = 30; // Keep last 30 data points

    const client = new Client({
      brokerURL: WEBSOCKET_URL,
      reconnectDelay: 5000,
      onConnect: () => {
        setConnectionStatus("Connected");
        client.subscribe("/api/metrics", (message) => {
          const data = JSON.parse(message.body);
          setLatestMetrics(data);

          setHistory((prevHistory) => {
            const newCpuHistory = [
              ...prevHistory.cpu,
              parseFloat(data.cpu.systemLoad),
            ].slice(-MAX_HISTORY_LENGTH);

            const newMemoryHistory = [
              ...prevHistory.memory,
              (data.memory.usedGB / data.memory.totalGB) * 100,
            ].slice(-MAX_HISTORY_LENGTH);

            return {
              cpu: newCpuHistory,
              memory: newMemoryHistory,
            };
          });
        });
      },
      onStompError: (frame) => {
        console.error("Broker reported error: " + frame.headers["message"]);
        setConnectionStatus("Error");
      },
      onWebSocketError: () => {
        setConnectionStatus("Error");
      },
    });

    client.activate();

    return () => {
      client.deactivate();
    };
  }, []);

  return (
    <div className="bg-brand-dark text-brand-text min-h-screen font-sans">
      <Header status={connectionStatus} />
      <main>
        {latestMetrics ? (
          <div className="p-6 grid grid-cols-1 lg:grid-cols-12 gap-6">
            {/* Main stats cards with charts */}
            <div className="lg:col-span-8">
              {latestMetrics.cpu && (
                <CpuCard cpu={latestMetrics.cpu} history={history.cpu} />
              )}
            </div>
            <div className="lg:col-span-4">
              {latestMetrics.memory && (
                <MemoryCard
                  memory={latestMetrics.memory}
                  history={history.memory}
                />
              )}
            </div>

            {/* Full-width process list */}
            <div className="lg:col-span-12">
              {latestMetrics.processes && (
                <ProcessList processes={latestMetrics.processes} />
              )}
            </div>

            {/* Other info cards */}
            <div className="lg:col-span-4">
              {latestMetrics.os && latestMetrics.cpu && (
                <OsCard
                  os={latestMetrics.os}
                  uptime={latestMetrics.cpu.Uptime}
                />
              )}
            </div>
            <div className="lg:col-span-4">
              {latestMetrics.disk && <DiskCard disks={latestMetrics.disk} />}
            </div>
            <div className="lg:col-span-4">
              {latestMetrics.network && (
                <NetworkCard networks={latestMetrics.network} />
              )}
            </div>
            <div className="lg:col-span-4">
              {latestMetrics.gpu && <GpuCard gpus={latestMetrics.gpu} />}
            </div>
          </div>
        ) : (
          <div className="p-6 m-6 bg-brand-card rounded-lg shadow-lg text-center">
            Connecting to server and waiting for data...
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
