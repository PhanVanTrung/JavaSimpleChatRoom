/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cosc2082_assignment2_s3479773_s3479765_s3446356;

/**
 *
 * @author VS9 X64Bit
 */
import cosc2082_assignment2_s3479773_s3479765_s3446356.ReceiveAudio;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class Client extends JFrame {

    Socket clientSocket = null;
    ServerSocket fileSocket;

    PrintWriter clientOut = null;           // outToServer
    BufferedReader clientIn = null;         // inFromServer

    static boolean serverNoOK = false;  // is server up
    boolean isPrivateChat = false;          // is the chat private
    boolean isGroupChat = false;            // is the chat group chat
    boolean checkSignup, check_signup = false;
    boolean isMasterGroupChat, isMemberGroupChat = false;     // is the current usernameTextField groupMaster or only member
    final static boolean shouldFill = true; //UI setting
    final static boolean shouldWeightX = true; //UI setting
    final static boolean RIGHT_TO_LEFT = false; //UI setting

    String privatePartner = null;       // who to chat private with
    String masterGroupName = null;              // store who is the group creator
    String fileRequest = null;          // file path.
    String username = null;
    String fileName = null;
    String ip;
    String[] listStatus = {"Available", "Busy", "Invisible"};
    String[] arrayEmotions = {"<SMILE>", "<BSMILE>", "<SAD>", "<CRY>", "<TOUNGE>", "<ANGEL>", "<DEVIL>", "<CONFUSE>", "<WINKING>", "<SURPRISE>"};

    JPanel loginPanel = new JPanel();           // log in panel
    JPanel signupPanel = new JPanel();           // register panel

    JLabel notiMess = new JLabel();
    JFrame loginFrame = new JFrame();
    JFrame privateChatFrame = new JFrame();
    JFrame groupChatFrame = new JFrame();

    JTextPane groupChatTextPanel = new JTextPane();

    JTextField usernameTextField = new JTextField(20); //sua lai, them so 20 vo
    JTextField usernameRegister = new JTextField(20); 
    JTextField groupTypeArea = new JTextField();            // typing area
    JTextField privateChatTextField = new JTextField();
    JTextField typeArea = new JTextField();

    ArrayList listBlocked = new ArrayList();    // list contains who being blocked.
    JFrame notificationFrame = new JFrame();
    JLabel privateChatLabel = new JLabel("Private Chat");
    JLabel userStatusLabel = new JLabel();                // status label (e.g: Nickname:  asdbasdb     Status: Available.....)
    ArrayList<JLabel> listIcon = new ArrayList<JLabel>();           // an ArrayList of each JLabel emoticon

    JPasswordField passwordRegister = new JPasswordField(20);     // passwordLogin register
    JPasswordField passwordRetypeRegister = new JPasswordField(20);     // retype passwordLogin
    JPasswordField passwordLogin = new JPasswordField(20);       // log in passwordLogin

    JButton loginButton = new JButton("Log In");
    JButton signupButton01 = new JButton("Register");         // Register button in welcome window
    JButton signupButton02 = new JButton("Register");       // confirm register button
    JButton backButton = new JButton("Back");
    JButton blockUserButton = new JButton("Block");
    JButton unBlockUserButton = new JButton("Unblock");
    JButton sendMessageButton = new JButton("Send");
    JButton inviteGroupChatButton = new JButton("Invite");
    JButton privateChatCloseButton = new JButton("Close");
    JButton groupCloseButton = new JButton("Close group");

    JMenuBar menu = new JMenuBar();
    JTextPane chatArea = new JTextPane();
    JTextArea privateTextArea = new JTextArea();

    JPanel blockPanel = new JPanel(new FlowLayout());
    JPanel emotionPanel = new JPanel(new GridLayout(0, 10, 0, 0));
    JPanel grupChatPanel = new JPanel();       // group chat panel
    JLabel welcomeGroupChatLabel = new JLabel();
    JPanel firstPanel = new JPanel();       // contains "userStatusLabel" (or status)
    JPanel secondPanel = new JPanel();      // contains "online" JList

    DefaultListModel model = new DefaultListModel();
    JList online = new JList(model);
    File fileFile = null;

    Icon atIcon = new ImageIcon("src/send-file-xxl.png");
    Icon atIcon1 = new ImageIcon("src/Mic-Icon-Square.png");
    Icon atIcon2 = new ImageIcon("src/video.png");
    Icon atIcon3 = new ImageIcon("src/chat_sm.png");

    JMenuItem fileTransfer = new JMenuItem("Send File", atIcon);
    JMenuItem voiceCall = new JMenuItem("Voice Call", atIcon1);
    JMenuItem videoCall = new JMenuItem("Video Call", atIcon2);
    JMenuItem chatGroup = new JMenuItem("Create Group Chat", atIcon3);

    JScrollPane listPane = new JScrollPane(online);
    JScrollPane chatScroll = new JScrollPane(chatArea);
    JScrollPane privateChatScroll = new JScrollPane(privateTextArea);
    JScrollPane groupChatScroll = new JScrollPane(groupChatTextPanel);

    JComboBox selectStatus = new JComboBox(listStatus);             // drop down menu to select status

    StyledDocument doc = null;
    Style def = null;
    Style notification = null;
    StyledDocument docGroup = null;
    Style defGroup = null;
    Style notificationGroup = null;
    Style regular = null;

    int filePort;

    public Client() throws IOException {
        this.fileSocket = new ServerSocket(0);
        this.filePort = fileSocket.getLocalPort();
        this.ip = InetAddress.getLocalHost().getHostAddress();
        for (int i = 1; i < 11; i++) {
            JLabel cell = new JLabel() {{setVisible(true);}};
            listIcon.add(cell);
            cell.setIcon(new ImageIcon("src/" + i + ".png"));
            emotionPanel.add(cell);
            listIcon.get(i - 1).addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (int j = 0; j < listIcon.size(); j++) {
                        if (e.getSource() == listIcon.get(j)) {
                            typeArea.setText(typeArea.getText() + arrayEmotions[j]);
                            typeArea.requestFocusInWindow();
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {}
                public void mouseReleased(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
            });
        }
        
        doc = chatArea.getStyledDocument();                         // set styles
        def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        notification = doc.addStyle("notification", regular);
        docGroup = groupChatTextPanel.getStyledDocument();
        defGroup = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        notificationGroup = docGroup.addStyle("notification", regular);
        
        StyleConstants.setItalic(notification, true);
        StyleConstants.setBold(notification, true);       
        StyleConstants.setBold(notificationGroup, true);
        StyleConstants.setItalic(notificationGroup, true);
        
        sendMessageButton.setBackground(new Color(53,180,51));
        sendMessageButton.setForeground(Color.WHITE);
        
        blockUserButton.setBackground(new Color(53,180,51));
        blockUserButton.setForeground(Color.WHITE);
        blockUserButton.setPreferredSize(new Dimension(80,25));
        
        unBlockUserButton.setBackground(new Color(53,180,51));
        unBlockUserButton.setForeground(Color.WHITE);
        unBlockUserButton.setPreferredSize(new Dimension(80,25));
        
        this.getContentPane().setBackground(new Color(194,232,194));
        emotionPanel.setBackground(new Color(194,232,194));
        blockPanel.setBackground(new Color(194,232,194));
        firstPanel.setBackground(new Color(194,232,194));
        secondPanel.setBackground(new Color(194,232,194));
                
        openChatGroupFrame();           // declare chat group frame
        openLoginFrame();               // declare Log in frame
        openPrivateChatFrame();               // declare private chat frame
        
        this.setLayout(new GridBagLayout());
        GridBagConstraints mainFrameConstraint = new GridBagConstraints();
        
        mainFrameConstraint.gridx = 0;
        mainFrameConstraint.gridy = 0;
        mainFrameConstraint.insets = new Insets(0, 4, 1, 0);
        mainFrameConstraint.anchor = GridBagConstraints.SOUTH;
        firstPanel.add(userStatusLabel);
        selectStatus.setSelectedItem("Available");
        firstPanel.add(selectStatus);
        this.add(firstPanel, mainFrameConstraint);

        mainFrameConstraint.ipady = 0;
        mainFrameConstraint.gridx = 1;
        mainFrameConstraint.weightx = 0.5;
        mainFrameConstraint.anchor = GridBagConstraints.SOUTH;
        this.add(secondPanel.add(new JLabel("List Online")), mainFrameConstraint);
        
        mainFrameConstraint.fill = GridBagConstraints.HORIZONTAL;
        mainFrameConstraint.ipady = 300;
        mainFrameConstraint.ipadx = 400;
        mainFrameConstraint.gridx = 0;
        mainFrameConstraint.gridy = 2;
        this.add(chatScroll, mainFrameConstraint);
        
        mainFrameConstraint.weightx = 0.5;
        mainFrameConstraint.ipady = 300;
        mainFrameConstraint.ipadx = 20;
        mainFrameConstraint.gridx = 1;
        mainFrameConstraint.insets = new Insets(0, 1, 0, 1);
        online.setModel(model);
        this.add(listPane, mainFrameConstraint);
        
        mainFrameConstraint.fill = GridBagConstraints.HORIZONTAL;
        mainFrameConstraint.ipady = 10;
        mainFrameConstraint.ipadx = 40;
        mainFrameConstraint.weightx = 0.5;
        mainFrameConstraint.gridx = 0;
        mainFrameConstraint.gridy = 3;
        mainFrameConstraint.insets = new Insets(0, 3, 5, 0);
        this.add(emotionPanel, mainFrameConstraint);
        
        mainFrameConstraint.fill = GridBagConstraints.HORIZONTAL;
        mainFrameConstraint.ipady = 40;
        mainFrameConstraint.ipadx = 40;
        mainFrameConstraint.weightx = 0.5;
        mainFrameConstraint.gridy = 4;
        mainFrameConstraint.insets = new Insets(0, 4, 0, 0);
        this.add(typeArea, mainFrameConstraint);

        mainFrameConstraint.fill = GridBagConstraints.HORIZONTAL;
        mainFrameConstraint.ipady = 1;
        mainFrameConstraint.ipadx = 40;
        mainFrameConstraint.weightx = 0.5;
        mainFrameConstraint.gridx = 1;
        mainFrameConstraint.gridy = 3;
        mainFrameConstraint.insets = new Insets(1, 0, 0, 0);
        this.add(blockPanel, mainFrameConstraint);

        blockPanel.add(blockUserButton);
        blockPanel.add(unBlockUserButton);

        mainFrameConstraint.fill = GridBagConstraints.HORIZONTAL;
        mainFrameConstraint.ipady = 30;
        mainFrameConstraint.ipadx = 30;
        mainFrameConstraint.weightx = 0.5;
        mainFrameConstraint.gridheight = 2;
        mainFrameConstraint.gridy = 4;
        mainFrameConstraint.insets = new Insets(3, 4, 3, 4);
        this.add(sendMessageButton, mainFrameConstraint);

        menu.add(fileTransfer);
        menu.add(voiceCall);
        menu.add(videoCall);
        menu.add(chatGroup);

        this.setJMenuBar(menu);
        this.setTitle("Chat room");
        this.setVisible(true);
        this.setSize(680, 550);
        chatArea.setEditable(false);
        this.setVisible(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MyActionListener al = new MyActionListener();
        MouseAdapter ma = new MouseAdapter();
        loginButton.addActionListener(al);
        signupButton01.addActionListener(al);
        signupButton02.addActionListener(al);
        backButton.addActionListener(al);
        sendMessageButton.addActionListener(al);
        typeArea.addActionListener(al);
        privateChatTextField.addActionListener(al);
        privateChatCloseButton.addActionListener(al);
        groupTypeArea.addActionListener(al);
        groupCloseButton.addActionListener(al);
        selectStatus.addActionListener(al);
        inviteGroupChatButton.addActionListener(al);
        fileTransfer.addActionListener(al);
        chatGroup.addMouseListener(ma);
        blockUserButton.addMouseListener(ma);
        unBlockUserButton.addMouseListener(ma);
        online.addMouseListener(ma);
        voiceCall.addMouseListener(ma);
        videoCall.addMouseListener(ma);
    }

    public void openChatGroupFrame() {
        groupChatFrame.setLayout(new BorderLayout());
        grupChatPanel.add(welcomeGroupChatLabel);
        grupChatPanel.add(inviteGroupChatButton);
        groupChatFrame.add(grupChatPanel,BorderLayout.NORTH);                 // frame > panel > label
        groupChatFrame.add(groupChatScroll,BorderLayout.CENTER);                 // display chat
        JPanel openChatPanel = new JPanel(new BorderLayout());
        groupChatFrame.add(openChatPanel,BorderLayout.SOUTH);    
        openChatPanel.add(groupTypeArea,BorderLayout.CENTER);                 // typing area
        groupChatTextPanel.setEditable(false);
        openChatPanel.add(groupCloseButton,BorderLayout.SOUTH);
        groupChatFrame.setVisible(false);
        groupChatFrame.setTitle("Group chat");
        groupChatFrame.setSize(400, 330);
        groupChatFrame.setResizable(false);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    public void openPrivateChatFrame() {
        privateChatFrame.setLayout(new BorderLayout());
        privateChatFrame.add(privateChatLabel,BorderLayout.NORTH);
        privateTextArea.setEditable(false);
        privateChatFrame.add(privateChatScroll,BorderLayout.CENTER);
        JPanel privateChatPanel = new JPanel(new BorderLayout());
        privateChatFrame.add(privateChatPanel,BorderLayout.SOUTH);
        privateChatPanel.add(privateChatTextField,BorderLayout.CENTER);
        privateChatPanel.add(privateChatCloseButton,BorderLayout.SOUTH);
        privateChatFrame.setVisible(false);
        privateChatFrame.setSize(300, 200);
        privateChatFrame.setResizable(false);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    public void openLoginFrame() {
        loginPanel.setLayout(new BorderLayout()); // after enter server ip address, this frame appears
        JPanel top1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginPanel.add(top1Panel,BorderLayout.CENTER);
        top1Panel.add(new JLabel("Username"));
        top1Panel.add(usernameTextField);
        top1Panel.add(new JLabel("Password"));
        top1Panel.add(passwordLogin);
        top1Panel.add(loginButton);
        top1Panel.add(signupButton01);

        signupPanel.setLayout(new  BorderLayout());                  // signupButton02 frame appears after clicking "Sign up"
        JPanel top2Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        signupPanel.add(top2Panel,BorderLayout.CENTER);
        top2Panel.add(new JLabel("New Username"));
        top2Panel.add(usernameRegister);
        top2Panel.add(new JLabel("New password"));
        top2Panel.add(passwordRegister);
        top2Panel.add(new JLabel("Re-enter password"));
        top2Panel.add(passwordRetypeRegister);
        top2Panel.add(signupButton02);
        top2Panel.add(backButton);

        signupPanel.setSize(300, 400);
        loginFrame.add(loginPanel);

        if (check_signup) {
            signupPanel.setVisible(false);
            loginPanel.setVisible(true);
            usernameRegister.setText(null);
            passwordRegister.setText(null);
            passwordRetypeRegister.setText(null);
            check_signup = false;
        }
        if (checkSignup) {                  // if true, register frame
            loginPanel.setVisible(false);
            loginFrame.add(signupPanel);
            signupPanel.setVisible(true);
        }

        loginFrame.setVisible(true);
        loginFrame.setSize(new Dimension(350, 150));
        loginFrame.setResizable(false);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // converting from emoticon text to emoticon icon (on display chat Area) 
    public void printText(JTextPane actionTextPane, String actionText) throws BadLocationException {              // actionTextPane: chat display area; actionText: the text to be replaced
        actionTextPane.setOpaque(false);          // to overlap component
        Pattern pattern = Pattern.compile("<SMILE>|<BSMILE>|<SAD>|<CRY>|<TOUNGE>|<ANGEL>|<DEVIL>|<CONFUSE>|<WINKING>|<SURPRISE>");
        Matcher matcher = pattern.matcher(actionText);              // check if JTextPane actionTextPane (((chat display area) match the pattern
        Style s = doc.addStyle("icon", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        int previousMatch = 0;              // flag. to flag index where one emoticon has been replaced by ImageIcon
        while (matcher.find()) {              // found matched text
            int startIndex = matcher.start();
            int endIndex = matcher.end();
            String group = matcher.group();
            String subText = actionText.substring(previousMatch, startIndex);
            if (!subText.isEmpty()) {
                doc.insertString(doc.getLength(), subText, doc.getStyle("regular"));
            }
            if (group.equals("<SMILE>")) {
                StyleConstants.setIcon(s, emoticon("../1.png"));
                doc.insertString(doc.getLength(), "<SMILE>", doc.getStyle("icon"));
            } else if (group.equals("<SAD>")) {
                StyleConstants.setIcon(s, emoticon("../3.png"));
                doc.insertString(doc.getLength(), "<SAD>", doc.getStyle("icon"));
            } else if (group.equals("<BSMILE>")) {
                StyleConstants.setIcon(s, emoticon("../2.png"));
                doc.insertString(doc.getLength(), "<BSMILE>", doc.getStyle("icon"));
            } else if (group.equals("<TOUNGE>")) {
                StyleConstants.setIcon(s, emoticon("../5.png"));
                doc.insertString(doc.getLength(), "<TOUNGE>", doc.getStyle("icon"));
            } else if (group.equals("<CRY>")) {
                StyleConstants.setIcon(s, emoticon("../4.png"));
                doc.insertString(doc.getLength(), "<CRY>", doc.getStyle("icon"));
            } else if (group.equals("<DEVIL>")) {
                StyleConstants.setIcon(s, emoticon("../7.png"));
                doc.insertString(doc.getLength(), "<DEVIL>", doc.getStyle("icon"));
            } else if (group.equals("<ANGEL>")) {
                StyleConstants.setIcon(s, emoticon("../6.png"));
                doc.insertString(doc.getLength(), "<ANGEL>", doc.getStyle("icon"));
            } else if (group.equals("<WINKING>")) {
                StyleConstants.setIcon(s, emoticon("../9.png"));
                doc.insertString(doc.getLength(), "<WINKING>", doc.getStyle("icon"));
            } else if (group.equals("<CONFUSE>")) {
                StyleConstants.setIcon(s, emoticon("../8.png"));
                doc.insertString(doc.getLength(), "<CONFUSE>", doc.getStyle("icon"));
            } else if (group.equals("<SURPRISE>")) {
                StyleConstants.setIcon(s, emoticon("../10.png"));
                doc.insertString(doc.getLength(), "<SURPRISE>", doc.getStyle("icon"));
            }
            previousMatch = endIndex;
        }
        String subText = actionText.substring(previousMatch);              // cut a whole message to sub-text to find "emot-text"
        if (!subText.isEmpty()) {              // display message in regular style if no emo-text found
            doc.insertString(doc.getLength(), subText, doc.getStyle("regular"));
        }
        doc.insertString(doc.getLength(), "\n", doc.getStyle("regular"));
    }

    public ImageIcon emoticon(String path) {
        URL imgURL = Server.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file path: " + path);
            return null;
        }
    }

    class MouseAdapter implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == voiceCall) {
                final JFrame inputframe = new JFrame();
                JPanel selectedUser = new JPanel();
                JLabel req = new JLabel("Select User: ");
                model.removeElement(username);
                String[] onl = new String[model.getSize()];
                for (int i = 0; i < model.getSize(); i++) {
                    onl[i] = model.getElementAt(i).toString();
                }
                final JComboBox listOnlUsers = new JComboBox(onl);
                selectedUser.add(req);
                JButton closeFrame = new JButton("OK");

                model.addElement(username);
                selectedUser.add(listOnlUsers);
                selectedUser.add(closeFrame);
                JButton cancelFrame = new JButton("Cancel");
                selectedUser.add(cancelFrame);
                cancelFrame.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        inputframe.setVisible(false);
                    }
                });
                selectedUser.add(cancelFrame);
                inputframe.setTitle("Audio Chat");
                inputframe.add(selectedUser);
                inputframe.setSize(400, 120);
                inputframe.setVisible(true);
                inputframe.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                closeFrame.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        inputframe.setVisible(false);
                        try {
                            String usernameTextField = listOnlUsers.getSelectedItem().toString();
                            try {
                                final ServerSocket ss = new ServerSocket(0);
                                String ip = ss.getInetAddress().getLocalHost().getHostAddress();
                                int port = ss.getLocalPort();
                                clientOut.println("VOICECHAT" + usernameTextField + ";" + ip + "*" + port);
                                final Socket conn = ss.accept();
                                ss.close();
                                final JFrame callFr = new JFrame();
                                JPanel callPn = new JPanel();
                                JButton endCall = new JButton("End Call");
                                callPn.add(endCall);
                                callFr.add(callPn);
                                callFr.setSize(400, 120);
                                callFr.setVisible(true);
                                callFr.setTitle("Server Audio Chat");
                                callFr.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosing(WindowEvent evt) {
                                        System.out.println("shjt. close it for me!!!!!!!!");
                                        try {
                                            conn.close();
                                            ss.close();
                                        } catch (IOException ex) {
                                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                });

                                endCall.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent ae) {
                                        callFr.setVisible(false);
                                        inputframe.setVisible(false);
                                        try {
                                            conn.close();
                                            ss.close();
                                            System.out.println("End call!");
                                        } catch (IOException ex) {
                                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                });

                                System.out.println("Connected to client: " + conn.getRemoteSocketAddress());
                                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                                // Formatting audio data, start microphone recording
                                AudioFormat af = new AudioFormat(8000.0f, 8, 1, true, false);
                                DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
                                TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
                                microphone.open(af);
                                microphone.start();
                                int bytesRead = 0;
                                byte[] soundData = new byte[1];

                                // Reading data in
                                Thread inThread = new Thread(new ReceiveAudio(conn));
                                inThread.start();
                                // Sending data out
                                while (bytesRead != -1) {
                                    bytesRead = microphone.read(soundData, 0, soundData.length);
                                    if (!conn.isClosed()) {
                                        if (bytesRead >= 0) {
                                            try {
                                                dos.write(soundData, 0, bytesRead);
                                            } catch (Exception e) {
                                                conn.close();
                                                ss.close();
                                                openNotiFrame("Data communication error! Exit!");
                                                break;
                                            }
                                        }
                                    } else {
                                        conn.close();
                                        ss.close();
                                        openNotiFrame("Client disconnected!");
                                        break;
                                    }
                                }
                                microphone.close();
                                microphone.stop();
                                System.out.println("IT IS DONE.");

                            } catch (Exception ex) {
                                openNotiFrame("Error!");
                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                                ex.printStackTrace();
                            }
                        } catch (Exception e) {
                            inputframe.setVisible(false);
                            openNotiFrame("No usernameTextField was selected");
                        }
                    }
                });
            }

            if (e.getClickCount() == 2 && e.getSource() == online && !username.equals(online.getSelectedValue())) {
                if (isPrivateChat == false) {
                    privateChatLabel.setText("Private chat with "+ online.getSelectedValue());
                    privateChatFrame.setTitle("Username: " + username);
                    privateChatFrame.setVisible(true);
                    privatePartner = (String) online.getSelectedValue();
                    isPrivateChat = true;
                    clientOut.println("PRIVATE" + privatePartner + ",Hey, What's up! It's me - " + username + "\n");
                } else {
                    openNotiFrame("Only one private chat at a time allowed.");
                }
            }
            if (e.getSource() == chatGroup) {
                groupChatFrame.setVisible(true);
                if (!isGroupChat) {
                    welcomeGroupChatLabel.setText("Group chat of " + username);
                    grupChatPanel.add(inviteGroupChatButton);
                    grupChatPanel.revalidate();
                    groupChatFrame.revalidate();
                    isMasterGroupChat = true;
                    isGroupChat = true;
                    try {
                        docGroup.insertString(docGroup.getLength(), "* Your group has been created * \n", docGroup.getStyle("notification"));
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            if (e.getSource() == blockUserButton) {
                final JFrame inputframe = new JFrame();
                JPanel selectedUser = new JPanel();
                JLabel req = new JLabel("Select User: ");
                model.removeElement(username);
                String[] onl = new String[model.getSize()];
                for (int i = 0; i < model.getSize(); i++) {
                    onl[i] = model.getElementAt(i).toString();
                }
                final JComboBox listOnlUsers = new JComboBox(onl);
                selectedUser.add(req);
                JButton closeFrame = new JButton("OK");
                model.addElement(username);
                selectedUser.add(listOnlUsers);
                selectedUser.add(closeFrame);

                JButton cancelFrame = new JButton("Cancel");
                selectedUser.add(cancelFrame);
                cancelFrame.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        inputframe.setVisible(false);
                    }
                });
                inputframe.add(selectedUser);
                inputframe.setSize(400, 120);
                inputframe.setTitle("Block User");
                inputframe.setVisible(true);
                inputframe.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                closeFrame.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String usernameTextField = listOnlUsers.getSelectedItem().toString();
                        clientOut.println("BLOCKUSER" + usernameTextField);
                        inputframe.setVisible(false);
                    }
                });
            }
            if (e.getSource() == unBlockUserButton) {
                final JFrame inputframe = new JFrame();
                JPanel selectedUser = new JPanel();
                JLabel req = new JLabel("Select User: ");
                model.removeElement(username);
                String[] onl = new String[model.getSize()];
                for (int i = 0; i < model.getSize(); i++) {
                    onl[i] = model.getElementAt(i).toString();
                }
                final JComboBox listOnlUsers = new JComboBox(onl);
                selectedUser.add(req);
                JButton closeFrame = new JButton("OK");
                model.addElement(username);
                selectedUser.add(listOnlUsers);
                selectedUser.add(closeFrame);
                JButton cancelFrame = new JButton("Cancel");
                selectedUser.add(cancelFrame);
                cancelFrame.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        inputframe.setVisible(false);
                    }
                });
                inputframe.add(selectedUser);

                inputframe.setTitle("Unblock User");
                inputframe.setSize(400, 120);
                inputframe.setVisible(true);
                inputframe.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                closeFrame.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String usernameTextField = listOnlUsers.getSelectedItem().toString();
                        if (usernameTextField.equals("Admin")) {
                            openNotiFrame("Cannot unblock Admin!");
                        } else {
                            clientOut.println("UNBLOCKUSER" + usernameTextField);
                        }
                        inputframe.setVisible(false);
                    }
                });
            }
        }

        @Override
        public void mousePressed(MouseEvent me) {}
        public void mouseReleased(MouseEvent me) {}
        public void mouseEntered(MouseEvent me) {}
        public void mouseExited(MouseEvent me) {}
    }

    class MyActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == loginButton) { //check empty
                if (usernameTextField.getText().equals("") || String.valueOf(passwordLogin.getPassword()).equals("")) {
                    openNotiFrame("Fields are empty .");
                    usernameTextField.setText("");
                    passwordLogin.setText("");
                } else if (serverNoOK == true) {
                    username = usernameTextField.getText().trim();
                    clientOut.println("CHECKUSER" + username + "|" + String.valueOf(passwordLogin.getPassword()).trim());
                }
            }
            if (e.getSource() == signupButton01) {
                loginFrame.add(signupPanel);
                loginPanel.setVisible(false);
                signupPanel.setVisible(true);
            }
            if (e.getSource() == inviteGroupChatButton) {
                final JFrame inputframe = new JFrame();
                JPanel selectedUser = new JPanel();
                JLabel req = new JLabel("Select User: ");
                model.removeElement(username);
                String[] onl = new String[model.getSize()];
                for (int i = 0; i < model.getSize(); i++) {
                    onl[i] = model.getElementAt(i).toString();
                }
                final JComboBox listOnlUsers = new JComboBox(onl);
                selectedUser.add(req);
                JButton closeFrame = new JButton("OK");
                model.addElement(username);
                selectedUser.add(listOnlUsers);
                selectedUser.add(closeFrame);
                JButton cancelFrame = new JButton("Cancel");
                selectedUser.add(cancelFrame);
                cancelFrame.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        inputframe.setVisible(false);
                    }
                });
                inputframe.add(selectedUser);
                inputframe.setTitle("Invite groupchat");
                inputframe.setSize(400, 120);
                inputframe.setVisible(true);
                inputframe.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                closeFrame.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String usernameTextField = listOnlUsers.getSelectedItem().toString();
                        if (usernameTextField.equals("Admin")) {
                            openNotiFrame("Admin could not join group chat.");
                        } else {
                            clientOut.println("GROUP" + usernameTextField);
                        }
                        inputframe.setVisible(false);
                    }
                });
            }
            if (e.getSource() == backButton) { // start loginButton again
                loginPanel.setVisible(true);
                signupPanel.setVisible(false);
            }
            if (e.getSource() == fileTransfer) {
                final JFrame inputframe = new JFrame();
                JPanel selectedUser = new JPanel();
                JLabel req = new JLabel("Select User: ");
                model.removeElement(username);
                String[] onl = new String[model.getSize()];
                for (int i = 0; i < model.getSize(); i++) {
                    onl[i] = model.getElementAt(i).toString();
                }
                final JComboBox listOnlUsers = new JComboBox(onl);
                selectedUser.add(req);
                JButton closeFrame = new JButton("OK");
                model.addElement(username);
                selectedUser.add(listOnlUsers);
                selectedUser.add(closeFrame);
                JButton cancelFrame = new JButton("Cancel");
                selectedUser.add(cancelFrame);
                cancelFrame.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        inputframe.setVisible(false);
                    }
                });
                inputframe.add(selectedUser);
                inputframe.setTitle("File Transfer");
                inputframe.setSize(400, 120);
                inputframe.setVisible(true);
                inputframe.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                closeFrame.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String sender = username;
                        String reciever = listOnlUsers.getSelectedItem().toString();
                        try {
                            JFileChooser chooser = new JFileChooser();
                            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                    "PDF, JAVA, TXT, ZIP", "pdf", "java", "txt", "zip");
                            chooser.setFileFilter(filter);
                            int returnVal = chooser.showOpenDialog(fileTransfer);
                            String type = chooser.getSelectedFile().getName();
                            System.out.println(ip);
                            System.out.print(filePort);
                            if (returnVal == JFileChooser.APPROVE_OPTION && (type.substring(type.lastIndexOf(".") + 1).toLowerCase().equals("pdf") || type.substring(type.lastIndexOf(".") + 1).toLowerCase().equals("java") || type.substring(type.lastIndexOf(".") + 1).toLowerCase().equals("txt") || type.substring(type.lastIndexOf(".") + 1).toLowerCase().equals("zip"))) {
                                clientOut.println("FILE" + sender + ";" + chooser.getSelectedFile().getName() + "*" + ip + "|" + filePort + "," + reciever);
                                fileName = chooser.getSelectedFile().getAbsolutePath();     //save full path if "ACCEPTFILE" request
                            } else {
                                openNotiFrame("Invalid File Location or File Type!");
                            }
                        } catch (NullPointerException npe) {
                            openNotiFrame("No proper file was selected.");
                        }
                        inputframe.setVisible(false);
                    }
                });
            }
            if (e.getSource() == selectStatus) {
                String status = (String) selectStatus.getSelectedItem();
                clientOut.println("STATUS" + status);
            }
            if (e.getSource() == sendMessageButton && !typeArea.getText().equals("")) {
                clientOut.println("PUBLIC" + typeArea.getText());
                typeArea.setText("");
            } else if (!typeArea.getText().equals("")) {
                clientOut.println("PUBLIC" + typeArea.getText());
                typeArea.setText("");
            }
            if (!privateChatTextField.getText().equals("")) {
                String content = privateChatTextField.getText();
                clientOut.println("PRIVATE" + privatePartner + "," + content);
                privateTextArea.append("You: " + content + "\n");
                privateChatTextField.setText("");
            }
            if (!groupTypeArea.getText().equals("")) {
                String content = groupTypeArea.getText();
                if (isMasterGroupChat) {
                    clientOut.println("CHATMASTER" + content);    
                    groupTypeArea.setText("");
                } else if (isMemberGroupChat) {
                    clientOut.println("CHATMEMBER" + masterGroupName + "|" + content);
                    groupTypeArea.setText("");
                }
            }
            if (e.getSource() == privateChatCloseButton) {
                privateChatTextField.setText("");
                privateTextArea.setText("");
                privateChatFrame.dispose();
                isPrivateChat = false;
                // send DELETEPRIVATE signal.
                clientOut.println("DELETEPRIVATE" + privatePartner);
            }
            if (e.getSource() == groupCloseButton) {
                if (isMasterGroupChat) {
                    clientOut.println("CANCELGROUP" + username);
                    isMasterGroupChat = false;
                } else if (isMemberGroupChat) {
                    clientOut.println("CANCELGROUP" + masterGroupName);
                    isMemberGroupChat = false;
                    masterGroupName = null;
                }
                groupChatTextPanel.setText("");
                groupTypeArea.setText("");
                groupChatFrame.dispose();
                isGroupChat = false;
            }
            if (e.getSource() == signupButton02) {
                if (String.valueOf(passwordRegister.getPassword()).equals(String.valueOf(passwordRetypeRegister.getPassword()))) {
                    if (usernameRegister.getText().equals("") || String.valueOf(passwordRegister.getPassword()).equals("")) {
                        openNotiFrame("Fields are empty .");
                        usernameRegister.setText("");
                        passwordRegister.setText("");
                        passwordRetypeRegister.setText("");
                    }
                    else
                        clientOut.println("REGISTER" + usernameRegister.getText().trim() + "|" + String.valueOf(passwordRegister.getPassword()).trim());
                } else {
                    openNotiFrame("Passwords are not the same.");
                    usernameRegister.setText("");
                    passwordRegister.setText("");
                    passwordRetypeRegister.setText("");
                }
            }
        }
    }

    public void writeFile() throws Exception {
        String filename = username + "ChatRecord.txt";
        FileWriter writer = new FileWriter(new File(filename));
        writer.write(chatArea.getText());
        writer.close();
    }

    // establish connection to server and handle input, output streams
    public void run() throws Exception {
        while (!serverNoOK) {
            int port;
            String ipAdd = JOptionPane.showInputDialog(loginPanel, "Please enter the server IP address.", "localhost");               // read server ip addr to create socket to
            if (ipAdd == null) {
                System.exit(0);
            } else if (ipAdd.isEmpty()) {
                JOptionPane.showInputDialog(loginPanel, "Please enter the server IP address", "localhost");
            } else {
                try {
                    clientSocket = new Socket(ipAdd, 60000);
                    clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));               // create socket, input-end
                    clientOut = new PrintWriter(clientSocket.getOutputStream(), true);               // create socket, output-end
                    serverNoOK = true;
                } catch (Exception e1) {
                    openNotiFrame("Connect error! Please check the server IP.");
                    serverNoOK = false;
                }
            }
        }
        while (true) {
            try {
                String input = clientIn.readLine().trim();
                byte[] file = new byte[8000000];
                if (input == null) {
                    return;
                } else if (input.startsWith("ADMINBLOCKED")) {
                    if (input.substring(12).equals(username)) {
                        JOptionPane.showMessageDialog(null, "You has been blocked by admin!");
                        System.exit(0);
                    } else {
                        doc.insertString(doc.getLength(), "* " + input.substring(12) + " just left our chat room * \n", doc.getStyle("notification"));
                        clientOut.println("REFRESHONL");
                        model.clear();
                    }
                } else if (input.startsWith("INCORRECT")) {
                    openNotiFrame("Wrong id or password.");
                    usernameTextField.setText("");
                    passwordLogin.setText("");
                } else if (input.startsWith("ISONLINE")) {
                    openNotiFrame("User is already online.");
                    usernameTextField.setText("");
                    passwordLogin.setText("");
                } else if (input.startsWith("ISBLOCKED")) {
                    openNotiFrame("This user has been blocked! Please contact admin.");
                    usernameTextField.setText("");
                    passwordLogin.setText("");
                    loginFrame.setVisible(false);
                } else if (input.startsWith("LOGINOK")) {
                    loginFrame.setVisible(false);
                    this.setVisible(true);
                    userStatusLabel.setText("Nickname: " + username + "                   Status: ");
                    clientOut.println("CONNECTED" + username);
                } else if (input.startsWith("DUPLICATE")) {
                    openNotiFrame("Duplicate account.");
                    usernameRegister.setText("");
                    passwordRegister.setText("");
                    passwordRetypeRegister.setText("");
                } else if (input.startsWith("REGISTEROK")) {
                    signupPanel.setVisible(false);
                    usernameRegister.setText("");
                    passwordRegister.setText("");
                    passwordRetypeRegister.setText("");
                    loginFrame.add(loginPanel);
                    loginPanel.setVisible(true);
                } else if (input.startsWith("PUBLIC")) {
                    String usernameTextField = input.substring(6, input.indexOf("|", 0));
                    if (!listBlocked.contains(usernameTextField)) {
                        printText(chatArea, input.substring(6, input.indexOf("|", 0)) + ": " + input.substring(input.indexOf("|", 0) + 1));
                    }
                } else if (input.startsWith("CONNECTED")) {
                    doc.insertString(doc.getLength(), "* " + input.substring(9) + " just joined our chat room * \n", doc.getStyle("notification"));
                    clientOut.println("REFRESHONL");
                    model.clear();
                } else if (input.startsWith("ONLINE")) {
                    model.addElement(input.substring(6));
                } else if (input.startsWith("STATUS")) {
                    String stupidShit = input.substring(6);
                    StringTokenizer tokenizer = new StringTokenizer(stupidShit, "|");
                    String usernameTextField = null;
                    String status = null;
                    while (tokenizer.hasMoreElements()) {
                        usernameTextField = tokenizer.nextToken().toString().trim();
                        status = tokenizer.nextToken().toString().trim();
                    }
                    if (!model.contains(usernameTextField)) {
                        model.addElement(usernameTextField);
                    }
                    doc.insertString(doc.getLength(), "* " + usernameTextField + " just update his status to " + status + " *\n", doc.getStyle("notification"));
                } else if (input.startsWith("INVISIBLE")) {
                    model.removeElement(input.substring(9));
                } else if (input.startsWith("PRIVATE")) {
                    String combi = input.substring(7);
                    String partner = combi.substring(0, combi.indexOf("|", 0));
                    String userRequest = combi.substring(combi.indexOf("|", 0) + 1, combi.indexOf(",", 0));
                    String content = combi.substring(combi.indexOf(",", 0) + 1);

                    if (listBlocked.contains(userRequest)) {
                        clientOut.println("BLOCKPRIVATE" + userRequest);
                    } else if (isPrivateChat == false && partner.equals(username)) {
                        privateChatLabel.setText("Private chat with " + userRequest);
                        privateChatFrame.setTitle("Nickname:" + username);
                        privateChatFrame.setVisible(true);
                        privatePartner = userRequest;
                        privateTextArea.append(userRequest + ": " + content + "\n");
                        isPrivateChat = true;
                    } else if (isPrivateChat == true && partner.equals(username) && userRequest.equals(privatePartner)) {
                        privateTextArea.append(userRequest + ": " + content + "\n");

                    } else if (isPrivateChat == true && partner.equals(username) && !userRequest.equals(privatePartner)) {
                        clientOut.println("BUSY" + userRequest);

                    }
                } else if (input.startsWith("BUSY")) {
                    if (input.substring(4).equals(username)) {
                        openNotiFrame("Requested user is busy! Try again later.");
                        privateChatTextField.setText("");
                        privateTextArea.setText("");
                        privateChatFrame.dispose();
                        isPrivateChat = false;
                        privatePartner = null;
                    }
                } else if (input.startsWith("BLOCKPRIVATE")) {
                    if (input.substring(12).equals(username)) {
                        openNotiFrame("You has blocked this usernameTextField from chat.");
                        privateChatTextField.setText("");
                        privateTextArea.setText("");
                        privateChatFrame.dispose();
                        isPrivateChat = false;
                        privatePartner = null;
                    }
                } else if (input.startsWith("VOICECHAT")) {
                    String usernameTextField = input.substring(9, input.indexOf(";"));
                    String ip = input.substring(input.indexOf(";") + 1, input.indexOf("*"));
                    int port = Integer.parseInt(input.substring(input.indexOf("*") + 1));
                    if (usernameTextField.equals(username)) {
                        openNotiFrame("Server starts voice chat with you");
                        final Socket conn = new Socket(ip, port);
                        final JFrame callFr = new JFrame();
                        JPanel callPn = new JPanel();
                        JButton endCall = new JButton("End Call");
                        callPn.add(endCall);
                        callFr.add(callPn);
                        callFr.setSize(400, 120);
                        callFr.setVisible(true);
                        callFr.setTitle("Client Audio Chat");
                        callFr.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent evt) {
                                System.out.println("shjt. close it for me!!!!!!!!");
                                try {
                                    conn.close();
                                } catch (IOException ex) {
                                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                        endCall.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent ae) {
                                callFr.setVisible(false);
                                try {
                                    conn.close();
                                    System.out.println("Call Ended!");
                                } catch (IOException ex) {
                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });

                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                        // Formatting audio data, start microphone recording
                        AudioFormat af = new AudioFormat(8000.0f, 8, 1, true, false);
                        DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
                        TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
                        microphone.open(af);
                        microphone.start();

                        int bytesRead = 0;
                        byte[] soundData = new byte[1];

                        // Reading data in
                        Thread inThread = new Thread(new ReceiveAudio(conn));
                        inThread.start();
                        // Sending data out
                        while (bytesRead != -1) {
                            bytesRead = microphone.read(soundData, 0, soundData.length);
                            if (!conn.isClosed()) {
                                if (bytesRead >= 0) {
                                    try {
                                        dos.write(soundData, 0, bytesRead);
                                    } catch (Exception e) {
                                        conn.close();
                                        openNotiFrame("Data communication error! Exit!");
                                        break;
                                    }
                                }
                            } else {
                                conn.close();
                                openNotiFrame("Server disconnected audio chat!");
                                break;
                            }
                        }
                        microphone.close();
                        microphone.stop();
                        System.out.println("IT IS DONE.");
                    }
                } else if (input.startsWith("REJECTFILE")) {
                    if (input.substring(10, input.indexOf(";")).equals(username)) {
                        openNotiFrame(input.substring(input.indexOf(",") + 1) + " just declined your file.");
                    }
                } else if (input.startsWith("ACCEPTFILE")) {
                    String sender = input.substring(10, input.indexOf(";"));
                    String remoteip = input.substring(input.indexOf("*") + 1, input.indexOf("|"));
                    String onPort = input.substring(input.indexOf("|") + 1, input.indexOf(","));
                    String reciever = input.substring(input.indexOf(",") + 1);
                    String filepath = input.substring(input.indexOf(";") + 1, input.indexOf("*"));
                    int port = Integer.parseInt(onPort);
                    System.out.println(input);
                    if (sender.equals(username)) {
                        doc.insertString(doc.getLength(), "* File transfer to " + reciever + " begin * \n", doc.getStyle("notification"));
                        try {
                            clientOut.println("BEGINFILE" + input.substring(10));
                            Socket client = fileSocket.accept();
                            System.out.println("Accept incoming request");
                            fileFile = new File(fileName);
                            byte[] files = new byte[(int) fileFile.length()];
                            FileInputStream fis = new FileInputStream(fileFile);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            bis.read(files, 0, files.length);
                            OutputStream os = client.getOutputStream();
                            os.write(files, 0, files.length);
                            os.flush();
                            os.close();
                            bis.close();
                            client.close();
                            fileSocket.close();
                            doc.insertString(doc.getLength(), "* File transfer to " + reciever + " completed * \n", doc.getStyle("notification"));
                        } catch (Exception e) {
                            openNotiFrame(e.getMessage());
                        }
                    }
                } else if (input.startsWith("FILE")) {
                    String sender = input.substring(4, input.indexOf(";"));
                    String remoteip = input.substring(input.indexOf("*") + 1, input.indexOf("|"));
                    String onPort = input.substring(input.indexOf("|") + 1, input.indexOf(","));
                    String reciever = input.substring(input.indexOf(",") + 1);
                    String filepath = input.substring(input.indexOf(";") + 1, input.indexOf("*"));
                    int port = Integer.parseInt(onPort);
                    if (reciever.equals(username)) {
                        int confirm = JOptionPane.showConfirmDialog(null, sender + " would like to transfer file to you, Do you accept ?", "File Transfer", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            fileRequest = fileName;
                            clientOut.println("ACCEPTFILE" + input.substring(4));
                        } else {
                            clientOut.println("REJECTFILE" + input.substring(4));
                        }
                    }
                } else if (input.startsWith("BEGINFILE")) {
                    String sender = input.substring(9, input.indexOf(";"));
                    String remoteip = input.substring(input.indexOf("*") + 1, input.indexOf("|"));
                    String onPort = input.substring(input.indexOf("|") + 1, input.indexOf(","));
                    String reciever = input.substring(input.indexOf(",") + 1);
                    String filepath = input.substring(input.indexOf(";") + 1, input.indexOf("*"));
                    int port = Integer.parseInt(onPort);
                    System.out.println("file request: " + filepath);
                    if (reciever.equals(username)) {
                        if (filepath != null) {
                            doc.insertString(doc.getLength(), "* File transfer between you and server begin * \n", doc.getStyle("notification"));
                            try {
                                Socket client1 = new Socket(remoteip, port);
                                System.out.println("requesting reliable socket");
                                int sizeRead;
                                int current;
                                FileOutputStream fos = new FileOutputStream("ClientDownload/" + reciever + "-" + filepath);
                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                InputStream is = client1.getInputStream();
                                sizeRead = is.read(file, 0, file.length);
                                current = sizeRead;
                                do {
                                    sizeRead = is.read(file, current, (file.length - current));
                                    if (sizeRead >= 0) {
                                        current += sizeRead;
                                    }
                                } while (sizeRead > -1);
                                bos.write(file, 0, current);
                                bos.flush();
                                bos.close();
                                client1.close();
                                doc.insertString(doc.getLength(), "* File transfer between you and server completed * \n", doc.getStyle("notification"));
                            } catch (Exception e1) {
                                System.err.println(e1.getMessage());
                            }
                        }
                    }
                } else if (input.startsWith("INVITE")) {
                    String usernameTextField = input.substring(6, input.indexOf("|", 0));
                    String master = input.substring(input.indexOf("|", 0) + 1);
                    if (listBlocked.contains(master) && usernameTextField.equals(username)) {
                        clientOut.println("BLOCKEDGROUP" + master);
                    } else if (! isGroupChat && usernameTextField.equals(username)) {
                        int confirm = JOptionPane.showConfirmDialog(groupChatFrame, master + " would like to invite you to his group, Do you accept ?", "Group chat", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            isGroupChat = true;
                            isMemberGroupChat = true;
                            masterGroupName = master;
                            grupChatPanel.remove(inviteGroupChatButton);
                            grupChatPanel.revalidate();
                            groupChatFrame.revalidate();
                            welcomeGroupChatLabel.setText("Group chat of " + masterGroupName);
                            groupChatFrame.setVisible(true);
                            clientOut.println("ACCEPTINVITE" + master);
                        } else {
                            clientOut.println("REJECTINVITE" + usernameTextField);
                        }
                    } else if (isGroupChat && usernameTextField.equals(username)) {
                        clientOut.println("NOJOIN" + username);
                    }
                } else if (input.startsWith("CHATMES") && isGroupChat){
                    if (input.substring(7, input.indexOf("|", 0)).equals(masterGroupName) || input.substring(7, input.indexOf("|", 0)).equals(username))
                    docGroup.insertString(docGroup.getLength(), input.substring(input.indexOf("|", 0) + 1) + "\n", docGroup.getStyle("regular"));
                } else if (input.startsWith("REJECTINVITE") && isMasterGroupChat) {
                    openNotiFrame(input.substring(12) + " decline to join.");
                } else if (input.startsWith("NOJOIN") && isMasterGroupChat){
                    if(!input.substring(6).equals(username))
                        openNotiFrame(input.substring(6) + " already have a group chat.");
                } else if (input.startsWith("BLOCKEDGROUP")){
                    if(username.equals(input.substring(12))) 
                        openNotiFrame("User has blocked you .");
                } else if (input.startsWith("DELETEPRIVATE") && input.substring(13).equals(username)) {
                    isPrivateChat = false;
                    privatePartner = null;
                    privateTextArea.setText("");
                    privateChatTextField.setText("");;
                    privateChatFrame.dispose();
                        openNotiFrame("Private chat has been cancelled");
                } else if (input.startsWith("CANCELGROUP") && isGroupChat && isMemberGroupChat){
                    if(input.substring(11, input.indexOf("|", 0)).equals(masterGroupName) || input.substring(11, input.indexOf("|", 0)).equals(username))
                    docGroup.insertString(docGroup.getLength(), "* " + input.substring(input.indexOf("|", 0) + 1) + " just left your group chat * \n", docGroup.getStyle("notification"));
                } else if (input.startsWith("CANCELMASTER") && isGroupChat && isMemberGroupChat && input.substring(12).equals(masterGroupName)) {
                    isMemberGroupChat = false;
                    isGroupChat = false;
                    groupChatTextPanel.setText("");
                    groupTypeArea.setText("");
                    masterGroupName = null;
                    openNotiFrame("Group chat is closed by group owner! Goodbye.");
                    groupChatFrame.dispose();
                } else if (input.startsWith("BLOCKEDBEFORE")) {
                    openNotiFrame(input.substring(13) + " has been blocked before .");
                } else if (input.startsWith("BLOCKEDSUCCESS")) {
                    listBlocked.add(input.substring(14));
                    openNotiFrame(input.substring(14) + " has been blocked successfully .");
                } else if (input.startsWith("BLOCKEDERROR")) {
                    openNotiFrame("Cannot block Admin.");
                } else if (input.startsWith("UNBLOCKSUCCESS")) {
                    listBlocked.remove(input.substring(14));
                    openNotiFrame(input.substring(14) + " has been unblocked.");
                } else if (input.startsWith("UNBLOCKEDERROR")) {
                    openNotiFrame(input.substring(14) + " is not blocked yet");
                }
            } catch (SocketException e) {               // if server is down
                JOptionPane.showMessageDialog(null, "There was a problem communicating with server. Exitting.", null, JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } catch (BadLocationException e) {
                openNotiFrame(e.getMessage());
            }
            catch(Exception e){
                System.err.println(e.getMessage());
            }
            finally {
                writeFile();
            }
        }
    }

    public void openNotiFrame(String noti) {
        notificationFrame.setLayout(new GridBagLayout());
        notiMess.setText(noti);
        notiMess.setFont(new Font("Serif", Font.BOLD, 17));
        notiMess.setForeground(Color.RED);
        GridBagConstraints notiFrameConstraint = new GridBagConstraints();
        notiFrameConstraint.insets = new Insets(10, 10, 10, 10);
        notificationFrame.add(notiMess);
        notificationFrame.setBackground(new Color(253, 170, 158));
        notificationFrame.setVisible(true);
        notificationFrame.setLocationRelativeTo(null);
        notificationFrame.setSize(500, 100);
        notificationFrame.setTitle("Notification");
        notificationFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();               // create frame
        client.run();               // connect to server and run
    }
}
