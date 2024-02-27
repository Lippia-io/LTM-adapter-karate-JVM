package ltm.screenshots;

import ltm.PropertyManager;

public class SSConfig {
    protected SSConfig(Strategy strategy) {
        this.strategy = strategy;
    }

    public static SSConfig load() {
        Strategy strategy = Strategy.DISABLED;
        if (PropertyManager.isPropertiesFilePresent()) {
            String strategyStr = PropertyManager.getProperty("test-manager.ltm.screenshots");
            strategy = Strategy.getValue(strategyStr);
        }

        return new SSConfig(strategy);
    }

    public boolean contains(Strategy strategy) {
        return this.strategy == strategy;
    }

    public boolean isNotContains(Strategy strategy) {
        return !(contains(strategy));
    }

    private final Strategy strategy;
}