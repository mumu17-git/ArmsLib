package com.mumu17.armslib.util;

import com.mumu17.armslib.mixin.tacz.AmmoBoxItemMixin;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ArsCuriosLivingEntity;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.AmmoItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Unique;

public class ArmsLibAmmoUtil {

    public static final int MAX_AMMO_COUNT = 9999;
    public static final String AMMO_BOX_MODE_KEY = "ArmsLib:IsMode";

    public static int handleInventoryAmmo(ItemStack stack, Inventory inventory) {
        int cacheInventoryAmmoCount = 0;

        for (int i = 0; i < ExtendedHand.values().length; i++) {
            ExtendedHand hand = ExtendedHand.values()[i];
            if (hand.isAmmoBox()) {
                ItemStack curiosStack = ArsCuriosInventoryHelper.getCuriosInventoryItem(inventory.player, hand.getSlotName());
                int tmp = handleInventoryAmmo(stack, null, curiosStack, cacheInventoryAmmoCount, -1);
                if (tmp != cacheInventoryAmmoCount) {
                    cacheInventoryAmmoCount = tmp;
                }
            }
        }

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            cacheInventoryAmmoCount = handleInventoryAmmo(stack, inventory, null, cacheInventoryAmmoCount, i);
            if (cacheInventoryAmmoCount >= MAX_AMMO_COUNT) {
                break;
            }
        }
        return cacheInventoryAmmoCount;
    }

    public static int handleInventoryAmmo(ItemStack stack, Inventory inventory, ItemStack curiosStack, int cacheInventoryAmmoCount, int i) {
        if ((inventory != null) || (curiosStack != null)) {
            ItemStack inventoryItem = (inventory != null ? inventory.getItem(i) : curiosStack);
            if (inventoryItem.getItem() instanceof AmmoItem iAmmo) {
                if (iAmmo.isAmmoOfGun(stack, inventoryItem)) {
                    cacheInventoryAmmoCount += inventoryItem.getCount();
                }
            }
            if (inventoryItem.getItem() instanceof AmmoBoxItem iAmmoBox) {
                if (ArmsLibAmmoUtil.ArmsLib$isAmmoBoxOfGun(stack, inventoryItem, iAmmoBox)) {
                    if (iAmmoBox.isAllTypeCreative(inventoryItem) || iAmmoBox.isCreative(inventoryItem)) {
                        cacheInventoryAmmoCount = MAX_AMMO_COUNT;
                        return cacheInventoryAmmoCount;
                    }
                    cacheInventoryAmmoCount += iAmmoBox.getAmmoCount(inventoryItem);
                }
            }
        }

        return cacheInventoryAmmoCount;
    }

    public static boolean isSelectedSpellSlot(ExtendedHand ammoBoxSlot, LivingEntity player) {
        return true;
    }

    @Unique
    public static boolean ArmsLib$isAmmoBoxOfGun(ItemStack gun, ItemStack ammoBox, IAmmoBox iAmmoBox) {
        ArmsLibAmmoUtil.setMode(ammoBox, true);
        return iAmmoBox.isAmmoBoxOfGun(gun, ammoBox);
    }

    @Unique
    public static void setMode(ItemStack ammoBox, boolean mode) {
        if (ammoBox != null && ammoBox.getItem() instanceof IAmmoBox) {
            ammoBox.getOrCreateTag().putBoolean(AMMO_BOX_MODE_KEY, mode);
        }
    }

    @Unique
    public static boolean getMode(ItemStack ammoBox) {
        if (ammoBox != null && ammoBox.getItem() instanceof IAmmoBox && ammoBox.getOrCreateTag().contains(AMMO_BOX_MODE_KEY)) {
            boolean b = ammoBox.getOrCreateTag().getBoolean(AMMO_BOX_MODE_KEY);
            ArmsLibAmmoUtil.setMode(ammoBox, false);
            return b;
        }
        return false;
    }
}
