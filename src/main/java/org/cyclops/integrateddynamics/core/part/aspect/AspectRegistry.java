package org.cyclops.integrateddynamics.core.part.aspect;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.integrateddynamics.core.part.IPartType;

import java.util.*;

/**
 * Registry for {@link org.cyclops.integrateddynamics.core.part.aspect.IAspect}.
 * @author rubensworks
 */
public final class AspectRegistry implements IAspectRegistry {

    private static AspectRegistry INSTANCE = new AspectRegistry();

    private Map<IPartType, Set<IAspect>> partAspects = Maps.newHashMap();
    private Map<IPartType, Set<IAspectRead>> partReadAspects = Maps.newHashMap();
    private Map<IPartType, Set<IAspectWrite>> partWriteAspects = Maps.newHashMap();
    private Map<IPartType, List<IAspectRead>> partReadAspectsListTransform = Maps.newHashMap();
    private Map<IPartType, List<IAspectWrite>> partWriteAspectsListTransform = Maps.newHashMap();
    private Map<String, IAspect> unlocalizedAspects = Maps.newHashMap();
    private Map<String, IAspectRead> unlocalizedReadAspects = Maps.newHashMap();
    private Map<String, IAspectWrite> unlocalizedWriteAspects = Maps.newHashMap();
    @SideOnly(Side.CLIENT)
    private Map<IAspect, ModelResourceLocation> aspectModels = Maps.newHashMap();

    private AspectRegistry() {

    }

    /**
     * @return The unique instance.
     */
    public static AspectRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public IAspect register(IPartType partType, IAspect aspect) {
        registerSubAspectType(partType, aspect, partAspects, unlocalizedAspects);
        if(aspect instanceof IAspectRead) {
            registerSubAspectType(partType, (IAspectRead) aspect, partReadAspects, unlocalizedReadAspects);
            partReadAspectsListTransform.put(partType, Lists.newArrayList(partReadAspects.get(partType)));
        }
        if(aspect instanceof IAspectWrite) {
            registerSubAspectType(partType, (IAspectWrite) aspect, partWriteAspects, unlocalizedWriteAspects);
            partWriteAspectsListTransform.put(partType, Lists.newArrayList(partWriteAspects.get(partType)));
        }
        return aspect;
    }

    protected <T extends IAspect> void registerSubAspectType(IPartType partType, T aspect, Map<IPartType,
                                                             Set<T>> partAspects, Map<String, T> unlocalizedAspects) {
        Set<T> aspects = partAspects.get(partType);
        if(aspects == null) {
            aspects = Sets.newTreeSet(IAspect.AspectComparator.getInstance());
            partAspects.put(partType, aspects);
        }
        aspects.add(aspect);
        unlocalizedAspects.put(aspect.getUnlocalizedName(), aspect);
    }

    @Override
    public void register(IPartType partType, Set<IAspect> aspects) {
        for(IAspect aspect : aspects) {
            register(partType, aspect);
        }
    }

    @Override
    public Set<IAspect> getAspects(IPartType partType) {
        if(!partAspects.containsKey(partType)) {
            return Collections.emptySet();
        }
        return partAspects.get(partType);
    }

    @Override
    public List<IAspectRead> getReadAspects(IPartType partType) {
        return partReadAspectsListTransform.get(partType);
    }

    @Override
    public List<IAspectWrite> getWriteAspects(IPartType partType) {
        return partWriteAspectsListTransform.get(partType);
    }

    @Override
    public Set<IAspect> getAspects() {
        return ImmutableSet.copyOf(unlocalizedAspects.values());
    }

    @Override
    public Set<IAspectRead> getReadAspects() {
        return ImmutableSet.copyOf(unlocalizedReadAspects.values());
    }

    @Override
    public Set<IAspectWrite> getWriteAspects() {
        return ImmutableSet.copyOf(unlocalizedWriteAspects.values());
    }

    @Override
    public IAspect getAspect(String unlocalizedName) {
        return unlocalizedAspects.get(unlocalizedName);
    }

    @Override
    public ItemStack writeAspect(ItemStack baseItemStack, int partId, IAspect aspect) {
        ItemStack itemStack = baseItemStack.copy();
        NBTTagCompound tag = ItemStackHelpers.getSafeTagCompound(itemStack);
        tag.setInteger("partId", partId);
        tag.setString("aspectName", aspect.getUnlocalizedName());
        return itemStack;
    }

    @Override
    public Pair<Integer, IAspect> readAspect(ItemStack itemStack) {
        if(!itemStack.hasTagCompound()) {
            return null;
        }
        NBTTagCompound tag = ItemStackHelpers.getSafeTagCompound(itemStack);
        if(!tag.hasKey("partId", MinecraftHelpers.NBTTag_Types.NBTTagInt.ordinal())
                || !tag.hasKey("aspectName", MinecraftHelpers.NBTTag_Types.NBTTagString.ordinal())) {
            return null;
        }
        int pairId = tag.getInteger("partId");
        IAspect aspect = getAspect(tag.getString("aspectName"));
        if(aspect == null) {
            return null;
        }
        return Pair.of(pairId, aspect);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerAspectModel(IAspect aspect, ModelResourceLocation modelLocation) {
        aspectModels.put(aspect, modelLocation);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelResourceLocation getAspectModel(IAspect aspect) {
        return aspectModels.get(aspect);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Collection<ModelResourceLocation> getAspectModels() {
        return aspectModels.values();
    }

}
