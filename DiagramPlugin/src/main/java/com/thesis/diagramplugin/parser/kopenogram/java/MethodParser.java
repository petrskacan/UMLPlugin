package com.thesis.diagramplugin.parser.kopenogram.java;

import com.intellij.psi.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;
import static com.thesis.diagramplugin.utils.DiagramConstants.XML_FILE_NAME_POSTFIX_KOPENOGRAM;

public class MethodParser {

    public static File parse(PsiMethod method) {
//        String className = method.getContainingClass().getName();
        String classFileName = method.getContainingClass().getContainingFile().getName();
        String dir = method.getContainingFile().getContainingDirectory().getVirtualFile().getPath();
//        String pathToClassFile = dir + "/" + classFileName;
        String methodName = method.getName();

        Document document = DocumentHelper.createDocument();
        Element methodElement = document.addElement(METHOD_TAG);
        methodElement.addAttribute(NAME_ATTRIBUTE, methodName);
        methodElement.addAttribute(RETURN_TYPE, Optional.ofNullable(method.getReturnType()).map(PsiType::getCanonicalText).orElseGet(()->"null"));

        parseMethodParameters(method, methodElement);
        parseMethodBody(method, methodElement);

        String fileName = dir + "/" + methodName + XML_FILE_NAME_POSTFIX_KOPENOGRAM;
        File file = new File(fileName);
        try {
            FileOutputStream fileOS = new FileOutputStream(file);
            XMLWriter writer = new XMLWriter(fileOS, OutputFormat.createPrettyPrint());
            writer.write(document);
            writer.flush();
            fileOS.close();
        } catch (IOException e) {
            System.out.println("IO ERROR during creation of " + fileName);
        }

        return file;
    }

