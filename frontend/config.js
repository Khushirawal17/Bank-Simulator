// Configuration for the local Bank Simulator.
const CONFIG = {
  API_BASE_URL: "http://localhost:9090",
  CHECKSUM_KEY: "TEST123",

  // The bank simulator calls this URL after payment processing.
  // Keep it pointed at the simulator's own callback endpoint.
  CALLBACK_URL: "http://localhost:9090/bank/callback",

  MERCHANT: {
    PAYEE_ID: "DEMO-MERCHANT",
    NAME: "Demo Merchant",
    AMOUNT: "999.00",
    CURRENCY: "INR"
  }
};
