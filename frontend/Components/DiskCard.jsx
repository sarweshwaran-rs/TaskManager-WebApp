import React from "react";
import InfoCard from "./InfoCard.jsx";

const DiskCard = ({ disks }) => {
  return (
    <InfoCard title="Disks">
      {disks.map((disk) => (
        <div
          key={disk.serial || disk.name}
          className="p-3 mb-2 border border-gray-700 rounded-md last:mb-0"
        >
          <h3 className="font-semibold text-sm text-brand-text">
            {disk.model} ({disk.size})
          </h3>
          <div className="grid grid-cols-2 gap-x-4 text-xs text-brand-text-secondary mt-1">
            <div>
              <strong className="font-medium text-brand-text">Read:</strong>{" "}
              {disk.readSpeed}
            </div>
            <div>
              <strong className="font-medium text-brand-text">Write:</strong>{" "}
              {disk.writeSpeed}
            </div>
          </div>
          <div className="text-xs text-brand-text-secondary mt-2">
            {disk.name}
          </div>
        </div>
      ))}
    </InfoCard>
  );
};

export default DiskCard;
