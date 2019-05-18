package test20190518;

import java.util.Map;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.BusinessKnowledgeModelNodeImpl;
import org.kie.dmn.core.compiler.BusinessKnowledgeModelCompiler;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.FunctionDefinition;

public class MyBKMCompiler extends BusinessKnowledgeModelCompiler {

    @Override
    public void compileEvaluator(DMNNode node, DMNCompilerImpl compiler, DMNCompilerContext ctx, DMNModelImpl model) {
        Map<QName, String> additionalAttributes = ((BusinessKnowledgeModelNode) node).getBusinessKnowledModel().getEncapsulatedLogic().getAdditionalAttributes();
        QName lookup = new QName("http://www.trisotech.com/2015/triso/modeling", "extra");
        if (additionalAttributes.containsKey(lookup)) {
            overrideEvaluator(node, compiler, ctx, model); // Only IFF extra xml attribute.
        } else {
            super.compileEvaluator(node, compiler, ctx, model);
        }
    }

    private void overrideEvaluator(DMNNode node, DMNCompilerImpl compiler, DMNCompilerContext ctx, DMNModelImpl model) {
        BusinessKnowledgeModelNodeImpl bkmi = (BusinessKnowledgeModelNodeImpl) node;
        compiler.linkRequirements(model, bkmi);

        ctx.enterFrame();
        try {
            for (DMNNode dep : bkmi.getDependencies().values()) {
                if (dep instanceof BusinessKnowledgeModelNode) {
                    ctx.setVariable(dep.getName(), ((BusinessKnowledgeModelNode) dep).getResultType());
                } else if (dep instanceof DecisionServiceNode) {
                    ctx.setVariable(dep.getName(), ((DecisionServiceNode) dep).getResultType());
                }
            }
            ctx.setVariable(bkmi.getName(), bkmi.getResultType());
            FunctionDefinition funcDef = bkmi.getBusinessKnowledModel().getEncapsulatedLogic();
            
            // Override the DMNEvaluatorCompiler:
            DMNExpressionEvaluator exprEvaluator = new MyExpressionEvaluator(compiler).compileExpression(ctx, model, bkmi, bkmi.getName(), funcDef);
            
            bkmi.setEvaluator(exprEvaluator);
        } finally {
            ctx.exitFrame();
        }
    }

}
