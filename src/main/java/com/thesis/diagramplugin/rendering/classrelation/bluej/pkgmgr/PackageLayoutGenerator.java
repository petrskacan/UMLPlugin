package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.*;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.role.ModuleRole;

import java.util.*;
import java.util.stream.Collectors;

public class PackageLayoutGenerator {

    public static final int PACKAGE_WINDOW_MARGIN = 30;
    public static final int PACKAGE_CONTENT_MARGIN = 40;
    public static final int CONTENT_MARGIN = 50;

    Map<PackageTarget, List<DependentTarget>> packages;
    TargetCollection targets;
    Map<ClassTarget, List<DependentTarget>> modules;

    public void generateLayout(Map<PackageTarget, List<DependentTarget>> packages, TargetCollection targets, Map<ClassTarget, List<DependentTarget>> modules) {
        PackageTarget firstPkg = packages.keySet().stream().findFirst().orElse(null);
        if (firstPkg == null) {
            return;
        }
        this.packages = packages;
        this.targets = targets;
        this.modules = modules;

        // packages that haven't been placed yet
        Set<PackageTarget> packagesToPlace = packages.keySet().stream().filter(ClassTarget::layoutGenerationEnabled).collect(Collectors.toSet());
        // packages that have been placed
        Set<PackageTarget> existingPackages = packages.keySet().stream().filter(pkg -> !pkg.layoutGenerationEnabled()).collect(Collectors.toSet());

        // start position where to place modules
        int x = findXForNewPackage(existingPackages) + PACKAGE_WINDOW_MARGIN;
        int y = PACKAGE_WINDOW_MARGIN;

        // place existing packages
        for (PackageTarget pkgToPlace : packagesToPlace) {
            List<DependentTarget> packageContents = packages.get(pkgToPlace);
            // collect modules in this package
            List<ClassTarget> pkgModules = packageContents.stream().filter(target -> ((ClassTarget)target).getRole() instanceof ModuleRole).map(target -> (ClassTarget)target).toList();
            if (pkgModules.isEmpty()) {
                generateLayoutWithoutModules(x, y, pkgToPlace);
            } else {
                // with modules
                x = generateLayoutWithModules(x, y, pkgToPlace, pkgModules) + PACKAGE_WINDOW_MARGIN;
                y = PACKAGE_WINDOW_MARGIN;
            }
        }

        // check the existing packages for possible new content
        for (PackageTarget existingPkg : existingPackages) {
            List<DependentTarget> packageContents = packages.get(existingPkg);

            // collect modules in this package
            List<ClassTarget> pkgModules = packageContents.stream().filter(target -> ((ClassTarget)target).getRole() instanceof ModuleRole).map(target -> (ClassTarget)target).toList();
            if (pkgModules.isEmpty()) {
                placeNewClassesWithoutModules(existingPkg);
            } else {
                placeExistingAndNewModules(existingPkg, pkgModules);
            }
        }
    }

    private void generateLayoutWithoutModules(int pkgX, int pkgY, PackageTarget packageTarget) {
        List<ClassTarget> classes = targets.getAllTargets().stream().filter(t -> ! (t instanceof PackageTarget)).filter(t -> t instanceof ClassTarget).map(t -> (ClassTarget) t).toList();

        int gridMaxWidth = (int) Math.min(Math.ceil(Math.sqrt(classes.size())), 6);
        int gridX = 0;

        int x = pkgX + PACKAGE_CONTENT_MARGIN;
        int y = pkgY + packageTarget.getHeaderHeight() + PACKAGE_CONTENT_MARGIN;

        int maxX = x;
        int maxY = y;

        for (ClassTarget ct : classes) {
            ct.setPos(x, y);

            maxY = Math.max(maxY, y + ct.getHeight());
            maxX = Math.max(maxX, x + ct.getWidth());
            if (++gridX >= gridMaxWidth) {
                gridX = 0;
                x = pkgX + packageTarget.getHeaderHeight() + PACKAGE_CONTENT_MARGIN;
                y += ct.getHeight() + CONTENT_MARGIN;
            } else {
                x += ct.getWidth() + CONTENT_MARGIN;
            }
        }

        int pkgWidth = maxX + PACKAGE_CONTENT_MARGIN - pkgX;
        int pkgHeight = maxY + PACKAGE_CONTENT_MARGIN - pkgY;
        packageTarget.setPos(pkgX, pkgY);
        packageTarget.setSize(pkgWidth, pkgHeight);
    }

