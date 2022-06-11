package org.sebastian.Controladoras;


import io.javalin.Javalin;
import org.sebastian.entidades.Carrito;
import org.sebastian.entidades.Producto;
import org.sebastian.services.CarritoService;
import org.sebastian.services.ProductoService;
import org.sebastian.util.BaseControlador;
import org.sebastian.util.RolesApp;
import java.util.*;
import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;

public class ProductosC extends BaseControlador {

    ProductoService productoService = ProductoService.getInstancia();
    CarritoService carritoService = CarritoService.getInstancia();

    public ProductosC(Javalin app) {
        super(app);
    }

    @Override
    public void aplicarRutas() {
        app.routes(()->{
            path("/productos/", () -> {

                get("/", ctx -> {
                    ctx.redirect("/productos/listar");
                });

                get("/listar", ctx -> {
                    List<Producto> lista = productoService.getListaProductos();
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("accion", "/productos/agregar");
                    modelo.put("cnt", ctx.cookie("productos-en-carrito"));
                    modelo.put("lista", lista);
                    //enviando al sistema de plantilla.
                    ctx.render("/publico/catalogo.html", modelo);
                });

                post("/agregar", ctx -> {
                    int id = ctx.formParam("id",Integer.class).get();
                    int cantidad = ctx.formParam("cantidad",Integer.class).get();

                    //encuentro el producto y construyo una copia con la cantidad correcta
                    Producto producto = productoService.getProductoById(id);
                    Producto clon = new Producto(id, producto.getNombre(),producto.getPrecio());
                    clon.setCantidad(cantidad);

                    //retrieve carrito
                    Carrito carrito = ctx.sessionAttribute("carrito");

                    if (carrito != null){
                        carrito.agregarProducto(clon);
                        ctx.sessionAttribute("carrito", carrito); //guardo carrito de la sesion
                    }else{
                        Carrito newCarrito = new Carrito(carritoService.getNewIndex(),new ArrayList<>()); //bobo aqui
                        newCarrito.agregarProducto(clon);
                        carritoService.agregarCarrito(newCarrito);
                        ctx.sessionAttribute("carrito", newCarrito); //guardo carrito de la sesion
                    }

                    //guardando sumando y guardando cookie de cantidad
                    int cnt = Integer.parseInt(ctx.cookie("productos-en-carrito"));
                    cnt+=cantidad;
                    ctx.cookie("productos-en-carrito", String.valueOf(cnt));

                    ctx.redirect("/productos/");
                });



            });
        });

        app.routes(() -> {
            path("/admin-productos/", () -> {

                get("/", ctx -> {
                    ctx.redirect("/admin-productos/listar");
                }, Collections.singleton(RolesApp.ROLE_ADMIN));

                get("/listar", ctx -> {
                    //tomando el parametro utl y validando el tipo.
                    List<Producto> lista = productoService.getListaProductos();
                    //
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("titulo", "Listado de Productos");
                    modelo.put("cnt", ctx.cookie("productos-en-carrito"));
                    modelo.put("lista", lista);
                    //enviando al sistema de plantilla.
                    ctx.render("/publico/productosCRUD.html", modelo);
                }, Collections.singleton(RolesApp.ROLE_ADMIN));

                get("/crear", ctx -> {
                    //
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("titulo", "Formulario Creación Producto");
                    modelo.put("accion", "/admin-productos/crear");
                    modelo.put("id",productoService.getNewIndex());
                    //enviando al sistema de plantilla.
                    ctx.render("/publico/crearEditarVisualizar.html", modelo);
                }, Collections.singleton(RolesApp.ROLE_ADMIN));

                post("/crear", ctx -> {
                    //obteniendo la información enviada.
                    int id = ctx.formParam("id",Integer.class).get();
                    String nombre = ctx.formParam("nombre");
                    int precio = ctx.formParam("precio", Integer.class).get();

                    Producto tmp = new Producto(id,nombre, precio);
                    //realizar algún tipo de validación...
                    productoService.crearProducto(tmp); //puedo validar, existe un error enviar a otro vista.
                    ctx.redirect("/admin-productos/");
                }, Collections.singleton(RolesApp.ROLE_ADMIN));

                get("/visualizar/:id", ctx -> {
                    Producto producto = productoService.getProductoById(ctx.pathParam("id",Integer.class).get());
                    //
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("titulo", "Formulario Visualizar Producto " + producto.getNombre());
                    modelo.put("producto", producto);
                    modelo.put("visualizar", true); //para controlar en el formulario si es visualizar
                    modelo.put("accion", "/admin-productos/");

                    //enviando al sistema de ,plantilla.
                    ctx.render("/publico/crearEditarVisualizar.html", modelo);
                }, Collections.singleton(RolesApp.ROLE_ADMIN));

                get("/editar/:id", ctx -> {
                    Producto producto = productoService.getProductoById(ctx.pathParam("id",Integer.class).get());
                    //
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("titulo", "Formulario Editar Producto " + producto.getNombre());
                    modelo.put("producto", producto);
                    modelo.put("accion", "/admin-productos/editar");

                    //enviando al sistema de ,plantilla.
                    ctx.render("/publico/crearEditarVisualizar.html", modelo);
                }, Collections.singleton(RolesApp.ROLE_ADMIN));


                post("/editar", ctx -> {
                    //obteniendo la información enviada.
                    int id = ctx.formParam("id", Integer.class).get();
                    String nombre = ctx.formParam("nombre");
                    int precio = ctx.formParam("precio", Integer.class).get();
                    //
                    Producto tmp = new Producto(id, nombre, precio);
                    //realizar algún tipo de validación...
                    productoService.editarProducto(tmp); //puedo validar, existe un error enviar a otro vista.
                    ctx.redirect("/admin-productos/");
                }, Collections.singleton(RolesApp.ROLE_ADMIN));


                get("/eliminar/:id", ctx -> {
                    productoService.eliminarProducto(ctx.pathParam("id",Integer.class).get());
                    ctx.redirect("/admin-productos/");
                }, Collections.singleton(RolesApp.ROLE_ADMIN));

            });
        });
    }
}