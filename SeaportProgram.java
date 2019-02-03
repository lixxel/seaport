// File: SeaportProgram.java
// Date: 8/12/18
// Author: Steph Anderson
// Purpose: simulate a number of sea ports
// Classes: SeaportProgram, Thing, World, Seaport, Dock, Ship,
//    PassengerShip, CargoShip, Person, Job, PortTime

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ConcurrentHashMap;


import java.io.File;

public class SeaportProgram extends JFrame {

   JTextArea jtaWorld = new JTextArea();
   JTextArea jtaSearch = new JTextArea();
   JTree worldTree;
   JComboBox<String> jcbSearchTarget,jcbSortTarget, jcbSortBy, jcbPorts;
   HashMap<String, List<String>> values = new HashMap<String, List<String>>();
   JTextField jtf;
   JTabbedPane jtp;
   JScrollPane jspWorld;
   JPanel jpInfoTab, jpWorldTab, jpSearchTab, jpJobTab, jpJob;
   JLabel jlWorkers;
   JTable jtJob;
   JFileChooser jfc = new JFileChooser (".");
   static String columnNames[] = {"Progress","Ship Name","Dock Name","Job Name","Requirements","Workers","Status","Cancel"};
               
   
   DefaultTableModel jobTable = new DefaultTableModel(columnNames, 0);
   
   World world;
   

