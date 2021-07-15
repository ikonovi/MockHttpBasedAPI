package ik.resources;

import com.google.gson.Gson;
import ik.mock.exceptions.JsonResourceDeserializationExceptionNot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Objects;

public class JsonResource<T> {

    public T deserialize(String resourcePath, Class<T> classOfType) throws JsonResourceDeserializationExceptionNot {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        if (Objects.nonNull(resource)) {
            FileReader fileReader;
            try {
                fileReader = new FileReader(resource.getPath());
            } catch (FileNotFoundException e) {
                throw new JsonResourceDeserializationExceptionNot(e);
            }
            Gson gson = new Gson();
            return gson.fromJson(fileReader, classOfType);
        } else {
            throw new JsonResourceDeserializationExceptionNot("URL of resource by path: \"" + resourcePath + "\" is Null");
        }
    }
}
