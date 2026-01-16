package com.youtyan.apoex.mixin.mekanism.generator;

import com.youtyan.apoex.IApoExGenerator;
import mekanism.api.math.FloatingLong;
import mekanism.generators.common.tile.TileEntityGasGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "mekanism.generators.common.tile.TileEntityGasGenerator$FuelTank", remap = false)
public class MixinTileEntityGasGeneratorFuelTank {

    // 内部クラスから外部クラスへの参照はコンパイラによって自動生成されるフィールド
    // 通常は this$0 だが、Mixin ではシャドウイングが難しい場合がある
    // ここではリフレクションやアクセッサを使わずに、Mixin の機能で参照を取得する
    
    // しかし、内部クラスの Mixin で外部クラスのインスタンスを取得するのは一般的だが、
    // フィールド名が環境によって異なる可能性がある (this$0, field_xxxx など)
    
    // ここでは、Redirect の対象メソッド recheckOutput が TileEntityGasGenerator のメソッドを呼んでいるわけではないので、
    // 外部クラスのインスタンスへの参照が必要。
    
    // @Shadow(aliases = "this$0")
    // @Final
    // private TileEntityGasGenerator this$0;
    // これが機能しない場合、Mixin のターゲットクラスが正しく認識されていないか、フィールド名が違う。

    // 一旦、この Mixin を無効化して、TileEntityGasGenerator 側で対処できないか考える。
    // TileEntityGasGenerator の onUpdateServer で updateMaxOutputRaw を呼んでいる。
    // FuelTank.recheckOutput も updateMaxOutputRaw を呼んでいる。
    
    // updateMaxOutputRaw をフックすれば、両方に対応できるのでは？
    // updateMaxOutputRaw は TileEntityGenerator で定義されている。
    
    // MixinTileEntityGeneratorV2 で updateMaxOutputRaw をフックする。
    
    /*
    @Redirect(method = "recheckOutput", at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/gas/attribute/GasAttributes$Fuel;getEnergyPerTick()Lmekanism/api/math/FloatingLong;"))
    private FloatingLong apoex_getEnergyPerTick_FuelTank(mekanism.api.chemical.gas.attribute.GasAttributes.Fuel fuel) {
        // ...
    }
    */
}