   public SeaportProgram () {
      setTitle("Sea Port");
      setSize(1000,600);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
      
      //create tabs
      jtp = new JTabbedPane();
      jpInfoTab = new JPanel(new BorderLayout());
      jpWorldTab = new JPanel(new BorderLayout());
      jpSearchTab = new JPanel(new BorderLayout());
      jpJobTab = new JPanel(new BorderLayout());
      
      
      //formatting
      jtaSearch.setFont(new java.awt.Font ("Monospaced",0,12));
      
      
      
      //buttons
      JButton jbRead = new JButton("Load");
      JButton jbSort = new JButton("Sort");
      JButton jbCollapse = new JButton("Collapse");
      JButton jbExpand = new JButton("Expand");
      JButton jbRefresh = new JButton("Refresh");
      JButton jbSearch = new JButton("Search");
      
      
      //labels
      JLabel jlSearchTarget = new JLabel("Search target");
      JLabel jlSort = new JLabel ("Sort");
      JLabel jlBy = new JLabel ("by");      
      jtf = new JTextField(10);
      
      jlWorkers = new JLabel();
               
      
      //search target combo box
      jcbSearchTarget = new JComboBox<String>();
      jcbSearchTarget.addItem("Index");
      jcbSearchTarget.addItem("Type");
      jcbSearchTarget.addItem("Name");
      jcbSearchTarget.addItem("Skill");
      
      //sorting combo boxes
      values.put("Port", Arrays.asList("Name"));
      values.put("Dock", Arrays.asList("Name"));
      values.put("Que", Arrays.asList("Name", "Weight", "Length", "Width", "Draft"));
      values.put("Ship", Arrays.asList("Name", "Weight", "Length", "Width", "Draft"));
      values.put("Person", Arrays.asList("Name"));
      //values.put("Job", Arrays.asList("Name"));
      
      jcbSortTarget = new JComboBox<String>(values.keySet().toArray(new String[values.keySet().size()]));
      jcbSortBy = new JComboBox<String>(new DefaultComboBoxModel<String>());
      

      
      //top level panel
      JPanel jp = new JPanel();
      jp.add(jbRead);
      jp.add(jlSort);
      jp.add(jcbSortTarget);
      jp.add(jlBy);
      jp.add(jcbSortBy);
      jp.add(jbSort);
      
      add(jp,BorderLayout.PAGE_START);
      
      
      //search panel
      JPanel jpSearch = new JPanel();
      jpSearch.add(jlSearchTarget);
      jpSearch.add(jtf);
      jpSearch.add(jcbSearchTarget);
      jpSearch.add(jbSearch);
      
      //world panel
      JPanel jpWorld = new JPanel();
      jpWorld.add(jbExpand);
      jpWorld.add(jbCollapse);
      jpWorld.add(jbRefresh);
      
      //job panel
      jcbPorts= new JComboBox<String>();
        
      jpJob = new JPanel(new FlowLayout(FlowLayout.LEFT));
      jpJob.add(new JLabel("Port:"));
      jpJob.add(jcbPorts);
      jpJob.add(new JLabel("Workers:"));
      jpJob.add(jlWorkers);   
      
      jtJob = new JTable(jobTable);  
       
            
      //tabs
      jpInfoTab.add(new JScrollPane(jtaWorld));
      jpWorldTab.add (jpWorld, BorderLayout.PAGE_START);
      jpSearchTab.add(jpSearch, BorderLayout.PAGE_START);
      jpSearchTab.add(new JScrollPane(jtaSearch), BorderLayout.CENTER);
      jpJobTab.add(jpJob, BorderLayout.PAGE_START);
         JScrollPane jspJob = new JScrollPane(jtJob);
         jspJob.getVerticalScrollBar().setUnitIncrement(16);
      jpJobTab.add(jspJob, BorderLayout.CENTER);
      
      jtp.addTab("Info", jpInfoTab);
      jtp.addTab("World", jpWorldTab);
      jtp.addTab("Search", jpSearchTab);
      jtp.addTab("Jobs", jpJobTab);
      
      add(jtp, BorderLayout.CENTER);
      
      validate();
      
      //action listeners
      jbRead.addActionListener(e -> readFile());
      jbSearch.addActionListener(e -> search((String)(jcbSearchTarget.getSelectedItem()),jtf.getText()));
      jtf.addActionListener(e -> search((String)(jcbSearchTarget.getSelectedItem()),jtf.getText()));
      jbExpand.addActionListener(e -> expandAll(worldTree, true));      
      jbCollapse.addActionListener(e -> expandAll(worldTree, false)); 
      jbRefresh.addActionListener(e -> displaySeaport());     
      jcbSortTarget.addActionListener(e -> {
         List<String> sortByValues = values.get((String)jcbSortTarget.getSelectedItem());
         DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) jcbSortBy.getModel();
         model.removeAllElements();
         for (String s: sortByValues) model.addElement(s);
         jcbSortBy.setModel(model);
      });
      jbSort.addActionListener(e -> sort((String)jcbSortTarget.getSelectedItem(), (String)(jcbSortBy.getSelectedItem())));
      jcbPorts.addActionListener(e -> displayCurrentWorkers());
               
      
   } // end default constructor
   
   public void readFile(){
      int returnVal = jfc.showOpenDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            //This is where a real application would open the file.
            jtaWorld.append("Opening " + file.getName() + "...\n");
            
            try{
               
               Scanner sf = new Scanner (file);
               world = new World(sf , jtJob);
               
               //changing the dropdown for ports of the job frame when loading in a new file
               jcbPorts.removeAllItems();
               for (Seaport mp : world.ports){
                  jcbPorts.addItem(mp.name);
               }

               jtaWorld.append(file.getName() + " loaded.\n");
               displaySeaport();
            }
            catch (Exception e){
               e.printStackTrace();
               jtaWorld.append(e.getMessage() + "\nFile error: " + file.getName() + "\n");
            }
        } else {
            jtaWorld.append("Open command cancelled by user.\n");
        }
      
   } //end readFile()
   
   public void displaySeaport(){
      if (world!=null){
         if (worldTree!=null && jspWorld!=null) jpWorldTab.remove(jspWorld);
         worldTree = new JTree(world.toTree());
         worldTree.setFont(new java.awt.Font ("Monospaced",0,12));
         jspWorld = new JScrollPane(worldTree);
         jpWorldTab.add (jspWorld);
         validate();
      
      }
      else
         jtaWorld.append("No file loaded.");
   } //end displaySeaport()
   
   public void displayCurrentWorkers(){
      for (Seaport msp: world.ports) {
         if (msp.name.equals(jcbPorts.getSelectedItem())){
            ArrayList<String> workerList = new ArrayList<String>();
            
            for (Map.Entry<String,Semaphore> entry: msp.workers.entrySet()){
               String workerEntry = entry.getKey() + ": " + (String.valueOf(entry.getValue().availablePermits())) + "/" ;
               int workerCounter = 0;
               for (Person mp: msp.persons){
                  if (mp.skill.equals(entry.getKey())) workerCounter++;
               }
               workerEntry += String.valueOf(workerCounter);
               workerList.add(workerEntry);
            }
            jlWorkers.setText(workerList.toString());
         }
      }
   }// end displayCurrentWorkers()
   
   public void expandAll(JTree tree, boolean expandBoolean){
      if (tree!=null){
         if (expandBoolean){
            for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
            }
         }
         else{
            for (int i = tree.getRowCount() - 1; i >= 1; i--) {
               tree.collapseRow(i);
            }
         }
      }
   }//end expandAll
   
   public void search (String type, String target){
      if (target.equals(""))
         return;
      jtaSearch.setText(String.format ("Searching type: %s, target: %s...\n", type, target));
      if (world!=null){
         String st = "";
         if (type=="Index"){
            try {
               st += world.searchIndex(Integer.parseInt(target));
            } catch (NumberFormatException e){
               st += "Invalid input.";
            }
         }
         else if (type == "Type") st += world.searchType(target);
         else if (type=="Name") st += world.searchName(target);
         else if (type=="Skill") st += world.searchSkill(target);
         
         if (st != null && !st.isEmpty()) {
            jtaSearch.append(st);
         }
         else {
            jtaSearch.append("Not found.");
         }
      }//end if world!=null
      else
         jtaSearch.append("No file loaded.");
      jtaSearch.setCaretPosition(0);
   }//end search()
   
   public void sort(String target, String sortBy){
      if (world!=null){
         world.sort(target, sortBy);
      }
      displaySeaport();
   }//end queSort
   
   public static void main (String [] args) {
      try {
         SeaportProgram sp = new SeaportProgram();
      } catch (java.awt.IllegalComponentStateException e) {
      }
   } //end main
} //end class SeaportProgram






