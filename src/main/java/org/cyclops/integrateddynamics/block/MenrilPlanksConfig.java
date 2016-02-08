package org.cyclops.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import org.cyclops.cyclopscore.config.configurable.ConfigurableBlock;
import org.cyclops.cyclopscore.config.configurable.IConfigurable;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfig;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.Reference;

/**
 * Config for the Menril Planks.
 * @author rubensworks
 *
 */
public class MenrilPlanksConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static MenrilPlanksConfig _instance;

    /**
     * Make a new instance.
     */
    public MenrilPlanksConfig() {
        super(
                IntegratedDynamics._instance,
        	true,
            "menrilPlanks",
            null,
            null
        );
    }

    @Override
    protected IConfigurable initSubInstance() {
        return (ConfigurableBlock) new ConfigurableBlock(this, Material.wood).
                setHardness(2.0F).setStepSound(Block.soundTypeWood);
    }
    
    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_WOODPLANK;
    }
    
    @Override
    public void onRegistered() {
    	Blocks.fire.setFireInfo(getBlockInstance(), 5, 20);
    }
    
}