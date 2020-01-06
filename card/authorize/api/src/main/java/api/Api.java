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
import org.redisson.api.MapOptions;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Controller("/authorize")
public class Api {

    private RedissonClient client;

    public Api() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        client = Redisson.create(config);
    }

    @Get("sales")
    public List<String> sales(){
        RList<Sales> list = client.getList("authorize");
        List<String> li = new ArrayList<>();
        for (Sales sales: list) {
            li.add(sales.toString());
        }
        return li;
    }

    @Get("balance")
    public String getBalance(){
        RMap<String, Float> map = client.getMap("balance", StringCodec.INSTANCE);
        return String.valueOf(map.get("1234-1234-1234-1234"));
    }

    @Get("add")
    public String getAdd(){
        RMap<String, Float> map = client.getMap("balance", StringCodec.INSTANCE);
        map.addAndGet("1234-1234-1234-1234", 100f);
        return String.valueOf(map.get("1234-1234-1234-1234"));
    }

    @Get("minus")
    public String getMinus(){
        RMap<String, Float> map = client.getMap("balance", StringCodec.INSTANCE);
        map.addAndGet("1234-1234-1234-1234", -100f);
        return String.valueOf(map.get("1234-1234-1234-1234"));
    }

    @Get("clear")
    public String clear(){
        client.getList("authorize").clear();
        client.getAtomicLong("authNum").set(0);
        resetBalance();
        return "clear";
    }

    private void resetBalance(){
        RMap<String, Float> map = client.getMap("balance", StringCodec.INSTANCE);
        map.clear();
        map.put("1234-1234-1234-1234", 600f);
    }

    @Post("auth")
    public HttpResponse<AuthResponse> auth(@Body AuthRequest ar) {

        RList<Sales> list = client.getList("authorize");
        list.add(new Sales(ar));

        RMap<String, Float> map = client.getMap("balance", StringCodec.INSTANCE);
        System.out.println(ar);
        System.out.println(Float.valueOf(ar.getPrice()*-1));
        Float balance = map.addAndGet(ar.getPan(), Float.valueOf(ar.getPrice()*-1));
        System.out.println(balance);
        AuthResponse res = new AuthResponse();
        if (balance < 0) {
            //map.addAndGet(ar.getPan(), Float.valueOf(ar.getPrice()));
            res.setErrorCode("G55");
            return HttpResponse.created(res).status(HttpStatus.UNAUTHORIZED);
        }
        res.setAuthNum(String.format("%010d", client.getAtomicLong("authNum").incrementAndGet()));
        return HttpResponse.created(res);
    }
}
