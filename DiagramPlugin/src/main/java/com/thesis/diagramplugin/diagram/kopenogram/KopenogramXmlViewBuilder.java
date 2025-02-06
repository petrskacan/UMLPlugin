package com.thesis.diagramplugin.diagram.kopenogram;

import com.intellij.ui.components.JBScrollPane;
import com.sun.source.tree.*;
import com.thesis.diagramplugin.rendering.kopenogram.PaintedNode;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.*;
import com.thesis.diagramplugin.rendering.kopenogram.treepainterElement.*;
import com.thesis.diagramplugin.rendering.kopenogram.treepainterElement.Container;
import com.thesis.diagramplugin.rendering.kopenogram.treeprinter.Surface;
import lombok.Getter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;


public class KopenogramXmlViewBuilder {

    @Getter
    private JComponent view;
    @Getter
    private String name;

    private RootPainter root;
    private final List<PaintedNode> parents;

    public KopenogramXmlViewBuilder(String diagramXml) {
        this.parents = new LinkedList<>();
        this.root = new RootPainter();
        this.view = this.buildView(diagramXml);
    }
    public JComponent buildView(String diagramXml) {
        // config with basic settings
        PainterConfig config = new PainterConfig();
        config.font = new Font("Arial", Font.PLAIN, Integer.parseInt(Settings.Property.FONT_SIZE.getValue()));
        config.verticalGap = Integer.parseInt(Settings.Property.VERTICAL_GAP.getValue());
        config.horizontalGap = Integer.parseInt(Settings.Property.HORIZONTAL_GAP.getValue());
        config.textMaxChars = Integer.parseInt(Settings.Property.NUMBER_OF_SYMBOLS_IN_EXPRESSION.getValue());

        try {
            Document document = DocumentHelper.parseText(diagramXml);
            Element rootElement = document.getRootElement();

            if (rootElement.getName() == METHOD_TAG) {
                if (rootElement.attribute(NAME_ATTRIBUTE) != null) {
                    this.name = rootElement.attribute(NAME_ATTRIBUTE).getValue();
                }
                // painting tree
                root.setRoot(processElement(rootElement));
                // serialize to file
//        PainterUtils.saveToFile(root, "painted.tree");
                // size of picture
                Dimension dim = root.getDimension(config, new Point(0, 0));
                // create img for paint
                BufferedImage img = new BufferedImage(dim.width + 1, dim.height + 1, BufferedImage.TYPE_3BYTE_BGR);
                // create graphics
                Graphics g = img.createGraphics();
                // paint img
                root.paint(g, config, new Point(0, 0), dim);
                //
//        //JCrollPane for kopenogram
                JComponent kopenogramPane = new JBScrollPane(new Surface(img, new Dimension(dim.width, dim.height)));

                return kopenogramPane;
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        config.textMaxChars = Integer.parseInt(Settings.Property.NUMBER_OF_SYMBOLS_IN_EXPRESSION.getValue());
        return new JBScrollPane();
    }

    private PainterElement processElement(Element element) {
        PaintedNode paintedNode = new PaintedNode(element);
        parents.add(paintedNode);

        PainterElement ret = new Bar("not resolved", Color.RED);
        if (element == null) {
            return ret;
        }

        ret = this.processTag(element);
        if (ret instanceof OverPainterElement) {
            root.addOverElement((OverPainterElement) ret);
        } else if (!(ret instanceof HorizontalContainer) && !(ret instanceof ExtendedBar)) {
            ret = new HorizontalContainer().addChild(ret);
        }

        if(element.getText().length() > Integer.parseInt(Settings.Property.NUMBER_OF_SYMBOLS_IN_EXPRESSION.getValue()))
        {
            Settings.Property.NUMBER_OF_SYMBOLS_IN_EXPRESSION.setValue(String.valueOf(element.getText().length()));
        }

        paintedNode.setElement(ret);
        parents.remove(parents.size() - 1);
        return ret;
    }

    private PainterElement processTag(Element element) {
        PainterElement outputElement = null;
        switch (element.getName()) {
            case METHOD_TAG : {
                outputElement = this.buildMethodElement(element);
            } break;

            case BLOCK_TAG : {
                outputElement = this.buildBlockElement(element);
            } break;

            case BREAK_TAG : {
                outputElement = this.buildBreakElement(element);
            } break;

            case CONTINUE_TAG : {
                outputElement = this.buildContinueElement(element);
            } break;

            case EXPRESSION_TAG : {
                outputElement = this.buildExpressionElement(element);
            } break;

            case DO_WHILE_LOOP_TAG : {
                outputElement = this.buildDoWhileLoopElement(element);
            } break;

            case WHILE_LOOP_TAG : {
                outputElement = this.buildWhileLoopElement(element);
            } break;

            case EMPTY_STATEMENT_TAG : {
                outputElement = this.buildEmptyStatementElement();
            } break;

            case FOREACH_LOOP_TAG : {
                outputElement = this.buildForeachLoopElement(element);
            } break;

            case FOR_LOOP_TAG : {
                outputElement = this.buildForLoopElement(element);
            } break;

            case IF_TAG : {
                outputElement = this.buildIfElement(element);
            } break;

            case LABEL_TAG : {
                outputElement = this.buildLabelElement(element);
            } break;

            case RETURN_TAG : {
                outputElement = this.buildReturnElement(element);
            } break;

            case MATCH_TAG :
            case SWITCH_TAG : {
                outputElement = this.buildSwitchElement(element);
            } break;

            case SYNCHRONIZED_TAG : {
                outputElement = this.buildSynchronizedElement(element);
            } break;

            case THROW_TAG : {
                outputElement = this.buildThrowElement(element);
            } break;

            case TRY_TAG : {
                outputElement = this.buildTryElement(element);
            } break;

            case DECLARATION_TAG : {
                outputElement = this.buildDeclarationElement(element);
            }
        }

        return outputElement;
    }


    private PainterElement buildDeclarationElement(Element declarationElement) {
        Color color = Settings.decodeColorProperty(Settings.Property.EXPRESSION_COLOR.getValue());
        return new Bar(declarationElement.getText(), color, Color.BLACK, Color.BLACK);
    }

    private PainterElement buildTryElement(Element tryElement) {
        int keyWords = Integer.parseInt(Settings.Property.KOPENOGRAM_KEY_WORDS.getValue());
        Color thc = Settings.decodeColorProperty(Settings.Property.TRY_HEAD_COLOR.getValue());
        Color tbc = Settings.decodeColorProperty(Settings.Property.TRY_BODY_COLOR.getValue());
        Color chc = Settings.decodeColorProperty(Settings.Property.CATCH_HEAD_COLOR.getValue());
        Color cbc = Settings.decodeColorProperty(Settings.Property.CATCH_BODY_COLOR.getValue());
        Color fhc = Settings.decodeColorProperty(Settings.Property.FINALLY_HEAD_COLOR.getValue());
        Color fbc = Settings.decodeColorProperty(Settings.Property.FINALLY_BODY_COLOR.getValue());
        VerticalContainer vContainer = new VerticalContainer();
        String text = ("" + Symbols.RIGHT).repeat(3);

        BarWithBody tryBody = new BarWithBody((keyWords == 1 ? "try" : "") + text, thc, tbc, Color.BLACK, Color.BLACK);
        Optional.of(tryElement.element(TRY_BLOCK_TAG)).ifPresent(tryBlockElement -> {
            tryBody.addChild(processBlock(tryBlockElement));
        });
        vContainer.addChild(tryBody);

        List<Element> catchList = tryElement.elements().stream().filter(catchElement -> catchElement.getName() == CATCH_TAG).toList();
        for (Element catchElement : catchList) {
            if (catchElement != null) {
                String catchType = catchElement.attributeValue(CATCH_TYPE_ATTRIBUTE);
                BarWithBody catchBody = new BarWithBody("" + catchType, chc, cbc, Color.BLACK, Color.BLACK);
                Optional.of(catchElement.element(CATCH_BLOCK)).ifPresent(catchBlockElement -> {
                    catchBody.addChild(processBlock(catchBlockElement));
                });
                vContainer.addChild(catchBody);
            }
        }
        // Finally
        Optional.ofNullable(tryElement.element(FINALLY_TAG)).ifPresent(finallyElement -> {
            BarWithBody finallyBody = new BarWithBody("finally", fhc, fbc, Color.BLACK, Color.BLACK);
            finallyBody.addChild(processBlock(finallyElement));
            vContainer.addChild(finallyBody);
        });

        return vContainer;
    }

    private PainterElement buildThrowElement(Element throwElement) {
        Color color = Settings.decodeColorProperty(Settings.Property.THROW_COLOR.getValue());
        String expression = throwElement.getText();
        return new Bar(Symbols.UP + "throw " + expression, color, Color.BLACK, Color.BLACK);
    }

    private PainterElement buildSynchronizedElement(Element synchronizedElement) {
        Color headColor = Settings.decodeColorProperty(Settings.Property.SYNCHRONIZED_HEAD_COLOR.getValue());
        Color bodyColor = Settings.decodeColorProperty(Settings.Property.SYNCHRONIZED_BODY_COLOR.getValue());

        String expression = synchronizedElement.attributeValue(EXPRESSION_ATTRIBUTE);

        BarWithBody synchronizedBody = new BarWithBody("synchronized " + expression, headColor, bodyColor, Color.BLACK, Color.BLACK);
        Optional.of(synchronizedElement.element(BLOCK_TAG)).ifPresent(blockElement -> {
            synchronizedBody.addChild(processBlock(blockElement));
        });

        return synchronizedBody;
    }

    private PainterElement buildSwitchElement(Element switchElement) {
        int keyWords = Integer.parseInt(Settings.Property.KOPENOGRAM_KEY_WORDS.getValue());
        Color headColor = Settings.decodeColorProperty(Settings.Property.SWITCH_HEAD_COLOR.getValue());
        Color bodyColor = Settings.decodeColorProperty(Settings.Property.SWITCH_BODY_COLOR.getValue());
        Color lbc = Color.BLACK;
        HorizontalContainer hContainer = new HorizontalContainer();
        VerticalContainer vContainer = new VerticalContainer();
        String text = ("" + Symbols.RIGHT).repeat(3);
        String keyWord = "";
        String expression = switchElement.attributeValue(EXPRESSION_ATTRIBUTE);
        if (keyWords == 1) {
            keyWord = switchElement.getName().toLowerCase() + ": ";
            Bar bar = new Bar(keyWord + expression, headColor);
            vContainer.addChild(bar);
        }
        boolean firstCase = true;
        boolean oCaseEmpty = false;

        List<Element> caseList = switchElement.elements().stream().filter(element -> CASE_TAG.equals(element.getName())).toList();
        for (Element caseElement : caseList) {
            List<OverPainterElement> overElements = new ArrayList<>(root.getOverElements());

            String caseLabel = caseElement.attributeValue(LABEL_ATTRIBUTE);
            if (caseLabel == null) {
                if (oCaseEmpty) {
                    lbc = bodyColor;
                }
                BarWithBody caseBody = new BarWithBody("" + Symbols.DOWN, headColor, bodyColor, headColor, lbc);
                boolean first = true;

                for (Element statementElement : caseElement.elements()) {
                    if (!first) {
                        caseBody.addChild(Separator.VERTICAL);
                    }
                    first = false;
                    caseBody.addChild(processTag(statementElement));
                }
                hContainer.addChild(caseBody);
                caseBody.setAvoidElements(overElements);
            } else {
                if (keyWords == 1) {
                    keyWord = "case ";
                }

                if (!firstCase && oCaseEmpty) {
                    lbc = bodyColor;
                }
                oCaseEmpty = caseElement.elements().isEmpty();

                BarWithBody caseBody = new BarWithBody(keyWord + caseLabel, headColor, bodyColor, headColor, lbc);

                boolean first = true;
                int last = 0;

                for (Element statementElement : caseElement.elements()) {
                    if (!first) {
                        caseBody.addChild(Separator.VERTICAL);
                    }
                    first = false;
                    last++;

                    caseBody.addChild(processTag(statementElement));

                    if (last == caseElement.elements().size() && !BREAK_TAG.equals((caseElement.elements().get(caseElement.elements().size() - 1)).getName())) {
                        caseBody.addChild(Separator.VERTICAL);
                        caseBody.addChild(new FootBar(text, headColor, Color.BLACK, Color.BLACK));
                    }
                }

                hContainer.addChild(caseBody);
                caseBody.setAvoidElements(overElements);
                lbc = Color.BLACK;
            }
            firstCase = false;
        }

        vContainer.addChild(hContainer);
        return vContainer;
    }

    private PainterElement buildReturnElement(Element returnElement) {
        int keyWords = Integer.parseInt(Settings.Property.KOPENOGRAM_KEY_WORDS.getValue());
        Color color = Settings.decodeColorProperty(Settings.Property.BREAK_COLOR.getValue());
        String text = ("" + Symbols.RIGHT).repeat(4);
//        for (int i = 0; i < 2; i++) {
//            text = text.concat(text);
//        }
//        String expression = returnElement.attributeValue(WHAT_ATTRIBUTE);
        String expression = returnElement.getText();
        if (expression == null) {
            return new ExtendedBar((keyWords == 1 ? "return " : "") + text, color, Color.BLACK, Color.BLACK);
        } else {
            return new ExtendedBar((keyWords == 1 ? "return " : "") + expression + text, color, Color.BLACK, Color.BLACK);
        }
    }

    private PainterElement buildLabelElement(Element labelElement) {
        Color labelColor = Settings.decodeColorProperty(Settings.Property.LABEL_COLOR.getValue());
        VerticalContainer vContainer = new VerticalContainer();

        Optional.of(labelElement.attributeValue(LABEL_ATTRIBUTE)).ifPresent(label -> {
            vContainer.addChild(new Bar(label, labelColor, Color.BLACK, Color.BLACK));
        });
        vContainer.addChild(processTag(labelElement.elements().get(0)));
        return vContainer;
    }

    private PainterElement buildIfElement(Element ifElement) {
        Color headColor = Settings.decodeColorProperty(Settings.Property.SWITCH_HEAD_COLOR.getValue());
        Color bodyColor = Settings.decodeColorProperty(Settings.Property.SWITCH_BODY_COLOR.getValue());
        HorizontalContainer hContainer = new HorizontalContainer();
        int keyWords = Integer.parseInt(Settings.Property.KOPENOGRAM_KEY_WORDS.getValue());

        String condition = ifElement.attributeValue(CONDITION_ATTRIBUTE);

        BarWithBody ifBody = new BarWithBody(keyWords == 1 ? "if (" + condition + ")" : condition, headColor, bodyColor, Color.BLACK, Color.BLACK);
        Optional.of(ifElement.element(THEN_TAG)).ifPresent(thenElement -> {
            for (Element statement : thenElement.elements()) {
                ifBody.addChild(processTag(statement));
            }
        });
        hContainer.addChild(ifBody);
        Optional.ofNullable(ifElement.element(ELSE_TAG)).ifPresent(elseElement -> {
            List<OverPainterElement> overElements = new ArrayList<>(root.getOverElements());
            BarWithBody elseBody = new BarWithBody("else ", headColor, bodyColor, Color.BLACK, Color.BLACK);
            for (Element statement : elseElement.elements()) {
                elseBody.addChild(processTag(statement));
            }
            hContainer.addChild(elseBody);
            setAvoidElements(elseBody, overElements);
        });

        return hContainer;
    }

    private BarWithBody buildForLoopBody(Element loopElement, String text) {
        Color headColor = Settings.decodeColorProperty(Settings.Property.CYCLE_HEAD_COLOR.getValue());
        Color bodyColor = Settings.decodeColorProperty(Settings.Property.CYCLE_BODY_COLOR.getValue());

        BarWithBody forLoopBody = new BarWithBody(text, headColor, bodyColor, Color.BLACK, Color.BLACK);

        Optional.ofNullable(loopElement.element(BLOCK_TAG)).ifPresent(loopBodyElement -> {
            forLoopBody.addChild(processTag(loopBodyElement));
        });

        return forLoopBody;
    }

    private PainterElement buildForLoopElement(Element forLoopElement) {
        int keyWords = Integer.parseInt(Settings.Property.KOPENOGRAM_KEY_WORDS.getValue());
        Color headColor = Settings.decodeColorProperty(Settings.Property.CYCLE_HEAD_COLOR.getValue());
        VerticalContainer vContainer = new VerticalContainer();

        String forInit = forLoopElement.attributeValue(INITIALIZATION_ATTRIBUTE);
        String forCondition = forLoopElement.attributeValue(CONDITION_ATTRIBUTE);
        String forUpdate = forLoopElement.attributeValue(UPDATE_ATTRIBUTE);

        String text = forInit + (forInit.endsWith(";") ? " " : "; ") + forCondition + "; " + forUpdate;
        if (keyWords == 1) {
            text = "for (" + text + ")";
        }

        vContainer.addChild(buildForLoopBody(forLoopElement, text));
        vContainer.addChild(new Bar("" + Symbols.UP, headColor));
        return vContainer;
    }

    private PainterElement buildForeachLoopElement(Element foreachElement) {
        int keyWords = Integer.parseInt(Settings.Property.KOPENOGRAM_KEY_WORDS.getValue());
        Color headColor = Settings.decodeColorProperty(Settings.Property.CYCLE_HEAD_COLOR.getValue());
        VerticalContainer vContainer = new VerticalContainer();

        String paramType = foreachElement.attributeValue(FOREACH_ITERATION_PARAM_TYPE);
        String paramName = foreachElement.attributeValue(FOREACH_ITERATION_PARAM_NAME);
        String paramIterable = foreachElement.attributeValue(FOREACH_ITERATED_VALUE);

        String expression = paramType == null ? "" : paramType + " ";
        expression += paramName + " : " + paramIterable;
        String text = expression;
        if (keyWords == 1) {
            text = "for (" + expression + ")";
        }

        vContainer.addChild(buildForLoopBody(foreachElement, text));
        vContainer.addChild(new Bar("" + Symbols.UP, headColor));
        return vContainer;
    }

    private PainterElement buildExpressionElement(Element expressionElement) {
        Color color = Settings.decodeColorProperty(Settings.Property.EXPRESSION_COLOR.getValue());
        return new Bar(expressionElement.getText(), color, Color.BLACK, Color.BLACK);
    }

    private PainterElement buildEmptyStatementElement() {
        return new Bar("empty statement", Settings.decodeColorProperty(Settings.Property.EXPRESSION_COLOR.getValue()), Color.BLACK, Color.BLACK);
    }

    private PainterElement buildDoWhileLoopElement(Element doWhileElement) {
        int keyWords = Integer.parseInt(Settings.Property.KOPENOGRAM_KEY_WORDS.getValue());
        Color chc = Settings.decodeColorProperty(Settings.Property.CYCLE_HEAD_COLOR.getValue());
        Color cbc = Settings.decodeColorProperty(Settings.Property.CYCLE_BODY_COLOR.getValue());
        VerticalContainer vContainer = new VerticalContainer();
        BarWithBody doWhileLoopBody = new BarWithBody("" + Symbols.DOWN, chc, cbc, Color.BLACK, Color.BLACK);
//        if (object.getStatement() instanceof BlockTree) {
//            doWhileLoopBody.addChild(processBlock((BlockTree) object.getStatement(), generator));
//        } else {
//            doWhileLoopBody.addChild(generator.processObject(object.getStatement()));
//        }
        Optional.of(doWhileElement.element(BLOCK_TAG)).ifPresent(doWhileBodyElement -> {
            doWhileLoopBody.addChild(processTag(doWhileBodyElement));
        });
        vContainer.addChild(doWhileLoopBody);

        Optional.of(doWhileElement.attributeValue(CONDITION_ATTRIBUTE)).ifPresent(condition -> {
            vContainer.addChild(new Bar(((keyWords == 1) ? "while (" + condition + ")" : condition), chc));
        });
        return vContainer;
    }

    private PainterElement buildWhileLoopElement(Element whileElement) {
        int keyWords = Integer.parseInt(Settings.Property.KOPENOGRAM_KEY_WORDS.getValue());
        Color headColor = Settings.decodeColorProperty(Settings.Property.CYCLE_HEAD_COLOR.getValue());
        Color bodyColor = Settings.decodeColorProperty(Settings.Property.CYCLE_BODY_COLOR.getValue());
        VerticalContainer vContainer = new VerticalContainer();

        String condition = whileElement.attributeValue(CONDITION_ATTRIBUTE);
        BarWithBody whileLoopBody = new BarWithBody((keyWords == 1 ? "while (" + condition + ")" : condition), headColor, bodyColor, Color.BLACK, Color.BLACK);

        Optional.ofNullable(whileElement.element(BLOCK_TAG)).ifPresent(whileBodyElement -> {
            whileLoopBody.addChild(processTag(whileBodyElement));
        });

        vContainer.addChild(whileLoopBody);
        vContainer.addChild(new Bar("" + Symbols.UP, headColor));
        return vContainer;
    }

    private PainterElement buildContinueElement(Element continueElement) {
        Color color = Settings.decodeColorProperty(Settings.Property.CONTINUE_COLOR.getValue());
        String label = continueElement.attributeValue(LABEL_ATTRIBUTE);

        return new Bar("continue " + ((label != null) ? label : ""), color, Color.BLACK, Color.BLACK);
    }

    private PainterElement buildBreakElement(Element breakElement) {
            Color breakColor = Settings.decodeColorProperty(Settings.Property.BREAK_COLOR.getValue());
            String text = ("" + Symbols.RIGHT);
            for (int i = 0; i < 2; i++) {
                text = text.concat(text);
            }
            String label = breakElement.attributeValue(LABEL_ATTRIBUTE);
            if (label != null) {
                PaintedNode labelParent = findLabeledParent(label);
                if (labelParent != null) {
                    return new ExtendedBarOver(label + " " + text, breakColor,
                            Color.BLACK, Color.BLACK, labelParent);
                }
            }
            return new ExtendedBar(text, breakColor, Color.BLACK, Color.BLACK);
    }

    private PainterElement buildBlockElement(Element blockElement) {
        Color blockBodyColor = Settings.decodeColorProperty(Settings.Property.BLOCK_BODY_COLOR.getValue());
        BarWithBody blockBody = new BarWithBody(null, Color.BLACK, blockBodyColor, Color.BLACK, Color.BLACK);
        boolean added = false;
        for (Element statementElement : blockElement.elements()) {
            PainterElement elem = this.processTag(statementElement);
            if (elem != null) {
                if (!blockBody.getChildren().isEmpty()) {
                    blockBody.addChild(Separator.VERTICAL);
                }
                blockBody.addChild(elem);
                added = true;
            }
        }
        if (!added) {
            blockBody.addChild(Empty.SPACE);
        }
        return blockBody;
    }

    private PainterElement buildMethodElement(Element methodElement) {
        BarWithBody methodBody = new BarWithBody(methodElement.attributeValue(NAME_ATTRIBUTE),
                Settings.decodeColorProperty(Settings.Property.METHOD_HEAD_COLOR.getValue()),
                Settings.decodeColorProperty(Settings.Property.METHOD_BODY_COLOR.getValue()),
                Color.BLACK, Color.BLACK);
        Optional.ofNullable(methodElement.element(BODY_TAG)).ifPresent(bodyElement -> {
            methodBody.addChild(this.processBlock(bodyElement));
        });
//        Element methodBodyElement = methodElement.element(BODY_TAG);
//        if (methodBodyElement != null && methodBodyElement.getName() == BODY_TAG) {
//            methodBody.addChild(this.processBlock(methodBodyElement));
//        }
        return methodBody;
    }

    private PainterElement processBlock(Element blockElement) {
        VerticalContainer vContainer = new VerticalContainer();
        for (Element statementElement : blockElement.elements()) {
            PainterElement elem = this.processTag(statementElement);
            if (elem != null) {
                if (!vContainer.getChildren().isEmpty()) {
                    vContainer.addChild(Separator.VERTICAL);
                }
                vContainer.addChild(elem);
            }
        }
        return vContainer;
    }

    protected PaintedNode findLabeledParent(String label) {
        ListIterator<PaintedNode> it = parents.listIterator(parents.size());
        while (it.hasPrevious()) {
            PaintedNode paintedNode = it.previous();
            Object node = paintedNode.getNode();
            if (node instanceof LabeledStatementTree) {
                if (((LabeledStatementTree) node).getLabel().equals(label)) {
                    return paintedNode;
                }
            }
        }
        return null;
    }

    protected void setAvoidElements(AvoidableContainer elem, List<? extends OverPainterElement> avoid) {
        if ((elem.getChildren().size() == 1) && (elem.getChildren().get(0) instanceof Container)) {
            Container vc = (Container) elem.getChildren().get(0);
            if ((vc.getChildren().size() == 1) && (vc.getChildren().get(0) instanceof AvoidableContainer)) {
                ((AvoidableContainer) vc.getChildren().get(0)).setAvoidElements(avoid);
                return;
            }
        }
        elem.setAvoidElements(avoid);
    }

    public void save() {
        PainterUtils.saveToFile(root, "painted.tree");
    }

    public void open() {

    }
}
