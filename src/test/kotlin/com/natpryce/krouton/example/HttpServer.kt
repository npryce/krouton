package com.natpryce.krouton.example

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_MOVED_TEMP
import java.net.InetSocketAddress
import java.net.URI

val HttpServer.uri: URI get() = URI("http", null, address.address.hostAddress, address.port, null, null, null)

fun HttpExchange.sendString(s: String) {
    sendResponseHeaders(200, 0)
    responseBody.bufferedWriter().use { w ->
        w.write(s)
    }
}

fun HttpExchange.sendOk() {
    sendResponseHeaders(HttpURLConnection.HTTP_OK, 0)
    close()
}

fun HttpExchange.sendError(statusCode: Int) {
    sendResponseHeaders(statusCode, 0)
    close()
}

fun HttpExchange.sendRedirect(path: String, status: Int = HTTP_MOVED_TEMP) {
    responseHeaders.add("Location", path)
    sendResponseHeaders(status, 0)
    close()
}

fun HttpServer(port: Int = 0, handler: (HttpExchange)->Unit) =
        HttpServer.create(InetSocketAddress("127.0.0.1", port), 0).apply {
            createContext("/", handler)
        }

val HTTP_FOUND = 307