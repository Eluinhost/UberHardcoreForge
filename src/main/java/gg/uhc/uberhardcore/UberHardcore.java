package gg.uhc.uberhardcore;

import gg.uhc.uberhardcore.tasks.DefaultTaskScheduler;
import gg.uhc.uberhardcore.tasks.TaskScheduler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = UberHardcore.MODID, version = UberHardcore.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class UberHardcore {
    public static final String MODID = "uberhardcore";
    public static final String VERSION = "0.0.1";

    @Mod.Instance
    public static UberHardcore INSTANCE;

    public static boolean inDebug = false;

    protected TaskScheduler scheduler;

    // TODO zombies riding chickens is going to be broken
    // TODO skeletons riding spiders is going to be broken

    public TaskScheduler getScheduler() {
        return scheduler;
    }

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        Configuration configuration = new Configuration(event.getSuggestedConfigurationFile());
        configuration.load();

        // set up scheduler
        scheduler = new DefaultTaskScheduler();
        FMLCommonHandler.instance().bus().register(scheduler);

        MobOverride.runMobOverrides(configuration);

        configuration.save();
    }


    @EventHandler
    public void init(FMLInitializationEvent event) {
        // register all of the required handlers for events
        for (MobOverride mobOverride : MobOverride.values()) {
            for (Object hander : mobOverride.handlers) {
                MinecraftForge.EVENT_BUS.register(hander);
            }
        }
    }
}


