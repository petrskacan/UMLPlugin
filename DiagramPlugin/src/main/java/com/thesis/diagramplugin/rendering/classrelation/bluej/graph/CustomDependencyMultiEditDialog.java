package com.thesis.diagramplugin.rendering.classrelation.bluej.graph;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.CustomDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.DependencyType;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.Target;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.*;

public class CustomDependencyMultiEditDialog extends JDialog {
    private JTable dependencyTable;
    private final DependencyTableModel tableModel;
    private static final HashMap<String, String> TARGET2TARGET = new HashMap<>();
    private Package pkg;
    private String path;
    private String[] packageNames; // Array of package names for the combo boxes.

    public CustomDependencyMultiEditDialog(Package pkg) {
        this.pkg = pkg;

        // Derive a common path (similar to your creation dialog).

        // Build the list of package names from available class names.
        Set<String> packageNamesSet = new LinkedHashSet<>();
        for(Target target :  pkg.getTargets().getAllTargets())
        {
            if(!target.getDisplayName().contains(".")) {
                TARGET2TARGET.put(target.getDisplayName(), target.getIdentifierName());
                packageNamesSet.add(target.getDisplayName());
            }
        }
        packageNames = packageNamesSet.toArray(new String[0]);

        // Get the list of current dependencies.
        List<CustomDependency> dependencies = CustomDependency.getCustomDependencies();

        // Create the table model.
        tableModel = new DependencyTableModel(dependencies, pkg, path);
        dependencyTable = new JBTable(tableModel);

        // For the "From Package" and "To Package" columns, use combo box cell editors.
        JComboBox<String> fromEditor = new ComboBox<>(packageNames);
        dependencyTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(fromEditor));

        JComboBox<String> toEditor = new ComboBox<>(packageNames);
        dependencyTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(toEditor));

        // For the "Dependency Type" column, use a combo box cell editor.
        JComboBox<DependencyType> typeEditor = new ComboBox<>(DependencyType.values());
        dependencyTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(typeEditor));

        // For the "Remove" column, set up a centered checkbox.
        dependencyTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                checkBox.setSelected(value != null && (Boolean) value);
                if (isSelected) {
                    checkBox.setBackground(table.getSelectionBackground());
                } else {
                    checkBox.setBackground(table.getBackground());
                }
                return checkBox;
            }
        });
        JCheckBox checkBoxEditor = new JCheckBox();
        checkBoxEditor.setHorizontalAlignment(SwingConstants.CENTER);
        dependencyTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(checkBoxEditor));

        JScrollPane scrollPane = new JBScrollPane(dependencyTable);

        // Create Save and Cancel buttons.
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            tableModel.saveChanges();
            dispose();
        });
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dependencyTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.setTitle("Manage Custom Dependencies");
        this.setPreferredSize(new Dimension(800, 600));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setModal(true);
        this.setVisible(true);
    }

    // Custom table model that holds the editable data for each dependency.
    private static class DependencyTableModel extends AbstractTableModel {
        // Updated columns: Name (non-editable), From Package, To Package, Dependency Type, Remove.
        private final String[] columnNames = {"Name", "From Package", "To Package", "Dependency Type", "Remove"};
        private final List<RowData> rows;
        private final Package pkg;
        private final String path;

        public DependencyTableModel(List<CustomDependency> dependencies, Package pkg, String path) {
            this.pkg = pkg;
            this.path = path;
            rows = new ArrayList<>();
            for (CustomDependency dep : dependencies) {
                rows.add(new RowData(
                        dep.getFrom().getDisplayName(),
                        dep.getTo().getDisplayName(),
                        dep.getType(),
                        false,
                        dep
                ));
            }
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        // Return appropriate class types for each column.
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0 -> // Name column
                        String.class; // From Package
                case 1, 2 -> // To Package
                        String.class;
                case 3 -> // Dependency Type
                        DependencyType.class;
                case 4 -> // Remove
                        Boolean.class;
                default -> Object.class;
            };
        }

        // Only allow editing for columns 1 (From), 2 (To), 3 (Type), and 4 (Remove).
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            RowData row = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 ->
                    // Compute the dependency name based on current values.
                        row.originalDependency.toString();
                case 1 -> row.from;
                case 2 -> row.to;
                case 3 -> row.type;
                case 4 -> row.remove;
                default -> null;
            };
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            RowData row = rows.get(rowIndex);
            switch (columnIndex) {
                case 1 -> row.from = (String) aValue;
                case 2 -> row.to = (String) aValue;
                case 3 -> row.type = (DependencyType) aValue;
                case 4 -> row.remove = (Boolean) aValue;
            }
            // Refresh the entire row so the computed "Name" is updated.
            fireTableRowsUpdated(rowIndex, rowIndex);
        }

        // Applies pending changes: updates dependencies or removes them.
        public void saveChanges() {
            for (RowData row : rows) {
                CustomDependency dep = row.originalDependency;
                if (row.remove) {
                    CustomDependency.removeCustomDependency(dep);
                    dep.getDependency().setVisible(false);
                }else {
                    // Compare current dependency values with edited values.
                    String currentFrom = dep.getFrom().getDisplayName();
                    String currentTo = dep.getTo().getDisplayName();
                    DependencyType currentType = dep.getType();

                    // Only update if there's an actual change.
                    if (!row.from.equals(currentFrom) || !row.to.equals(currentTo) || !row.type.equals(currentType)) {
                        DependentTarget newFrom = pkg.getDependentTarget(TARGET2TARGET.get(row.from));
                        DependentTarget newTo = pkg.getDependentTarget(TARGET2TARGET.get(row.to));
                        if(!row.type.equals(currentType))
                        {
                            CustomDependency.removeCustomDependency(dep);
                            dep.getDependency().setVisible(false);
                            new CustomDependency(pkg, newFrom,newTo, row.type);
                        }
                        else
                        {
                            dep.getDependency().setFrom(newFrom);
                            dep.getDependency().setTo(newTo);
                        }
                    }
                }
            }
        }

        // Helper class to store a row's data.
        private static class RowData {
            String from;
            String to;
            DependencyType type;
            boolean remove;
            CustomDependency originalDependency;

            public RowData(String from, String to, DependencyType type, boolean remove, CustomDependency originalDependency) {
                this.from = from;
                this.to = to;
                this.type = type;
                this.remove = remove;
                this.originalDependency = originalDependency;
            }
        }
    }
}
