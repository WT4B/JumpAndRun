package de.wt4b.jumpandrun.utils;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;

/**
 * @author WT4B | https://github.com/WT4B
 */
public class GsonLocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException{
        String world = jsonElement.getAsJsonObject().get("world").getAsString();
        double x = jsonElement.getAsJsonObject().get("x").getAsDouble();
        double y = jsonElement.getAsJsonObject().get("y").getAsDouble();
        double z = jsonElement.getAsJsonObject().get("z").getAsDouble();
        float yaw = jsonElement.getAsJsonObject().get("yaw").getAsFloat();
        float pitch = jsonElement.getAsJsonObject().get("pitch").getAsFloat();
        if(Bukkit.getWorld(world) == null) return null;
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", location.getWorld().getName());
        jsonObject.addProperty("x", location.getX());
        jsonObject.addProperty("y", location.getY());
        jsonObject.addProperty("z", location.getZ());
        jsonObject.addProperty("yaw", location.getYaw());
        jsonObject.addProperty("pitch", location.getPitch());
        return jsonObject;
    }
}
