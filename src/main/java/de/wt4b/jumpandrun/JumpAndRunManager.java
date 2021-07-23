package de.wt4b.jumpandrun;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import de.wt4b.jumpandrun.utils.ConfigFile;
import de.wt4b.jumpandrun.utils.Stringify;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * @author WT4B | https://github.com/WT4B
 */
public class JumpAndRunManager {

    private String name = null;
    private String builder = null;
    private JumpAndRunDifficulty difficulty = null;
    private Location startLocation;
    private Location endLocation;
    private List<Location> checkpoints = Lists.newArrayList();

    private final ConfigFile<Map<String, JumpAndRun>> configFile;

    private Map<String, JumpAndRun> jumpAndRuns;
    private final Map<Player, Object[]> jumpAndRunData;
    private final Map<Player, JumpAndRunSetup> setup;

    public JumpAndRunManager(){
        File directory = new File("/plugins/JumpAndRun/");
        if(Files.notExists(directory.toPath())) directory.mkdirs();

        this.configFile = new ConfigFile<>(directory.getPath() + "/JumpAndRuns.json", new TypeToken<Map<String, JumpAndRun>>(){}.getType());

        this.jumpAndRuns = Maps.newHashMap();
        this.jumpAndRunData = Maps.newHashMap();
        this.setup = Maps.newHashMap();

        loadJumpAndRuns();
    }

    public boolean isInJumpAndRun(Player player){
        return jumpAndRunData.containsKey(player);
    }

    public void enterJumpAndRun(Player player, JumpAndRun jumpAndRun){
        if(!isInJumpAndRun(player)){
            jumpAndRunData.put(player, createJumpAndRunData(jumpAndRun));
            player.sendTitle(jumpAndRun.getDifficulty().getColor() + "§l" + jumpAndRun.getName(), "§agestartet", 10, 70, 20);
            player.sendMessage(Main.getPrefix() + "§7Du hast das §2JumpAndRun §l" + jumpAndRun.getName() +
                    "§7 von §2" + jumpAndRun.getBuilder() + " §7betreten§8.");
            player.sendMessage(Main.getPrefix() + "§7Difficulty§8: " + jumpAndRun.getDifficulty().getColor() +
                    jumpAndRun.getDifficulty().name() + " §8║ §7Bestzeit§8: §2" + Stringify.time(jumpAndRun.getRecordTime()) +
                    " §8║ §7Checkpoints§8: §2" + jumpAndRun.getCheckpoints().size());
        }else{
            player.sendMessage(Main.getPrefix() + "§7Du musst dein vorheriges JumpAndRun §cbeenden§7, bevor du ein neues JumpAndRun betrittst§8.");
        }
    }

    public void abortJumpAndRun(Player player){
        if(isInJumpAndRun(player)){
            Object[] data = jumpAndRunData.get(player);
            JumpAndRun jumpAndRun = (JumpAndRun) data[0];
            player.sendTitle(jumpAndRun.getDifficulty().getColor() + "§l" + jumpAndRun.getName(), "§cabgebrochen", 10, 70, 20);
            player.sendMessage(Main.getPrefix() + "§7Du hast das §2JumpAndRun §l" + jumpAndRun.getName() +
                    "§7 von §2" + jumpAndRun.getBuilder() + " §7abgebrochen§8.");
            player.sendMessage(Main.getPrefix() + "§7Difficulty§8: " + jumpAndRun.getDifficulty().getColor() +
                    jumpAndRun.getDifficulty().name() + " §8║ §7Spielzeit§8: §2" + Stringify.time(System.currentTimeMillis() - (long) data[3]) +
                    " §8║ §7Fails§8: §2" + data[1]);
            jumpAndRunData.remove(player);
        }
    }

