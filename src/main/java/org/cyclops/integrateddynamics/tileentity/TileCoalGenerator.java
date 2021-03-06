package org.cyclops.integrateddynamics.tileentity;

import cofh.api.energy.IEnergyConnection;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.persist.nbt.NBTPersist;
import org.cyclops.integrateddynamics.Reference;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.block.BlockCoalGenerator;
import org.cyclops.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import org.cyclops.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import org.cyclops.integrateddynamics.core.helper.EnergyHelpers;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.tileentity.TileCableConnectableInventory;
import org.cyclops.integrateddynamics.network.CoalGeneratorNetworkElement;

/**
 * A part entity for the coal energy generator.
 * @author rubensworks
 */
@Optional.Interface(iface = "cofh.api.energy.IEnergyConnection", modid = Reference.MOD_RF_API, striprefs = true)
public class TileCoalGenerator extends TileCableConnectableInventory implements IEnergyConnection, IEnergyStorage {

    public static final int MAX_PROGRESS = 13;
    public static final int ENERGY_PER_TICK = 20;
    public static final int SLOT_FUEL = 0;

    @NBTPersist
    private int currentlyBurningMax;
    @NBTPersist
    private int currentlyBurning;

    public TileCoalGenerator() {
        super(1, "fuel", 64);
        addCapabilityInternal(NetworkElementProviderConfig.CAPABILITY, new NetworkElementProviderSingleton() {
            @Override
            public INetworkElement createNetworkElement(World world, BlockPos blockPos) {
                return new CoalGeneratorNetworkElement(DimPos.of(world, blockPos));
            }
        });
        addCapabilityInternal(CapabilityEnergy.ENERGY, this);
    }

    public IEnergyNetwork getEnergyNetwork() {
        return NetworkHelpers.getEnergyNetwork(getNetwork());
    }

    public void updateBlockState() {
        getWorld().setBlockState(getPos(), getWorld().getBlockState(getPos()).withProperty(BlockCoalGenerator.ON, isBurning()));
    }

    public int getProgress() {
        float current = currentlyBurning;
        float max = currentlyBurningMax;
        if (max == 0) {
            return -1;
        }
        return Math.round((current / max) * (float) MAX_PROGRESS);
    }

    public boolean isBurning() {
        return currentlyBurning < currentlyBurningMax;
    }

    public boolean canAddEnergy(int energy) {
        IEnergyNetwork network = getEnergyNetwork();
        if(network != null && network.receiveEnergy(energy, true) == energy) {
            return true;
        }
        return addEnergyFe(energy, true) == energy;
    }

    protected int addEnergy(int energy) {
        IEnergyNetwork network = getEnergyNetwork();
        int toFill = energy;
        if(network != null) {
            toFill -= network.receiveEnergy(toFill, false);
        }
        if(toFill > 0) {
            toFill -= addEnergyFe(toFill, false);
        }
        return energy - toFill;
    }

    protected int addEnergyFe(int energy, boolean simulate) {
        return EnergyHelpers.fillNeigbours(getWorld(), getPos(), energy, simulate);
    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        if((getStackInSlot(SLOT_FUEL) != null || isBurning()) && canAddEnergy(ENERGY_PER_TICK)) {
            if (isBurning()) {
                if (currentlyBurning++ >= currentlyBurningMax) {
                    currentlyBurning = 0;
                    currentlyBurningMax = 0;
                    sendUpdate();
                }
                int toFill = ENERGY_PER_TICK;
                addEnergy(toFill);
                markDirty();
            }
            if (!isBurning()) {
                ItemStack fuel;
                if ((fuel = decrStackSize(SLOT_FUEL, 1)) != null && TileEntityFurnace.isItemFuel(fuel)) {
                    if(getStackInSlot(SLOT_FUEL) == null) {
                        setInventorySlotContents(SLOT_FUEL, fuel.getItem().getContainerItem(fuel));
                    }
                    currentlyBurningMax = TileEntityFurnace.getItemBurnTime(fuel);
                    currentlyBurning = 0;
                    sendUpdate();
                    updateBlockState();
                }
            }
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    /*
     * ------------------ RF API ------------------
     */

    @Optional.Method(modid = Reference.MOD_RF_API)
    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }
}
