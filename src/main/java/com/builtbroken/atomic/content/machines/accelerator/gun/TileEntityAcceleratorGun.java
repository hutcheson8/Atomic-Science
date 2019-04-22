package com.builtbroken.atomic.content.machines.accelerator.gun;

import com.builtbroken.atomic.api.AtomicScienceAPI;
import com.builtbroken.atomic.api.accelerator.AcceleratorHelpers;
import com.builtbroken.atomic.api.accelerator.IAcceleratorTube;
import com.builtbroken.atomic.content.machines.accelerator.data.TubeConnectionType;
import com.builtbroken.atomic.content.machines.accelerator.graph.AcceleratorHandler;
import com.builtbroken.atomic.content.machines.accelerator.graph.AcceleratorNetwork;
import com.builtbroken.atomic.content.machines.accelerator.graph.AcceleratorNode;
import com.builtbroken.atomic.content.machines.accelerator.tube.AcceleratorTubeCap;
import com.builtbroken.atomic.content.machines.container.TileEntityItemContainer;
import com.builtbroken.atomic.content.machines.laser.emitter.TileEntityLaserEmitter;
import com.builtbroken.atomic.content.prefab.TileEntityMachine;
import com.builtbroken.atomic.lib.timer.TickTimerTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2018.
 */
public class TileEntityAcceleratorGun extends TileEntityMachine
{
    private AcceleratorNetwork network;

    private final AcceleratorNode acceleratorNode = new AcceleratorNode();
    private final IAcceleratorTube tubeCap = new AcceleratorTubeCap(() -> getPos(), () -> acceleratorNode);

    public TileEntityAcceleratorGun()
    {
        tickServer.add(TickTimerTileEntity.newConditional(20, (tick) -> validateNetwork(), () -> getNetwork() == null || getNetwork().nodes.isEmpty()));
    }

    @Override
    public void onLoad()
    {
        acceleratorNode.setData(getPos(), getDirection(), TubeConnectionType.START_CAP);
    }

    private void validateNetwork()
    {
        //If we have no network try to locate a tube with a network
        if (getNetwork() == null)
        {
            final EnumFacing facing = getDirection();

            final TileEntity tileEntity = world.getTileEntity(getPos().offset(facing));
            final IAcceleratorTube tube = AcceleratorHelpers.getAcceleratorTube(tileEntity, null);
            if (tube != null)
            {
                //Set network
                setNetwork(tube.getNode().getNetwork());

                //Connect to fake node
                tube.getNode().connect(acceleratorNode, getDirection().getOpposite());

                //If network null create
                if (getNetwork() == null)
                {
                    setNetwork(new AcceleratorNetwork());
                    getNetwork().connect(tube.getNode());
                }

                //Link to network
                if (getNetwork() != null)
                {
                    getNetwork().guns.add(this);
                    getNetwork().connect(acceleratorNode);
                }
            }
        }

        //Find tubes
        if (getNetwork() != null)
        {
            getNetwork().path(getWorld(), getPos().offset(getDirection()));
        }
    }

    /**
     * Called when laser fires through a container into the gun
     *
     * @param container    - container holding an item
     * @param laserEmitter - laser that fired, used to get starting energy
     */
    public void onLaserFiredInto(TileEntityItemContainer container, TileEntityLaserEmitter laserEmitter)
    {
        if(laserEmitter.getDirection() == getDirection())
        {
            final ItemStack heldItem = container.getHeldItem();
            if (!heldItem.isEmpty())
            {
                createParticle(heldItem, laserEmitter.boosterCount / container.consumeItems()); //TODO figure out how we are going to do energy
            }
        }
        else
        {
            //Explode
        }
    }

    /**
     * Creates a new particle at the gun tip fired into the system
     *
     * @param item          - item to use
     * @param energyToStart - energy to start with
     */
    public void createParticle(ItemStack item, int energyToStart)
    {
        AcceleratorHandler.newParticle(world, acceleratorNode, item, energyToStart);
    }

    /**
     * Called from remote system to trigger laser
     */
    public boolean fireLaser()
    {
        TileEntityLaserEmitter laser = getLaser();
        if (laser != null)
        {
            return laser.triggerFire();
        }
        return false;
    }

    /**
     * Gets the laser behind the gun tip
     *
     * @return
     */
    public TileEntityLaserEmitter getLaser()
    {
        final EnumFacing facing = getDirection().getOpposite();

        final TileEntity tileEntity = world.getTileEntity(getPos().offset(facing, 2));
        if (tileEntity instanceof TileEntityLaserEmitter)
        {
            TileEntityLaserEmitter laser = (TileEntityLaserEmitter) tileEntity;
            if (laser.getDirection() == getDirection())
            {
                return laser;
            }
        }
        return null;
    }

    public AcceleratorNetwork getNetwork()
    {
        return network;
    }

    public void setNetwork(AcceleratorNetwork network)
    {
        this.network = network;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == AtomicScienceAPI.ACCELERATOR_TUBE_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == AtomicScienceAPI.ACCELERATOR_TUBE_CAPABILITY)
        {
            return (T) tubeCap;
        }
        return super.getCapability(capability, facing);
    }
}
