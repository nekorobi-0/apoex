package com.youtyan.apoex.mixin.mekanismgenerators.generator.multiblock;

import com.youtyan.apoex.IApoExMultiblock;
import mekanism.api.Action;
import mekanism.api.math.FloatingLong;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TurbineMultiblockData.class, remap = false)
public abstract class MixinTurbineMultiblockData {

    @Shadow public int lowerVolume;
    @Shadow public int vents;
    @Shadow public abstract int getDispersers();
    @Shadow public IExtendedFluidTank ventTank;

    // 燃料効率 (Fuel Efficiency) - 蒸気消費を減少
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/gas/IGasTank;shrinkStack(JLmekanism/api/Action;)J"))
    private long apoex_shrinkStack_redirect(mekanism.api.chemical.gas.IGasTank instance, long amount, Action action) {
        if (this instanceof IApoExMultiblock multiblock) {
            float efficiency = multiblock.getFuelEfficiency();
            if (efficiency > 0) {
                long actualConsumption = (long) (amount * (1.0F - efficiency));
                actualConsumption = Math.max(0, actualConsumption);
                instance.shrinkStack(actualConsumption, action);
                // 消費量を偽って、本来の処理量を返す (エネルギー計算には影響させないため)
                return amount;
            }
        }
        return instance.shrinkStack(amount, action);
    }

    // 発電量倍率 (Generation Multiplier) - エネルギー生成量を増加
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lmekanism/api/math/FloatingLong;multiply(J)Lmekanism/api/math/FloatingLong;"))
    private FloatingLong apoex_redirectEnergyMultiplierCalculation(FloatingLong instance, long value) {
        FloatingLong result = instance.multiply(value);
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getGenerationMultiplier();
            if (mult > 0) {
                return result.multiply(1.0F + mult);
            }
        }
        return result;
    }

    // 燃料容量 (Fuel Capacity) - 流量制限を緩和
    @ModifyVariable(method = "tick", at = @At(value = "STORE"), name = "proportion")
    private double apoex_capProportion(double proportion) {
        // Remove cap to allow infinite scaling based on stored steam
        return proportion;
    }

    // 発電量倍率 (Generation Multiplier) - 最大発電量表示
    @Inject(method = "getMaxProduction", at = @At("RETURN"), cancellable = true)
    private void apoex_getMaxProduction(CallbackInfoReturnable<FloatingLong> cir) {
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getGenerationMultiplier();
            if (mult > 0) {
                cir.setReturnValue(cir.getReturnValue().multiply(1.0F + mult));
            }
        }
    }
    
    // 燃料容量 (Fuel Capacity) - 最大流量
    @Inject(method = "getMaxFlowRate", at = @At("HEAD"), cancellable = true)
    private void apoex_getMaxFlowRate(CallbackInfoReturnable<Long> cir) {
        TurbineMultiblockData self = (TurbineMultiblockData) (Object) this;
        FloatingLong rate = FloatingLong.create(self.lowerVolume)
              .multiply(self.getDispersers())
              .multiply(MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get());
        rate = rate.min(FloatingLong.create(self.vents).multiply(MekanismGeneratorsConfig.generators.turbineVentGasFlow.get()));
        
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getFuelCapacityMultiplier();
            if (mult > 0) {
                rate = rate.multiply(1.0F + mult);
            }
        }
        
        cir.setReturnValue(rate.longValue());
    }
    
    // 燃料容量 (Fuel Capacity) - 蒸気タンク容量
    @Inject(method = "getSteamCapacity", at = @At("RETURN"), cancellable = true)
    private void apoex_getSteamCapacity(CallbackInfoReturnable<Long> cir) {
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getFuelCapacityMultiplier();
            if (mult > 0) {
                cir.setReturnValue((long) (cir.getReturnValue() * (1.0F + mult)));
            }
        }
    }
    
    // 発電量倍率 (Generation Multiplier) - 発電量表示
    @Inject(method = "getProductionRate", at = @At("RETURN"), cancellable = true)
    private void apoex_getProductionRate(CallbackInfoReturnable<FloatingLong> cir) {
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getGenerationMultiplier();
            FloatingLong rate = cir.getReturnValue();
            
            if (mult > 0) {
                rate = rate.multiply(1.0F + mult);
            }
            
            // Apply tick speed multiplier to display value to match actual production
            float tickSpeed = multiblock.getTickSpeedMultiplier();
            if (tickSpeed > 0) {
                rate = rate.multiply(1.0F + tickSpeed);
            }
            
            if (mult > 0 || tickSpeed > 0) {
                cir.setReturnValue(rate);
            }
        }
    }
    
    // エネルギー容量 (Energy Capacity) & 燃料容量 (Fuel Capacity) - エネルギー容量
    @Inject(method = "getEnergyCapacity", at = @At("RETURN"), cancellable = true)
    private void apoex_getEnergyCapacity(CallbackInfoReturnable<FloatingLong> cir) {
        if (this instanceof IApoExMultiblock multiblock) {
            float energyMult = multiblock.getEnergyCapacityMultiplier();
            float fuelMult = multiblock.getFuelCapacityMultiplier();
            
            FloatingLong capacity = cir.getReturnValue();
            
            if (energyMult > 0) {
                capacity = capacity.multiply(1.0F + energyMult);
            }
            if (fuelMult > 0) {
                capacity = capacity.multiply(1.0F + fuelMult);
            }
            
            if (energyMult > 0 || fuelMult > 0) {
                cir.setReturnValue(capacity);
            }
        }
    }
    
    // 燃料容量 (Fuel Capacity) - ディスパーサー数 (流量計算用)
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lmekanism/generators/common/content/turbine/TurbineMultiblockData;getDispersers()I"))
    private int apoex_getDispersers(TurbineMultiblockData instance) {
        int dispersers = instance.getDispersers();
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getFuelCapacityMultiplier();
            if (mult > 0) {
                return (int) (dispersers * (1.0F + mult));
            }
        }
        return dispersers;
    }
    
    // 燃料容量 (Fuel Capacity) - ベント数 (流量計算用)
    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lmekanism/generators/common/content/turbine/TurbineMultiblockData;vents:I"))
    private int apoex_getVents(TurbineMultiblockData instance) {
        int vents = instance.vents;
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getFuelCapacityMultiplier();
            if (mult > 0) {
                return (int) (vents * (1.0F + mult));
            }
        }
        return vents;
    }
    
    // 燃料容量 (Fuel Capacity) - 蒸気容量 (tick内計算用)
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lmekanism/generators/common/content/turbine/TurbineMultiblockData;getSteamCapacity()J"))
    private long apoex_getSteamCapacity_tick(TurbineMultiblockData instance) {
        long capacity = instance.getSteamCapacity();
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getFuelCapacityMultiplier();
            if (mult > 0) {
                return (long) (capacity * (1.0F + mult));
            }
        }
        return capacity;
    }

    // 発電量倍率 (Generation Multiplier) - 水生成量 (凝縮水)
    // @ModifyArg ではなく @Inject(shift = AFTER) を使用して競合を回避
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lmekanism/api/fluid/IExtendedFluidTank;setStack(Lnet/minecraftforge/fluids/FluidStack;)V", shift = At.Shift.AFTER))
    private void apoex_postSetVentTankStack(net.minecraft.world.level.Level world, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getGenerationMultiplier();
            if (mult > 0) {
                FluidStack current = this.ventTank.getFluid();
                if (!current.isEmpty()) {
                    FluidStack newStack = current.copy();
                    newStack.setAmount((int) (current.getAmount() * (1.0F + mult)));
                    this.ventTank.setStack(newStack);
                }
            }
        }
    }
}
