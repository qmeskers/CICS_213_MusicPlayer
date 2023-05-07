/**
 * This class creates the initial login page for user management within the player class
 * @author CISC213.N81
 */
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Box;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class loginWindow extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField usernameField;
	private JPasswordField passwordField;

	/**
	 * Create the dialog.
	 */
	public loginWindow() {
		setBounds(100, 100, 450, 150);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			Box verticalBox = Box.createVerticalBox();
			contentPanel.add(verticalBox);
			{
				Box usernameBox = Box.createHorizontalBox();
				verticalBox.add(usernameBox);
				{
					JLabel usernameLabel = new JLabel("Username: ");
					usernameBox.add(usernameLabel);
				}
				{
					usernameField = new JTextField();
					usernameBox.add(usernameField);
					usernameField.setColumns(10);
				}
			}
			{
				Box passwordBox = Box.createHorizontalBox();
				verticalBox.add(passwordBox);
				{
					JLabel lblNewLabel_1 = new JLabel("Password: ");
					passwordBox.add(lblNewLabel_1);
				}
				{
					passwordField = new JPasswordField();
					passwordBox.add(passwordField);
					passwordField.setColumns(10);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.CENTER);
			buttonPane.setLayout(fl_buttonPane);
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				/**
				 * Button listener for the login button
				 */
				JButton loginButton = new JButton("Login");
				buttonPane.add(loginButton);
				loginButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						//iterate through the list of users.  if a match is found, set that user to currentUser and display their playlists
						//otherwise show an error
						for (User user : Player.userList) {
							if (user.getUsername().equalsIgnoreCase(usernameField.getText()) 
									&& (user.getPassword().equals(passwordField.getText()))){
								launchMainApp(user);
							}
						}
						if (Player.currentUser == null) {
							JOptionPane.showMessageDialog(null, "Username or password incorrect, please try again");
						}
					}
				});//end login button listener
			}
			{
				/**
				 * button listener for the guest user button
				 */
				JButton guestButton = new JButton("Continue As Guest");
				buttonPane.add(guestButton);
				guestButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						/**
						 * set currentUser to "guest", a special account with no playlists or meaningful user data, and proceed
						 */
						for (User user : Player.userList) {
							if (user.getUsername().equals("guest")){
								launchMainApp(user);
							}
						}//end guest button listener
					}
				});
			}
			{
				/**
				 * button listener for the new account button
				 */
				JButton newAccountButton = new JButton("Create New Account");
				buttonPane.add(newAccountButton);
				newAccountButton.addMouseListener(new MouseAdapter() {
					//because the program defaults to the new user setup screen, this button only needs to close the login prompt
					//it launches with a guest user to prevent playlists from loading in
					public void mouseClicked(MouseEvent e) {
						for (User user : Player.userList) {
							if (user.getUsername().equals("guest")) {
								launchMainApp(user);
							}
						}
					}
				});//end new account listener
			}
		}
	}

	public void launchMainApp(User user) {
		try {
			dispose();
			Player.currentUser = user;
			Display display = Display.getDefault();
			Player shell = new Player(display);
			shell.addListener(SWT.Close, new Listener()
		    {
		        public void handleEvent(Event event)
		        {
		            shell.dispose();
		            System.exit(ABORT);
		        }
		    });

			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
