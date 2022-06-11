package org.sebastian.Controladoras;

import io.javalin.Javalin;
import org.sebastian.Entidades.Usuario;
import org.sebastian.Services.UsuarioService;
import org.sebastian.util.BaseControlador;

public class SesionesC extends BaseControlador {

    UsuarioService usuarioService = UsuarioService.getInstancia();

    public SesionesC(Javalin app) {
        super(app);
    }

    @Override
    public void aplicarRutas(){

        app.get("/login",ctx -> {
            ctx.render("/publico/login.html");
        });

        app.post("/login", ctx -> {
            //Obteniendo la informacion de la petion. Pendiente validar los parametros.
            String nombreUsuario = ctx.formParam("usuario");
            String password = ctx.formParam("password");

            //Autenticando el usuario para nuestro ejemplo siempre da una respuesta correcta.
            Usuario usuario = UsuarioService.getInstancia().autenticarUsuario(nombreUsuario, password);
            //System.out.println(usuario.getUsuario());
            if (usuario != null){
                //agregando el usuario en la session... se puede validar si existe para solicitar el cambio.-
                ctx.sessionAttribute("usuario", usuario);
                //redireccionando la vista con autorizacion.
                ctx.redirect("/admin-productos/");
            }else{
                new RuntimeException("No Existe el usuario");
            }



        });


        app.get("/invalidarSesion", ctx -> {
            String id = ctx.req.getSession().getId();
            ctx.req.getSession().getAttribute("");
            //invalidando la sesion.
            ctx.req.getSession().invalidate();
            ctx.result(String.format("Sesion con ID: %s fue invalidada", id));

            //redirect somewhere instead of ctx result...
        });
    }
}
