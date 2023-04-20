import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;


public class Player extends Shell {
	/*for the timer we might want to do something like timer += 2 after we set it when the video loads to account for load 
	times in the youtube player so the song doesn't cut off early */
	private int timer = 5; //this number will be replaced with the song length, set to five for testing purposes
	private int stopTime; //currently unused, will be used for pause button
	private boolean paused = false; //currently unused, variable will be used to stop timer when song is paused
	private String currentSong = "https://www.youtube.com/embed/Zmvt7yFTtt8?autoplay=1"; //will be replaced by top of queue 
	private String testURL = "https://www.youtube.com/embed/MgV-bCxE6ZI?autoplay=1"; //used for testing purposes
	private ArrayList<Song> songs = new ArrayList<>();
	private ArrayList<Song> songlist = new ArrayList<Song>();// this is the arraylist for the song list-brian
	
	
	  private Song song;
	    
	    public Player(String artist, String album, String name, String url, int yearReleased) {
	        this.song = new Song(artist, album, name, url, yearReleased);
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
	 * @throws IOException 
	 */
	public Player(Display display) throws IOException {
		super(display, SWT.SHELL_TRIM);
		setMaximumSize(new Point(600, 400));
		setMinimumSize(new Point(600, 400));
		  // load the songs from the JSON file
	   
	   
		
	
		

		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setBounds(0, 0, 600, 400);
		
		
		//Now playing tab controls:
		TabItem playerTab = new TabItem(tabFolder, SWT.NONE);
		playerTab.setText("Now Playing");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		playerTab.setControl(composite);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.verticalSpacing = 10;
		composite.setLayout(gl_composite);
		
		
		Label lblArtist = new Label(composite, SWT.NONE);
		lblArtist.setSize(34, 15);
		lblArtist.setBounds(10, 9, 81, 25);
		lblArtist.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblArtist.setText("Artist:");
		
		Label lblWhenSongPlays = new Label(composite, SWT.NONE);
		lblWhenSongPlays.setSize(206, 15);
		lblWhenSongPlays.setBounds(10, 39, 332, 25);
		lblWhenSongPlays.setText("When song plays artist name goes here");
		
		Label lblAlbum = new Label(composite, SWT.NONE);
		lblAlbum.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblAlbum.setBounds(10, 69, 81, 25);
		lblAlbum.setText("Album:");
		
		Label lblWhenSongPlays_1 = new Label(composite, SWT.NONE);
		lblWhenSongPlays_1.setBounds(10, 100, 332, 25);
		lblWhenSongPlays_1.setText("When song plays album name goes here");
		
		Label lblSong = new Label(composite, SWT.NONE);
		lblSong.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblSong.setBounds(10, 131, 81, 25);
		lblSong.setText("Song:");
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(10, 162, 332, 25);
		lblNewLabel.setText("When song plays song name goes here");
		
		Button btnRepeat = new Button(composite, SWT.NONE);
		btnRepeat.setBounds(36, 279, 105, 35);
		btnRepeat.setText("Repeat");
		
		Button btnPlay = new Button(composite, SWT.NONE);
		btnPlay.setBounds(168, 279, 105, 35);
		btnPlay.setText("Play");
		btnPlay.addListener(SWT.Selection, event -> {
			/* creates the browser that has the embedded youtube video, made the size 1 by 1 so it is invisible. Anytime a
			a new song gets loaded the startTimer method will need to be called */
			Browser browser = new Browser(this, SWT.NONE);
			browser.setBounds(50, 50, 1, 1);
			browser.setUrl(currentSong);
			startTimer(display);
		});
		
		Button btnPause = new Button(composite, SWT.NONE);
		btnPause.setBounds(299, 279, 105, 35);
		btnPause.setText("Pause");
		
		Button btnSkip = new Button(composite, SWT.NONE);
		btnSkip.setBounds(429, 279, 105, 35);
		btnSkip.setText("Skip");
		
		
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
