package com.example.mysteriousresurgence.capability;

import com.example.mysteriousresurgence.MysteriousResurgenceMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = MysteriousResurgenceMod.MOD_ID)
public class ModCapabilities {
    @CapabilityInject(ISanity.class)
    public static final Capability<ISanity> SANITY_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(ISanity.class, new Storage(), Sanity::new);
    }

    public static class Storage implements Capability.IStorage<ISanity> {
        @Nullable
        @Override
        public CompoundTag writeNBT(Capability<ISanity> capability, ISanity instance, Direction side) {
            CompoundTag tag = new CompoundTag();
            ((Sanity) instance).saveNBTData(tag);
            return tag;
        }

        @Override
        public void readNBT(Capability<ISanity> capability, ISanity instance, Direction side, CompoundTag nbt) {
            ((Sanity) instance).loadNBTData(nbt);
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(
                new ResourceLocation(MysteriousResurgenceMod.MOD_ID, "sanity"),
                new ICapabilityProvider() {
                    final LazyOptional<ISanity> sanity = LazyOptional.of(Sanity::new);

                    @Nonnull
                    @Override
                    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                        if (cap == SANITY_CAPABILITY) {
                            return sanity.cast();
                        }
                        return LazyOptional.empty();
                    }
                }
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(SANITY_CAPABILITY).ifPresent(oldStore -> {
                event.getPlayer().getCapability(SANITY_CAPABILITY).ifPresent(newStore -> {
                    ((Sanity) newStore).copyFrom((Sanity) oldStore);
                });
            });
        }
    }
}
