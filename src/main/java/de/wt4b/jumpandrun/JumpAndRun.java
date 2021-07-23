package de.wt4b.jumpandrun;

import org.bukkit.Location;

import java.util.List;

/**
 * @author WT4B | https://github.com/WT4B
 */
public class JumpAndRun {

    private final String name;
    private final String builder;

    private final JumpAndRunDifficulty difficulty;

    private final Location startLocation;
    private final Location endLocation;
    private List<Location> checkpoints;

    private long recordTime;

    public JumpAndRun(String name, String builder, JumpAndRunDifficulty difficulty, Location startLocation, Location endLocation, List<Location> checkpoints){
        this.name = name;
        this.builder = builder;
        this.difficulty = difficulty;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.recordTime = -1;
        if(checkpoints != null) this.checkpoints = checkpoints;
    }

    public String getName(){
        return name;
    }

    public String getBuilder(){
        return builder;
    }

    public JumpAndRunDifficulty getDifficulty(){
        return difficulty;
    }

    public Location getStartLocation(){
        return startLocation;
    }

    public Location getEndLocation(){
        return endLocation;
    }

    public List<Location> getCheckpoints(){
        return checkpoints;
    }

    public long getRecordTime(){
        return recordTime;
    }

    public void setRecordTime(long recordTime){
        this.recordTime = recordTime;
    }
}
