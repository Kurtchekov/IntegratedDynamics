package org.cyclops.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import org.cyclops.cyclopscore.config.configurable.ConfigurableBlock;
import org.cyclops.cyclopscore.config.configurable.IConfigurable;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfig;
import org.cyclops.integrateddynamics.IntegratedDynamics;

/**
 * Config for the Crystalized Menril block.
 * @author rubensworks
 *
 */
public class CrystalizedMenrilBlockConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static CrystalizedMenrilBlockConfig _instance;

    /**
     * Make a new instance.
     */
    public CrystalizedMenrilBlockConfig() {
        super(
                IntegratedDynamics._instance,
                true,
                "crystalizedMenrilBlock",
                null,
                null
        );
    }

    @Override
    protected IConfigurable initSubInstance() {
        return (ConfigurableBlock) new ConfigurableBlock(this, Material.craftedSnow)
        .setHardness(1.5F).setStepSound(Block.soundTypeSnow);
    }
    
}