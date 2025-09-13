import React from "react";
import CpuCard from "../CpuCard.jsx";
import MemoryCard from "../MemoryCard.jsx";
import ProcessList from "../ProcessList.jsx";
import OsCard from "../OsCard.jsx";
import DiskCard from "../DiskCard.jsx";
import NetworkCard from "../NetworkCard.jsx";
import GpuCard from "../GpuCard.jsx";

const Dashboard = ({ data, history }) => {
  return (
    <div className="p-6 grid grid-cols-1 lg:grid-cols-12 gap-6">
      {/* Main stats cards with charts */}
      <div className="lg:col-span-8">
        {data.cpu && <CpuCard cpu={data.cpu} history={history.cpu} />}
      </div>
      <div className="lg:col-span-4">
        {data.memory && (
          <MemoryCard memory={data.memory} history={history.memory} />
        )}
      </div>

      {/* Full-width process list */}
      <div className="lg:col-span-12">
        {data.processes && <ProcessList processes={data.processes} />}
      </div>

      {/* Other info cards */}
      <div className="lg:col-span-4">
        {data.os && data.cpu && (
          <OsCard os={data.os} uptime={data.cpu.Uptime} />
        )}
      </div>
      <div className="lg:col-span-4">
        {data.disk && <DiskCard disks={data.disk} />}
      </div>
      <div className="lg:col-span-4">
        {data.network && <NetworkCard networks={data.network} />}
      </div>
      <div className="lg:col-span-4">
        {data.gpu && <GpuCard gpus={data.gpu} />}
      </div>
    </div>
  );
};

export default Dashboard;
