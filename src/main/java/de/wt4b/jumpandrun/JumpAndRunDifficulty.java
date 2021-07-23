package de.wt4b.jumpandrun;

/**
 * @author WT4B | https://github.com/WT4B
 */
public enum JumpAndRunDifficulty {

    EASY(1, "§a"),
    NORMAL(2, "§6"),
    HARD(3, "§c"),
    EXTREME(4, "§b");

    private final int id;
    private final String color;

    JumpAndRunDifficulty(int id, String color){
        this.id = id;
        this.color = color;
    }

    public static JumpAndRunDifficulty getDifficultyByID(int id){
        for(JumpAndRunDifficulty difficulty : JumpAndRunDifficulty.values())
            if(difficulty.getDifficulty() == id) return difficulty;
        return JumpAndRunDifficulty.EASY;
    }

    public int getDifficulty(){
        return id;
    }

    public String getColor(){
        return color;
    }
}
