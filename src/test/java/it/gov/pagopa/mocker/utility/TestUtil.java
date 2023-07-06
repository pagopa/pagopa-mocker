package it.gov.pagopa.mocker.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

@UtilityClass
public class TestUtil {

    public String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    public String readJsonFromFile(String relativePath) throws IOException {
        ClassLoader classLoader = TestUtil.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(relativePath)).getPath());
        return Files.readString(file.toPath());
    }

    public static String readXMLFromFile(String relativePath) throws IOException {
        ClassLoader classLoader = TestUtil.class.getClassLoader();
        Reader fileReader = new FileReader(Objects.requireNonNull(classLoader.getResource(relativePath)).getPath());
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder builder = new StringBuilder();
        String line = bufferedReader.readLine();
        while(line != null){
            builder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        return builder.toString();
    }
}
