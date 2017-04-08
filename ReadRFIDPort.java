package com.company;

/**
 * Created by SF on 2-4-2017.
 */
import java.util.Scanner;
import java.io.*;
import java.util.*;
import gnu.io.*; // for rxtxSerial library

public class ReadRFIDPort implements SerialPortEventListener {
    static CommPortIdentifier portId;
    static CommPortIdentifier saveportId;
    static Enumeration        portList;
    InputStream           inputStream;
    SerialPort           serialPort;


    public static void main(String[] args) {
        boolean           portFound = false;
        String           rfidPort;
        Scanner input = new Scanner (System.in);
        System.out.println("Please enter the port here");
        rfidPort = input.next();
        System.out.println("Set default port to "+ rfidPort);

        // parse ports and if the default port is found, initialized the reader
        portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals(rfidPort)) {
                    System.out.println("Found port: "+rfidPort);
                    portFound = true;

                    ReadRFIDPort reader = new ReadRFIDPort();
                }
            }

        }
        if (!portFound) {
            System.out.println("port " + rfidPort + "not found.");
        }

    }

    public ReadRFIDPort() {
        // initalize serial port
        try {
            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
        } catch (PortInUseException e) {}

        try {
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {}

        try {
            serialPort.addEventListener(this);
        } catch (TooManyListenersException e) {}

        // activate the DATA_AVAILABLE notifier
        serialPort.notifyOnDataAvailable(true);

        try {
            // set port parameters
            serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {}



    }

    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
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
            case SerialPortEvent.DATA_AVAILABLE:
                // we get here if data has been received
                byte[] readBuffer = new byte[40];
                try {
                    // read data
                    while (inputStream.available() > 0) {
                        int numBytes = inputStream.read(readBuffer);
                    }
                    // print data
                    String result  = new String(readBuffer);
                    System.out.println(result);

                } catch (IOException e) {}

                break;
        }
    }

}