## E‑Auctions Manager

Full‑stack web application for running online auctions: users can create auctions, bid on items, and the system automatically selects winners when auctions finish.

This project is designed as a clean, portfolio‑ready example of a transactional system with authentication, role‑based access control, server‑side validation, and scheduled background jobs.

---

### Tech stack

- **Backend**: Java 17, Spring Boot, Spring Security, Spring Data JPA
- **Frontend**: Angular, TypeScript, SCSS, Bootstrap
- **Database**: MySQL / MariaDB (any relational DB supported by Spring can be configured)
- **Build / Dev tools**: Gradle, npm

---

### Features

- **User accounts**
  - Registration, login, logout.
  - Profile update and password change.

- **Auctions**
  - Authenticated users can create auctions with:
    - title, description, starting price, end date, photo upload.
  - Owners see and manage their own auctions in the “My auctions” view.
  - Auctions are deactivated after end date.

- **Bidding**
  - Logged‑in users can bid on auctions they do not own.
  - Server‑side checks:
    - bid only on active auctions that have not finished,
    - new bid must be strictly greater than current price,
    - owner cannot bid on their own auction.
  - Current price is computed from the `bids` table and falls back to starting price when there are no bids.

- **Guest view**
  - Public list of active auctions with:
    - photo, title, seller username, start price, current price, end date.
  - Logged‑in users see:
    - badge for their own auctions (`Twoja aukcja`),
    - “Twoja oferta” badge when they hold the highest bid,
    - inline form to place bids and contextual error messages.

- **My auctions (seller dashboard)**
  - List of auctions created by the logged‑in user.
  - For each auction:
    - starting price, current price,
    - highest bidder username when there are any bids.

- **Automatic winner selection**
  - Background job runs every minute:
    - finds active auctions whose end date has passed,
    - if there were bids, writes the highest bid and bidder into `auction_winners`,
    - marks those auctions as inactive.

---

### Project structure (high level)

- **Backend** (`src/main/java/com/example/demo`)
  - `entity/` – `User`, `Auction`, `Bids`, `AuctionWinners` etc.
  - `repository/` – Spring Data repositories (`AuctionRepository`, `BidsRepository`, `AuctionWinnersRepository`, `UserRepository`).
  - `services/` – business logic, e.g. `AuctionService` (creating auctions, bidding, closing auctions and writing winners).
  - `controller/` – REST controllers, e.g. `AuctionController` (`/api/auction/...`).
  - `dto/Response/` – response models (`GuestAuctionResponse`, `MyAuctionResponse`).

- **Frontend** (`frontend/`)
  - `src/app/_models/` – shared interfaces, e.g. `Auction`.
  - `src/app/_services/` – HTTP clients (`auction.service.ts`, `user.service.ts`).
  - `src/app/guest-view/` – public auction list and bidding UI.
  - `src/app/my-auctions/` – seller dashboard for “my auctions”.
  - `src/app/login/`, `src/app/register/`, `src/app/account-details/` – auth and profile.
  - Global styles in `src/styles.scss`.

---

### Prerequisites

- **Java** 17+
- **Node.js** 18+ and **npm**
- **MySQL / MariaDB** running locally (or another SQL DB configured via Spring)

---

### Backend – how to run

1. **Create a database**

   Example for MySQL:

   ```sql
   CREATE DATABASE eauctions CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **Configure connection**

   In `src/main/resources/application.properties` (or `application.yml`), configure at least:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/eauctions
   spring.datasource.username=YOUR_DB_USER
   spring.datasource.password=YOUR_DB_PASSWORD

   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   ```

3. **Create uploads directory for photos**

   Product photos are stored on disk and the `uploads/auctions` directory is ignored by Git.  
   Make sure it exists before running the app:

   ```bash
   mkdir -p uploads/auctions   # PowerShell: New-Item -ItemType Directory -Force uploads/auctions
   ```

4. **Build & run**

   From the project root:

   ```bash
   ./gradlew bootRun        # Linux / macOS
   gradlew.bat bootRun      # Windows
   ```

   The backend API will be available at `http://localhost:8080/api`.

---

### Frontend – how to run

1. **Install dependencies**

   ```bash
   cd frontend
   npm install
   ```

2. **Configure API URL**

   In `frontend/src/app/environments/environment.ts` (or equivalent), ensure:

   ```ts
   export const environment = {
     production: false,
     apiUrl: 'http://localhost:8080/api'
   };
   ```

   If you use `proxy.conf.json`, keep it consistent with your backend port.

3. **Start Angular dev server**

   ```bash
   npm start
   ```

   The SPA will run at `http://localhost:4200/`.

---

### Typical development flow

1. Start **backend** with `bootRun`.
2. Start **frontend** with `npm start`.
3. Register a user and log in through the UI.
4. Create a new auction (with photo) and verify:
   - it appears in **guest view** and **my auctions**,
   - bidding works from another account and errors are shown correctly.
5. Let the auction end and wait up to one minute:
   - a row for the winner is written into `auction_winners`,
   - the auction becomes inactive and no further bids are accepted.

---

### Possible extensions

- Email notifications (outbid, auction won).
- “My wins” page based on `auction_winners`.
- Pagination and advanced filters for large numbers of auctions.
- Dockerization (Dockerfile + docker‑compose) for one‑command startup.

This project demonstrates end‑to‑end ownership of a non‑trivial full‑stack system: data modeling, domain logic, background processing, secure APIs, and a modern SPA frontend.