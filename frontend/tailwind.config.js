/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./*.{js,jsx}",
    "./components/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        "brand-dark": "#111827", // Dark background
        "brand-card": "#1f2937", // Card background
        "brand-text": "#d1d5db", // Primary text
        "brand-text-secondary": "#9ca3af", // Secondary text
        "brand-violet": "#8b5cf6", // Accent color for charts/buttons
        "brand-sky": "#38bdf8", // Secondary accent
      },
    },
  },
  plugins: [],
};
