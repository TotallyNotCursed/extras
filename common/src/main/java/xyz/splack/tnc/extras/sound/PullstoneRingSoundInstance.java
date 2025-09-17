package xyz.splack.tnc.extras.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import xyz.splack.tnc.extras.item.PullstoneRingItem;

public class PullstoneRingSoundInstance extends AbstractTickableSoundInstance {
    private final Player player;

    public PullstoneRingSoundInstance(Player player) {
        super(ModSounds.PULLSTONE_RING_USE.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.delay = 0;

        // Set initial position
        this.x = this.player.getX();
        this.y = this.player.getY();
        this.z = this.player.getZ();
    }

    @Override
    public void tick() {
        /*
         Stop if the player is dead, not using an item,
         OR the item being used is NOT the Pullstone Ring.
        */
        if (!this.player.isAlive()
                || !this.player.isUsingItem()
                || !(this.player.getUseItem().getItem() instanceof PullstoneRingItem)) {
            this.stop();
            return;
        }

        // Update position every tick to follow the player
        this.x = this.player.getX();
        this.y = this.player.getY();
        this.z = this.player.getZ();
    }

    /*
     Prevents the sound from starting if the player isn't actually using the ring. This is a good
     safeguard.
    */
    @Override
    public boolean canPlaySound() {
        return !player.isSilent();
    }
}
