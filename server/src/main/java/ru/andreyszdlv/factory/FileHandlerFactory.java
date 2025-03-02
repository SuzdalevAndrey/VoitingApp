package ru.andreyszdlv.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.andreyszdlv.service.file.FileHandler;
import ru.andreyszdlv.service.file.JsonFileHandler;

public class FileHandlerFactory {
    private static final FileHandler fileHandler = new JsonFileHandler(new ObjectMapper());

    public static FileHandler getFileHandler() {
        return fileHandler;
    }
}
