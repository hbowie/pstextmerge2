/*
 * Copyright 1999 - 2017 Herb Bowie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.powersurgepub.pstextmerge2;

  import com.powersurgepub.psutils2.basic.*;
  import com.powersurgepub.psutils2.env.*;
  import com.powersurgepub.psutils2.files.*;
  import com.powersurgepub.psutils2.list.*;
  import com.powersurgepub.psutils2.logging.*;
  import com.powersurgepub.psutils2.records.*;
  import com.powersurgepub.psutils2.script.*;
  import com.powersurgepub.psutils2.strings.*;
  import com.powersurgepub.psutils2.textmerge.*;
  import com.powersurgepub.psutils2.ui.*;

  import de.codecentric.centerdevice.*;

  import java.io.*;
  import java.net.*;
  import java.text.*;

  import javafx.application.*;
  import javafx.event.*;
  import javafx.geometry.*;
  import javafx.scene.*;
  import javafx.scene.control.*;
  import javafx.scene.image.*;
  import javafx.scene.input.*;
  import javafx.scene.layout.*;
  import javafx.stage.*;

/**
   An application with a GUI interface to do all sorts of things
   with tabular data, rewritten to use JavaFX instead of Swing.

   @author Herb Bowie
 */
public class PSTextMerge
    extends Application
      implements
        TextMergeController {

	/*
	   Constants
	 */

	// String associated with a logging threshold of normal severity.
  public    static final String  LOG_NORMAL_STRING = "Normal";

  // String associated with a logging threshold of minor severity.
  public    static final String  LOG_MINOR_STRING  = "Minor";

  // String associated with a logging threshold of medium severity.
  public    static  final String  LOG_MEDIUM_STRING = "Medium";

  // String associated with a logging threshold of major severity.
  public    static  final String  LOG_MAJOR_STRING  = "Major";

	// Default values for tab configurations.
	private   static  final String  DEFAULT_TABS = "IVSFOTCLA";

  // Maximum number of records that can be processed in demo mode.
  private		static	final	int			DEMO_MAX_RECORDS = 20;

  /** Program Name */
  public    static  final String  PROGRAM_NAME = "PSTextMerge";
  public    static  final String  PROGRAM_VERSION = "6.00";

  private   static  final String  USER_GUIDE
      = "userguide/pstextmerge.html";

  private   static  final String  PROGRAM_HISTORY
      = "versions.html";
  
  private             String  country = "  ";
  private             String  language = "  ";
  
  private             Appster             appster = null;
  private             Home                home;
  private             ProgramVersion      programVersion;
  private             UserPrefs           userPrefs;
  private             Trouble             trouble;
  private             FXUtils             fxUtils;
  
  private             Stage               primaryStage = null;
  private             VBox                primaryLayout = null;
  private             Scene               primaryScene = null;

  /**
     Command line arguments.
   */
  private boolean quietMode = false;
  private String  startingScript = "";

  private ScriptExecutor scriptExecutor = null;

  // Flag to indicate this is not the main program
  private boolean mainClass = true;

  private     static  final double   DEFAULT_WIDTH = 680;
  private     static  final double   DEFAULT_HEIGHT = 450;

  private                   double   defaultX = 0;
  private                   double   defaultY = 0;

	/*
	   Fields to control the tabs that are displayed.
	 */
	// Tab configuration.
	private     String              tabConfig = DEFAULT_TABS;

	// Should Input Tab be displayed?
	private     boolean             inputTabDisplay = false;

	// Should View Tab be displayed?
	private     boolean             viewTabDisplay = false;

	// Should Sort Tab be displayed?
	private     boolean             sortTabDisplay = false;

	// Should Filter Tab be displayed?
	private     boolean             filterTabDisplay = false;

	// Should Output Tab be displayed?
	private     boolean             outputTabDisplay = false;

	// Should Template Tab be displayed?
	private     boolean             templateTabDisplay = false;

	// Should Script Tab be displayed?
	private     boolean             scriptTabDisplay = false;

	// Should Logging Tab be displayed?
	private     boolean             logTabDisplay = false;

  private     DataRecList         list = new DataRecList();

  private     TextMergeInput      textMergeInput = null;
  private     TextMergeScript     textMergeScript = null;
  private     TextMergeFilter     textMergeFilter = null;
  private     TextMergeSort       textMergeSort = null;
  private     TextMergeTemplate   textMergeTemplate = null;
  private     TextMergeOutput     textMergeOutput = null;

  // A log juggler to create other logging objects and perform logging operations.
  private     LogJuggler          logJuggler = new LogJuggler("PSTextMerge");

  /** Various system properties. */
  // private			String							mrjVersion;
  private     File                appFolder;
  private     String              userDirString;
  private     String              fileSeparatorString;
  private     File                userDirFile;
  private     URL                 pageURL;
  private     URL                 userGuideURL;
  private     URL                 programHistoryURL;
  // private			File								userGuideFile;
  // private			String							userGuideString;

  private     File                currentDirectory = null;

  private     boolean             listAvailable = false;

	private     int                 numberOfFields = 0;

  private     String              possibleFileName = "";
  private     String              fileName = "";

  private     String              lastTabNameOutput = "";
  /** File name of file that contains basic information about PSTextMerge. */
  private     String              aboutFileName = "about.html";

  private     DataSource          dataSource;

  // Miscellaneous Fields
	private     String              iconFileName = "pstextmerge_icon_32.gif";
  private     String              iconURLString;
	private     URL                 iconURL;
	private     Image               iconImage;

  // Fields used for tab-delimited data files, whether input or output
  private     FileName            tabFileName;

  // Fields used for the User Interface
  // private			JFrame							mainFrame;
  private     Border              raisedBevel;
  private     Border              etched;

  // Menu Objects
  private			MenuBar						  menuBar;
  
  private     Menu                appMenu;
  private     MenuItem            aboutMenuItem;

  private			Menu								fileMenu;
  private			MenuItem						fileOpen;
  private			MenuItem						fileExit;

  private     Menu                windowMenu;

  private			Menu								helpMenu;
  private			MenuItem						helpAbout;

	private     GridPane            header;
	private     Label               programNameLabel;
	private     Label               fileNameLiteral   = new Label ("File Name: ");
	private     Label               fileNameLabel      = new Label();
	private     ImageView           iconLabel;

	private     TabPane         tabs;

	// private     Insets              insetsTLBR0 = new Insets (0, 0, 0, 0);
	// private     Insets              insetsTLBR1 = new Insets (1, 1, 1, 1);
	// private     Insets              insetsTB2LR1 = new Insets (2, 1, 2, 1);
	// private     Insets              insetsTB1LR3 = new Insets (1, 3, 1, 3);

	private     int                 tabPosition = 0;
	private     int                 viewTabPosition = 0;

  // View Panel Objects
  private     Tab                 viewTab;
  private     GridPane            viewPane;
  private     TableView <DataRecord> viewTable = null;
  private     boolean             tabTableBuilt = false;

  private     boolean             sortTabBuilt      = false;
  private     boolean             filterTabBuilt    = false;
  private     boolean             templateTabBuilt  = false;

  // Logging Panel objects
  private    Tab                  logTab;
  private    GridPane             logPane;

  // Column 1 - Logging Output
  private    Label                logOutputLabel;
  private    ToggleGroup          logOutputGroup;
  private    RadioButton          logOutputNoneButton;
  private    RadioButton          logOutputWindowButton;
  private		 RadioButton					logOutputTextButton;
  private    RadioButton          logOutputDiskButton;

  // Column 2 - Logging Threshold
  private    Label                logThresholdLabel;
  private    ToggleGroup          logThresholdGroup;
  private    RadioButton          logThresholdNormalButton;
  private    RadioButton          logThresholdMinorButton;
  private    RadioButton          logThresholdMediumButton;
  private    RadioButton          logThresholdMajorButton;

  // Column 3 - Log All Data?
  private    Label                logAllDataLabel;
  private    CheckBox             logAllDataCkBox;

  // Bottom of Panel
  private     TextArea            logText;

  private     AboutWindow         aboutWindow;
  
  public PSTextMerge() {

  }
  
	/**
     Constructor.
   */
  public PSTextMerge(String[] args) {
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.startsWith ("-")) {
        arg = arg.toLowerCase();
        for (int j = 1; j < arg.length(); j++) {
          char opt = arg.charAt (j);
          if (opt == 'q') {
            quietMode = true;
          }
        }
      } else {
        startingScript = arg;
      }
    } // end argument processing
    
	}

  @Override
  public void start(Stage primaryStage) {
    
    this.primaryStage = primaryStage;
    primaryStage.setTitle("PSTextMerge");
    setMainClass (true);
    run(null);
  }
  
  @Override
  public void stop() {
    wrapThingsUp();
  }

  /**
   @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
  
	/**
     Play a script.
   
     @param script    Name of script file to be played.
     @param logOutput Output destination for log messages.
   */
	public static void execScript (String script, Logger logIn) {
    String[] args = new String[2];
    args[0] = "-q";
    args[1] = script;
		PSTextMerge merge = new PSTextMerge(args);
    merge.setMainClass (false);
    merge.setLogger(logIn);
    logIn.recordEvent (LogEvent.NORMAL, 
        PROGRAM_NAME + " " + PROGRAM_VERSION + " invoked by another program",
        false);
    merge.run(logIn);
	} // end execScript method
  
	/**
     Play a script.

     @param script    Name of script file to be played.
     @param logOutput Output destination for log messages.
     @param scriptExecutor A class that is prepared to execute requested
                           script callbacks.
   */
	public static void execScript (String script, Logger logIn,
      ScriptExecutor scriptExecutor) {
    String[] args = new String[2];
    args[0] = "-q";
    args[1] = script;
    PSTextMerge merge = new PSTextMerge(args);
    merge.setLogger(logIn);
    merge.setMainClass (false);
    merge.setScriptExecutor(scriptExecutor);
    logIn.recordEvent (LogEvent.MEDIUM,
        PROGRAM_NAME + " " + PROGRAM_VERSION + " invoked by another program",
        false);
    merge.run(logIn);
	} // end execScript method

  /**
    Indicate whether this is the main class.

    @param mainClass True if this is the main class, false if called from another.
   */
  public void setMainClass (boolean mainClass) {
    this.mainClass = mainClass;
  }
  
  /**
   Set a class to be used for callbacks. 
  
   @param scriptExecutor The class to be used for callbacks.
  */
  public void setScriptExecutor(ScriptExecutor scriptExecutor) {
    this.scriptExecutor = scriptExecutor;
    if (textMergeScript != null) {
      textMergeScript.setScriptExecutor(scriptExecutor);
    }
  }
  
  public void setLogger(Logger logger) {
    logJuggler.setLogger(logger);
  }
  
  /**
     Get the user interface up and running.
   */
  private void run(Logger logIn) {
    
    if (mainClass) {
      appster = new Appster
        (this,
          "powersurgepub", "com",
          PROGRAM_NAME, PROGRAM_VERSION,
          primaryStage);
      // Create About Pane
      aboutWindow = new AboutWindow (primaryStage, false);
    } 
    home = Home.getShared();
    programVersion = ProgramVersion.getShared();
    trouble = Trouble.getShared();
    logJuggler.setLogger(logIn);

    if (primaryStage == null) {
      primaryStage = new Stage(StageStyle.DECORATED);
      primaryStage.setTitle("PSTextMerge");
    }
    primaryLayout = new VBox();
    
    aboutMenuItem = new MenuItem("About " + PROGRAM_NAME);
    aboutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent evt) {
        handleAbout();
      }
    });
    
    helpMenu = new Menu("Help"); 
    appFolder = home.getAppFolder();
    
    list = new DataRecList();
    textMergeScript   = new TextMergeScript   (primaryStage, list);
    textMergeInput    = new TextMergeInput    
        (primaryStage, list, this, textMergeScript);
    textMergeFilter   = new TextMergeFilter   
        (primaryStage, list, this, textMergeScript);
    textMergeSort     = new TextMergeSort
        (primaryStage, list, this, textMergeScript);
    textMergeTemplate = new TextMergeTemplate
        (primaryStage, list, this, textMergeScript);
    textMergeOutput   = new TextMergeOutput   
        (primaryStage, list, this, textMergeScript);
    textMergeScript.allowAutoplay(mainClass);
    textMergeScript.setInputModule(textMergeInput);
    textMergeScript.setFilterModule(textMergeFilter);
    textMergeScript.setOutputModule(textMergeOutput);
    textMergeScript.setSortModule(textMergeSort);
    textMergeScript.setTemplateModule(textMergeTemplate);
    textMergeScript.setScriptExecutor(scriptExecutor);

		try {
			initComponents(logIn);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

    if (mainClass) {
      // xos.setFileMenu (fileMenu);
      home.setHelpMenu(primaryStage, helpMenu, aboutWindow);
      WindowMenuManager.getShared().addWindowMenu(windowMenu);
      try {
        userGuideURL = new URL (pageURL, USER_GUIDE);
      } catch (MalformedURLException e) {
      }
      try {
        programHistoryURL = new URL(pageURL, PROGRAM_HISTORY);
      } catch (MalformedURLException e) {
        // shouldn't happen
      }
      Trouble.getShared().setParent(primaryStage);
    }
    calcDefaultScreenLocation();
    
    // Open script file if it was passed as a parameter
    possibleFileName = System.getProperty ("scriptfile", "");
    if ((possibleFileName != null) && (! possibleFileName.equals (""))) {
      fileName = possibleFileName;
    }
    else
    if (! startingScript.equals ("")) {
      fileName = startingScript;
    } 
    if (! fileName.equals("")) {
      File sFile = new File (fileName);
      boolean scriptFound = (sFile.exists() && sFile.isFile());
      if (! scriptFound) {
        logJuggler.recordEvent (LogEvent.MEDIUM, 
          "MSG001 " + sFile.toString() + " could not be opened as a valid Script File",
          true);
        sFile = new File (currentDirectory, fileName);
        scriptFound = (sFile.exists() && sFile.isFile());
      }
      if (scriptFound) {
        textMergeScript.playScript(sFile);
      } 
      else {
        logJuggler.recordEvent (LogEvent.MEDIUM, 
          sFile.toString() + " could not be opened as a valid Script File",
          true);
      } 
    } // end if non-blank file name
    
    if (quietMode) {
      wrapThingsUp();
      System.exit(0);
    } else {
      primaryStage.setScene(primaryScene);
      primaryStage.setX(userPrefs.getPrefAsDouble(UserPrefs.LEFT, defaultX));
      primaryStage.setY(userPrefs.getPrefAsDouble(UserPrefs.TOP, defaultY));
      primaryStage.setWidth
          (userPrefs.getPrefAsDouble(UserPrefs.WIDTH, DEFAULT_WIDTH));
      primaryStage.setHeight
          (userPrefs.getPrefAsDouble(UserPrefs.HEIGHT, DEFAULT_HEIGHT));
      primaryStage.show();
      textMergeScript.checkAutoPlay();
    } // end if not quiet mode
  }
  
  /**
   Build the User Interface. 
  
   @param logIn The logger to be used. 
  
   @throws Exception 
  */
	private void initComponents(Logger logIn) 
      throws Exception {  
    
    // Get System Properties
    userDirString = System.getProperty (GlobalConstants.USER_DIR);
    if ((userDirString != null) && (! userDirString.equals (""))) {
      userDirFile = new File (userDirString);
      currentDirectory = userDirFile;
      textMergeScript.setNormalizerPath(userDirString);
    }
    currentDirectory = home.getUserHome();
    fileSeparatorString = System.getProperty (GlobalConstants.FILE_SEPARATOR, "/");
    try {
      pageURL = appFolder.toURI().toURL(); 
    } catch (MalformedURLException e) {
      trouble.report ("Trouble forming pageURL from " + appFolder.toString(), 
          "URL Problem");
    }
    
    // Set up logging stuff
    if (logIn != null) {
      logJuggler.setLogger(logIn);
    }
    else
    if (quietMode) {
      logJuggler.switchLogOutput (LogJuggler.LOG_DISK_STRING);
    } 
    else {
      // Do nothing now -- wait for log text area to be set up later
      // logJuggler.switchLogOutput (LogJuggler.LOG_TEXT_STRING);
    }
    if (logIn == null) {
      logJuggler.getLogger().setLogAllData (false);
      logJuggler.getLogger().setLogThreshold (LogEvent.NORMAL);
    } else {
      logJuggler.getLogger().setLogAllData (logIn.getLogAllData());
      logJuggler.getLogger().setLogThreshold (logIn.getLogThreshold());
    }
    
    // Determine which Tabs are to be displayed
    tabConfig = System.getProperty ("tabs", DEFAULT_TABS);
    for (int i = 0; i < tabConfig.length(); i++) {
      char t = Character.toUpperCase (tabConfig.charAt (i));
      switch (t) {
        case 'I':
          inputTabDisplay = true;
          break;
        case 'V':
          viewTabDisplay = true;
          break;
        case 'S':
          sortTabDisplay = true;
          break;
        case 'F':
          filterTabDisplay = true;
          break;
        case 'O':
        	outputTabDisplay = true;
        	break;
        case 'T':
          templateTabDisplay = true;
          break;
        case 'C':
          scriptTabDisplay = true;
          break;
        case 'L':
          logTabDisplay = true;
          break;
        case 'A':
          // aboutTabDisplay = true;
          break;
        default:
          logJuggler.recordEvent (LogEvent.MINOR, 
            String.valueOf (t) + " is not a valid Tab Identifier",
            false);
          break;
      } // end switch
    } // end for loop
    
    // Get info from Parms file
    if (mainClass) {
      home = Home.getShared (this, PROGRAM_NAME, PROGRAM_VERSION);
    } else {
      home = Home.getShared();
    }
    userPrefs = UserPrefs.getShared();
    
    fxUtils = FXUtils.getShared();
    // Create common interface components
    // raisedBevel = BorderFactory.createRaisedBevelBorder();
    // etched      = BorderFactory.createEtchedBorder();
    
    // Create Menu Bar
    menuBar = new MenuBar();
    menuBar.setUseSystemMenuBar(true);
    menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
    primaryLayout.getChildren().add(menuBar);
    
    // if (Home.runningOnMac()) {
    //   appMenu = new Menu(PROGRAM_NAME);
    //   menuBar.getMenus().add(appMenu);
    //   appMenu.getItems().add(aboutMenuItem);

      /*
      MenuToolkit menuTools = MenuToolkit.toolkit();

      // Create the default Application menu
      Menu defaultApplicationMenu = menuTools.createDefaultApplicationMenu(PROGRAM_NAME);

      // Update the existing Application menu
      menuTools.setApplicationMenu(defaultApplicationMenu);

      // Add the default application menu
      menuBar.getMenus().add(defaultApplicationMenu);

      // Use the menu bar for all stages including new ones
      menuTools.setGlobalMenuBar(menuBar); 
      */
    // }
    
    
    fileMenu = new Menu("File");
    menuBar.getMenus().add (fileMenu);
        
    // Create Header
    header = new GridPane();
    header.setHgap(10);
    header.setVgap(10);
    header.setAlignment(Pos.TOP_LEFT);
    header.setStyle("-fx-padding: 10; ");
    
    iconURL = new URL (pageURL, iconFileName);
    iconURLString = pageURL.toString();
    iconImage = new Image (iconURLString);
    iconLabel = new ImageView (iconImage);
    iconLabel.setVisible (true);
    fileNameLiteral.setVisible (true);
    fileNameLabel.setVisible (true);
    if (textMergeInput != null) {
		  fileNameLabel.setText (textMergeInput.getFileNameToDisplay());
    }
    header.add(iconLabel, 0, 0);
    header.add(fileNameLiteral, 1, 0);
    header.add(fileNameLabel, 2, 0);
    primaryLayout.getChildren().add(header);
    
		// Create tabbed pane
		tabs = new TabPane();
    
    if (inputTabDisplay) {
      textMergeInput.setTabs(tabs);
      textMergeInput.setMenus(menuBar);
      tabPosition++;
    }
		
		// Create view Tab with Table
		if (viewTabDisplay) {
		  addViewTab();
		  viewTabPosition = tabPosition;
		  tabPosition++;
		}
		
		// Create Sort Pane
		if (sortTabDisplay) {
		  textMergeSort.setTabs(tabs, true);
		  sortTabBuilt = true;
		  tabPosition++;
		}

		// Create Filter Pane
		if (filterTabDisplay) {
      textMergeFilter.setTabs(tabs);
      filterTabBuilt = true;
		  tabPosition++;
		}
		
		// Create Output Pane
		if (outputTabDisplay) {
      textMergeOutput.setTabs(tabs);
      textMergeOutput.setMenus(menuBar);
			tabPosition++;
		}
		
		// Create a Template Pane
		if (templateTabDisplay) {
      textMergeTemplate.setTabs(tabs);
      textMergeTemplate.setMenus(menuBar);
      templateTabBuilt = true;
		  tabPosition++;
		}
		
		// Create Script Pane
		if (scriptTabDisplay) {
      textMergeScript.setTabs(tabs);
      textMergeScript.setMenus(menuBar, "Script");
		  tabPosition++;
		}
		
		// Create Logging Pane
		if (logTabDisplay) {
		  addLoggingTab();
		  tabPosition++;
		}
    
    // Finish off File Menu
    if (! Home.runningOnMac()) {
      fileExit = new MenuItem ("Exit/Quit");
      KeyCombination quitKC
          = new KeyCharacterCombination("Q", KeyCombination.SHORTCUT_DOWN);
      fileExit.setAccelerator(quitKC);
      fileMenu.getItems().add (fileExit);
      fileExit.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent evt) {
          Platform.exit();
        } // end actionPerformed method
        } // end action listener
      );
    } // end if not running on a Mac
    
    // Window menu
    windowMenu = new Menu("Window");
    menuBar.getMenus().add (windowMenu);
    
    // Help Menu 
    // helpMenu = new JMenu("Help");
    menuBar.getMenus().add (helpMenu);
    
    logJuggler.recordEvent (LogEvent.NORMAL,
        "Application Folder = " + home.getAppFolder().toString(),
        false);
    logJuggler.recordEvent (LogEvent.NORMAL,
        "Java Virtual Machine = " + System.getProperty("java.vm.name") + 
        " version " + System.getProperty("java.vm.version") +
        " from " + StringUtils.removeQuotes(System.getProperty("java.vm.vendor")),
        false);
    Runtime runtime = Runtime.getRuntime();
    runtime.gc();
    NumberFormat numberFormat = NumberFormat.getInstance();
    logJuggler.recordEvent (LogEvent.NORMAL,
        "Available Memory = " + numberFormat.format (Runtime.getRuntime().freeMemory()),
        false);
		
		// Set the starting Tab that will be visible
		if (listAvailable) {
		  // tabs.getSelectionModel().select(viewTabPosition);
		}
		else {
		  tabs.getSelectionModel().select(0);
		}
		
		// Add the Tabbed Pane to the Applet
    primaryLayout.getChildren().add(tabs);
    primaryScene = new Scene(primaryLayout);

	} // end initComponents method	
  
	/**
	   Add the View tab to the interface. 
	 */
	private void addViewTab() {
    
		viewTab = new Tab("View");
    viewPane = new GridPane();
    fxUtils.applyStyle(viewPane);
    
    setupTableView();
    
		tabTableBuilt = true;
    // setTableModels();
    
    viewTab.setContent(viewPane);
    viewTab.setClosable(false);
    tabs.getTabs().add(viewTab);
	} // end method addViewTab
  
	/**
	   Add the Logging tab to the interface. 
	 */
	private void addLoggingTab () {
	
    // create the Logging pane
    logTab = new Tab("Logging");
    
		logPane = new GridPane();
    fxUtils.applyStyle(logPane);
    
    // create column 1 - Logging Output
    logOutputLabel = new Label ("Log Destination");
    fxUtils.addLabelHeading(logOutputLabel, 0, 0);

    logOutputGroup = new ToggleGroup();
  
    logOutputNoneButton = new RadioButton (LogJuggler.LOG_NONE_STRING);
    logOutputGroup.getToggles().add(logOutputNoneButton);
    logOutputNoneButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent evt) {
          logJuggler.switchLogOutput (LogJuggler.LOG_NONE_STRING);
		    } // end ActionPerformed method
		  } // end action listener
		);
    logPane.add(logOutputNoneButton, 0, 1);
    
    logOutputWindowButton = new RadioButton (LogJuggler.LOG_WINDOW_STRING);
    logOutputGroup.getToggles().add (logOutputWindowButton);
    logOutputWindowButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent evt) {
          logJuggler.switchLogOutput (LogJuggler.LOG_WINDOW_STRING);
		    } // end ActionPerformed method
		  } // end action listener
		);
    logPane.add(logOutputWindowButton, 0, 2);
		
    logOutputTextButton = new RadioButton (LogJuggler.LOG_TEXT_STRING);
    logOutputTextButton.setSelected (true);
    // Wait for this to be done later
    // logJuggler.switchLogOutput (LogJuggler.LOG_TEXT_STRING);
    logOutputGroup.getToggles().add (logOutputTextButton);
    logOutputTextButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent evt) {
          logJuggler.switchLogOutput (LogJuggler.LOG_TEXT_STRING);
		    } // end ActionPerformed method
		  } // end action listener
		);
    logPane.add(logOutputTextButton, 0, 3);
    
    logOutputDiskButton = new RadioButton (LogJuggler.LOG_DISK_STRING);
    logOutputGroup.getToggles().add (logOutputDiskButton);
    logOutputDiskButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent evt) {
          logJuggler.switchLogOutput (LogJuggler.LOG_DISK_STRING);
		    } // end ActionPerformed method
		  } // end action listener
		);
    logPane.add(logOutputDiskButton, 0, 4);
            
    // create column 2 - Logging Threshold
    logThresholdLabel = new Label ("Logging Threshold");
    fxUtils.addLabelHeading(logThresholdLabel, 1, 0);
  
    logThresholdGroup = new ToggleGroup();

    logThresholdNormalButton = new RadioButton (LOG_NORMAL_STRING);
    logThresholdNormalButton.setSelected (true);
    logJuggler.getLogger().setLogThreshold (LogEvent.NORMAL);
    logThresholdGroup.getToggles().add (logThresholdNormalButton);
    logThresholdNormalButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent evt) {
          logJuggler.getLogger().setLogThreshold (LogEvent.NORMAL);
		    } // end ActionPerformed method
		  } // end action listener
		);
    logPane.add(logThresholdNormalButton, 1, 1);
    
    logThresholdMinorButton = new RadioButton (LOG_MINOR_STRING);
    logThresholdGroup.getToggles().add (logThresholdMinorButton);
    logThresholdMinorButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent evt) {
          logJuggler.getLogger().setLogThreshold (LogEvent.MINOR);
		    } // end ActionPerformed method
		  } // end action listener
		);
    logPane.add(logThresholdMinorButton, 1, 2);
    
    logThresholdMediumButton = new RadioButton (LOG_MEDIUM_STRING);
    logThresholdGroup.getToggles().add (logThresholdMediumButton);
    logThresholdMediumButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent evt) {
          logJuggler.getLogger().setLogThreshold (LogEvent.MEDIUM);
		    } // end ActionPerformed method
		  } // end action listener
		);
    logPane.add(logThresholdMediumButton, 1, 3);
    
    logThresholdMajorButton = new RadioButton (LOG_MAJOR_STRING);
    logThresholdGroup.getToggles().add (logThresholdMajorButton);
    logThresholdMajorButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent evt) {
          logJuggler.getLogger().setLogThreshold (LogEvent.MAJOR);
		    } // end ActionPerformed method
		  } // end action listener
		);
    logPane.add(logThresholdMajorButton, 1, 4);
    
    // create column 3 - Log All Data? 
    logAllDataLabel = new Label ("Data Logging");
    fxUtils.addLabelHeading(logAllDataLabel, 2, 0);
    
    logAllDataCkBox = new CheckBox ("Log All Data?");
    logAllDataCkBox.setSelected (false);
    logJuggler.getLogger().setLogAllData (false);
    logAllDataCkBox.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent evt) {
          boolean logAllData = (logAllDataCkBox.isSelected());
          logJuggler.getLogger().setLogAllData (logAllData);
		    } // end itemStateChanged method
		  } // end action listener
		);
    logPane.add(logAllDataCkBox, 2, 1);
		
		// Bottom of Screen
		logText = new TextArea("");
		logText.setWrapText(true);
		logText.setEditable (false);
		logText.setVisible (true);
    logText.setMaxWidth(Double.MAX_VALUE);
    logText.setMaxHeight(Double.MAX_VALUE);
    logText.setPrefRowCount(100);
    logPane.add(logText, 0, 5, 3, 1);
    GridPane.setHgrow(logText, Priority.ALWAYS);
    GridPane.setVgrow(logText, Priority.ALWAYS);
    
    logTab.setContent(logPane);
    logTab.setClosable(false);
    tabs.getTabs().add(logTab);
		
    if (mainClass) {
      logJuggler.setTextArea (logText);
      logJuggler.switchLogOutput (LogJuggler.LOG_TEXT_STRING);
    }
    logJuggler.recordEvent(LogEvent.NORMAL, "Logging tab created", false);

	} // end addLoggingTab method
  
  /**
   Let's set to a default size and then center the window on the screen.. 
  */
  private void setDefaultScreenSizeAndLocation() {

    primaryStage.setWidth(DEFAULT_WIDTH);
    primaryStage.setHeight(DEFAULT_HEIGHT);
    primaryStage.setResizable(true);
		calcDefaultScreenLocation();
    primaryStage.setX(defaultX);
    primaryStage.setY(defaultY);
  }
  
  /**
   Let's try to center the window on the primary screen. 
  */
  private void calcDefaultScreenLocation() {
    Screen primaryScreen = Screen.getPrimary();
    Rectangle2D d = primaryScreen.getVisualBounds();
    defaultX = (d.getWidth() - primaryStage.getWidth()) / 2;
    defaultY = (d.getHeight() - primaryStage.getHeight()) / 2;
  }

  /**
   Indicate whether or not a list has been loaded. This method should be
   called each time a new list is loaded and/or each time the list is created
   anew.

   @param listAvailable True if a list has been loaded, false if the list
                        is not available.
  */
  public void setListAvailable (boolean listAvailable) {
    
    this.listAvailable = listAvailable;
    setupTableView();
    if (listAvailable && textMergeInput != null) {
		  fileNameLabel.setText (textMergeInput.getFileNameToDisplay());
    } else {
      fileNameLabel.setText("No Input File");
    }

    if (textMergeOutput != null) {
      textMergeOutput.setListAvailable(listAvailable);
    }
    
    if (listAvailable) {
      // dataTable = new DataTable (filteredDataSet);
      numberOfFields = list.getRecDef().getNumberOfFields();
      /*
      if (tabTableBuilt) {
        tabTable.setModel (list);
        fileNameLabel.setText (textMergeInput.getFileNameToDisplay());
        setTableModels();
      }
      */
      if (sortTabBuilt) {
        textMergeSort.setList(list);
      }

      if (filterTabBuilt) {
        textMergeFilter.setList(list);
      }
      
      if (templateTabBuilt) {
        textMergeTemplate.setList(list);
      }
      
    }

  }
  
  private void setupTableView() {
    // Update the List View
    if (viewTable != null) {
      viewPane.getChildren().remove(viewTable);
    }
    if (viewPane != null) {
      viewTable = new TableView<DataRecord>();
      viewTable.setPlaceholder(new Label("No Input File"));
      viewTable.setMaxWidth(Double.MAX_VALUE);
      viewTable.setMaxHeight(Double.MAX_VALUE);
      viewPane.add(viewTable, 0, 0, 1, 1);
      GridPane.setHgrow(viewTable, Priority.ALWAYS);
      GridPane.setVgrow(viewTable, Priority.ALWAYS);
      if (listAvailable
          && list != null
          && list.getRecDef() != null
          && list.getRecDef().getNumberOfFields() > 0) {
        viewTable.setItems(list.getList());
        for (int i = 0; i < list.getRecDef().getNumberOfFields(); i++) {
          TableColumn nextColumn = list.getTableColumn(i);
          viewTable.getColumns().add(nextColumn);
        }
      }
    }
  }

  /**
   Indicate whether or not a list has been loaded.

   @return True if a list has been loaded, false if the list is not
           available.
  */
  public boolean isListAvailable() {
    return listAvailable;
  }
  
  /**
     Standard way to respond to an About Menu Item Selection on a Mac.
   */
  public void handleAbout() {
    WindowMenuManager.getShared().locateUpperLeftAndMakeVisible
        (primaryStage, aboutWindow);
  }
  
  /**
     Let's wrap things up. 
   */
  public void wrapThingsUp() {	
    textMergeScript.stopScriptRecording();
    if (mainClass) {
      textMergeScript.savePrefs();
      textMergeTemplate.savePrefs();
      userPrefs.setPref (UserPrefs.LEFT, primaryStage.getX());
      userPrefs.setPref (UserPrefs.TOP, primaryStage.getY());
      userPrefs.setPref (UserPrefs.WIDTH, primaryStage.getWidth());
      userPrefs.setPref (UserPrefs.HEIGHT, primaryStage.getHeight());
      userPrefs.savePrefs();
    }
  }
  
  

}
