package com.natpryce.krouton.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.unaryPlus
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.Test

class RouterCompositionTests {
    private val routeX = +"x"
    private val appX = resources {
        routeX methods {
            GET { Response(OK).body("x") }
        }
    }
    
    private val routeY = +"y"
    private val appY = resources {
        routeY methods {
            GET { Response(OK).body("y") }
        }
    }
    
    @Test
    fun `you can add Krouton apps together`() {
        val composedApp = appX + appY
        
        val monolithicApp = resources {
            routeX methods {
                GET { Response(OK).body("x") }
            }
        
            routeY methods {
                GET { Response(OK).body("y") }
            }
        }
        
        assertThat(composedApp.urlTemplates(), equalTo(monolithicApp.urlTemplates()))
        assertThat(composedApp.router.handlerIfNoMatch, equalTo(appY.router.handlerIfNoMatch))
    }
}
