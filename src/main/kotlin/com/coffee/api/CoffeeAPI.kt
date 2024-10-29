package com.coffee.api

import com.coffee.api.roaster.PostgresRoasterRepository
import com.coffee.api.roaster.Roaster
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.format.Moshi.autoBody
import org.http4k.routing.path
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction


fun main() {

    Database.connect(
        url = "jdbc:postgresql://localhost:5432/mycoffeeapp",
        driver = "org.postgresql.Driver",
        user = "remi",
        password = "postgres",
    )
    transaction {
        addLogger(StdOutSqlLogger) // Add the logger here for global logging
    }
    coffeeAPI().asServer(SunHttp(port = 9000)).start()
}

val allRoastersLens = autoBody<List<Roaster>>().toLens()
val roasterLens = autoBody<Roaster>().toLens()

fun coffeeAPI(): HttpHandler {
    val repository = PostgresRoasterRepository();

    return routes(
        "/" bind GET to {
            Response(OK).body("Coffee API")
        },

        "/roasters" bind GET to {
            val roastersList = repository.allRoasters()
            allRoastersLens.inject(roastersList, Response(OK))
        },

        "/roasters/byName/{name}" bind GET to { request ->
            val name = request.path("name")
            val roaster = repository.roasterByName("$name")
            if (roaster == null) {
                Response(NOT_FOUND)
            } else {
                roasterLens.inject(roaster, Response(OK))
            }
        },

        "/roasters/byId/{id}" bind GET to { request ->
            val id = request.path("id")
            val roaster = repository.roasterById("$id")
            if(roaster == null) {
                Response(NOT_FOUND)
            } else {
                roasterLens.inject(roaster, Response(OK))
            }
        },

        "/roasters" bind Method.POST to { request ->
            runCatching {
                val newRoaster = roasterLens.extract(request)
                if (newRoaster.isValid()) {
                    repository.addRoaster(newRoaster)
                    Response(NO_CONTENT)
                } else {
                    Response(Status.BAD_REQUEST).body("Invalid roaster data")
                }
            }.getOrElse {
                Response(Status.BAD_REQUEST).body("Invalid Data")
            }
        },

        "/roasters/{name}" bind Method.DELETE to { request ->
            val name = request.path("name")
            val roaster = repository.roasterByName("$name")
            if (roaster != null) {
                repository.removeRoaster("$name")
                Response(NO_CONTENT)
            } else {
                Response(NOT_FOUND)
            }
        }
    ).withFilter(DebuggingFilters.PrintRequestAndResponse().then(ServerFilters.CatchAll()))
}

fun Roaster.isValid(): Boolean {
    val (name, url, address) = this
    return name.isNotEmpty() && url.isNotEmpty() && address.isNotEmpty()
}
