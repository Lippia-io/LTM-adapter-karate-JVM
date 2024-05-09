package ltm;

import com.intuit.karate.RuntimeHook;

import com.intuit.karate.StringUtils;
import com.intuit.karate.core.FeatureRuntime;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.core.Step;
import com.intuit.karate.core.StepResult;

import ltm.models.run.request.TestDTO;
import ltm.screenshots.SSConfig;

import ltm.models.run.request.StepDTO;
import ltm.models.run.response.RunDTO;
import ltm.screenshots.Strategy;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

public abstract class TestManagerAPIAdapter implements RuntimeHook {
    private static final RunDTO runResponseDTO;
    private final ThreadLocal<URI> currentFeatureFile = new ThreadLocal<>();
    private static final SSConfig screenshotConfig;
    private static final ThreadLocal<List<StepDTO>> steps = new ThreadLocal<>();

    static {
        screenshotConfig = SSConfig.load();
        runResponseDTO = TestManagerAPIClient.createRun();
    }

    @Override
    public void afterScenario(ScenarioRuntime sr) {
        String title = sr.scenario.getName();
        String status = sr.result.isFailed() ? "failed" : "passed";
        status = status.toUpperCase().substring(0, status.length() - 2);
        String feature = sr.featureRuntime.feature.getName();
        TestDTO test = new TestDTO(title, runResponseDTO.getId(), status, feature, "SCENARIO", sr.tags.getTags(), steps.get());
        TestManagerAPIClient.createTest(test);
        cleanSteps();
    }

    @Override
    public void afterFeature(FeatureRuntime fr) {
    }

    @Override
    public void afterStep(StepResult result, ScenarioRuntime sr) {
        String stepText = getStepText(result.getStep());
        String status = result.getResult().getStatus();
        status = status.toUpperCase().substring(0, status.length() - 2);
        String base64Image = null;
        String stackTrace = null;

        if (steps.get() == null) {
            steps.set(new LinkedList<>());
        }

        if (screenshotConfig.contains(Strategy.ON_EACH_STEP)) {
            base64Image = this.getBase64Image();
        } else if (screenshotConfig.contains(Strategy.ON_FAILURE) && status.equalsIgnoreCase("failed")) {
            base64Image = this.getBase64Image();
            stackTrace = truncate(result.getResult().getError().getMessage(), 5);
        }

        steps.get().add(new StepDTO(stepText, stackTrace, base64Image, status));
    }

    private String getStepText(Step step) {
        StringBuilder stepTextBuilder = new StringBuilder();
        stepTextBuilder.append(step.getPrefix());
        stepTextBuilder.append(" ");
        stepTextBuilder.append(step.getText());

        if (!StringUtils.isBlank(step.getDocString())) {
            stepTextBuilder.append(step.getDocString());
        }

        return stepTextBuilder.toString();
    }

    private void cleanSteps() {
        if (steps.get() != null) {
            steps.remove();
        }
    }

    public static synchronized String truncate(String str, int length) {
        if (str.length() <= length) {
            return str.substring(0, length);
        }

        return str;
    }

    public abstract String getBase64Image();
}