package com.natpryce.krouton.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.path
import com.natpryce.krouton.plus
import com.natpryce.krouton.unaryPlus
import com.oneeyedmen.minutest.junit.JupiterTests
import com.oneeyedmen.minutest.junit.context
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import java.time.LocalTime


class SyntaxExtensionTests: JupiterTests {
    private val internalRoot = +"internal"
    private val status = internalRoot + "status"
    private val config = internalRoot + "config"
    
    private fun ResourceRoutesBuilder.canBeMonitored() {
        status methods {
            GET {
                Response(OK).body("it's ${LocalTime.now()} and all's well")
            }
        }
        
        config methods {
            GET {
                Response(OK).body(System.getenv().entries.joinToString("\n") { (k,v) -> "$k = $v" })
            }
        }
    }
    
    private val myApp = +"myapp"
    
    val app = resources {
        myApp methods {
            GET {
                Response(OK).body("not a very useful app!")
            }
        }
        
        canBeMonitored()
    }
    
    override val tests = context<Unit> {
        test("can add common routes") {
            assertThat(app(Request(GET, myApp.path())).status, equalTo(OK))
            assertThat(app(Request(GET, status.path())).status, equalTo(OK))
            assertThat(app(Request(GET, config.path())).status, equalTo(OK))
            assertThat(app(Request(GET, "/not/there")).status, equalTo(NOT_FOUND))
        }
    }
}