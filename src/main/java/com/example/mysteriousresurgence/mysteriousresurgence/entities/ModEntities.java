package com.example.mysteriousresurgence.entities;

import com.example.mysteriousresurgence.MysteriousResurgenceMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MysteriousResurgenceMod.MOD_ID);

    // 游荡鬼魂
    public static final RegistryObject<EntityType<WanderingGhost>> WANDERING_GHOST = 
        ENTITIES.register("wandering_ghost", 
            () -> EntityType.Builder.of(WanderingGhost::new, MobCategory.MONSTER)
                .sized(0.6F, 1.95F)
                .clientTrackingRange(8)
                .build("wandering_ghost"));
    
    // 敲门鬼
    public static final RegistryObject<EntityType<KnockingGhost>> KNOCKING_GHOST = 
        ENTITIES.register("knocking_ghost", 
            () -> EntityType.Builder.of(KnockingGhost::new, MobCategory.MONSTER)
                .sized(0.7F, 2.2F)
                .clientTrackingRange(10)
                .build("knocking_ghost"));
    
    // 鬼差
    public static final RegistryObject<EntityType<GhostOfficer>> GHOST_OFFICER = 
        ENTITIES.register("ghost_officer", 
            () -> EntityType.Builder.of(GhostOfficer::new, MobCategory.MONSTER)
                .sized(0.8F, 2.5F)
                .clientTrackingRange(12)
                .build("ghost_officer"));
}
