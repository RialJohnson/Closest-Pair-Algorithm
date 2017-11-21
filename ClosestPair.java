import java.util.*;
import java.awt.geom.Point2D;
import java.util.Scanner;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;

public class ClosestPair
{



    // instance variables
    public static Point[] points;
    public static float[] values;


    // reads in points from text file
    public static void readIn() {

        int countValues = 0;

        Scanner scanner;

        try {
            scanner = new Scanner(new File("input.txt"));

            scanner.useLocale(Locale.US);

            String input = "";

            while (scanner.hasNext()) {

                input = input + " " + scanner.next();

            }

            input = input.replaceAll(",", " ");

            Scanner scanner2 = new Scanner(input);

            int j = 0;

            while (scanner2.hasNextFloat()) {

                scanner2.nextFloat();
                countValues++;

            }

            Scanner scanner3 = new Scanner(input);

            values = new float[countValues];

            while (scanner3.hasNextFloat()) {

                values[j] = scanner3.nextFloat();
                j++;

            }

            // sets size for Point array
            points = new Point[countValues / 2];


            // creates points and adds them to an array
            int k = 0;

            for (int i = 0; i < countValues; i = i + 2) {
                points[k] = new Point(values[i], values[i + 1]);
                //the print below states what is being entered in from the text file
                //System.out.println(points[k].x + " " + points[k].y);
                k++;
            }


        } catch (FileNotFoundException exception) {

            System.out.print("File not found!");
        }

    }

    public static class Point
    {
		//creating the variables for the points x and y coordinates 
        public final double x;
        public final double y;
		
		//assigning the variables
        public Point(double x, double y)
        {
            this.x = x;
            this.y = y;
        }

		//ability to print out the x and y variables in a string
        public String toString()
        {  return "(" + x + ", " + y + ")";  }
    }

    public static class Pair
    {
		//we will need to make a pair class to compare two points and the calculate distance
        public Point point1 = null;
        public Point point2 = null;
        public double distance = 0.0;

        public Pair(){
		}

        public Pair(Point point1, Point point2){
            this.point1 = point1;
            this.point2 = point2;
            calcDistance();
        }

        public void update(Point point1, Point point2, double distance)
        {
            this.point1 = point1;
            this.point2 = point2;
            this.distance = distance;
        }
		
		//assigning/calculating the distance between the two points
        public void calcDistance()
        {  this.distance = distance(point1, point2);  }

		//ability to print out the x and y variables in a string
        public String toString()
        {  return point1 + "-" + point2 + " : " + distance;  }
    }

    public static double distance(Point p1, Point p2)
    {
		//in order to calculate distances we need both the distances of x and y of each coordinate
        double xdist = p2.x - p1.x;
        double ydist = p2.y - p1.y;

		//need hypotenuse to find the full distance
        double trueDistance = Math.hypot(xdist, ydist);

		//Originally the distance converts it into a very long floating point. We however are only interested in
		//accuracy up to two decimals
        double scale = Math.pow(10, 1);

        double roundedResult = Math.round(trueDistance * scale) / scale;

        return roundedResult;
    }

    public static Pair bruteForce(List<? extends Point> points)
    {
		
		//When we are at the points were we are in the simplest cast, we use the bruteforce method
		//to find the the distance
		
        int numPoints = points.size();
        if (numPoints < 2)
            return null;
        Pair pair = new Pair(points.get(0), points.get(1));
        if (numPoints > 2)
        {
            for (int i = 0; i < numPoints - 1; i++)
            {
                Point point1 = points.get(i);
                for (int j = i + 1; j < numPoints; j++)
                {
                    Point point2 = points.get(j);

                    double distance = distance(point1, point2);
                    if (distance < pair.distance) {
                        pair.update(point1, point2, distance);

                    }
                }
            }
        }
        return pair;
    }

