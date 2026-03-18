import React from "react";
import InfoCard from "./InfoCard.jsx";

const OsCard = ({ os, uptime }) => {
  return (
    <InfoCard title="System Information">
      <div className="text-sm space-y-2 text-brand-text-secondary">
        <p>
          <strong className="font-medium text-brand-text">OS:</strong>{" "}
          {os.family} {os.version}
        </p>
        <p>
          <strong className="font-medium text-brand-text">Manufacturer:</strong>{" "}
          {os.Manufacturer}
        </p>
        <p>
          <strong className="font-medium text-brand-text">Uptime:</strong>{" "}
          {uptime}
        </p>
      </div>
    </InfoCard>
  );
};

export default OsCard;
