

import java.util.ArrayList;

import java.util.HashMap;

import java.util.LinkedList;

import java.util.Queue;

import java.util.Random;

import java.util.Stack;



import tester.*;

import javalib.impworld.*;



import java.awt.Color;

import java.awt.color.*;

import javalib.worldimages.*;



// Represents a node in a grid

class Node {

    // logical coordinates

    int x;

    int y;

    Edge top;

    Edge bottom;

    Edge right;

    Edge left;

    

    // Constructs this node with the given x and y coordinates

    Node(int x, int y) {

        this.x = x;

        this.y = y;

        this.top = null;

        this.bottom = null;

        this.left = null;

        this.right = null;

    }

    

    // Creates an edge that connects this node to the given node

    // This node is the top node and that node is the bottom node

    void setBottom(Node bottom) {

        Edge edge = new Edge(this, bottom);

        this.bottom = edge;

        bottom.top = edge;

    }

    // Creates an edge that connects this node to the given node

    // This node is the left node and that node is the right node

    void setRight(Node right) {

        Edge edge = new Edge(this, right);

        this.right = edge;

        right.left = edge;

    }

    // setLeft and setTop are never used and therefore unnecessary

    

    // Checks if two nodes are the same using their x and y coordinates

    boolean sameNode(Node that) {

        return this.x == that.x && this.y == that.y;

    }

}



// Represents an edge connecting two nodes in a grid

class Edge {

    // Represents the node on the left or top of this edge

    Node a;

    // Represents the node on the right or bottom of this edge

    Node b;

    int weight;

    

    // Constructs this edge with two given nodes and sets the weight to -1

    Edge(Node a, Node b) {

        this.a = a;

        this.b = b;

        this.weight = -1;

    }

    

    // Sets the weight of this edge to a given value

    void setWeight(int weight) {

        this.weight = weight;

    }

    

    // Checks if this edge is the same as that edge by checking if their nodes are the same

    boolean sameEdge(Edge that) {

        return this.a.sameNode(that.a) && this.b.sameNode(that.b);

    }

    

    // Displays this edge as a path in a maze

    void displayAsPathOnScene(WorldScene ws, Color color) {

        int ax = this.a.x * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE;

        int ay = this.a.y * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE;

        int bx = this.b.x * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE;

        int by = this.b.y * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE;

    

        Posn pos = new Posn(bx - ax, by - ay);

        WorldImage line = new LineImage(pos, color);

        ws.placeImageXY(line, (ax + bx) / 2, 

                          (ay + by) / 2);

    }

    // Displays this edge as a path in a maze

    void displayAsPathOnScene(WorldScene ws, Color color, int offset) {

        int ax = this.a.x * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE;

        int ay = this.a.y * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE;

        int bx = this.b.x * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE;

        int by = this.b.y * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE;

    

        if (bx - ax == 0) {

            Posn pos = new Posn(0, by - ay);

            WorldImage line = new LineImage(pos, color);

            ws.placeImageXY(line, (ax + bx) / 2 + offset, 

                              (ay + by) / 2 + offset);

        }

        else {

            Posn pos = new Posn(bx - ax, 0);

            WorldImage line = new LineImage(pos, color);

            ws.placeImageXY(line, (ax + bx) / 2 + offset, 

                              (ay + by) / 2 + offset);

        }

    }

    // Displays this edge as a wall in a maze

    void displayAsWallOnScene(WorldScene ws) {

        int ax = this.a.x * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE;

        int ay = this.a.y * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE;

        int bx = this.b.x * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE;

        int by = this.b.y * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE;

        int mx = ((ax + bx) / 2);

        int my = ((ay + by) / 2);

      

        Posn pos = new Posn(by - ay, bx - ax);

        WorldImage line = new LineImage(pos, Color.BLACK);

        ws.placeImageXY(line, mx, my);

    }

}



// Represents the representatives of nodes in a spanning tree

class SpanningTree {

    // Represents which node represents which node

    HashMap<Node, Node> data;

    

    SpanningTree() {

        this.data = new HashMap<Node, Node>();

    }

    

    // Initializes the representations by having each node represent itself

    void initTree(ArrayList<ArrayList<Node>> nodes) {

        for (int x = 0 ; x < Maze.SIZE_X ; x += 1) {

            for (int y = 0 ; y < Maze.SIZE_Y ; y += 1) {

                Node node = nodes.get(x).get(y);

                data.put(node, node);

            }

        }

    }

