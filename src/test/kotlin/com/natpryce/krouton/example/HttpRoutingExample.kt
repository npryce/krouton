package com.natpryce.krouton.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isEmptyString
import com.natpryce.hamkrest.present
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
import java.time.LocalDate.now

// An application-specific mapping between parsed URL elements and typed data
object _LocalDate : Projection3<Int,Int,Int,LocalDate> {
    override fun fromParts(t1: Int, t2: Int, t3: Int) =
            try { LocalDate.of(t1, t2, t3) } catch (e: DateTimeException) { null }

    override fun toParts(u: LocalDate) =
            u.year to u.monthValue to u.dayOfMonth
}

// Route components
val year = int
val month = int
val day = int
val date = year/month/day asA _LocalDate

// Application routes
val reverse = "reverse" / string
val reversed = "reversed" / string
val negate = "negate" / int
val negative = "negative" / int
val weekday = "weekday" / date
val weekdayToday = root / "weekday" / "today"


// The server that uses the routes
fun exampleServer(port: Int = 0) = HttpServer(port) { exchange ->
    routeOn(exchange.requestURI.rawPath,
            root by {
                exchange.sendString("Hello, World.")
            },

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

            reversed by { s ->
                exchange.sendRedirect(reversed.path(s))
            },

            weekday by { date ->
                exchange.sendString(date.dayOfWeek.name.toLowerCase())
            },

            weekdayToday by {
                // Note - reverse routing using user-defined projection
                exchange.sendRedirect(weekday.path(now()))
            }

    ) otherwise {
        exchange.sendError(HTTP_NOT_FOUND)
    }
}


class HttpRoutingExample {
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

    @Test
    fun weekday_today() {
        assertThat(getText("/weekday/today"), present(!isEmptyString))
    }

    @Test(expected = FileNotFoundException::class)
    fun bad_dates_not_found() {
        getText("/weekday/2016/02/30")
    }

    @Test
    fun root() {
        assertThat(getText("/"), equalTo("Hello, World."))
    }

    companion object {
        val server = exampleServer()

        @BeforeClass @JvmStatic
        fun startServer() {
            server.start()
        }

        @AfterClass @JvmStatic
        fun stopServer() {
            server.stop(0)
        }
    }

    private fun getText(path: String) = server.uri.resolve(path).toURL()
            .openStream().reader().use { it.readText().trim() }

}