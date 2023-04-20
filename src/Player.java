import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;


public class Player extends Shell {
    /*for the timer we might want to do something like timer += 2 after we set it when the video loads to account for load 
    times in the youtube player so the song doesn't cut off early */
    private int timer;  //this number will be replaced with the song lengthes
    private int stopTime; //currently unused, will be used for pause button
    private int resumeTime; //used for pausing and resuming
    private boolean paused = false; //currently unused, variable will be used to stop timer when song is paused
    private boolean repeat = false;
    private int i = 0; //used to track location in Arraylist
    private String currentSong = "https://www.youtube.com/embed/Zmvt7yFTtt8?autoplay=1"; //will be replaced by top of queue 
    private String testURL = "https://www.youtube.com/embed/MgV-bCxE6ZI?autoplay=1"; //used for testing purposes
    private java.util.List<Song> songList = new ArrayList<Song>();// this is the arraylist for the song list-brian
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
     * @throws IOException 
     * @wbp.parser.constructor
     */
    public Player(Display display) throws IOException {
        super(display, SWT.SHELL_TRIM);
        setSize(598, 431);
        setMaximumSize(new Point(600, 400));
        setMinimumSize(new Point(600, 400));
        
     // load the songs from the JSON file
        songList = Song.loadSongsFromJson("songsList.json");

        // create the hashmap to store songs by genre
        HashMap<String, ArrayList<Song>> songsByGenre = new HashMap<>();

        for (Song song : songList) {
            String genre = song.getGenre();
            if (!songsByGenre.containsKey(genre)) {
                songsByGenre.put(genre, new ArrayList<Song>());
            }
            songsByGenre.get(genre).add(song);
        }

        // create the tree widget
        Tree tree = new Tree(this, SWT.BORDER);
        tree.setBounds(285, 20, 150, 200);

        // populate the tree with genres as top-level items
        for (String genre : songsByGenre.keySet()) {
            TreeItem genreItem = new TreeItem(tree, SWT.NONE);
            genreItem.setText(genre);

            // add the songs for this genre as children of the genre item
            for (Song song : songsByGenre.get(genre)) {
                TreeItem songItem = new TreeItem(genreItem, SWT.NONE);
                songItem.setText(song.getName());
                songItem.setData(song);
            }
        }

		Label lblArtist = new Label(this, SWT.NONE);
		lblArtist.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblArtist.setBounds(10, 10, 81, 25);
		lblArtist.setText("Artist:");
		
		Label lblWhenSongPlays = new Label(this, SWT.NONE);
		lblWhenSongPlays.setBounds(10, 38, 332, 25);
		lblWhenSongPlays.setText("When song plays artist name goes here");
		
		Label lblAlbum = new Label(this, SWT.NONE);
		lblAlbum.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblAlbum.setBounds(10, 69, 81, 25);
		lblAlbum.setText("Album:");
		
		Label lblWhenSongPlays_1 = new Label(this, SWT.NONE);
		lblWhenSongPlays_1.setBounds(10, 100, 332, 25);
		lblWhenSongPlays_1.setText("When song plays album name goes here");
		
		Label lblSong = new Label(this, SWT.NONE);
		lblSong.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblSong.setBounds(10, 131, 81, 25);
		lblSong.setText("Song:");
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setBounds(10, 162, 332, 25);
		lblNewLabel.setText("When song plays song name goes here");
		
		Button btnRepeat = new Button(this, SWT.NONE);
		btnRepeat.setBounds(36, 279, 105, 35);
		btnRepeat.setText("Repeat");
		btnRepeat.addListener(SWT.Selection, event -> {
			//adds current song to the next index on the Arraylist
			if (repeat = false) {
				repeat = true;
				btnRepeat.setText("UnRepeat");
			}
			else {
				repeat = false; 
				btnRepeat.setText("Repeat");
			}
			songList.add(i+1, songList.get(i));
		});
		
		Button btnRestart = new Button(this, SWT.NONE);
		btnRestart.setBounds(168, 279, 105, 35);
		btnRestart.setText("Restart");
		btnRestart.addListener(SWT.Selection, event -> {
			Control[] controls = Player.this.getChildren();
			for (Control control : controls) {
				if (control instanceof Browser) {
					control.dispose();
					}//end if
			}//end for loop
			Browser browser = new Browser(this, SWT.NONE);
			browser.setBounds(50, 50, 200, 200);
			browser.setUrl(songList.get(i).getUrl());
			timer = songList.get(i).getDuration();
		
		});
		
		
		tree.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            // Get the selected item in the tree
	            TreeItem selectedItem = (TreeItem) event.item;
	            
	            // Get the Song object associated with the selected item
	            Song selectedSong = (Song) selectedItem.getData();
	            
	            // Update the currentSong URL if a song is selected
	            if (selectedSong != null) {
	                currentSong = selectedSong.getUrl();
	                lblWhenSongPlays.setText(selectedSong.getArtist());
	                lblWhenSongPlays_1.setText(selectedSong.getAlbum());
	                lblNewLabel.setText(selectedSong.getName());
	                  
	            }
	        }
	    });
		
		btnRepeat.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        // Get the selected item in the tree
		        TreeItem selectedItem = tree.getSelection()[0];
		        
		        // Get the Song object associated with the selected item
		        Song selectedSong = (Song) selectedItem.getData();
		        
		        // Update the currentSong URL if a song is selected
		        if (selectedSong != null) {
		            currentSong = selectedSong.getUrl();
		            System.out.println("Playing " + selectedSong.getUrl());
		        }
		    }
		});
		
		Button btnPause = new Button(this, SWT.NONE);
		btnPause.setBounds(299, 279, 105, 35);
		btnPause.setText("Pause");
		btnPause.addListener(SWT.Selection, event -> {
			/* pauses the video */
			if(paused == true) {
				Browser browser = new Browser(this, SWT.NONE);
				browser.setBounds(50, 50, 200, 200);
				browser.setUrl(songList.get(i).getUrl() + "&start=" + resumeTime);
				btnPause.setText("Pause");
				paused = false;
				}//end if
			else {
				paused = true;
				stopTime = timer;
				resumeTime = songList.get(i).getDuration() - stopTime;
				Control[] controls = Player.this.getChildren();
				for (Control control : controls) {
					if (control instanceof Browser) {
						control.dispose();
						}//end if
				}//end for loop
			btnPause.setText("Resume"); }//end else
			});
		
		
		Button btnSkip = new Button(this, SWT.NONE);
		btnSkip.setBounds(429, 279, 105, 35);
		btnSkip.setText("Skip");
		btnSkip.addListener(SWT.Selection, event -> {
			/* skips the current song */
			timer = 0;
			if (i == songList.size()-1) {
				Control[] controls = Player.this.getChildren();
				for (Control control : controls) {
					if (control instanceof Browser) {
						control.dispose();
					}//end if
				}//end for
			}//end if
			});
		createContents(); 
		//loads the first song
		Browser browser = new Browser(this, SWT.NONE);
		browser.setBounds(50, 50, 1, 1);
		browser.setUrl(songList.get(i).getUrl());
		lblWhenSongPlays.setText(songList.get(i).getArtist());
		lblWhenSongPlays_1.setText(songList.get(i).getAlbum());
		lblNewLabel.setText(songList.get(i).getAlbum());
		timer = songList.get(i).getDuration();
		startTimer(display);
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
		if(paused == false) {
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
						if (repeat = false) {
						i++;}//end if
						// Remove the Browser widget so we can load the second one
						Control[] controls = Player.this.getChildren();
						for (Control control : controls) {
							if (control instanceof Browser) {
								control.dispose();
							}//end if
						}//end for
						if (i == songList.size() - 1) { //if statment to check if current song is the last in the list
							return;
						}//end if
						//creating the new browser
						Browser browser = new Browser(Player.this, SWT.NONE);
						browser.setBounds(50, 50, 1, 1);
						browser.setUrl(songList.get(i).getUrl());
						// Set the timer variable to 36 for testing purposes will be changed later
						timer = songList.get(i).getDuration();
						//start timer method will be called again here
					}//end else
				}
			});
			}//end if
		}//end StartTimer method


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}