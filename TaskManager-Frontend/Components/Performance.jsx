import React, { useState } from "react";
import CpuCard from "./CpuCard.jsx";
import MemoryCard from "./MemoryCard.jsx";
import DiskCard from "./DiskCard.jsx";
import NetworkCard from "./NetworkCard.jsx";
import GpuCard from "./GpuCard.jsx";

const PerfNavItem = ({ label, value, onClick, isSelected }) => (
  <button
    onClick={onClick}
    className={`w-full p-3 text-left rounded-md transition-colors duration-150 ${
      isSelected ? "bg-brand-violet/30" : "hover:bg-brand-dark"
    }`}
  >
    <div className="text-sm font-semibold text-brand-text">{label}</div>
    <div className="text-xs text-brand-text-secondary mt-1">{value}</div>
  </button>
);

const Performance = ({ metrics, history }) => {
  const [selected, setSelected] = useState("cpu");

  const renderDetails = () => {
    if (!metrics) return null;
    switch (selected) {
      case "cpu":
        return metrics.cpu ? (
          <CpuCard cpu={metrics.cpu} history={history.cpu} />
        ) : null;
      case "memory":
        return metrics.memory ? (
          <MemoryCard memory={metrics.memory} history={history.memory} />
        ) : null;
      case "disk":
        return metrics.disk ? <DiskCard disks={metrics.disk} /> : null;
      case "network":
        return metrics.network ? (
          <NetworkCard networks={metrics.network} />
        ) : null;
      case "gpus":
        return metrics.gpus ? <GpuCard gpus={metrics.gpus} /> : null;
      default:
        return metrics.cpu ? (
          <CpuCard cpu={metrics.cpu} history={history.cpu} />
        ) : null;
    }
  };

  const metricsList = [
    {
      id: "cpu",
      label: "CPU",
      value: `${parseFloat(metrics.cpu?.systemLoad || 0).toFixed(1)}%`,
    },
    {
      id: "memory",
      label: "Memory",
      value: `${metrics.memory?.usedGB?.toFixed(1) || "N/A"} GB / ${
        metrics.memory?.totalGB?.toFixed(1) || "N/A"
      } GB`,
    },
    {
      id: "disk",
      label: "Disk",
      value: `${metrics.disk?.[0]?.model || "N/A"}`,
    },
    {
      id: "network",
      label: "Network",
      value: `${metrics.network?.[0]?.displayName || "N/A"}`,
    },
    {
      id: "gpus",
      label: "GPU",
      value: `${metrics.gpus?.[0]?.name || "N/A"}`,
    },
  ];

  return (
    <div className="flex h-full">
      <div className="w-48 flex-shrink-0 space-y-1 p-4 border-r border-gray-800 overflow-y-auto">
        {metricsList.map(
          (m) =>
            metrics[m.id] &&
            (!Array.isArray(metrics[m.id]) || metrics[m.id].length > 0) && (
              <PerfNavItem
                key={m.id}
                label={m.label}
                value={m.value}
                onClick={() => setSelected(m.id)}
                isSelected={selected === m.id}
              />
            )
        )}
      </div>
      <div className="flex-1 overflow-y-auto">{renderDetails()}</div>
    </div>
  );
};

export default Performance;