class Thing implements Comparable <Thing> {
   int index;
   String name;
   int parent;
   
   public Thing (Scanner sc){
      if (sc.hasNext()) name = sc.next();
      if (sc.hasNextInt()) index = sc.nextInt();
      if (sc.hasNext()) parent = sc.nextInt();
   }//end Scanner constructor
   
   @Override
   public int compareTo(Thing other){
      return this.name.compareTo(other.name);
      //return Integer.compare(this.index, other.index);
   }
   
}



class World {
   ArrayList<Seaport> ports = new ArrayList<Seaport>();
   PortTime time;
   DefaultTableModel jobTable;
   JTable jtJob;
   int rowCounter;
   
   public World (Scanner sc, JTable jtJob){
   
      this.jtJob = jtJob;
      jobTable = new DefaultTableModel(SeaportProgram.columnNames,0);
      rowCounter = 0;
      HashMap <Integer, Thing> hm = new HashMap <Integer,Thing> ();
      
      while (sc.hasNextLine()){
         process(sc.nextLine(),hm);
      }//end while
      
      
      //initialize persons skills
      for (Seaport msp: ports){
         msp.initializeResources();
      }
      
      //set up job table
      jtJob.setModel(jobTable);
      jtJob.getColumnModel().getColumn(0).setCellRenderer(new ProgressCellRenderer());
      jtJob.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
      jtJob.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
      jtJob.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(){
         void buttonClicked(){
            for (Seaport mp: ports){
               for (Ship ms: mp.ships){
                  for (Job mj: ms.jobs){
                     if (mj.currentRow == row) mj.toggleGoFlag();
                  }
               }
            }
         }
      });
      jtJob.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(){
         void buttonClicked(){
            for (Seaport mp: ports){
               for (Ship ms: mp.ships){
                  for (Job mj: ms.jobs){
                     if (mj.currentRow == row) mj.setKillFlag();
                  }
               }
            }
         }
      });            

      jobTable.fireTableDataChanged();
      
      //clear out ships without jobs from docks and que
      for (Seaport msp: ports){
         synchronized (msp){
            for (Dock md: msp.docks){
               if (md.ship!=null && md.ship.jobs.size()==0){
                  md.ship.parent = md.parent; 
                  md.ship = null;
                  msp.notifyAll();
               }
            }//end for docks
            
            Iterator itr = msp.que.iterator();
            while (itr.hasNext()){
               Ship ms = (Ship) itr.next();
               if (ms.jobs.size()==0) itr.remove();
            }
            
         }//end synchronized
      }//end for ports
      
   } //end Scanner constructor
   
   public DefaultMutableTreeNode toTree(){
      DefaultMutableTreeNode worldTree = new DefaultMutableTreeNode("The World");
      for (Seaport p: ports){
         worldTree.add(p.toTree());
      }
      return worldTree;
   }//end toTree()
   
   public String toString() {
      String st = ">>>>>The World:\n";
      for (Seaport p: ports) st += p + "\n";
      return st;
   }//end toString()
   
   
   void process (String st, HashMap<Integer,Thing> hm) {
     Scanner sc = new Scanner (st);
      if (!sc.hasNext())
         return;
      switch (sc.next()) {
         case "port": addPort(sc,hm); break;
         case "dock": addDock(sc,hm); break;
         case "ship": addShip(sc,hm); break;
         case "cship": addCShip(sc,hm); break;
         case "pship": addPShip(sc,hm); break;
         case "person": addPerson(sc,hm); break;
         case "job": addJob(sc,hm); break;
         case "//": return;
      } //end switch
    }// end process()
    
    
    /* 
      The following methods add objects to the internal data structure
   */ 
    
    void addPort(Scanner sc, HashMap<Integer,Thing> hm){
      Seaport msp = new Seaport(sc);
      ports.add(msp);
      hm.put(msp.index,msp);
    }//end addPort()
    
    void addDock(Scanner sc, HashMap<Integer,Thing> hm){
      Dock md = new Dock(sc);
      Seaport msp = (Seaport) hm.get(md.parent);
      if (msp == null) return;
      msp.docks.add(md);
      hm.put(md.index,md);
    }//end addDock()
    
    void addShip(Scanner sc, HashMap<Integer,Thing> hm){
      Ship ms = new Ship(sc);
      assignShip(ms,hm);
      hm.put(ms.index,ms);
    }//end addShip()
    
    void addCShip(Scanner sc, HashMap<Integer,Thing> hm){
      CargoShip ms = new CargoShip(sc);
      assignShip(ms,hm);
      hm.put(ms.index,ms);
    }//end addShip()
    
    void addPShip(Scanner sc, HashMap<Integer,Thing> hm){
      PassengerShip ms = new PassengerShip(sc);
      assignShip(ms,hm);
      hm.put(ms.index,ms);
    }//end addShip()
    
    void addPerson(Scanner sc, HashMap<Integer,Thing> hm){
      Person mp = new Person(sc);
      Seaport msp = (Seaport) hm.get(mp.parent);
      if (msp == null) return;
      msp.persons.add(mp);
    }//end addDock()
    
    void addJob(Scanner sc, HashMap<Integer,Thing> hm){
      Job mj = new Job(sc, hm, jobTable, rowCounter);
      rowCounter++;
      Ship ms = (Ship) hm.get(mj.parent);
      if (ms == null) return;
      ms.jobs.add(mj);
    }//end addJob()
       
    void assignShip (Ship ms, HashMap<Integer,Thing> hm) {
      try {
         Dock md = (Dock) hm.get(ms.parent);
         md.ship = ms;
         ((Seaport)hm.get(md.parent)).ships.add (ms);
      } catch (ClassCastException e){ 
        ((Seaport)hm.get(ms.parent)).ships.add (ms);
         ((Seaport)hm.get(ms.parent)).que.add (ms);
         return;
      }
   } // end method assignShip
    
    
    /*  
    The following four methods are for the search function
    By index
    By name
    By type
    By skill
    
   */
    
    public String searchIndex(int x){
      String st = "";
      
      for (Seaport msp: ports){
         if (msp.index == x) st += msp;
         for (Dock md: msp.docks)
            if (md.index == x) st +=  md;
         for (Ship ms: msp.ships){
            if (ms.index == x) st +=  ms;
            for (Job mj: ms.jobs)
               if (mj.index == x) st +=  mj;
            }
         for (Person mp: msp.persons)
            if (mp.index == x) st +=  mp;
      } // end for ports
      
      return st;
   } // end getThingByIndex()
   
   public String searchName(String mn){
      String st = "";
      
      for (Seaport msp: ports){
         if (msp.name.equalsIgnoreCase(mn)) st += msp;
         for (Dock md: msp.docks)
            if (md.name.equalsIgnoreCase(mn)) st += md;
         for (Ship ms: msp.ships){
            if (ms.name.equalsIgnoreCase(mn)) st += ms;
            for (Job mj: ms.jobs)
               if (mj.name.equalsIgnoreCase(mn)) st += mj;
         }
         for (Person mp: msp.persons)
            if (mp.name.equalsIgnoreCase(mn)) st += mp;
      } // end for ports
      
      return st;
   } //end getThingByName()
    
   public String searchType(String mt){
      String st = "";
      
      for (Seaport sp: ports){
         if (mt.equalsIgnoreCase("port")){
            st += sp + "\n";
         }
         else if (mt.equalsIgnoreCase("dock")){
            for (Dock d: sp.docks) st += d + "\n";
         }
         else if (mt.equalsIgnoreCase("ship") ){
            for (Ship s: sp.ships) st += s + "\n";
         }
         else if (mt.equalsIgnoreCase("cargo ship") || mt.equalsIgnoreCase("cship") ){
            for (Ship s: sp.ships) if (s instanceof CargoShip) st += s + "\n";
         }
         else if (mt.equalsIgnoreCase("passenger ship") || mt.equalsIgnoreCase("pship") ){
            for (Ship s: sp.ships) if (s instanceof PassengerShip) st += s + "\n";
         }
         else if (mt.equalsIgnoreCase("job")){
            for (Ship s: sp.ships){
               for (Job j: s.jobs) st+= j + "\n";
            }
         }
         else if (mt.equalsIgnoreCase("person") ){
            for (Person p: sp.persons) st += p + "\n";
         }
      } //end for ports
         
      if (st!="") st = "All " + mt + "s: \n" + st;
      
      return st;
   } //end getThingByType()
   
   public String searchSkill (String msk){
      String st = "";
      
      for (Seaport msp: ports){
         for (Person mp: msp.persons)
            if (mp.skill.equalsIgnoreCase(msk)) st += mp + "\n";
      } // end for ports 
      
      return st;
   } //end getThingBySkill



   
   /*
   The following method supports sorting of the ArrayLists (target) by different attributes (sortBy)
   */
   
   
   public void sort (String target, String sortBy){
      if (target=="Port"){
         Collections.sort(ports);
      }
      else if (target=="Dock"){
         if (sortBy=="Name"){
            for (Seaport msp: ports)
               Collections.sort(msp.docks);
         }
      }
      else if (target=="Que"){
         for (Seaport msp: ports){
            if (sortBy=="Weight"){
               Collections.sort(msp.que, (a,b) -> Double.compare(a.weight,b.weight));
            }
            else if (sortBy=="Length"){
               Collections.sort(msp.que, (a,b) -> Double.compare(a.length,b.length));
            }
            else if (sortBy=="Width"){
               Collections.sort(msp.que, (a,b) -> Double.compare(a.width,b.width));
            }
            else if (sortBy=="Draft"){
               Collections.sort(msp.que, (a,b) -> Double.compare(a.draft,b.draft));
            }
            else if (sortBy=="Name"){
               Collections.sort(msp.que);
            }
         }
      }
      else if (target=="Ship"){
         for (Seaport msp: ports){
            if (sortBy=="Weight"){
               Collections.sort(msp.ships, (a,b) -> Double.compare(a.weight,b.weight));
            }
            else if (sortBy=="Length"){
               Collections.sort(msp.ships, (a,b) -> Double.compare(a.length,b.length));
            }
            else if (sortBy=="Width"){
               Collections.sort(msp.ships, (a,b) -> Double.compare(a.width,b.width));
            }
            else if (sortBy=="Draft"){
               Collections.sort(msp.ships, (a,b) -> Double.compare(a.draft,b.draft));
            }
            else if (sortBy=="Name"){
               Collections.sort(msp.ships);
            }
         }
      }
      else if (target=="Person"){
         if (sortBy=="Name"){
            for (Seaport msp: ports)
               Collections.sort(msp.persons);
         }
      }
      /*
      else if (target=="Job"){
         if (sortBy=="Name"){
            for (Seaport msp: ports)
               for (Ship ms: msp.ships)
                  Collections.sort(ms.jobs);
         }
      }
      */
   }//end sort()
       
} //end class World



