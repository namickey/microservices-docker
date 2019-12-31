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
import io.reactivex.Single;

import javax.inject.Inject;

@Controller("/psp")
public class Api {

    @Post("auth")
    public HttpResponse<Message> auth(@Body Message message) {
        Message m = new Message();
        m.setStr(message.getStr() + "world");
        m.setPan((message.getPan() + "-5678"));
        return HttpResponse.created(m);
    }
}
