import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.Map;

public class MainWindow {
    private Callback callback;

    public JPanel root;
    private JButton importBtn;
    private JTextField dataPath;
    JComboBox algoCombo;
    JComboBox testCombo;
    private JButton trainBtn;
    JTextArea outputArea;

    public File selectedFile;

    public boolean hasSelectedFile()
    {
        return selectedFile != null;
    }

    public void setCallback(Callback callback)
    {
        this.callback = callback;
    }

    public void fillAlgorithms(Map<String, String> algoMap) {
        algoCombo.removeAllItems();
        for (String key : algoMap.keySet()) {
            algoCombo.addItem(key);
        }
    }

    public void fillSplits(Map<String, Double> splitMap) {
        testCombo.removeAllItems();
        for (String key : splitMap.keySet()) {
            testCombo.addItem(key);
        }
    }

    public MainWindow() {
        // constructor where you add listeners later

        importBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();

            FileNameExtensionFilter filter =
                    new FileNameExtensionFilter("Datasets (.arff, .csv)", "arff", "csv");
            chooser.setFileFilter(filter);

            int result = chooser.showOpenDialog(root);

            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                dataPath.setText(selectedFile.getAbsolutePath());
            }
        });

        trainBtn.addActionListener(e -> {
            if (callback != null) {
                callback.onTrain();
            }
        });
    }
}
