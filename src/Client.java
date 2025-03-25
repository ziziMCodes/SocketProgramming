import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        final File[] fileToSend = new File[1];
        JFrame jframe = new JFrame("Zizi's client");
        jframe.setSize(450,450);
        jframe.setLayout(new BoxLayout(jframe.getContentPane(), BoxLayout.Y_AXIS));
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //program stops when gui exited

        JLabel jtitle = new JLabel("Zizi's File Sender");
        jtitle.setFont(new Font("Arial", Font.BOLD, 25));
        jtitle.setBorder(new EmptyBorder(20,0,10,0));
        jtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jlFilename = new JLabel("Choose a file to send");
        jlFilename.setFont(new Font("Arial", Font.BOLD, 20));
        jlFilename.setBorder(new EmptyBorder(50,0,0,0));
        jlFilename.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButton = new JPanel();
        jpButton.setBorder(new EmptyBorder(75,0,10,0));

        JButton jbsendFile = new JButton("Send file");
        jbsendFile.setPreferredSize(new Dimension(150,75));
        jbsendFile.setFont(new Font("Arial", Font.BOLD, 20));

        JButton jbChooseFile = new JButton("Choose file");
        jbChooseFile.setPreferredSize(new Dimension(150,75));
        jbChooseFile.setFont(new Font("Arial", Font.BOLD, 20));

        jpButton.add(jbsendFile);
        jpButton.add(jbChooseFile);

        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Choose a file to send");
                //parent == null so that the filechooser appears in middle of screen
                if(jFileChooser.showOpenDialog(null) == jFileChooser.APPROVE_OPTION){
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jlFilename.setText("The file you want to send is: " + fileToSend[0].getName());
                }
            }
        });

        jbsendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileToSend[0] == null){
                    jlFilename.setText("Please choose a file first");
                } else{
                    try {
                        //can now write into this file
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                        Socket socket = new Socket("localhost", 1234);

                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        String filename = fileToSend[0].getName();
                        byte[] filenameBytes = filename.getBytes();

                        byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
                        fileInputStream.read(fileContentBytes);

                        //how much data is going to be sent based on the filename
                        dataOutputStream.writeInt(filenameBytes.length);
                        //actual filename
                        dataOutputStream.write(filenameBytes);

                        dataOutputStream.writeInt(fileContentBytes.length);
                        dataOutputStream.write(fileContentBytes);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        jframe.add(jtitle);
        jframe.add(jlFilename);
        jframe.add(jpButton);
        jframe.setVisible(true);
    }
}
