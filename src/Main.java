import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import javax.swing.*;
import java.util.Map;

public class Main {
    public static MainWindow ui;

    public static final Map<String, String> ALGORITHMS = Map.ofEntries(
            // Trees
            Map.entry("J48 (Decision Tree)", "weka.classifiers.trees.J48"),
            Map.entry("Random Forest", "weka.classifiers.trees.RandomForest"),
            Map.entry("Random Tree", "weka.classifiers.trees.RandomTree"),
            Map.entry("Decision Stump", "weka.classifiers.trees.DecisionStump"),
            Map.entry("REPTree", "weka.classifiers.trees.REPTree"),

            // Bayes
            Map.entry("Naive Bayes", "weka.classifiers.bayes.NaiveBayes"),
            Map.entry("Bayes Net", "weka.classifiers.bayes.BayesNet"),

            // Lazy
            Map.entry("IBk (KNN)", "weka.classifiers.lazy.IBk"),
            Map.entry("KStar", "weka.classifiers.lazy.KStar"),
            Map.entry("LWL", "weka.classifiers.lazy.LWL"),

            // Functions
            Map.entry("Logistic Regression", "weka.classifiers.functions.Logistic"),
            Map.entry("SMO (SVM)", "weka.classifiers.functions.SMO"),

            // Rules
            Map.entry("ZeroR", "weka.classifiers.rules.ZeroR"),
            Map.entry("OneR", "weka.classifiers.rules.OneR"),
            Map.entry("PART", "weka.classifiers.rules.PART"),
            Map.entry("JRip (RIPPER)", "weka.classifiers.rules.JRip"),
            Map.entry("Decision Table", "weka.classifiers.rules.DecisionTable"),

            // Meta
            Map.entry("AdaBoostM1", "weka.classifiers.meta.AdaBoostM1"),
            Map.entry("Bagging", "weka.classifiers.meta.Bagging"),
            Map.entry("LogitBoost", "weka.classifiers.meta.LogitBoost"),
            Map.entry("Random Committee", "weka.classifiers.meta.RandomCommittee"),
            Map.entry("Random SubSpace", "weka.classifiers.meta.RandomSubSpace")
    );

    public static final Map<String, Double> SPLITS = Map.of(
            "70% Train / 30% Test", 0.7,
            "80% Train / 20% Test", 0.8,
            "60% Train / 40% Test", 0.6,
            "90% Train / 10% Test", 0.9
    );

    public static void main(String[] args) {
        JFrame frame = new JFrame("SUINZ App");

        ui = new MainWindow();
        ui.fillAlgorithms(ALGORITHMS);
        ui.fillSplits(SPLITS);
        ui.setCallback(Main::onTrain);
        ui.outputArea.setEditable(false);
        frame.setSize(900, 700);
        frame.setContentPane(ui.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void onTrain() {
        if (!ui.hasSelectedFile()) {
            ui.outputArea.setText("⚠ No dataset selected.\nPlease import a dataset first.");
            return;
        }

        try {
            // 1. Load dataset
            Instances data = ConverterUtils.DataSource.read(ui.selectedFile.getAbsolutePath());
            if (data.classIndex() == -1)
                data.setClassIndex(data.numAttributes() - 1);

            // 2. Get selected algorithm
            String algoName = (String) ui.algoCombo.getSelectedItem();
            String algoClass = ALGORITHMS.get(algoName);

            // 3. Instantiate classifier
            Classifier cls = (Classifier) Class.forName(algoClass).newInstance();

            // 4. Get training split
            String splitName = (String) ui.testCombo.getSelectedItem();
            double trainPercent = SPLITS.get(splitName);

            int trainSize = (int) Math.round(data.numInstances() * trainPercent);
            int testSize = data.numInstances() - trainSize;

            Instances train = new Instances(data, 0, trainSize);
            Instances test = new Instances(data, trainSize, testSize);

            // 5. Train model
            long start = System.currentTimeMillis();
            cls.buildClassifier(train);
            long end = System.currentTimeMillis();

            // 6. Evaluate
            Evaluation eval = new Evaluation(train);
            eval.evaluateModel(cls, test);

            // 7. Print results to output area
            ui.outputArea.setText("");   // clear previous output

            ui.outputArea.append("=== Training Completed ===\n");
            ui.outputArea.append("Algorithm: " + algoName + "\n");
            ui.outputArea.append("Training Split: " + splitName + "\n");
            ui.outputArea.append("Time: " + (end - start) + " ms\n\n");

            ui.outputArea.append(eval.toSummaryString() + "\n");
            ui.outputArea.append(eval.toClassDetailsString() + "\n");
            ui.outputArea.append(eval.toMatrixString() + "\n");

        } catch (Exception ex) {
            ui.outputArea.setText("❌ ERROR during training:\n\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}