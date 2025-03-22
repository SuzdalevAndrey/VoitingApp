package ru.andreyszdlv.service.file;

import ru.andreyszdlv.model.Topic;

import java.io.IOException;
import java.util.Map;

public interface FileHandler {

    void save(String filename, Map<String, Topic> topics) throws IOException;

    Map<String, Topic> load(String filename) throws IOException;
}
