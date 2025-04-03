[![progress-banner](https://backend.codecrafters.io/progress/http-server/26c3b405-11ae-45b0-bc9b-2da5a3d5fff7)](https://app.codecrafters.io/users/codecrafters-bot?r=2qF)

# HTTP Server Implementation in Java

Welcome to the Codecrafters HTTP Server project! This repository contains a simple HTTP server implemented in Java as part of the Codecrafters challenge. This project demonstrates understanding of network programming, HTTP protocol, concurrent programming, and Java's NIO capabilities. This project is an excellent demonstration of my understanding of core Java concepts and my ability to build applications from scratch.

## 🚀 Features

- **HTTP/1.1 Protocol Implementation**: Built following RFC 2616 specifications
- **Concurrent Request Handling**: Multi-threaded design to handle multiple client connections
- **RESTful Endpoints**:
  - `GET /`: Basic health check endpoint
  - `GET /echo/{message}`: Echo service that returns the message
  - `GET /user-agent`: Returns client's User-Agent
  - `GET /files/{filename}`: Serves static files
  - `POST /files/{filename}`: Uploads files to server
- **Static File Serving**: Ability to serve and receive files with proper content types
- **Custom Router Implementation**: Flexible routing system with path-based handlers
- **Error Handling**: Proper HTTP status codes and error responses

## 🛠 Technical Stack

- Java 23
- Maven for build automation
- Pure Java standard library (no external dependencies)
- TCP/IP Networking (java.net)
- Java NIO for file operations
- Multi-threading for concurrent connections

## 🏗 Architecture

The server follows a modular design with clear separation of concerns:

- **Main**: Server bootstrapping and configuration
- **Router**: Request routing and handler management
- **HttpRequest**: HTTP request parsing and representation
- **HttpResponse**: Response building and formatting
- **RequestHandler**: Interface for endpoint handlers
- **FilesHandler**: File operations handler

### Key Components

```
src/main/java/
├── Main.java           # Server entry point and configuration
├── Router.java         # Request routing logic
├── HttpRequest.java    # Request parsing and model
├── HttpResponse.java   # Response building and model
├── RequestHandler.java # Handler interface
└── FilesHandler.java   # File operations implementation
```

## 🔍 Key Learning Outcomes

1. **Network Programming**
   - Socket programming in Java
   - TCP/IP communication
   - HTTP protocol implementation

2. **Concurrent Programming**
   - Multi-threaded server design
   - Thread management
   - Resource synchronization

3. **Software Design**
   - Interface-based design
   - Separation of concerns
   - Modular architecture

4. **File System Operations**
   - File I/O using Java NIO
   - Content type handling
   - File upload/download operations

## 🚦 Getting Started

1. Clone the repository
```bash
git clone https://github.com/Md-Talim/codecrafters-http-server-java.git
cd codecrafters-http-server-java
```

2. Build the project
```bash
mvn clean package
```

3. Run the server
```bash
./your_program.sh [port] [directory]
```

## 🧪 Testing

Test the server using curl commands:

```bash
# Test echo endpoint
curl -v http://localhost:4221/echo/hello

# Test file upload
curl -v --data-binary "@file.txt" http://localhost:4221/files/file.txt

# Test file download
curl -v http://localhost:4221/files/file.txt
```

## 💡 Technical Challenges & Solutions

1. **Concurrent Connection Handling**
   - Implemented thread-per-connection model
   - Used try-with-resources for proper resource cleanup

2. **HTTP Protocol Compliance**
   - Careful implementation of request parsing
   - Proper header handling
   - Status code management

3. **File System Security**
   - Path validation
   - Content-Type determination
   - Error handling for file operations

## 📚 References

- [HTTP/1.1 Specification (RFC 2616)](https://tools.ietf.org/html/rfc2616)
- [Java Socket Programming Guide](https://docs.oracle.com/javase/tutorial/networking/sockets/)
- [Java NIO Documentation](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html)
