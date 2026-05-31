package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class CCDataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateConnected.MODID);

    public static final DataComponentType<Double> BATTERY_LEVEL = register(
            "battery_level",
            builder -> builder.persistent(strictDoubleRange(0.0, KineticBatteryBlockEntity.getMaxBatteryLevel() / 3600 / 20, 2)).networkSynchronized(ByteBufCodecs.DOUBLE)
    );

    private static Codec<Double> strictDoubleRange(final double minInclusive, final double maxInclusive, final double maxDecimalPlaces) {
        return Codec.DOUBLE.validate(value -> {
            double multipliedNum = value * Math.pow(10, maxDecimalPlaces);
            return (!(Math.abs(multipliedNum - Math.round(multipliedNum)) > 1e-9)) ?
                    Codec.checkRange(minInclusive, maxInclusive).apply(value) :
                    DataResult.error(() -> "Value " + value + " has too many decimal places, maximum is " + maxDecimalPlaces);
        });
    }


    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
