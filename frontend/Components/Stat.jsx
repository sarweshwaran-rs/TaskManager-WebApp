import React from "react";

const Stat = ({ label, value }) => {
  return (
    <div className="text-center">
      <div className="text-2xl font-semibold text-brand-text">{value}</div>
      <div className="text-xs text-brand-text-secondary uppercase tracking-wider">
        {label}
      </div>
    </div>
  );
};

export default Stat;
