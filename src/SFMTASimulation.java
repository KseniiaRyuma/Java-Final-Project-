import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


abstract class SFMTAObject
{
	
}


public class SFMTASimulation 
{
	
	private ArrayList<Station> stationList;
	private ArrayList<Route> routeList;
	private ArrayList<Passenger> passengerList;
	private ArrayList<Driver> driverList;
	
	
	public SFMTASimulation()
	{
		stationList = new ArrayList<Station>();
		routeList = new ArrayList<Route>(); 
	}
	
	public String toString()
	{
		String str;
		
		str = "Pring Routes:\n";
		
		// Print all the routes
		for(int i = 0; i < routeList.size(); i++)
		{
			Route r = routeList.get(i);
			str += r.toString() + "\n";
		}
		
		str += "Print Stations:\n";
		
		// Print all the stations
		for(int i = 0; i < stationList.size(); i++)
		{
			Station s = stationList.get(i);
			str += s.toString();
		}

		
		return str;
	}

	
	public void parsePassangers(Scanner file)
	{
		
		// Recursion stop condition
		if(file.hasNextLine() == false)
		{
			return;
		}
				
		// Fetch line
		String line = file.nextLine();
				
				
		// Split the line
		String [] value = line.split(",");
		
		// Change ID type to Integer
		int val1 = Integer.parseInt(value[1]);
		int val2 = Integer.parseInt(value[2]);
		
		// Create new station
		createPassenger(value[0], val1, val2);		
		
		// Recursive call
		parsePassangers(file);
			
	}
	
	public void parseDrivers(Scanner file)
	{
		// Recursion stop condition
		if(file.hasNextLine() == false)
		{
			return;
		}
						
		// Fetch line
		String line = file.nextLine();
										
		// Split the line
		String [] value = line.split(",");
				
		// Change ID type to Integer
		int val1 = Integer.parseInt(value[1]);
				
		// Create new station
		createDriver(value[0], val1);		
				
		// Recursive call
		parseDrivers(file);
	}
	
	public Driver createDriver(String name, int startID)
	{
		// Get the stations corresponding to start and end points
		Station start = getStationById(startID);
		
		// Create a new Passenger object
		Driver driver  = new Driver(name, start);
				
		// Add the object to the list
		passengerList.add(driver);
				
		//Return the created object
		return driver;
		
	}
	
	public void parseStations(Scanner file, Route route)
	{
		
		// Recursion stop condition
		if(file.hasNextLine() == false)
		{
			return;
		}
				
		// Fetch line
		String line = file.nextLine();
				
				
		// Split the line
		String [] value = line.split(",");
		
		// Change ID type to Integer
		int val = Integer.parseInt(value[1]);
		
		
		
		// Create new station
		Station station = createStation(value[0], val);
		
		
		// Push statioan into route's list
		route.addStation(station);
		
		
		// Recursive call
		parseStations(file, route );
			
	}
	
	
	public Station createStation (String stationName, int stationID)
	{
		// Iterate over the list of the existing stations
		for(int i = 0; i < stationList.size(); i++)
		{
			Station st = stationList.get(i);
			
			// Return the station if found
			if(st.getID() == stationID)
			{
				//System.out.println("Double station: " + stationName + "; " + st.getName());
				return st;
			}
		}
		
		// If we reached this point, we need to create a new station
		Station st = new Station(stationName, stationID);
		stationList.add(st);
		return st;
	}
	
	
	public Route createRoute(String n)
	{
		// Create a new Route object
		Route route = new Route(n);
		
		// Add the object to the list
		routeList.add(route);
		
		// Return the created object
		return route;
	}
	
	public Station getStationById(int id)
	{
		// Iterate over the list of stations
		for(int i = 0; i < stationList.size(); i++)
		{
			Station st = stationList.get(i);
			
			// Return station object if there is a match
			if(st.getID() == id)
			{
				return st;
			}
		}
		
		// Case when no match was found
		return null;
	}
	
	public void mapStationToRoutes(Station s, ArrayList<Route> list)
	{
		// Iterate over all the routes
		for(int i = 0; i < routeList.size(); i++)
		{
			// Get the current route
			Route r = routeList.get(i);
			
			// If the station belongs to the route, then add the route
			if(r.isRouteStation(s))
			{
				list.add(r);
			}
		}
	}
	
	public ArrayList<Station> createPassengerRoute(Station start, Station end)
	{
		ArrayList<Station> list = new ArrayList<Station>();
		
		// Create array lists containing routes having start and end points
		ArrayList<Route> routeStart = new ArrayList<Route>();
		ArrayList<Route> routeEnd = new ArrayList<Route>();
		
		// Map stations to routes that have this station
		mapStationToRoutes(start, routeStart);
		mapStationToRoutes(end, routeEnd);
		
		// Check if the stations are located on the same route
		for(int i = 0; i < routeStart.size(); i++)
		{
			Route r = routeStart.get(i);
			
			for(int j = 0; j < routeEnd.size(); j++)
			{
				// If the stations are on the same route, return the sub-route
				if( r == routeEnd.get(j))
				{
					r.buildSubRoute(list, start, end);
					return list;
				}
			}
		}
		
		return list;
	}
	
	
	public Passenger createPassenger(String n, int startP, int finalP)
	{
		// Get the stations corresponding to start and end points
		Station start = getStationById(startP);
		Station end = getStationById(finalP);
		
		ArrayList<Station> list = createPassengerRoute(start, end);
		
		// Create a new Passenger object
		Passenger passenger = new Passenger(n, start, end);
		
		// Add the object to the list
		passengerList.add(passenger);
		
		//Return the created object
		return passenger;
		
		
	}
	
