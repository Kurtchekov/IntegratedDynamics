package org.cyclops.integrateddynamics.world.biome;

import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cyclops.cyclopscore.config.configurable.ConfigurableBiome;
import org.cyclops.cyclopscore.config.extendedconfig.BiomeConfig;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.integrateddynamics.GeneralConfig;

import java.util.Random;

/**
 * Meneglin biome.
 * @author rubensworks
 *
 */
public class BiomeMeneglin extends ConfigurableBiome {

    private static final BlockFlower.EnumFlowerType[] FLOWERS = new BlockFlower.EnumFlowerType[]{
            BlockFlower.EnumFlowerType.BLUE_ORCHID,
            BlockFlower.EnumFlowerType.OXEYE_DAISY,
            BlockFlower.EnumFlowerType.WHITE_TULIP
    };
    
    private static BiomeMeneglin _instance = null;
    
    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static BiomeMeneglin getInstance() {
        return _instance;
    }

    public BiomeMeneglin(ExtendedConfig<BiomeConfig> eConfig) {
        super(constructProperties(eConfig.downCast()).setBaseHeight(0.4F)
                .setHeightVariation(0.4F).setTemperature(0.75F)
                .setRainfall(0.25F).setWaterColor(Helpers.RGBToInt(85, 221, 168)), eConfig.downCast());

        this.theBiomeDecorator.treesPerChunk = 3;
        this.theBiomeDecorator.flowersPerChunk = 70;
        MinecraftForge.TERRAIN_GEN_BUS.register(this);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public int getGrassColorAtPos(BlockPos blockPos) {
        return Helpers.RGBToInt(85, 221, 168);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getFoliageColorAtPos(BlockPos blockPos) {
        return Helpers.RGBToInt(128, 208, 185);
    }

    @Override
    public int getSkyColorByTemp(float temp) {
        return Helpers.RGBToInt(178, 238, 233);
    }

    @Override
    public BiomeDecorator createBiomeDecorator() {
        return getModdedBiomeDecorator(new MeneglinBiomeDecorator());
    }

    @Override
    public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
        return FLOWERS[rand.nextInt(FLOWERS.length)];
    }

    @Override
    public void addDefaultFlowers() {
        for(BlockFlower.EnumFlowerType flower : FLOWERS) {
            addFlower(Blocks.RED_FLOWER.getDefaultState().withProperty(Blocks.RED_FLOWER.getTypeProperty(), flower), 20);
        }
    }

    @SubscribeEvent
    public void onDecorate(DecorateBiomeEvent.Decorate decorateBiomeEvent) {
        if(decorateBiomeEvent.getType() == DecorateBiomeEvent.Decorate.EventType.TREE) {
            if(GeneralConfig.wildMenrilTreeChance > 0 && decorateBiomeEvent.getRand().nextInt(GeneralConfig.wildMenrilTreeChance) == 0) {
                int k6 = decorateBiomeEvent.getRand().nextInt(16) + 8;
                int l = decorateBiomeEvent.getRand().nextInt(16) + 8;
                MeneglinBiomeDecorator.MENRIL_TREE_GEN.setDecorationDefaults();
                BlockPos blockpos = decorateBiomeEvent.getWorld().getHeight(decorateBiomeEvent.getPos().add(k6, 0, l));
                MeneglinBiomeDecorator.MENRIL_TREE_GEN.growTree(decorateBiomeEvent.getWorld(), decorateBiomeEvent.getRand(), blockpos);
            }
        }
    }
}
