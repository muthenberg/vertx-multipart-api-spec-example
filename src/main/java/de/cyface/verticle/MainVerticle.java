package de.cyface.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class MainVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
//        super.start(startFuture);

        OpenAPI3RouterFactory.create(vertx, this.getClass().getResource("/openapi.yml").getFile(), r -> {
            if (r.succeeded()) {
                OpenAPI3RouterFactory routerFactory = r.result();
                routerFactory.setBodyHandler(BodyHandler.create().setDeleteUploadedFilesOnEnd(false));
                routerFactory.addHandlerByOperationId("upload", result -> {
                });
                routerFactory.setNotImplementedFailureHandler(result -> result.response().setStatusCode(501).end());
                routerFactory.setValidationFailureHandler(result -> result.response().setStatusCode(400).end());
                final Router router = routerFactory.getRouter();
                vertx.createHttpServer().requestHandler(router).listen(8080, serverStartup -> {
                    if (serverStartup.succeeded()) {
                        startFuture.complete();
                    } else {
                        startFuture.fail(serverStartup.cause());
                    }
                });
            } else {
                startFuture.fail(r.cause());
            }
        });
    }

}
