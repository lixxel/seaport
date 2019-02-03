// File: CreateSeaPortDataFile.java
// Date: Jul 8, 2016
// Author: Nicholas Duchon
// Purpose: CMSC 335 new project

import java.util.Scanner; 
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class CreateSeaPortDataFile {
   static int nt = 8; // number of ports
   static int nd = 15; // number of docks per port + 5 (>0)
   static int np = 20; // number of passenger ships per port
   static int nc = 20; // number of cargo ships per port
   static int nj = 5; // number of jobs per dock (>0)
   static int nn = 30; // number of persons per port + 5
   static String filename = "aSPad.txt";
   
   static String portSpec = 
     "// port   name index parent(null)\n" +
     "//    port   <string> <int> <int>\n";
   static String dockSpec = 
     "// dock   name index parent(port)\n" +
     "//    dock   <string> <int> <int>\n";
   static String shipSpec = 
     "// ship   name index parent(dock/port) weight length width draft\n" +
     "//    ship   <string> <int> <int> <double> <double> <double> <double>\n";
   static String cshipSpec = 
     "// cship  name index parent(dock/port) weight length width draft cargoWeight cargoVolume cargoValue\n" +
     "//    cship  <string> <int> <int> <double> <double> <double> <double> <double> <double> <double>\n";
   static String pshipSpec = 
     "// pship  name index parent(dock/port) weight length width draft numPassengers numRooms numOccupied\n" +
     "//    pship  <string> <int> <int> <double> <double> <double> <double> <int> <int> <int>\n";
   static String personSpec = 
     "// person name index parent skill\n" +
     "//    person <string> <int> <int> <string>\n";
   static String jobSpec = 
     "// job    name index parent duration [skill]+ (one or more, matches skill in person, may repeat)\n" +
     "//    job    <string> <int> <int> <double> [<string>]+\n";

   public static void main (String [] args) {
      if (args.length > 0) nt = Integer.parseInt (args [0]);
      if (args.length > 1) nd = Integer.parseInt (args [1]);
      if (args.length > 2) np = Integer.parseInt (args [2]);
      if (args.length > 3) nc = Integer.parseInt (args [3]);
      if (args.length > 4) nj = Integer.parseInt (args [4]);
      if (args.length > 5) nn = Integer.parseInt (args [5]);
      MyWorld mw;
      mw = new MyWorld ();
      // number of port, docks per port, num pass ships, num cargo ships, jobs per dock, persons per port
      mw.createRandomPorts (nt, nd, np, nc, nj, nn); 
   //       System.out.println ("\n\n>>>>> Random world:\n" + mw.toFileString());
      
      ArrayList <MyThing> mwa = new ArrayList <> ();
      mw.toArray (mwa); 
      java.util.Collections.sort(mwa);
   
      try {
         int type = 0;
         java.io.PrintStream pw = System.out; // comment out next line to send output to console
         pw = new java.io.PrintStream (new File (filename));
         pw.println ("// File: " + filename);
         pw.println ("// Data file for SeaPort projects");
         java.util.Date date = new java.util.Date ();
         pw.printf ("// Date: %tc\n", date);
         pw.printf ("// parameters: %d %d %d %d %d %d\n", nt, nd, np, nc, nj, nn);
         pw.printf ("//   ports, docks, pships, cships, jobs, persons\n");
         // shuffle within category
         int start = 0;
         int end = 0;
         ArrayList <MyThing> sub = new ArrayList <> ();
         ArrayList <MyThing> mta = new ArrayList <> ();
         for (int i = 1; i < 7; i++) {
            sub.clear ();
            while (start < mwa.size() && mwa.get (start).index/10000 < i) start ++;
            end = start;
            if (end >= mwa.size ()) 
               continue;
            while (end < mwa.size() && mwa.get (end).index/10000 == i) {
               sub.add (mwa.get(end)); 
               end ++;
            }
            java.util.Collections.shuffle (sub);
            mta.addAll (sub);
            System.out.printf ("index: %d Sub: %s\n", i, "sub?");
         } // end for each category
         mwa = mta;
         for (MyThing mt: mwa) {
            if (mt.index/10000 != type) {
               pw.print ("\n");
               switch (mt.index/10000) {
                  case 1: pw.print (portSpec)  ; 
                     break;
                  case 2: pw.print (dockSpec)  ; 
                     break;
                  case 3: pw.print (pshipSpec) ; 
                     break;
                  case 4: pw.print (cshipSpec) ; 
                     break;
                  case 5: pw.print (personSpec); 
                     break;
                  case 6: pw.print (jobSpec)   ; 
                     break;
               } // end switch
               type = mt.index/10000;
            } // end if changing type
            pw.print (mt.toFileString());
         } // end writing data file
      } 
      catch (java.io.FileNotFoundException e) {
         System.out.println ("output file not found");
      }
      
      System.out.println ("done");
   } // end main
   
} // end class CreateSeaPortDataFile

