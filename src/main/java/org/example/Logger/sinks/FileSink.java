package Logger.sinks;

import Logger.enums.LogLevel;
import Logger.formatters.Formatter;
import jdk.dynalink.StandardOperation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileSink extends Sink{

    private final Path filePath;

    public FileSink(Formatter formatter, LogLevel threshold, Path filePath){
        super(formatter, threshold);
        this.filePath = filePath;
    }

    @Override
    protected void write(String formattedString){
        try{
            Files.writeString(filePath, formattedString+System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }catch (IOException e){
            throw new RuntimeException("Failed to log", e);
        }
    }
}
