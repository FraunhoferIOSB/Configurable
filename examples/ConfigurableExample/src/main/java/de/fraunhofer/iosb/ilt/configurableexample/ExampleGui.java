package de.fraunhofer.iosb.ilt.configurableexample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.ConfigurationException;
import de.fraunhofer.iosb.ilt.configurable.Reflection;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author scf
 */
public class ExampleGui extends javax.swing.JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleGui.class);
    private JTextArea jsonTextArea;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonToJson;
    private javax.swing.JButton jButtonFromJson;
    private javax.swing.JPanel panelEditor;
    private ConfigEditor editor;

    /**
     * Creates new form ExampleGui
     */
    public ExampleGui() {
        createGui();
    }

    private void createGui() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.BorderLayout());

        GridBagConstraints gridBagConstraints;

        JSplitPane splitPane1 = new javax.swing.JSplitPane();
        JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        jsonTextArea = new javax.swing.JTextArea();

        splitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane1.setResizeWeight(0.5);
        jScrollPane1.setViewportView(jsonTextArea);
        splitPane1.setBottomComponent(jScrollPane1);

        JPanel panelTop = new JPanel();
        panelTop.setLayout(new java.awt.GridBagLayout());

        jButton1 = new javax.swing.JButton();
        jButtonToJson = new javax.swing.JButton();
        jButtonFromJson = new javax.swing.JButton();
        panelEditor = new javax.swing.JPanel();

        jButton1.setText("Wave");
        jButton1.addActionListener((ActionEvent e) -> {
            useConfig();
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.01;
        panelTop.add(jButton1, gridBagConstraints);

        jButtonToJson.setText("To JSON");
        jButtonToJson.addActionListener((ActionEvent e) -> {
            printConfig();
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 0.01;
        panelTop.add(jButtonToJson, gridBagConstraints);

        jButtonFromJson.setText("Load JSON");
        jButtonFromJson.addActionListener((ActionEvent e) -> {
            loadConfig();
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.01;
        panelTop.add(jButtonFromJson, gridBagConstraints);

        panelEditor.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        panelTop.add(panelEditor, gridBagConstraints);

        addEditorToGui(panelEditor);

        splitPane1.setTopComponent(panelTop);
        getContentPane().add(splitPane1, java.awt.BorderLayout.CENTER);
        pack();
    }

    private void addEditorToGui(JPanel parentPanel) {
        FlagShapeList flag = new FlagShapeList();
        editor = flag.getConfigEditor(null, null);
        panelEditor.add(editor.getGuiFactorySwing().getComponent());
    }

    private void useConfig() {
        try {
            JsonElement config = editor.getConfig();
            FlagShapeList flag = new FlagShapeList();
            flag.configure(config, null, null);
            flag.wave();
        } catch (ConfigurationException ex) {
            LOGGER.error("Could not configure the flag!", ex);
        }
    }

    private void printConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement config = editor.getConfig();
        String jsonString = gson.toJson(config);
        jsonTextArea.setText(jsonString);
        LOGGER.info("Our configuration is:\n{}", jsonString);
    }

    public void loadConfig() {
        loadConfig(jsonTextArea.getText());
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
        // Do classpath scanning in the background.
        new Thread(() -> {
            Reflection.getReflections();
        }).start();
    }

}
