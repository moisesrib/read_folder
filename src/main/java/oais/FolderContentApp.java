package oais;

import oais.service.RegexService;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FolderContentApp extends JFrame {
    private static final String ICON_CHECK = "/icons/check.png";
    private static final String ICON_ERROR = "/icons/error.png";
    private static final int ICON_SIZE = 16;
    private static final String TITLE_FOLDER_INFO = "Informações da Pasta";
    private static final String TITLE_CONTENT_INFO = "Conteúdo da Pasta";
    private static final String TITLE_DROP_AREA = "Arraste e solte a pasta aqui";
    private static final String JPG_FILE_PATTERN = "00001-\\d+\\.jpg";
    private static final int MIN_FILE_SIZE_MB = 1048576;

    private JPanel folderInfoPanel;
    private JLabel infoNameJpgLabel, responseNameLabel, infoLengthJpgLabel, responseLengthLabel, jpgCountLabel;
    private Icon iconChecked, iconCross;
    private int rowsInFolderPanel;

    public FolderContentApp() {
        super("Identificador de Pasta");
        configureFrame();
        initializeComponents();
    }

    private void configureFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeComponents() {
        iconChecked = resizeIcon(new ImageIcon(getClass().getResource(ICON_CHECK)), ICON_SIZE, ICON_SIZE);
        iconCross = resizeIcon(new ImageIcon(getClass().getResource(ICON_ERROR)), ICON_SIZE, ICON_SIZE);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        folderInfoPanel = new JPanel();
        folderInfoPanel.setBorder(new TitledBorder(TITLE_FOLDER_INFO));

        JPanel contentInfoPanel = createContentInfoPanel();
        JPanel dropArea = createDropArea();

        mainPanel.add(folderInfoPanel);
        mainPanel.add(contentInfoPanel);
        add(mainPanel, BorderLayout.CENTER);
        add(dropArea, BorderLayout.NORTH);
    }

    private JPanel createContentInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));
        panel.setBorder(new TitledBorder(TITLE_CONTENT_INFO));

        infoNameJpgLabel = new JLabel();
        responseNameLabel = new JLabel();
        infoLengthJpgLabel = new JLabel();
        responseLengthLabel = new JLabel();
        jpgCountLabel = new JLabel();

        panel.add(infoNameJpgLabel);
        panel.add(responseNameLabel);
        panel.add(infoLengthJpgLabel);
        panel.add(responseLengthLabel);
        panel.add(jpgCountLabel);

        return panel;
    }

    private JPanel createDropArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(TITLE_DROP_AREA));
        panel.setPreferredSize(new Dimension(800, 50));

        JLabel label = new JLabel(TITLE_DROP_AREA, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(label, BorderLayout.CENTER);
        panel.setTransferHandler(createTransferHandler());

        return panel;
    }

    private TransferHandler createTransferHandler() {
        return new TransferHandler() {
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            public boolean importData(TransferSupport support) {
                try {
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty() && files.getFirst().isDirectory()) {
                        processFolder(files.getFirst());
                    }
                    return true;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(FolderContentApp.this, "Erro ao processar a pasta: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        };
    }

    private void processFolder(File folder) {
        String[] parts = folder.getName().split("_");
        rowsInFolderPanel = 0; // Reset do contador de linhas para o GridLayout

        initializeFolderLabels(parts, folder);
        updateLayout();
        updateJpgCount(folder);
    }

    private void initializeFolderLabels(String[] parts, File folder) {
        folderInfoPanel.removeAll();

        try {
            String typeFiche = RegexService.typeFiche(parts[0]);
            String fpsFiche = RegexService.fpsFiche(parts[1]);
            String numberFiche = RegexService.numberFiche(parts[2]);
            String pasepFiche = RegexService.numberPasep(parts[3]);
            String dateFicheFinal = RegexService.dateFiche(parts[6], parts[5], parts[4]);

            addLabelWithIcon("Nome: ", folder.getName());
            addLabelWithIcon("Tipo: ", typeFiche);
            addLabelWithIcon("FPS: ", fpsFiche);
            addLabelWithIcon("Número da Ficha: ", numberFiche);
            addLabelWithIcon("PASEP: ", pasepFiche);
            addLabelWithIcon("Data: ", dateFicheFinal);
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, "Erro na formatação do nome da pasta: " + e.getMessage(), "Erro de Formatação", JOptionPane.ERROR_MESSAGE);
        }

        // Processamento adicional com base no tipo de pasta
        processAdditionalFolderLabels(parts);
    }

    private void processAdditionalFolderLabels(String[] parts) {
        // Assume que o tipo da pasta está no primeiro elemento (exemplo: 'PA')
        String folderType = parts[0];

        // Dependendo do tipo de pasta, adicionar labels específicos
        if ("PA".equals(folderType.trim())) {
            // Processamento específico para pastas do tipo 'PA'
            boolean isEnvelopeNumeric = RegexService.isNumeric(parts[7]);
            JLabel envelopeLabel = new JLabel("Envelope: " + (isEnvelopeNumeric ? parts[7] : "Envelope não identificado"));
            setLabelWithIcon(envelopeLabel, isEnvelopeNumeric ? parts[7] : null);
            folderInfoPanel.add(envelopeLabel);
            rowsInFolderPanel++;

            // Apenas adiciona os labels de linha e coluna se existirem nas posições corretas
            if (parts.length > 8 && "13".equals(parts[8])) {
                JLabel rowLabel = new JLabel("Linha: " + parts[8]);
                setLabelWithIcon(rowLabel, parts[8]);
                folderInfoPanel.add(rowLabel);
                rowsInFolderPanel++;
            }
            if (parts.length > 9 && "16".equals(parts[9])) {
                JLabel columnLabel = new JLabel("Coluna: " + parts[9]);
                setLabelWithIcon(columnLabel, parts[9]);
                folderInfoPanel.add(columnLabel);
                rowsInFolderPanel++;
            }
        } else {
            // Processamento para outros tipos de pastas, similar ao exemplo 'PA'
            boolean isBoxParcialNumeric = RegexService.isNumeric(parts[7]);
            JLabel boxParcialLabel = new JLabel("Caixa Parcial: " + (isBoxParcialNumeric ? parts[7] : "Caixa Parcial não identificado"));
            setLabelWithIcon(boxParcialLabel, isBoxParcialNumeric ? parts[7] : null);
            folderInfoPanel.add(boxParcialLabel);
            rowsInFolderPanel++;

            boolean isBoxTotalNumeric = RegexService.isNumeric(parts[8]);
            JLabel boxTotalLabel = new JLabel("Caixa Total: " + (isBoxTotalNumeric ? parts[8] : "Caixa Total não identificado"));
            setLabelWithIcon(boxTotalLabel, isBoxTotalNumeric ? parts[8] : null);
            folderInfoPanel.add(boxTotalLabel);
            rowsInFolderPanel++;

            boolean isBoxJacareNumeric = RegexService.isNumeric(parts[9]);
            JLabel boxJacareLabel = new JLabel("Caixa Jacaré: " + (isBoxJacareNumeric ? parts[9] : "Caixa Jacaré não identificado"));
            setLabelWithIcon(boxJacareLabel, isBoxJacareNumeric ? parts[9] : null);
            folderInfoPanel.add(boxJacareLabel);
            rowsInFolderPanel++;

            if (parts.length > 10 && "13".equals(parts[10])) {
                JLabel rowLabel = new JLabel("Linha: " + parts[10]);
                setLabelWithIcon(rowLabel, parts[10]);
                folderInfoPanel.add(rowLabel);
                rowsInFolderPanel++;
            } else {
                JLabel rowLabel = new JLabel("Linha não identificada");
                setLabelWithIcon(rowLabel, null);
                folderInfoPanel.add(rowLabel);
                rowsInFolderPanel++;
            }
            if (parts.length > 11 && "16".equals(parts[11])) {
                JLabel columnLabel = new JLabel("Coluna: " + parts[11]);
                setLabelWithIcon(columnLabel, parts[11]);
                folderInfoPanel.add(columnLabel);
                rowsInFolderPanel++;
            } else {
                JLabel columnLabel = new JLabel("Coluna não identificada");
                setLabelWithIcon(columnLabel, null);
                folderInfoPanel.add(columnLabel);
                rowsInFolderPanel++;

            }
        }
    }


    private void addLabelWithIcon(String text, String value) {
        JLabel label = new JLabel(text + (value == null || value.isEmpty() ? "não identificado" : value));
        setLabelWithIcon(label, value);
        folderInfoPanel.add(label);
        rowsInFolderPanel++;
    }

    private void setLabelWithIcon(JLabel label, String text) {
        label.setIcon(text == null || text.isEmpty() ? iconCross : iconChecked);
        label.setHorizontalTextPosition(SwingConstants.RIGHT);
    }

    private void updateJpgCount(File folder) {
        File[] allFiles = folder.listFiles();
        if (allFiles == null) {
            JOptionPane.showMessageDialog(this, "Acesso negado ou diretório vazio", "Erro de Acesso", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> expectedNames = IntStream.rangeClosed(1, 56).mapToObj(i -> String.format("00001-%d.jpg", i)).collect(Collectors.toList());
        Set<String> foundNames = new HashSet<>();
        boolean allJpg = true;
        StringBuilder nonJpgFiles = new StringBuilder();
        StringBuilder filesOutOfSequence = new StringBuilder();

        for (File file : allFiles) {
            String fileName = file.getName();
            if (!fileName.toLowerCase().endsWith(".jpg")) {
                allJpg = false;
                nonJpgFiles.append(fileName).append(", ");
            } else {
                foundNames.add(fileName);
                if (!expectedNames.contains(fileName)) {
                    filesOutOfSequence.append(fileName).append(", ");
                }
            }
        }

        boolean allInSequence = filesOutOfSequence.isEmpty();
        boolean correctJpgCount = foundNames.size() == 56; // Verifica se o número de arquivos é exatamente 56
        displayJpgValidationResults(allJpg, nonJpgFiles, allInSequence, filesOutOfSequence, foundNames.size(), correctJpgCount);
    }

    private void displayJpgValidationResults(boolean allJpg, StringBuilder nonJpgFiles, boolean allInSequence, StringBuilder filesOutOfSequence, int jpgCount, boolean correctJpgCount) {
        infoNameJpgLabel.setText("Validação de tipo de arquivo: " + (allJpg ? "Todos são JPG" : "Arquivos não-JPG encontrados:"));
        setLabelWithIcon(infoNameJpgLabel, "Validação de tipo de arquivo", !allJpg);

        responseNameLabel.setText(trimComma(nonJpgFiles.toString()));
        setLabelWithIcon(responseNameLabel, "Detalhes dos arquivos não-JPG", nonJpgFiles.length() > 0);

        infoLengthJpgLabel.setText("Verificação de sequência de nomes: " + (allInSequence ? "Todos em sequência" : "Fora da sequência:"));
        setLabelWithIcon(infoLengthJpgLabel, "Verificação de sequência de nomes", !allInSequence);

        responseLengthLabel.setText(trimComma(filesOutOfSequence.toString()));
        setLabelWithIcon(responseLengthLabel, "Detalhes dos arquivos fora da sequência", filesOutOfSequence.length() > 0);

        jpgCountLabel.setText("Número de arquivos JPG: " + jpgCount + (correctJpgCount ? "" : " (incorreto, esperado: 56)"));
        setLabelWithIcon(jpgCountLabel, "Contagem de arquivos JPG", !correctJpgCount);
    }


    private void setLabelWithIcon(JLabel label, String description, boolean isError) {
        if (isError) {
            label.setIcon(iconCross);
            label.setHorizontalTextPosition(SwingConstants.RIGHT);
            label.setToolTipText(description);
        } else {
            label.setIcon(null);
            label.setToolTipText(null);
        }
    }




    private String trimComma(String text) {
        if (text.endsWith(", ")) {
            return text.substring(0, text.length() - 2);
        }
        return text;
    }


    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private void updateLayout() {
        folderInfoPanel.setLayout(new GridLayout(rowsInFolderPanel, 1, 5, 5));
        folderInfoPanel.revalidate();
        folderInfoPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FolderContentApp::new);
    }
}