const STORAGE_KEY = "billdeskDemoTransaction";

function $(id) {
  return document.getElementById(id);
}

function saveTransaction(data) {
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(data));
}

function getTransaction() {
  try {
    return JSON.parse(sessionStorage.getItem(STORAGE_KEY) || "null");
  } catch {
    return null;
  }
}

function generatePrn() {
  const stamp = Date.now().toString(36).toUpperCase();
  const random = Math.random().toString(36).substring(2, 7).toUpperCase();
  return `PRN-${stamp}-${random}`;
}

function rupees(value) {
  return `₹${Number(value || 0).toFixed(2)}`;
}

function setLoading(button, loading, text = "Processing...") {
  if (!button) return;
  if (loading) {
    button.dataset.originalText = button.textContent;
    button.disabled = true;
    button.textContent = text;
  } else {
    button.disabled = false;
    button.textContent = button.dataset.originalText || button.textContent;
  }
}

function showError(id, message) {
  const el = $(id);
  if (!el) return;
  el.textContent = message;
  el.classList.remove("hidden");
}

async function sha256(value) {
  const data = new TextEncoder().encode(value);
  const hash = await crypto.subtle.digest("SHA-256", data);
  return Array.from(new Uint8Array(hash))
    .map(b => b.toString(16).padStart(2, "0"))
    .join("");
}

async function createPaymentChecksum(payment) {
  const input = [
    payment.md,
    payment.pid,
    payment.nar,
    payment.prn,
    payment.amt,
    payment.crn,
    payment.ru,
    CONFIG.CHECKSUM_KEY
  ].map(v => v ?? "").join("|");

  return sha256(input);
}

