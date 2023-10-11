package ltm;

import com.intuit.karate.RuntimeHook;
import com.intuit.karate.Suite;

import com.intuit.karate.core.FeatureRuntime;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.core.Step;
import com.intuit.karate.core.StepResult;

import com.intuit.karate.http.HttpRequest;
import com.intuit.karate.http.Response;

import ltm.screenshots.SSConfig;

import ltm.models.run.response.RunDTO;

public abstract class TestManagerAPIAdapter implements RuntimeHook {
    private static final RunDTO runResponseDTO;
    private static final SSConfig screenshotConfig;

    static {
        screenshotConfig = SSConfig.load();
        runResponseDTO = TestManagerAPIClient.createRun();
    }

    @Override
    public boolean beforeScenario(ScenarioRuntime sr) {
        return true;
    }

    @Override
    public void afterScenario(ScenarioRuntime sr) {
    }

    @Override
    public boolean beforeFeature(FeatureRuntime fr) {
        return true;
    }

    @Override
    public void afterFeature(FeatureRuntime fr) {
    }

    @Override
    public void beforeSuite(Suite suite) {
    }

    @Override
    public void afterSuite(Suite suite) {
    }

    @Override
    public boolean beforeStep(Step step, ScenarioRuntime sr) {
        return true;
    }

    @Override
    public void afterStep(StepResult result, ScenarioRuntime sr) {
    }

    @Override
    public void beforeHttpCall(HttpRequest request, ScenarioRuntime sr) {
    }

    @Override
    public void afterHttpCall(HttpRequest request, Response response, ScenarioRuntime sr) {
    }

    public static synchronized String truncate(String str, int length) {
        if (str.length() <= length) {
            return str.substring(0, length);
        }

        return str;
    }

    public abstract String getBase64Image();
}