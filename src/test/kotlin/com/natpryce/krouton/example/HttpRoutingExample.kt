package com.natpryce.krouton.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.*
import com.natpryce.krouton.simple.by
import com.natpryce.krouton.simple.otherwise
import com.natpryce.krouton.simple.routeOn
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.FileNotFoundException
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.time.DateTimeException
import java.time.LocalDate

// An application-specific mapping between parsed URL elements and typed data
object LocalDate : Projection3<Int,Int,Int,LocalDate> {
    override fun fromParts(t1: Int, t2: Int, t3: Int) =
            try { LocalDate.of(t1, t2, t3) } catch (e: DateTimeException) { null }

    override fun toParts(u: LocalDate) =
            u.year to u.monthValue to u.dayOfMonth
}

// Route components
val year = int
val month = int
val day = int
val date = year/month/day asA LocalDate

// Application routes
val reverse = "reverse" / string
val negate = "negate" / int
val negative = "negative" / int
val weekday = "weekday" / date

// The server that uses the routes
val server = HttpServer(0) { exchange ->
    routeOn(exchange.requestURI.rawPath,
            negate by { i ->
                exchange.sendString((-i).toString())
            },

            negative by { i ->
                // Note - reverse routing from integer to URL path
                exchange.sendRedirect(negate.path(i))
            },

            reverse by { s ->
                exchange.sendString(s.reversed())
            },

            weekday by { date ->
                exchange.sendString(date.dayOfWeek.name.toLowerCase())
            },

            root by {
                exchange.sendString("Hello, World.")
            }


    ) otherwise {
        exchange.sendError(HTTP_NOT_FOUND)
    }
}


fun main(args: Array<String>) {
    server.start()
}

class HttpRoutingExample {
    companion object {
        @BeforeClass @JvmStatic
        fun startServer() {
            server.start()
        }

        @AfterClass @JvmStatic
        fun stopServer() {
            server.stop(0)
        }
    }

    @Test
    fun negate() {
        assertThat(getText("/negate/100"), equalTo("-100"))
    }

    @Test
    fun negative_redirects_to_negate() {
        assertThat(getText("/negative/20"), equalTo("-20"))
    }

    @Test
    fun reverse() {
        assertThat(getText("/reverse/hello%20world"), equalTo("dlrow olleh"))
    }

    @Test
    fun weekday() {
        assertThat(getText("/weekday/2016/02/29"), equalTo("monday"))
        assertThat(getText("/weekday/2016/03/01"), equalTo("tuesday"))
    }

    @Test(expected = FileNotFoundException::class)
    fun bad_dates_not_found() {
        getText("/weekday/2016/02/30")
    }

    @Test
    fun root() {
        assertThat(getText("/"), equalTo("Hello, World."))
    }

    private fun getText(path: String) = server.uri.resolve(path).toURL()
            .openStream().reader().use { it.readText().trim() }

}