    // Gets the representation of a given node

    Node getRepresentation(Node node) {

        if (node.sameNode(this.data.get(node))) {

            return node;

        }

        else {

            return this.getRepresentation(this.data.get(node));

        }

    }

    // A given nodes representation to the other given node

    void setRepresentation(Node node, Node rep) {

        this.data.put(node, rep);

    }

    

}



// Represents a runner

// Runners can be user controlled or they can be used in search algorithms

class MazeRunner {

    // Represents the node the runner is currently on

    Node node;

    // Represents all the edges this runner has passed

    ArrayList<Edge> edgesPassed;

    // Represents the color of the runner and its path

    Color color;

    String direction;

    

    // Creates a new runner from a given node and color

    MazeRunner(Node node, Color color) {

        this.node = node;

        this.color = color;

        this.direction = "down";

        this.edgesPassed = new ArrayList<Edge>();

    }

    // Creates a new runner from a given runner

    MazeRunner(MazeRunner that) {

        this.node = that.node;

        this.color = that.color;

        this.direction = that.direction;

        this.edgesPassed = new ArrayList<Edge>(that.edgesPassed);

    }

    

    // Moves this player based on its direction 

    // and returns the edge it passed

    Edge move() {

        if (this.direction.equals("up")) {

            this.edgesPassed.add(this.node.top);

            this.node = this.node.top.a;

            return this.node.bottom;

        }

        else if (this.direction.equals("down")) {

            this.edgesPassed.add(this.node.bottom);

            this.node = this.node.bottom.b;

            return this.node.top;

        }

        else if (this.direction.equals("left")) {

            this.edgesPassed.add(this.node.left);

            this.node = this.node.left.a;

            return this.node.right;

        }

        else if (this.direction.equals("right")) {

            this.edgesPassed.add(this.node.right);

            this.node = this.node.right.b;

            return this.node.left;

        }

        else {

            throw new RuntimeException("runner was given a bad direction");

        }

    }



    // Displays this player on the given world scene

    void displayOnScene(WorldScene ws) {

        WorldImage runnerImage = new CircleImage(Maze.SIZE_IMAGE / 3, OutlineMode.SOLID, this.color);

        ws.placeImageXY(runnerImage, 

                this.node.x * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE, 

                this.node.y * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE);

        for (Edge edge : this.edgesPassed) {

            edge.displayAsPathOnScene(ws, this.color);

        }

    }

    // Displays this player on the given world scene

    void displayOnScene(WorldScene ws, int offset) {

        WorldImage runnerImage = new CircleImage(Maze.SIZE_IMAGE / 3, OutlineMode.SOLID, this.color);

        ws.placeImageXY(runnerImage, 

                this.node.x * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE, 

                this.node.y * Maze.SIZE_IMAGE + Maze.SIZE_IMAGE);

        for (Edge edge : this.edgesPassed) {

            edge.displayAsPathOnScene(ws, this.color, offset);

        }

    }

}



class Maze extends World {



    static final int SIZE_X = 32;

    static final int SIZE_Y = 32;

    static final int SIZE_IMAGE = 20;

    

    // Represents the nodes in the grid of this maze

    ArrayList<ArrayList<Node>> nodes;

    // Represents the edges that are not in the maze's path

    ArrayList<Edge> edges;

    // Represents the maze's path

    ArrayList<Edge> mazePath;

    // Represents the player

    MazeRunner runner;

    // Represents the solution to the maze

    MazeRunner solution;

    // Represents the trackers for a breadth search

    Queue<MazeRunner> bsTrackers;

    // Represents the trackers for a depth search

    Stack<MazeRunner> dsTrackers;

    // Represents where the trackers in a breadth search have been

    ArrayList<Edge> bsTrackerPath;

    // Represents where the trackers in a depth search have been

    ArrayList<Edge> dsTrackerPath;

    // Toggle for showing the maze path

    boolean showMazePath;

    // Toggle for showing the walls

    boolean showWalls;

    // Toggle for showing and moving the player

    boolean showRunner;

    // Executing a breadth search?

    boolean breadthSearching;

    // Executing a depth search?

    boolean depthSearching;

    

