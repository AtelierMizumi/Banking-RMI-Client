import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.NumberFormat;
import java.util.Locale;

public class BankClient extends JFrame {
    private BankService bankService;
    private JTextField accountNumberField;
    private JTextField amountField;
    private JTextField recipientAccountField;
    private JTextArea logArea;
    private JLabel balanceLabel;
    private String currentAccount;
    private BankCallbackImpl callback;
    private JComboBox<String> currencySelector;
    private double currentBalance;

    public BankClient() {
        setTitle("eBanking Client");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create Menu Bar
        createMenuBar();

        // Connect to server
        connectToServer();
        
        // Create UI components
        createUI();
        
        setLocationRelativeTo(null);
    }
    
    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            bankService = (BankService) registry.lookup("BankService");
            System.out.println("Connected to Bank Server");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Cannot connect to server: " + e.getMessage(),
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void createUI() {
        // Top panel - Account info
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Account number
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Số tài khoản:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        accountNumberField = new JTextField(15);
        topPanel.add(accountNumberField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.weightx = 0;
        JButton queryButton = new JButton("Vấn tin");
        queryButton.addActionListener(e -> queryAccount());
        topPanel.add(queryButton, gbc);
        
        // Balance display
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        balanceLabel = new JLabel("Số dư hiện tại: ");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(balanceLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        String[] currencies = {"VND", "USD"};
        currencySelector = new JComboBox<>(currencies);
        currencySelector.addActionListener(e -> updateBalanceDisplay());
        topPanel.add(currencySelector, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Actions Panel
        JPanel actionsPanel = new JPanel(new GridBagLayout());
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Thực hiện giao dịch"));
        GridBagConstraints agbc = new GridBagConstraints();
        agbc.insets = new Insets(5, 5, 5, 5);
        agbc.fill = GridBagConstraints.HORIZONTAL;

        // Amount
        agbc.gridx = 0; agbc.gridy = 0;
        actionsPanel.add(new JLabel("Số tiền:"), agbc);

        agbc.gridx = 1; agbc.gridy = 0;
        agbc.gridwidth = 2;
        amountField = new JTextField(15);
        actionsPanel.add(amountField, agbc);

        // Deposit and Withdraw buttons
        agbc.gridx = 1; agbc.gridy = 1;
        agbc.gridwidth = 1;
        JButton depositButton = new JButton("Nạp tiền");
        depositButton.addActionListener(e -> deposit());
        actionsPanel.add(depositButton, agbc);

        agbc.gridx = 2; agbc.gridy = 1;
        JButton withdrawButton = new JButton("Rút tiền");
        withdrawButton.addActionListener(e -> withdraw());
        actionsPanel.add(withdrawButton, agbc);

        // Recipient Account
        agbc.gridx = 0; agbc.gridy = 2;
        actionsPanel.add(new JLabel("Tài khoản nhận:"), agbc);

        agbc.gridx = 1; agbc.gridy = 2;
        agbc.gridwidth = 2;
        recipientAccountField = new JTextField(15);
        actionsPanel.add(recipientAccountField, agbc);

        // Transfer button
        agbc.gridx = 1; agbc.gridy = 3;
        agbc.gridwidth = 2;
        JButton transferButton = new JButton("Chuyển khoản");
        transferButton.addActionListener(e -> transfer());
        actionsPanel.add(transferButton, agbc);

        // Log area
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(actionsPanel, BorderLayout.NORTH);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Lịch sử giao dịch"));
        
        logArea = new JTextArea(15, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        centerPanel.add(logPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        
        // Initialize with a default account if you want
        // currentAccount = "01234";
        // accountNumberField.setText(currentAccount);
        // queryAccount();
        // registerCallback();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Trợ giúp");
        JMenuItem demoGuide = new JMenuItem("Hướng dẫn Demo");
        demoGuide.addActionListener(e -> showDemoGuide());
        helpMenu.add(demoGuide);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void showDemoGuide() {
        String guide = "<html>"
                + "<h2>Hướng dẫn Demo Ứng dụng eBanking</h2>"
                + "<p><b>Các tài khoản demo có sẵn:</b></p>"
                + "<ul>"
                + "<li>00001 - Demo User 1</li>"
                + "<li>00002 - Demo User 2</li>"
                + "<li>00003 - Demo User 3</li>"
                + "<li>01234 - Nguyen Van A</li>"
                + "<li>12345 - Tran Thi B</li>"
                + "<li>23456 - Le Van C</li>"
                + "</ul>"
                + "<p>Mỗi tài khoản có số dư ban đầu là 50,000 VND.</p>"
                + "<br>"
                + "<p><b>Cách thực hiện các chức năng:</b></p>"
                + "<ol>"
                + "<li><b>Vấn tin:</b> Nhập số tài khoản và nhấn 'Vấn tin' để xem số dư.</li>"
                + "<li><b>Nạp tiền:</b> Nhập số tài khoản, số tiền và nhấn 'Nạp tiền'.</li>"
                + "<li><b>Rút tiền:</b> Nhập số tài khoản, số tiền và nhấn 'Rút tiền'.</li>"
                + "<li><b>Chuyển khoản:</b> Nhập tài khoản của bạn (vấn tin trước), tài khoản nhận, số tiền và nhấn 'Chuyển khoản'.</li>"
                + "<li><b>Đổi tiền tệ:</b> Sử dụng ô chọn bên cạnh số dư để xem số tiền theo VND hoặc USD.</li>"
                + "</ol>"
                + "</html>";

        JOptionPane.showMessageDialog(this, guide, "Hướng dẫn Demo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateBalanceDisplay() {
        if (currentAccount != null && !currentAccount.isEmpty()) {
            balanceLabel.setText("Số dư hiện tại: " + formatCurrency(currentBalance));
        } else {
            balanceLabel.setText("Số dư hiện tại: ");
        }
    }

    private String formatCurrency(double amount) {
        String selectedCurrency = (String) currencySelector.getSelectedItem();
        Locale locale;
        NumberFormat currencyFormatter;

        switch (selectedCurrency) {
            case "USD":
                locale = Locale.US;
                // For USD, let's assume an exchange rate, e.g., 23000 VND = 1 USD
                amount /= 23000;
                break;
            case "VND":
            default:
                locale = new Locale("vi", "VN");
                break;
        }
        currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        return currencyFormatter.format(amount);
    }

    private void queryAccount() {
        try {
            String accountNumber = accountNumberField.getText().trim();
            if (accountNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập số tài khoản!",
                        "Lỗi",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Account account = bankService.getAccount(accountNumber);
            
            if (account != null) {
                currentBalance = account.getBalance();
                updateBalanceDisplay();
                logArea.append("Vấn tin tài khoản: " + accountNumber +
                             " - Số dư: " + formatCurrency(account.getBalance()) + "\n");
                currentAccount = accountNumber;
                if (callback == null) { // Register callback only once or if it's null
                    registerCallback();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy tài khoản!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException e) {
            showError("Lỗi vấn tin: " + e.getMessage());
        }
    }
    
    private void deposit() {
        try {
            String accountNumber = accountNumberField.getText().trim();
            String amountStr = amountField.getText().trim();
            
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập số tiền!", 
                    "Lỗi", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(amountStr);
            boolean success = bankService.deposit(accountNumber, amount);
            
            if (success) {
                logArea.append("Đã nạp " + formatCurrency(amount) + " vào tài khoản " + accountNumber + "\n");
                queryAccount();
                amountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Nạp tiền thất bại!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            showError("Số tiền không hợp lệ!");
        } catch (RemoteException e) {
            showError("Lỗi nạp tiền: " + e.getMessage());
        }
    }
    
    private void withdraw() {
        try {
            String accountNumber = accountNumberField.getText().trim();
            String amountStr = amountField.getText().trim();
            
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập số tiền!", 
                    "Lỗi", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(amountStr);
            boolean success = bankService.withdraw(accountNumber, amount);
            
            if (success) {
                logArea.append("Đã rút " + formatCurrency(amount) + " từ tài khoản " + accountNumber + "\n");
                queryAccount();
                amountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Rút tiền thất bại! Kiểm tra số dư.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            showError("Số tiền không hợp lệ!");
        } catch (RemoteException e) {
            showError("Lỗi rút tiền: " + e.getMessage());
        }
    }
    
    private void transfer() {
        try {
            String fromAccount = accountNumberField.getText().trim();
            String toAccount = recipientAccountField.getText().trim();
            String amountStr = amountField.getText().trim();
            
            if (toAccount.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập đầy đủ thông tin!", 
                    "Lỗi", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(amountStr);
            boolean success = bankService.transfer(fromAccount, toAccount, amount);
            
            if (success) {
                logArea.append("Đã chuyển " + formatCurrency(amount) + " từ " + fromAccount +
                             " đến " + toAccount + "\n");
                queryAccount();
                amountField.setText("");
                recipientAccountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Chuyển khoản thất bại! Kiểm tra số dư và tài khoản.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            showError("Số tiền không hợp lệ!");
        } catch (RemoteException e) {
            showError("Lỗi chuyển khoản: " + e.getMessage());
        }
    }
    
    private void registerCallback() {
        try {
            if (currentAccount != null && !currentAccount.isEmpty()) {
                callback = new BankCallbackImpl(this);
                bankService.registerCallback(currentAccount, callback);
            }
        } catch (RemoteException e) {
            showError("Lỗi đăng ký callback: " + e.getMessage());
        }
    }
    
    public void receiveNotification(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("THÔNG BÁO: " + message + "\n");
            JOptionPane.showMessageDialog(this, 
                message, 
                "Thông báo chuyển khoản", 
                JOptionPane.INFORMATION_MESSAGE);
            queryAccount();
        });
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankClient client = new BankClient();
            client.setVisible(true);
        });
    }
    
    // Callback implementation
    class BankCallbackImpl extends UnicastRemoteObject implements BankCallback {
        private BankClient client;
        
        protected BankCallbackImpl(BankClient client) throws RemoteException {
            super();
            this.client = client;
        }
        
        @Override
        public void notifyTransfer(String message) throws RemoteException {
            client.receiveNotification(message);
        }
    }
}
