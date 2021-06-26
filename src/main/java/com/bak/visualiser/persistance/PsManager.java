package com.bak.visualiser.persistance;

import com.bak.visualiser.parser.Parser;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PsManager {
    private final Path workDir;
    private ObjectOutputStream oos;
    private final HashMap<String, Parser> parsers = new HashMap<>();

    public PsManager(Path workDir) {
        this.workDir = workDir;
        File work = new File(String.valueOf(workDir));
        work.mkdirs();
    }

    public Parser get(String projectName) {
        Parser parser;
        if (!parsers.containsKey(projectName)) {
            try {
                FileInputStream fis = new FileInputStream(workDir + "/" + projectName);
                ObjectInputStream ois = new ObjectInputStream(fis);
                try {
                    parser = (Parser) ois.readObject();
                    parsers.put(projectName, parser);
                } catch (IOException | ClassNotFoundException exception) {
                    ois.close();
                    throw new RuntimeException("Can't load project");
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Project doesn't exist");
            } catch (IOException ioException) {
                throw new RuntimeException("Can't load project");
            }
        } else {
            parser = parsers.get(projectName);
        }
        return parser;
    }

    public void save(Parser parser, String projectName) {
        try {
            FileOutputStream fos = new FileOutputStream(workDir + "/" + projectName);
            oos = new ObjectOutputStream(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oos.writeObject(parser);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public List<String> getProjects() {
        File file = new File(String.valueOf(workDir));
        return Arrays.stream(Objects.requireNonNull(file.listFiles()))
                .map(File::toPath)
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }
}
