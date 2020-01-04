package api;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;

import javax.inject.Inject;
import java.util.List;

@Controller("/dashboard")
public class DashBoard {

    @Inject
    @Client("http://localhost:${micronaut.server.port:`8080`}/merchant")
    HttpClient merchant;

    @Inject
    @Client("http://localhost:${psp.port:`8081`}/psp")
    HttpClient psp;

    @Inject
    @Client("http://localhost:8082/cafis")
    HttpClient cafis;

    @Inject
    @Client("http://localhost:8083/authorize")
    HttpClient authorize;

    @Get(value="/buy", produces = MediaType.TEXT_HTML)
    public HttpResponse<AuthResponse> buy(HttpRequest<?> request) {
        AuthRequest ar = new AuthRequest();
        ar.setPan("1234-1234-1234-1234");
        ar.setPrice(Integer.valueOf(request.getParameters().getFirst("price").get()));
        try{
            HttpResponse<AuthResponse> res = merchant.toBlocking().exchange(HttpRequest.POST("/auth", ar),
                    Argument.of(AuthResponse.class), Argument.of(AuthResponse.class));
            AuthResponse authRes = res.getBody().get();
            return HttpResponse.created(authRes);
        }catch(HttpClientResponseException e){
            e.printStackTrace();
            return HttpResponse.created(e.getResponse().getBody(AuthResponse.class).get()).status(HttpStatus.UNAUTHORIZED);
        }
    }

    @Get(value="/clear", produces = MediaType.TEXT_HTML)
    public String clear() {
        merchant.toBlocking().retrieve("clear");
        psp.toBlocking().retrieve("clear");
        cafis.toBlocking().retrieve("clear");
        authorize.toBlocking().retrieve("clear");
        return "clear";
    }

    @Get(value="/sales")
    public List<Sales> sales() {
        List<Sales> res = merchant.toBlocking().retrieve("sales", List.class);
        return res;
    }
}
