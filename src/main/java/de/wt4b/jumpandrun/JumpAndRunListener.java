package de.wt4b.jumpandrun;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author WT4B | https://github.com/WT4B
 */
public class JumpAndRunListener implements Listener {

    public JumpAndRunListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        JumpAndRunManager jumpAndRunManager = Main.getInstance().getJumpAndRunManager();
        if(jumpAndRunManager.isInJumpAndRun(player)) {
            Object[] data = jumpAndRunManager.getJumpAndRunData().get(player);
            JumpAndRun playerJumpAndRun = (JumpAndRun) data[0];
            if (player.getLocation().getBlock().getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                if (player.getLocation().getBlockX() == playerJumpAndRun.getEndLocation().getBlockX() &&
                        player.getLocation().getBlockY() == playerJumpAndRun.getEndLocation().getBlockY() &&
                        player.getLocation().getBlockZ() == playerJumpAndRun.getEndLocation().getBlockZ())
                    jumpAndRunManager.finishJumpAndRun(player);
            } else if (player.getLocation().getBlock().getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)) {
                if (player.getLocation().getBlockX() == jumpAndRunManager.getCheckpoint(player).getBlockX() &&
                        player.getLocation().getBlockY() == jumpAndRunManager.getCheckpoint(player).getBlockY() &&
                        player.getLocation().getBlockZ() == jumpAndRunManager.getCheckpoint(player).getBlockZ()) return;
                for (Location checkpoint : playerJumpAndRun.getCheckpoints()) {
                    if (player.getLocation().getBlockX() == checkpoint.getBlockX()
                            && player.getLocation().getBlockY() == checkpoint.getBlockY()
                            && player.getLocation().getBlockZ() == checkpoint.getBlockZ())
                        jumpAndRunManager.updateCheckpoint(player, checkpoint.clone());
                }
            }
            return;
        }
        if(player.getLocation().getBlock().getType().equals(Material.OAK_PRESSURE_PLATE)){
            if(jumpAndRunManager.getJumpAndRun(player.getLocation()) != null)
                jumpAndRunManager.enterJumpAndRun(player, jumpAndRunManager.getJumpAndRun(player.getLocation()));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        JumpAndRunManager jumpAndRunManager = Main.getInstance().getJumpAndRunManager();
        if(jumpAndRunManager.getSetup().containsKey(player)){
            event.setCancelled(true);
            String message = event.getMessage().split(" ")[0];
            if(event.getMessage().equalsIgnoreCase("cancel")){
                jumpAndRunManager.getSetup().remove(player);
                player.sendMessage(Main.getPrefix() + "§7Du hast das Setup für das §2§lJumpAndRun §7abgebrochen§8.");
                return;
            }
            switch (jumpAndRunManager.getSetup().get(player)){
                case NAME:
                    jumpAndRunManager.setName(message);
                    player.sendMessage(Main.getPrefix() + "§7Du hast den Namen des §2§lJumpAndRun's §7gesetzt§8.");
                    player.sendMessage(Main.getPrefix() + "§7Tippe den Namen vom Builder des §2§lJumpAndRun's §7in den Chat§8.");
                    jumpAndRunManager.getSetup().put(player, JumpAndRunSetup.BUILDER);
                    break;
                case BUILDER:
                    jumpAndRunManager.setBuilder(message);
                    player.sendMessage(Main.getPrefix() + "§7Du hast den Builder des §2§lJumpAndRun's §7gesetzt§8.");
                    player.sendMessage(Main.getPrefix() + "§7Tippe die Difficulty-ID des §2§lJumpAndRun's §7in den Chat§8.");
                    for(JumpAndRunDifficulty difficulty : JumpAndRunDifficulty.values())
                        player.sendMessage(" §8» " + difficulty.getColor() + difficulty.name() + "§8(" +
                                difficulty.getColor() + difficulty.getDifficulty() + "§8)");
                    jumpAndRunManager.getSetup().put(player, JumpAndRunSetup.DIFFICULTY);
                    break;
                case DIFFICULTY:
                    jumpAndRunManager.setDifficulty(JumpAndRunDifficulty.getDifficultyByID(Integer.parseInt(message)));
                    player.sendMessage(Main.getPrefix() + "§7Du hast die Difficulty des §2§lJumpAndRun's §7gesetzt§8.");
                    player.sendMessage(Main.getPrefix() + "§7Stelle dich auf die Startlocation und tippe §8'§2§lset§8' §7in den Chat§8.");
                    jumpAndRunManager.getSetup().put(player, JumpAndRunSetup.STARTLOCATION);
                    break;
                case STARTLOCATION:
                    jumpAndRunManager.setStartLocation(player.getLocation());
                    player.sendMessage(Main.getPrefix() + "§7Du hast die Startlocation des §2§lJumpAndRun's §7gesetzt§8.");
                    player.sendMessage(Main.getPrefix() + "§7Stelle dich auf die Endlocation und tippe §8'§2§lset§8' §7in den Chat§8.");
                    jumpAndRunManager.getSetup().put(player, JumpAndRunSetup.ENDLOCATION);
                    break;
                case ENDLOCATION:
                    jumpAndRunManager.setEndLocation(player.getLocation());
                    player.sendMessage(Main.getPrefix() + "§7Du hast die Endlocation des §2§lJumpAndRun's §7gesetzt§8.");
                    player.sendMessage(Main.getPrefix() + "§7Stelle dich auf einen Checkpoint und tippe §8'§2§lset§8' §7in den Chat§8.");
                    player.sendMessage(Main.getPrefix() + "§7Um das §2§lJumpAndRun §7zu erstellen §8» §c/jar create");
                    jumpAndRunManager.getSetup().put(player, JumpAndRunSetup.CHECKPOINTS);
                    break;
                case CHECKPOINTS:
                    jumpAndRunManager.addCheckpoint(player.getLocation());
                    player.sendMessage(Main.getPrefix() + "§7Du hast den Checkpoint des §2§lJumpAndRun's §7gesetzt§8.");
                    player.sendMessage(Main.getPrefix() + "§7Stelle dich auf einen weiteren Checkpoint und tippe §8'§2§lset§8' §7in den Chat§8.");
                    player.sendMessage(Main.getPrefix() + "§7Um das §2§lJumpAndRun §7zu erstellen §8» §c/jar create");
                    break;
            }
        }
    }
}