class MyWorld extends MyThing {
   ArrayList <MySeaPort> ports = new ArrayList <> ();
   MyTime time = new MyTime (0);
      
   public void createRandomPorts (int n, int numDocks, int numPass, int numCargo, int numJobs, int numPersons) {
      for (int i = 0; i < n; i++)
         ports.add (new MySeaPort (numDocks, numPass, numCargo, numJobs, numPersons));
   } // end method createRandomPorts
   
   public void toArray (List <MyThing> ma) {
      for (MySeaPort mp: ports) mp.toArray (ma);
   } // end toArray
   
} // end class MyWorld

class MyThing implements Comparable <MyThing> {
   static java.util.Random rn = new java.util.Random ();
   static ArrayList <String> skillNames = null; // to read file only once
   
   String name = null;
   int index = 0;
   int parent = 0;
   
   public MyThing () {}
   
   void readSkillsFile () {
      try {
         skillNames = new ArrayList <> ();
         Scanner sp = new Scanner (new File ("skillNames.txt"));
         while (sp.hasNext()) skillNames.add (sp.next());
         System.out.println ("Skills File size: " + skillNames.size());  
      } 
      catch (java.io.FileNotFoundException e) {System.out.println ("bad file");}
   } // end readWordsFile

   void toArray (List <MyThing> mta) {
      mta.add (this);
   } // end method toList
   
   public int compareTo (MyThing m) {
      return index - m.index;
   } // end method compareTo > Comparable
   
   public String toFileString () {
      return "";
   } // default toFileString method in MyThing
} // end class MyThing

class MySeaPort extends MyThing {
   static int indexNew = 10000;
   static ArrayList <String> portNames = null;
   
   double latitude  = 0;
   double longitude = 0;
   ArrayList <MyDock>   docks   = new ArrayList <> ();
   ArrayList <MyShip>   que     = new ArrayList <> ();
   ArrayList <MyShip>   ships   = new ArrayList <> ();
   ArrayList <MyPerson> persons = new ArrayList <> ();
   
   public MySeaPort (int numDocks, int numPass, int numCargo, int numJobs, int numPersons) {
      if (portNames == null) readPortsFile ();
      name = portNames.get (rn.nextInt (portNames.size()));
      index = indexNew++;
      parent = 0;
      int remainingDocks = rn.nextInt (numDocks) + 5;
      while (remainingDocks > 0 && numPass > 0) {
         MyPassengerShip mps = new MyPassengerShip (true, numJobs); // random ship
         ships.add (mps);
         MyDock md = new MyDock (true); // random dock
         docks.add (md);
         md.ship = mps;
         md.parent = index;
         mps.parent = md.index;
         numPass --;
         remainingDocks--;
      } // end adding passenger ships first
      while (numPass > 0) {
         MyPassengerShip mps = new MyPassengerShip (true, numJobs); // random ship
         ships.add (mps);
         que.add (mps);
         mps.parent = index;
         numPass --;
      } // end remaining passenger ships
      while (remainingDocks > 0 && numCargo > 0) {
         MyCargoShip mpc = new MyCargoShip (true, numJobs); // random ship
         ships.add (mpc);
         MyDock md = new MyDock (true); // random dock
         docks.add (md);
         md.ship = mpc;
         md.parent = index;
         mpc.parent = md.index;
         numCargo --;
         remainingDocks--;
      } // end adding passenger ships first
      while (numCargo > 0) {
         MyCargoShip mpc = new MyCargoShip (true, numJobs); // random ship
         ships.add (mpc);
         que.add (mpc);
         mpc.parent = index;
         numCargo --;
      } // end remaining passenger ships
      
      for (int i = 0; i < rn.nextInt (numPersons) + 5; i++) {
         persons.add (new MyPerson (this));
      }
   } // end list of port names constructor - creates a random port

