package ik;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ik.mock.admin.mapping.*;

public class Test {
    public static void main(String[] args) {
        CreateStubMappingEntity createStubMappingEntity = CreateStubMappingEntity.builder()
                .request(Request.builder()
                        .url("/plaintext/mapping1")
                        .method("GET")
                        .build())
                .response(Response.builder()
                        .status(200)
                        .body("bodybodybody")
                        .headers(Header.builder().contentType("text/plain").build())
                        .build())
                .build();
        System.out.println(createStubMappingEntity);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(createStubMappingEntity);
        System.out.println(json);

    }
}
