package api;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.reactivex.Single;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import javax.inject.Inject;

@Controller("/cafis")
public class Api {

    private RedissonClient client;

    public Api() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        client = Redisson.create(config);
    }

    @Post("auth")
    public HttpResponse<AuthResponse> auth(@Body AuthRequest ar) {
        RList<Sales> list = client.getList("cafis");
        list.add(new Sales(ar));

        AuthResponse res = new AuthResponse();
        if (ar.getPrice() < 200){
            res.setAuthNum(String.format("%010d", client.getAtomicLong("authNum").incrementAndGet()));
            return HttpResponse.created(res);
        } else {
            res.setErrorCode("G55");
            return HttpResponse.created(res).status(HttpStatus.UNAUTHORIZED);
        }
    }
}
