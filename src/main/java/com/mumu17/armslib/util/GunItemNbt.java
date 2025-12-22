package com.mumu17.armslib.util;

import com.mumu17.arscurios.util.InteractionHandUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Objects;

public interface GunItemNbt {

    String  OWNER_UUID = "OwnerUUID", OWNER_DIMENSION = "OwnerDimension",
            LAST_SHOOT_TIMESTAMP = "LastShootTimestamp", LAST_AMMO_COUNT = "LastAmmoCount", LAST_GUN_DAMAGE = "LastGunDamage",
            INTERACTION_HAND_ID = "InteractionHandID";

    default void setLastTimestamp(ItemStack gunItem, long timestamp) {
        CompoundTag tag = gunItem.getOrCreateTag();
        tag.putLong(LAST_SHOOT_TIMESTAMP, timestamp);
    }

    default long getLastTimestamp(ItemStack gunItem) {
        CompoundTag tag = gunItem.getTag();
        if (tag != null && tag.contains(LAST_SHOOT_TIMESTAMP)) {
            return tag.getLong(LAST_SHOOT_TIMESTAMP);
        }
        return 0L;
    }

    default void setLastAmmoCount(ItemStack gunItem, int ammoCount) {
        CompoundTag tag = gunItem.getOrCreateTag();
        tag.putInt(LAST_AMMO_COUNT, ammoCount);
    }

    default int getLastAmmoCount(ItemStack gunItem) {
        CompoundTag tag = gunItem.getTag();
        if (tag != null && tag.contains(LAST_AMMO_COUNT)) {
            return tag.getInt(LAST_AMMO_COUNT);
        }
        return 0;
    }

    default void setGunDamage(ItemStack gunItem, float damage) {
        CompoundTag tag = gunItem.getOrCreateTag();
        tag.putFloat(LAST_GUN_DAMAGE, damage);
    }

    default float getGunDamage(ItemStack gunItem) {
        CompoundTag tag = gunItem.getTag();
        if (tag != null && tag.contains(LAST_GUN_DAMAGE)) {
            return tag.getFloat(LAST_GUN_DAMAGE);
        }
        return 0.0F;
    }

    default void setOwner(ItemStack gunItem, LivingEntity owner) {
        CompoundTag tag = gunItem.getOrCreateTag();
        if (owner != null) {
            tag.putUUID(OWNER_UUID, owner.getUUID());
            tag.putString(OWNER_DIMENSION, owner.level().dimension().location().toString());
        }
    }

    default LivingEntity getOwner(ItemStack gunItem) {
        CompoundTag tag = gunItem.getOrCreateTag();
        if (!tag.hasUUID(OWNER_UUID) || !tag.contains(OWNER_DIMENSION)) {
            return null;
        }
        String dimId = tag.getString(OWNER_DIMENSION);
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimId));
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return null;
        if (server.getLevel(dimension) == null)
            return null;

        return (LivingEntity) Objects.requireNonNull(server.getLevel(dimension)).getEntity(tag.getUUID(OWNER_UUID));
    }

    default void setInteractionHand(ItemStack gunItem, InteractionHand hand) {
        CompoundTag tag = gunItem.getOrCreateTag();
        if (hand != null) {
            tag.putString(INTERACTION_HAND_ID, InteractionHandUtil.getSlotName(hand));
        }
    }

    default InteractionHand getInteractionHand(ItemStack gunItem) {
        CompoundTag tag = gunItem.getTag();
        if (tag != null && tag.contains(INTERACTION_HAND_ID)) {
            String id = tag.getString(INTERACTION_HAND_ID);
            return InteractionHandUtil.getSlotByName(id);
        }
        return InteractionHand.MAIN_HAND;
    }

    default void setIsArsMode(ItemStack gunItem, boolean isArsMode) {

    }

    default boolean getIsArsMode(ItemStack gunItem) {
        return false;
    }

    default void setIsIronsMode(ItemStack gunItem, boolean isIronsMode) {

    }

    default boolean getIsIronsMode(ItemStack gunItem) {
        return false;
    }
}