    public void finishJumpAndRun(Player player){
        if(isInJumpAndRun(player)){
            Object[] data = jumpAndRunData.get(player);
            JumpAndRun jumpAndRun = (JumpAndRun) data[0];
            player.sendTitle(jumpAndRun.getDifficulty().getColor() + "§l" + jumpAndRun.getName(), "§ageschafft", 10, 70, 20);
            player.sendMessage(Main.getPrefix() + "§7Du hast das §2JumpAndRun §l" + jumpAndRun.getName() +
                    "§7 von §2" + jumpAndRun.getBuilder() + " §7geschafft§8.");
            player.sendMessage(Main.getPrefix() + "§7Difficulty§8: " + jumpAndRun.getDifficulty().getColor() +
                    jumpAndRun.getDifficulty().name() + " §8║ §7Spielzeit§8: §2" + Stringify.time(System.currentTimeMillis() - (long) data[3]) +
                    " §8║ §7Fails§8: §2" + data[1]);
            jumpAndRunData.remove(player);
        }
    }

    public void addJumpAndRun(String name, String builder, JumpAndRunDifficulty difficulty, Location startLocation, Location endLocation, List<Location> checkpoints){
        JumpAndRun jumpAndRun = new JumpAndRun(name, builder, difficulty, startLocation, endLocation, checkpoints);
        jumpAndRuns.put(name, jumpAndRun);
        saveJumpAndRuns();
    }

    public JumpAndRun getJumpAndRun(String name){
        return jumpAndRuns.getOrDefault(name, null);
    }

    public JumpAndRun getJumpAndRun(Location startLocation){
        for(JumpAndRun jumpAndRun : jumpAndRuns.values())
            if(jumpAndRun.getStartLocation().getBlock().getLocation().equals(startLocation.getBlock().getLocation()))
                return jumpAndRun;
        return null;
    }

    public void deleteJumpAndRun(String name){
        if(jumpAndRuns.containsKey(name)){
            jumpAndRuns.remove(name);
            Bukkit.broadcastMessage(Main.getPrefix() + "§7Das §2JumpAndRun §l" + name + "§7 wurde §cgelöscht§8!");
        }
    }

    public void loadJumpAndRuns(){
        this.configFile.setExternal(true);
        this.configFile.load();
        if(configFile.getContent() != null) jumpAndRuns = this.configFile.getContent();
    }

    public void saveJumpAndRuns(){
        if(jumpAndRuns != null) this.configFile.store(jumpAndRuns);
        else this.configFile.store(Maps.newHashMap());
    }

    private Object[] createJumpAndRunData(JumpAndRun jumpAndRun){
        return new Object[] {
                jumpAndRun, // JumpAndRun
                0, // FailCount
                jumpAndRun.getStartLocation(), // Checkpoint
                System.currentTimeMillis() // Time since start
        };
    }

    public void addFail(Player player){
        Object[] data = jumpAndRunData.get(player);
        jumpAndRunData.remove(player);
        Object[] newData = new Object[] { data[0], (int)data[1]+1, data[2], data[3]};
        jumpAndRunData.put(player, newData);
    }

    public void updateCheckpoint(Player player, Location location){
        Object[] data = jumpAndRunData.get(player);
        jumpAndRunData.remove(player);
        Object[] newData = new Object[]{data[0], data[1], location, data[3]};
        jumpAndRunData.put(player, newData);
    }

    public Location getCheckpoint(Player player){
        return (Location) jumpAndRunData.get(player)[2];
    }

    public Map<String, JumpAndRun> getJumpAndRuns() {
        return jumpAndRuns;
    }

    public Map<Player, Object[]> getJumpAndRunData() {
        return jumpAndRunData;
    }

    public Map<Player, JumpAndRunSetup> getSetup() {
        return setup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuilder() {
        return builder;
    }

    public void setBuilder(String builder) {
        this.builder = builder;
    }

    public JumpAndRunDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(JumpAndRunDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public List<Location> getCheckpoints() {
        return checkpoints;
    }

    public void addCheckpoint(Location location){
        checkpoints.add(location);
    }

    public void setCheckpoints(List<Location> checkpoints) {
        this.checkpoints = checkpoints;
    }
}