class Seaport extends Thing{
   ArrayList<Dock> docks = new ArrayList<Dock>();
   ArrayList<Ship> que = new ArrayList<Ship>();
   ArrayList<Ship> ships = new ArrayList<Ship>();
   ArrayList<Person> persons = new ArrayList<Person>();
   HashMap<String, Semaphore> workers = new HashMap<String, Semaphore>();
   
   public Seaport(Scanner sc){
      super(sc);
   } //end Scanner constructor
   
   
   //create worker semaphores
   public void initializeResources(){
      HashMap<String,Integer> allSkills= new HashMap<String,Integer>();
      for (Person mp: persons){
         if (allSkills.get(mp.skill)==null){
            allSkills.put(mp.skill, 1);
         }
         else{
            allSkills.put(mp.skill, allSkills.get(mp.skill) + 1);
         }
      }//end for persons
      
      //workers = 
      for (Map.Entry<String,Integer> entry: allSkills.entrySet()){
         workers.put(entry.getKey(), new Semaphore(entry.getValue()));
      }
      
   }//end initresources
   
   
   public DefaultMutableTreeNode toTree(){
      DefaultMutableTreeNode portTree = new DefaultMutableTreeNode(String.format ("\nSeaPort: %s %s\n", name, index));
      DefaultMutableTreeNode dockTree = new DefaultMutableTreeNode("Docks");
      for (Dock d: docks){
         dockTree.add(d.toTree());
      }
      portTree.add(dockTree);
      
      DefaultMutableTreeNode queTree = new DefaultMutableTreeNode("All Ships in Que");
      if (que.size()==0) queTree.add(new DefaultMutableTreeNode("No ships in que."));
      Iterator<Ship> it = que.iterator();
      while (it.hasNext()){
         queTree.add(it.next().toTree());
      }
      /*
      for (Ship s: que){
        queTree.add(s.toTree());
      }*/
      portTree.add(queTree);
      
      DefaultMutableTreeNode shipTree = new DefaultMutableTreeNode("All Ships");
      if (ships.size()==0) shipTree.add(new DefaultMutableTreeNode("No ships at dock."));
      for (Ship s: ships){
         shipTree.add(s.toTree());
      }
      portTree.add(shipTree);
      
      DefaultMutableTreeNode personTree = new DefaultMutableTreeNode("All Persons");
      if (persons.size()==0) personTree.add(new DefaultMutableTreeNode("No persons at dock."));
      for (Person p: persons){
         personTree.add(p.toTree());
      }
      portTree.add(personTree);
      
      return portTree;
   }//end toTree()
   
