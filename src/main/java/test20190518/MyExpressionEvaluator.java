package test20190518;

import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNCompilerHelper;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNEvaluatorCompiler;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.feel.runtime.functions.extended.CodeFunction;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.InformationItem;

public class MyExpressionEvaluator extends DMNEvaluatorCompiler {

    protected MyExpressionEvaluator(DMNCompilerImpl compiler) {
        super(compiler);
    }

    @Override
    public DMNExpressionEvaluator compileExpression(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, Expression expression) {
        if (expression instanceof FunctionDefinition) {
            FunctionDefinition funcDef = (FunctionDefinition) expression;

            // It's a BKM so the evaluation of the BKM result in a function, which other DRG element will invoke/call, standard:
            DMNFunctionDefinitionEvaluator func = new DMNFunctionDefinitionEvaluator(node.getName(), funcDef);
            for (InformationItem p : funcDef.getFormalParameter()) {
                DMNCompilerHelper.checkVariableName(model, p, p.getName());
                DMNType dmnType = compiler.resolveTypeRef(model, p, p, p.getTypeRef());
                func.addParameter(p.getName(), dmnType);
            }

            // Overriding the logic of the actual function behavior:
            DMNExpressionEvaluator dummyFunction = new DMNExpressionEvaluator() {
                private final CodeFunction quoting = new CodeFunction();
                @Override
                public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult result) {
                    StringBuilder overridden = new StringBuilder("OVERRIDDEN: you have called: ").append(node.getName())
                                                                                                 .append("( ")
                                                                                                 .append(funcDef.getFormalParameter()
                                                                                                                .stream()
                                                                                                                .map(InformationItem::getName)
                                                                                                                .map(result.getContext()::get)
                                                                                                                .map(quoting::invoke)
                                                                                                                .map(x -> x.getOrElse(""))
                                                                                                                .collect(Collectors.joining(", ")))
                                                                                                 .append(" )");
                    return new EvaluatorResultImpl(overridden, ResultType.SUCCESS);
                }
            };
            func.setEvaluator(dummyFunction);
            
            return func;
        } else {
            return super.compileExpression(ctx, model, node, exprName, expression);
        }
    }

}
