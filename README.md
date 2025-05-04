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
```

# 📚 What I Learned / Project Evolution

This project started as a challenge to build a simple orchestrator in Java, but quickly grew into a deep learning journey covering key backend and systems-level concepts:

## ✅ From Thread-per-Socket → Selector-based Multiplexing
At first, the design used one thread per socket (similar to blocking I/O models). While functional for a small number of connections, I realized this design doesn’t scale under high connection counts because each thread consumes stack and scheduler resources.

I then discovered and migrated to Java NIO with a selector-based design, where one thread can monitor thousands of sockets simultaneously—solving the scalability bottleneck.

## ✅ Understanding the Kernel’s Role in I/O Events
I learned that the Java selector doesn’t “watch” the sockets directly—rather, it queries the OS kernel for readiness. This helped me understand that data may already exist in the kernel buffer even if the selector hasn’t yet signaled readiness, and clarified why non-blocking I/O works without spinning threads per connection.

## ✅ Handling Handshake Verification Without Blocking Threads
Initially, I tried reading immediately after accepting a socket, expecting the client to send a service declaration right away. I realized this could block a worker thread indefinitely if the client didn’t send data immediately.

I explored multiple solutions:
- Blocking read inside a dedicated handshake thread pool
- Non-blocking selector approach combined with a timeout sweep to forcibly close sockets that didn’t declare within X seconds
- Maintaining connection state (verified/unverified) mapped per socket

This forced me to balance scalability (non-blocking, no thread starvation) and security (no unauthorized socket lingering unverified).

## ✅ Architectural Separation of Concerns
Throughout the project, I prioritized clean separation between socket management, request verification, and business logic. Instead of coupling all logic into the selector loop, I delegated processing into separate “Chain” and “Link” modules, simulating a middleware chain pattern.

## ✅ Writing a Protocol from Scratch
Instead of using frameworks (Netty, Akka, etc.), I wanted to build from first principles. I implemented a simple protocol where a client must declare its service upon connecting. I enforced that a socket’s identity can’t change mid-session—aligning with good security practices and reducing potential abuse vectors.

## ✅ Improved Operational Awareness
I discovered subtle but critical system-level considerations:
- Why relying only on OP_READ events risks “silent dead connections” from idle clients
- Why blocking reads inside event loops kill scalability
- Why selector keys must be cleared explicitly after processing
- The importance of flip() in ByteBuffer to prepare for reading buffer content
