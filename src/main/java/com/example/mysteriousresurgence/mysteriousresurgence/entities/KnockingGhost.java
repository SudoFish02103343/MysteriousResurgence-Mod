package com.example.mysteriousresurgence.entities;

import com.example.mysteriousresurgence.capability.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class KnockingGhost extends Monster {
    private int knockTimer = 0;
    private boolean isKnocking = false;
    
    public KnockingGhost(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 50.0D) // 更高的生命值
            .add(Attributes.ATTACK_DAMAGE, 10.0F) // 更高的伤害
            .add(Attributes.MOVEMENT_SPEED, 0.15F) // 更慢的移动速度
            .add(Attributes.ARMOR, 5.0D)
            .add(Attributes.FOLLOW_RANGE, 30.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 敲门鬼对物理伤害免疫
        if (!source.isMagic() && !source.isFire() && !source.isExplosion()) {
            this.playSound(SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, 1.0F, 0.5F);
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        
        // 敲门行为
        knockTimer++;
        if (knockTimer >= 100) { // 每5秒敲门一次
            performKnock();
            knockTimer = 0;
        }
        
        // 降低附近玩家的理智值（比游荡鬼魂更强）
        if (!level.isClientSide() && tickCount % 20 == 0) {
            Player player = level.getNearestPlayer(this, 20.0D);
            if (player != null) {
                player.getCapability(ModCapabilities.SANITY_CAPABILITY).ifPresent(sanity -> {
                    sanity.consumeSanity(8.0F); // 更高的理智消耗
                    
                    // 近距离时给予玩家特殊提示
                    if (this.distanceTo(player) < 8.0F) {
                        player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("§4你听到了敲门声...门就要开了！"), 
                            true
                        );
                    }
                });
            }
        }
    }
    
    private void performKnock() {
        // 播放敲门声
        this.playSound(SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, 2.0F, 0.8F);
        isKnocking = true;
        
        // 寻找最近的玩家
        Player player = level.getNearestPlayer(this, 25.0D);
        if (player != null) {
            // 如果玩家在室内（有屋顶），造成额外效果
            BlockPos playerPos = player.blockPosition();
            if (!level.canSeeSky(playerPos)) {
                // 玩家在室内，敲门鬼更危险
                player.getCapability(ModCapabilities.SANITY_CAPABILITY).ifPresent(sanity -> {
                    sanity.consumeSanity(15.0F);
                });
                
                // 小几率直接伤害玩家
                if (level.random.nextFloat() < 0.3F) {
                    player.hurt(DamageSource.MAGIC, 5.0F);
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§c门开了！有什么东西进来了！"), 
                        true
                    );
                }
            }
        }
        
        // 重置敲门状态
        isKnocking = false;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ZOMBIE_HURT;
    }
    
    public boolean isKnocking() {
        return isKnocking;
    }
}
