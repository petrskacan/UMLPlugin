package com.thesis.diagramplugin.parser.classdiagram.python;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.*;
import com.jetbrains.python.psi.impl.PyFunctionImpl;
import com.jetbrains.python.psi.types.TypeEvalContext;
import com.thesis.diagramplugin.parser.classdiagram.model.ElementType;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;

public class PyClassDiagramParser {

    private final Project project;
    private final TypeEvalContext context;

    private Map<PyClass, Element> elements;
    private Map<String, Element> elementsById;
    private Map<PyFunction, Element> methodElements;

    private Map<String, Set<String>> protocolMethodMap = new HashMap<>();
    private Set<String> protocolIds = new HashSet<>();

    private class PosPlacement {
        public int x = 0;
        public int y = 0;
        public int w = 0;
        public int h = 0;
        public boolean visible = true;
    }

    private final Map<String, PosPlacement> existingElements = new HashMap<>();
    private final Map<String, List<String>> knownIdentifiers = new HashMap<>();


    public PyClassDiagramParser(Project project) {
        this.project = project;
        this.context = TypeEvalContext.codeInsightFallback(project);
        elements = new HashMap<>();
        methodElements = new HashMap<>();
        elementsById = new HashMap<>();
    }

    public File parse(PsiDirectory psiDir) {
        Map<PsiFile, List<PyClass>> moduleMap = createModuleMap(psiDir);
        String filePath = ((PsiFile)moduleMap.keySet().toArray()[0]).getContainingFile().getVirtualFile().getPath();
        String pkgPath = filePath.substring(0, filePath.lastIndexOf("/"));
        String pkgName = pkgPath.substring(pkgPath.lastIndexOf("/") + 1);
        String fileName = pkgPath + "/" + pkgName + XML_FILE_NAME_POSTFIX_CLASS_DIAGRAM;
        File file = new File(fileName);
        try {

            // maintain placement/size of existing objects if file already exists
            if (file.exists()) {
                Document orig = DocumentHelper.parseText(new String(Files.readAllBytes(Path.of(fileName))));
                Element origRoot = orig.getRootElement();
                buildSizePositionMap(origRoot, existingElements);
            }

            Document document = DocumentHelper.createDocument();
            Element packageElement = document.addElement(PACKAGE_TAG);
            createPackageElement(psiDir, packageElement);
            processProtocols(packageElement);

            FileOutputStream fileOS = new FileOutputStream(file);
            XMLWriter writer = new XMLWriter(fileOS, OutputFormat.createPrettyPrint());
            writer.write(document);
            writer.flush();
            fileOS.close();
        } catch (IOException e) {
            System.out.println("IO ERROR during creation of " + fileName);
        } catch (DocumentException e) {
            System.out.println("Document exception on existing document " + fileName);
        }
        return file;
    }

    private void processProtocols(Element packageElement) {

        for (String protocolId : protocolIds) {
            Set<String> protocolMethods = protocolMethodMap.get(protocolId);
            if (protocolMethods != null) {
                for (HashMap.Entry<String, Set<String>> entry : protocolMethodMap.entrySet()) {
                    Set<String> methodsToImplement = entry.getValue();
                    boolean impl = false;
                    for (String protocolMethod : protocolMethods) {
                        if (methodsToImplement.contains(protocolMethod)) {
                            impl = true;
                        } else {
                            break;
                        }
                    }
                    if (impl) {
                        String clsId = entry.getKey();
                        Element clsElement = elementsById.get(clsId);
                        if (clsElement != null) {
                            clsElement.addElement(IMPLEMENTATION_TAG).setText(protocolId);
                        }
                    }
                }
            }
        }
    }

