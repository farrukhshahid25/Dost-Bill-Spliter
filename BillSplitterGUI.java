import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BillSplitterGUI extends JFrame {

    private static final Color COLOR_BG_DARK       = new Color(15, 23, 42);
    private static final Color COLOR_BG_MEDIUM     = new Color(30, 41, 59);
    private static final Color COLOR_BG_CARD       = new Color(51, 65, 85);
    private static final Color COLOR_BG_INPUT      = new Color(71, 85, 105);
    private static final Color COLOR_TEXT_WHITE     = new Color(241, 245, 249);
    private static final Color COLOR_TEXT_GRAY      = new Color(148, 163, 184);
    private static final Color COLOR_INDIGO         = new Color(99, 102, 241);
    private static final Color COLOR_INDIGO_LIGHT   = new Color(129, 140, 248);
    private static final Color COLOR_GREEN          = new Color(52, 211, 153);
    private static final Color COLOR_RED            = new Color(248, 113, 113);
    private static final Color COLOR_AMBER          = new Color(251, 191, 36);
    private static final Color COLOR_BORDER         = new Color(71, 85, 105, 120);

    private static final Color COLOR_RECEIPT_BG     = new Color(253, 252, 245);
    private static final Color COLOR_RECEIPT_TEXT   = new Color(30, 41, 59);
    private static final Color COLOR_RECEIPT_ACCENT = new Color(71, 85, 105);

    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_INPUT   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_RESULT  = new Font("Consolas", Font.BOLD, 14);
    private static final Font FONT_BIG_NUM = new Font("Segoe UI", Font.BOLD, 28);

    private BillCalculator calculator = new BillCalculator();
    private ParchiGenerator parchiGen = new ParchiGenerator();

    private JTabbedPane tabbedPane;
    private JTextField txtTotalBill, txtPersonName, txtItemName, txtItemPrice, txtPeople, txtTip;
    private JTable tblItems;
    private DefaultTableModel tableModel;
    private ArrayList<BillCalculator.ItemEntry> itemsList = new ArrayList<BillCalculator.ItemEntry>();

    private JLabel lblPerPerson;
    private JTextPane txtResult;
    private JButton btnCalculate, btnParchi;

    private BillCalculator.SplitResult lastResult = null;
    private String lastMode = "";

    public BillSplitterGUI() {
        setTitle("Dost Bill Splitter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1080, 700);
        setResizable(false);

        JPanel mainContainer = new JPanel(new BorderLayout(20, 0));
        mainContainer.setBackground(COLOR_BG_DARK);
        mainContainer.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(COLOR_BG_DARK);

        leftPanel.add(buildHeaderSection());
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(buildTabsSection());
        leftPanel.add(Box.createVerticalStrut(10));

        leftPanel.add(makeSectionTitle("  Split Configuration"));

        leftPanel.add(buildSharedInputsSection());
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(buildButtonsSection());

        JPanel rightPanel = buildReceiptSection();
        rightPanel.setPreferredSize(new Dimension(420, 0));

        mainContainer.add(leftPanel, BorderLayout.CENTER);
        mainContainer.add(rightPanel, BorderLayout.EAST);

        setContentPane(mainContainer);
        setLocationRelativeTo(null);
    }

    private JPanel buildHeaderSection() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(COLOR_BG_DARK);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Dost Bill Splitter");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_TEXT_WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Split fair, stay friends — with GST & Tip");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(COLOR_TEXT_GRAY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(2));
        header.add(lblSubtitle);
        return header;
    }

    private JPanel buildTabsSection() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_BG_DARK);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(COLOR_BG_MEDIUM);
        tabbedPane.setForeground(COLOR_TEXT_WHITE);
        tabbedPane.setFont(FONT_LABEL);
        tabbedPane.setFocusable(false);

        tabbedPane.addTab("  Equal Split  ", buildEqualSplitTab());
        tabbedPane.addTab("  Itemized Split  ", buildItemizedSplitTab());

        wrapper.add(tabbedPane, BorderLayout.CENTER);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));
        return wrapper;
    }

    private JPanel buildEqualSplitTab() {
        JPanel panel = makeCardPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lbl = new JLabel("Total Bill Amount (PKR)");
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(COLOR_TEXT_GRAY);
        panel.add(lbl, gbc);

        gbc.gridy = 1;
        txtTotalBill = makeTextField("e.g. 5000");
        panel.add(txtTotalBill, gbc);

        gbc.gridy = 2;
        JLabel lblNote = new JLabel("Enter the full pre-tax bill amount. GST (16%) will be added automatically.");
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNote.setForeground(COLOR_TEXT_GRAY);
        panel.add(lblNote, gbc);

        return panel;
    }

    private JPanel buildItemizedSplitTab() {
        JPanel panel = makeCardPanel();
        panel.setLayout(new BorderLayout(0, 8));

        JPanel inputRow = new JPanel(new GridBagLayout());
        inputRow.setBackground(COLOR_BG_CARD);
        inputRow.setBorder(new EmptyBorder(0, 0, 8, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 2, 4, 2);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 7;
        gbc.weightx = 1.0;
        JLabel lblOrder = new JLabel("Order Items");
        lblOrder.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblOrder.setForeground(COLOR_INDIGO_LIGHT);
        lblOrder.setBorder(new EmptyBorder(0, 0, 6, 0));
        inputRow.add(lblOrder, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.weightx = 0;
        JLabel lblPerson = new JLabel("Person:");
        lblPerson.setFont(FONT_LABEL);
        lblPerson.setForeground(COLOR_TEXT_GRAY);
        inputRow.add(lblPerson, gbc);

        gbc.gridx = 1; gbc.weightx = 0.8;
        txtPersonName = makeTextField("e.g. Ali");
        inputRow.add(txtPersonName, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        JLabel lblName = new JLabel(" Item:");
        lblName.setFont(FONT_LABEL);
        lblName.setForeground(COLOR_TEXT_GRAY);
        inputRow.add(lblName, gbc);

        gbc.gridx = 3; gbc.weightx = 1.0;
        txtItemName = makeTextField("e.g. Biryani");
        inputRow.add(txtItemName, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        JLabel lblPrice = new JLabel(" Price:");
        lblPrice.setFont(FONT_LABEL);
        lblPrice.setForeground(COLOR_TEXT_GRAY);
        inputRow.add(lblPrice, gbc);

        gbc.gridx = 5; gbc.weightx = 0.6;
        txtItemPrice = makeTextField("e.g. 450");
        inputRow.add(txtItemPrice, gbc);

        gbc.gridx = 6; gbc.weightx = 0;
        JButton btnAddItem = makeStyledButton("+ Add", COLOR_GREEN, new Color(16, 185, 129));
        btnAddItem.setPreferredSize(new Dimension(60, 32));
        btnAddItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addItemToTable();
            }
        });
        inputRow.add(btnAddItem, gbc);

        panel.add(inputRow, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"#", "Person", "Item", "Price (PKR)"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblItems = new JTable(tableModel);
        applyTableStyling(tblItems);

        JScrollPane tableScroll = new JScrollPane(tblItems);
        tableScroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        tableScroll.getViewport().setBackground(COLOR_BG_MEDIUM);
        tableScroll.setPreferredSize(new Dimension(100, 95));
        panel.add(tableScroll, BorderLayout.CENTER);

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomRow.setBackground(COLOR_BG_CARD);
        bottomRow.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnRemove = makeStyledButton("Remove Selected", COLOR_RED, new Color(220, 80, 80));
        btnRemove.setPreferredSize(new Dimension(140, 30));
        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSelectedItem();
            }
        });
        bottomRow.add(btnRemove);

        JButton btnClear = makeStyledButton("Clear All", COLOR_BG_INPUT, COLOR_BG_CARD);
        btnClear.setPreferredSize(new Dimension(100, 30));
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                itemsList.clear();
                tableModel.setRowCount(0);
            }
        });
        bottomRow.add(btnClear);

        panel.add(bottomRow, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildSharedInputsSection() {
        JPanel card = makeCardPanel();
        card.setLayout(new GridBagLayout());
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.weightx = 0.5;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblPeople = new JLabel("Number of People (Equal Split)");
        lblPeople.setFont(FONT_LABEL);
        lblPeople.setForeground(COLOR_TEXT_GRAY);
        card.add(lblPeople, gbc);

        gbc.gridy = 1;
        txtPeople = makeTextField("e.g. 4");
        card.add(txtPeople, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        JLabel lblTip = new JLabel("Tip Amount (PKR) (optional)");
        lblTip.setFont(FONT_LABEL);
        lblTip.setForeground(COLOR_TEXT_GRAY);
        card.add(lblTip, gbc);

        gbc.gridy = 1;
        txtTip = makeTextField("e.g. 500");
        card.add(txtTip, gbc);

        return card;
    }

    private JPanel buildButtonsSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        panel.setBackground(COLOR_BG_DARK);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        btnCalculate = makeStyledButton("Calculate Split", COLOR_INDIGO, COLOR_INDIGO_LIGHT);
        btnCalculate.setPreferredSize(new Dimension(200, 44));
        btnCalculate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateSplit();
            }
        });
        panel.add(btnCalculate);

        btnParchi = makeStyledButton("Copy Parchi Text", COLOR_AMBER, new Color(245, 158, 11));
        btnParchi.setPreferredSize(new Dimension(200, 44));
        btnParchi.setForeground(COLOR_BG_DARK);
        btnParchi.setEnabled(false);
        btnParchi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateParchi();
            }
        });
        panel.add(btnParchi);

        return panel;
    }

    private JPanel buildReceiptSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(COLOR_RECEIPT_BG);

        Border dashed = BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 3, 4);

        panel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, COLOR_INDIGO),
                new CompoundBorder(new EmptyBorder(10, 12, 12, 12), new CompoundBorder(dashed, new EmptyBorder(16, 16, 16, 16)))
        ));

        JPanel banner = new JPanel();
        banner.setBackground(COLOR_RECEIPT_BG);
        banner.setBorder(new MatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));

        JLabel lblBannerTitle = new JLabel("OFFICIAL RECEIPT");
        lblBannerTitle.setFont(new Font("Consolas", Font.BOLD, 18));
        lblBannerTitle.setForeground(COLOR_RECEIPT_ACCENT);
        lblBannerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPerPerson = new JLabel("PKR 0.00");
        lblPerPerson.setFont(FONT_BIG_NUM);
        lblPerPerson.setForeground(COLOR_RECEIPT_TEXT);
        lblPerPerson.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("TOTAL SPLIT");
        lblSub.setFont(new Font("Consolas", Font.PLAIN, 12));
        lblSub.setForeground(COLOR_RECEIPT_ACCENT);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        banner.add(Box.createVerticalStrut(5));
        banner.add(lblBannerTitle);
        banner.add(Box.createVerticalStrut(12));
        banner.add(lblPerPerson);
        banner.add(lblSub);
        banner.add(Box.createVerticalStrut(12));

        txtResult = new JTextPane();
        txtResult.setFont(FONT_RESULT);
        txtResult.setBackground(COLOR_RECEIPT_BG);
        txtResult.setForeground(COLOR_RECEIPT_TEXT);
        txtResult.setEditable(false);
        txtResult.setBorder(new EmptyBorder(12, 4, 12, 4));
        txtResult.setText("\n\n\nCalculate a split to see\nthe breakdown here.");

        applyCenterAlignment(txtResult);

        JScrollPane scroll = new JScrollPane(txtResult);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_RECEIPT_BG);

        panel.add(banner, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void applyCenterAlignment(JTextPane textPane) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    private void addItemToTable() {
        String person = getActualText(txtPersonName, "e.g. Ali").trim();
        String name = getActualText(txtItemName, "e.g. Biryani").trim();
        String priceText = getActualText(txtItemPrice, "e.g. 450").trim();

        if (person.isEmpty()) { showError("Please enter a person's name."); return; }
        if (name.isEmpty()) { showError("Please enter an item name."); return; }

        try {
            double price = calculator.validateAmount(priceText);
            BillCalculator.ItemEntry item = new BillCalculator.ItemEntry(person, name, price);
            itemsList.add(item);
            int rowNum = itemsList.size();
            tableModel.addRow(new Object[]{rowNum, person, name, String.format("%,.2f", price)});

            txtItemName.setText("");
            txtItemPrice.setText("");
            txtItemName.requestFocus();
        } catch (Exception ex) {
            showError("Invalid amount entered.");
        }
    }

    private void removeSelectedItem() {
        int selectedRow = tblItems.getSelectedRow();
        if (selectedRow < 0) { showError("Please select an item row to remove."); return; }
        itemsList.remove(selectedRow);
        tableModel.removeRow(selectedRow);
        for (int i = 0; i < tableModel.getRowCount(); i++) { tableModel.setValueAt(i + 1, i, 0); }
    }

    private void calculateSplit() {
        try {
            BillCalculator.SplitResult result;

            if (tabbedPane.getSelectedIndex() == 0) {
                lastMode = "Equal Split";
                result = calculator.performEqualSplit(
                        getActualText(txtTotalBill, "e.g. 5000"),
                        getActualText(txtPeople, "e.g. 4"),
                        getActualText(txtTip, "e.g. 500")
                );
            } else {
                lastMode = "Itemized Split";
                result = calculator.performItemizedSplit(
                        itemsList,
                        getActualText(txtTip, "e.g. 500")
                );
            }

            lastResult = result;
            showResults(result);

            JOptionPane.showMessageDialog(this, "Calculation successful!", "Calculation Result", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateParchi() {
        if (lastResult == null) return;
        try {
            String receipt = parchiGen.generateAndCopy(lastResult, lastMode);
            JOptionPane.showMessageDialog(this, "Parchi copied to clipboard!\nPaste it anywhere with Ctrl+V.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError("Failed to copy to clipboard.");
        }
    }

    private void showResults(BillCalculator.SplitResult result) {
        btnParchi.setEnabled(true);
        StringBuilder breakdown = new StringBuilder();

        breakdown.append("===============================\n");
        breakdown.append("ORDER DETAILS\n");
        breakdown.append("===============================\n\n");

        ArrayList<BillCalculator.ItemEntry> items = result.getItems();
        if (items != null && !items.isEmpty()) {
            breakdown.append("ITEMS PURCHASED:\n");
            for (BillCalculator.ItemEntry item : items) {
                breakdown.append(item.getName()).append(" [").append(item.getPersonName()).append("]\n");
                breakdown.append("PKR ").append(String.format("%,.2f", item.getPrice())).append("\n\n");
            }
        }

        breakdown.append(String.format("Subtotal: PKR %,.2f\n", result.getSubtotal()));
        breakdown.append(String.format("GST (16%%): PKR %,.2f\n", result.getGstAmount()));
        if (result.getTipAmount() > 0) {
            breakdown.append(String.format("Tip (Flat): PKR %,.2f\n", result.getTipAmount()));
        }

        breakdown.append("-------------------------------\n");
        breakdown.append(String.format("GRAND TOTAL: PKR %,.2f\n", result.getGrandTotal()));
        breakdown.append("===============================\n\n");

        if (result.getPersonTotals() != null && !result.getPersonTotals().isEmpty()) {
            lblPerPerson.setText("See Below");
            lblPerPerson.setFont(new Font("Segoe UI", Font.BOLD, 22));
            breakdown.append("INDIVIDUAL OWED AMOUNTS:\n\n");
            for (BillCalculator.PersonTotal pt : result.getPersonTotals()) {
                breakdown.append(pt.getPersonName()).append(" owes:\n");
                breakdown.append(String.format("PKR %,.2f", pt.getGrandTotal())).append("\n\n");
            }
        } else {
            lblPerPerson.setText(String.format("PKR %,.2f", result.getPerPerson()));
            lblPerPerson.setFont(FONT_BIG_NUM);
            breakdown.append(String.format("Total People: %d\n", result.getPeople()));
            breakdown.append(String.format("Per Person: PKR %,.2f\n", result.getPerPerson()));
        }

        breakdown.append("\n===============================\n");
        breakdown.append("THANK YOU & STAY FRIENDS");

        txtResult.setText(breakdown.toString());
        applyCenterAlignment(txtResult);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private JLabel makeSectionTitle(String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(COLOR_TEXT_GRAY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));
        return lbl;
    }

    private JPanel makeCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(COLOR_BG_CARD);
        card.setBorder(new EmptyBorder(12, 16, 12, 16));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

    private JTextField makeTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_INPUT);
        field.setBackground(COLOR_BG_INPUT);
        field.setForeground(COLOR_TEXT_GRAY);
        field.setCaretColor(COLOR_TEXT_WHITE);
        field.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(COLOR_TEXT_WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(COLOR_TEXT_GRAY);
                }
            }
        });
        return field;
    }

    private JButton makeStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text) {
            private boolean isHovering = false;
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovering ? hoverColor : bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { isHovering = true; repaint(); }
                    public void mouseExited(MouseEvent e) { isHovering = false; repaint(); }
                });
            }
        };
        button.setFont(FONT_BUTTON);
        button.setForeground(COLOR_TEXT_WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void applyTableStyling(JTable table) {
        table.setBackground(COLOR_BG_MEDIUM);
        table.setForeground(COLOR_TEXT_WHITE);
        table.setSelectionBackground(COLOR_INDIGO);
        table.setSelectionForeground(COLOR_TEXT_WHITE);
        table.setGridColor(COLOR_BORDER);
        table.setFont(FONT_INPUT);
        table.setRowHeight(26);
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_BG_INPUT);
        header.setForeground(COLOR_TEXT_WHITE);
        header.setFont(FONT_LABEL);
        header.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
    }

    private String getActualText(JTextField field, String placeholder) {
        String text = field.getText();
        return text.equals(placeholder) ? "" : text;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", COLOR_BG_DARK);
            UIManager.put("nimbusBlueGrey", COLOR_BG_MEDIUM);
            UIManager.put("control", COLOR_BG_MEDIUM);
            UIManager.put("text", COLOR_TEXT_WHITE);
            UIManager.put("nimbusLightBackground", COLOR_BG_INPUT);
            UIManager.put("info", COLOR_BG_MEDIUM);
            UIManager.put("nimbusSelectionBackground", COLOR_INDIGO);
            UIManager.put("TabbedPane.background", COLOR_BG_MEDIUM);
            UIManager.put("TabbedPane.foreground", COLOR_TEXT_WHITE);
            UIManager.put("TabbedPane.selected", COLOR_BG_CARD);
            UIManager.put("TabbedPane:TabbedPaneTab.contentMargins", new Insets(8, 16, 8, 16));
            UIManager.put("OptionPane.background", COLOR_BG_MEDIUM);
            UIManager.put("Panel.background", COLOR_BG_MEDIUM);
            UIManager.put("OptionPane.messageForeground", COLOR_TEXT_WHITE);
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BillSplitterGUI().setVisible(true);
            }
        });
    }
}