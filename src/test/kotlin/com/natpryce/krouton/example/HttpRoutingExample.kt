//package com.natpryce.krouton.example
//
//import com.natpryce.hamkrest.assertion.assertThat
//import com.natpryce.hamkrest.equalTo
//import com.natpryce.hamkrest.isEmptyString
//import com.natpryce.hamkrest.present
//import com.natpryce.krouton.*
//import com.natpryce.krouton.simple.by
//import com.natpryce.krouton.simple.otherwise
//import com.natpryce.krouton.simple.routeOn
//import org.junit.AfterClass
//import org.junit.BeforeClass
//import org.junit.Test
//import java.io.FileNotFoundException
//import java.net.HttpURLConnection.*
//import java.time.DateTimeException
//import java.time.LocalDate
//import java.time.LocalDate.now
//import java.time.format.DateTimeFormatter
//
//// Components of the application's routes
//val year = int
//val month = int
//val day = int
//val date = year + month + day asA LocalDate_
//
//// The application's routes
//val reverse = root + "reverse" + string
//val negate = root + "negate" + int
//val weekday = root + "weekday" + locale + date
//val weekdayToday = root + "weekday" + locale + "today"
//
//// Obsolete routes that each redirect to one of the routes above
//val negative = root + "negative" + int
//val reversed = root + "reversed" + string
//
//
//// An application-specific mapping between parsed URL elements and typed data
//object LocalDate_ : Projection<HStack3<Int, Int, Int>, LocalDate> {
//    override fun fromParts(parts: HStack3<Int, Int, Int>): LocalDate? {
//        val (year, month, day) = parts
//        return try {
//            LocalDate.of(year, month, day)
//        }
//        catch (e: DateTimeException) {
//            null
//        }
//    }
//
//    override fun toParts(mapped: LocalDate) =
//        Empty + mapped.year + mapped.monthValue + mapped.dayOfMonth
//}
//
//
//// The server that uses the routes
//fun exampleServer(port: Int = 0) = HttpServer(port) { exchange ->
//    routeOn(exchange.requestURI.rawPath,
//        root by {
//            exchange.sendString("Hello, World.")
//        },
//
//        negate by { i ->
//            exchange.sendString((-i).toString())
//        },
//
//        negative by { i ->
//            // Note - reverse routing from integer to URL path
//            exchange.sendRedirect(negate.path(i), HTTP_MOVED_PERM)
//        },
//
//        reverse by { s ->
//            exchange.sendString(s.reversed())
//        },
//
//        reversed by { s ->
//            exchange.sendRedirect(reverse.path(s), HTTP_MOVED_PERM)
//        },
//
//        weekday by { (locale, date) ->
//            exchange.sendString(date.format(DateTimeFormatter.ofPattern("EEEE", locale)))
//        },
//
//        weekdayToday by { locale ->
//            // Note - reverse routing using user-defined projection
//            exchange.sendRedirect(weekday.path(Empty + locale + now()), HTTP_FOUND)
//        }
//
//    ) otherwise {
//        exchange.sendError(HTTP_NOT_FOUND)
//    }
//}
//
//
//class HttpRoutingExample {
//    @Test
//    fun negate() {
//        assertThat(getText("/negate/100"), equalTo("-100"))
//    }
//
//    @Test
//    fun negative_redirects_to_negate() {
//        assertThat(getText("/negative/20"), equalTo("-20"))
//    }
//
//    @Test
//    fun reverse() {
//        assertThat(getText("/reverse/hello%20world"), equalTo("dlrow olleh"))
//    }
//
//    @Test
//    fun weekday() {
//        assertThat(getText("/weekday/en/2016/02/29"), equalTo("Monday"))
//        assertThat(getText("/weekday/fr/2016/02/29"), equalTo("lundi"))
//        assertThat(getText("/weekday/de/2016/02/29"), equalTo("Montag"))
//
//        assertThat(getText("/weekday/en/2016/03/01"), equalTo("Tuesday"))
//    }
//
//    @Test
//    fun weekday_today() {
//        assertThat(getText("/weekday/en/today"), present(!isEmptyString))
//    }
//
//    @Test(expected = FileNotFoundException::class)
//    fun bad_dates_not_found() {
//        getText("/weekday/2016/02/30")
//    }
//
//    @Test
//    fun root() {
//        assertThat(getText("/"), equalTo("Hello, World."))
//    }
//
//    companion object {
//        val server = exampleServer()
//
//        @BeforeClass
//        @JvmStatic
//        fun startServer() {
//            server.start()
//        }
//
//        @AfterClass
//        @JvmStatic
//        fun stopServer() {
//            server.stop(0)
//        }
//    }
//
//    private fun getText(path: String) = server.uri.resolve(path).toURL()
//        .openStream().reader().use { it.readText().trim() }
//
//}