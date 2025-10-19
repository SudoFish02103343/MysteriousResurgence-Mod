package com.example.mysteriousresurgence.entities;

import com.example.mysteriousresurgence.capability.ModCapabilities;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class GhostOfficer extends Monster {
    private int captureCooldown = 0;
    
    public GhostOfficer(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 80.0D) // 很高的生命值
            .add(Attributes.ATTACK_DAMAGE, 8.0F)
            .add(Attributes.MOVEMENT_SPEED, 0.20F)
            .add(Attributes.ARMOR, 8.0D)
            .add(Attributes.FOLLOW_RANGE, 35.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D); // 免疫击退
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 鬼差对所有伤害都有抗性
        float reducedAmount = amount * 0.3F; // 只受30%伤害
        
        // 播放特殊的受伤音效
        if (source.getEntity() instanceof Player) {
            this.playSound(SoundEvents.IRON_GOLEM_HURT, 1.0F, 0.8F);
        }
        
        return super.hurt(source, reducedAmount);
    }

    @Override
    public void tick() {
        super.tick();
        
        // 捕获能力冷却
        if (captureCooldown > 0) {
            captureCooldown--;
        }
        
        // 鬼差能力：可以"关押"其他鬼魂
        if (!level.isClientSide() && tickCount % 80 == 0) {
            attemptCaptureOtherGhosts();
        }
        
        // 对玩家的特殊效果
        Player player = level.getNearestPlayer(this, 15.0D);
        if (player != null) {
            // 持续降低理智值
            player.getCapability(ModCapabilities.SANITY_CAPABILITY).ifPresent(sanity -> {
                float distance = (float) this.distanceTo(player);
                float sanityLoss = 20.0F / distance; // 很强的理智侵蚀
                sanity.consumeSanity(sanityLoss);
            });
            
            // 近距离特殊效果
            if (this.distanceTo(player) < 6.0F && captureCooldown == 0) {
                attemptCapturePlayer(player);
            }
        }
    }
    
    private void attemptCaptureOtherGhosts() {
        // 搜索附近的鬼魂（包括游荡鬼魂和敲门鬼）
        net.minecraft.world.phys.AABB area = this.getBoundingBox().inflate(12.0D);
        var entities = level.getEntities(this, area);
        
        for (var entity : entities) {
            // 如果是其他类型的鬼魂，有几率将其"关押"（移除）
            if ((entity instanceof WanderingGhost || entity instanceof KnockingGhost) && 
                !(entity instanceof GhostOfficer)) {
                
                if (level.random.nextFloat() < 0.2F) { // 20%几率
                    entity.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
                    this.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 1.0F);
                    
                    // 给鬼差恢复生命值作为奖励
                    this.heal(10.0F);
                }
            }
        }
    }
    
    private void attemptCapturePlayer(Player player) {
        // 尝试"关押"玩家 - 造成大量伤害和效果
        player.hurt(DamageSource.MAGIC, 12.0F);
        
        // 大幅降低理智值
        player.getCapability(ModCapabilities.SANITY_CAPABILITY).ifPresent(sanity -> {
            sanity.consumeSanity(25.0F);
        });
        
        // 施加负面效果
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.WEAKNESS, 200, 1
        ));
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 100, 2
        ));
        
        player.displayClientMessage(
            net.minecraft.network.chat.Component.literal("§4你被鬼差抓住了！快逃！"), 
            true
        );
        
        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.5F, 0.7F);
        captureCooldown = 100; // 5秒冷却
    }

    @Override
    public boolean isInvisible() {
        return false; // 鬼差不隐形，显得更威严
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.IRON_GOLEM_STEP;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }
}