    private void placeNewClassesWithoutModules(PackageTarget existingPkg) {
        List<ClassTarget> classes = targets.getAllTargets().stream().filter(t -> ! (t instanceof PackageTarget)).filter(t -> t instanceof ClassTarget).map(t -> (ClassTarget) t).toList();

        int x = 0;
        int y = existingPkg.getY() + existingPkg.getHeaderHeight() + PACKAGE_CONTENT_MARGIN;
        for (ClassTarget ct : classes) {
            x = Math.max(x, ct.getX() + ct.getWidth() + CONTENT_MARGIN);
        }

        int maxX = 0, maxY = 0;
        List<ClassTarget> classesToPlace = classes.stream().filter(ClassTarget::layoutGenerationEnabled).toList();
        for (ClassTarget clsToPlace : classesToPlace) {
            clsToPlace.setPos(x, y);

            maxY = Math.max(maxY, y + clsToPlace.getHeight() + PACKAGE_CONTENT_MARGIN);
            maxX = Math.max(maxX, x + clsToPlace.getWidth() + PACKAGE_CONTENT_MARGIN);

            y += clsToPlace.getHeight() + CONTENT_MARGIN;
        }

        fixPackageBounds(existingPkg, maxX, maxY);
    }

    private void placeExistingAndNewModules(PackageTarget existingPkg, List<ClassTarget> moduleTargets) {
        List<ClassTarget> existingModules = moduleTargets.stream().filter(module -> !module.layoutGenerationEnabled()).toList();
        List<ClassTarget> modulesToPlace = moduleTargets.stream().filter(ClassTarget::layoutGenerationEnabled).toList();

        // existing modules -> place new packages
        for (ClassTarget existingModule : existingModules) {
            placeNewClasses(existingPkg, existingModule);
        }

        // place new modules and packages
        placeNewModules(existingPkg, modulesToPlace, existingModules);

    }

    private void placeNewModules(PackageTarget existingPkg, List<ClassTarget> modulesToPlace, List<ClassTarget> existingModules) {
        int x = 0, y = 0;
        int rightBound = existingPkg.getX() + existingPkg.getWidth();
        int lowerBound = existingPkg.getY() + existingPkg.getHeight();
        x = findXForModule(existingModules);
        for (ClassTarget newModule : modulesToPlace ) {
            y = existingPkg.getY() + existingPkg.getHeaderHeight() + PACKAGE_CONTENT_MARGIN;

            // compute positions and place the modules and class targets ==> call setPos() and setSize() on targets

            newModule.setPos(x, y);

            y += newModule.getHeight();
            rightBound = Math.max(rightBound, x + newModule.getWidth() + PACKAGE_CONTENT_MARGIN);
            lowerBound = Math.max(lowerBound, y + PACKAGE_CONTENT_MARGIN);

            List<ClassTarget> moduleClasses = modules.containsKey(newModule) ? modules.get(newModule).stream().map(target -> (ClassTarget) target).toList() : new ArrayList<>();

            int columnMaxX = newModule.getX() + newModule.getWidth();
            // process module's content
            for (ClassTarget clsTarget : moduleClasses) {

                clsTarget.setPos(x, y + CONTENT_MARGIN);
                y += CONTENT_MARGIN + clsTarget.getHeight();
                rightBound = Math.max(rightBound, x + clsTarget.getWidth() + PACKAGE_CONTENT_MARGIN);
                lowerBound = Math.max(lowerBound, y + PACKAGE_CONTENT_MARGIN);
                columnMaxX = Math.max(columnMaxX, clsTarget.getX() + clsTarget.getWidth());
            }

            x = columnMaxX + CONTENT_MARGIN;
        }

        // fix package size if contents went out of bounds
        fixPackageBounds(existingPkg, rightBound, lowerBound);
    }

