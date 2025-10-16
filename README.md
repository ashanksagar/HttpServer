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



