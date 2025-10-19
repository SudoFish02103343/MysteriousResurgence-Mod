package com.example.mysteriousresurgence.entities;

import com.example.mysteriousresurgence.capability.ModCapabilities;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.LightLayer;

public class WanderingGhost extends Monster {
    public WanderingGhost(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.ATTACK_DAMAGE, 4.0F)
            .add(Attributes.MOVEMENT_SPEED, 0.25F)
            .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 鬼魂只能被特定伤害类型伤害
        if (source.isMagic()) {
            return super.hurt(source, amount);
        }
        return false; // 物理伤害无效
    }

    @Override
    public void tick() {
        super.tick();
        
        // 在光亮处受到伤害
        BlockPos pos = this.blockPosition();
        int lightLevel = level.getBrightness(LightLayer.BLOCK, pos);
        if (lightLevel > 7 && tickCount % 20 == 0) {
            this.hurt(DamageSource.MAGIC, 2.0F);
        }
        
        // 降低附近玩家的理智值
        if (!level.isClientSide() && tickCount % 40 == 0) {
            Player player = level.getNearestPlayer(this, 10.0D);
            if (player != null) {
                player.getCapability(ModCapabilities.SANITY_CAPABILITY).ifPresent(sanity -> {
                    sanity.consumeSanity(5.0F);
                });
            }
        }
    }

    @Override
    public boolean isInvisible() {
        return true; // 鬼魂默认隐形
    }
}