    private int findXForModule(List<ClassTarget> existingModules) {
        int x = 0;
        for (ClassTarget module : existingModules) {
            x = Math.max(x, module.getX() + module.getWidth());
            List<DependentTarget> classes = modules.get(module);
            if (classes != null) {
                for (DependentTarget dt : classes) {
                    if (dt instanceof ClassTarget classTarget) {
                        x = Math.max(x, classTarget.getX() + classTarget.getWidth());
                    }
                }
            }
        }
        return x + CONTENT_MARGIN;
    }

    private void placeNewClasses(PackageTarget existingPkg, ClassTarget existingModule) {
        List<DependentTarget> tmp = modules.get(existingModule);
        if (tmp == null) return;
        List<ClassTarget> existingClasses = tmp.stream().map(dt -> (ClassTarget)dt).filter(ct -> !ct.layoutGenerationEnabled()).toList();
        List<ClassTarget> classesToPlace = tmp.stream().map(dt -> (ClassTarget)dt).filter(ClassTarget::layoutGenerationEnabled).toList();

        int lowerBound = existingPkg.getY() + existingPkg.getHeight();
        int rightBound = existingPkg.getX() + existingPkg.getWidth();

        int y = findYForNewClass(existingModule, existingClasses) + CONTENT_MARGIN;
        int moduleCenter = existingModule.getX() + existingModule.getWidth() / 2;
        for (ClassTarget ct : classesToPlace) {
            int x = moduleCenter - ct.getWidth() / 2;
            ct.setPos(x, y);
            y += ct.getHeight() + CONTENT_MARGIN;
            lowerBound = Math.max(lowerBound, y);
            rightBound = Math.max(rightBound, x + ct.getWidth() /2);
        }

        // fix package size if contents went out of bounds
        fixPackageBounds(existingPkg, rightBound, lowerBound);
    }

    private void fixPackageBounds(PackageTarget packageTarget, int rightBound, int lowerBound) {
        // fix package size if contents went out of bounds
        if (rightBound > packageTarget.getX() + packageTarget.getWidth()) {
            packageTarget.setSize(rightBound - packageTarget.getX(), packageTarget.getHeight());
        }
        if (lowerBound > packageTarget.getY() + packageTarget.getHeight()) {
            packageTarget.setSize(packageTarget.getWidth(), lowerBound - packageTarget.getY());
        }
    }

    private int findYForNewClass(ClassTarget existingModule, List<ClassTarget> classTargets) {

        int y = existingModule.getY() + existingModule.getHeight();

        for (ClassTarget ct : classTargets) {
            y = Math.max(y, ct.getY()) + ct.getHeight();
        }

        return y;
    }

    private int findXForNewPackage(Set<PackageTarget> existingPackages) {
        int x = 0;
        for (PackageTarget pkg : existingPackages) {
            x = Math.max(x, pkg.getX() + pkg.getWidth());
        }
        return x;
    }

