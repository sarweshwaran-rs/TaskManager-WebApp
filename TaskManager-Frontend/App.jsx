import React, { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";
import Header from "./Components/Header.jsx";
import CpuCard from "./Components/CpuCard.jsx";
import MemoryCard from "./Components/MemoryCard.jsx";
import ProcessList from "./Components/ProcessList.jsx";
import OsCard from "./Components/OsCard.jsx";
import DiskCard from "./Components/DiskCard.jsx";
import NetworkCard from "./Components/NetworkCard.jsx";
import GpuCard from "./Components/GpuCard.jsx";
import Sidebar from "./Components/Sidebar.jsx";
import Performance from "./Components/Performance.jsx";

const WEBSOCKET_URL = "ws://localhost:8080/ws";

const PlaceholderView = ({ title }) => (
  <div className="bg-brand-card rounded-lg shadow-lg text-center p-6">
    <h2 className="text-xl font-bold mb-4 text-brand-text">{title}</h2>
    <p className="text-brand-text-secondary">
      This feature is not yet implemented.
    </p>
  </div>
);

function App() {
  const [latestMetrics, setLatestMetrics] = useState(null);
  const [history, setHistory] = useState({
    cpu: [],
    memory: [],
  });
  const [connectionStatus, setConnectionStatus] = useState("Connecting...");
  const [activeView, setActiveView] = useState("performance");

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
<<<<<<< HEAD
            const cpuLoad = parseFloat(data.cpu?.systemLoad || 0);
            const newCpuHistory = [...prevHistory.cpu, cpuLoad].slice(
              -MAX_HISTORY_LENGTH
            );

            const totalMem = parseFloat(data.memory?.totalGB);
            const usedMem = parseFloat(data.memory?.usedGB);
            const memPercent = totalMem > 0 ? (usedMem / totalMem) * 100 : 0;
            const newMemoryHistory = [...prevHistory.memory, memPercent].slice(
              -MAX_HISTORY_LENGTH
            );
=======
            const newHistory = { ...prevHistory };

            if (data.cpu && data.cpu.systemLoad) {
              newHistory.cpu = [
                ...prevHistory.cpu,
                parseFloat(data.cpu.systemLoad),
              ].slice(-MAX_HISTORY_LENGTH);
            }
>>>>>>> upstream/backend

            if (data.memory && data.memory.usedGB && data.memory.totalGB) {
              const used = parseFloat(data.memory.usedGB);
              const total = parseFloat(data.memory.totalGB);
              newHistory.memory = [
                ...prevHistory.memory,
                total > 0 ? (used / total) * 100 : 0,
              ].slice(-MAX_HISTORY_LENGTH);
            }

            return newHistory;
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

  const renderActiveView = () => {
    if (!latestMetrics) {
      return (
        <div className="p-6 m-6 bg-brand-card rounded-lg shadow-lg text-center">
          <p className="text-brand-text-secondary">
            {connectionStatus} to server and waiting for data...
          </p>
        </div>
      );
    }
    switch (activeView) {
      case "processes":
        return <ProcessList processes={latestMetrics.processes} />;
      case "performance":
        return <Performance metrics={latestMetrics} history={history} />;
      case "app-history":
        return <PlaceholderView title="App history" />;
      case "startup":
        return <PlaceholderView title="Startup apps" />;
      case "users":
        return <PlaceholderView title="Users" />;
      case "details":
        return <PlaceholderView title="Details" />;
      case "services":
        return <PlaceholderView title="Services" />;
      default:
        return <Performance metrics={latestMetrics} history={history} />;
    }
  };

  return (
    <div className="bg-brand-dark text-brand-text h-screen font-sans flex flex-col">
      <Header status={connectionStatus} />
      <div className="flex flex-1 overflow-hidden">
        <Sidebar activeView={activeView} setActiveView={setActiveView} />
        <main className="flex-1 overflow-y-auto p-6">{renderActiveView()}</main>
      </div>
    </div>
  );
}
export default App;
