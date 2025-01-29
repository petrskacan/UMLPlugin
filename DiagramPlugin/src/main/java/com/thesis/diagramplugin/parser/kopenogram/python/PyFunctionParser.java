package com.thesis.diagramplugin.parser.kopenogram.python;

import com.jetbrains.python.psi.*;
import com.jetbrains.python.psi.types.PyType;
import com.jetbrains.python.psi.types.TypeEvalContext;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;

public class PyFunctionParser {

    public static File parse(PyFunction function) {
        String classFileName = function.getContainingClass().getContainingFile().getName();
        String dir = function.getContainingFile().getContainingDirectory().getVirtualFile().getPath();
        String functionName = function.getName();

        Document document = DocumentHelper.createDocument();
        Element functionElement = document.addElement(METHOD_TAG);
        functionElement.addAttribute(NAME_ATTRIBUTE, functionName);

        String returnType = getReturnType(function);
        functionElement.addAttribute(RETURN_TYPE, returnType);

        parseFunctionParameters(function, functionElement);
        parseFunctionBody(function, functionElement);

        String fileName = dir + "/" + functionName + XML_FILE_NAME_POSTFIX_KOPENOGRAM;
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

    public static String getReturnType(PyFunction function) {
        TypeEvalContext evalContext = TypeEvalContext.codeInsightFallback(function.getProject());
        PyType returnType = evalContext.getReturnType(function);
        return Optional.ofNullable(returnType).map(PyType::getName).orElse("");
//        Optional.ofNullable(function.getReturnType()).map(PyType::getName).orElseGet(()->"null")
    }

    private static void parseFunctionParameters(PyFunction function, Element functionElement) {
        Element parametersElement = functionElement.addElement(PARAMETERS_TAG);
        for (PyParameter parameter : function.getParameterList().getParameters()) {
            Element parameterElement = parametersElement.addElement(PARAMETER_TAG);
            parameterElement.addAttribute(FIELD_NAME_ATTRIBUTE, parameter.getName());
//            parameterElement.addAttribute(FIELD_TYPE_ATTRIBUTE, parameter..getCanonicalText());
        }
    }

    private static void parseFunctionBody(PyFunction function, Element functionElement) {
        Element bodyElement = functionElement.addElement(BODY_TAG);
        for (PyStatement statement : function.getStatementList().getStatements()) {
            parseStatement(statement, bodyElement);
        }
    }

    private static void parseStatement(PyStatement statement, Element parent) {
        if (statement instanceof PyAssignmentStatement declarationStatement) {
            parseDeclaration(declarationStatement, parent);
        } else if (statement instanceof PyAugAssignmentStatement assignmentStatement) {
            // It's a declaration statement (variable declaration)
            parseAssignment(assignmentStatement, parent);
        } else if (statement instanceof PyExpressionStatement expressionStatement) {
            parseExpression(expressionStatement, parent);
        } else if (statement instanceof PyPassStatement) {
            parent.addElement(EMPTY_STATEMENT_TAG);
        } else if (statement instanceof PyContinueStatement continueStatement) {
            parseContinue(continueStatement, parent);
        } else if (statement instanceof PyBreakStatement breakStatement) {
            parseBreak(breakStatement, parent);
        } else if (statement instanceof PyIfStatement ifStatement) {
            parseIfCondition(ifStatement, parent);
        } else if (statement instanceof PyWhileStatement whileStatement) {
            parseWhileLoop(whileStatement, parent);
        } else if (statement instanceof PyForStatement forStatement) {
            parseForLoop(forStatement, parent);
        } else if (statement instanceof PyMatchStatement matchStatement) {
            parseMatchStatement(matchStatement, parent);
        } else if (statement instanceof PyTryExceptStatement tryStatement) {
            parseTry(tryStatement, parent);
        } else if (statement instanceof PyReturnStatement returnStatement) {
            parseReturn(returnStatement, parent);
        } else if (statement instanceof PyRaiseStatement raiseStatement) {
            parseRaise(raiseStatement, parent);
        } else {
            // Handle any other types of statements or unknown statements
            Element unknown = parent.addElement("UNKNOWN");
            unknown.addAttribute("text",  statement.getText());
            unknown.addAttribute("class", statement.getClass().getName());
        }
    }

    private static void parseMatchStatement(PyMatchStatement matchStatement, Element parent) {
        Element matchElement = parent.addElement(MATCH_TAG);
        Optional.ofNullable(matchStatement.getSubject()).ifPresent(subject -> {
            matchElement.addAttribute(EXPRESSION_ATTRIBUTE, subject.getText());
        });

        for (PyCaseClause caseClause : matchStatement.getCaseClauses()) {
            Element caseElement = matchElement.addElement(CASE_TAG);
            caseElement.addAttribute(LABEL_ATTRIBUTE, caseClause.getText());
            for (PyStatement statement : caseClause.getStatementList().getStatements()) {
                parseStatement(statement, caseElement);
            }
        }
    }

    private static void parseRaise(PyRaiseStatement raiseStatement, Element parent) {
        Element throwElement = parent.addElement(THROW_TAG);
        throwElement.setText(raiseStatement.getText());
    }

    private static void parseReturn(PyReturnStatement returnStatement, Element parent) {
        Element returnElement = parent.addElement(RETURN_TAG);
        Optional.ofNullable(returnStatement).map(PyReturnStatement::getExpression).ifPresent(expression -> {
            returnElement.setText(expression.getText());
        });
    }

    private static void parseTry(PyTryExceptStatement tryStatement, Element parent) {
        Element tryElement = parent.addElement(TRY_TAG);
        Element tryBlock = tryElement.addElement(TRY_BLOCK_TAG);
        parseStatementList(tryStatement.getTryPart().getStatementList(), tryBlock);

        for (PyExceptPart exceptPart : tryStatement.getExceptParts()) {
            Element exceptElement = tryElement.addElement(CATCH_TAG);
            exceptElement.addAttribute(CATCH_TYPE_ATTRIBUTE, exceptPart.getText());
            parseStatementList(exceptPart.getStatementList(), exceptElement.addElement(CATCH_BLOCK));
        }

        Optional.ofNullable(tryStatement.getElsePart()).map(PyStatementListContainer::getStatementList).ifPresent(statementList -> {
            Element elseElement = tryElement.addElement(ELSE_TAG);
            parseStatementList(statementList, elseElement);
        });

        Optional.ofNullable(tryStatement.getFinallyPart()).map(PyStatementListContainer::getStatementList).ifPresent(statementList -> {
            Element finallyElement = tryElement.addElement(FINALLY_TAG);
            parseStatementList(statementList, finallyElement);
        });

    }

    private static void parseForLoop(PyForStatement forStatement, Element parent) {
        Element forElement = parent.addElement(FOREACH_LOOP_TAG);

        Optional.of(forStatement.getForPart()).map(PyForPart::getSource).ifPresent(source -> {
            forElement.addAttribute(FOREACH_ITERATED_VALUE, source.getText());
        });
        Optional.of(forStatement.getForPart()).map(PyForPart::getTarget).ifPresent(target -> {
            forElement.addAttribute(FOREACH_ITERATION_PARAM_NAME, target.getText());
        });

        Element bodyElement = forElement.addElement(BLOCK_TAG);
        parseStatementList(forStatement.getForPart().getStatementList(), bodyElement);

        Optional.ofNullable(forStatement.getElsePart())
                        .map(PyStatementListContainer::getStatementList)
                        .ifPresent(statements -> {
                            parseStatementList(statements, forElement.addElement(ELSE_TAG));
                        });
    }

    private static void parseWhileLoop(PyWhileStatement whileStatement, Element parent) {
        Element whileElement = parent.addElement(WHILE_LOOP_TAG);
        if (whileStatement.getWhilePart().getCondition() != null) {
            whileElement.addAttribute(CONDITION_ATTRIBUTE, whileStatement.getWhilePart().getCondition().getText());
        }
        parseStatementList(whileStatement.getWhilePart().getStatementList(), whileElement.addElement(BLOCK_TAG));
    }

    private static void parseExpression(PyExpressionStatement expressionStatement, Element parent) {
        Element expressionElement = parent.addElement(EXPRESSION_TAG);
        expressionElement.setText(expressionStatement.getText());
    }

    private static void parseAssignment(PyAugAssignmentStatement assignmentStatement, Element parent) {
        Element declarationElement = parent.addElement(DECLARATION_TAG);
        declarationElement.setText(assignmentStatement.getText());;//type + " " + vars);
    }

    private static void parseDeclaration(PyAssignmentStatement assignmentStatement, Element parent) {
        Element assignmentElement = parent.addElement(DECLARATION_TAG);
        assignmentElement.setText(assignmentStatement.getText());;//type + " " + vars);
    }

    private static void parseContinue(PyContinueStatement continueStatement, Element parent) {
        Element continueElement = parent.addElement(CONTINUE_TAG);

    }

    private static void parseBreak(PyBreakStatement breakStatement, Element parent) {
        Element breakElement = parent.addElement(BREAK_TAG);

    }

    private static void parseIfCondition(PyIfStatement ifStatement, Element parent) {
        Element ifElement = parent.addElement(IF_TAG);
        PyIfPart ifPart = ifStatement.getIfPart();
        if (ifPart.getCondition() != null) {
            ifElement.addAttribute(CONDITION_ATTRIBUTE, ifPart.getCondition().getText());
        }
        Element thenElement = ifElement.addElement(THEN_TAG);
        parseStatementList(ifPart.getStatementList(), thenElement);


        Element lastIf = null;
        for (PyIfPart elifPart : ifStatement.getElifParts()) {
            Element elseElement = lastIf != null ? lastIf.addElement(ELSE_TAG) : ifElement.addElement(ELSE_TAG);
            Element elseIfElement = elseElement.addElement(IF_TAG);

            elseIfElement.addAttribute(CONDITION_ATTRIBUTE, Optional.ofNullable(elifPart.getCondition())
                                                            .map(PyExpression::getText).orElse(""));
            Element elifThen = elseIfElement.addElement(THEN_TAG);
            parseStatementList(elifPart.getStatementList(), elifThen);

            lastIf = elseIfElement;
        }

        if (ifStatement.getElsePart() != null) {
            Element elseElement = lastIf == null ? ifElement.addElement(ELSE_TAG) : lastIf.addElement(ELSE_TAG);
            parseStatementList(ifStatement.getElsePart().getStatementList(), elseElement);
        }
    }

    private static void parseStatementList(PyStatementList statementList, Element parent) {
        for (PyStatement statement : statementList.getStatements()) {
            parseStatement(statement, parent);
        }
    }

}