    private void buildSizePositionMap(Element el, Map<String, PosPlacement> existingElements) {
        if (PACKAGE_TAG.equals(el.getName()) || MODULE_TAG.equals(el.getName()) || ENUM_TAG.equals(el.getName()) ||
            RECORD_TAG.equals(el.getName()) || CLASS_TAG.equals(el.getName()) || INTERFACE_TAG.equals(el.getName())) {

            PosPlacement pos = new PosPlacement();
            String id = Optional.ofNullable(el.attributeValue(UNIQUE_ID_ATTRIBUTE)).orElse("unknown");
            Optional.ofNullable(el.attributeValue(POS_X_ATTRIBUTE)).map(Integer::parseInt).ifPresent(x -> {
                pos.x = x;
            });
            Optional.ofNullable(el.attributeValue(POS_Y_ATTRIBUTE)).map(Integer::parseInt).ifPresent(y -> {
                pos.y = y;
            });
            Optional.ofNullable(el.attributeValue(SIZE_W_ATTRIBUTE)).map(Integer::parseInt).ifPresent(w -> {
                pos.w = w;
            });
            Optional.ofNullable(el.attributeValue(SIZE_H_ATTRIBUTE)).map(Integer::parseInt).ifPresent(h -> {
                pos.h = h;
            });
            Optional.ofNullable(el.attributeValue(VISIBLE_ATTRIBUTE)).map(Boolean::parseBoolean).ifPresent(v -> {
                pos.visible = v;
            });

            existingElements.put(id, pos);

            for (Element child : el.elements()) {
                buildSizePositionMap(child, existingElements);
            }
        }
    }

    private String getPackageId(PsiDirectory psiDir) {
        if (psiDir == null) return null;
        String id = psiDir.getVirtualFile().getPath();
        String basePath = psiDir.getProject().getBasePath();
        if (basePath != null && id.startsWith(basePath)) {
            id = id.substring(basePath.length() + 1);
        }
//        return id;
        return DigestUtils.md5Hex(id).toUpperCase();
    }

    private void createPackageElement(PsiDirectory psiDir, Element packageElement) {
        Map<PsiFile, List<PyClass>> moduleMap = createModuleMap(psiDir);
        if (moduleMap.isEmpty()) {
            System.out.println("No Python files found in directory: " + psiDir.getName());
            return;
        }
        String filePath = ((PsiFile)moduleMap.keySet().toArray()[0]).getContainingFile().getVirtualFile().getPath();
        String pkgPath = filePath.substring(0, filePath.lastIndexOf("/"));
        String pkgName = pkgPath.substring(pkgPath.lastIndexOf("/") + 1);

        String packageId = getPackageId(psiDir);
        packageElement.addAttribute(NAME_ATTRIBUTE, pkgName);
        packageElement.addAttribute(PATH_ATTRIBUTE, pkgPath);
        packageElement.addAttribute(UNIQUE_ID_ATTRIBUTE, packageId);

        copyExistingSizePosAttributes(packageId, packageElement);

        createModuleElements(packageElement, moduleMap);
        ArrayList<PyClass> classes = collectClasses(moduleMap);
        createRelations(classes, pkgPath);
        resolveTests();

        for (PsiDirectory subDir : psiDir.getSubdirectories()) {
            Element subPackageElement = packageElement.addElement(PACKAGE_TAG);
            createPackageElement(subDir, subPackageElement);
        }
    }

    private void copyExistingSizePosAttributes(String packageId, Element packageElement) {
        PosPlacement posPlacement = existingElements.get(packageId);
        if (posPlacement != null) {
            packageElement.addAttribute(POS_X_ATTRIBUTE, Integer.toString(posPlacement.x));
            packageElement.addAttribute(POS_Y_ATTRIBUTE, Integer.toString(posPlacement.y));
            packageElement.addAttribute(SIZE_W_ATTRIBUTE, Integer.toString(posPlacement.w));
            packageElement.addAttribute(SIZE_H_ATTRIBUTE, Integer.toString(posPlacement.h));
            packageElement.addAttribute(VISIBLE_ATTRIBUTE, Boolean.toString(posPlacement.visible));
        }
    }

    private Map<PsiFile, List<PyClass>> createModuleMap(PsiDirectory psiDirectory) {
        Map<PsiFile, List<PyClass>> moduleMap = new HashMap<>();

        for (PsiFile psiFile : psiDirectory.getFiles()) {
            if (psiFile.getName().endsWith(".py")) {
                List<PyClass> classes = new ArrayList<>();
                for (PsiElement child : psiFile.getChildren()) {
                    if (child instanceof PyClass pyClass) {
                        classes.add(pyClass);
                    }
                }
                moduleMap.put(psiFile, classes);
            }
        }

        return moduleMap;
    }

