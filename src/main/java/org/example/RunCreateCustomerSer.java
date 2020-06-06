package org.example;

public class RunCreateCustomerSer
{
    public static void main(String [] args)
    {

        defaultGUILook();
        CarRentalGUI gui = new CarRentalGUI();
        gui.setVisible(true);

        CreateCustomerSer runCustSer = new CreateCustomerSer();
        runCustSer.openFile();
        runCustSer.writeObjects();
        runCustSer.closeFile();
    }

    public static void defaultGUILook(){
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException: " + ex.getMessage());
        } catch (InstantiationException ex) {
            System.out.println("InstantiationException: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.out.println("IllegalAccessException: " + ex.getMessage());
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            System.out.println("UnsupportedLookAndFeelException: " + ex.getMessage());
        }
    }


}
