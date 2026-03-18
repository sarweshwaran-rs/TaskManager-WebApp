import React from "react";
import InfoCard from "./InfoCard.jsx";

const GpuCard = ({ gpus }) => {
  return (
    <InfoCard title="Graphics">
      {gpus.map((gpu) => (
        <div
          key={gpu.deviceId}
          className="p-3 mb-2 border border-gray-700 rounded-md last:mb-0"
        >
          <h3 className="font-semibold text-sm text-brand-text">{gpu.name}</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-1 text-xs text-brand-text-secondary mt-1">
            <div>
              <strong className="font-medium text-brand-text">Vendor:</strong>{" "}
              {gpu.vendor}
            </div>
            <div>
              <strong className="font-medium text-brand-text">VRAM:</strong>{" "}
              {gpu.vram}
            </div>
            <div>
              <strong className="font-medium text-brand-text">Version:</strong>{" "}
              {gpu.versionInfo}
            </div>
          </div>
        </div>
      ))}
    </InfoCard>
  );
};

export default GpuCard;
