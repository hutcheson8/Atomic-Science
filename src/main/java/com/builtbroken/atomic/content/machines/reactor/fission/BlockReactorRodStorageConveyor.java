package com.builtbroken.atomic.content.machines.reactor.fission;

import com.builtbroken.atomic.AtomicScience;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/20/2018.
 */
public class BlockReactorRodStorageConveyor extends Block
{
    public BlockReactorRodStorageConveyor()//KNIVES testme
    {
        super(Material.iron);
        setHardness(1);
        setResistance(5);
        setCreativeTab(AtomicScience.creativeTab);
        setBlockTextureName(AtomicScience.PREFIX + "plasma");
        setBlockName(AtomicScience.PREFIX + "reactor.rodstorageconveyor");
    }
}
