package it.gov.pagopa.mocker.service.scripting;

import it.gov.pagopa.mocker.entity.ScriptEntity;
import it.gov.pagopa.mocker.exception.MockerInitializationException;
import it.gov.pagopa.mocker.exception.MockerScriptExecutionException;
import it.gov.pagopa.mocker.repository.ScriptRepository;
import it.gov.pagopa.mocker.util.Constants;
import it.gov.pagopa.mocker.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.script.*;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ScriptExecutor {

    private ScriptEngine engine;

    private ScriptRepository scriptRepository;

    public ScriptExecutor(ScriptRepository scriptRepository) throws MockerInitializationException {
        ScriptEngineManager manager = new ScriptEngineManager();
        System.setProperty("nashorn.args", "--language=es6");
        this.engine = manager.getEngineByName("nashorn");
        this.scriptRepository = scriptRepository;
        initializeEngine();
    }

    private void initializeEngine() throws MockerInitializationException {

        List<ScriptEntity> scriptEntities = this.scriptRepository.findAll().stream()
                .filter(entity -> entity.getCode() != null)
                .toList();

        List<ScriptEntity> libFunctions = scriptEntities.stream()
                .filter(e -> Boolean.FALSE.equals(e.getSelectable()))
                .toList();
        for (ScriptEntity scriptEntity : libFunctions) {
            loadScript(scriptEntity);
        }

        List<ScriptEntity> customFunctions = scriptEntities.stream()
                .filter(e -> !libFunctions.contains(e))
                .toList();
        for (ScriptEntity scriptEntity : customFunctions) {
            loadScript(scriptEntity);
        }
    }

    private void loadScript(ScriptEntity scriptEntity) throws MockerInitializationException {
        try {
            String scriptCode = Utility.decodeBase64(scriptEntity.getCode());
            if (Boolean.TRUE.equals(scriptEntity.getSelectable())) {
                String scriptName = scriptEntity.getName();
                scriptCode = scriptCode.replace("function execute(", "function " + scriptName + Constants.SCRIPTEXECUTOR_FUNCTION_SUFFIX + "(");
            }
            engine.eval(scriptCode);
        } catch (ScriptException e) {
            log.error("An error occurred while initializing ScriptExecutor. ", e);
            throw new MockerInitializationException(e);
        }
    }

    @SuppressWarnings({"unchecked"})
    public Map<String, String> execute(String scriptName, Map<String, Object> parameters) throws MockerScriptExecutionException {
        Map<String, String> result;
        try {
            Bindings bindings = new SimpleBindings();
            bindings.putAll(parameters);
            result = (Map<String, String>) ((Invocable) engine).invokeFunction(scriptName + Constants.SCRIPTEXECUTOR_FUNCTION_SUFFIX, bindings);
        } catch (ScriptException | NoSuchMethodException e) {
            throw new MockerScriptExecutionException(e, scriptName);
        }
        return result == null ? Map.of() : result;
    }
}
