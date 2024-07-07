package me.rubyyyh.crimepackrp.tasks;

import me.rubyyyh.crimepackrp.CrimePackRp;
import me.rubyyyh.crimepackrp.Data;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class BombsTask extends BukkitRunnable {

    public BombsTask() {
        this.runTaskTimer(CrimePackRp.plugin, 0, 40L);
    }

    @Override
    public void run() {
        for (UUID uuid : Data.bombs.keySet()) {
            Location loc = Data.bombs.get(uuid).clone().add(0.5, 0.5, 0.5);
            World world = loc.getWorld();

            Particle particle = Particle.SQUID_INK;

            world.spawnParticle(particle, loc, 100, 0, 0, 0, 5, null, true);
            world.playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 2, 1);
        }
    }
}