   public String toString(){
      String st = String.format ("\nSeaPort: %s %s\n", name, index);
      for (Dock d: docks) st += d + "\n";
      st += "\n---List of all ships in que:\n";
      for (Ship s: que) st += "      > " + s + "\n";
      st += "\n---List of all ships:\n";
      for (Ship s: ships) st += "      > " + s + "\n";
      st += "\n---List of all persons:\n";
      for (Person p: persons) st += "      > " + p + "\n";
      return st;
   }//end toString()
   
}//end class SeaPort

class Dock extends Thing{
   
   public Dock (Scanner sc){
      super(sc);
   } //end Scanner constructor
   Ship ship;
   
   public DefaultMutableTreeNode toTree(){
      DefaultMutableTreeNode dockTree = new DefaultMutableTreeNode(String.format ("Dock: %s %s", name, index));
      if (ship!=null){
      dockTree.add(ship.toTree());
      } else {
         dockTree.add(new DefaultMutableTreeNode("No ship docked."));
      }
      
      return dockTree;
   }//end toTree()
   
   public String toString(){
      String str = String.format ("\nDock: %s %s", name, index);
      if (ship!=null) str += String.format("\n  Ship: %s", ship);
      return str;
   }//end toString()
   
}//end class Dock

class Ship extends Thing{
   PortTime arrivalTime, dockTime;
   double draft, length, weight, width;
   ArrayList<Job> jobs = new ArrayList<Job>();
   