    private ArrayList<PyClass> collectClasses(Map<PsiFile, List<PyClass>> moduleMap) {
        ArrayList<PyClass> classes = new ArrayList<>();
        for (List<PyClass> clsList : moduleMap.values()) {
            classes.addAll(clsList);
        }
        return classes;
    }

    private String getModuleId(PsiFile module) {
        String modulePath = module.getVirtualFile().getPath();
        String projectPath = module.getProject().getBasePath();
        if (projectPath != null && modulePath.startsWith(projectPath)) {
            return DigestUtils.md5Hex(modulePath.substring(projectPath.length() + 1)).toUpperCase();
        }
        return DigestUtils.md5Hex(modulePath).toUpperCase();
    }

    private String getClassId(PyClass pyClass) {


        String path = pyClass.getContainingFile().getVirtualFile().getPath();
        String projectPath = pyClass.getProject().getBasePath();
        String substr = path.substring(projectPath.length() + 1) + "_" + pyClass.getName();

        if (projectPath != null && path.startsWith(projectPath)) {
            return DigestUtils.md5Hex(path.substring(projectPath.length() + 1) + "_" + pyClass.getName()).toUpperCase();
        }
        return DigestUtils.md5Hex(path).toUpperCase();
    }

    public void createModuleElements(Element packageElement, Map<PsiFile, List<PyClass>> moduleMap) {

        for (Map.Entry<PsiFile, List<PyClass>> entry : moduleMap.entrySet()) {
            Element moduleElement = packageElement.addElement(ElementType.MODULE.toString().toLowerCase());
            String moduleId = getModuleId(entry.getKey());
            String moduleName = entry.getKey().getName();
            if (moduleName.endsWith(".py")) {
                moduleName = moduleName.substring(0, moduleName.indexOf(".py"));
            }
            moduleElement.addAttribute(NAME_ATTRIBUTE, moduleName);
            moduleElement.addAttribute(UNIQUE_ID_ATTRIBUTE, moduleId);

            copyExistingSizePosAttributes(moduleId, moduleElement);

            for (PsiElement psiElement : entry.getKey().getChildren()) {
//                System.out.println("psiElement: " + psiElement.getText());


                if (psiElement instanceof PyStatementList statementList) {
                    for (PyStatement statement : statementList.getStatements()) {
                        if (statement instanceof PyAssignmentStatement assignmentStatement) {
                            createAttributeElement(assignmentStatement, moduleElement, false);
                        } else if (statement instanceof PyFunctionImpl functionImpl) {

                            for (PyStatement functionStatement : functionImpl.getStatementList().getStatements()) {
                                if (functionStatement instanceof PyAssignmentStatement varAssignment) {
                                    createAttributeElement(varAssignment, moduleElement, true);
                                }
                            }
                        }
                    }
                } else if (psiElement instanceof PyFunction method) {
                    System.out.println(method);
                    Element methodElement = moduleElement.addElement(METHOD_TAG)
                            .addAttribute(NAME_ATTRIBUTE, method.getName())
                            .addAttribute(CONSTRUCTOR_ATTRIBUTE, "__init__".equals(method.getName()) ? "true" : "false")
                            .addAttribute(MODIFIERS_ATTRIBURE, getFunctionModifiers(method));

                    String returnType = getReturnType(method);
                    if (!"".equals(returnType)) {
                        methodElement.addAttribute(RETURN_TYPE, returnType);
                    }

                    createMethodParameters(methodElement, method);

//                    if (method.getName().toUpperCase().startsWith("TEST") || method.getName().toUpperCase().endsWith("TEST")) {
//                        if (moduleElement.attribute(TEST_ATTRIBUTE) == null || moduleElement.attribute(TEST_ATTRIBUTE).getValue().equals("false")) {
//                            moduleElement.addAttribute(TEST_ATTRIBUTE, "true");
//                        }
//                    }
                    methodElements.put(method, methodElement);
                }

            }

            createClassElements(moduleElement, entry.getValue());
        }
    }

