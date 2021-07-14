package ik.mock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ik.mock.exceptions.JsonResourceDeserializationException;
import ik.json.JsonResource;
import ik.mock.admin.mappings.entity.AllStubMappings;
import ik.mock.admin.mappings.entity.Mapping;

public class Experiment {

    public static void main(String[] args) throws JsonResourceDeserializationException {

        String jsonPath = "./mock_mappings.json";
        JsonResource<AllStubMappings> jsonResource = new JsonResource<>();
        AllStubMappings allStubMappings = jsonResource.deserialize(jsonPath, AllStubMappings.class);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        for (Mapping mapping : allStubMappings.getMappings()){
            System.out.println(mapping + "\n" + gson.toJson(mapping));
        }

        /*
        Mapping mapping0 = allStubMappings.getMappings().get(0);
        Map<String, Object> headers = mapping0.getResponse().getHeaders();
        System.out.println(headers.get("Content-Type").toString());
        System.out.println(headers.get("H1").toString());
        List<String> h1 = (List<String>) headers.get("H1");
        h1.forEach(System.out::println);*/
    }

}
