import React from "react";
import LineChart from "./LineChart.jsx";
import Stat from "./Stat.jsx";

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
      borderColor: "rgb(139, 92, 246)", // brand-violet
      borderWidth: 2,
      backgroundColor: "rgba(139, 92, 246, 0.2)",
    },
  },
};

const CpuCard = ({ cpu, history }) => {
  const systemLoad = parseFloat(cpu.systemLoad);

  const chartData = {
    labels: history.map((_, i) => i),
    datasets: [
      {
        label: "CPU Load",
        data: history,
        fill: true,
      },
    ],
  };

  return (
    <div className="bg-brand-card rounded-lg shadow-lg p-6 flex flex-col">
      <h2 className="text-xl font-bold text-brand-text border-b border-gray-700 pb-3 mb-4">
        CPU
      </h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 flex-grow">
        {/* Left side: Stats */}
        <div>
          <div className="font-semibold text-brand-text mb-4">{cpu.name}</div>
          <div className="grid grid-cols-3 gap-4 mb-6">
            <Stat label="Load" value={`${systemLoad.toFixed(1)}%`} />
            <Stat label="Temp" value={cpu.Temperature} />
            <Stat label="Freq" value={cpu.frequency} />
          </div>
          <div>
            <h3 className="text-base font-semibold text-brand-text mb-2">
              Logical Cores
            </h3>
            <div className="grid grid-cols-4 md:grid-cols-8 gap-3">
              {cpu.perProcessorLoad.map((load, index) => (
                <div key={index} className="text-center">
                  <div className="w-full bg-gray-700 rounded-full h-1.5 my-1 overflow-hidden">
                    <div
                      className="bg-brand-sky h-full rounded-full transition-all duration-500"
                      style={{ width: `${parseFloat(load)}%` }}
                    ></div>
                  </div>
                  <span className="text-xs text-brand-text-secondary">
                    {parseFloat(load).toFixed(0)}%
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right side: Chart */}
        <div className="relative h-48 md:h-full">
          <LineChart data={chartData} options={chartOptions} />
        </div>
      </div>
    </div>
  );
};

export default CpuCard;
