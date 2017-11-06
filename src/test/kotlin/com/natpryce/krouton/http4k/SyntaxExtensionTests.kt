package com.natpryce.krouton.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.path
import com.natpryce.krouton.plus
import com.natpryce.krouton.unaryPlus
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.junit.Test
import java.time.LocalTime


class SyntaxExtensionTests {
    val internalRoot = +"internal"
    val status = internalRoot + "status"
    val config = internalRoot + "config"
    
    fun ResourceRoutesBuilder.canBeMonitored() {
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
    
    
    @Test
    fun `can add common routes`() {
        val app = resources {
            myApp methods {
                GET {
                    Response(OK).body("not a very useful app!")
                }
            }
            
            canBeMonitored()
        }
    
        assertThat(app(Request(GET, myApp.path())).status, equalTo(OK))
        assertThat(app(Request(GET, status.path())).status, equalTo(OK))
        assertThat(app(Request(GET, config.path())).status, equalTo(OK))
        assertThat(app(Request(GET, "/not/there")).status, equalTo(NOT_FOUND))
    }
}