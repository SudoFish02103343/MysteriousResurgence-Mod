package com.example.mysteriousresurgence.events;

import com.example.mysteriousresurgence.MysteriousResurgenceMod;
import com.example.mysteriousresurgence.entities.ModEntities;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MysteriousResurgenceMod.MOD_ID)
public class EntitySpawningEvents {
    
    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        // 调整生物生成权重
        if (!event.getLevel().isClientSide()) {
            // 获取世界的生物生成设置
            var serverLevel = (net.minecraft.server.level.ServerLevel) event.getLevel();
            var chunkSource = serverLevel.getChunkSource();
            var spawner = chunkSource.getLastSpawnState();
            
            if (spawner != null) {
                // 设置不同鬼魂的生成权重
                // 游荡鬼魂：常见（权重6）
                // 敲门鬼：少见（权重3） 
                // 鬼差：稀有（权重1）
                var mobSpawnSettings = serverLevel.getChunkSource().getGenerator().getSettings().getMobSpawnSettings();
                
                // 注意：实际生成权重的精细控制需要更复杂的实现
                // 这里我们依赖默认的Minecraft生成机制
            }
        }
    }
    
    @SubscribeEvent
    public static void onLivingSpawn(net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn event) {
        // 控制鬼魂的生成条件
        if (event.getEntity().getType() == ModEntities.KNOCKING_GHOST.get()) {
            // 敲门鬼只在夜晚生成
            if (event.getLevel().getDayTime() % 24000 < 12000) {
                event.setCanceled(true);
            }
        } else if (event.getEntity().getType() == ModEntities.GHOST_OFFICER.get()) {
            // 鬼差生成几率很低，且只在月圆之夜附近生成
            long dayTime = event.getLevel().getDayTime();
            boolean isFullMoon = (dayTime / 24000) % 8 == 0; // 简化版月圆判断
            
            if (!isFullMoon && event.getLevel().random.nextFloat() > 0.1F) {
                event.setCanceled(true);
            }
        }
    }
}
