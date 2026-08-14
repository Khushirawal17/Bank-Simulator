const express = require("express");
const crypto = require("crypto");

const app = express();

const PORT = 4000;

const SECRET = "1234567890123456";

console.log("SECRET LENGTH:", Buffer.byteLength(SECRET, "utf8"));

app.use(express.text({ type: "text/plain" }));

app.post("/callback", (req, res) => {

    console.log("\n========================================");
    console.log("GATEWAY CALLBACK RECEIVED");
    console.log("========================================");

    try {

        console.log("Request body type:", typeof req.body);
        console.log("Request body length:", req.body?.length);

        const encryptedPayload = req.body.trim();

        console.log("Encrypted payload received.");
        console.log("Encrypted payload length:", encryptedPayload.length);

        // -----------------------------
        // DECRYPT
        // -----------------------------

        const jsonPayload = decrypt(encryptedPayload);

        console.log("\n========== DECRYPTED CALLBACK ==========");
        console.log(jsonPayload);
        console.log("========================================");

        // -----------------------------
        // PARSE JSON
        // -----------------------------

        const callback = JSON.parse(jsonPayload);

        console.log("\n========== CALLBACK DATA ==========");
        console.log("PRN    :", callback.prn);
        console.log("Status :", callback.status);
        console.log("Amount :", callback.amt);
        console.log("PID    :", callback.pid);
        console.log("NAR    :", callback.nar);
        console.log("===================================");

        // -----------------------------
        // CREATE FRONTEND URL
        // -----------------------------

        const encodedData =
    encodeURIComponent(jsonPayload);

const redirectUrl =
    `http://127.0.0.1:3000/scenario.html?data=${encodedData}`;

console.log("\n========== REDIRECT ==========");
console.log("Redirecting browser to:");
console.log(redirectUrl);
console.log("==============================");

return res.redirect(302, redirectUrl);

    } catch (error) {

        console.error("\n========== GATEWAY ERROR ==========");
        console.error(error);
        console.error("====================================");

        res.status(400).send("Invalid callback");
    }
});


function decrypt(encryptedPayload) {

    console.log("\n========== DECRYPTION START ==========");

    const combined =
        Buffer.from(encryptedPayload, "base64");

    console.log("Decoded bytes:", combined.length);

    if (combined.length < 12 + 16) {
        throw new Error(
            "Encrypted payload is too short"
        );
    }

    // --------------------------------
    // Java format:
    //
    // IV
    // +
    // ciphertext
    // +
    // GCM authentication tag
    // --------------------------------

    const iv =
        combined.subarray(0, 12);

    const encrypted =
        combined.subarray(12);

    console.log("IV length:", iv.length);
    console.log("Encrypted section length:", encrypted.length);

    // Last 16 bytes = GCM authentication tag

    const authTag =
        encrypted.subarray(
            encrypted.length - 16
        );

    const ciphertext =
        encrypted.subarray(
            0,
            encrypted.length - 16
        );

    console.log("Ciphertext length:", ciphertext.length);
    console.log("Auth tag length:", authTag.length);

    const key =
        Buffer.from(SECRET, "utf8");

    console.log("Key length:", key.length);

    // IMPORTANT:
    // Java secret is 16 bytes.
    // Therefore AES-128-GCM.

    const decipher =
        crypto.createDecipheriv(
            "aes-128-gcm",
            key,
            iv
        );

    decipher.setAuthTag(authTag);

    const decrypted =
        Buffer.concat([
            decipher.update(ciphertext),
            decipher.final()
        ]);

    console.log("Decrypted bytes:", decrypted.length);

    console.log("========== DECRYPTION SUCCESS ==========");

    return decrypted.toString("utf8");
}


app.listen(PORT, () => {

    console.log(
        `Gateway server running at http://localhost:${PORT}`
    );

    console.log(
        `Callback endpoint: http://localhost:${PORT}/callback`
    );
});