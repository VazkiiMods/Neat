package vazkii.neat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Neat.MOD_ID, name = Neat.MOD_NAME, version = Neat.VERSION, dependencies = Neat.DEPENDENCIES, clientSideOnly = true)
public class Neat {
	
	public static final String MOD_ID = "Neat";
	public static final String MOD_NAME = MOD_ID;
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;	
	public static final String DEPENDENCIES = "required-after:Forge@[12.16.0.1809,);";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		NeatConfig.init(event.getSuggestedConfigurationFile());
		
		MinecraftForge.EVENT_BUS.register(new HealthBarRenderer());
	}
}
