# Quick Http Server

![Version](https://img.shields.io/badge/Version-1.1.2-green) ![Java](https://img.shields.io/badge/Java-17-orange)

Local web server (http only). Useful for local dev.
Can also be a starting point for a simple Spring Boot Web app.

## Run it from source

To run it from source you need at least Java 17.

    run/run quickserver [--local] [--port=3000] --source=path/to/directory

**--local** [Optional] Makes the server bind to localhost (127.0.0.1) only.

**--port** [Optional] Specifies which port to serve files from.

**--source** The directory from which to start serving. All files starting at the specified source will be available to a requester, with CORS permitted.