   public Ship (Scanner sc){
      super(sc);
      if (sc.hasNextDouble()) weight = sc.nextDouble();
      if (sc.hasNextDouble()) length = sc.nextDouble();
      if (sc.hasNextDouble()) width = sc.nextDouble();
      if (sc.hasNextDouble()) draft = sc.nextDouble();
      
   } //end Scanner Constructor
   
  
  public DefaultMutableTreeNode toTree(){
      DefaultMutableTreeNode shipTree = new DefaultMutableTreeNode(String.format("%-16s %-5s weight: %-6s length: %-6s width: %-6s draft: %-5s", name, index, weight, length, width, draft));
      if (jobs.size()==0) return shipTree;
      for (Job mj: jobs){
         shipTree.add(mj.toTree());
      }
      return shipTree;
   }//end toTree() 
   
   
   public String toString(){
      String st = String.format("%-16s %-5s weight: %-6s length: %-6s width: %-6s draft: %-5s", name, index, weight, length, width, draft);
      if (jobs.size()==0) return st;
      for (Job mj: jobs) st += "\n         " + mj;
      return st;
   }//end toString()
   
}//end class Ship

class PassengerShip extends Ship{
   int numberOfOccupiedRooms, numberOfPassengers, numberOfRooms;
   
   public PassengerShip (Scanner sc){
      super(sc);
      if (sc.hasNextInt()) numberOfPassengers = sc.nextInt();
      if (sc.hasNextInt()) numberOfRooms = sc.nextInt();
      if (sc.hasNextInt()) numberOfOccupiedRooms = sc.nextInt();
   } //end Scanner Constructor
   
   public DefaultMutableTreeNode toTree(){
      DefaultMutableTreeNode shipTree = super.toTree();
      shipTree.setUserObject("Passenger Ship: " + super.toTree().toString());
      return shipTree;
   }//end toTree()
   
   public String toString(){
      return "Passenger Ship: " + super.toString();
   }//end toString()
   
}//end class PassengerShip

class CargoShip extends Ship{
   double cargoValue, cargoVolume, cargoWeight;
   
   public CargoShip (Scanner sc){
      super(sc);
      if (sc.hasNextDouble()) cargoWeight = sc.nextDouble();
      if (sc.hasNextDouble()) cargoVolume = sc.nextDouble();
      if (sc.hasNextDouble()) cargoValue = sc.nextDouble();
   } //end Scanner Constructor
   
   public DefaultMutableTreeNode toTree(){
      DefaultMutableTreeNode shipTree = super.toTree();
      shipTree.setUserObject("Cargo Ship:     " + super.toTree().toString());
      return shipTree;
   }//end toTree()
   
   public String toString(){
      return "Cargo Ship:     " + super.toString();
   }//end toString()
   
}//end class CargoShip

class Person extends Thing{
   String skill;
   
   public Person (Scanner sc){
      super(sc);
      if (sc.hasNext()) skill = sc.next();
   } //end Scanner Constructor
   
   public DefaultMutableTreeNode toTree(){
     return new DefaultMutableTreeNode(String.format("Person: %-10s %-5s %s", name, index, skill));
   }//end toTree()
   
   public String toString(){
      return String.format ("Person: %-10s %-5s %s", name, index, skill);
   }//end toString()
   
}//end class Person

class Job extends Thing implements Runnable{

   double duration;
   ArrayList<String> requirements = new ArrayList<String>();
   
   Object [] tableRow = new Object[8];
   Ship parentShip = null;
   boolean goFlag = true, noKillFlag = true, resourceFlag = false;
   Status status = Status.SUSPENDED;
   DefaultTableModel jobTable;
   int currentRow;
   
   HashMap hm;
   
   enum Status {RUNNING, SUSPENDED, WAITING, DONE, WAITINGWORKER, NORESOURCE};
   
   public Job (Scanner sc, HashMap <Integer,Thing> hm, DefaultTableModel jobTable, int currentRow){
      super(sc);
      if (sc.hasNextDouble()) duration = sc.nextDouble();
      while (sc.hasNext()) requirements.add(sc.next());
      
      //sort requirements so we wait on lowest requirement, solution to dining philosophers
      Collections.sort(requirements);
      
      this.currentRow = currentRow;
      this.jobTable = jobTable;
      this.hm = hm;
      parentShip = (Ship) hm.get(parent);
      
      
      //{"Progress","Ship Name","Dock Name","Job Name","Requirements","Workers","Status","Cancel"}
      tableRow[0] = 0;
      tableRow[1]= parentShip.name;
      tableRow[2]= (!(hm.get(parentShip.parent) instanceof Dock) ? "" : hm.get(parentShip.parent).name);
      tableRow[3]= name;
      tableRow[4]= (requirements.isEmpty() ? "--" : requirements.toString());
      tableRow[5]= "";
      tableRow[6]= "";
      tableRow[7]= "Cancel";
      
      jobTable.addRow(tableRow);
      
      new Thread(this,name).start();
   } //end Scanner constructor
   
   
   public void toggleGoFlag(){
      goFlag = !goFlag;
   }//end toggleGoFlag()
   
