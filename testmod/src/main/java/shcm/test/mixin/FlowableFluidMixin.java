package shcm.test.mixin;

import io.shcm.shsupercm.fabric.fletchingtable.api.InterfaceInjection;
import net.minecraft.fluid.FlowableFluid;
import org.spongepowered.asm.mixin.Mixin;
import shcm.test.Nah;
import shcm.test.TestShit;
import shcm.test.Yes;

@InterfaceInjection(false)
@Mixin(FlowableFluid.class)
public class FlowableFluidMixin implements TestShit, Yes, Nah {
    @Override
    public String getCoolTestShit() {
        return "What.";
    }
}