    Maze() {

        this.nodes = new ArrayList<ArrayList<Node>>();

        this.edges = new ArrayList<Edge>();

        this.mazePath = new ArrayList<Edge>();

        this.bsTrackers = new LinkedList<MazeRunner>();

        this.dsTrackers = new Stack<MazeRunner>();

        this.bsTrackerPath = new ArrayList<Edge>();

        this.dsTrackerPath = new ArrayList<Edge>();

        this.showMazePath = false;

        this.showWalls = true;

        this.showRunner = false;

        this.breadthSearching = false;

        this.depthSearching = false;

        this.solution = null;

    }

    

    // Draws the scene

    public WorldScene makeScene() {

        // Creates an empty scene

        WorldScene ws = new WorldScene(SIZE_X * SIZE_IMAGE + 2 * SIZE_IMAGE, 

                                       SIZE_Y * SIZE_IMAGE + 2 * SIZE_IMAGE);

        

        // Marks the start point (top left corner) and end point (bottom right corner)

        WorldImage start = new RectangleImage(SIZE_IMAGE, SIZE_IMAGE, OutlineMode.SOLID, Color.GREEN);

        ws.placeImageXY(start, SIZE_IMAGE, SIZE_IMAGE);

        WorldImage end = new RectangleImage(SIZE_IMAGE, SIZE_IMAGE, OutlineMode.SOLID, Color.RED);

        ws.placeImageXY(end, SIZE_IMAGE * (SIZE_X - 1) + SIZE_IMAGE, 

                               SIZE_IMAGE * (SIZE_Y - 1) + SIZE_IMAGE);

        

        // Displays maze path when enabled

        if (this.showMazePath) {

            for (Edge edge : this.mazePath) {

                edge.displayAsPathOnScene(ws, Color.RED);

            }

        }

        

        // Displays walls when enabled

        if (this.showWalls) {

            for (Edge edge : this.edges) {

                edge.displayAsWallOnScene(ws);

            }

            // draws a frame around entire maze

            WorldImage frame = new RectangleImage(SIZE_IMAGE * SIZE_X, 

                                                  SIZE_IMAGE * SIZE_Y, 

                                                  OutlineMode.OUTLINE, Color.BLACK);

            

            ws.placeImageXY(frame, ((SIZE_IMAGE * SIZE_X + SIZE_IMAGE) / 2), 

                                   ((SIZE_IMAGE * SIZE_Y + SIZE_IMAGE) / 2));

        }

        // Displays tracker path when enabled

        

        for (Edge edge : this.bsTrackerPath) {

            edge.displayAsPathOnScene(ws, Color.ORANGE, SIZE_IMAGE / 4);

        }

        for (Edge edge : this.dsTrackerPath) {

            edge.displayAsPathOnScene(ws, Color.CYAN, -SIZE_IMAGE / 4);

        }

        

        // Draws all trackers for breadth search

        for (MazeRunner tracker : this.bsTrackers) {

            tracker.displayOnScene(ws, SIZE_IMAGE / 4);

        }

        // Draws all trackers for depth search

        for (MazeRunner tracker : this.dsTrackers) {

            tracker.displayOnScene(ws, -SIZE_IMAGE / 4);

        }

        // Draws the solution given by a search

        if (solution != null) {

            solution.displayOnScene(ws);

        }

        // Draws the user's runner

        if (this.showRunner) {

            this.runner.displayOnScene(ws);

        }

        

        return ws;

    }

    // Executes every tick

    // Used to run a search step by step

    public void onTick() {

      if (breadthSearching) {

          this.breadthSearch();

      }

      if (depthSearching) {

         this.depthSearch();

      }

    }

    // World ends if the player has reached the end of the maze

    public WorldEnd worldEnds() {

        if (this.runner.node.sameNode(this.nodes.get(SIZE_X - 1).get(SIZE_Y - 1))) {

            String text = "You WON!";

            // If the solution is displayed when the player wins, he's a dirty, lying, no-good cheater

            if (solution != null) {

                text = text + "... by cheating";

            }

            WorldImage endGameText = new TextImage(text, (SIZE_X * SIZE_IMAGE) / 15,

                    Color.GREEN);

            WorldScene ws = new WorldScene(SIZE_X * SIZE_IMAGE, SIZE_Y * SIZE_IMAGE);

            ws.placeImageXY(endGameText, SIZE_X * SIZE_IMAGE / 2, SIZE_Y * SIZE_IMAGE / 2);

            return new WorldEnd(true, ws);

        }

        else {

            return new WorldEnd(false, makeScene());

        }

    }

