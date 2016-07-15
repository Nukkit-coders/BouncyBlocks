package bouncyblocks;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityMotionEvent;
import cn.nukkit.event.player.PlayerInvalidMoveEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.plugin.PluginBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BouncyBlocks extends PluginBase implements Listener {

    private List<Integer> blocks = new ArrayList<>();

    private int force = 5;

    private Set<String> jumpPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        blocks = getConfig().getIntegerList("blocks");
        force = getConfig().getInt("jump_force");

        if (!blocks.isEmpty()) {
            getServer().getPluginManager().registerEvents(this, this);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (e.isCancelled() || !p.isOnGround() || p.gamemode > 1) {
            return;
        }

        int id = p.getLevel().getBlockIdAt((int) e.getTo().x, (int) (e.getTo().y - 0.3), (int) e.getTo().z);

        if (blocks.contains(id)) {
            p.setMotion(p.temporalVector.setComponents(0, force, 0));
            jumpPlayers.add(p.getName());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity p = e.getEntity();

        if (e.isCancelled()) {
            return;
        }

        if (p instanceof Player && e.getCause() == EntityDamageEvent.CAUSE_FALL) {
            int id = p.getLevel().getBlockIdAt((int) p.x, (int) (p.y - 0.3), (int) p.z);

            if (blocks.contains(id)) {
                e.setCancelled();
            } else if (jumpPlayers.contains(p.getName())) {
                e.setCancelled();
                jumpPlayers.remove(p.getName());
            }
        }
    }

    @EventHandler
    public void onInvalidMove(PlayerInvalidMoveEvent e) {
        if (jumpPlayers.contains(e.getPlayer().getName())) {
            e.setCancelled();
        }
    }
}
