package whatsappbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class Controller {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private Label lblTest;
    @FXML
    private TextArea areaText;
    @FXML
    private CheckBox checkbox;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    private File file;

    @FXML
    void SelectFile(ActionEvent event) {
	final JFileChooser fc = new JFileChooser();
	// In response to a button click:
	fc.showOpenDialog(null);
//	System.out.println(fc.getSelectedFile().getAbsolutePath());
	if (fc.getSelectedFile() != null) {
	    file = fc.getSelectedFile();
	    lblTest.setText(file.getAbsolutePath());
	 }
    }

    @FXML
    void StartBot(ActionEvent event) {
	if(file == null) {
	    JOptionPane.showMessageDialog(null, "Select numbers file", "", JOptionPane.WARNING_MESSAGE);
	    return;
	}
	final String text = areaText.getText();
	
	if (text.length() >= 65536) {
	    JOptionPane.showMessageDialog(null, "Message too long " + text.length(), "", JOptionPane.WARNING_MESSAGE);
	    return;
	}

	new Thread() {
	    @Override
	    public void run() {
		try {
		    App.startBot(file, text, checkbox.isSelected());
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}.start();
    }

}
