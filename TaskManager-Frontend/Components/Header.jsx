import React from "react";

const Header = ({ status }) => {
  let statusClass = "";
  if (status === "Connected") {
    statusClass = "connected";
  } else if (status === "Error") {
    statusClass = "error";
  }

  return (
    <header className="bg-brand-card/80 backdrop-blur-lg shadow-lg sticky top-0 z-10 border-b border-gray-700">
      <div className="container mx-auto px-6 py-4 flex justify-between items-center">
        <h1 className="text-2xl font-bold text-brand-text">
          System Monitor Dashboard
        </h1>
        <div className="flex items-center text-sm text-brand-text-secondary">
          <span
            className={`h-3 w-3 rounded-full mr-2 ${
              statusClass === "connected"
                ? "bg-green-400"
                : statusClass === "error"
                ? "bg-red-400"
                : "bg-yellow-400"
            } transition-colors duration-300`}
          ></span>
          <span id="status-text">{status}</span>
        </div>
      </div>
    </header>
  );
};

export default Header;