   void readPortsFile () {
      try {
         portNames = new ArrayList <> ();
         Scanner sp = new Scanner (new File ("portNames.txt"));
         while (sp.hasNext()) portNames.add (sp.next());
         System.out.println ("Ports file size: " + portNames.size());  
      } 
      catch (java.io.FileNotFoundException e) {System.out.println ("bad file");}
   } // end method readPortsFile
   
   void toArray (List <MyThing> ma) {
      ma.add (this);
      for (MyDock   md: docks  ) md.toArray (ma);
      for (MyShip   ms: ships  ) ms.toArray (ma);
      for (MyPerson mp: persons) mp.toArray (ma);
   } // end toArray in MySeaPort

   public String toFileString () {
      return String.format ("port %s %d %d\n", name, index, 0);
   } // end method toFileString

} // end class MySeaPort

class MyDock extends MyThing {
   static int indexNew = 20000;
   
   MyShip ship = null;

   public MyDock (boolean f) {
      index = indexNew++;
      name = "Pier_"  + index%2000;
   } // end random ship constructor

   public String toFileString () {
      String st = "";
      st += String.format ("  dock %s %d %d %d\n", name, index, parent, ship.index);
      return st;
   } // end method toFileString

} // end class MyDock

class MyShip extends MyThing {
   static ArrayList <String> words = null;

   double weight = 0;
   double length = 0;
   double width  = 0;
   double draft  = 0;
   MyTime arrivalTime = null;
   MyTime dockTime    = null;
   ArrayList <MyJob> jobs = new ArrayList <> ();

   public MyShip () {} // end no-parameter constructor
   
   public MyShip (boolean t, int n) {
      if (words == null) readWordsFile ();
      name = words.get (rn.nextInt (words.size()));
      weight =  50 + rn.nextDouble () * 200;
      length = 100 + rn.nextDouble () * 400;
      width  =  30 + rn.nextDouble () * 100;
      draft  =  15 + rn.nextDouble () *  30;
   } // create a random ship
   
   void readWordsFile () {
      try {
         words = new ArrayList <> ();
         Scanner sp = new Scanner (new File ("shipNames.txt"));
         while (sp.hasNext()) words.add (sp.next());
         System.out.println ("Words File size: " + words.size());  
      } 
      catch (java.io.FileNotFoundException e) {System.out.println ("bad file");}
   } // end readWordsFile

   void toArray (List <MyThing> mta) {
      mta.add (this);
      for (MyJob mj: jobs) mj.toArray (mta);
   } // end method toList
   
   public String toFileString () {
      String st = "";
      st += String.format ("    ship %20s %d %d %.2f %.2f %.2f %.2f\n", 
             name, index, parent, weight, length, width, draft);
      return st;
   } // end method toFileString

} // end class MyShip

class MyCargoShip extends MyShip {
   static int indexNew = 40000;
   
   double cargoWeight = 0;
   double cargoVolume = 0;
   double cargoValue  = 0;
   
   public MyCargoShip (boolean f, int n) {
      super (f, n);
      index = indexNew++;
      cargoWeight =  20 + rn.nextDouble() *  200;
      cargoVolume = 100 + rn.nextDouble() *  100;
      cargoValue  =  10 + rn.nextDouble() * 1000;
      for (int i = 0; i < rn.nextInt (n); i++)
         jobs.add (new MyJob (this));
   } // end random ship constructor

