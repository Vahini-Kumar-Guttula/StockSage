 # 📈 StockSage — Portfolio Stock Annualized Returns Calculator

**StockSage** is a portfolio analysis application that helps investors and portfolio managers calculate **annualized returns** for their stock holdings.  
It processes stock details from a JSON file, fetches price data from third-party APIs (Tiingo and AlphaVantage), and computes returns based on purchase and end dates.

---

## 🚀 Overview

StockSage is designed to analyze investment performance efficiently and accurately.  
It leverages **Factory Design Pattern** for extensible API integration and uses **multithreading** to fetch stock data in parallel — improving overall performance and responsiveness.

---

## ✨ Features

- 🔹 Fetches stock price data from **Tiingo** and **AlphaVantage** APIs  
- 🔹 Calculates **annualized returns** based on stock purchase and end dates  
- 🔹 Implements **Factory Design Pattern** to switch between different data providers  
- 🔹 Supports **multithreading** for efficient API calls  
- 🔹 Includes **robust exception handling** and input validation  
- 🔹 Fully **unit tested** for reliability and maintainability  
- 🔹 Modular and extensible codebase suitable for future API integrations  

---

## 🧠 Tech Stack & Skills Used

| Category | Technologies / Concepts |
|-----------|-------------------------|
| Language | Java |
| Framework | Spring Boot |
| Build Tool | Gradle |
| API Integration | REST APIs, HTTP Requests |
| JSON Processing | Jackson Library |
| Concurrency | Multithreading |
| Design Pattern | Factory Design Pattern |
| Testing | JUnit, Mockito |
| Others | Exception Handling, Code Refactoring |

---

## ⚙️ How It Works

1. **Input:**  
   Provide a JSON file containing stock symbols, purchase dates, and quantities, along with an **end date**.

2. **Process:**  
   - The application fetches historical stock prices from **Tiingo** or **AlphaVantage** APIs.  
   - It computes **annualized returns** using the formula:  
     \[
     Annualized Return = \left(\frac{EndPrice}{PurchasePrice}\right)^{\frac{1}{Years}} - 1
     \]
   - Data fetching is done concurrently for multiple stocks.

3. **Output:**  
   The results are returned in a **structured JSON format**, sorted by highest annualized return.

---

## 📦 Setup & Usage

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/<your-username>/StockSage.git
cd StockSage
