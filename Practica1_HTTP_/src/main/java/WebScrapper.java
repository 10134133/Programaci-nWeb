import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Scanner;

//Sebastian Sanchez 20180032 | 10134133
public class WebScrapper {
    public static void main(String[] args) throws IOException {

        System.out.println("-------------------------------");
        System.out.println("---------CLIENTE HTTP----------");
        System.out.println("-------------------------------");
        System.out.println("Ingrese una URL:");
        Scanner s = new Scanner(System.in); //Aqui pide la url
        String url =  s.nextLine();           //Lee el url
        Document doc = Jsoup.connect(url).timeout(5000).followRedirects(true).get();
        long cant_lineas = doc.html().lines().count();
        System.out.print("["+cant_lineas+"] lineas.\n"); //a) Indicar la cantidad de lineas del recurso retornado.

        Elements parrafos = doc.select("p");
        System.out.println("["+parrafos.size()+"] párrafos.\n"); //b) Indicar la cantidad de párrafos (p) que contiene el documento HTML

        if (parrafos.size() != 0) {
            Elements img = doc.select("p img");
            System.out.println("["+img.size()+"] imagenes dentro de parrafos. \n"); //c) Indicar la cantidad de imágenes (img) dentro
            // de los párrafos quecontiene el archivo HTML.

        }

        //d) indicar la cantidad de formularios (form) que contiene el HTML por
        //ategorizando por el método implementado POST o GET
        Elements POST = doc.select("form[method$=post]");
        System.out.println("[" + POST.size() +"] formularios con POST.\n");
        Elements GET = doc.select("form[method$=get]");
        System.out.println("[" + GET.size() + "] formularios con GET.\n");

        input(POST); //e) Para cada formulario mostrar los campos del tipo input y su
        input(GET);  //respectivo tipo que contiene en el documento HTML.


    }

    public static void input(@NotNull Elements forms) throws IOException{
        int cont = 1;
        try {
            for (Element form : forms) {
                if (form.children().select("input").size() != 0) {
                    System.out.println("INPUTS FORMULARIOS: " + form.attr("method").toLowerCase() + cont);
                    for (Element child : form.select("input")) {
                        System.out.println("\t" + child);
                    }
                } else {
                    System.out.println( form.attr("method").toLowerCase() + cont);
                }
                cont++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //f) Para cada formulario "parseado" identificar que el método de envío
    //del formulario sea POST y enviar una petición al servidor con el
    //parámetro llamado asignatura y valor practica1 y un header llamado
    //matricula con el valor correspondiente a matrícula asignada. Debe
    //mostrar la respuesta por la salida estándar.
    public static void peticion_servidor(Elements forms, String url) throws IOException{
        int cont = 1;
        for (Element form: forms) {
            try {
                String postURL = form.attr("action");
                Connection.Response salida;

                if(!postURL.contains("https")) {
                    postURL = url.concat(postURL);
                }
                salida = Jsoup.connect(postURL).method(Connection.Method.POST).data("asignatura","practica1").header("matricula","20180032").execute();
                System.out.println("---------------------------------------");
                System.out.println("\n"+cont+"\n" + salida.statusCode() + "\n" + salida.url());
                System.out.println(salida.headers());
                System.out.println("---------------------------------------");

            } catch (IOException error) {
                System.out.println(error);
            }
        }
    }
}