   public void setKillFlag(){
      noKillFlag = false;
      
      if (!(hm.get(parentShip.parent) instanceof Dock)){
         Seaport parentPort = (Seaport) hm.get(parentShip.parent);
         synchronized (parentPort){
            parentPort.notifyAll();
         }
      }
      
   }//end setKillFlag
   
   void showStatus (Status st){
      status = st;
      switch(status){
         case RUNNING:
            jobTable.setValueAt("Running", currentRow, 6);
            jobTable.fireTableCellUpdated(currentRow, 6);
            break;
         case SUSPENDED:
            jobTable.setValueAt("Suspended", currentRow, 6);
            jobTable.fireTableCellUpdated(currentRow, 6);
            break;
         case WAITING:
            jobTable.setValueAt("Waiting turn", currentRow, 6);
            jobTable.fireTableCellUpdated(currentRow, 6);
            break;
         case DONE:
            jobTable.setValueAt("Done", currentRow, 6);
            jobTable.fireTableCellUpdated(currentRow, 6);
            break;
         case WAITINGWORKER:
            jobTable.setValueAt("Waiting on workers", currentRow, 6);
            jobTable.fireTableCellUpdated(currentRow, 6);
            break;
      }//end switch
   }//end showStatus
   
   public void run(){
      long time = System.currentTimeMillis();
      long startTime = time;
      long stopTime = time + 1000 * (long) duration;     //correct version
      //long stopTime = time + 100 * (long) duration;        //fast version
      double secDuration = stopTime - time;
      ConcurrentHashMap<String,Semaphore> currentWorkers = new ConcurrentHashMap<String,Semaphore>();
      
      //WAITING FOR DOCK
      if (!(hm.get(parentShip.parent) instanceof Dock)){
         Seaport parentPort = (Seaport) hm.get(parentShip.parent);
         synchronized (parentPort){
            while (noKillFlag && !(hm.get(parentShip.parent) instanceof Dock)){
               showStatus (Status.WAITING);
               try {
                  parentPort.wait();
               }catch (InterruptedException e){}
               for (Dock md : parentPort.docks){
                  //dock ship
                  if (md.ship == null && (!(hm.get(parentShip.parent) instanceof Dock))){
                     md.ship = parentShip;
                     parentShip.parent = md.index;
                     parentPort.que.remove(md.ship);
                  }
               }
               
               
            }//end while waiting
            if (noKillFlag){
               jobTable.setValueAt(((Dock)hm.get(parentShip.parent)).name, currentRow, 2);
               jobTable.fireTableCellUpdated(currentRow, 2);
            }
         }//end synchronized on parent ship
      } //end if undocked
      
      
      if (noKillFlag) {
         try {
         
            //ACQUIRING WORKERS
            for (String req: requirements){
               Semaphore sem = ((Seaport) hm.get(((Dock) hm.get(parentShip.parent)).parent)).workers.get(req);
               if (sem!=null){
                  currentWorkers.put(req,sem);
               }
               else {
                  setKillFlag();
                  resourceFlag = true;
               }
            }
            if (resourceFlag==false){
               ArrayList<String> waitingOn = new ArrayList<String>();
               for (Map.Entry<String,Semaphore> entry: currentWorkers.entrySet()){
                  showStatus(Status.WAITINGWORKER);
                  entry.getValue().acquire();
                  waitingOn.add(entry.getKey());
                  jobTable.setValueAt(waitingOn.isEmpty() ? "" : waitingOn.toString(), currentRow, 5);
                  jobTable.fireTableCellUpdated(currentRow, 5);
               }

            }
            
            //RUNNING   
            while (time < stopTime && noKillFlag){
               try{
                  Thread.sleep(100);
               } catch (InterruptedException e){}
               if (goFlag) {
                  showStatus(Status.RUNNING);
                  time +=100;
                  jobTable.setValueAt((int)(((time - startTime) / secDuration) * 100), currentRow, 0);
                  jobTable.fireTableCellUpdated(currentRow, 0);
               }
               else {
                  showStatus(Status.SUSPENDED);
               }//end if
            }//end while
         } catch (InterruptedException exc){}
         
      }//end if nokillFlag
      
      
      //DONE
      jobTable.setValueAt(100, currentRow, 0);
      jobTable.fireTableCellUpdated(currentRow, 0);
      showStatus (Status.DONE);
      
      for (Map.Entry<String,Semaphore> entry: currentWorkers.entrySet()){
         if (resourceFlag==false) entry.getValue().release();
         currentWorkers.remove(entry.getKey());
      }
      jobTable.setValueAt(currentWorkers.isEmpty() ? "" : currentWorkers.keySet().toString(), currentRow, 5);
      jobTable.fireTableCellUpdated(currentRow, 5);
      
      //UNDOCKING
      if ((hm.get(parentShip.parent) instanceof Dock)){
         Dock parentDock = (Dock) hm.get(parentShip.parent);
         synchronized ((Seaport) hm.get(parentDock.parent)){
            Seaport parentPort = (Seaport) hm.get(parentDock.parent);
         
         
            boolean allDone = true;
            for (Job mj: parentShip.jobs){
               if (mj.status!=Status.DONE) allDone = false;
            }
            if (allDone){
               //undock ship
               parentShip.parent = parentDock.parent;
               parentDock.ship = null;
               parentPort.notifyAll();
               for (Job mj: parentShip.jobs) {
                  //clear dock name
                  jobTable.setValueAt("", mj.currentRow, 2);
                  jobTable.fireTableCellUpdated(mj.currentRow, 2);
               }
            }
         } //end synchronized
      }//end if
      
      //mark impossible
      if (resourceFlag) {
         jobTable.setValueAt("Impossible", currentRow, 6);
         jobTable.fireTableCellUpdated(currentRow, 6);
      }
      
      
   }//end run()
   
   
   public DefaultMutableTreeNode toTree(){
      DefaultMutableTreeNode jobTree = new DefaultMutableTreeNode(String.format("Job: %s %s", name, index));
      if (requirements.size()==0) return jobTree;
      for (String s: requirements){
         jobTree.add(new DefaultMutableTreeNode(s));
      }
      
      return jobTree;
   }//end toTree() 
   
