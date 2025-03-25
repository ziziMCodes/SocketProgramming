import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.catalog.Catalog;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static ArrayList<MyFile> myFiles = new ArrayList<>(); //holds each of the objects client sends over

    public static void main(String[] args) throws IOException {

        int fileId = 0;
        JFrame jFrame = new JFrame("Zizi's Server");
        jFrame.setSize(400,400);
        //everything stacked vertically
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jlTitle = new JLabel("Zizi's File receiver");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20,0,10,0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        jFrame.add(jlTitle);
        jFrame.add(jScrollPane);
        jFrame.setVisible(true);
        //SS waits for requests to come in over the network, performs operation, returns a result
        ServerSocket serverSocket = new ServerSocket(1234);

        while (true) {
            try {
                //new socket to communicate over once client joins
                Socket socket = serverSocket.accept();
                //get incoming data from client
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                int fileNameLength = dataInputStream.readInt();

                /*
                The data input stream contains the data sent from client
                We can read data from this input stream and write it to an output stream
                e.g. a file output stream to create a file
                 */

                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength];
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);

                    int fileContentLength = dataInputStream.readInt();
                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);

                        JPanel jpFileRow = new JPanel();
                        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));

                        JLabel jlFileName = new JLabel(fileName);
                        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                        jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));
                        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

                        if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                            jpFileRow.setName(String.valueOf(fileId));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);
                            jFrame.validate(); //another file added, changes shape of jframe
                        } else{
                            jpFileRow.setName(String.valueOf(fileId));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);

                            jFrame.validate();
                        }
                        myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                        //id increments
                        fileId++;

                    }

                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }
        public static String getFileExtension(String filename){
            //wouldn't work with .tar.gz
            int i = filename.lastIndexOf('.');
            if(i>0){
                //want to return second half of string
                return filename.substring(i+1);
            }
            else{
                return "No Extension found";
            }
        }
        public static MouseListener getMyMouseListener() {
            return new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    JPanel jPanel = (JPanel) e.getSource();
                    int fileId = Integer.parseInt(jPanel.getName());
                    for (MyFile files : myFiles) {
                        if (files.getId() == fileId) {
                            JFrame jfPreview = createFrame(files.getName(), files.getData(), files.getFileExtention());
                            jfPreview.setVisible(true);
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            };
        }
        public static JFrame createFrame(String filename, byte[] filedata, String fileExtension){
            JFrame jFrame = new JFrame("Zizi's File Downloader");
            jFrame.setSize(400,400);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel label = new JLabel("Zizi's File Downloader");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setFont(new Font("Arial", Font.BOLD, 25));
            label.setBorder(new EmptyBorder(20,0,10,0));

            JLabel prompt = new JLabel("Are you sure you want to download: "+ filename);
            prompt.setFont(new Font("Arial", Font.BOLD, 20));
            prompt.setBorder(new EmptyBorder(20,0,10,0));
            prompt.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton yes = new JButton("Yes");
            yes.setPreferredSize(new Dimension(150,75));
            yes.setFont(new Font("Arial", Font.BOLD, 20));
            yes.setBorder(new EmptyBorder(20,0,10,0));

            JButton no = new JButton("No");
            no.setPreferredSize(new Dimension(150,75));
            no.setFont(new Font("Arial", Font.BOLD, 20));
            no.setBorder(new EmptyBorder(20,0,10,0));

            JLabel fileContent = new JLabel();
            fileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel buttonsBox = new JPanel();
            buttonsBox.setBorder(new EmptyBorder(20,0,10,0));
            buttonsBox.add(yes);
            buttonsBox.add(no);

            if(fileExtension.equalsIgnoreCase("txt")){
                fileContent.setText("<html>" + new String(filedata) + "</html>");
            } else{
                fileContent.setIcon(new ImageIcon(filedata));
            }
            yes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    File fileToDownload = new File(filename);

                    try{
                        FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                        fileOutputStream.write(filedata);
                        fileOutputStream.close();

                        jFrame.dispose();
                    } catch (IOException err){
                        err.printStackTrace();
                    }
                }
            });

            no.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jFrame.dispose();
                }
            });
            panel.add(label);
            panel.add(prompt);
            panel.add(fileContent);
            panel.add(buttonsBox);

            jFrame.add(panel);
            return jFrame;
        }
}