    private void createClassElements(Element moduleElement, List<PyClass> classes, PyClass owner) {

        for (PyClass pyClass : classes) {
            ElementType type = determineClassType(pyClass);
            Element pyClassElement = moduleElement.addElement(type.toString().toLowerCase());
            String classId = getClassId(pyClass);

            pyClassElement.addAttribute(NAME_ATTRIBUTE, pyClass.getName());
            pyClassElement.addAttribute(UNIQUE_ID_ATTRIBUTE, classId);
            pyClassElement.addAttribute(ABSTRACT_ATTRIBUTE, isClassAbstract(pyClass) ? "true" : "false");
            if (owner != null) {
                pyClassElement.addAttribute(OWNER_ATTRIBUTE, getClassId(owner));
            }
            copyExistingSizePosAttributes(classId, pyClassElement);

            createVariableElements(pyClassElement, pyClass);
            createMethodElements(pyClassElement, pyClass);
            createExtendsElements(pyClassElement, pyClass);

            this.elements.put(pyClass, pyClassElement);
            this.elementsById.put(classId, pyClassElement);
            List<String> ids = new ArrayList<>();
            ids.add(getClassId(pyClass));
            this.knownIdentifiers.put(pyClass.getName(), ids);
            if (ElementType.INTERFACE.equals(type)) {
                protocolIds.add(classId);
            }

            createClassElements(moduleElement, Arrays.asList(pyClass.getNestedClasses()), pyClass);
        }
    }

    public void createClassElements(Element moduleElement, List<PyClass> classes) {
        this.createClassElements(moduleElement, classes, null);
    }

    private void resolveTests() {
        for (PyClass test : elements.keySet()) {
            for (PyClass tested : elements.keySet()) {
                if (test.getName() == null || tested.getName() == null || Objects.equals(test.getName(), tested.getName())) continue;
                String testClassNameUpper = test.getName().toUpperCase();
                if (test.getName().contains(tested.getName()) &&
                        (testClassNameUpper.startsWith("TEST") || testClassNameUpper.endsWith("TEST"))) {
                    elements.get(test).addElement(TESTING_TAG).addAttribute(NAME_ATTRIBUTE, tested.getName());
                }
            }

            for (PyFunction testFunction : test.getMethods()) {
                if (testFunction.getName() == null) continue;
                if (testFunction.getName().toUpperCase().startsWith("TEST") || testFunction.getName().toUpperCase().endsWith("TEST")) {
                    Element testElement = elements.get(test);
                    if (testElement.attribute(TEST_ATTRIBUTE) == null || testElement.attribute(TEST_ATTRIBUTE).getValue().equals("false")) {
                        elements.get(test).addAttribute(TEST_ATTRIBUTE, "true");
                    }
                }
            }
        }
    }

    private void createRelations(ArrayList<PyClass> classes, String pkgPath) {
        for (PyClass pyClass : classes) {
            for (PyFunction method : pyClass.getMethods()) {
                Element methodElement = methodElements.get(method);
                if (methodElement != null) {
                    this.createMethodRelations(methodElement, method);
                }
                this.visitPyFunctionReferences(method, methodElement);
                this.fixReferences(methodElement, pkgPath);
            }
            Element classElement = elements.get(pyClass);
            if (classElement != null) {
                this.createClassRelations(classElement, pyClass);
                this.visitPyClassReferences(pyClass, classElement);
                this.fixReferences(classElement, pkgPath);
            }
        }
    }


    private void createVariableElements(Element pyClassElement, PyClass pyClass) {
        for (PsiElement child : pyClass.getChildren()) {
            if (child instanceof PyStatementList statementList) {
                for (PyStatement statement : statementList.getStatements()) {
                    if (statement instanceof PyAssignmentStatement assignmentStatement) {
                        createAttributeElement(assignmentStatement, pyClassElement, false);
                    } else if (statement instanceof PyFunctionImpl functionImpl) {

                        for (PyStatement functionStatement : functionImpl.getStatementList().getStatements()) {
                            if (functionStatement instanceof PyAssignmentStatement varAssignment) {
                                createAttributeElement(varAssignment, pyClassElement, true);
                            }
                        }
                    }
                }
            }
        }
    }

