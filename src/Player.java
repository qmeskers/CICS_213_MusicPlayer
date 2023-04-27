import java.io.IOException;
import java.net.http.WebSocket.Listener;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.GridData;

@SuppressWarnings("unused")
public class Player extends Shell {
    /*for the timer we might want to do something like timer += 2 after we set it when the video loads to account for load 
    times in the youtube player so the song doesn't cut off early */
    private int timer;  //this number will be replaced with the song lengths
    private int stopTime; //used for pause button
    private int resumeTime; //used for pausing and resuming
    private boolean paused = false; //used to stop timer when song is paused
	private boolean repeat = false;//used tell if song has been repeated
    private int i = 0; //used to track location in Arraylist
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
    @SuppressWarnings("static-access")
	public Player(Display display) throws IOException {
        super(display, SWT.SHELL_TRIM);
        setMaximumSize(new Point(600, 500));
        setMinimumSize(new Point(600, 500));
        setSize(709, 474);
        
        TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setBounds(0, 0, 576, 457);
		
		//Now playing tab controls:
		TabItem playerTab = new TabItem(tabFolder, SWT.NONE);
		playerTab.setText("Now Playing");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		playerTab.setControl(composite);
		GridLayout gl_composite = new GridLayout(5, false);
		gl_composite.verticalSpacing = 10;
		composite.setLayout(gl_composite);
		
		        // create the tree widget
		        Tree tree = new Tree(composite, SWT.BORDER);
		        tree.setBounds(285, 20, 150, 200);
		        new Label(composite, SWT.NONE);
		        new Label(composite, SWT.NONE);
		        new Label(composite, SWT.NONE);
		        new Label(composite, SWT.NONE);
		        
		        		Label lblArtist = new Label(composite, SWT.NONE);
		        		lblArtist.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		        		lblArtist.setBounds(10, 10, 81, 25);
		        		lblArtist.setText("Artist:");
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		
		        		Label lblartistplaying = new Label(composite, SWT.NONE);
		        		lblartistplaying.setBounds(10, 38, 332, 25);
		        		lblartistplaying.setText("When song plays artist name goes here");
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		
		        		Label lblAlbum = new Label(composite, SWT.NONE);
		        		lblAlbum.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		        		lblAlbum.setBounds(10, 69, 81, 25);
		        		lblAlbum.setText("Album:");
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		
		        		Label lblalbumplaying = new Label(composite, SWT.NONE);
		        		lblalbumplaying.setBounds(10, 100, 332, 25);
		        		lblalbumplaying.setText("When song plays album name goes here");
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		
		        		Label lblSong = new Label(composite, SWT.NONE);
		        		lblSong.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		        		lblSong.setBounds(10, 131, 81, 25);
		        		lblSong.setText("Song:");
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		
		        		Label lblsongplaying = new Label(composite, SWT.NONE);
		        		lblsongplaying.setBounds(10, 162, 332, 25);
		        		lblsongplaying.setText("When song plays song name goes here");
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		new Label(composite, SWT.NONE);
		        		//ProgressBar used to track location within song
		        		ProgressBar progressBar = new ProgressBar(composite, SWT.NONE);
		        		progressBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 5, 1));
		        		progressBar.setMinimum(0);
		        		//button used to restart the current song
		        		Button btnRestart = new Button(composite, SWT.NONE);
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
		        			browser.setBounds(50, 50, 1, 1);
		        			browser.setUrl(songList.get(i).getUrl());
		        			timer = songList.get(i).getDuration();
		        		
		        		});//end btnRestart event
		        		//button used to start playing the playlist
		        		Button btnPlay = new Button(composite, SWT.NONE);
		        		btnPlay.setText("Play");
		        		btnPlay.addListener(SWT.Selection, event -> {
		        			Browser browser = new Browser(this, SWT.NONE);
		        			browser.setBounds(50, 50, 1, 1);
		        			browser.setUrl(songList.get(i).getUrl());
		        			timer = songList.get(i).getDuration();
		        			progressBar.setMaximum(songList.get(i).getDuration());
		        			startTimer(display, progressBar);
		        			UpdateLabels(lblsongplaying, lblalbumplaying, lblartistplaying);
		        		});//end btnPlay event
		        		//button used to repeat the current song once it's finished playing
		        		Button btnRepeat = new Button(composite, SWT.NONE);
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
		        		});//end btnRepeat event
		        		
		        	/*Note from Nick C - I don't understand the purpose of this event if the person who wrote it could explain
		        	why it's using btnRepeat that would be wonderful */
		        		btnRepeat.addSelectionListener(new SelectionAdapter() {
		        		    @Override
		        		    public void widgetSelected(SelectionEvent e) {
		        		        // Get the selected item in the tree
		        		        TreeItem selectedItem = tree.getSelection()[0];
		        		        
		        		        // Get the Song object associated with the selected item
		        		        Song selectedSong = (Song) selectedItem.getData();
		        		        
		        		        // Update the currentSong URL if a song is selected
		        		        if (selectedSong != null) {
		        		        	Control[] controls = Player.this.getChildren();
		        		            if (selectedSong != null) {
		        		            	for (Control control : controls) {
		        	    					if (control instanceof Browser) {
		        	    						((Browser) control).setUrl(selectedSong.getUrl());
		        	    						}//end if
		        	    				}//end for loop
		        		            	timer = selectedSong.getDuration();
		        		            	startTimer(display,progressBar);
		        		            System.out.println("Playing " + selectedSong.getUrl());
		        		        }
		        		    }
		        		    }
		        		});
		        		
		        		
		    tree.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            // Get the selected item in the tree
	            TreeItem selectedItem = (TreeItem) event.item;
	            
	            // Get the Song object associated with the selected item
	            Song selectedSong = (Song) selectedItem.getData();
	            songList.add(selectedSong);
	            
	            // Update the currentSong URL if a song is selected
	            Control[] controls = Player.this.getChildren();
	            if (selectedSong != null) {
	            	for (Control control : controls) {
    					if (control instanceof Browser) {
    						control.dispose();
    						}//end if
    				}//end for loop
	            	Browser browser = new Browser(Player.this, SWT.NONE);
					browser.setBounds(50, 50, 1, 1);
					browser.setUrl(songList.get(i).getUrl());
	            	timer = selectedSong.getDuration();
	            	startTimer(display,progressBar);
	                lblartistplaying.setText(selectedSong.getArtist());
	                lblalbumplaying.setText(selectedSong.getAlbum());
	                lblsongplaying.setText(selectedSong.getName());
	                  
	            }
	        }
	    });
		        		
		        		Button btnPause = new Button(composite, SWT.NONE);
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
		        			btnPause.setText("Resume");
		        			startTimer(display,progressBar);}//end else
		        			});
		        		
		        		
		        		Button btnSkip = new Button(composite, SWT.NONE);
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
        
     // load the songs from the JSON file
        songList = Songs.loadSongsFromJson("songsList.json");
    	lblartistplaying.setText(songList.get(i).getArtist());
		lblalbumplaying.setText(songList.get(i).getAlbum());
		lblsongplaying.setText(songList.get(i).getAlbum());

        // create the hashmap to store songs by genre
        HashMap<String, ArrayList<Song>> songsByGenre = new HashMap<>();

        for (Song song : songList) {
        	
            String genre = song.getGenre();
            if (!songsByGenre.containsKey(genre)) {
                songsByGenre.put(genre, new ArrayList<Song>());
            }
            songsByGenre.get(genre).add(song);
        }

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
		createContents(); 
		PlaylistCollections playlistCollections = new PlaylistCollections();
		//add listener to play the selected playlist from the list
		
		
		//Playlist Builder Tab Controls:
		TabItem playlistBuilderTab = new TabItem(tabFolder, SWT.NONE);
		playlistBuilderTab.setText("Create Playlists");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		playlistBuilderTab.setControl(composite_2);
		
		List allSongs = new List(composite_2, SWT.BORDER | SWT.V_SCROLL);
		allSongs.setBounds(10, 25, 150, 268);//spacing TBD
		for (Song song : songList) {
		    allSongs.add(song.getName());
		}
		
		List songsToAdd = new List(composite_2, SWT.BORDER);
		songsToAdd.setBounds(410, 25, 150, 268);//spacing TBD
		
		Button rightArrow = new Button(composite_2, SWT.NONE);
		rightArrow.setText(">");
		rightArrow.setBounds(280, 95, 35, 35);
		//add listener to add song selected on right to left list
		
		
		
		Button leftArrow = new Button(composite_2, SWT.NONE);
		leftArrow.setText("<");
		leftArrow.setBounds(280, 140, 35, 35);
		Label playlistNameLabel = new Label(composite_2, SWT.NONE);
		playlistNameLabel.setText("Playlist Name: ");
		playlistNameLabel.setBounds(195, 10, 80, 25);
		
		rightArrow.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        String[] selectedItems = allSongs.getSelection();
		        for (String item : selectedItems) {
		            songsToAdd.add(item);
		            allSongs.remove(item);
		        }
		    }
		});
		
		leftArrow.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        String[] selectedItems = songsToAdd.getSelection();
		        for (String item : selectedItems) {
		            allSongs.add(item);
		            songsToAdd.remove(item);
		        }
		    }
		});
		
		Text playlistNameField = new Text(composite_2, SWT.BORDER);
		playlistNameField.setText("");
		playlistNameField.setBounds(280, 10, 100, 20);
		//add listener to move song selected on left off of the list
		
		Button createPlaylistButton = new Button(composite_2, SWT.NONE);
		createPlaylistButton.setText("create playlist");
		createPlaylistButton.setBounds(240, 279, 105, 35);
		
		//Playlist tab controls:
		TabItem playlistTab = new TabItem(tabFolder, SWT.NONE);
		playlistTab.setText("Playlists");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		playlistTab.setControl(composite_1);
		
		List playlistList = new List(composite_1, SWT.BORDER);
		playlistList.setBounds(10, 50, 150, 150); //spacing TBD
		
		Label playlistListLabel = new Label(composite_1, SWT.NONE);
		playlistListLabel.setText("Playlists");
		playlistListLabel.setBounds(25, 25, 150, 30);
		
		List songsInPlayList = new List(composite_1, SWT.BORDER);
		songsInPlayList.setBounds(400, 50, 150, 150); //spacing TBD
		Label songsInPlaylistLabel = new Label(composite_1, SWT.NONE);
		songsInPlaylistLabel.setText("Songs in Playlist");
		songsInPlaylistLabel.setBounds(425, 20, 150, 20); //spacing TBD
		
				playlistList.addSelectionListener(new SelectionAdapter() {
				    @Override
				    public void widgetSelected(SelectionEvent e) {
				        songsInPlayList.removeAll(); // clear the songs list
				        String playlistName = playlistList.getItem(playlistList.getSelectionIndex());
				        Playlist playlist = playlistCollections.getPlaylistByName(playlistName);
				        if (playlist != null) {
				            for (Song song : playlist.getSongs()) {
				                songsInPlayList.add(song.getName());
				            }
				        }
				    }
				});
				
				Button selectButton = new Button(composite_1, SWT.NONE);
				selectButton.setText("Play");
				selectButton.setBounds(240, 279, 105, 35);
		
		//creates playlist and adds it to the list on tab 2
		createPlaylistButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        String playlistName = playlistNameField.getText();
		        String[] songs = songsToAdd.getItems();
		        Playlist newPlaylist = new Playlist(playlistName);
		        boolean playlistExists = false;

		        if (playlistName.isEmpty()) {
		            MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
		            messageBox.setText("Invalid playlist name");
		            messageBox.setMessage("Playlist name cannot be empty. Please enter a valid name.");
		            messageBox.open();
		        } else {
		            // Check if playlist name already exists
		            for (int i = 0; i < playlistList.getItemCount(); i++) {
		                if (playlistList.getItem(i).equals(playlistName)) {
		                    playlistExists = true;
		                    break;
		                }
		            }

		            if (!playlistExists) {
		                // add the new playlist to the playlist list on tab 2
		                playlistList.add(playlistName); 
		                // add songs to the new playlist
		                for (String songName : songs) {
		                    for (Song song : songList) {
		                        if (song.getName().equals(songName)) {
		                            newPlaylist.addSong(song);
		                            break;
		                        }
		                    }
		                }
		                // clear the songsToAdd list
		                songsToAdd.removeAll();
		                playlistNameField.setText("");
		                
		                // Add the new playlist to the playlistCollections
		                playlistCollections.addPlaylist(newPlaylist);
		                
		                // Debug output
		                System.out.println("Playlist '" + newPlaylist.getName() + "' added with songs:");
		                for (Song song : newPlaylist.getSongs()) {
		                    System.out.println(song.getName());
		                }
		            } else {
		                MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
		                messageBox.setText("Playlist already exists");
		                messageBox.setMessage("A playlist with the name '" + playlistName + "' already exists. Please choose a different name.");
		                messageBox.open();
		            }
		        }
		    }
		});
		
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
	public void startTimer(Display display, ProgressBar progress) {
		if(paused == false) {
			display.timerExec(1000, new Runnable() {
				public void run() {
					if (timer > 0) {
						timer--;
						display.asyncExec(new Runnable() {
							public void run() {
								progress.setSelection(songList.get(i).getDuration() - timer);
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
						startTimer(display, progress);
					}//end else
				}
			});
			}//end if
		}//end StartTimer method
	
	public void UpdateLabels(Label song, Label album, Label artist) {
		song.setText(songList.get(i).getName());
		album.setText(songList.get(i).getAlbum());
		artist.setText(songList.get(i).getArtist());
	}//end updatelables method


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
