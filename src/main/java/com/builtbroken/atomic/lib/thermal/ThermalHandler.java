package com.builtbroken.atomic.lib.thermal;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntSupplier;

/**
 * Created by Dark(DarkGuardsman, Robert) on 5/11/2018.
 */
public class ThermalHandler
{
    private static final Map<Block, HeatSpreadFunction> blockHeatFunction = new HashMap();

    //Heat weight is treated as a percentage of pressure. For each block contacting the surface heat
    //  spread logic will calculate a total and then break it down into a precentage. So if you have two
    //  blocks with 20 and 80. The total heat weight would be 100. The first block would get 20% and the second 80%.

    //Weight of heat given for a material
    private static final Map<Material, IntSupplier> materialGiveRate = new HashMap();
    //Weight of heat received for a material
    private static final Map<Material, IntSupplier> materialReceiveRate = new HashMap();

    //Amount of heat the block can store
    private static final Map<Material, IntSupplier> materialCapacity = new HashMap();
    //Amount of heat lost transferring heat
    private static final Map<Material, IntSupplier> materialLoss = new HashMap();

    //Weight of heat given away
    private static final Map<Block, IntSupplier> blockGiveRate = new HashMap();
    //Weight of heat received
    private static final Map<Block, IntSupplier> blockReceiveRate = new HashMap();

    //Amount of heat the block can store
    private static final Map<Block, IntSupplier> blockCapacity = new HashMap();
    //Amount of heat lost transferring heat
    private static final Map<Block, IntSupplier> blockLoss = new HashMap();

    public static void init()
    {
        setHeatMoveRate(Material.IRON, 5000, 5000, 50, 2);

        setHeatMoveRate(Blocks.GOLD_BLOCK, 8000, 8000, 200, 1);
        setHeatMoveRate(Blocks.WATER, 100, 1000, 1000, 0);
        setHeatMoveRate(Blocks.FLOWING_WATER, 100, 1000, 1000, 0);
        blockHeatFunction.put(Blocks.WATER, (self, target) -> {
            if (self.getMaterial() == target.getMaterial())
            {
                return 1000;
            }
            return -1;
        });
        blockHeatFunction.put(Blocks.FLOWING_WATER, (self, target) -> {
            if (self.getMaterial() == target.getMaterial())
            {
                return 1000;
            }
            return -1;
        });
    }

    public static void setHeatMoveRate(Block block, int give, int receive, int cap, int loss)
    {
        blockGiveRate.put(block, () -> give);
        blockReceiveRate.put(block, () -> receive);
        blockCapacity.put(block, () -> cap);
        blockLoss.put(block, () -> loss);
    }

    public static void setHeatMoveRate(Material material, int give, int receive, int cap, int loss)
    {
        materialGiveRate.put(material, () -> give);
        materialReceiveRate.put(material, () -> receive);
        materialCapacity.put(material, () -> cap);
        materialLoss.put(material, () -> loss);
    }

    public static int getHeatMoveWeight(IBlockState giver, IBlockState receiver)
    {
        final Block selfBlock = giver.getBlock();
        if (blockHeatFunction.containsKey(selfBlock))
        {
            int weight = blockHeatFunction.get(selfBlock).getSpreadWeight(giver, receiver);
            if (weight >= 0)
            {
                return weight;
            }
        }
        return (int) Math.min(getBlockReceiveWeight(receiver), getBlockGiveWeight(giver));
    }

    public static int getBlockLoss(IBlockState state)
    {
        final Block block = state.getBlock();
        if (blockLoss.containsKey(block))
        {
            return blockLoss.get(block).getAsInt();
        }

        final Material material = state.getMaterial();
        if (materialLoss.containsKey(material))
        {
            return materialLoss.get(material).getAsInt();
        }
        return 10;
    }

    public static int getBlockCapacity(IBlockState state)
    {
        final Block block = state.getBlock();
        if (blockCapacity.containsKey(block))
        {
            return blockCapacity.get(block).getAsInt();
        }

        final Material material = state.getMaterial();
        if (materialCapacity.containsKey(material))
        {
            return materialCapacity.get(material).getAsInt();
        }
        return 20;
    }

    public static int getBlockGiveWeight(IBlockState state)
    {
        final Block block = state.getBlock();
        if (blockGiveRate.containsKey(block))
        {
            return blockGiveRate.get(block).getAsInt();
        }

        final Material material = state.getMaterial();
        if (materialGiveRate.containsKey(material))
        {
            return materialGiveRate.get(material).getAsInt();
        }
        return 100;
    }

    public static float getBlockReceiveWeight(IBlockState state)
    {
        final Block block = state.getBlock();
        if (blockReceiveRate.containsKey(block))
        {
            return blockReceiveRate.get(block).getAsInt();
        }

        final Material material = state.getMaterial();
        if (materialReceiveRate.containsKey(material))
        {
            return materialReceiveRate.get(material).getAsInt();
        }
        return 100;
    }

    public static void main(String... args)
    {
        int[] heatRates = new int[]{100, 100, 1000, 1000, 5000, 5000};
        int total = 12200;

        int heatToGive = 1000;

        for (int i = 0; i < heatRates.length; i++)
        {
            double per = heatRates[i] / (float) total;

            int heat = (int) Math.floor(heatToGive * per);

            System.out.printf("[%d] Rate: %d; Per: %.2f; Heat: %d\n", i, heatRates[i], per, heat);
        }

    }
}