    private void createAttributeElement(PyAssignmentStatement assignment, Element pyClassElement, boolean methodLevel) {
        PyExpression left = assignment.getLeftHandSideExpression();
        if (left == null || (methodLevel && !assignment.getText().trim().startsWith("self"))) return;

        Element attributeElement = pyClassElement.addElement(VARIABLE_TAG);
        attributeElement.addAttribute(NAME_ATTRIBUTE, left.getName());
        if (!left.getText().startsWith("self")) {
            attributeElement.addAttribute(MODIFIERS_ATTRIBURE, STATIC_MODIFIER);
        }
        String type = getFieldType(assignment);
        if (!"".equals(type)) {
            attributeElement.addAttribute(FIELD_TYPE_ATTRIBUTE, type);
        }
    }

    private String getFieldType(PyAssignmentStatement assignmentStatement) {
        String returnType = "";
        if (!assignmentStatement.getText().contains(":")) return returnType;

        returnType = assignmentStatement.getText().substring(assignmentStatement.getText().indexOf(":") + 1);
        if (returnType.contains("=")) {
            return returnType.substring(0, returnType.indexOf("=")).trim();
        }

        return returnType;
    }

    private void createMethodElements(Element pyClassElement, PyClass pyClass) {
//        boolean protocol = determineClassType(pyClass) == ElementType.INTERFACE;
        for (PyFunction method : pyClass.getMethods()) {
            System.out.println(method);
            Element methodElement = pyClassElement.addElement(METHOD_TAG)
                        .addAttribute(NAME_ATTRIBUTE, method.getName())
                        .addAttribute(CONSTRUCTOR_ATTRIBUTE, "__init__".equals(method.getName()) ? "true" : "false")
                        .addAttribute(MODIFIERS_ATTRIBURE, getFunctionModifiers(method));

            String returnType = getReturnType(method);
            if (!"".equals(returnType)) {
                methodElement.addAttribute(RETURN_TYPE, returnType);
            }

            createMethodParameters(methodElement, method);

            if (method.getName().toUpperCase().startsWith("TEST") || method.getName().toUpperCase().endsWith("TEST")) {
                if (pyClassElement.attribute(TEST_ATTRIBUTE) == null || pyClassElement.attribute(TEST_ATTRIBUTE).getValue().equals("false")) {
                    pyClassElement.addAttribute(TEST_ATTRIBUTE, "true");
                }
            }
//            for (PyStatement statement : method.getStatementList().getStatements()) {
//                if (statement instanceof PyTargetExpression variable) {
////                    this.knownIdentifiers.add(variable.getName());
//                }
//            }
            methodElements.put(method, methodElement);

            if (protocolMethodMap.get(getClassId(pyClass)) == null) {
                protocolMethodMap.put(getClassId(pyClass), new HashSet<>());
            }
            protocolMethodMap.get(getClassId(pyClass)).add(method.getName());
        }
    }

    private void createMethodParameters(Element methodElement, PyFunction method) {
        for (PyParameter parameter : method.getParameterList().getParameters()) {
            Element variableElement = methodElement.addElement(PARAMETER_TAG)
                    .addAttribute(NAME_ATTRIBUTE, parameter.getName());
            String type = parameter.getText().substring(parameter.getText().indexOf(":") + 1).trim();
            if (!type.equals(parameter.getText())) {
                variableElement.addAttribute(FIELD_TYPE_ATTRIBUTE, type);
            }
        }
    }

    private void createExtendsElements(Element pyClassElement, PyClass pyClass) {
        for (PyClass ext : pyClass.getSuperClasses(context)) {
            if (!"object".equals(ext.getName())) {
                pyClassElement.addElement(EXTENDS_TAG).setText(getClassId(ext));
            }
        }
    }

