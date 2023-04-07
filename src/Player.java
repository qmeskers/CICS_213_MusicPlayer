import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Player extends JFrame {

	private JPanel contentPane;
	private JLabel lblCurrentArtist;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Player frame = new Player();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Player() {
		setTitle("Music Player");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() { // play button action method
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPlay.setBounds(124, 212, 89, 23);
		contentPane.add(btnPlay);
		
		JButton btnskip = new JButton("Skip");
		btnskip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {// action to skip 
			}
		});
		btnskip.setBounds(324, 212, 89, 23);
		contentPane.add(btnskip);
		
		JButton btnRepeat = new JButton("Repeat");
		btnRepeat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {// action to repeat
			}
		});
		btnRepeat.setBounds(25, 212, 89, 23);
		contentPane.add(btnRepeat);
		
		JProgressBar progressBar = new JProgressBar();// progress bar to show how length of song if its possible to implement
		progressBar.setBounds(10, 187, 403, 14);
		contentPane.add(progressBar);
		
		JLabel lbl_Artist = new JLabel("Artist");
		lbl_Artist.setBounds(28, 11, 46, 14);
		contentPane.add(lbl_Artist);
		
		JLabel lbl_Album = new JLabel("Album");
		lbl_Album.setBounds(28, 63, 46, 14);
		contentPane.add(lbl_Album);
		
		JLabel lbl_SongName = new JLabel("Song Name");
		lbl_SongName.setBounds(28, 119, 74, 14);
		contentPane.add(lbl_SongName);
		
		JButton btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {// action for pause
			}
		});
		btnPause.setBounds(225, 212, 89, 23);
		contentPane.add(btnPause);
		
		lblCurrentArtist = new JLabel("When song plays artist goes here");// this displays the current Artist playingg. replace text with an array element or string
		lblCurrentArtist.setBounds(25, 25, 376, 14);
		contentPane.add(lblCurrentArtist);
		
		JLabel lblCurrentAlbum = new JLabel("When song plays album goes here");// this displays the current album playingg. replace text with an array element or string
		lblCurrentAlbum.setBounds(25, 78, 388, 14);
		contentPane.add(lblCurrentAlbum);
		
		JLabel lblCurrentSong = new JLabel("When song plays the name of song goes here"); // this displays the current song playingg. replace text with an array element or string
		lblCurrentSong.setBounds(28, 134, 376, 14);
		contentPane.add(lblCurrentSong);
	}
}