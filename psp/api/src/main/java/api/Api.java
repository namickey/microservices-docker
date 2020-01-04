package api;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
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

@Controller("/psp")
public class Api {

    @Inject
    @Client("http://localhost:8082/cafis")
    HttpClient cafis;

    private RedissonClient client;

    @Value("${company.code:'P001'}")
    private String companyCode;

    public Api() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        client = Redisson.create(config);
        client.getList("psp").clear();
        client.getAtomicLong("pspTransactionId").set(0);
    }

    @Get("sales")
    public List<String> sales(){
        RList<Sales> list = client.getList(companyCode);
        List<String> li = new ArrayList<>();
        for (Sales sales: list) {
            li.add(sales.toString());
        }
        return li;
    }

    @Get("clear")
    public String clear(){
        client.getList(companyCode).clear();
        client.getAtomicLong("pspTransactionId"+companyCode).set(0);
        return "clear";
    }

    @Post("auth")
    public HttpResponse<AuthResponse> auth(@Body AuthRequest ar) {

        try{
            HttpResponse<AuthResponse> res = cafis.toBlocking().exchange(HttpRequest.POST("/auth", ar),
                    Argument.of(AuthResponse.class), Argument.of(AuthResponse.class));
            AuthResponse authRes = res.getBody().get();
            authRes.setTransactionId(String.format("%010d", client.getAtomicLong("pspTransactionId"+companyCode).incrementAndGet()));

            RList<Sales> list = client.getList(companyCode);
            list.add(new Sales(ar, authRes));

            return HttpResponse.created(authRes);
        }catch(HttpClientResponseException e){
            return HttpResponse.created(e.getResponse().getBody(AuthResponse.class).get()).status(HttpStatus.UNAUTHORIZED);
        }
    }
}
