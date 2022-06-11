package org.sebastian.Controladoras;

import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import org.sebastian.util.BaseControlador;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class ExcepcionesC extends BaseControlador {

    public ExcepcionesC(Javalin app) {
        super(app);
    }

    @Override
    public void aplicarRutas() {

        app.routes(() -> {
            path("/excepciones",() -> {
                // ver el listado completo en:
                // https://javalin.io/documentation#default-responses
                // ir a http://localhost:7000/excepciones/ruta-no-encontrada
                get("/ruta-no-encontrada", ctx -> {
                    throw new NotFoundResponse();
                });

                // ir a http://localhost:7000/excepciones/ruta-sin-permisos
                get("/ruta-sin-permisos", ctx -> {
                    throw new UnauthorizedResponse();
                });

                //ir a http://localhost:7000/excepciones/provocando-error
                get("/provocando-error", ctx -> {
                    ctx.result("Error: "+Integer.parseInt("gagdagsd"));
                });

            });
        });

        /**
         * Para el manejo de excepciones y codigo de errores
         */
        app.exception(NumberFormatException.class, (exception, ctx) -> {
            ctx.html("Ocurrió un error en la conversacion numerica: "+exception.getLocalizedMessage());
        });

        /**
         * Solo aplica cuando venga para vistas html.
         */
        app.error(404,"text/html", ctx -> {
            ctx.html("<h1>Recurso consultado no existe... Favor verificar...</h1>");
        });
    }
}
