package test20190518;

import java.util.Collections;
import java.util.List;

import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.DRGElementCompiler;
import org.kie.dmn.feel.runtime.FEELFunction;

public class MyDMNProfile implements DMNProfile {

    @Override
    public List<DRGElementCompiler> getDRGElementCompilers() {
        return Collections.singletonList(new MyBKMCompiler());
    }

    @Override
    public List<FEELFunction> getFEELFunctions() {
        return Collections.emptyList();
    }

    @Override
    public List<DMNExtensionRegister> getExtensionRegisters() {
        return Collections.emptyList();
    }

}
