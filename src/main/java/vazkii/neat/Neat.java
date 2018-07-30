package vazkii.neat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Neat.MOD_ID, name = Neat.MOD_NAME, version = Neat.VERSION, dependencies = Neat.DEPENDENCIES, guiFactory = Neat.GUI_FACTORY, clientSideOnly = true)
public class Neat {
	
	public static final String MOD_ID = "neat";
	public static final String MOD_NAME = "Neat";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;	
	public static final String DEPENDENCIES = "";
	public static final String GUI_FACTORY = "vazkii.neat.GuiFactory";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		NeatConfig.init(event.getSuggestedConfigurationFile());
		
		MinecraftForge.EVENT_BUS.register(new ToggleKeybind());
		MinecraftForge.EVENT_BUS.register(new HealthBarRenderer());
	}
}
