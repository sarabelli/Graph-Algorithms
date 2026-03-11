# Graph Visualizer

A Java Swing desktop application for visualizing graphs through an interactive adjacency matrix.

## Features

- **Adjacency matrix editor** — enter edge weights directly into a table to build the graph in real time
- **Directed / Undirected** — switch between graph types; the matrix mirrors symmetrically for undirected graphs
- **Weighted / Unweighted** — supports arbitrary integer weights or simple 0/1 connectivity
- **Smooth rendering** — nodes and curved edges are drawn with anti-aliasing and drop shadows
- **Pan & Zoom** — navigate the canvas by dragging and scrolling the mouse wheel

<img width="1905" height="908" alt="Immagine 2026-03-11 162633" src="https://github.com/user-attachments/assets/4a179837-7c8c-4983-9837-5cb729f78d5e" />

## Project Structure

| File | Description |
|---|---|
| `Node.java` | Represents a graph node with position and drawing logic |
| `Edge.java` | Represents an edge (directed or undirected) with optional weight and curved rendering |
| `Graph.java` | Stores the list of nodes and edges; handles edge insertion rules |
| `GraphPanel.java` | Swing panel that renders the graph with pan and zoom support |
| `MainWindow.java` | Main window with toolbar, adjacency matrix table and event handling |

## How to Run

1. Compile all `.java` files inside the `graph_algorithms` package
2. Run `MainWindow`

```bash
javac graph_algorithms/*.java
java graph_algorithms.MainWindow
```

> Requires **Java 8** or later.

## Usage

1. Type the number of nodes (1–20) in the **Nodes** field and click **Create matrix**
2. Select **Directed** or **Undirected** and **Weighted** or **Unweighted**
3. Fill in the adjacency matrix — the graph updates automatically
4. Use the **mouse wheel** to zoom and **drag** to pan the canvas
5. Click **Reset** to start over

## Author

Sara Belli