    public static void sortByX(List<? extends Point> points)
    {
		//sorting the lists by the X values
        Collections.sort(points, new Comparator<Point>() {
                    public int compare(Point point1, Point point2)
                    {
                        if (point1.x < point2.x)
                            return -1;
                        if (point1.x > point2.x)
                            return 1;
                        return 0;
                    }
                }
        );
    }

    public static void sortByY(List<? extends Point> points)
    {
		//sorting the lists by the Y values
        Collections.sort(points, new Comparator<Point>() {
                    public int compare(Point point1, Point point2)
                    {
                        if (point1.y < point2.y)
                            return -1;
                        if (point1.y > point2.y)
                            return 1;
                        return 0;
                    }
                }
        );
    }

    public static Pair divideAndConquer(List<? extends Point> points)
    {
		//recursively dividing the lists into sub lists until we have the simplest case
		
        List<Point> pointsSortedByX = new ArrayList<Point>(points);
        sortByX(pointsSortedByX);
        List<Point> pointsSortedByY = new ArrayList<Point>(points);
        sortByY(pointsSortedByY);
        return divideAndConquer(pointsSortedByX, pointsSortedByY);
    }

    private static Pair divideAndConquer(List<? extends Point> pointsSortedByX, List<? extends Point> pointsSortedByY)
    {	
		//recursively dividing the list into sub arrays until we have the simplest case (under 3)
        int numPoints = pointsSortedByX.size();
        if (numPoints <= 3)
            return bruteForce(pointsSortedByX);

        int dividingIndex = numPoints >>> 1;
		
		//divides current list into left and right sub arrays based off of the parent list's center
        List<? extends Point> leftOfCenter = pointsSortedByX.subList(0, dividingIndex);
        List<? extends Point> rightOfCenter = pointsSortedByX.subList(dividingIndex, numPoints);

		//creates temperary list to store the sub list into for both the left and right side
        List<Point> tempList = new ArrayList<Point>(leftOfCenter);
        sortByY(tempList);
        Pair closestPair = divideAndConquer(leftOfCenter, tempList);


        tempList.clear();
        tempList.addAll(rightOfCenter);
        sortByY(tempList);
        Pair closestPairRight = divideAndConquer(rightOfCenter, tempList);


		//if the current closest pair is smaller than the current systems right closest pair, label it as the new
		//system's right closest pair
        if (closestPairRight.distance < closestPair.distance) {
            closestPair = closestPairRight;
        }

		//clear the temp list 
        tempList.clear();
		
		//create a variable to store the shortest distance of our pairs
        double shortestDistance =closestPair.distance;
        double centerX = rightOfCenter.get(0).x;
        for (Point point : pointsSortedByY)
            if (Math.abs(centerX - point.x) < shortestDistance)
                tempList.add(point);

			
			
        for (int i = 0; i < tempList.size() - 1; i++)
        {
            Point point1 = tempList.get(i);
            for (int j = i + 1; j < tempList.size(); j++)
            {
                Point point2 = tempList.get(j);
                if ((point2.y - point1.y) >= shortestDistance) {

                    break;
                }
                double distance = distance(point1, point2);
                if (distance < closestPair.distance)
                {
                    closestPair.update(point1, point2, distance);
                    shortestDistance = distance;

                }
            }
        }
        return closestPair;
    }

    public static void main(String[] args){


        readIn();

        int numPoints = values.length/2;

        List<Point> points = new ArrayList<Point>();


        // creates points and adds them to an array
        int k = 0;

        // the print statements below are to view what points are being added
        //System.out.println("Printing Out Points");
        for (int i = 0; i < values.length; i = i + 2) {


            //System.out.println(points);
            points.add(new Point(values[i], values[i+1]));
            k++;
        }
		
        //when we are down to the simplest case we will use the brute force method
		Pair bruteForceClosestPair = bruteForce(points);
		
		//if we are not at the simplest case we must divide and conquer
        Pair dqClosestPair = divideAndConquer(points);



        System.out.println("Final Result: Closest pair: " + dqClosestPair);

    }
}