    private static void parseMethodParameters(PsiMethod method, Element methodElement) {
        Element parametersElement = methodElement.addElement(PARAMETERS_TAG);
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            Element parameterElement = parametersElement.addElement(PARAMETER_TAG);
            parameterElement.addAttribute(FIELD_NAME_ATTRIBUTE, parameter.getName());
            parameterElement.addAttribute(FIELD_TYPE_ATTRIBUTE, parameter.getType().getCanonicalText());
        }
    }
    private static void parseMethodBody(PsiMethod method, Element methodElement) {
        Element bodyElement = methodElement.addElement(BODY_TAG);
        PsiCodeBlock methodBody = method.getBody();
        if (methodBody != null) {
            PsiStatement[] statements = methodBody.getStatements();
            for (PsiStatement statement : statements) {
                parseStatement(statement, bodyElement);
            }
        }
    }

    private static void parseIfCondition(PsiIfStatement ifStatement, Element parent) {
        Element ifElement = parent.addElement(IF_TAG);
        if (ifStatement.getCondition() != null) {
            ifElement.addAttribute(CONDITION_ATTRIBUTE, ifStatement.getCondition().getText());
        }
        Element thenElement = ifElement.addElement(THEN_TAG);
        if (ifStatement.getThenBranch() != null) {
            parseStatement(ifStatement.getThenBranch(), thenElement);
        }
        if (ifStatement.getElseBranch() != null) {
            Element elseElement = ifElement.addElement(ELSE_TAG);
            parseStatement(ifStatement.getElseBranch(), elseElement);
        }
    }

    private static void parseContinue(PsiContinueStatement continueStatement, Element parent) {
        Element continueElement = parent.addElement(CONTINUE_TAG);
        if (continueStatement.getLabelIdentifier() != null) {
            continueElement.addAttribute(LABEL_ATTRIBUTE, continueStatement.getLabelIdentifier().getText());
        }
    }

    private static void parseBreak(PsiBreakStatement breakStatement, Element parent) {
        Element breakElement = parent.addElement(BREAK_TAG);
        if (breakStatement.getLabelIdentifier() != null) {
            breakElement.addAttribute(LABEL_ATTRIBUTE, breakStatement.getLabelIdentifier().getText());
        }
    }

    private static void parseDoWhileLoop(PsiDoWhileStatement doWhileStatement, Element parent) {
        Element doWhileElement = parent.addElement(DO_WHILE_LOOP_TAG);
//        Element bodyElement = doWhileElement.addElement(BODY_TAG);
        parseStatement(doWhileStatement.getBody(), doWhileElement);
        if (doWhileStatement.getCondition() != null) {
            doWhileElement.addAttribute(CONDITION_ATTRIBUTE, doWhileStatement.getCondition().getText());
        }
    }

    private static void parseBlockStatement(PsiBlockStatement blockStatement, Element parent) {
        Element blockElement = parent.addElement(BLOCK_TAG);
        for (PsiStatement statement : blockStatement.getCodeBlock().getStatements()) {
            parseStatement(statement, blockElement);
        }
    }

    private static void parseWhileLoop(PsiWhileStatement whileStatement, Element parent) {
        Element whileElement = parent.addElement(WHILE_LOOP_TAG);
//        Element bodyElement = whileElement.addElement(BODY_TAG);
        parseStatement(whileStatement.getBody(), whileElement);
        if (whileStatement.getCondition() != null) {
            whileElement.addAttribute(CONDITION_ATTRIBUTE, whileStatement.getCondition().getText());
        }
    }

    private static void parseForLoop(PsiForStatement forStatement, Element parent) {
        Element forElement = parent.addElement(FOR_LOOP_TAG);
//        Element bodyElement = forElement.addElement(BODY_TAG);
        parseStatement(forStatement.getBody(), forElement);
        if (forStatement.getCondition() != null) {
            forElement.addAttribute(CONDITION_ATTRIBUTE, forStatement.getCondition().getText());
        }
        if (forStatement.getInitialization() != null) {
            forElement.addAttribute(INITIALIZATION_ATTRIBUTE, forStatement.getInitialization().getText());
        }
        if (forStatement.getUpdate() != null) {
            forElement.addAttribute(UPDATE_ATTRIBUTE, forStatement.getUpdate().getText());
        }
    }

    private static void parseForeachLoop(PsiForeachStatement foreachStatement, Element parent) {
        Element forElement = parent.addElement(FOREACH_LOOP_TAG);
//        Element bodyElement = forElement.addElement(BODY_TAG);
        parseStatement(foreachStatement.getBody(), forElement);

        forElement.addAttribute(FOREACH_ITERATION_PARAM_NAME, foreachStatement.getIterationParameter().getName());
        forElement.addAttribute(FOREACH_ITERATION_PARAM_TYPE, foreachStatement.getIterationParameter().getType().getCanonicalText());
        if (foreachStatement.getIteratedValue() != null) {
            forElement.addAttribute(FOREACH_ITERATED_VALUE, foreachStatement.getIteratedValue().getText());
        }
    }

    private static void parseLabeledStatement(PsiLabeledStatement labeledStatement, Element parent) {
        Element labeledElement = parent.addElement(LABEL_TAG);
        labeledElement.addAttribute(LABEL_ATTRIBUTE, labeledStatement.getName());
        parseStatement(labeledStatement.getStatement(), labeledElement);
    }

    private static void parseThrow(PsiThrowStatement throwStatement, Element parent) {
        Element throwElement = parent.addElement(THROW_TAG);
        if (throwStatement.getException() != null) {
//            throwElement.addAttribute(WHAT_ATTRIBUTE, throwStatement.getException().getText());
            throwElement.setText(throwStatement.getException().getText());
        }
    }

    private static void parseReturn(PsiReturnStatement returnStatement, Element parent) {
        Element returnElement = parent.addElement(RETURN_TAG);
        if (returnStatement.getReturnValue() != null) {
            returnElement.setText(returnStatement.getReturnValue().getText());
//            returnElement.addAttribute(WHAT_ATTRIBUTE, returnStatement.getReturnValue().getText());
        }
    }

    private static void parseSynchronized(PsiSynchronizedStatement synchronizedStatement, Element parent) {
        Element synchronizedElement = parent.addElement(SYNCHRONIZED_TAG);
        if (synchronizedStatement.getLockExpression() != null) {
            synchronizedElement.addAttribute(EXPRESSION_ATTRIBUTE, synchronizedStatement.getLockExpression().getText());
        }
        Element bodyElement = synchronizedElement.addElement(BLOCK_TAG);
        if (synchronizedStatement.getBody() != null) {
            for (PsiStatement statement : synchronizedStatement.getBody().getStatements()) {
                parseStatement(statement, bodyElement);
            }
        }
    }

    private static void parseTry(PsiTryStatement tryStatement, Element parent) {
        Element tryElement = parent.addElement(TRY_TAG);
        if (tryStatement.getTryBlock() != null) {
            Element tryBlock = tryElement.addElement(TRY_BLOCK_TAG);
            for (PsiStatement statement : tryStatement.getTryBlock().getStatements()) {
                parseStatement(statement, tryBlock);
            }
        }
        for (PsiCatchSection catchSection : tryStatement.getCatchSections()) {
            Element catchSectionElement = tryElement.addElement(CATCH_TAG);
            catchSectionElement.addAttribute(CATCH_TYPE_ATTRIBUTE, catchSection.getCatchType().getCanonicalText());
            if (catchSection.getCatchBlock() != null) {
                Element catchBlock = catchSectionElement.addElement(CATCH_BLOCK);
                for (PsiStatement statement : catchSection.getCatchBlock().getStatements()) {
                    parseStatement(statement, catchBlock);
                }
            }
        }
        if (tryStatement.getFinallyBlock() != null) {
            Element finallyElement = tryElement.addElement(FINALLY_TAG);
            Element finallyBlock = finallyElement.addElement(FINALLY_BLOCK);
            for (PsiStatement statement : tryStatement.getFinallyBlock().getStatements()) {
                parseStatement(statement, finallyBlock);
            }
        }
    }

    private static void parseSwitch(PsiSwitchStatement switchStatement, Element parent) {
        Element switchElement = parent.addElement(SWITCH_TAG);
        if (switchStatement.getExpression() != null) {
            switchElement.addAttribute(EXPRESSION_ATTRIBUTE, switchStatement.getExpression().getText());
        }
        if (switchStatement.getBody() != null) {
            Element caseElement = switchElement;
            for (PsiStatement statement : switchStatement.getBody().getStatements()) {
                if (statement instanceof PsiSwitchLabelStatement caseStatement) {
                    caseElement = switchElement.addElement(CASE_TAG);
                    if (caseStatement.getCaseLabelElementList() != null) {
                        String caseString = Arrays.stream(caseStatement.getCaseLabelElementList().getElements()).map(PsiElement::getText).collect(Collectors.joining(" | "));
                        caseElement.addAttribute(LABEL_ATTRIBUTE, caseString);
                    } else if (caseStatement.isDefaultCase()) {
                        caseElement.addAttribute(LABEL_ATTRIBUTE, DEFAULT_CASE_LABEL);
                    }
                } else {
                    parseStatement(statement, caseElement);
                }
            }
        }
    }

    private static void parseExpression(PsiExpressionStatement expressionStatement, Element parent) {
        Element expressionElement = parent.addElement(EXPRESSION_TAG);
        expressionElement.setText(expressionStatement.getText());
    }

    private static void parseDeclaration(PsiDeclarationStatement declarationStatement, Element parent) {
        String type = "";
        String vars = "";
        for (PsiElement declared : declarationStatement.getDeclaredElements()) {
            if (declared instanceof PsiVariable variable) {
                type = variable.getType().getCanonicalText();
                vars += ", " + variable.getName() + (variable.getInitializer() != null ? " = " + variable.getInitializer().getText() : "");
            }
        }
        vars = vars.substring(2);

        Element declarationElement = parent.addElement(DECLARATION_TAG);
        declarationElement.setText(type + " " + vars);
    }

    private static void parseStatement(PsiStatement statement, Element parent) {

        if (statement instanceof PsiDeclarationStatement declarationStatement) {
            // It's a declaration statement (variable declaration)
            parseDeclaration(declarationStatement, parent);
        } else if (statement instanceof PsiExpressionStatement expressionStatement) {
            parseExpression(expressionStatement, parent);
        } else if (statement instanceof PsiEmptyStatement) {
            parent.addElement(EMPTY_STATEMENT_TAG);
        } else if (statement instanceof PsiContinueStatement continueStatement) {
            parseContinue(continueStatement, parent);
        } else if (statement instanceof PsiBreakStatement breakStatement) {
            parseBreak(breakStatement, parent);
        } else if (statement instanceof PsiIfStatement ifStatement) {
            parseIfCondition(ifStatement, parent);
        } else if (statement instanceof PsiBlockStatement blockStatement) {
            parseBlockStatement(blockStatement, parent);
        } else if (statement instanceof PsiDoWhileStatement doWhileStatement) {
            parseDoWhileLoop(doWhileStatement, parent);
        } else if (statement instanceof PsiWhileStatement whileStatement) {
            parseWhileLoop(whileStatement, parent);
        } else if (statement instanceof PsiForStatement forStatement) {
            parseForLoop(forStatement, parent);
        } else if (statement instanceof PsiForeachStatement foreachStatement) {
            parseForeachLoop(foreachStatement, parent);
        } else if (statement instanceof PsiLabeledStatement labeledStatement) {
            parseLabeledStatement(labeledStatement, parent);
        } else if (statement instanceof PsiSwitchStatement switchStatement) {
            parseSwitch(switchStatement, parent);
        } else if (statement instanceof PsiSynchronizedStatement synchronizedStatement) {
            parseSynchronized(synchronizedStatement, parent);
        } else if (statement instanceof PsiTryStatement tryStatement) {
            parseTry(tryStatement, parent);
        } else if (statement instanceof PsiReturnStatement returnStatement) {
            parseReturn(returnStatement, parent);
        } else if (statement instanceof PsiThrowStatement throwStatement) {
            parseThrow(throwStatement, parent);
        } else {
            // Handle any other types of statements or unknown statements
            Element unknown = parent.addElement("UNKNOWN");
            unknown.addAttribute("text",  statement.getText());
            unknown.addAttribute("class", statement.getClass().getName());
        }
    }

}
