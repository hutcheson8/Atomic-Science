package com.builtbroken.atomic.content.items;

import com.builtbroken.atomic.AtomicScience;
import com.builtbroken.atomic.api.AtomicScienceAPI;
import com.builtbroken.atomic.api.armor.IAntiPoisonArmor;
import com.builtbroken.atomic.api.effect.IIndirectEffectInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Simple hazmat suit that takes damage as its used
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) & Calclavia
 */
public class ItemHazmat extends ItemArmor implements IAntiPoisonArmor
{
    /** Prefix for model textures */
    public static final String ARMOR_MODEL_TEXTURE = AtomicScience.PREFIX + AtomicScience.MODEL_TEXTURE_DIRECTORY + "armor/hazmat.png";
    /** Prefix for item textures */
    public static final String ARMOR_TEXTURE_FOLDER = AtomicScience.PREFIX + "armor/";

    /** Armor material */
    public static ItemArmor.ArmorMaterial hazmatArmorMaterial;

    public static int damagePerTick = 1;
    public static int damagePerAttack = 100; //TODO take damage faster from attacks

    public ItemHazmat(int slot, String type)
    {
        super(hazmatArmorMaterial, 0, slot);
        this.setCreativeTab(AtomicScience.creativeTab);
        this.setUnlocalizedName(AtomicScience.PREFIX + "hazmat." + type);
        this.setTextureName(ARMOR_TEXTURE_FOLDER + "hazmat_" + type);
        this.setMaxDamage(200000);
    }

    ///------------------------------------------------------------------------------------
    /// Texture stuff
    ///------------------------------------------------------------------------------------

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        return ARMOR_MODEL_TEXTURE;
    }

    ///------------------------------------------------------------------------------------
    /// Poison armor API stuff
    ///------------------------------------------------------------------------------------

    @Override
    public boolean doesArmorProtectFromSource(ItemStack itemStack, EntityLivingBase entity, IIndirectEffectInstance instance)
    {
        if (isFullArmorSetNeeded(itemStack, entity, instance) && !hasFullSetOfArmor(entity))
        {
            return false;
        }
        return instance.getIndirectEffectType() == AtomicScienceAPI.RADIATION;
    }

    protected boolean hasFullSetOfArmor(EntityLivingBase entity)
    {
        ItemStack itemStack = null;
        for (int i = 1; i < 5; i++)
        {
            final ItemStack slotStack = entity.getEquipmentInSlot(i);
            if (slotStack != null)
            {
                //Init compare stack
                if (itemStack == null)
                {
                    itemStack = slotStack;
                    continue;
                }

                //Check if item is part of set
                if (slotStack.getItem() instanceof IAntiPoisonArmor && !isArmorPartOfSet(itemStack, slotStack))
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isArmorPartOfSet(ItemStack armorStack, ItemStack compareStack)
    {
        return armorStack.getItem() instanceof ItemHazmat && compareStack.getItem() instanceof ItemHazmat;
    }

    @Override
    public void onArmorProtectFromSource(ItemStack itemStack, EntityLivingBase entityLiving, IIndirectEffectInstance instance)
    {
        if (instance.getIndirectEffectType() == AtomicScienceAPI.RADIATION)
        {
            itemStack.damageItem(((int) (damagePerTick * instance.getIndirectEffectPower())), entityLiving);
        }
    }

    @Override
    public int getArmorType()
    {
        return this.armorType;
    }
}