    private String getReturnType(PyFunction method) {
        for (String line : method.getText().split("\n")) {
            if (line.startsWith("@")) continue;
            int index = line.indexOf("->");
            if (index == -1) return "";
            String returnType = line.substring(index + 3).trim();
            returnType = returnType.substring(0, returnType.indexOf(":")).trim();
//            this.knownIdentifiers.add(returnType);
            return returnType;
        }
        return "";
    }

    private String getFunctionModifiers(PyFunction method) {
        StringBuilder modifiers = new StringBuilder();

        // Check decorators
        PyDecoratorList decoratorList = method.getDecoratorList();
        if (decoratorList != null) {
            for (PyDecorator decorator : decoratorList.getDecorators()) {
                String name = decorator.getName();
                if ("staticmethod".equals(name)) {
                    modifiers.append(";static");
                } else if ("classmethod".equals(name)) {
                    modifiers.append(";class");
                }
            }
        }

        // Name-based visibility guess (Python convention)
        String methodName = method.getName();
        if (methodName != null) {
            if (methodName.startsWith("__") && !methodName.endsWith("__")) {
                modifiers.append(";private");
            } else if (methodName.startsWith("_")) {
                modifiers.append(";protected");
            } else {
                modifiers.append(";public");
            }
        }

        String result = modifiers.toString();
        return result.startsWith(";") ? result.substring(1) : result;
    }

