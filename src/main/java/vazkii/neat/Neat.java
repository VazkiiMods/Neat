package vazkii.neat;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = Neat.MOD_ID, name = Neat.MOD_NAME, version = Neat.VERSION)
public class Neat {
	
	public static final String MOD_ID = "neat";
	public static final String MOD_NAME = "Neat";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;	

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new HealthBarRenderer());
	}
}
