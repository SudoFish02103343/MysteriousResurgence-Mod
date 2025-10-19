package com.example.mysteriousresurgence.world;

import com.example.mysteriousresurgence.MysteriousResurgenceMod;
import com.example.mysteriousresurgence.entities.*;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = MysteriousResurgenceMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntitySpawning {
    
    @SubscribeEvent
    public static void entityAttributes(EntityAttributeCreationEvent event) {
        // 注册所有鬼魂实体的属性
        event.put(ModEntities.WANDERING_GHOST.get(), 
            WanderingGhost.createAttributes().build());
        event.put(ModEntities.KNOCKING_GHOST.get(), 
            KnockingGhost.createAttributes().build());
        event.put(ModEntities.GHOST_OFFICER.get(), 
            GhostOfficer.createAttributes().build());
    }
    
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 设置所有鬼魂的生成规则
            SpawnPlacements.register(ModEntities.WANDERING_GHOST.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules
            );
            
            SpawnPlacements.register(ModEntities.KNOCKING_GHOST.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules
            );
            
            SpawnPlacements.register(ModEntities.GHOST_OFFICER.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules
            );
        });
    }
}