    // x, y == position to place the top-left corner of the package
    private int generateLayoutWithModules(int pkgX, int pkgY, PackageTarget packageTarget, List<ClassTarget> pkgModules) {
        // compute widths and height to find out the package position and dimensions
        HashMap<ClassTarget, Integer> columnWidths = new HashMap<>();
        int maxHeight = this.computeColumnWidthsForModules(columnWidths, pkgModules, packageTarget);

        // compute package width
        int pkgwidth = 2*PACKAGE_CONTENT_MARGIN + (columnWidths.values().size()-1)*CONTENT_MARGIN;
        for (Integer w : columnWidths.values()) {
            pkgwidth += w;
        }

        // set package size and placement
        if ((packageTarget.getX() == 0 && packageTarget.getY() == 0) || packageTarget.layoutGenerationEnabled()) {
            packageTarget.setPos(pkgX, pkgY);
            packageTarget.setSize(pkgwidth, packageTarget.getHeaderHeight() + maxHeight + 2 * PACKAGE_CONTENT_MARGIN);
            System.out.println("LAYOUT GENERATOR - GENERATING LAYOUT FOR " + packageTarget.getIdentifierName());
        } else {
            System.out.println("LAYOUT GENERATOR - SKIPPING " + packageTarget.getIdentifierName());
        }

        // set start x, y for the position of package contents
        int x = pkgX + PACKAGE_CONTENT_MARGIN;
        int y = pkgY + packageTarget.getHeaderHeight() + PACKAGE_CONTENT_MARGIN;
        // compute positions and place the modules and class targets ==> call setPos() and setSize() on targets
        for (ClassTarget moduleTarget : pkgModules) {

            int modulewidth = columnWidths.getOrDefault(moduleTarget, moduleTarget.getWidth());
            this.placeTargetIntoColumn(moduleTarget, x, y, modulewidth);
            y += moduleTarget.getHeight() + CONTENT_MARGIN;


            List<ClassTarget> moduleClasses = modules.containsKey(moduleTarget) ? modules.get(moduleTarget).stream().map(target -> (ClassTarget) target).toList() : new ArrayList<>();;
            for (ClassTarget clsTarget : moduleClasses) {

                this.placeTargetIntoColumn(clsTarget, x, y, modulewidth);
                y += CONTENT_MARGIN + clsTarget.getHeight();
            }

            x += modulewidth + CONTENT_MARGIN;
            y = pkgY + packageTarget.getHeaderHeight() + PACKAGE_CONTENT_MARGIN;
        }
        return x;
    }

    private void placeTargetIntoColumn(ClassTarget clsTarget, int x, int y, int modulewidth) {
        if ((clsTarget.getX() == 0 && clsTarget.getY() == 0) || clsTarget.layoutGenerationEnabled()) {
            if (clsTarget.getWidth() >= modulewidth) {
                clsTarget.setPos(x, y);
            } else {
                int offset = x + (modulewidth - clsTarget.getWidth())/2;
                clsTarget.setPos(offset, y);
            }
            System.out.println("LAYOUT GENERATOR - GENERATING LAYOUT FOR " + clsTarget.getIdentifierName());
        } else {
            System.out.println("LAYOUT GENERATOR - SKIPPING " + clsTarget.getIdentifierName());
        }
    }

    private int computeColumnWidthsForModules(HashMap<ClassTarget, Integer> columnWidths, List<ClassTarget> pkgModules, PackageTarget packageTarget) {
        // count column widths and height for package
        int maxHeight = packageTarget.getBodyHeight();

        // for every module iterate through its contents to compute total height and width
        for (ClassTarget moduleTarget : pkgModules) {
            // find module contents (contained classes)
            List<ClassTarget> moduleClasses = null;
            if (modules.containsKey(moduleTarget)) {
                moduleClasses = modules.get(moduleTarget).stream().map(target -> (ClassTarget) target).toList();
            } else {
                moduleClasses = new ArrayList<>();
            }

            // set starting width + height to the module size
            int columnWidth = moduleTarget.getWidth();
            int columnHeight = moduleTarget.getHeight();

            // iterate over modules content and add height, store max width
            for (ClassTarget clsTarget : moduleClasses) {
                columnWidth = Math.max(clsTarget.getWidth(), columnWidth);
                columnHeight += +CONTENT_MARGIN + clsTarget.getHeight();
            }
            columnWidths.put(moduleTarget, columnWidth);
            maxHeight = Math.max(maxHeight, columnHeight);
        }
        return maxHeight;
    }
}
