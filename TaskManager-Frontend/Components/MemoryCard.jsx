import React from "react";
import LineChart from "./LineChart.jsx";

const formatBytesToGB = (bytes) => {
  if (!bytes || bytes === 0) return "0 GB";
  return (bytes / (1024 * 1024 * 1024)).toFixed(1) + " GB";
};

const formatHertzToMHz = (hertz) => {
  if (!hertz || hertz === 0) return "N/A";
  return Math.round(hertz / 1000000) + " MHz";
};

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    x: {
      display: false,
    },
    y: {
      beginAtZero: true,
      max: 100,
      ticks: {
        stepSize: 25,
        callback: (value) => `${value}%`,
      },
    },
  },
  plugins: {
    legend: {
      display: false,
    },
    tooltip: {
      enabled: false,
    },
  },
  elements: {
    point: {
      radius: 0,
    },
    line: {
      tension: 0.3,
      borderColor: "rgb(56, 189, 248)", // brand-sky
      borderWidth: 2,
      backgroundColor: "rgba(56, 189, 248, 0.2)",
    },
  },
};

const MemoryCard = ({ memory, history }) => {
  const total = parseFloat(memory.total);
  const used = parseFloat(memory.used);
  const memPercent = total > 0 ? (used / total) * 100 : 0;

  const swapTotal = parseFloat(memory.swapTotal);
  const swapUsed = parseFloat(memory.swapUsed);
  const swapPercent = swapTotal > 0 ? (swapUsed / swapTotal) * 100 : 0;
  const physicalMemory = memory["Physical Memory"] || [];

  const chartData = {
    labels: history.map((_, i) => i),
    datasets: [
      {
        label: "Memory Usage",
        data: history,
        fill: true,
      },
    ],
  };

  return (
    <div className="bg-brand-card rounded-lg shadow-lg p-6 flex flex-col">
      <h2 className="text-xl font-bold text-brand-text border-b border-gray-700 pb-3 mb-4">
        Memory
      </h2>
      <div className="mb-4">
        <label className="block mb-1 font-medium text-brand-text-secondary text-sm">
          Physical Memory
        </label>
        <div className="w-full bg-gray-700 rounded-full h-4 overflow-hidden">
          <div
            className="bg-brand-sky h-full rounded-full transition-all duration-500"
            style={{ width: `${memPercent}%` }}
          ></div>
        </div>
        <span className="text-sm text-brand-text-secondary mt-1 block">{`${used.toFixed(
          1
        )} GB / ${total.toFixed(1)} GB`}</span>
      </div>
      <div className="mb-4">
        <label className="block mb-1 font-medium text-brand-text-secondary text-sm">
          Swap
        </label>
        <div className="w-full bg-gray-700 rounded-full h-4 overflow-hidden">
          <div
            className="bg-brand-sky h-full rounded-full transition-all duration-500"
            style={{ width: `${swapPercent}%` }}
          ></div>
        </div>
        <span className="text-sm text-brand-text-secondary mt-1 block">
          {swapTotal > 0
            ? `${swapUsed.toFixed(1)} GB / ${swapTotal.toFixed(1)} GB`
            : "N/A"}
        </span>
      </div>

      <div className="relative h-24 mb-4">
        <LineChart data={chartData} options={chartOptions} />
      </div>

      {physicalMemory.length > 0 && (
        <div className="mt-auto pt-4 border-t border-gray-700">
          <h3 className="text-base font-semibold text-brand-text pb-2 mb-3">
            Memory Slots
          </h3>
          {physicalMemory.map((stick, index) => (
            <div
              key={index}
              className="p-3 mb-2 border border-gray-700 rounded-md"
            >
              <h4 className="font-semibold text-sm text-brand-text">
                {stick.bankLabel}
              </h4>
              <div className="grid grid-cols-2 gap-x-4 gap-y-1 text-xs text-brand-text-secondary mt-1">
                <div>
                  <strong className="font-medium text-brand-text">
                    Capacity:
                  </strong>{" "}
                  {formatBytesToGB(stick.capacity)}
                </div>
                <div>
                  <strong className="font-medium text-brand-text">
                    Speed:
                  </strong>{" "}
                  {formatHertzToMHz(stick.clockSpeed)}
                </div>
                <div>
                  <strong className="font-medium text-brand-text">Type:</strong>{" "}
                  {stick.memoryType}
                </div>
                <div>
                  <strong className="font-medium text-brand-text">
                    Manufacturer:
                  </strong>{" "}
                  {stick.manufacturer}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MemoryCard;
