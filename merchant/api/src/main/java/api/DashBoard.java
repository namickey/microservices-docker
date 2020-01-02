package api;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
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

    @Get(value="/a", produces = MediaType.TEXT_HTML)
    public String ok() {
        return req(100);
    }

    @Get(value="/b", produces = MediaType.TEXT_HTML)
    public String ng() {
        return req(300);
    }

    @Get(value="/c", produces = MediaType.TEXT_HTML)
    public String clear() {
        merchant.toBlocking().retrieve("clear");
        psp.toBlocking().retrieve("clear");
        cafis.toBlocking().retrieve("clear");
        return "clear";
    }

    private String dash(String res){
        return addHtml(res + getList(merchant, "sales") + getList(psp, "sales"));
    }

    private String addHtml(String str){
        return "<html><body>"+str+"</body></html>";
    }

    private String getList(HttpClient client, String key){
        List<String> list = client.toBlocking().retrieve(key, List.class);
        String result = "<ul>";
        for (String str: list) {
            result += "<li>" + str + "</li>";
        }
        return result += "</ul>";
    }

    private String req(int price){
        AuthRequest ar = new AuthRequest();
        ar.setPan("1234-1234-1234-1234");
        ar.setPrice(price);
        try{
            HttpResponse<AuthResponse> res = merchant.toBlocking().exchange(HttpRequest.POST("/auth", ar),
                    Argument.of(AuthResponse.class), Argument.of(AuthResponse.class));
            return dash(res.body().toString());
        }catch(HttpClientResponseException e){
            e.printStackTrace();
            return dash(e.getResponse().getBody(AuthResponse.class).get().getErrorCode());
        }
    }


}
