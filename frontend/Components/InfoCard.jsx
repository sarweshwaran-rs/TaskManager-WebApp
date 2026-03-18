import React from "react";

const InfoCard = ({ title, children }) => {
  return (
    <div className="bg-brand-card rounded-lg shadow-lg p-6 h-full">
      <h2 className="text-lg font-bold text-brand-text border-b border-gray-700 pb-3 mb-4">
        {title}
      </h2>
      {children}
    </div>
  );
};

export default InfoCard;
