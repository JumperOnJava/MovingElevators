package com.supermartijn642.movingelevators.blocks;

import com.supermartijn642.movingelevators.MovingElevators;
import com.supermartijn642.movingelevators.elevator.ElevatorGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Created 5/5/2020 by SuperMartijn642
 */
public class RemoteControllerBlockEntity extends ElevatorInputBlockEntity {

    private Direction facing = Direction.NORTH;
    private BlockPos controllerPos = BlockPos.ZERO;
    private int groupCheckCounter = 0;
    private ElevatorGroup lastGroup;

    public RemoteControllerBlockEntity(BlockPos pos, BlockState state){
        super(MovingElevators.button_tile, pos, state);
    }

    @Override
    public void tick(){
        super.tick();
        this.groupCheckCounter++;
        if(this.groupCheckCounter == 40){
            ElevatorGroup group = this.getGroup();
            if(group != this.lastGroup){
                this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
                group.addComparatorListener(this.getFloorLevel(), this.worldPosition);
                this.lastGroup = group;
            }
        }
    }

    public void setValues(Direction facing, BlockPos controllerPos){
        this.facing = facing;
        this.controllerPos = controllerPos;
        this.dataChanged();
    }

    @Override
    protected CompoundTag writeData(){
        CompoundTag compound = super.writeData();
        compound.putInt("facing", this.facing.get3DDataValue());
        compound.putInt("controllerX", this.controllerPos.getX());
        compound.putInt("controllerY", this.controllerPos.getY());
        compound.putInt("controllerZ", this.controllerPos.getZ());
        return compound;
    }

    @Override
    protected void readData(CompoundTag compound){
        super.readData(compound);
        this.facing = Direction.from3DDataValue(compound.getInt("facing"));
        this.controllerPos = new BlockPos(compound.getInt("controllerX"), compound.getInt("controllerY"), compound.getInt("controllerZ"));
    }

    @Override
    public Direction getFacing(){
        return this.facing;
    }

    public ControllerBlockEntity getController(){
        if(this.level == null || this.controllerPos == null)
            return null;
        BlockEntity tile = this.level.getBlockEntity(this.controllerPos);
        return tile instanceof ControllerBlockEntity ? (ControllerBlockEntity)tile : null;
    }

    @Override
    public boolean hasGroup(){
        ControllerBlockEntity controller = this.getController();
        return controller != null && controller.hasGroup();
    }

    @Override
    public ElevatorGroup getGroup(){
        ControllerBlockEntity controller = this.getController();
        return controller == null ? null : controller.getGroup();
    }

    @Override
    public String getFloorName(){
        ControllerBlockEntity controller = this.getController();
        return controller == null ? null : controller.getFloorName();
    }

    @Override
    public DyeColor getDisplayLabelColor(){
        ControllerBlockEntity controller = this.getController();
        return controller == null ? null : controller.getDisplayLabelColor();
    }

    @Override
    public int getFloorLevel(){
        return this.controllerPos.getY();
    }

    public BlockPos getControllerPos(){
        return this.controllerPos;
    }

    @Override
    public void setRemoved(){
        ElevatorGroup group = this.getGroup();
        if(group != null)
            group.removeComparatorListener(this.worldPosition);
        super.setRemoved();
    }
}
