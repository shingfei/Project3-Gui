package com.company;

/**
 * Created by shing on 28-3-2017.
 */
import gnu.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

public class ReadCard implements SerialPortEventListener {
    private static CardLayout cardTest = new CardLayout();

    // String pagina binden met elke panel als ze wisselen.
    String pagina;
    //First panel
    JButton scan = new JButton("Scan uw kaart");
    JButton sluitprogramma = new JButton("Exit program");
    static JPanel mainPanel = new JPanel();
    static JPanel buttonPanel = new JPanel(new GridBagLayout());
    static JPanel titlePanel = new JPanel();
    Font typecharacters = new Font("Times New Roman", Font.BOLD, 30);

    // ok Panel
    JButton okButton = new JButton("Ok [A]");
    JButton homeButton = new JButton("Exit [D]");
    static JPanel buttonokpanel = new JPanel(new GridBagLayout());
    static JPanel oktopPanel = new JPanel();

    //second panel Menu
    static JPanel topPanel = new JPanel();
    static JPanel centerPanel = new JPanel();
    static JPanel westPanel = new JPanel();
    static JPanel eastPanel = new JPanel();
    static JPanel bottomPanel = new JPanel();
    JButton exit = new JButton("Exit[D]");
    JButton returnButton = new JButton("Return[C]");
    JButton saldoButton = new JButton("Saldoweergeven[A]");
    JButton anderButton = new JButton("Ander Bedrag[B]");
    JButton tienEuro = new JButton("10 euro [1]");
    JButton twintigEuro = new JButton("20 euro [2]");
    JButton vijftigEuro = new JButton("50 euro[3]");

    //third panel saldo weergeven
    static JPanel saldoTop = new JPanel();
    static JPanel saldoCenter = new JPanel(new GridBagLayout());
    static JPanel saldoBottom = new JPanel();
    JButton exitSaldo = new JButton("Exit[D]");
    JButton returnSaldo = new JButton("Return[C]");

    //fourth panel ander weergeven
    static JPanel anderTop = new JPanel();
    static JPanel anderCenter = new JPanel(new GridBagLayout());
    static JPanel anderBottom = new JPanel();
    JTextField anderField = new JTextField(10);

    JButton exitAnder = new JButton("Exit[D]");
    JButton returnAnder = new JButton("Return[C]");
    JButton enterAnder = new JButton("Bevestig[A]");
    JButton clearAnder = new JButton("Clear [*]");
    JButton backSpaceAnder = new JButton("Backspace [#]");


    //Fifth panel Bevestiging
    static JPanel bevestigTop = new JPanel();
    static JPanel bevestigCenter = new JPanel(new GridBagLayout());
    static JPanel bevestigBottom = new JPanel();
    JButton exitBevestig = new JButton("Exit[D]");
    JButton jaBevestig = new JButton("Ja[A]");
    JButton neeBevestig = new JButton("Nee[B]");

    //Sixth panel printbon
    static JPanel printTop = new JPanel();
    static JPanel printCenter = new JPanel(new GridBagLayout());
    JButton neeBon = new JButton("Nee[B]");
    JButton jaBon = new JButton("Ja[A]");

    // Seventh panel pin
    static JPanel pinTop = new JPanel();
    static JPanel pinBot = new JPanel();
    JPasswordField pinField = new JPasswordField(6);
    static JPanel pinCenter = new JPanel(new GridBagLayout());
    JButton enter = new JButton("Enter[A]");
    JButton backspace = new JButton("Backspace [#]");
    JButton clear = new JButton("Clear [*]");
    JButton exitPin = new JButton("Exit [D]");
    JLabel pinLabel = new JLabel("Voer uw pincode in");
    JButton hiddenButton = new JButton();

    JLabel geldBevestiging = new JLabel(); //Tekst voor de hoeveelheid opnemen van de user
    float saldo = 40; // saldo van de user
    int a; // variable voor input geld.
    int kansen;
    int secondsAll;
    int secondsPin;

    timer timerCounter = new timer();

