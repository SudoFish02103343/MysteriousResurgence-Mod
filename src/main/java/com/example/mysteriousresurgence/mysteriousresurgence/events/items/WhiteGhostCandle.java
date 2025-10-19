package com.example.mysteriousresurgence.items;

import com.example.mysteriousresurgence.capability.ModCapabilities;
import com.example.mysteriousresurgence.entities.WanderingGhost;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class WhiteGhostCandle extends Item {
    public WhiteGhostCandle(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            // 吸引周围的鬼魂
            attractGhosts(level, player);
            
            // 消耗理智值
            player.getCapability(ModCapabilities.SANITY_CAPABILITY).ifPresent(sanity -> {
                sanity.consumeSanity(15.0F);
            });
            
            // 添加冷却时间
            player.getCooldowns().addCooldown(this, 100); // 5秒冷却
            
            // 播放音效
            player.playSound(net.minecraft.sounds.SoundEvents.CANDLE_EXTINGUISH, 1.0F, 0.8F);
            
            // 发送消息给玩家
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§f你点燃了白色鬼烛，周围的寒意变得更重了..."), 
                true
            );
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    private void attractGhosts(Level level, Player player) {
        // 搜索半径20格内的所有怪物
        AABB area = new AABB(
            player.getX() - 20, player.getY() - 10, player.getZ() - 20,
            player.getX() + 20, player.getY() + 10, player.getZ() + 20
        );
        
        List<Entity> entities = level.getEntities(player, area);
        
        for (Entity entity : entities) {
            if (entity instanceof Monster monster) {
                // 如果是鬼魂，强制吸引到玩家位置
                if (entity instanceof WanderingGhost) {
                    monster.setTarget(player);
                    // 给鬼魂加速效果，让它更快接近玩家
                    monster.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 200, 1
                    ));
                }
            }
        }
        
        // 额外生成1-2个鬼魂
        if (level.random.nextFloat() < 0.7F) { // 70%几率生成额外鬼魂
            int ghostCount = level.random.nextInt(2) + 1; // 1-2个
            
            for (int i = 0; i < ghostCount; i++) {
                WanderingGhost ghost = new WanderingGhost(
                    com.example.mysteriousresurgence.entities.ModEntities.WANDERING_GHOST.get(), 
                    level
                );
                
                // 在玩家周围5-10格范围内生成
                double offsetX = (level.random.nextDouble() - 0.5) * 10;
                double offsetZ = (level.random.nextDouble() - 0.5) * 10;
                
                ghost.setPos(
                    player.getX() + offsetX,
                    player.getY(),
                    player.getZ() + offsetZ
                );
                
                ghost.setTarget(player); // 立即锁定玩家
                
                if (level.noCollision(ghost)) {
                    level.addFreshEntity(ghost);
                }
            }
        }
    }
    
    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        // 制作鬼烛时会损失理智
        player.getCapability(ModCapabilities.SANITY_CAPABILITY).ifPresent(sanity -> {
            sanity.consumeSanity(5.0F);
        });
        super.onCraftedBy(stack, level, player);
    }
}
