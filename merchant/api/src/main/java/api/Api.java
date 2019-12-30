package api;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import javax.inject.Inject;


@Controller("/api")
public class Api {

    //@Inject
    //@Client("http://localhost:8081")
    //HttpClient client;

    @Get(produces = MediaType.TEXT_PLAIN)
    public String index() {
        //String response = client.toBlocking()
        //        .retrieve(HttpRequest.GET("/hello"));
        //return response + " good";
        return "api called.";
    }
}