    public class timer
    {
        Timer timer = new Timer();
        TimerTask task = new TimerTask()
        {
            public void run()
            {
                secondsAll++;
                secondsPin++;
                if(secondsPin >60 && pagina == "pin")
                {
                    kansen = kansen + 1;
                    System.out.println(kansen);
                    secondsPin = 0;
                    if(kansen == 1 && pagina == "pin")
                    {
                        pinLabel.setText("U heeft nog 2 pogingen");
                    }
                    else if(kansen == 2 && pagina == "pin")
                    {
                        pinLabel.setText("U heeft nog 1 poging");
                    }
                    else if(kansen == 3 && pagina == "pin")
                    {
                        enter.setEnabled(false);
                        pinLabel.setText("Uw pas is geblokkeerd");
                        try {
                            TimeUnit.SECONDS.sleep(2);
                            cardTest.show(mainPanel, "home");
                            pagina = "home";
                            enter.setEnabled(true);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(secondsAll >60)
                {
                    cardTest.show(mainPanel,"home");
                }
            }
        };

        public void start()
        {
            timer.scheduleAtFixedRate(task, 1000, 1000);
        }
    }

    public ReadCard() {
        readFrame();
        timerCounter.start();
    }

    private void initialize() {
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }
        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);
            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            // open the streams!
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            inputStream = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            // add event listeners
            serialPort.addEventListener((SerialPortEventListener) this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());

        }
    }

    private void nullmaker() {
        pinField.setText(null);
        anderField.setText(null);
    }

    SerialPort serialPort;
    /**
     * The port we're normally going to use.
     */
    private static final String PORT_NAMES[] = {
            // "/dev/tty.usbmodem1d11", // Mac OS X
            //"/dev/ttyUSB0", // Linux
            "COM3", "COM4" // Windows
    };

    private BufferedReader input;
    /**
     * The output stream to the port
     */
    private OutputStream output;
    private InputStream inputStream;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;

    private static final int DATA_RATE = 9600;

    public void readFrame() {

        pinField.setEditable(false);
        anderField.setEditable(false);
        initialize();
        JFrame mainFrame = new JFrame();
       // mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setSize(new Dimension(1000,1000));
        mainFrame.setUndecorated(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        mainPanel.setLayout(cardTest); // in de main panel wordt de cardlayout gebruikt.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 50;
        scan.setFont(typecharacters);
        scan.setPreferredSize(new Dimension(400, 100));

        JLabel title = new JLabel("Welcome to Mesosbank(W.I.P.)");
        title.setFont(typecharacters);
        titlePanel.add(title);
        buttonPanel.add(scan, gbc);
        JPanel homePag = new JPanel();
        homePag.setLayout(new BorderLayout());
        homePag.add(titlePanel, BorderLayout.NORTH);
        homePag.add(buttonPanel, BorderLayout.CENTER);
        geldBevestiging.setFont(typecharacters);
/////////////////// main menu
        JPanel pMenu = new JPanel();
        pMenu.setLayout(new BorderLayout());
        JLabel titleMenu = new JLabel("Mesosbank mainmenu(W.I.P.)");
        titleMenu.setFont(typecharacters);
        JLabel snelGeld = new JLabel("Opties voor snelopnemen");
        JLabel blankLabel = new JLabel("Welkom in het hoofdmenu");
        snelGeld.setFont(typecharacters);
        tienEuro.setPreferredSize(new Dimension(200, 50));
        tienEuro.setFont(typecharacters);
        twintigEuro.setPreferredSize(new Dimension(200, 50));
        twintigEuro.setFont(typecharacters);
        vijftigEuro.setPreferredSize(new Dimension(200, 50));
        vijftigEuro.setFont(typecharacters);
        blankLabel.setFont(typecharacters);
        saldoButton.setFont(typecharacters);
        anderButton.setFont(typecharacters);
        returnButton.setPreferredSize(new Dimension(200, 50));
        returnButton.setFont(typecharacters);
        exit.setPreferredSize(new Dimension(200, 50));
        exit.setFont(typecharacters);

        centerPanel.setLayout(new GridBagLayout());

        gbc.gridx = 0;
        gbc.gridy = 50;
        centerPanel.add(blankLabel,gbc);
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.PAGE_AXIS));
        westPanel.add(snelGeld);
        westPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        westPanel.add(tienEuro);
        westPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        westPanel.add(twintigEuro);
        westPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        westPanel.add(vijftigEuro);

