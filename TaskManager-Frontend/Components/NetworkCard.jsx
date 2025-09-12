import React from "react";
import InfoCard from "./InfoCard.jsx";

const NetworkCard = ({ networks }) => {
  return (
    <InfoCard title="Network">
      {networks.map((net, index) => (
        <div
          key={index}
          className="p-3 mb-2 border border-gray-700 rounded-md last:mb-0"
        >
          <h3 className="font-semibold text-sm text-brand-text">
            {net["Display Name"]} ({net.Type})
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-1 text-xs text-brand-text-secondary mt-1">
            <div>
              <strong className="font-medium text-brand-text">IPv4:</strong>{" "}
              {net["IP V4 Address"]}
            </div>
            <div>
              <strong className="font-medium text-brand-text">Upload:</strong>{" "}
              {net["Upload Speed"]}
            </div>
            <div>
              <strong className="font-medium text-brand-text">Download:</strong>{" "}
              {net["Download Speed"]}
            </div>
          </div>
          <div className="text-xs text-gray-600 mt-2">{net["MAC Address"]}</div>
        </div>
      ))}
    </InfoCard>
  );
};

export default NetworkCard;
