# Java HTTP Server

A custom **multi-threaded HTTP server** built from scratch in **Java** using raw sockets.  
Implements fundamental web server functionality — request parsing, routing, concurrency control, and performance tuning — without relying on external frameworks.

---

## Features

- **Full HTTP/1.1 Support**
  - Handles `GET` and `POST` requests  
  - Serves static files (HTML, CSS, JS) and dynamic responses through handler interfaces  

- **Thread Pool Architecture**
  - Uses a fixed-size thread pool to handle concurrent clients efficiently  
  - Prevents thread explosion under heavy load  

- **Router & Handlers**
  - Modular routing system maps URL paths to custom handler classes  
  - `HttpHandler` interface allows easy extension for new endpoints  

- **Performance Tuning**
  - Tuned socket backlog and thread pool size for optimal throughput  
  - Benchmarked with 100–500 concurrent clients  
  - Achieved:
    - **14,000+ req/sec (POST)** at 6.8 ms average latency  
    - **30,000+ req/sec (GET)** at 30.9 ms average latency  
    - **95th percentile latency < 100 ms**

- **Static File Serving**
  - Streams static files directly from disk using buffered I/O  
  - Supports common MIME types and caching headers  

- **Error Handling**
  - Returns appropriate HTTP codes for malformed requests, missing routes, and internal errors  

---

## Architecture Overview



### 1. Connection Layer
- The server starts a `ServerSocket` that continuously listens for incoming TCP connections on a specified port (default: 8000).
- Each new connection is handed off to a **fixed-size thread pool**, which ensures efficient resource use and prevents unbounded thread creation under load.

### 2. Request Parsing
- Each worker thread reads the raw HTTP request stream line-by-line.
- The parser extracts:
  - Request method (`GET`, `POST`, etc.)
  - Target path and query parameters
  - Headers and content length
  - Request body (for POST/PUT)
- The data is wrapped in a `HttpRequest` object for easy downstream use.

### 3. Routing Layer
- The parsed request is passed to a **Router**, which maps paths to specific handler classes.
- The router supports dynamic route registration (e.g., `/upload`, `/echo`).
- If no route matches, a `404 Not Found` response is returned automatically.

### 4. Handler Execution
- Each route implements an `HttpHandler` interface with a `handle(HttpRequest request)` method.
- Handlers can:
  - Return dynamic text or JSON responses  
  - Read and write files  
  - Process form or JSON POST bodies  

### 5. Response Builder
- The handler returns a `HttpResponse` object containing:
  - Status line (e.g., `HTTP/1.1 200 OK`)
  - Headers (e.g., `Content-Type`, `Content-Length`)
  - Response body (string or byte array)

### 6. Static File Engine
- For `GET` requests targeting files, the static file engine:
  - Locates the file in the server’s root directory
  - Determines its MIME type
  - Streams the file in buffered chunks to reduce memory overhead
- Missing or restricted files trigger `403` or `404` responses automatically.

### 7. Shutdown
- On shutdown, all active threads complete their current requests.
- The thread pool and sockets are closed cleanly