        topPanel.add(titleMenu);

        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.PAGE_AXIS));
        eastPanel.add(saldoButton);
        eastPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        eastPanel.add(anderButton);

        bottomPanel.add(returnButton);
        bottomPanel.add(exit);

        pMenu.add(westPanel, BorderLayout.WEST);
        pMenu.add(topPanel, BorderLayout.NORTH);
        pMenu.add(centerPanel, BorderLayout.CENTER);
        pMenu.add(eastPanel, BorderLayout.EAST);
        pMenu.add(bottomPanel, BorderLayout.SOUTH);
///////////saldo
        JPanel saldoPanel = new JPanel();
        saldoPanel = saldoWeergave();
//////// ander
        JPanel anderePage = new JPanel();
        anderePage = andereBedrag();
////////bevestig
        JPanel bevestigPag = new JPanel();
        bevestigPag = bevestiging();
//// bon printen
        JPanel bonPag = new JPanel();
        bonPag = bonPrint();
///// pin
        JPanel pinPage = new JPanel();
        pinPage = pinMaken();

        JPanel okPage = new JPanel();
        okPage = okPanel();
///
        mainPanel.add(homePag, "home");
        mainPanel.add(okPage, "ok");// panel, benaming van de main panel.
        mainPanel.add(pinPage, "pin");
        mainPanel.add(pMenu, "menu"); // Panel, de benaming van de panel.
        mainPanel.add(saldoPanel, "saldoPage");
        mainPanel.add(anderePage, "anderPage");
        mainPanel.add(bevestigPag, "bevestigPage");
        mainPanel.add(bonPag, "bonPage");
        mainFrame.add(mainPanel);
        pagina = "home";

        //First panel
        scan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "ok");
                pagina = "ok";
                pinField.setText("");
            }
        });
        sluitprogramma.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });

        //Second panel main
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "home");
                pagina = "home";
                anderField.setText("");
                pinField.setText("");
            }
        });

        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "home");
                anderField.setText("");
                pinField.setText("");
                pagina = "home";
            }
        });

        saldoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "saldoPage");
                pagina = "saldoPage";
            }
        });

        anderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "anderPage");
                pagina = "anderPage";
                anderField.setText("");
                pinField.setText("");
            }
        });
        tienEuro.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int a = 10;
                if(a> saldo)
                {
                    System.out.println("Werkt 10 euro");
                    blankLabel.setForeground(Color.red);
                    blankLabel.setText("Ovoldoende saldo.");
                }
                else if(a<saldo) {
                    cardTest.show(mainPanel, "bevestigPage");
                    geldBevestiging.setText("Wilt u " + a + " euro opnemen?");
                   blankLabel.setText("Welkom in het hoofdmenu");
                    blankLabel.setForeground(Color.BLACK);
                    pagina = "bevestigPage";
                }

            }
        });
        twintigEuro.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                a = 20;
                if(a> saldo)
                {
                    System.out.println("Werkt20 euro");
                    blankLabel.setForeground(Color.red);
                    blankLabel.setText("Ovoldoende saldo.");
                }
                else if(a<saldo) {
                    cardTest.show(mainPanel, "bevestigPage");
                    geldBevestiging.setText("Wilt u " + a + " euro opnemen?");
                    blankLabel.setText("Welkom in het hoofdmenu");
                    blankLabel.setForeground(Color.BLACK);
                    pagina = "bevestigPage";
                }
            }
        });
        vijftigEuro.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                a = 50;
                if(a> saldo)
                {
                    System.out.println("Werkt 50 euro");
                    blankLabel.setForeground(Color.red);
                    blankLabel.setText("Ovoldoende saldo.");
                }
                else if(a<saldo) {
                    cardTest.show(mainPanel, "bevestigPage");
                    geldBevestiging.setText("Wilt u " + a + " euro opnemen?");
                    blankLabel.setText("Welkom in het hoofdmenu");
                    blankLabel.setForeground(Color.BLACK);
                    pagina = "bevestigPage";
                }
            }
        });

        return;
    }

    private JPanel okPanel()
    {
        JPanel okPanel = new JPanel();
        okPanel.setLayout(new BorderLayout());
        JLabel okLabel = new JLabel("Bevestig uw kaart");
        GridBagConstraints gbc = new GridBagConstraints();

        buttonokpanel.setFont(typecharacters);
        okLabel.setFont(typecharacters);
        okButton.setFont(typecharacters);
        okButton.setPreferredSize(new Dimension(200, 100));
        homeButton.setFont(typecharacters);
        homeButton.setPreferredSize(new Dimension(200, 100));

        buttonokpanel.add(okLabel,gbc);
        gbc.gridx = 0;
        gbc.gridy = 50;
        buttonokpanel.add(okButton,gbc);
        gbc.gridx = 200;
        gbc.gridy = 50;
        buttonokpanel.add(homeButton,gbc);

        okPanel.add(buttonokpanel,BorderLayout.CENTER);
        okPanel.add(oktopPanel, BorderLayout.NORTH);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                {
                    cardTest.show(mainPanel, "pin");
                    pagina = "pin";
                    pinField.setText("");

                }
            }
        });

        homeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                {
                    cardTest.show(mainPanel, "home");
                    pagina = "home";
                    pinField.setText("");

                }
            }
        });

        return okPanel;
    }
    private JPanel pinMaken() {
        JPanel pM = new JPanel();
        pM.setLayout(new BorderLayout());
        pinLabel.setFont(typecharacters);
        GridBagConstraints gbc = new GridBagConstraints();
        enter.setFont(typecharacters);
        backspace.setFont(typecharacters);
        clear.setFont(typecharacters);
        enter.setPreferredSize(new Dimension(200, 50));
        pinField.setFont(typecharacters);
        pinField.setPreferredSize(new Dimension(200, 50));
        pinField.setHorizontalAlignment(SwingConstants.CENTER);

        exitPin.setPreferredSize(new Dimension(200, 50));
        exitPin.setFont(typecharacters);

        gbc.gridx = 150;
        gbc.gridy = 0;
        pinCenter.add(pinLabel,gbc);
        gbc.gridx = 0;
        gbc.gridy = 200;
        pinCenter.add(enter, gbc);
        gbc.gridx = 150;
        gbc.gridy = 200;
        pinCenter.add(backspace, gbc);
        gbc.gridx = 300;
        gbc.gridy = 200;
        pinCenter.add(clear, gbc);
        gbc.gridx = 150;
        gbc.gridy = 100;
        pinCenter.add(pinField, gbc);

        pinBot.add(exitPin);
        pM.add(pinBot, BorderLayout.SOUTH);
        pM.add(pinTop, BorderLayout.NORTH);
        pM.add(pinCenter);

        /// Seventh panel pin
        enter.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae) {
                {
                    if(checkPassword() == false) {
                        cardTest.show(mainPanel, "menu");
                        pagina = "menu";
                        System.out.println("Toegang ");
                        kansen = 0;
                        pinLabel.setText("Voer uw pincode in");
                    }
                    else if(checkPassword() == true && kansen == 0)
                    {
                        pinLabel.setText("U heeft nog 2 pogingen");
                        kansen = 1;
                    }
                    else if(checkPassword() == true && kansen == 1)
                    {
                        pinLabel.setText("U heeft nog 1 poging over");
                        kansen = 2;
                    }
                    else if(checkPassword() == true && kansen == 2)
                    {
                        enter.setEnabled(false);
                        pinLabel.setText("Uw pas is geblokkeerd");
                        try {
                            TimeUnit.SECONDS.sleep(2);
                            cardTest.show(mainPanel, "home");
                            pagina = "home";
                            enter.setEnabled(true);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        exitPin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                {
                    cardTest.show(mainPanel, "home");
                    pagina = "home";
                    pinLabel.setText("Voer uw pincode in");

                }
            }
        });
        return pM;
    }

    private JPanel bonPrint() {
        JPanel bonPage = new JPanel();
        bonPage.setLayout(new BorderLayout());
        JLabel titleBon = new JLabel("Mesosbank(W.I.P.)");
        JLabel vraagBon = new JLabel("Wilt u een bon?");
        GridBagConstraints gbc = new GridBagConstraints();
        printTop.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleBon.setFont(typecharacters);
        vraagBon.setFont(typecharacters);
        jaBon.setFont(typecharacters);
        jaBon.setPreferredSize(new Dimension(200, 50));
        neeBon.setFont(typecharacters);
        neeBon.setPreferredSize(new Dimension(200, 50));

        printTop.add(titleBon);
        gbc.gridx = 0;
        gbc.gridy = 0;
        printCenter.add(vraagBon, gbc);
        gbc.gridx = 0;
        gbc.gridy = 200;
        printCenter.add(jaBon, gbc);
        gbc.gridx = 5;
        gbc.gridy = 200;
        printCenter.add(neeBon, gbc);
        bonPage.add(printTop, BorderLayout.NORTH);
        bonPage.add(printCenter, BorderLayout.CENTER);
        ///////////////// Sixth panel print bon
        jaBon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "home");
                anderField.setText("");
                pinField.setText("");
                pagina = "home";
            }
        });
        neeBon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "home");
                anderField.setText("");
                pinField.setText("");
                pagina = "home";
            }
        });
        return bonPage;
    }

    private JPanel bevestiging() {
        JPanel bevestigPage = new JPanel();
        bevestigPage.setLayout(new BorderLayout());
        JLabel titleBevestig = new JLabel("Mesosbank bevestiging menu(W.I.P.)");
        GridBagConstraints gbc = new GridBagConstraints();
        titleBevestig.setFont(typecharacters);
        jaBevestig.setFont(typecharacters);
        jaBevestig.setPreferredSize(new Dimension(200, 50));
        neeBevestig.setFont(typecharacters);
        neeBevestig.setPreferredSize(new Dimension(200, 50));
        exitBevestig.setPreferredSize(new Dimension(200, 50));
        exitBevestig.setFont(typecharacters);

        bevestigTop.add(titleBevestig);

        gbc.gridx = 0;
        gbc.gridy = 0;
        bevestigCenter.add(geldBevestiging, gbc);
        gbc.gridx = 0;
        gbc.gridy = 200;
        bevestigCenter.add(jaBevestig, gbc);
        gbc.gridx = 5;
        gbc.gridy = 200;
        bevestigCenter.add(neeBevestig, gbc);
        bevestigBottom.add(exitBevestig);
        bevestigPage.add(bevestigTop, BorderLayout.NORTH);
        bevestigPage.add(bevestigCenter);
        bevestigPage.add(bevestigBottom, BorderLayout.SOUTH);
        /////// fifth panel bevestigPage
        exitBevestig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "home");
                pagina = "home";
            }
        });

        neeBevestig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "menu");
                pagina = "menu";
            }
        });

        jaBevestig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                   cardTest.show(mainPanel, "bonPage");
                   pagina = "bonPage";
            }
        });
        return bevestigPage;
    }

    private JPanel andereBedrag() {
        JPanel anderPage = new JPanel();
        JLabel titleAnder = new JLabel("Mesosbank ander bedrag menu(W.I.P.)");
        JLabel textAnder = new JLabel("Voer uw bedrag in:");
        JLabel textVoorbeeld = new JLabel("Geen bedragen met eenheden(1, 12, 23 etc.)");
        textAnder.setFont(typecharacters);
        titleAnder.setFont(typecharacters);
        enterAnder.setFont(typecharacters);
        anderField.setFont(typecharacters);
        backSpaceAnder.setFont(typecharacters);
        clearAnder.setFont(typecharacters);
        textVoorbeeld.setFont(typecharacters);
        returnAnder.setPreferredSize(new Dimension(200, 50));
        returnAnder.setFont(typecharacters);
        exitAnder.setPreferredSize(new Dimension(200, 50));
        exitAnder.setFont(typecharacters);

        anderPage.setLayout(new BorderLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        anderField.setHorizontalAlignment(SwingConstants.CENTER);
        anderTop.add(titleAnder);
        gbc.gridx = 150;
        gbc.gridy = 0;
        anderCenter.add(textAnder, gbc);
        gbc.gridx = 150;
        gbc.gridy = 50;
        anderCenter.add(textVoorbeeld, gbc);
        gbc.gridx = 150;
        gbc.gridy = 100;
        anderCenter.add(anderField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 300;
        anderCenter.add(enterAnder, gbc);
        gbc.gridx = 150;
        gbc.gridy = 300;
        anderCenter.add(backSpaceAnder, gbc);
        gbc.gridx = 300;
        gbc.gridy = 300;
        anderCenter.add(clearAnder, gbc);
        anderBottom.add(returnAnder);
        anderBottom.add(exitAnder);
        anderPage.add(anderTop, BorderLayout.NORTH);
        anderPage.add(anderCenter);
        anderPage.add(anderBottom, BorderLayout.SOUTH);
        /////////////// fourth panel ander bedrag

        enterAnder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if(checkEental() == false)
                {
                    textVoorbeeld.setForeground(Color.RED);
                    textVoorbeeld.setText("Geen bedragen met eenheden(1, 12, 23 etc.)");

                }
                else if(checkEental() == true)
                {
                    if(checkSaldo() == true)
                    {
                        textVoorbeeld.setForeground(Color.RED);
                        textVoorbeeld.setText("Onvoldoende saldo, check uw saldo A.U.B.");
                    }
                    else if(checkSaldo() == false)
                    {
                        cardTest.show(mainPanel, "bevestigPage");
                        textVoorbeeld.setText("Geen bedragen met eenheden(1, 12, 23 etc.)");
                        geldBevestiging.setText("Wilt u " + anderField.getText() + " euro opnemen?");
                        textVoorbeeld.setForeground(Color.black);
                        pagina = "bevestigPage";
                    }
                }
            }
        });
        //}
        exitAnder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "home");
                anderField.setText("");
                pagina = "home";
                textVoorbeeld.setText("Geen bedragen met eenheden(1, 12, 23 etc.)");
                textVoorbeeld.setForeground(Color.black);
            }
        });

        returnAnder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "menu");
                pagina = "menu";
                textVoorbeeld.setText("Geen bedragen met eenheden(1, 12, 23 etc.)");
                textVoorbeeld.setForeground(Color.black);
            }
        });
        return anderPage;
    }

    private JPanel saldoWeergave() {
        JPanel saldoPage = new JPanel();
        JLabel titleSaldo = new JLabel("Mesosbank saldoweergave(W.I.P.)");
        titleSaldo.setFont(typecharacters);
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel huidigeSaldo = new JLabel("Uw huidige saldo is " + saldo + " euro");
        huidigeSaldo.setFont(typecharacters);
        returnSaldo.setPreferredSize(new Dimension(200, 50));
        returnSaldo.setFont(typecharacters);
        exitSaldo.setPreferredSize(new Dimension(200, 50));
        exitSaldo.setFont(typecharacters);

        saldoCenter.add(huidigeSaldo, gbc);
        saldoPage.setLayout(new BorderLayout());
        saldoTop.add(titleSaldo);
        saldoBottom.add(returnSaldo);
        saldoBottom.add(exitSaldo);
        saldoPage.add(saldoCenter);
        saldoPage.add(saldoTop, BorderLayout.NORTH);
        saldoPage.add(saldoBottom, BorderLayout.SOUTH);

        //Third panel Saldo
        exitSaldo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "home");
                anderField.setText("");
                pagina = "home";
            }
        });

        returnSaldo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cardTest.show(mainPanel, "menu");
                pagina = "menu";
            }
        });
        return saldoPage;
    }

    private boolean checkEental()
    {
        int getal = Integer.parseInt(anderField.getText());
        int remainderEental = 10;
        int eental = getal % remainderEental;
        boolean checkEental;

        if(eental == 0 && getal >0)
        {
            System.out.println("Eental systeem");
            checkEental = true;
            return checkEental;
        }
        else {
            System.out.println("niet goed.");
            checkEental = false;
            return checkEental;
        }

    }

    private boolean checkSaldo()
    {
        int getal = Integer.parseInt(anderField.getText());
        boolean checkSaldo;

        if(getal> saldo)
        {
            System.out.println("Niet genoeg saldo werkt");
            checkSaldo = true;
            return checkSaldo;
        }
        else
        {
            checkSaldo = false;
            System.out.println("Genoeg saldo");
            return checkSaldo;
        }

    }

    private boolean checkPassword()
    {
        String pinPassword = new String(pinField.getPassword());
        boolean checkPassword;
        if(pinPassword.equals("0000"))
        {
            System.out.println("Pass werkt");
            checkPassword = false;
            return checkPassword;
        }
        else
            {
                System.out.println("Niet goed wachtwoord");
                checkPassword = true;
                return checkPassword;
            }
    }
    ///////////////////// serialprinlnt
    public void serialEvent(SerialPortEvent spe) {
        switch (spe.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE: {
                try {
                    byte[] readBuffer = new byte[40];
                    String inputLine = input.readLine();
                    System.out.println(inputLine);
                    int nummer = 0;
                    int numBytes = inputStream.read(readBuffer);
                    //home scan page
                    if (inputLine.equals("D") && pagina == "home") {
                        sluitprogramma.doClick(400);}
                    else if(numBytes>nummer && pagina == "home"){scan.doClick(300); nummer =1; break;}
                    else if(inputLine.equals("A") && pagina == "home") {
                        scan.doClick(300);}
                    // ok page
                    else if(inputLine.equals("A") && pagina == "ok"){okButton.doClick(300);}

                    else if (inputLine.equals("A") && pagina == "ok") {
                        okButton.doClick(100);
                    }
                    else if (inputLine.equals("D") && pagina == "ok") {
                        homeButton.doClick(400);
                    }
                    //pin invoeren
                    else if (pagina == "pin" &&  pinField.getText().length()>5){pinField.setText("");}
                    else if(inputLine.equals("A") && pagina == "pin") {enter.doClick(400);}
                    else if (inputLine.equals("D") && pagina == "pin") {exit.doClick(300);}
                    else if (inputLine.equals("B") && pagina == "pin") {inputLine = null;}
                    else if (inputLine.equals("C") && pagina == "pin") {inputLine = null;}
                    else if (inputLine.equals("*") && pagina == "pin") nullmaker();
                    else if (pinField.getText().length() > 3 && pagina == "pin")
                    {if (inputLine.equals("*")) nullmaker();
                    else if (inputLine.equals("#") && pagina == "pin")
                        pinField.setText(pinField.getText().substring(0, pinField.getText().length() - 1));
                    }
                    else if (inputLine.equals("#")) {
                        pinField.setText(pinField.getText().substring(0, pinField.getText().length() - 1));
                        anderField.setText(anderField.getText().substring(0, anderField.getText().length() - 1));
                    }

                    //menu
                    else if (inputLine.equals("1") && pagina == "menu") {
                        tienEuro.doClick(300);
                    } else if (inputLine.equals("2") && pagina == "menu") {
                        twintigEuro.doClick(300);
                    } else if (inputLine.equals("3") && pagina == "menu") {
                        vijftigEuro.doClick(300);
                    } else if (inputLine.equals("A") && pagina == "menu") {
                        saldoButton.doClick(300);
                    } else if (inputLine.equals("B") && pagina == "menu") {
                        anderButton.doClick(300);
                    } else if (inputLine.equals("D") && pagina == "menu") {
                        exit.doClick(300);
                    }
                    else if (inputLine.equals("C") && pagina == "menu") {
                        returnButton.doClick(300);
                    }
                    //Bevestig
                    else if (inputLine.equals("A") && pagina == "bevestigPage") {
                        jaBevestig.doClick(300);
                    } else if (inputLine.equals("B") && pagina == "bevestigPage") {
                        neeBevestig.doClick(300);
                    } else if (inputLine.equals("D") && pagina == "bevestigPage") {
                        exitBevestig.doClick(300);
                    }

                    //Saldo
                    else if (inputLine.equals("C") && pagina == "saldoPage") {
                        returnSaldo.doClick(300);
                    } else if (inputLine.equals("D") && pagina == "saldoPage") {
                        exitSaldo.doClick(300);
                    }
                    //andere saldo
                    else if (pagina == "anderPage" &&  anderField.getText().length()>5){anderField.setText("");}
                    else if (inputLine.equals("C") && pagina == "anderPage") {
                        returnAnder.doClick(300);
                    } else if (inputLine.equals("D") && pagina == "anderPage") {
                        exitAnder.doClick(300);
                    } else if (inputLine.equals("A") && pagina == "anderPage") {
                        enterAnder.doClick(300);
                    } else if (inputLine.equals("*") && pagina == "anderPage") nullmaker();
                    else if (anderField.getText().length() > 3 && pagina == "anderPage") {
                        if (inputLine.equals("*")) nullmaker();
                        else if (inputLine.equals("#"))
                            anderField.setText(anderField.getText().substring(0, anderField.getText().length() - 1));
                    }
                    else if (inputLine.equals("B") && pagina == "anderPage") {
                        inputLine = null;
                    }
                    //bon page
                    else if (inputLine.equals("A") && pagina == "bonPage") {
                        jaBon.doClick(300);
                    } else if (inputLine.equals("B") && pagina == "bonPage") {
                        neeBon.doClick(300);
                    }
                    else {
                        pinField.setText(pinField.getText() + inputLine);
                        anderField.setText(anderField.getText() + inputLine);
                        secondsAll = 0;
                    }

                } catch (IOException e) {
                    System.err.println(e.toString());
                }
            }
        }
    }
}
