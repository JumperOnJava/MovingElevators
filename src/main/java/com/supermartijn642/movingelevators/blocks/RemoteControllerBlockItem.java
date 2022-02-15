package com.supermartijn642.movingelevators.blocks;

import com.supermartijn642.core.TextComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Created 5/5/2020 by SuperMartijn642
 */
public class RemoteControllerBlockItem extends BlockItem {

    public RemoteControllerBlockItem(Block blockIn, Properties builder){
        super(blockIn, builder);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
        ItemStack stack = player.getItemInHand(hand);
        if(stack.getItem() == this){
            if(player.isShiftKeyDown()){
                if(stack.hasTag() && stack.getTag().contains("controllerDim")){
                    if(!world.isClientSide){
                        stack.removeTagKey("controllerDim");
                        stack.removeTagKey("controllerX");
                        stack.removeTagKey("controllerY");
                        stack.removeTagKey("controllerZ");
                        player.displayClientMessage(TextComponents.translation("movingelevators.remote_controller.clear").get(), true);
                    }
                    return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
                }
            }else{
                if(!world.isClientSide){
                    if(stack.hasTag() && stack.getTag().contains("controllerDim")){
                        CompoundTag compound = stack.getTag();
                        Component x = TextComponents.number(compound.getInt("controllerX")).color(ChatFormatting.GOLD).get();
                        Component y = TextComponents.number(compound.getInt("controllerY")).color(ChatFormatting.GOLD).get();
                        Component z = TextComponents.number(compound.getInt("controllerZ")).color(ChatFormatting.GOLD).get();
                        Component dimension = TextComponents.dimension(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compound.getString("controllerDim")))).color(ChatFormatting.GOLD).get();
                        player.displayClientMessage(TextComponents.translation("movingelevators.remote_controller.tooltip.bound", x, y, z, dimension).get(), true);
                    }else
                        player.displayClientMessage(TextComponents.translation("movingelevators.remote_controller.tooltip").get(), true);
                }
                return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
            }
        }
        return super.use(world, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context){
        CompoundTag tag = context.getItemInHand().getTag();
        if(tag == null || !tag.contains("controllerDim")){
            Player player = context.getPlayer();
            if(player != null && !context.getPlayer().level.isClientSide)
                context.getPlayer().displayClientMessage(TextComponents.translation("movingelevators.remote_controller.not_bound").color(ChatFormatting.RED).get(), true);
            return InteractionResult.FAIL;
        }
        if(!tag.getString("controllerDim").equals(context.getLevel().dimension().location().toString())){
            Player player = context.getPlayer();
            if(player != null && !context.getPlayer().level.isClientSide)
                context.getPlayer().displayClientMessage(TextComponents.translation("movingelevators.remote_controller.wrong_dimension").color(ChatFormatting.RED).get(), true);
            return InteractionResult.FAIL;
        }
        return super.useOn(context);
    }
}
