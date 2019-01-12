package com.natpryce.krouton.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.unaryPlus
import com.oneeyedmen.minutest.rootContext
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK

fun `composition of routers`() = rootContext<Unit> {
    val routeX = +"x"
    val appX = resources {
        routeX methods {
            GET { Response(OK).body("x") }
        }
    }
    
    val routeY = +"y"
    val appY = resources {
        routeY methods {
            GET { Response(OK).body("y") }
        }
    }
    
    val composedApp = appX + appY
    
    val monolithicApp = resources {
        routeX methods {
            GET { Response(OK).body("x") }
        }
        
        routeY methods {
            GET { Response(OK).body("y") }
        }
    }
    
    test("you can add Krouton apps together") {
        assertThat(composedApp.urlTemplates(), equalTo(monolithicApp.urlTemplates()))
        assertThat(composedApp.router.handlerIfNoMatch, equalTo(appY.router.handlerIfNoMatch))
    }
}
