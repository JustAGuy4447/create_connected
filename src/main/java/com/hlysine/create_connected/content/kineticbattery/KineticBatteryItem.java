package com.hlysine.create_connected.content.kineticbattery;


import com.hlysine.create_connected.ConnectedLang;
import com.hlysine.create_connected.registries.CCDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KineticBatteryItem extends BlockItem {

    public KineticBatteryItem(Block block, Properties builder) {
        super(block, builder);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        float maxBatteryLevel = (float)KineticBatteryBlockEntity.getMaxBatteryLevel() / 3600 / 20;
        return Math.round(13.0F * ((float)getBatteryLevel(stack) / maxBatteryLevel));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        float maxBatteryLevel = (float)KineticBatteryBlockEntity.getMaxBatteryLevel() / 3600 / 20;
        float f = Math.max(0.0F, (float) getBatteryLevel(stack) / maxBatteryLevel);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(tooltipFlag.isAdvanced()) {
            tooltipComponents.add(Component.empty());
            ConnectedLang.translate("battery.tooltip.charge")
                            .add(Component.literal(" "))
                            .add(ConnectedLang.number(getBatteryLevel(stack)))
                            .add(ConnectedLang.text(" / "))
                            .add(ConnectedLang.number(KineticBatteryBlockEntity.getMaxBatteryLevel() / 3600 / 20)
                                    .add(Component.literal(" "))
                                    .add(ConnectedLang.translate("generic.unit.su_hours")))
                    .addTo(tooltipComponents);
            ConnectedLang.translate("battery.tooltip.notice")
                    .style(ChatFormatting.GRAY)
                    .addTo(tooltipComponents);
        }
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pos, @NotNull Level world, Player player, @NotNull ItemStack stack, @NotNull BlockState state) {
        boolean ret = super.updateCustomBlockEntityTag(pos, world, player, stack, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof KineticBatteryBlockEntity batteryBE))
            return ret;
        batteryBE.setBatteryLevel(stack.getOrDefault(CCDataComponents.BATTERY_LEVEL, 0.0) * 3600 * 20);
        return true;
    }

    public static double getBatteryLevel(ItemStack stack) {
        return stack.getOrDefault(CCDataComponents.BATTERY_LEVEL, 0.0);
    }

}

