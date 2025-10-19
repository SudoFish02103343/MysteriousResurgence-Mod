package com.example.mysteriousresurgence.events;

import com.example.mysteriousresurgence.MysteriousResurgenceMod;
import com.example.mysteriousresurgence.capability.ModCapabilities;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MysteriousResurgenceMod.MOD_ID)
public class SanityTriggers {
    
    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        // 玩家受伤时降低理智值
        if (event.getEntity() instanceof Player player) {
            player.getCapability(ModCapabilities.SANITY_CAPABILITY).ifPresent(sanity -> {
                sanity.consumeSanity(event.getAmount() * 0.5F); // 受伤量的一半作为理智损失
            });
        }
    }
    
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        // 玩家目睹死亡时降低理智值
        if (event.getEntity() instanceof Monster && event.getSource().getEntity() instanceof Player player) {
            player.getCapability(ModCapabilities.SANITY_CAPABILITY).ifPresent(sanity -> {
                sanity.consumeSanity(5.0F); // 每次击杀怪物损失5点理智
            });
        }
        
        // 玩家死亡时重置理智值（或者可以设置为惩罚性减少）
        if (event.getEntity() instanceof Player player) {
            player.getCapability(ModCapabilities.SANITY_CAPABILITY).ifPresent(sanity -> {
                sanity.setSanity(sanity.getMaxSanity() * 0.7F); // 死亡后恢复70%理智
            });
        }
    }
    
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        // 破坏某些特定方块时降低理智值（比如墓碑、诡异的方块）
        Player player = event.getPlayer();
        if (player != null) {
            // 检查是否破坏了"受诅咒"的方块
            if (event.getState().getBlock() == net.minecraft.world.level.block.Blocks.SPAWNER ||
                event.getState().getBlock() == net.minecraft.world.level.block.Blocks.END_PORTAL_FRAME) {
                
                player.getCapability(ModCapabilities.SANITY_CAPABILITY).ifPresent(sanity -> {
                    sanity.consumeSanity(10.0F);
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§5你破坏了不该破坏的东西...感觉有什么在盯着你"), 
                        true
                    );
                });
            }
        }
    }
}
