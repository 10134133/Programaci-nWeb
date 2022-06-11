package org.sebastian.Controladoras;


import io.javalin.Javalin;
import org.sebastian.Entidades.Usuario;
import org.sebastian.services.UsuarioService;
import org.sebastian.util.BaseControlador;
import org.sebastian.util.RolesApp;

/**
 * Ejemplo de permisos basado en roles
 */
public class RolesC extends BaseControlador {

    UsuarioService usuarioService = UsuarioService.getInstancia();

    public RolesC(Javalin app) {
        super(app);
    }

    @Override
    public void aplicarRutas() {

        /**
         * Aplicando la configuracion para manejar los roles/
         */
        app.config.accessManager((handler, ctx, permittedRoles) -> {
            //para obtener el usuario estaré utilizando el contexto de sesion.
            final Usuario usuario = ctx.sessionAttribute("usuario");
            //System.out.println("Los roles permitidos: " + permittedRoles.toString());

            if(permittedRoles.isEmpty()){
                handler.handle(ctx);
                return;
            }
            //validando si existe el usuario.
            if(usuario == null){
                System.out.println("No tiene permiso para acceder..");
                ctx.status(401).result("No tiene permiso para acceder...");
                ctx.redirect("/login");
                //ctx.render("/publico/sinPermiso.html");
                return;
            }
            //buscando el permiso del usuario.
            Usuario usuarioTmp = usuarioService.getListaUsuarios().stream()
                    .filter(u -> u.getUsuario().equalsIgnoreCase(usuario.getUsuario()))
                    .findAny()
                    .orElse(null);

            if(usuarioTmp==null){
                System.out.println("Existe el usuario pero sin roles para acceder.");
                ctx.status(401).result("No tiene roles para acceder...");
                return;
            }

            //validando que el usuario registrando tiene el rol permitido.
            for(RolesApp role : usuarioTmp.getListaRoles() ) {
                if (permittedRoles.contains(role)) {
                    System.out.printf("El Usuario: %s - con el Rol: %s tiene permiso%n", usuarioTmp.getUsuario(), role.name());
                    handler.handle(ctx);
                    break;
                }
            }

        });

    }
}