    private boolean isClassAbstract(PyClass pyClass) {
        if (pyClass.getSuperClassTypes(context).stream().anyMatch(superClass -> superClass != null && "ABC".equals(superClass.getName()))) {
            return true;
        }
        for (PyFunction method : pyClass.getMethods()) {
            if (method.getDecoratorList() == null) continue;
            for (PyDecorator decorator : method.getDecoratorList().getDecorators()) {
                if ("abstractmethod".equals(decorator.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private ElementType determineClassType(PyClass pyClass) {
        for (PyExpression expression : pyClass.getSuperClassExpressions()) {
            if ("Enum".equals(expression.getName()) ||
                "IntEnum".equals(expression.getName()) ||
                expression.getName().toLowerCase().contains("enum")) {

                return ElementType.ENUM;
            } else if ("Protocol".equals(expression.getName())) {
//                if (pyClass.getSuperClassTypes(context).stream().anyMatch(superClass -> "ABC".equals(superClass.getName())));
                return ElementType.INTERFACE;
            }
        }
        return ElementType.CLASS;
    }

    private void createClassRelations(Element classElement, PyClass pyClass) {

        for (PsiElement child : pyClass.getChildren()) {
            if (child instanceof PyStatementList statementList) {
                for (PyStatement statement : statementList.getStatements()) {
                    if (statement instanceof PyAssignmentStatement assignmentStatement) {
                        addRelationForAssignment(classElement, assignmentStatement);
                    }
                }
            }
        }
    }

    private void createMethodRelations(Element methodElement, PyFunction method) {

        for (PyStatement statement : method.getStatementList().getStatements()) {

            if (statement instanceof PyCallExpression callStatement) {
                addRelationTag(methodElement, callStatement);
            } else if (statement instanceof PyAssignmentStatement assignmentStatement) {
                addRelationForAssignment(methodElement, assignmentStatement);
            }
        }

    }

    private void addRelationForAssignment(Element element, PyAssignmentStatement assignmentStatement) {
        PyExpression left = assignmentStatement.getLeftHandSideExpression();
        PyExpression right = assignmentStatement.getAssignedValue();

        if (left != null && right != null) {
            addRelationTag(element, left);
            addRelationTag(element, right);
        }
    }

    private ArrayList<String> parseClassNames(String text) {
        ArrayList<String> classNames = new ArrayList<>();

        // Regular expression pattern to match class names
        Pattern pattern = Pattern.compile("\\b[A-Z][a-zA-Z0-9]*\\b");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String className = matcher.group();

            // Check if the matched className is enclosed in quotes
            if (!isEnclosedInQuotes(text, matcher.start())) {
                classNames.add(className);
            }
        }

        return classNames;
    }

    public static boolean isEnclosedInQuotes(String text, int index) {
        int quoteCount = 0;
        for (int i = 0; i < index; i++) {
            char ch = text.charAt(i);
            if (ch == '"') {
                quoteCount++;
            }
        }
        return quoteCount % 2 == 1;
    }

    private void addRelationTag(Element methodElement, PyExpression exp) {
        if (exp.getName() != null) {
            if (knownIdentifiers.containsKey(exp.getName())) {
                methodElement.addElement(RELATION_TAG).addAttribute(NAME_ATTRIBUTE, exp.getName());
            }
        }
        for (String name : this.parseClassNames(exp.getText())) {
            if (knownIdentifiers.containsKey(name)) {
                methodElement.addElement(RELATION_TAG).addAttribute(NAME_ATTRIBUTE, name);
            }
        }
    }

    public class ReferenceVisitor extends PsiRecursiveElementVisitor {

        private Set<String> existingReferences;
        private Map<String, List<String>> knownIdentifiers;
        private Element element;

        public ReferenceVisitor(Element element, Set<String> existingReferences, Map<String, List<String>> knownIdentifiers) {
            this.existingReferences = existingReferences;
            this.knownIdentifiers = knownIdentifiers;
            this.element = element;
        }

        @Override
        public void visitElement(@NotNull PsiElement psiElement) {
            if (psiElement instanceof PyReferenceExpression) {
                PyReferenceExpression referenceExpression = (PyReferenceExpression) psiElement;
                String reference = referenceExpression.getName();

                if (knownIdentifiers.containsKey(reference) && !existingReferences.contains(reference)) {
                    this.element.addElement(RELATION_TAG).addAttribute(NAME_ATTRIBUTE, reference);
                    existingReferences.add(reference);
                }
            }
            super.visitElement(psiElement);
        }

        private String getReferenceUniqueId(PyReferenceExpression referenceExpression) {
            String path = referenceExpression.getContainingFile().getVirtualFile().getPath();
            String projectPath = referenceExpression.getProject().getBasePath();
            if (projectPath != null && path.startsWith(projectPath)) {
                return path.substring(projectPath.length() + 1);
            }
            return path;
        }
    }

    // Usage for PyClass
    public void visitPyClassReferences(PyClass pyClass, Element classElement) {
        Set<String> existingReferences = getExistingReferencesFromElement(classElement);

        ReferenceVisitor visitor = new ReferenceVisitor(classElement, existingReferences, knownIdentifiers);
        pyClass.acceptChildren(visitor);
    }

    // Usage for PyFunction
    public void visitPyFunctionReferences(PyFunction pyFunction, Element methodElement) {
        Set<String> existingReferences = getExistingReferencesFromElement(methodElement);

        ReferenceVisitor visitor = new ReferenceVisitor(methodElement, existingReferences, knownIdentifiers);
        pyFunction.acceptChildren(visitor);
    }

    private Set<String> getExistingReferencesFromElement(Element element) {
        Set<String> existingReferences = new HashSet<>();
        element.elements().stream().filter(el -> RELATION_TAG.equals(el.getName())).forEach(relElement -> {
            if (relElement.attributeValue(NAME_ATTRIBUTE) != null) {
                existingReferences.add(relElement.attributeValue(NAME_ATTRIBUTE));
            }
        });
        return existingReferences;
    }

    private void fixReferences(Element element, String pkgPath) {
        element.elements().stream().filter(el -> RELATION_TAG.equals(el.getName())).forEach(relElement -> {
            if (relElement.attributeValue(NAME_ATTRIBUTE) != null) {
                String relName = relElement.attributeValue(NAME_ATTRIBUTE);
                if (knownIdentifiers.containsKey(relName)) {
                    List<String> ids = knownIdentifiers.get(relName);
                    String relId = ids.stream().filter(id -> id.startsWith(pkgPath)).findFirst().orElse(ids.get(0));
                    relElement.addAttribute(UNIQUE_ID_ATTRIBUTE, relId);
                }
            }
        });
    }
}
