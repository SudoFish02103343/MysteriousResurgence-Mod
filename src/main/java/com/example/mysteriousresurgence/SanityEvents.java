package com.example.mysteriousresurgence.events;

import com.example.mysteriousresurgence.MysteriousResurgenceMod;
import com.example.mysteriousresurgence.capability.ModCapabilities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MysteriousResurgenceMod.MOD_ID)
public class SanityEvents {
    
    private static int lowSanityTimer = 0;
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level.isClientSide()) {
            Player player = event.player;
            
            player.getCapability(ModCapabilities.SANITY_CAPABILITY).ifPresent(sanity -> {
                float currentSanity = sanity.getSanity();
                
                // 理智值自然缓慢恢复（在安全环境下）
                if (player.level.getMaxLocalRawBrightness(player.blockPosition()) > 7) {
                    if (currentSanity < sanity.getMaxSanity() && player.tickCount % 100 == 0) {
                        sanity.addSanity(1.0F);
                    }
                }
                
                // 低理智值效果
                if (currentSanity < 30.0F) {
                    lowSanityTimer++;
                    if (lowSanityTimer % 40 == 0) { // 每2秒触发一次
                        // 播放心跳声
                        player.playSound(SoundEvents.BEACON_AMBIENT, 0.5F, 0.5F);
                        
                        // 发送警告消息
                        if (currentSanity < 10.0F) {
                            player.displayClientMessage(
                                net.minecraft.network.chat.Component.literal("§c你的理智即将崩溃！快找安全的地方！"), 
                                true
                            );
                        }
                    }
                } else {
                    lowSanityTimer = 0;
                }
                
                // 理智值归零的惩罚
                if (currentSanity <= 0.0F) {
                    if (player.tickCount % 60 == 0) { // 每3秒触发一次
                        player.hurt(net.minecraft.world.damagesource.DamageSource.MAGIC, 2.0F);
                        player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("§4理智崩溃！你开始伤害自己！"), 
                            true
                        );
                    }
                }
            });
        }
    }
}
