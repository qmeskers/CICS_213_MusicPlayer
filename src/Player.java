import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.WebSocket.Listener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import java.util.*;

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
    //TODO: private User currentUser; This will be set by the login button on the User Management tab
  	//TODO: private PlaylistCollections currentUserPlaylist; This may or may not be needed, tbd

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
        
        
        //adds the icon to the top corner of the window
        Image play = new Image(display, "play.png");
		this.setImage(play);
        
        TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setBounds(2, 0, 576, 457);
		
		
		//Now playing tab controls:
		TabItem playerTab = new TabItem(tabFolder, SWT.NONE);
		playerTab.setText("Now Playing");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		playerTab.setControl(composite);
		composite.setLayout(null);

		Label lblArtist = new Label(composite, SWT.NONE);
		lblArtist.setBounds(10, 10, 81, 25);
		lblArtist.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblArtist.setText("Artist:");

		Label lblartistplaying = new Label(composite, SWT.NONE);
		lblartistplaying.setBounds(10, 38, 307, 25);
		lblartistplaying.setText(" ");

		Label lblAlbum = new Label(composite, SWT.NONE);
		lblAlbum.setBounds(10, 69, 81, 25);
		lblAlbum.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblAlbum.setText("Album:");

		Label lblalbumplaying = new Label(composite, SWT.NONE);
		lblalbumplaying.setBounds(10, 100, 307, 25);
		lblalbumplaying.setText(" ");

		Label lblSong = new Label(composite, SWT.NONE);
		lblSong.setBounds(10, 131, 81, 25);
		lblSong.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblSong.setText("Song:");

		Label lblsongplaying = new Label(composite, SWT.NONE);
		lblsongplaying.setBounds(10, 162, 307, 25);
		lblsongplaying.setText(" ");
		
		Label lblGenre = new Label(composite, SWT.NONE);
		lblGenre.setText("Genre:");
		lblGenre.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblGenre.setBounds(10, 204, 81, 25);
		
		Label lblgenreplaying = new Label(composite, SWT.NONE);
		lblgenreplaying.setText(" ");
		lblgenreplaying.setBounds(10, 235, 307, 25);
		
		
		// create the tree widget
		Tree tree = new Tree(composite, SWT.BORDER);
		tree.setBounds(333, 31, 181, 229);

		//ProgressBar used to track location within song
		ProgressBar progressBar = new ProgressBar(composite, SWT.NONE);
		progressBar.setBounds(35, 301, 478, 19);
		progressBar.setMinimum(0);

		
		//button used to restart the current song
		Button btnRestart = new Button(composite, SWT.NONE);
		btnRestart.setBounds(132, 349, 91, 39);
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
		btnPlay.setBounds(229, 349, 91, 39);
		btnPlay.setText("Play");
		btnPlay.addListener(SWT.Selection, event -> {
			Browser browser = new Browser(this, SWT.NONE);
			browser.setBounds(50, 50, 1, 1);
			browser.setUrl(songList.get(i).getUrl());
			timer = songList.get(i).getDuration();
			progressBar.setMaximum(songList.get(i).getDuration());
			startTimer(display, progressBar);
			UpdateLabels(lblsongplaying, lblalbumplaying, lblartistplaying, lblgenreplaying);
		});//end btnPlay event
		
		
		//button used to repeat the current song once it's finished playing
		Button btnRepeat = new Button(composite, SWT.NONE);
		btnRepeat.setBounds(35, 348, 91, 40);
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
	
		/*
		 * This listens to the tree and when a song is pressed, it will chenge the index(i) to the location of 
		 * the song in the songList or the json file to play that song
		 */
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// Get the selected item in the tree
				TreeItem selectedItem = (TreeItem) event.item;

				// Get the Song object associated with the selected item
				Song selectedSong = (Song) selectedItem.getData();
				
				//looks for the index of the song you click and sets the current index as its number
				for(int j = 0; j < songList.size(); j++) {
					if(songList.get(j).equals(selectedSong)) {
						i = j;
					}
				}
				
				 //Update the currentSong URL if a song is selected
				Control[] controls = Player.this.getChildren();
				if (selectedSong != null) {
					for (Control control : controls) {
						if (control instanceof Browser) {
							control.dispose();
						}//end if
					}//end for loop
					UpdateLabels(lblsongplaying, lblalbumplaying, lblartistplaying, lblgenreplaying);

				}
			}
		});

		Button btnPause = new Button(composite, SWT.NONE);
		btnPause.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnPause.setBounds(326, 349, 91, 39);
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
				startTimer(display, progressBar);//end else
			}});


		Button btnSkip = new Button(composite, SWT.NONE);
		btnSkip.setBounds(423, 349, 91, 39);
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
			i++; //(Quinn) I added this and got the skip kinda working if you press play afterwards (might be too simplistic though)
			UpdateLabels(lblsongplaying, lblalbumplaying, lblartistplaying, lblgenreplaying);
		});

		// load the songs from the JSON file
		songList = Songs.loadSongsFromJson("songsList.json");
		

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

		//User Management Controls:
		TabItem userManagementTab = new TabItem(tabFolder, SWT.NONE);
		userManagementTab.setText("User Management");

		Composite userTabComposite = new Composite(tabFolder, SWT.NONE);
		userManagementTab.setControl(userTabComposite);

		Label currentUserLabel = new Label(userTabComposite, SWT.NONE);
		currentUserLabel.setText("Current User: ");//Regularly Updated Label Text
		currentUserLabel.setBounds(50, 10, 150, 25);

		Label usernameLabel = new Label(userTabComposite, SWT.NONE);
		usernameLabel.setText("Username: ");
		usernameLabel.setBounds(50, 40, 80, 25);

		Label firstNameLabel = new Label(userTabComposite, SWT.NONE);
		firstNameLabel.setText("First Name: ");
		firstNameLabel.setBounds(50, 70, 80, 25);

		Label lastNameLabel = new Label(userTabComposite, SWT.NONE);
		lastNameLabel.setText("Last Name: ");
		lastNameLabel.setBounds(50, 100, 80, 25);

		Label passwordLabel = new Label(userTabComposite, SWT.NONE);
		passwordLabel.setText("Password: ");
		passwordLabel.setBounds(50, 130, 80, 25);

		Text usernameField = new Text(userTabComposite, SWT.BORDER);
		usernameField.setText("");
		usernameField.setBounds(145, 40, 100, 20);

		Text firstNameField = new Text(userTabComposite, SWT.BORDER);
		firstNameField.setText("");
		firstNameField.setBounds(145, 70, 100, 20);

		Text lastNameField = new Text(userTabComposite, SWT.BORDER);
		lastNameField.setText("");
		lastNameField.setBounds(145, 100, 100, 20);

		Text passwordField = new Text(userTabComposite, SWT.BORDER);
		passwordField.setText("");
		passwordField.setBounds(145, 130, 100, 20);

		Button loginButton = new Button(userTabComposite, SWT.NONE);
		loginButton.setText("Login");
		loginButton.setBounds(50, 170, 80, 25);

		Button createUserButton = new Button(userTabComposite, SWT.NONE);
		createUserButton.setText("Create User");
		createUserButton.setBounds(160, 170, 85, 25);	
		createUserButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String fileName = usernameField.getText() + "Playlists.json";
				createNewUserFile(fileName);
				//TODO: add constructor for new user
			}
		});
		
		//Playlist Builder Tab Controls:
		TabItem playlistBuilderTab = new TabItem(tabFolder, SWT.NONE);
		playlistBuilderTab.setText("Create Playlists");

		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		playlistBuilderTab.setControl(composite_2);

		List allSongs = new List(composite_2, SWT.BORDER | SWT.V_SCROLL);
		allSongs.setBounds(10, 45, 200, 268);

		Text songSearchField = new Text(composite_2, SWT.BORDER);
		songSearchField.setText("");
		songSearchField.setBounds(65, 10, 110, 30);

		Label songSearchLabel = new Label(composite_2, SWT.NONE);
		songSearchLabel.setText("Seach: ");
		songSearchLabel.setBounds(10, 10, 110, 26);

		for (Song song : songList) {	
			allSongs.add(song.getName());	    
		}

		List songsToAdd = new List(composite_2, SWT.BORDER);
		songsToAdd.setBounds(360, 45, 200, 268);

		Button searchSong = new Button(composite_2, SWT.NONE);
		searchSong.setText(">");
		searchSong.setBounds(179, 9, 32, 32);

		Button rightArrow = new Button(composite_2, SWT.NONE);
		rightArrow.setText(">");
		rightArrow.setBounds(267, 125, 35, 35);

		Button leftArrow = new Button(composite_2, SWT.NONE);
		leftArrow.setText("<");
		leftArrow.setBounds(267, 170, 35, 35);
		Label playlistNameLabel = new Label(composite_2, SWT.NONE);
		playlistNameLabel.setText("Playlist Name: ");
		playlistNameLabel.setBounds(360, 10, 110, 26);

		searchSong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				allSongs.removeAll();
				for (Song song : songList) {
					//if search field isn't empty
					if (!songSearchField.getText().toLowerCase().equals("")) {
						if (song.getName().toLowerCase().contains(songSearchField.getText().toLowerCase()) || 
								song.getArtist().toLowerCase().contains(songSearchField.getText().toLowerCase())) {
							allSongs.add(song.getName());
						}
					}
					else {
						allSongs.add(song.getName());
					}
				}
			}
		});

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
		playlistNameField.setBounds(475, 10, 85, 30);
		//add listener to move song selected on left off of the list

		Button createPlaylistButton = new Button(composite_2, SWT.NONE);
		createPlaylistButton.setText("Create Playlist");
		createPlaylistButton.setBounds(225, 279, 120, 35);
		
		//Playlist tab controls:
		TabItem playlistTab = new TabItem(tabFolder, SWT.NONE);
		playlistTab.setText("Playlists");

		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		playlistTab.setControl(composite_1);

		Tree playlistList = new Tree(composite_1, SWT.BORDER);
		playlistList.setBounds(220, 70, 150, 150);
		
		Label playlistListLabel = new Label(composite_1, SWT.NONE);
		playlistListLabel.setText("Playlists");
		playlistListLabel.setBounds(270, 25, 150, 30);

		Button playButton = new Button(composite_1, SWT.NONE);
		playButton.setText("Play Playlist");
		playButton.setBounds(300, 279, 105, 35);
		
		Button deleteButton = new Button(composite_1, SWT.NONE);
		deleteButton.setText("Delete");
		deleteButton.setBounds(180, 279, 105, 35);
		
		Button shuffleButton = new Button(composite_1, SWT.NONE);
		shuffleButton.setText("Shuffle");
		shuffleButton.setBounds(420, 279, 105, 35);
		
		Button skipButton = new Button(composite_1, SWT.NONE);
		skipButton.setText("Skip Song");
		skipButton.setBounds(50, 279, 105, 35);

		/**
		 * This button iterates over the current user's collection of playlists and rewrites it to exclude the playlist the user selected to delete
		 * it then repopulates the playlistList with the new user playlists
		 */
		deleteButton.addSelectionListener(new SelectionAdapter() {
		    @SuppressWarnings("unchecked")
		    public void widgetSelected(SelectionEvent e) {
		        TreeItem[] currentSelection = playlistList.getSelection();
		        if (currentSelection.length > 0) {
		            TreeItem selectedPlaylist = currentSelection[0];
		            String playlistName = selectedPlaylist.getText();
		            //TODO: get rid of this first line below, replace it with an iteration over the currentUserPlaylist
		            //TODO: probably add a line for file output filename creation user.getUsername + "playlist.json" or something
		            java.util.List<Playlist> playlists = PlaylistCollections.loadPlaylistsFromJson("UserPlaylistTest.json");
		            JSONObject newUserPlaylistsJson = new JSONObject();
		            JSONArray playlistsJsonArray = new JSONArray();
		            for (Playlist playlist : playlists) {
		                if (playlist.getName().equalsIgnoreCase(playlistName)) {
		                    //TODO: this will become a call to removePlaylist method in currentuserplaylist once user class is done
		                    //at that time, get rid of else as that method is contained in the other class
		                    continue;
		                } else {
		                    JSONObject currentPlaylistJson = new JSONObject();
		                    currentPlaylistJson.put("playlist name", playlist.getName());
		                    JSONArray songJsonArray = new JSONArray();
		                    for (Song song : playlist.getSongs()) {
		                        JSONObject currentSongJson = new JSONObject();
		                        currentSongJson.put("artist", song.getArtist());
		                        currentSongJson.put("album",  song.getAlbum());
		                        currentSongJson.put("name", song.getName());
		                        currentSongJson.put("yearReleased", song.getYearReleased());
		                        currentSongJson.put("url", song.getUrl());
		                        currentSongJson.put("genre", song.getGenre());
		                        currentSongJson.put("duration", song.getDuration());
		                        songJsonArray.add(currentSongJson);
		                    }
		                    currentPlaylistJson.put("songs", songJsonArray);
		                    playlistsJsonArray.add(currentPlaylistJson);
		                }                   
		                //sampleObject.put("playlists", *need to make the playlist first*);
		            }                
		            newUserPlaylistsJson.put("playlists", playlistsJsonArray);
		            try {
		                Files.write(Paths.get("UserPlaylistTest.json"), newUserPlaylistsJson.toJSONString().getBytes());
		            } catch (IOException e1) {
		                e1.printStackTrace();
		            }
		            selectedPlaylist.dispose();
		        }
		    }
		});
		
		//Buttons:
		//creates play list and adds it to the list on playlist tab
		//TODO: make sure this is tied to the user's playlistCollections field/object so that the json file for that user is updated correctly
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
					for (TreeItem item : playlistList.getItems()) {
						if (item.getText().equals(playlistName)) {
							playlistExists = true;
							break;
						}
					}

					if (!playlistExists) {
						// add the new playlist to the playlist list on tab 2
						TreeItem newPlaylistItem = new TreeItem(playlistList, SWT.NONE);
						newPlaylistItem.setText(playlistName);

						// add songs to the new playlist
						for (String songName : songs) {
							for (Song song : songList) {
								if (song.getName().equals(songName)) {
									newPlaylist.addSong(song);
									TreeItem songItem = new TreeItem(newPlaylistItem, SWT.NONE);
									songItem.setText(songName);
									songItem.setData(song);
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
		
		

		playButton.addListener(SWT.Selection, event -> {
		    TreeItem[] selectedItems = playlistList.getSelection();
		    if (selectedItems.length > 0) {
		        // Only play songs if a playlist is selected
		        ArrayList<Song> songs = new ArrayList<>();
		        TreeItem playlistItem = selectedItems[0];
		        for (TreeItem songItem : playlistItem.getItems()) {
		            Song song = (Song) songItem.getData();
		            songs.add(song);
		        }
		        if (!songs.isEmpty()) {
		            Browser browser = new Browser(getShell(), SWT.NONE);
		            ProgressBar progress = new ProgressBar(getShell(), SWT.NONE);
		            Queue<Song> songQueue = new LinkedList<>(songs);
		            startTimerPlaylist(getDisplay(), progress, songQueue, browser);
		        }
		    }
		});
	
		shuffleButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
			   // Get the selected playlist
			   TreeItem[] selection = playlistList.getSelection();
			   if (selection.length == 0) {
				  // No playlist has been selected, do nothing
				   MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
					 messageBox.setText("No play list Selected.");
					 messageBox.setMessage("Please select a play list to shuffle");
					 messageBox.open();
				  return;
			   }
			   TreeItem selectedPlaylist = selection[0];
			   
			   // Get the child items (songs) of the selected playlist
			   TreeItem[] songs = selectedPlaylist.getItems();
			   
			   // Create a list to hold the song objects
			   ArrayList<Song> songList = new ArrayList<Song>();
			   
			   // Add each song object to the list
			   for (TreeItem song : songs) {
				  songList.add((Song) song.getData());
			   }
			   
			   // Shuffle the list
			   Collections.shuffle(songList);
			   
			   // Clear the existing child items from the selected playlist
			   selectedPlaylist.removeAll();
			   
			   // Add the shuffled songs back to the playlist as child items
			   for (Song song : songList) {
				  TreeItem songItem = new TreeItem(selectedPlaylist, SWT.NONE);
				  songItem.setText(song.getName());
				  songItem.setData(song);
			   }
			   
			   // Update the tree widget
			   tree.update();
			}
		 });
		//TODO add skip song in play list functionality
		//User Management Tab Login Button Listener:
		//TODO: update the below per block comment
		loginButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				currentUserLabel.setText("Current User: " + usernameField.getText());
				//TODO: set currentUserPlaylist to the PlaylistCollections that is a field for the User object
				reloadPlaylists();
			}

			public void reloadPlaylists() {
				playlistList.removeAll();
				/**
				 * THIS WILL HAVE TO CHANGE TO PULL A NEW FILENAME FOR EACH NEW USER -- BUT THE TEST WORKS!
				 */
				java.util.List<Playlist> playlists = PlaylistCollections.loadPlaylistsFromJson("UserPlaylistTest.json");
				for (Playlist playlist : playlists) {
					((Collection) playlistList).add(playlist.getName());
				}
			}
		});

    }

	private void populateTree(Tree playlistList) {
		// TODO Auto-generated method stub
		
	}

	protected void createNewUserFile(String fileName) {
    	File newUserFile = new File(fileName);
    	FileWriter fileWriter;
    	try {
    		fileWriter = new FileWriter(newUserFile, false);
    		fileWriter.write("\n");
    	} catch (IOException e) {
    		e.printStackTrace();
    	}


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
	
	//this method gives the start timer attributes but for the queue
	private void startTimerPlaylist(Display display, ProgressBar progress, Queue<Song> songQueue, Browser browser) {
	    if (songQueue == null || songQueue.isEmpty()) {
	        browser.setUrl("");
	        return;
	    }

	    Song currentSong = songQueue.peek();
	    browser.setUrl(currentSong.getUrl());
	    progress.setMaximum(currentSong.getDuration());
	    progress.setSelection(0);

	    browser.addProgressListener(new ProgressListener() {
	        @Override
	        public void completed(ProgressEvent event) {}

	        @Override
	        public void changed(ProgressEvent event) {}
	    });

	    final int[] timer = { currentSong.getDuration() };
	    display.timerExec(1000, new Runnable() {
	        public void run() {
	            if (timer[0] > 0) {
	                progress.setSelection(currentSong.getDuration() - timer[0]);
	                timer[0]--;
	                display.timerExec(1000, this);
	            } else {
	                songQueue.poll();
	                startTimerPlaylist(display, progress, songQueue, browser);
	            }
	        }
	    });
	}

//end StartTimerPlaylist method
	
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
	
	
	
	public void UpdateLabels(Label song, Label album, Label artist, Label genre) {
		song.setText(songList.get(i).getName());
		album.setText(songList.get(i).getAlbum());
		artist.setText(songList.get(i).getArtist());
		genre.setText(songList.get(i).getGenre());
	}//end updatelables method

	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
}
