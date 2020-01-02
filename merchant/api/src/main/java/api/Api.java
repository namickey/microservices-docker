package api;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Controller("/merchant")
public class Api {

    @Inject
    @Client("http://localhost:${psp.port:`8081`}/psp")
    HttpClient psp;

    private RedissonClient client;

    @Value("${merchant.code:'M001'}")
    private String merchantCode;

    public Api() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        client = Redisson.create(config);
    }

    @Get("sales")
    public List<String> sales(){
        RList<Sales> list = client.getList(merchantCode);
        List<String> li = new ArrayList<>();
        for (Sales sales: list) {
            li.add(sales.toString());
        }
        return li;
    }

    @Get("clear")
    public String clear(){
        client.getList(merchantCode).clear();
        return "clear";
    }

    @Post("auth")
    public HttpResponse<AuthResponse> auth(@Body AuthRequest ar) {
        try{
            ar.setMerchantCode(merchantCode);
            HttpResponse<AuthResponse> res = psp.toBlocking().exchange(HttpRequest.POST("/auth", ar),
                    Argument.of(AuthResponse.class), Argument.of(AuthResponse.class));

            RList<Sales> list = client.getList(merchantCode);
            list.add(new Sales(ar, res.getBody().get()));

            return HttpResponse.created(res.body());
        }catch(HttpClientResponseException e){
            return HttpResponse.created(e.getResponse().getBody(AuthResponse.class).get());
        }
    }
}
