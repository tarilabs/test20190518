
package test20190518;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicTest {

    public static final Logger LOG = LoggerFactory.getLogger(BasicTest.class);

    @Test
    public void test() {
        System.getProperties().forEach((x, y) -> System.out.println(x + " " + y));

        KieServices kieServices = KieServices.Factory.get();

        KieContainer kieContainer = kieServices.getKieClasspathContainer();

        DMNRuntime dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);

        String namespace = "http://www.trisotech.com/definitions/_0f5cb930-28ce-4a68-a76e-cef041832f4d";
        String modelName = "Drawing 1";

        DMNModel dmnModel = dmnRuntime.getModel(namespace, modelName);
        DMNContext emptyContext = dmnRuntime.newContext();
    
        DMNResult evaluateAll = dmnRuntime.evaluateAll(dmnModel, emptyContext);
        LOG.info("{}", evaluateAll);
    }

}