    // Key events

    public void onKeyEvent(String ke) {

        // If the user presses the 1 key, a new random maze is generated

        if (ke.equals("1")) {

            this.newRandomMaze();

        }

        // If the user presses the 2 key, a new horizontal maze is generated

        if (ke.equals("2")) {

            this.newHorizontalMaze();

        }

        // If the user presses the . key, toggle displaying the walls

        if (ke.equals(".")) {

            this.showWalls = !this.showWalls;

        }

        // If the user presses the , key, toggle displaying the maze path

        if (ke.equals(",")) {

            this.showMazePath = !this.showMazePath;

        }

        // If the user presses the up key and the player is on, move the player up

        if (ke.equals("up") && showRunner) {

            if (this.runner.node.top != null && this.inPath(this.runner.node.top, this.mazePath)) {

                this.runner.direction = ke;

                this.runner.move();

            }

        }

        // If the user presses the down key and the player is on, move the player down

        if (ke.equals("down") && showRunner) {

            if (this.runner.node.bottom != null && this.inPath(this.runner.node.bottom, this.mazePath)) {

                this.runner.direction = ke;

                this.runner.move();

            }

        }

        // If the user presses the left key and the player is on, move the player left

        if (ke.equals("left") && showRunner) {

            if (this.runner.node.left != null && this.inPath(this.runner.node.left, this.mazePath)) {

                this.runner.direction = ke;

                this.runner.move();

            }

        }

        // If the user presses the right key and the player is on, move the player right

        if (ke.equals("right") && showRunner) {

            if (this.runner.node.right != null && this.inPath(this.runner.node.right, this.mazePath)) {

                this.runner.direction = ke;

                this.runner.move();

            }

        }

        // If the user presses the q key, show the runner and let the user move it

        if (ke.equals("q")) {

            this.showRunner = !this.showRunner;

        }

        // If the user presses the [ key, start a breadth search

        if (ke.equals("[")) {

            this.bsTrackerPath.clear();

            this.initBreadthSearch();

        }

        // If the user presses the ] key, start a depth search

        if (ke.equals("]")) {

            this.dsTrackerPath.clear();

            this.initDepthSearch();

        }

        // If the user presses the - key, stop the breadth search

        if (ke.equals("-")) {

            this.breadthSearching = false;

            this.bsTrackerPath.clear();

            this.bsTrackers.clear();

        }

        // If the user presses the = key, stop the depth search

        if (ke.equals("=")) {

            this.depthSearching = false;

            this.dsTrackerPath.clear();

            this.dsTrackers.clear();

        }

        if (ke.equals("\\")) {

            this.solution = null;

        }

    }

    // Generates a new randomly generated maze

    // Uses same nodes from previous maze, so initNodes() must 

    // be called at least once before calling this method

    void newRandomMaze() {

        this.initEdges();

        this.randomWeights();

        this.sortEdges();

        this.solution = null;

        this.bsTrackers.clear();

        this.dsTrackers.clear();

        this.bsTrackerPath.clear();

        this.dsTrackerPath.clear();

        this.breadthSearching = false;

        this.depthSearching = false;

        this.mazePath = this.spanningTree();

        this.initRunner();

    }

    // Generates a new horizontally generated maze

    // Uses same nodes from previous maze, so initNodes() must 

    // be called at least once before calling this method

    void newHorizontalMaze() {

        this.initEdges();

        this.horizontalWeights();

        this.sortEdges();

        this.solution = null;

        this.bsTrackers.clear();

        this.dsTrackers.clear();

        this.bsTrackerPath.clear();

        this.dsTrackerPath.clear();

        this.breadthSearching = false;

        this.depthSearching = false;

        this.mazePath = this.spanningTree();

        this.initRunner();

    }

    // Initializes all nodes in the grid of this maze

    void initNodes() {

        this.nodes.clear();

        for (int x = 0 ; x < SIZE_X ; x += 1) {

            this.nodes.add(new ArrayList<Node>());

            for (int y = 0 ; y < SIZE_Y ; y += 1) {

                Node node = new Node(x, y);

                this.nodes.get(x).add(node);

            }

        }

    }

