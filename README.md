# Quick Http Server

Local web server (http only). Useful for local dev.
Can also be used as an example of a basic Spring Boot Web app.

## Run it from source

To run it from source you need at least Java 17.

    ./run quickserver [--local] [--port=3000] --source=path/to/directory

**--local** [Optional] Makes the server bind to localhost (127.0.0.1) only.

**--port** [Optional] Specifies which port to serve files from.

**--source** The directory from which to start serving. All files starting at the specified source will be available to a requester, with CORS permitted.