   public String toString(){
      String st = String.format("Job: %s %s", name, index);
      if (requirements.size()!=0){
         st += ", Requirements: ";
         for (String s: requirements) st+= s + " ";
      }
      return st;
   }//end toString()
  
   
}//end class Job

class PortTime{
   int time;
}//end class PortTime

class ProgressCellRenderer extends JProgressBar
                        implements TableCellRenderer {
 
   public ProgressCellRenderer(){
      super(0, 100);
      setValue(0);
      setString("0%");
      setStringPainted(true);
   }
 
   @Override
   public Component getTableCellRendererComponent(
                                    JTable table,
                                    Object value,
                                    boolean isSelected,
                                    boolean hasFocus,
                                    int row,
                                    int column) {
      setValue((Integer) value);
      setString(value.toString() + "%");
      return this;
  }
}//end class ProgressCellRenderer



class ButtonRenderer implements TableCellRenderer
{
   JButton btn = new JButton ("");
   public ButtonRenderer() {
      btn.setOpaque(true);
   }
   @Override
   public Component getTableCellRendererComponent(
                                   JTable table,
                                   Object obj,
                                   boolean selected,
                                   boolean focused,
                                   int row,
                                   int col) {
                                   
      String status = (obj==null) ? "" : obj.toString();
      btn.setText(status);
      btn.setOpaque(true);
      
      switch(status){
         case "Running":
            btn.setBackground(Color.green);
            break;
         case "Suspended":
            btn.setBackground(Color.yellow);
            break;
         case "Waiting turn":
            btn.setBackground(Color.orange);
            break;
         case "Done":
            btn.setBackground(Color.red);
            //jbKill.setEnabled(false);
            break;
         case "Waiting on workers":
            btn.setBackground(Color.yellow);
            break;
         case "Impossible":
            btn.setBackground(Color.red);
            break;
         case "Cancel":
            btn.setOpaque(false);
            break;
      }//end switch


    return btn;
  }

}//end class ButtonRenderer

class	ButtonEditor extends	DefaultCellEditor	{
   protected JButton	btn;
	private String	status;
	private boolean clicked;
	int row;

	public ButtonEditor() {
      super(new JTextField());
      
      btn=new	JButton();
      btn.setOpaque(true);

      btn.addActionListener(	e-> fireEditingStopped());
      //btn.addActionListener(e	->	System.out.println("clicked"));
  }

  @Override
   public Component getTableCellEditorComponent(JTable table, Object obj,
                                 boolean selected, int row, int col){

      status = (obj==null) ? "" : obj.toString();
      
      this.row = row;		 
      btn.setText(status);
      clicked=true;
      return	btn;
   }

   @Override
   public Object getCellEditorValue(){

      if(clicked) buttonClicked();
      clicked=false;
      return new String(status);
  }

   @Override
   public boolean stopCellEditing(){
      clicked=false;
      return super.stopCellEditing();
   }

   @Override
   protected void fireEditingStopped(){
      // TODO	Auto-generated	method stub
      super.fireEditingStopped();
   }
  
   void buttonClicked(){
   //override this
   }
}//end class ButtonEditor