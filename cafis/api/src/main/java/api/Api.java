package api;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Controller("/cafis")
public class Api {

    @Inject
    @Client("http://localhost:${authorize.port:`8083`}/authorize")
    HttpClient authorize;

    private RedissonClient client;

    public Api() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        client = Redisson.create(config);
    }

    @Get("sales")
    public List<String> sales(){
        RList<Sales> list = client.getList("cafis");
        List<String> li = new ArrayList<>();
        for (Sales sales: list) {
            li.add(sales.toString());
        }
        return li;
    }

    @Get("clear")
    public String clear(){
        client.getList("cafis").clear();
        return "clear";
    }

    @Post("auth")
    public HttpResponse<AuthResponse> auth(@Body AuthRequest ar) {
        try{
            HttpResponse<AuthResponse> res = authorize.toBlocking().exchange(HttpRequest.POST("/auth", ar),
                    Argument.of(AuthResponse.class), Argument.of(AuthResponse.class));
            AuthResponse authRes = res.getBody().get();

            RList<Sales> list = client.getList("cafis");
            list.add(new Sales(ar, authRes));

            return HttpResponse.created(authRes);
        }catch(HttpClientResponseException e){
            return HttpResponse.created(e.getResponse().getBody(AuthResponse.class).get()).status(HttpStatus.UNAUTHORIZED);
        }
    }
}
