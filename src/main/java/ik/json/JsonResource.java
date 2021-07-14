package ik.json;

import com.google.gson.Gson;
import ik.mock.exceptions.JsonResourceDeserializationException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Objects;

public class JsonResource<T> {

    public T deserialize(String resourcePath, Class<T> classOfType) throws JsonResourceDeserializationException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        if (Objects.nonNull(resource)) {
            FileReader fileReader;
            try {
                fileReader = new FileReader(resource.getPath());
            } catch (FileNotFoundException e) {
                throw new JsonResourceDeserializationException(e);
            }
            Gson gson = new Gson();
            return gson.fromJson(fileReader, classOfType);
        } else {
            throw new JsonResourceDeserializationException("Resource could not be found by path: \"" + resourcePath + "\"");
        }
    }
}