async function api(path, options = {}) {
  const response = await fetch(`${CONFIG.API_BASE_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {})
    }
  });

  const text = await response.text();
  let body = null;

  try {
    body = text ? JSON.parse(text) : null;
  } catch {
    body = text;
  }

  if (!response.ok) {
    const message =
      body?.message ||
      body?.error ||
      `Request failed with HTTP ${response.status}`;
    throw new Error(message);
  }

  return body;
}

function startCheckout() {
  const transaction = {
    merchantName: CONFIG.MERCHANT.NAME,
    amount: CONFIG.MERCHANT.AMOUNT,
    currency: CONFIG.MERCHANT.CURRENCY,
    payeeId: CONFIG.MERCHANT.PAYEE_ID,
    paymentMethod: "Net Banking",
    bank: "DBS Corporate Bank",
    prn: generatePrn(),
    scenario: "NORMAL",
    delayMs: 0
  };

  saveTransaction(transaction);
  window.location.href = "checkout.html";
}

function goToScenarios() {
  const transaction = getTransaction();

  if (!transaction) {
    window.location.href = "index.html";
    return;
  }

  transaction.paymentMethod = "Net Banking";
  transaction.bank = $("bank")?.value || "DBS Corporate Bank";

  saveTransaction(transaction);

  window.open(
    "scenario.html",
    "scenarioWindow",
    "width=900,height=700"
  );
}

function selectScenario(button) {
  document.querySelectorAll(".scenario").forEach(el => el.classList.remove("selected"));
  button.classList.add("selected");

  const mode = button.dataset.mode;
  const transaction = getTransaction();

  if (transaction) {
    transaction.scenario = mode;
    transaction.delayMs = mode === "DELAY"
      ? Number($("delayMs")?.value || 3000)
      : 0;
    saveTransaction(transaction);
  }

  const labels = {
    NORMAL: "Normal",
    SUCCESS: "Force Success",
    FAILURE: "Force Failure",
    PENDING: "Force Pending",
    DELAY: "Delayed Callback",
    DROP: "Drop Callback",
    DUPLICATE: "Duplicate Callback"
  };

  if ($("scenarioLabel")) $("scenarioLabel").textContent = labels[mode] || mode;
  if ($("delayBox")) $("delayBox").classList.toggle("hidden", mode !== "DELAY");
}

async function processScenario() {
  const transaction = getTransaction();
  const selected = document.querySelector(".scenario.selected");
  const mode = selected?.dataset.mode || transaction?.scenario || "NORMAL";
  const delayMs = mode === "DELAY" ? Number($("delayMs")?.value || 3000) : 0;

  if (!transaction) {
    window.location.href = "index.html";
    return;
  }

  transaction.scenario = mode;
  transaction.delayMs = delayMs;
  saveTransaction(transaction);

  const button = $("continueBtn");
  setLoading(button, true, mode === "DELAY" ? "Waiting for bank..." : "Processing...");

  try {
    // Configure the simulator BEFORE /bank/payment because PaymentService
    // determines the transaction status from the saved override.
    await api("/control/override", {
      method: "POST",
      body: JSON.stringify({
        prn: transaction.prn,
        simulationMode: mode,
        delayMs
      })
    });

    const payment = {
      md: "P",
      pid: transaction.payeeId,
      nar: transaction.merchantName,
      prn: transaction.prn,
      amt: transaction.amount,
      crn: transaction.currency,
      ru: CONFIG.CALLBACK_URL,
      accno: "DBS-DEMO-001",
      date: new Date().toISOString(),
      data: ""
    };

    payment.checkVal = await createPaymentChecksum(payment);

    const response = await api("/bank/payment", {
      method: "POST",
      body: JSON.stringify(payment)
    });
    console.log("PAYMENT RESPONSE FROM BACKEND:", response);
console.log("PAYMENT STATUS:", response?.status);

    transaction.paymentResponse = response;
transaction.statusCode = response?.status || null;

if (window.opener && !window.opener.closed) {
  window.opener.sessionStorage.setItem(STORAGE_KEY, JSON.stringify(transaction));
  window.opener.location.href = "receipt.html";
  window.close();
} else {
  saveTransaction(transaction);
  window.location.href = "receipt.html";
}
  } catch (error) {
    showError("scenarioError", error.message);
    setLoading(button, false);
  }
}

function goBack() {
  window.history.back();
}

function finishTransaction() {
  if (window.opener && !window.opener.closed) {
    window.opener.location.href = "index.html";
    window.close();
  } else {
    window.location.href = "index.html";
  }
}

// function statusInfo(code) {
//   switch (code) {
//     case "Y":
//       return {
//         label: "SUCCESS",
//         title: "Payment Successful",
//         message: "The bank has accepted the transaction.",
//         className: "success"
//       };
//     case "P":
//       return {
//         label: "PENDING",
//         title: "Payment Pending",
//         message: "The bank has received the transaction and marked it pending.",
//         className: "pending"
//       };
//     default:
//       return {
//         label: "FAILED",
//         title: "Payment Failed",
//         message: "The bank simulator returned a failed transaction.",
//         className: "failure"
//       };
//   }
// }

function statusInfo(code, scenario) {

  if (scenario === "DROP") {
    return {
      label: "CALLBACK NOT RECEIVED",
      title: "Callback Not Received",
      message: "The bank processed the transaction, but no callback was received.",
      className: "pending"
    };
  }

  switch (code) {
    case "Y":
      return {
        label: "SUCCESS",
        title: "Payment Successful",
        message: "The bank has accepted the transaction.",
        className: "success"
      };

    case "P":
      return {
        label: "PENDING",
        title: "Payment Pending",
        message: "The bank has received the transaction and marked it pending.",
        className: "pending"
      };

    case "N":
      return {
        label: "FAILED",
        title: "Payment Failed",
        message: "The bank simulator returned a failed transaction.",
        className: "failure"
      };

    default:
      return {
        label: "UNKNOWN",
        title: "Transaction Status Unknown",
        message: "The transaction status could not be confirmed.",
        className: "pending"
      };
  }
}
function scenarioLabel(mode) {
  return {
    NORMAL: "Normal",
    FORCE_SUCCESS: "Force Success",
    FORCE_FAILURE: "Force Failure",
    FORCE_PENDING: "Force Pending",
    DELAY: "Delayed Callback",
    DROP: "Drop Callback",
    DUPLICATE: "Duplicate Callback"
  }[mode] || mode || "Normal";
}

function hydrateCheckout() {
  const tx = getTransaction();
  if (!tx) return;

  if ($("checkoutAmount")) $("checkoutAmount").textContent = rupees(tx.amount);
  if ($("summaryAmount")) $("summaryAmount").textContent = rupees(tx.amount);
  if ($("merchantName")) $("merchantName").textContent = tx.merchantName;
}

function hydrateScenario() {
  let tx = getTransaction();

  // Read callback data sent by the gateway
  const encodedData = new URLSearchParams(window.location.search).get("data");

  if (encodedData) {
    try {
      const callback = JSON.parse(encodedData);

      console.log("CALLBACK RECEIVED BY FRONTEND:", callback);

      // Create/update transaction from gateway callback
      tx = {
        ...(tx || {}),
        prn: callback.prn,
        merchantName: callback.nar || "AudioHub Retail",
        amount: callback.amt || "0.00",
        currency: "INR",
        payeeId: callback.pid || "DEMO-MERCHANT",
        paymentMethod: "Net Banking",
        bank: "DBS Corporate Bank",
        scenario: "SUCCESS",
        delayMs: 0,
        paymentResponse: callback,
        statusCode: callback.status
      };

      saveTransaction(tx);

      // Remove callback data from URL after saving it
      window.history.replaceState(
        {},
        document.title,
        "scenario.html"
      );

    } catch (error) {
      console.error("FAILED TO READ CALLBACK DATA:", error);
    }
  }

  if (!tx) {
    console.log("NO TRANSACTION");
    window.location.href = "index.html";
    return;
  }

  if ($("prn")) {
    $("prn").textContent = tx.prn;
  }

  const button = document.querySelector(
    `.scenario[data-mode="${tx.scenario || "NORMAL"}"]`
  );

  if (button) {
    selectScenario(button);
  }
}

function hydrateResult() {
  const tx = getTransaction();
  if (!tx) {
    window.location.href = "index.html";
    return;
  }

  const info = statusInfo(
  tx.paymentResponse?.status || tx.statusCode,
  tx.scenario
);
  const response = tx.paymentResponse || {};

  if ($("resultIcon")) {
    $("resultIcon").textContent =
      info.className === "success" ? "✓" :
      info.className === "pending" ? "…" : "×";
    $("resultIcon").className = `result-icon ${info.className}`;
  }

  if ($("resultTitle")) $("resultTitle").textContent = info.title;
  if ($("resultMessage")) $("resultMessage").textContent =
    response.errormsg || info.message;
  if ($("resultStatus")) $("resultStatus").textContent = info.label;
  if ($("resultPrn")) $("resultPrn").textContent = tx.prn;
  if ($("resultAmount")) $("resultAmount").textContent = rupees(response.amt || tx.amount);
  if ($("resultScenario")) $("resultScenario").textContent = scenarioLabel(tx.scenario);
}

function hydrateReceipt() {
  const tx = getTransaction();
  if (!tx) {
    window.location.href = "index.html";
    return;
  }

  const payment = tx.paymentResponse || {};
  // const info = statusInfo(payment.status || tx.statusCode);
  const info = statusInfo(
  payment.status || tx.statusCode,
  tx.scenario
);

  if ($("receiptIcon")) {
    $("receiptIcon").textContent =
      info.className === "success" ? "✓" :
      info.className === "pending" ? "…" : "×";
    $("receiptIcon").className = `result-icon ${info.className}`;
  }

  if ($("receiptTitle")) $("receiptTitle").textContent =
    info.className === "success" ? "Transaction Successful" :
    info.className === "pending" ? "Transaction Pending" :
    "Transaction Failed";

  if ($("receiptMessage")) $("receiptMessage").textContent =
    payment.errormsg || info.message;

  if ($("receiptAmount")) $("receiptAmount").textContent =
    rupees(payment.amt || tx.amount);

  if ($("receiptStatus")) $("receiptStatus").textContent = info.label;
  if ($("receiptPrn")) $("receiptPrn").textContent = tx.prn;
  if ($("receiptScenario")) $("receiptScenario").textContent = scenarioLabel(tx.scenario);
  if ($("receiptMerchant")) $("receiptMerchant").textContent = tx.merchantName || "—";
  if ($("receiptBank")) $("receiptBank").textContent = tx.bank || "DBS Corporate Bank";
  if ($("receiptMethod")) $("receiptMethod").textContent = tx.paymentMethod || "Net Banking";
}

document.addEventListener("DOMContentLoaded", () => {
  const page = location.pathname.split("/").pop();

  if (page === "checkout.html") hydrateCheckout();
  if (page === "scenario.html") hydrateScenario();
  if (page === "result.html") hydrateResult();
  if (page === "receipt.html") hydrateReceipt();
});