    // Initializes all edges between nodes in the grid of this maze

    // Edges are applied to the list of edges that are not in the maze's

    // path because the maze's path has not been calculated yet

    void initEdges() {

        this.edges.clear();

        for (int x = 0 ; x < SIZE_X ; x += 1) {

            for (int y = 0 ; y < SIZE_Y ; y += 1) {

                Node curr = this.nodes.get(x).get(y);

                

                if (x < SIZE_X - 1) {

                    Node right = this.nodes.get(x + 1).get(y);

                    curr.setRight(right);

                    this.edges.add(curr.right);

                }

                if (y < SIZE_Y - 1) {

                    Node bottom = this.nodes.get(x).get(y + 1);

                    curr.setBottom(bottom);

                    this.edges.add(curr.bottom);

                }

            }

        }

    }

    // Initializes the player

    void initRunner() {

        this.runner = new MazeRunner(this.nodes.get(0).get(0), Color.BLUE);

    }

    // Gives the edges in this maze random weights

    void randomWeights() {

        Random random = new Random();

        for (Edge edge : this.edges) {

            if (edge.b.x == SIZE_X - 1 && edge.b.y == SIZE_Y - 1) {

                edge.setWeight(random.nextInt(10) + 100);

            }

            else {

                edge.setWeight(random.nextInt(100));

            }

        }

    }

    // Gives the edges in this maze weights 

    // to make the maze have horizontal paths

    void horizontalWeights() {

        for (Edge edge : this.edges) {

            if (edge.a.y == edge.b.y) {

                edge.setWeight(0);

            }

            else {

                edge.setWeight(SIZE_Y + edge.a.y + 1);

            }

        }

    }

    // Separates edges into edges that are part of the maze's path

    // and those that are not

    ArrayList<Edge> spanningTree() {

        this.mazePath.clear();

        SpanningTree tree = new SpanningTree();

        tree.initTree(this.nodes);

        ArrayList<Edge> treeEdges = new ArrayList<Edge>();

        int index = 0;

        while (index < this.edges.size()) {

            Edge edge = this.edges.get(index);

            Node a = edge.a;

            Node b = edge.b;

            

            if (!tree.getRepresentation(a).sameNode(tree.getRepresentation(b))) {

                this.edges.remove(index);

                treeEdges.add(edge);

                tree.setRepresentation(tree.getRepresentation(a), tree.getRepresentation(b));

            }

            else {

                index += 1;

            }

        }

        return treeEdges;

    }

    // Sorts the edges in this maze by weight via quicksort

    void sortEdges() {

        ArrayList<Edge> temp = new ArrayList<Edge>(this.edges);

        quicksort(temp, 0, this.edges.size());

    }

    void quicksort(ArrayList<Edge> temp, int loIdx, int hiIdx) {

        // Step 0: check for completion

        if (loIdx >= hiIdx) {

            return; // There are no items to sort

        }

        // Step 1: select pivot

        Edge pivot = this.edges.get(loIdx);

        // Step 2: partition items to lower or upper portions of the temp list

        int pivotIdx = partitionCopying(temp, loIdx, hiIdx, pivot);

        // Step 4: sort both halves of the list

        quicksort(temp, loIdx, pivotIdx);

        quicksort(temp, pivotIdx + 1, hiIdx);

    }

    int partitionCopying(ArrayList<Edge> temp, int loIdx, int hiIdx, Edge pivot) {

        int curLo = loIdx;

        int curHi = hiIdx - 1;

        // Notice we skip the loIdx index, because that's where the pivot was

        for (int i = loIdx + 1; i < hiIdx; i = i + 1) {

            if (this.edges.get(i).weight <= pivot.weight) { // lower

                temp.set(curLo, this.edges.get(i));

                curLo = curLo + 1; // advance the current lower index

            }

            else { // upper

                temp.set(curHi, this.edges.get(i));

                curHi = curHi - 1; // advance the current upper index

            }

        }

        temp.set(curLo, pivot); // place the pivot in the remaining spot

        // Step 3: copy all items back into the source

        for (int i = loIdx; i < hiIdx; i = i + 1) {

            this.edges.set(i, temp.get(i));

        }

        return curLo;

    }

    // Checks if a given edge is in the given list of edges

