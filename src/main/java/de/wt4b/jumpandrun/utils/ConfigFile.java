package de.wt4b.jumpandrun.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author WT4B | https://github.com/WT4B
 */
public class ConfigFile<T> {

    public static final Path INTERNAL_RESOURCE_PATH = Paths.get("");
    public static final Path EXTERNAL_RESOURCE_PATH = Paths.get("");
    private Path path;
    private final String fileName;
    private T content;
    private final T defaultContent;
    private final Type contentType;

    public ConfigFile(String fileName, T content, T defaultContent, Type contentType){
        this.fileName = fileName;
        this.content = content;
        this.defaultContent = defaultContent;
        this.contentType = contentType;
        this.path = Paths.get(INTERNAL_RESOURCE_PATH + fileName);
        if(Files.notExists(EXTERNAL_RESOURCE_PATH)){
            try {
                Files.createDirectories(EXTERNAL_RESOURCE_PATH);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public ConfigFile(String fileName){
        this(fileName, null, null, new TypeToken<T>(){}.getType());
    }

    public ConfigFile(String fileName, Type type){
        this(fileName, null, null, type);
    }

    public ConfigFile(String fileName, T content){
        this(fileName, content, null, new TypeToken<T>(){}.getType());
    }

    public ConfigFile(String fileName, T content, Type contentType){
        this(fileName, content, null, contentType);
    }

    public ConfigFile(String fileName, T content, T defaultContent){
        this(fileName, content, defaultContent, new TypeToken<T>(){}.getType());
    }

    public void setExternal(boolean external){
        if(external) this.path = Paths.get(EXTERNAL_RESOURCE_PATH + fileName);
        else this.path = Paths.get(INTERNAL_RESOURCE_PATH + fileName);
    }

    public void load(){
        try {
            final Path path = this.path;
            if(path.toFile().exists()){
                Reader reader = Files.newBufferedReader(path);
                final Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new GsonLocationAdapter()).create();
                final T content = gson.fromJson(reader, contentType);
                this.content = content;
            }else{
                store(defaultContent);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void store(T content){
        final Path path = this.path;
        if(!path.toFile().exists()){
            try {
                if(!path.toFile().createNewFile()) System.err.println("Could not create config " + path);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        try(BufferedWriter writer = Files.newBufferedWriter(path)){
            final Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new GsonLocationAdapter()).create();
            gson.toJson(content, this.contentType, writer);
            this.content = content;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void store(){
        store(content);
    }

    public T getContent() {
        if(content == null) System.err.println("No Content loaded!");
        return content;
    }

    public T getDefaultContent() {
        return defaultContent;
    }

    public Path getPath() {
        return path;
    }
}