   public String toFileString () {
      String st = "";
      st += String.format ("    cship %20s %d %d %.2f %.2f %.2f %.2f %.2f %.2f %.2f\n", 
             name, index, parent, weight, length, width, draft,
             cargoWeight, cargoVolume, cargoValue);
      return st;
   } // end method toFileString
   
} // end class MyCargoShip

class MyPassengerShip extends MyShip {
   static int indexNew = 30000;
   
   int numberOfPassengers    = 0;
   int numberOfRooms         = 0;
   int numberOfOccupiedRooms = 0;

   public MyPassengerShip (boolean f, int n) {
      super (f, n);
      index = indexNew++;
      numberOfRooms         = 100 + rn.nextInt (1000);
      numberOfPassengers    = Math.round(numberOfRooms * rn.nextFloat() * 4);
      numberOfOccupiedRooms = Math.min (numberOfRooms, numberOfPassengers/2);
      for (int i = 0; i < rn.nextInt (n); i++)
         jobs.add (new MyJob (this));
   } // end random ship constructor

   public String toFileString () {
      return String.format ("    pship %20s %d %d %.2f %.2f %.2f %.2f %d %d %d\n", 
             name, index, parent, weight, length, width, draft,
             numberOfPassengers, numberOfRooms, numberOfOccupiedRooms);
   } // end method toFileString
   
} // end class MyPassengerShip

class MyPerson extends MyThing {
   static int indexNew = 50000;
   static ArrayList <String> words;
  
   String skill = "";
  
   public MyPerson (MySeaPort msp) {
      if (words == null) readNamesFile ();
      name = words.get (rn.nextInt (words.size()));
      index = indexNew++;
      parent = msp.index;
      if (skillNames == null) readSkillsFile ();
      skill = skillNames.get (rn.nextInt (skillNames.size()));
   } // create a random person
   
   void readNamesFile () {
      try {
         words = new ArrayList <> ();
         Scanner sp = new Scanner (new File ("personNames.txt"));
         while (sp.hasNext()) words.add (sp.next());
         System.out.println ("Names File size: " + words.size());  
      } 
      catch (java.io.FileNotFoundException e) {System.out.println ("bad file");}
   } // end readWordsFile

   public String toString () {
      return "Person: " + super.toString() + skill;
   } // end toString

   public String toFileString () {
      String st = "";
      st += String.format ("    person %20s %d %d %s\n", 
             name, index, parent, skill);
      return st;
   } // end method toFileString
   
} // end MyPerson

class MyJob extends MyThing {
   static int indexNew = 60000;
   static ArrayList <String> words;
  
   double duration = 0;
   ArrayList <String> requirements = new ArrayList <> ();
  // eg {"painter", "painter", "painter", "carpenter"};

   public MyJob (MyShip ms) {
      name = String.format ("Job_%d_%d_%d", 
                rn.nextInt(90)+10, rn.nextInt(90)+10, rn.nextInt(90)+10);
      index = indexNew++;
      parent = ms.index;
      duration = rn.nextDouble () * 100 + 20;
      if (skillNames == null) readSkillsFile ();
      for (int i = 0; i < rn.nextInt (5); i++)
         requirements.add (skillNames.get (rn.nextInt (skillNames.size())));
   } // create a random job
   
   public String toFileString () {
      String st = "";
      st += String.format ("    job %20s %d %d %.2f", 
             name, index, parent, duration);
      for (String sr: requirements) st += " " + sr;
      return st + "\n";
   } // end method toFileString
   
} // end MyThing

class MyTime {
   int time = 0; // measured in seconds
   
   public MyTime (int t) {time = t;}
   
   public String toString () {
      return String.format ("%d %d:%d:%d", time/60/60/24, (time/60/60)%24, (time/60)%60, time%60);
   } // end method toString
} // end class MyTime
