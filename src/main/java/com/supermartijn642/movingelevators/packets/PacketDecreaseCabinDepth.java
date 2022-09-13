package com.supermartijn642.movingelevators.packets;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.movingelevators.blocks.ControllerBlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created 4/3/2020 by SuperMartijn642
 */
public class PacketDecreaseCabinDepth extends BlockEntityBasePacket<ControllerBlockEntity> {

    public PacketDecreaseCabinDepth(BlockPos pos){
        super(pos);
    }

    public PacketDecreaseCabinDepth(){
    }

    @Override
    protected void handle(ControllerBlockEntity elevatorEntity, PacketContext packetContext){
        elevatorEntity.getGroup().decreaseCageDepth();
    }
}
