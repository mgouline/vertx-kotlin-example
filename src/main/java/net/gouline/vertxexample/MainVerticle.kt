package net.gouline.vertxexample

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

@Suppress("unused")
class MainVerticle : AbstractVerticle() {

    private val MOCK_ISLANDS by lazy {
        listOf(
                Island("Kotlin", Country("Russia", "RU")),
                Island("Stewart Island", Country("New Zealand", "NZ")),
                Island("Cockatoo Island", Country("Australia", "AU")),
                Island("Tasmania", Country("Australia", "AU"))
        )
    }

    override fun start(startFuture: Future<Void>?) {
        val router = createRouter()

        vertx.createHttpServer()
                .requestHandler { router.accept(it) }
                .listen(Integer.getInteger("http.port", 8080)) { result ->
                    if (result.succeeded()) {
                        startFuture?.complete()
                    } else {
                        startFuture?.fail(result.cause())
                    }
                }
    }

    private fun createRouter() = Router.router(vertx).apply {
        get("/").handler(handlerRoot)
        get("/islands").handler(handlerIslands)
        get("/countries").handler(handlerCountries)
    }

    val handlerRoot = Handler<RoutingContext> { req ->
        req.response().end("Welcome!")
    }

    val handlerIslands = Handler<RoutingContext> { req ->
        req.response().endWithJson(MOCK_ISLANDS)
    }

    val handlerCountries = Handler<RoutingContext> { req ->
        val countries = MOCK_ISLANDS.map { it.country }.distinct().sortedBy { it.code }
        req.response().endWithJson(countries)
    }

    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(obj))
    }

}