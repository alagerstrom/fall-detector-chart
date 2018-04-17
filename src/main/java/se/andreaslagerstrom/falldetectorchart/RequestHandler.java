package se.andreaslagerstrom.falldetectorchart;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class RequestHandler {
    private String key = "4576a0751b340ca4c25340d73b483c70e5f64cb2";
    private String appId = "40659ef1cd8716431500899524169afe95725cc4";
    private String url = "http://52.29.51.77:80/parse/classes/Fall/?limit=1000";

    public ResponseEntity<FallResponse> makeRequest() {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Parse-REST-API-Key", key);
        headers.set("X-Parse-Application-Id", appId);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<FallResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, FallResponse.class);

        return responseEntity;
    }
}
