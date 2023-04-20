package MusicPlayer;

import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.SWTResourceManager;

public class Player extends Shell {
	/*for the timer we might want to do something like timer += 2 after we set it when the video loads to account for load 
	times in the youtube player so the song doesn't cut off early */
	private int timer = 5; //this number will be replaced with the song length, set to five for testing purposes
	private int stopTime; //currently unused, will be used for pause button
	private boolean paused = false; //currently unused, variable will be used to stop timer when song is paused
	private String currentSong = "https://www.youtube.com/embed/Zmvt7yFTtt8?autoplay=1"; //will be replaced by top of queue 
	private String testURL = "https://www.youtube.com/embed/MgV-bCxE6ZI?autoplay=1"; //used for testing purposes
	private ArrayList<Song> songlist = new ArrayList<Song>();// this is the arraylist for the song list-brian
	
	
	  private Song song;
	    
	    public Player(String artist, String album, String name, String url, int yearReleased, String genre, int duration) {
	        this.song = new Song(artist, album, name, url, yearReleased, genre, duration);
	    }
	    
	    
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			Player shell = new Player(display);
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

	/**
	 * Create the shell.
	 * @param display
	 * @wbp.parser.constructor
	 */
	public Player(Display display) {
		super(display, SWT.SHELL_TRIM);
		setMaximumSize(new Point(600, 400));
		setMinimumSize(new Point(600, 400));
		// load the songs from the JSON file
		
		
		
		
		
		
		
		
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setBounds(0, 0, 585, 362);
		
		//Now playing tab controls:
		TabItem tbtmNowPlaying = new TabItem(tabFolder, SWT.NONE);
		tbtmNowPlaying.setText("Now Playing");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmNowPlaying.setControl(composite);
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setBounds(29, 268, 116, 45);
		btnNewButton.setText("Repeat");
		
		Button btnPlay = new Button(composite, SWT.NONE);
		btnPlay.setText("Play");
		btnPlay.setBounds(161, 268, 116, 45);
		btnPlay.addListener(SWT.Selection, event -> {
			/* creates the browser that has the embedded youtube video, made the size 1 by 1 so it is invisible. Anytime a
			a new song gets loaded the startTimer method will need to be called */
			Browser browser = new Browser(this, SWT.NONE);
			browser.setBounds(50, 50, 1, 1);
			browser.setUrl(currentSong);
			startTimer(display);
		});
		
		Button btnPause = new Button(composite, SWT.NONE);
		btnPause.setText("Pause");
		btnPause.setBounds(295, 268, 116, 45);
		
		Button btnSkip = new Button(composite, SWT.NONE);
		btnSkip.setText("Skip");
		btnSkip.setBounds(429, 268, 116, 45);
		
		ProgressBar progressBar = new ProgressBar(composite, SWT.NONE);
		progressBar.setBounds(29, 232, 516, 19);
		
		Label lblArtist = new Label(composite, SWT.NONE);
		lblArtist.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblArtist.setBounds(10, 10, 55, 15);
		lblArtist.setText("Artist: ");
		
		Label lblAlbum = new Label(composite, SWT.NONE);
		lblAlbum.setText("Album: ");
		lblAlbum.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblAlbum.setBounds(10, 61, 55, 15);
		
		Label lblSong = new Label(composite, SWT.NONE);
		lblSong.setText("Song: ");
		lblSong.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblSong.setBounds(10, 117, 55, 15);
		
		Label lblnameplaying = new Label(composite, SWT.NONE);
		lblnameplaying.setText("When song plays artist name goes here");
		lblnameplaying.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblnameplaying.setBounds(10, 31, 239, 15);
		
		Label lblalbumplaying = new Label(composite, SWT.NONE);
		lblalbumplaying.setText("When song plays album name goes here");
		lblalbumplaying.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblalbumplaying.setBounds(10, 82, 239, 15);
		
		Label lblsongplaying = new Label(composite, SWT.NONE);
		lblsongplaying.setText("When song plays song name goes here");
		lblsongplaying.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblsongplaying.setBounds(10, 138, 239, 15);
		//Playlist tab controls:
		TabItem playlistTab = new TabItem(tabFolder, SWT.NONE);
		playlistTab.setText("Playlists");
				
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		playlistTab.setControl(composite_1);
				
		List playlistList = new List(composite_1, SWT.BORDER);
		playlistList.setBounds(10, 10, 71, 68); //spacing TBD
		//load all playlists into this list and assign a variable for getselected
				
		Button selectButton = new Button(composite_1, SWT.NONE);
		selectButton.setText("Play");
		selectButton.setBounds(240, 279, 105, 35);
		//add listener to play the selected playlist from the list
				
				
		//Playlist Builder Tab Controls:
		TabItem playlistBuilderTab = new TabItem(tabFolder, SWT.NONE);
		playlistBuilderTab.setText("Create Playlists");
				
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		playlistBuilderTab.setControl(composite_2);
				
		List allSongs = new List(composite_2, SWT.BORDER);
		allSongs.setBounds(10, 25, 150, 268);//spacing TBD
				
		List songsToAdd = new List(composite_2, SWT.BORDER);
		songsToAdd.setBounds(410, 25, 150, 268);//spacing TBD
				
		Button rightArrow = new Button(composite_2, SWT.NONE);
		rightArrow.setText(">");
		rightArrow.setBounds(280, 95, 35, 35);
		//add listener to add song selected on right to left list
				
		Button leftArrow = new Button(composite_2, SWT.NONE);
		leftArrow.setText("<");
		leftArrow.setBounds(280, 140, 35, 35);
		//add listener to move song selected on left off of the list
				
		Button createPlaylistButton = new Button(composite_2, SWT.NONE);
		createPlaylistButton.setText("create playlist");
		createPlaylistButton.setBounds(240, 279, 105, 35);
		//add listener to generate new playlist and add it to the list of playlists located on tab 2
				
		Label playlistNameLabel = new Label(composite_2, SWT.NONE);
		playlistNameLabel.setText("Playlist Name: ");
		playlistNameLabel.setBounds(195, 10, 80, 25);
				
		Text playlistNameField = new Text(composite_2, SWT.BORDER);
		playlistNameField.setText("");
		playlistNameField.setBounds(280, 10, 100, 20);
		
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Music Player");
		setSize(450, 300);

	}
	/**
	 * This method is a timer that decrements the timer variable by 1 every second, the code to make the progress bar will
	 * be going in here
	 * 
	 * It also loads the next song when the timer hits zero, right now there are only two set songs but it'll pull 
	 * whatever's next in the queue. It does this by removing the browser object and creating a new one
	 * 
	 * will add more code later to stop timer when btnpause is pressed and save the current time, when the paused song is
	 * resumed we will remove the video, then reload it at the time saved in stopTime and then do timer = timer - stopTime 
	 * before we call the startTimer method
	 * @param display
	 */
	public void startTimer(Display display) {
	    ProgressBar progressBar = new ProgressBar(this, SWT.NONE);
	    progressBar.setBounds(26, 237, 518, 14);

	    display.timerExec(1000, new Runnable() {
	        public void run() {
	            if (timer > 0) {
	                timer--;
	                display.asyncExec(new Runnable() {
	                    public void run() {
	                        //code to affect progress bar will go here
	                    }
	                });
	                display.timerExec(1000, this);
	            } else {
	                // Stop the timer
	                display.timerExec(-1, this);
	                // Remove the Browser widget so we can load the second one
	                Control[] controls = Player.this.getChildren();
	                for (Control control : controls) {
	                    if (control instanceof Browser) {
	                        control.dispose();
	                    }
	                }
	                //creating the new browser
	                Browser browser = new Browser(Player.this, SWT.NONE);
	                browser.setBounds(50, 50, 1, 1);
	                browser.setUrl(testURL);
	                // Set the timer variable to 36 for testing purposes will be changed later
	                timer = 36;
	                //start timer method will be called again here
	            }
	        }
	    });
	}
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
