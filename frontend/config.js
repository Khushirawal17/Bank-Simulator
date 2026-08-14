// Configuration for the local Bank Simulator.
const CONFIG = {
  API_BASE_URL: "http://localhost:9090",
  CHECKSUM_KEY: "TEST123",
  // The bank simulator calls this URL after payment processing.
  // Keep it pointed at the simulator's own callback endpoint.
  CALLBACK_URL: "http://localhost:4000/callback",
  MERCHANT: {
    PAYEE_ID: "DEMO-MERCHANT",
    NAME: "AudioHub Retail",
    AMOUNT: "999.00",
    CURRENCY: "INR"
  },
  PRODUCT: {
    TITLE: "boAt Rockerz 450 Wireless Bluetooth On-Ear Headphones",
    MRP: "2,499",
    DISCOUNT: "60% off",
    RATING: "4.3",
    RATING_COUNT: "48,213 ratings and 3,102 reviews"
  }
};