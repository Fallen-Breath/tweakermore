package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signMultilinePasteSupport;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.signMultilinePasteSupport.SelectionManagerInSignEditScreen;
import me.fallenbreath.tweakermore.impl.mc_tweaks.signMultilinePasteSupport.SignEditScreenRowIndexController;
import net.minecraft.SharedConstants;
import net.minecraft.client.util.SelectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(SelectionManager.class)
public abstract class SelectionManagerMixin implements SelectionManagerInSignEditScreen
{
	@Shadow
	//#if MC >= 11600
	//$$ public
	//#else
	protected
	//#endif
	abstract void insert(String string);

	@Shadow public abstract void moveCaretToEnd();

	private SignEditScreenRowIndexController signEditScreen$TKM;
	private String pastingString$TKM;

	@Override
	public void setSignEditScreen$TKM(SignEditScreenRowIndexController signEditScreen)
	{
		this.signEditScreen$TKM = signEditScreen;
	}

	@ModifyArg(
			//#if MC >= 11600
			//$$ method = "paste",
			//$$ at = @At(
			//$$ 		value = "INVOKE",
			//$$ 		target = "Lnet/minecraft/client/util/SelectionManager;insert(Ljava/lang/String;Ljava/lang/String;)V"
			//$$ ),
			//$$ index = 1
			//#else
			method = "handleSpecialKey",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/Formatting;strip(Ljava/lang/String;)Ljava/lang/String;"
			)
			//#endif
	)
	private String signMultilinePasteSupport_storeRawString(String string)
	{
		if (TweakerMoreConfigs.SIGN_MULTILINE_PASTE_SUPPORT.getBooleanValue())
		{
			this.pastingString$TKM = string;
		}
		return string;
	}

	@Inject(
			//#if MC >= 11600
			//$$ method = "insert(Ljava/lang/String;Ljava/lang/String;)V",
			//#else
			method = "insert(Ljava/lang/String;)V",
			//#endif
			at = @At("HEAD"),
			cancellable = true
	)
	private void signMultilinePasteSupport_impl(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.SIGN_MULTILINE_PASTE_SUPPORT.getBooleanValue())
		{
			String string = this.pastingString$TKM;
			this.pastingString$TKM = null;

			if (string == null || string.indexOf('\n') == -1 || this.signEditScreen$TKM == null)
			{
				return;
			}

			Consumer<String> inserter = line -> this.insert(
					//#if MC >= 11600
					//$$ line
					//#else
					SharedConstants.stripInvalidChars(line)
					//#endif
			);
			String[] lines = string.split("\\n");
			for (int i = 0; i < lines.length; i++)
			{
				//#if MC >= 11600
				//$$ // MC 1.16+ doesn't do SharedConstants.stripInvalidChars for the pasting string
				//$$ // So we need to manually removed the \r character
				//$$ lines[i] = lines[i].replaceAll("\\r", "");
				//#endif

				if (this.signEditScreen$TKM.canAddCurrentRowIndex(1))
				{
					inserter.accept(lines[i]);
					this.signEditScreen$TKM.addCurrentRowIndex(1);
					this.moveCaretToEnd();
				}
				else
				{
					StringBuilder rest = new StringBuilder();
					for (int j = i; j < lines.length; j++)
					{
						rest.append(lines[j]);
					}
					inserter.accept(rest.toString());
					break;
				}
			}
			ci.cancel();
		}
	}
}
