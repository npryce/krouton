package com.natpryce.krouton.example

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import org.junit.AfterClass
import org.junit.BeforeClass
import java.net.HttpURLConnection
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

fun HttpExchange.sendRedirect(path: String) {
    responseHeaders.add("Location", path)
    sendResponseHeaders(HttpURLConnection.HTTP_MOVED_TEMP, 0)
    close()
}

fun HttpServer(port: Int = 0, handler: (HttpExchange)->Unit) =
        HttpServer.create(InetSocketAddress(port), 0).apply {
            createContext("/", handler)
        }

