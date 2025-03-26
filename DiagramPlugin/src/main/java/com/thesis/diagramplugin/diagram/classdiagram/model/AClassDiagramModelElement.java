package com.thesis.diagramplugin.diagram.classdiagram.model;

import lombok.Getter;
import lombok.Setter;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.*;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;

@Getter
public abstract class AClassDiagramModelElement {

    protected String name;
    protected String uniqueId;
    protected ClassDiagramModelType modelType;
    protected Set<String> modifiers = new LinkedHashSet<>();
    protected int posX;
    protected int posY;
    protected int width;
    protected int height;
    protected boolean visible = true;
    @Setter
    private boolean sizeAndPosSet = false;
    public boolean hasSizeAndPosSet() { return sizeAndPosSet; }

    protected final Map<String, AClassDiagramModelClassLikeEntity> relations = new HashMap<>();


    public boolean isPublic() {
        return modifiers.contains("public");
    }

    public boolean isPrivate() {
        return (modifiers.contains("private") || (!this.isPublic() && !this.isProtected()));
    }

    public boolean isProtected() {
        return modifiers.contains("protected");
    }

    public void setModifiers(String modifiers) {
        for (String modifier : modifiers.split(";")) {
            this.modifiers.add(modifier);
        }
    }

    public AClassDiagramModelElement(Element element) {
        this.name = Optional.ofNullable(element.attributeValue(NAME_ATTRIBUTE)).orElse("unknown");
        this.uniqueId = Optional.ofNullable(element.attributeValue(UNIQUE_ID_ATTRIBUTE)).orElse("unknown");
        Optional.ofNullable(element.attributeValue(POS_X_ATTRIBUTE)).map(Integer::parseInt).ifPresent(x -> { this.posX = x; this.sizeAndPosSet = true; });
        Optional.ofNullable(element.attributeValue(POS_Y_ATTRIBUTE)).map(Integer::parseInt).ifPresent(y -> { this.posY = y; this.sizeAndPosSet = true; });
        Optional.ofNullable(element.attributeValue(SIZE_W_ATTRIBUTE)).map(Integer::parseInt).ifPresent(w -> { this.width = w; this.sizeAndPosSet = true; });
        Optional.ofNullable(element.attributeValue(SIZE_H_ATTRIBUTE)).map(Integer::parseInt).ifPresent(h -> { this.height = h; this.sizeAndPosSet = true; });
        Optional.ofNullable(element.attributeValue(VISIBLE_ATTRIBUTE)).map(Boolean::parseBoolean).ifPresent(v -> { this.visible = v; });

        Optional.ofNullable(element.attributeValue(MODIFIERS_ATTRIBURE)).map(String::toLowerCase).ifPresent(this::setModifiers);

        List<Node> nodes = element.selectNodes(".//" + RELATION_TAG);
        for (Node node : nodes) {
            if (node instanceof Element el) {
                Optional.ofNullable(el.attributeValue(NAME_ATTRIBUTE))
                        .ifPresent(this::addRelation);
            }
        }

        element.elements().stream().filter(el -> RELATION_TAG.equals(el.getName())).forEach(relElement -> {
            Optional.ofNullable(relElement.attributeValue(UNIQUE_ID_ATTRIBUTE)).ifPresent(this::addRelation);
        });
    };

    public void addRelation(String relation) {
        this.relations.put(relation, null);
    }

    public void resolveRelations(ClassDiagramModelPackage pkg) {
        for (Iterator<Map.Entry<String, AClassDiagramModelClassLikeEntity>> it = relations.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, AClassDiagramModelClassLikeEntity> relationEntry = it.next();
            AClassDiagramModelClassLikeEntity related = pkg.getClassLikeEntities().get(relationEntry.getKey());
            if (related != null) {
                relationEntry.setValue(related);
            } else {
                it.remove();
            }
        }

        this.getChildren().forEach(child -> child.resolveRelations(pkg));
    }

    protected abstract List<AClassDiagramModelElement> getChildren();
}
