package com.builtbroken.atomic.content.machines.processing.recipes;

import com.builtbroken.atomic.content.machines.processing.TileEntityProcessingMachine;
import com.builtbroken.atomic.proxy.ProxyLoader;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/25/2018.
 */
public class ProcessingRecipeList<H extends TileEntityProcessingMachine> extends ProxyLoader
{
    private final ArrayList<ProcessingRecipe<H>> recipes = new ArrayList();

    public ProcessingRecipeList(String name)
    {
        super(name);
    }

    public void add(ProcessingRecipe<H> recipe)
    {
        if (recipe != null)
        {
            recipes.add(recipe);
        }
    }

    public ProcessingRecipe<H> getMatchingRecipe(H machine)
    {
        for (ProcessingRecipe recipe : recipes)
        {
            if (recipe.matches(machine))
            {
                return recipe;
            }
        }
        return null;
    }

    public boolean isComponent(H machine, Fluid fluid)
    {
        return recipes.stream().anyMatch(r -> r.isComponent(machine, fluid));
    }

    public boolean isComponent(H machine, ItemStack stack)
    {
        return recipes.stream().anyMatch(r -> r.isComponent(machine, stack));
    }

    public boolean isInput(H machine, Fluid fluid)
    {
        return false;
    }
}
