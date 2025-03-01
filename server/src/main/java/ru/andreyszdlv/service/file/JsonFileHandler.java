package ru.andreyszdlv.service.file;

import lombok.RequiredArgsConstructor;
import ru.andreyszdlv.model.Topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class JsonFileHandler implements FileHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void save(String filename, Map<String, Topic> topics) throws IOException {
        objectMapper.writeValue(new File(filename), topics);
    }

    @Override
    public Map<String, Topic> load(String filename) throws IOException {
        return objectMapper.readValue(new File(filename),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Topic.class));
    }
}