# Java Orchestrator (Socket-based Microservice Orchestrator)

## 🚀 Overview

This project is a **lightweight orchestrator** that manages multiple microservices using **pure Java NIO (non-blocking I/O)** and socket communication.

It provides:

✅ Plug-and-play registration of services  
✅ Load balancing across service instances  
✅ Long-lived socket connections for message passing  
✅ Event-driven architecture using Java NIO selector  
✅ Thread pool for request handling without blocking selector thread

No external frameworks (Netty, Akka, etc) → **built from scratch to learn & appreciate the underlying mechanics.**

---

## 🏗️ Architecture

- Each incoming socket declares its intended service at connection time
- Services are verified upfront (unverified sockets are dropped)
- A selector thread handles readiness events
- A worker thread pool processes data concurrently
- Periodic cleanup closes stale unverified connections

_Designed for small-to-medium scale systems with ~40 concurrent sockets._

---

## 💻 Running

### Build:

```bash
mvn clean install

