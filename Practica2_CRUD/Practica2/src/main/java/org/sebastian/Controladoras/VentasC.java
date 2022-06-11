package org.sebastian.Controladoras;

import io.javalin.Javalin;
import org.sebastian.Entidades.Carrito;
import org.sebastian.Entidades.Producto;
import org.sebastian.Entidades.Venta;
import org.sebastian.Services.VentaService;
import org.sebastian.util.BaseControlador;
import org.sebastian.util.RolesApp;

import java.text.SimpleDateFormat;
import java.util.*;

import static io.javalin.apibuilder.ApiBuilder.*;

public class VentasC extends BaseControlador {
    VentaService ventaService = VentaService.getInstancia();

    public VentasC(Javalin app) {
        super(app);
    }

    @Override
    public void aplicarRutas() {

        app.routes(() -> {
            path("/registro", () -> {

                get("/", ctx -> {
                    ctx.redirect("/registro/listar");
                }, Collections.singleton(RolesApp.ROLE_ADMIN));

                get("/listar", ctx -> {
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("lista", ventaService.getListaVentas());

                    ctx.render("publico/registro.html", modelo);
                });


            });
        });

        app.routes(() -> {
            path("/carrito", () ->{

                get("/", ctx -> {
                    Carrito carrito = ctx.sessionAttribute("carrito");

                    if(carrito != null){
                        List<Producto> lista = carrito.getListaProductos();

                        Map<String, Object> modelo = new HashMap<>();
                        modelo.put("cnt", ctx.cookie("productos-en-carrito"));
                        modelo.put("lista", lista);
                        modelo.put("total", carrito.getTotal());
                        ctx.render("/publico/carrito.html",modelo);
                    }else{ctx.render("/publico/carrito.html");}
                });

                get("/eliminar/:id", ctx ->{
                    Carrito carrito = ctx.sessionAttribute("carrito");
                    int idProducto = ctx.pathParam("id",Integer.class).get();

                    int cnt = Integer.parseInt(ctx.cookie("productos-en-carrito"));
                    ctx.cookie("productos-en-carrito", String.valueOf(cnt - carrito.getProductoById(idProducto).getCantidad()));
                    carrito.eliminarProducto(idProducto);
                    ctx.redirect("/carrito/");
                });

                post("/procesar-compra",ctx ->{
                    Carrito carrito = ctx.sessionAttribute("carrito");

                    //se crea una venta
                    Venta venta = new Venta(ventaService.getNewIndex(), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())
                            ,ctx.formParam("nombre"),carrito.getTotal(), carrito.getListaProductos());

                    //save venta
                    ventaService.agregarVenta(venta);
                    carrito.getListaProductos().clear();
                    ctx.cookie("productos-en-carrito", String.valueOf(0));
                    ctx.redirect("/carrito/");
                });


            });
        });


    }
}
