package com.example.mysteriousresurgence;

import com.example.mysteriousresurgence.entities.*;
import com.example.mysteriousresurgence.items.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("mysteriousresurgence")
public class MysteriousResurgenceMod {
    public static final String MOD_ID = "mysteriousresurgence";
    public static final Logger LOGGER = LogManager.getLogger();

    public MysteriousResurgenceMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册物品和实体
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        
        // 注册实体属性
        modEventBus.addListener(this::entityAttributes);
        
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("神秘复苏模组加载成功！现在包含3种鬼魂类型。");
    }
    
    private void entityAttributes(EntityAttributeCreationEvent event) {
        // 注册所有鬼魂实体的属性
        event.put(ModEntities.WANDERING_GHOST.get(), 
            WanderingGhost.createAttributes().build());
        event.put(ModEntities.KNOCKING_GHOST.get(), 
            KnockingGhost.createAttributes().build());
        event.put(ModEntities.GHOST_OFFICER.get(), 
            GhostOfficer.createAttributes().build());
    }
}
