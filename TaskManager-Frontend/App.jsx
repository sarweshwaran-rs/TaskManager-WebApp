import React, { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";
import Header from "./components/Header.jsx";
import Sidebar from "../TaskManager-Frontend/Components/Sidebar.jsx";
import Dashboard from "../TaskManager-Frontend/Components/Dashboard.jsx";
import Performance from "../TaskManager-Frontend/Components/Performance.jsx";
import ProcessList from "../TaskManager-Frontend/Components/ProcessList.jsx";

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
  const [activeView, setActiveView] = useState("dashboard");

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
            const newHistory = { ...prevHistory };

            if (data.cpu && data.cpu.systemLoad) {
              newHistory.cpu = [
                ...prevHistory.cpu,
                parseFloat(data.cpu.systemLoad),
              ].slice(-MAX_HISTORY_LENGTH);
            }

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
          <p className="text-brand-text-secondary animate-pulse">
            {{
              Connecting: "Connecting to server...",
              Connected: "Waiting for initial data from server...",
              Error: "Connection error. Please check the server and refresh.",
            }[connectionStatus] || "Loading..."}
          </p>
        </div>
      );
    }
    switch (activeView) {
      case "dashboard":
        return <Dashboard data={latestMetrics} history={history} />;
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
        return <Dashboard data={latestMetrics} history={history} />;
    }
  };

  return (
    <div className="bg-brand-dark text-brand-text h-screen font-sans flex">
      <Sidebar activeView={activeView} setActiveView={setActiveView} />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header status={connectionStatus} />
        <main className="flex-1 overflow-y-auto">{renderActiveView()}</main>
      </div>
    </div>
  );
}
export default App;
