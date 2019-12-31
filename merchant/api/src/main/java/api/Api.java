package api;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import javax.inject.Inject;


@Controller("/merchant")
public class Api {

    @Inject
    @Client("http://localhost:8081/psp")
    HttpClient psp;

    @Get(value="/", produces = MediaType.TEXT_PLAIN)
    public String index() {
        Message m = new Message();
        m.setStr("hello ");
        m.setPan("1234");
        return auth(m).body().getPan();
    }

    @Post("auth")
    public HttpResponse<Message> auth(@Body Message m) {
        HttpResponse<Message> res = psp.toBlocking().exchange(HttpRequest.POST("/auth", m), Message.class);
        return HttpResponse.created(res.body());
    }
}
