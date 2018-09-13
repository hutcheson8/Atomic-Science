package com.builtbroken.atomic.content.machines.reactor.fission.core;

import com.builtbroken.atomic.AtomicScience;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/7/2018.
 */
public class BlockReactorCell extends BlockContainer
{
    public BlockReactorCell()
    {
        super(Material.IRON);
        setHardness(1);
        setResistance(5);
        setCreativeTab(AtomicScience.creativeTab);
        setTranslationKey(AtomicScience.PREFIX + "reactor.cell");
        setRegistryName(AtomicScience.PREFIX + "reactor_cell");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityReactorCell();
    }

    //-----------------------------------------------
    //--------- Triggers ---------------------------
    //----------------------------------------------

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityReactorCell)
        {
            TileEntityReactorCell reactorCell = ((TileEntityReactorCell) tileEntity);
            ItemStack heldItem = player.getHeldItem(hand);
            if (heldItem != null)
            {
                if(heldItem.getItem() == Items.STICK)
                {
                    if (!world.isRemote)
                    {
                        player.sendStatusMessage(new TextComponentString("Fuel: " + reactorCell.getFuelRuntime()), true);
                    }
                    return true;
                }
                else if (reactorCell.isItemValidForSlot(0, heldItem))
                {
                    if (!world.isRemote && reactorCell.getInventory().getStackInSlot(0) == null)
                    {
                        ItemStack copy = heldItem.splitStack(1);
                        reactorCell.getInventory().setStackInSlot(0, copy);

                        if (heldItem.getCount() <= 0)
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                        else
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, heldItem);
                        }
                        player.inventoryContainer.detectAndSendChanges();
                    }
                    return true;
                }
            }
            else
            {
                if (!world.isRemote && reactorCell.getInventory().getStackInSlot(0) != null)
                {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, reactorCell.getInventory().getStackInSlot(0));
                    reactorCell.getInventory().setStackInSlot(0, null);
                    player.inventoryContainer.detectAndSendChanges();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityReactorCell)
        {
            ((TileEntityReactorCell) tileEntity).updateStructureType();
        }
    }

    //-----------------------------------------------
    //-------- Properties ---------------------------
    //----------------------------------------------

    @Override
    public boolean  isBlockNormalCube(IBlockState state)
    {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }


    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state)
    {
        return false;
    }
}