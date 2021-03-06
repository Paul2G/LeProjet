import database.storage.Employee;
import database.storage.Item;
import database.storage.Move;
import extraPackage.DBCheck;
import extraPackage.NewTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.crypto.Data;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.OK_OPTION;

public class EmpConGUI extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTable empMovesTable;
    private JTable empDebtTable;
    private JButton delDebtButton;

    private JLabel empIDLabel;
    private JLabel empNameLabel;
    private JLabel empRFCLabel;
    private JLabel empDepLabel;
    private JLabel empDebtLabel;

    private NewTableModel regTM = new NewTableModel();
    private NewTableModel invTM = new NewTableModel();

    private Employee emp;
    private int empDebt;

    public EmpConGUI(Employee emp) {
        this.emp = emp;

        /*Inicializacion*/
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        //Inicializando elementos relacionados al empleado
        refreshRegTable();
        refreshDebtTable();

        empDebt = 0;
        for(int i = 0 ; i < empDebtTable.getRowCount(); i++){
            empDebt += (int) invTM.getValueAt(i, 3);
        }

        empIDLabel.setText(emp.getId() + "");
        empNameLabel.setText(emp.getFullName());
        empRFCLabel.setText(emp.getRFC());
        empDebtLabel.setText(empDebt + "");
        if(emp.getDepartment() == null) {
            empDepLabel.setText("Sin departamento asignado");
        }else {
            empDepLabel.setText(emp.getDepartment().getName());
        }


        // call onOK() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        // call onOK() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        delDebtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selection, itemIndex;
                Item itemDB;
                List<Item> itemList = DatabaseUpdate.database.getItems();
                Object[] newRow;

                if(empDebt == 0){
                    JOptionPane.showMessageDialog(
                            null,
                            "El empleado no tiene articulos en posesion que eliminar",
                            "El empleado no debe",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    selection = JOptionPane.showConfirmDialog(
                            null,
                            "\u00BFSeguro que desea liquidar la deuda del empleado...?\n" +
                                    emp.toString() +
                                    "\n\u0024 " + empDebt,
                            "Liquidar deuda",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE
                    );

                    if (selection == OK_OPTION) {
                        //Limpiando lista de deudas
                        for(Item itemDebt: emp.getDebt()){
                            itemIndex = DBCheck.existThisIdIn(itemDebt.getId(), itemList);
                            if(itemIndex != -1 && itemDebt.getQty() != 0){
                                itemDB = itemList.get(itemIndex);
                                itemDB.setTotalQty(itemDB.getTotalQty() - itemDebt.getQty());
                            }
                        }

                        emp.getDebt().clear();

                        //Actualizando tabla y datos
                        refreshDebtTable();
                        empDebt = 0;
                        empDebtLabel.setText(empDebt + "");

                        JOptionPane.showMessageDialog(
                                null,
                                "Los prestamos del empleado han sido saldados por completo",
                                "Exito al liquidar deuda",
                                INFORMATION_MESSAGE
                        );
                    }
                }
            }
        });
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void refreshDebtTable ()
    {
        List<Item> itemList = DatabaseUpdate.database.getItems();
        Item itemDB;
        int itemIndex;
        String columnsName[] = {"Codigo", "Nombre", "Unidades en posesion", "Coste"};
        Object[] newRow;

        //Limpiando para actualizar
        clearTable(empDebtTable);

        //Reincializando tabla
        invTM = new NewTableModel();
        invTM.setColumnIdentifiers(columnsName);
        empDebtTable.setModel(invTM);
        empDebtTable.getTableHeader().setReorderingAllowed(false);

        //Insertando los datos
        for(Item itemDebt: emp.getDebt()){
            itemIndex = DBCheck.existThisIdIn(itemDebt.getId(), itemList);
            if(itemIndex != -1 && itemDebt.getQty() != 0){
                itemDB = itemList.get(itemIndex);
                newRow = new Object[]{itemDB.getId(), itemDB.getName(), itemDebt.getQty(), itemDebt.getQty() * itemDB.getCost()};
                invTM.addRow(newRow);
            }
        }
    }

    private void refreshRegTable ()
    {
        List<Move> moveList = emp.getMoves();
        String columnsName[] = {"Folio", "Fecha", "Articulo", "Cantidad"};
        Object[] newRow;

        Collections.reverse(moveList);

        //Limpiando para actualizar
        clearTable(empMovesTable);

        //Reincializando tabla
        regTM = new NewTableModel();
        regTM.setColumnIdentifiers(columnsName);
        empMovesTable.setModel(regTM);
        empMovesTable.getTableHeader().setReorderingAllowed(false);

        //Insertando los datos
        for(Move mov : moveList ){
            if(mov.getQty() < 0){
                newRow = new Object[]{mov.getId(), mov.getDate(), mov.getItem().getName(),  "[Devolucion]  " + (-mov.getQty())};
            } else {
                newRow = new Object[]{mov.getId(), mov.getDate(), mov.getItem().getName(), "[Prestamo]     " + mov.getQty()};
            }
            regTM.addRow(newRow);
        }

        Collections.reverse(moveList);
    }

    private void clearTable(JTable table) {
        int rows = table.getRowCount();
        DefaultTableModel model;

        for (int i = 0 ; i < rows ; i++){
            model = (DefaultTableModel) table.getModel();
            model.removeRow(0);
        }
    }
}
