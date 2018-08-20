package com.natpryce.krouton.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isEmptyString
import com.natpryce.hamkrest.present
import com.natpryce.krouton.PathTemplate
import com.natpryce.krouton.PathTemplate2
import com.natpryce.krouton.Projection
import com.natpryce.krouton.Tuple3
import com.natpryce.krouton.asA
import com.natpryce.krouton.getValue
import com.natpryce.krouton.http4k.resources
import com.natpryce.krouton.int
import com.natpryce.krouton.locale
import com.natpryce.krouton.path
import com.natpryce.krouton.plus
import com.natpryce.krouton.root
import com.natpryce.krouton.string
import com.natpryce.krouton.tuple
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.MOVED_PERMANENTLY
import org.http4k.core.Status.Companion.OK
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.FileNotFoundException
import java.net.URI
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.format.DateTimeFormatter
import java.util.Locale


// An application-specific mapping between parsed URL elements and typed data
object LocalDate_ : Projection<Tuple3<Int, Int, Int>, LocalDate> {
    override fun fromParts(parts: Tuple3<Int, Int, Int>): LocalDate? {
        val (year, month, day) = parts
        return try {
            LocalDate.of(year, month, day)
        }
        catch (e: DateTimeException) {
            null
        }
    }
    
    override fun toParts(mapped: LocalDate) =
        tuple(mapped.year, mapped.monthValue, mapped.dayOfMonth)
}

// Components of the application's routes
val year by int
val month by int
val day by int
val date = year + month + day asA LocalDate_

// The application's routes
val reverse = root + "reverse" + string
val negate = root + "negate" + int

// Note: without these explicit type declarations, the Kotlin compiler crashes with an internal error
val weekday: PathTemplate2<Locale, LocalDate> = root + "weekday" + locale.named("locale") + date
val weekdayToday: PathTemplate<Locale> = root + "weekday" + locale.named("locale") + "today"

// Obsolete routes that each redirect to one of the routes above
val negative = root + "negative" + int
val reversed = root + "reversed" + string


// The server that uses the routes
fun exampleServer() = resources {
    root methods {
        GET { ok("Hello, World.") }
    }
    
    negate methods {
        GET { _, i -> ok((-i).toString()) }
    }
    
    negative methods {
        // Note - reverse routing from integer to URL path
        GET { _, i -> redirect(MOVED_PERMANENTLY, negate.path(i)) }
    }
    
    reverse methods {
        GET { _, s -> ok(s.reversed()) }
    }
    
    reversed methods {
        GET { _, s -> redirect(MOVED_PERMANENTLY, reverse.path(s)) }
    }
    
    weekday methods {
        GET { _, (locale, date) -> ok(date.format(DateTimeFormatter.ofPattern("EEEE", locale))) }
    }
    
    weekdayToday methods {
        /* Note - reverse routing using user-defined projection*/
        GET { _, locale -> redirect(FOUND, weekday.path(locale, now())) }
    }
}

private fun ok(s: String) =
    Response(OK).body(s)

private fun redirect(status: Status, location: String) =
    Response(status).header("Location", location)


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
        assertThat(getText("/weekday/en/2016/02/29"), equalTo("Monday"))
        assertThat(getText("/weekday/fr/2016/02/29"), equalTo("lundi"))
        assertThat(getText("/weekday/de/2016/02/29"), equalTo("Montag"))
        
        assertThat(getText("/weekday/en/2016/03/01"), equalTo("Tuesday"))
    }
    
    @Test
    fun weekday_today() {
        assertThat(getText("/weekday/en/today"), present(!isEmptyString))
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
        private val port = 8965
        private val server = exampleServer().asServer(SunHttp(port))
        val serverUri = URI("http://127.0.0.1:$port/")
        
        @BeforeClass
        @JvmStatic
        fun startServer() {
            server.start()
        }
        
        @AfterClass
        @JvmStatic
        fun stopServer() {
            server.stop()
        }
    }
    
    private fun getText(path: String): String {
        return serverUri.resolve(path).toURL()
            .openStream().reader().use { it.readText().trim() }
    }
}