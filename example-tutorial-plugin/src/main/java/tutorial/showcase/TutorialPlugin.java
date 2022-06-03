package tutorial.showcase;

import com.ericlam.mc.eld.*;

@ELDBukkit(
        lifeCycle = TutorialLifeCycle.class,
        registry = TutorialRegistry.class
)
public class TutorialPlugin extends ELDBukkitPlugin {

    @Override
    public void bindServices(ServiceCollection serviceCollection) {
        serviceCollection.bindService(ExampleService.class, ExampleServiceImpl.class);

        AddonInstallation addonManager = serviceCollection.getInstallation(AddonInstallation.class);

        MyExampleInstallationImpl installation = new MyExampleInstallationImpl();
        // 添加安裝器
        addonManager.customInstallation(MyExampleInstallation.class, installation);
        // 安裝 guice module
        addonManager.installModule(new MyExampleModule(installation));
    }

    @Override
    protected void manageProvider(BukkitManagerProvider bukkitManagerProvider) {

    }
}
