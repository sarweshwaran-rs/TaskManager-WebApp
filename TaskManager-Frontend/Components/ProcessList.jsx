import React, { useState, useMemo } from "react";

// Helper to format bytes
const formatBytes = (bytes, decimals = 2) => {
  if (!+bytes) return "0 Bytes";
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ["Bytes", "KB", "MB", "GB", "TB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
};

const ProcessList = ({ processes }) => {
  const [sortConfig, setSortConfig] = useState({
    key: "cpuLoad",
    direction: "descending",
  });

  const sortedProcesses = useMemo(() => {
    let sortableItems = [...processes];
    if (sortConfig !== null) {
      sortableItems.sort((a, b) => {
        if (a[sortConfig.key] < b[sortConfig.key]) {
          return sortConfig.direction === "ascending" ? -1 : 1;
        }
        if (a[sortConfig.key] > b[sortConfig.key]) {
          return sortConfig.direction === "ascending" ? 1 : -1;
        }
        return 0;
      });
    }
    return sortableItems;
  }, [processes, sortConfig]);

  const requestSort = (key) => {
    let direction = "ascending";
    if (sortConfig.key === key && sortConfig.direction === "ascending") {
      direction = "descending";
    }
    setSortConfig({ key, direction });
  };

  const getSortIndicator = (key) => {
    if (sortConfig.key === key) {
      return sortConfig.direction === "ascending" ? " ▲" : " ▼";
    }
    return "";
  };

  return (
    <div className="bg-brand-card rounded-lg shadow-lg p-6">
      <h2 className="text-xl font-bold text-brand-text border-b border-gray-700 pb-3 mb-4">
        Top Processes
      </h2>
      <div className="overflow-x-auto max-h-96">
        <table className="w-full text-sm text-left text-brand-text-secondary">
          <thead className="text-xs text-brand-text uppercase bg-gray-700/50 sticky top-0">
            <tr>
              <th
                scope="col"
                className="px-6 py-3 cursor-pointer hover:bg-gray-700"
                onClick={() => requestSort("pid")}
              >
                PID{getSortIndicator("pid")}
              </th>
              <th
                scope="col"
                className="px-6 py-3 cursor-pointer hover:bg-gray-700"
                onClick={() => requestSort("name")}
              >
                Name{getSortIndicator("name")}
              </th>
              <th
                scope="col"
                className="px-6 py-3 cursor-pointer hover:bg-gray-700"
                onClick={() => requestSort("cpuLoad")}
              >
                CPU %{getSortIndicator("cpuLoad")}
              </th>
              <th
                scope="col"
                className="px-6 py-3 cursor-pointer hover:bg-gray-700"
                onClick={() => requestSort("memory")}
              >
                Memory{getSortIndicator("memory")}
              </th>
            </tr>
          </thead>
          <tbody>
            {sortedProcesses.map((p) => (
              <tr
                key={p.pid}
                className="border-b border-gray-700 hover:bg-gray-700/50"
              >
                <td className="px-6 py-3 font-medium text-brand-text whitespace-nowrap">
                  {p.pid}
                </td>
                <td className="px-6 py-3">{p.name}</td>
                <td className="px-6 py-3">{p.cpuLoad.toFixed(2)}</td>
                <td className="px-6 py-3">{formatBytes(p.memory)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ProcessList;
