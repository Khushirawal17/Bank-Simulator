# BillDesk Bank Simulator Frontend

Vanilla HTML/CSS/JavaScript frontend for the Spring Boot Bank Simulator.

## Folder

Place this folder beside the backend:

```text
Project/
├── Bank-Simulator/
└── frontend/
    ├── index.html
    ├── checkout.html
    ├── scenario.html
    ├── result.html
    ├── receipt.html
    ├── config.js
    ├── app.js
    └── styles.css
```

## Backend API used

- `POST /control/override`
- `POST /bank/payment`
- `POST /bank/verification`
- `POST /bank/callback`

The frontend configures the simulation override first, then sends the payment request. This is necessary because the backend's `PaymentService` calls `SimulationService.determineStatus(prn)` while processing `/bank/payment`.

## Important

`config.js` contains:

```js
API_BASE_URL: "http://localhost:9090"
CHECKSUM_KEY: "TEST123"
CALLBACK_URL: "http://localhost:9090/bank/callback"
```

The checksum key must match:

```properties
bank.security.checksum-key=TEST123
```

The frontend reproduces the backend's SHA-256 payment checksum:

```text
md|pid|nar|prn|amt|crn|ru|checksumKey
```

## Running

Because the backend runs on port 9090, serve this folder from a local HTTP server rather than opening `index.html` directly.

For example, with VS Code Live Server:

1. Open the `frontend` folder.
2. Right-click `index.html`.
3. Choose **Open with Live Server**.
4. Open the displayed localhost URL.

## CORS

The current backend controllers do not define CORS rules. If the browser blocks requests from the frontend's localhost origin, add `@CrossOrigin` to the relevant controllers or configure global CORS in Spring Boot.

For example:

```java
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/bank")
public class PaymentController {
    ...
}
```

Use the actual origin/port shown by your frontend server.

## Flow

1. Merchant page
2. BillDesk payment selection
3. Bank scenario selection
4. Bank payment response
5. Verification / Process Transaction
6. Final receipt

The selected scenario is retained with `sessionStorage` so it can be displayed on the result and receipt pages.