    boolean inPath(Edge e, ArrayList<Edge> edges) {

        // If the edge is null, it's not in the path

        if (e == null) {

            return false;

        }

        // compares the given edge to each edge in the maze path

        for (Edge edge : edges) {

            if (edge.sameEdge(e)) {

                return true;

            }

        }

        return false;

    }



    // Initializes a breadth search for this maze

    void initBreadthSearch() {

        this.breadthSearching = true;

        this.bsTrackers.clear();

        // Top left node in this maze

        Node initialNode = this.nodes.get(0).get(0);

        // Checks if down is a valid move

        if (this.inPath(initialNode.bottom, this.mazePath)) {

            // Creates a tracker moving down in the top left corner 

            // and adds it to the list of breadth search trackers

            MazeRunner t = new MazeRunner(initialNode, Color.ORANGE);

            t.direction = "down";

            this.bsTrackers.add(t);

        }

        // Checks if right is a valid move

        if (this.inPath(initialNode.right, this.mazePath)) {

            // Creates a tracker moving right in the top left corner 

            // and adds it to the list of breadth search trackers

            MazeRunner t = new MazeRunner(initialNode, Color.ORANGE);

            t.direction = "right";

            this.bsTrackers.add(t);

        }

    }

    // Takes another step in the breadth search

    void breadthSearch() {

        // Takes the first tracker from the list of breadth search trackers

        MazeRunner tracker = this.bsTrackers.remove();

        // Moves the tracker

        Edge e = tracker.move();

        // Adds the edge it passed to the tracker path 

        // if it is not already in the tracker path

        if (!this.inPath(e, this.bsTrackerPath)) {

            this.bsTrackerPath.add(e);

        }

        // Checks if the tracker reached the end

        if (tracker.node.sameNode(this.nodes.get(SIZE_X - 1).get(SIZE_Y - 1))) {

            // Stops breadth searching, highlights the tracker, and sets it as the solution

            this.breadthSearching = false;

            tracker.color = Color.BLUE;

            this.solution = tracker;

            return;

        }

        // Adds a new tracker to the list of trackers from the current tracker moving up if 

        // that is a valid move and the current tracker did not just come from that direction

        if (!tracker.direction.equals("down") && this.inPath(tracker.node.top, this.mazePath)) {

            MazeRunner t = new MazeRunner(tracker);

            t.direction = "up";

            this.bsTrackers.add(t);

        }

        // Adds a new tracker to the list of trackers from the current tracker moving left if 

        // that is a valid move and the current tracker did not just come from that direction

        if (!tracker.direction.equals("right") && this.inPath(tracker.node.left, this.mazePath)) {

            MazeRunner t = new MazeRunner(tracker);

            t.direction = "left";

            this.bsTrackers.add(t);

        }

        // Adds a new tracker to the list of trackers from the current tracker moving right if 

        // that is a valid move and the current tracker did not just come from that direction

        if (!tracker.direction.equals("left") && this.inPath(tracker.node.right, this.mazePath)) {

            MazeRunner t = new MazeRunner(tracker);

            t.direction = "right";

            this.bsTrackers.add(t);

        }

        // Adds a new tracker to the list of trackers from the current tracker moving down if 

        // that is a valid move and the current tracker did not just come from that direction

        if (!tracker.direction.equals("up") && this.inPath(tracker.node.bottom, this.mazePath)) {

            MazeRunner t = new MazeRunner(tracker);

            t.direction = "down";

            this.bsTrackers.add(t);

        }

    }

    // Initializes a depth search for this maze

    void initDepthSearch() {

        this.depthSearching = true;

        this.dsTrackers.clear();

        // Top left node in this maze

        Node initialNode = this.nodes.get(0).get(0);

        // Checks if down is a valid move

        if (this.inPath(initialNode.bottom, this.mazePath)) {

            // Creates a tracker moving down in the top left corner 

            // and adds it to the list of depth search trackers

            MazeRunner t = new MazeRunner(initialNode, Color.CYAN);

            t.direction = "down";

            this.dsTrackers.add(t);

        }

        // Checks if right is a valid move

        if (this.inPath(initialNode.right, this.mazePath)) {

            // Creates a tracker moving right in the top left corner 

            // and adds it to the list of breadth search trackers

            MazeRunner t = new MazeRunner(initialNode, Color.CYAN);

            t.direction = "right";

            this.dsTrackers.add(t);

        }

    }

