package de.fraunhofer.iosb.ilt.configurableexample;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;

/**
 *
 * @author scf
 */
public class ExampleGui extends javax.swing.JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleGui.class);
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel panelEditor;
    private ConfigEditor editor;

    /**
     * Creates new form ExampleGui
     */
    public ExampleGui() {
        createGui();
    }

    private void createGui() {
        java.awt.GridBagConstraints gridBagConstraints;

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        panelEditor = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jButton1.setText("Paint");
        jButton1.addActionListener((ActionEvent e) -> {
            useConfig();
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.01;
        getContentPane().add(jButton1, gridBagConstraints);

        jButton2.setText("PrintConfig");
        jButton2.addActionListener((ActionEvent e) -> {
            printConfig();
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.01;
        getContentPane().add(jButton2, gridBagConstraints);

        panelEditor.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        getContentPane().add(panelEditor, gridBagConstraints);

        addEditorToGui(panelEditor);
        pack();

    }

    private void addEditorToGui(JPanel parentPanel) {
        FlagShapeList flag = new FlagShapeList();
        editor = flag.getConfigEditor(null, null);
        panelEditor.add(editor.getGuiFactorySwing().getComponent());
    }

    private void useConfig() {
        JsonElement config = editor.getConfig();
        FlagShapeList flag = new FlagShapeList();
        flag.configure(config, null, null);
        flag.wave();
    }

    private void printConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement config = editor.getConfig();
        String jsonString = gson.toJson(config);
        LOGGER.info("Our configuration is:\n{}", jsonString);
    }

    public void loadConfig(String jsonString) {
        JsonElement config = new JsonParser().parse(jsonString);
        editor.setConfig(config);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            ExampleGui exampleGui = new ExampleGui();
            exampleGui.setVisible(true);
            exampleGui.setSize(400, 300);
        });
    }

}
