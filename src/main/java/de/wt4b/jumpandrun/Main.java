package de.wt4b.jumpandrun;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author WT4B | https://github.com/WT4B
 */
public final class Main extends JavaPlugin {

    private static Main instance;

    private JumpAndRunManager jumpAndRunManager;

    @Override
    public void onEnable(){
        instance = this;

        this.jumpAndRunManager = new JumpAndRunManager();

        registerListener();
        registerCommands();
    }

    @Override
    public void onDisable(){

    }

    private void registerListener(){
        new JumpAndRunListener();
    }

    private void registerCommands(){
        new JumpAndRunCommand();
    }

    public static Main getInstance(){
        return instance;
    }

    public static String getPrefix(){
        return "§8[§2JAR§8] ";
    }

    public JumpAndRunManager getJumpAndRunManager(){
        return jumpAndRunManager;
    }
}