    // Takes another step in the depth search

    void depthSearch() {

        // Takes the first tracker from the list of depth search trackers

        MazeRunner tracker = this.dsTrackers.pop();

        // Moves the tracker

        Edge e = tracker.move();

        // Adds the edge it passed to the tracker path 

        // if it is not already in the tracker path

        if (!this.inPath(e, this.dsTrackerPath)) {

            this.dsTrackerPath.add(e);

        }

        // Checks if the tracker reached the end

        if (tracker.node.sameNode(this.nodes.get(SIZE_X - 1).get(SIZE_Y - 1))) {

            // Stops depth searching, highlights the tracker, and sets it as the solution

            this.depthSearching = false;

            tracker.color = Color.BLUE;

            this.solution = tracker;

            return;

        }

        // Adds a new tracker to the list of trackers from the current tracker moving up if 

        // that is a valid move and the current tracker did not just come from that direction

        if (!tracker.direction.equals("down") && this.inPath(tracker.node.top, this.mazePath)) {

            MazeRunner t = new MazeRunner(tracker);

            t.direction = "up";

            this.dsTrackers.add(t);

        }

        // Adds a new tracker to the list of trackers from the current tracker moving left if 

        // that is a valid move and the current tracker did not just come from that direction

        if (!tracker.direction.equals("right") && this.inPath(tracker.node.left, this.mazePath)) {

            MazeRunner t = new MazeRunner(tracker);

            t.direction = "left";

            this.dsTrackers.add(t);

        }

        // Adds a new tracker to the list of trackers from the current tracker moving right if 

        // that is a valid move and the current tracker did not just come from that direction

        if (!tracker.direction.equals("left") && this.inPath(tracker.node.right, this.mazePath)) {

            MazeRunner t = new MazeRunner(tracker);

            t.direction = "right";

            this.dsTrackers.add(t);

        }

        // Adds a new tracker to the list of trackers from the current tracker moving down if 

        // that is a valid move and the current tracker did not just come from that direction

        if (!tracker.direction.equals("up") && this.inPath(tracker.node.bottom, this.mazePath)) {

            MazeRunner t = new MazeRunner(tracker);

            t.direction = "down";

            this.dsTrackers.add(t);

        }

    }

}



class ExamplesMaze {

    

    // Method testing

    void testMazeFunctions(Tester t) {

        Maze testMaze = new Maze();

        testMaze.initNodes();

        t.checkExpect(testMaze.nodes.size(), Maze.SIZE_X);

        t.checkExpect(testMaze.nodes.get(0).size(), Maze.SIZE_Y);

        testMaze.initEdges();

        t.checkExpect(testMaze.edges.size(), 

                     (2 * Maze.SIZE_X * Maze.SIZE_Y) - Maze.SIZE_X - Maze.SIZE_Y);

        testMaze.randomWeights();

        t.checkNumRange(testMaze.edges.get(0).weight, 0, 110);

        t.checkNumRange(testMaze.edges.get((testMaze.edges.size() - 1) / 2).weight, 0, 110);

        t.checkNumRange(testMaze.edges.get(testMaze.edges.size() - 1).weight, 0, 110);

        testMaze.sortEdges();

        t.checkExpect(testMaze.edges.size(), 

                     (2 * Maze.SIZE_X * Maze.SIZE_Y) - Maze.SIZE_X - Maze.SIZE_Y);

        testMaze.mazePath = testMaze.spanningTree();

        t.checkExpect(testMaze.edges.size() + testMaze.mazePath.size(), 

                     (2 * Maze.SIZE_X * Maze.SIZE_Y) - Maze.SIZE_X - Maze.SIZE_Y);

    }

    

    // Running the maze

    void testMaze(Tester t) {

        Maze maze = new Maze();

        maze.initNodes();

        maze.initEdges();

        maze.randomWeights();

        maze.sortEdges();

        maze.mazePath = maze.spanningTree();

        maze.initRunner();

        maze.bigBang(Maze.SIZE_X * Maze.SIZE_IMAGE + 2 * Maze.SIZE_IMAGE, 

                Maze.SIZE_Y * Maze.SIZE_IMAGE + 2 * Maze.SIZE_IMAGE, .1);

    }

}