	public static void main(String[] arg) throws IOException
	{
		
		// Create a new simulation object
		SFMTASimulation transitSystem = new SFMTASimulation();
		
		// Define list of route names
		String[] routeName = 
			{ "47VanNess", "49Mission", "8xBayshore", "KIngleside", "LTaraval", "NJudah", "TThird" };
		
		// Parse the route files in loop
		for( int i = 0; i < routeName.length; i++ )
		{
			// Open the file
			File file = new File (routeName[i] + ".csv");
			
			// Create scanner object
			Scanner inputFile = new Scanner(file);
			
			// Skip the first line of the file
			inputFile.nextLine();
			
			// Create a new route object
			Route route = transitSystem.createRoute(routeName[i]);
			
			// Call the method to read info from the text
			transitSystem.parseStations(inputFile, route);

		}
		
		//
		// Parse the transfer stops file
		//
		
		// Open the file
		File file = new File ("TransferStops.csv");
					
		// Create scanner object
		Scanner inputFile = new Scanner(file);
					
		// Skip the first line of the file
		inputFile.nextLine();
					
		//for()
		

		//
		// Parse the passengers
		//
		
		// Open the file with passenger data
		file = new File ("passengers.csv");
		inputFile = new Scanner(file);
		
		
		
		// Parse drivers
		// Open the file with passenger data
		file = new File ("drivers.csv");
		inputFile = new Scanner(file);
		
		// Call the method to read info from the text
		transitSystem.parseDrivers(inputFile);
		
		
		//System.out.print(transitSystem);
		
	}
}

class Route
{
	private String name;
	private ArrayList<Station> stationList;
	
	public Route(String n)
	{
		stationList = new ArrayList<Station>();
		name = n;
	}
	
	public void addStation(Station st)
	{
		stationList.add(st);
	}
	
	public boolean isRouteStation(Station st)
	{
		// Iterate over the list of stations in the route
		for(int i = 0; i < stationList.size(); i++)
		{
			// If match, return true
			if(st.getID() == stationList.get(i).getID())
			{
				return true;
			}
		}
		
		// Return false if the station wasn't found
		return false;
	}
	
	public String toString()
	{
		String str;
		
		str = "Route " + name + ":\n";
		
		// Print the list of stations for this route
		for(int i = 0; i < stationList.size(); i++)
		{
			Station st = stationList.get(i);
			str += i + ". " + st.getName() + "\n";
		}
		
		return str;
	}
	
	public boolean buildSubRoute(ArrayList<Station> list, Station a, Station b)
	{
		// Init to illegal sequential order number
		int seqNumA = -1, seqNumB = -1;
		
		// Map the stations to sequential order number in the stationList
		for(int i = 0; i < stationList.size(); i++)
		{
			Station st = stationList.get(i);
			if(a.getID() == st.getID())
			{
				seqNumA = a.getID();
			}
			
			if(b.getID() == st.getID())
			{
				seqNumB = b.getID();
			}
		}
		
		// Check if one of the stations is not in the list
		if(seqNumA == -1 || seqNumB == -1)
		{
			return false;
		}
		
		// Build the sub-route
		if(seqNumA < seqNumB)
		{
			for(int i = seqNumA; i <= seqNumB; i++)
			{
				Station st = stationList.get(i);
				list.add(st);
			}
		}
		else
		{
			for(int i = seqNumA; i >= seqNumB; i--)
			{
				Station st = stationList.get(i);
				list.add(st);
			}
		}
		
		// Successful case
		return true;
	}
}


class Driver extends Passenger
{
	private Vehicle vehicle;
	
	public Driver(String n, Station startP) 
	{
		super(n, startP);
	}

	public Vehicle startMarshrut()
	{
		Vehicle vehicle = new Vehicle(super.getStartPoint());
		return vehicle;
	}
	
}

class Vehicle 
{
	private int id;
	private Route route;
	
	public Vehicle(Station s)
	{
		
	}
	
	public Vehicle(int num)
	{
		id = num;
	}
	
	public int getId()
	{
		return id;
	}
	
	
}

class TransferStation extends Station
{
	// The id of the station it connects to
	private int transitId;
	
	public TransferStation(String name, int id1, int id2)
	{
		// Create the station
		super(name, id1);
		
		// Save the transit station id
		transitId = id2;
	}
}

class Station
{
	private String name;
	private int iD;
	
	
	public Station(String stationName, int stationId) 
	{
		name = stationName;
		iD = stationId;
	}

	public int getID()
	{
		return iD;
	}

	public String getName()
	{
		return name;
	}
}

class Passenger
{
	private String name;
	private Station start;
	private Station end;
	ArrayList<Station> personalRoute;
	
	/**
	 * Constructor
	 */
	
	public Passenger(String n, Station startP, Station finalP)
	{
		name = n;
		start = startP;
		end = finalP;
		personalRoute = new ArrayList<Station>();
	}
	
	public Passenger(String n, Station startP)
	{
		name = n;
		start = startP;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Station getStartPoint()
	{
		return start;
	}
	
	public Station getEndPoint()
	{
		return end;
	}
	
	
}
