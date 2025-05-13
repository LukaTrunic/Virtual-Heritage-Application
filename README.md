# 🏛️ Virtual Heritage Assembly – 3D Construction Project

This project is a **Java + JOGL (OpenGL)** application developed for the Computer Graphics course. The application provides users with an interactive experience of constructing 3D heritage showpieces using a set of basic geometric parts — simulating the logic of LEGO-like assembly in a virtual heritage setting.


## 🎯 Project Goal

The main objective is to enable users to recreate a target 3D showpiece by selecting, placing, rotating, and scaling parts from a predefined palette. The goal is to match the target structure's layout, orientation, and color using discrete transformation steps.


## 🧩 Features

- ✅ Palette of 3D parts (cubes, cones, cylinders, etc.) with varied textures and colors
- ✅ Target shape (outline) displayed in wireframe
- ✅ Sample shape displayed as a reference
- ✅ Selection of target parts using keyboard traversal
- ✅ "Add", "Rotate", "Scale", and "Remove" functionality for each part
- ✅ Discrete transformation control (scale by 0.1, rotate by 5° steps)
- ✅ Collision avoidance for deployed parts
- ✅ Visual success feedback when shape is correctly constructed
- ✅ Dynamic light source toggle and camera control (rotate, zoom)


## 🖥 Technologies Used

- **Language**: Java
- **Graphics Library**: JOGL (Java OpenGL)
- **IDE**: IntelliJ IDEA / NetBeans
- **3D Shapes**: Custom primitives (Cube, Cylinder, Cone, Pyramid, Sphere)
- **Input**: Keyboard and mouse interaction


## 🚀 How to Run

### Requirements:
- Java 11+
- JOGL libraries properly configured
- IntelliJ IDEA or NetBeans with OpenGL setup

### Instructions:
1. Download the project folder.
2. Open the project in IntelliJ IDEA or NetBeans.
3. Ensure all JOGL dependencies are available.
4. Run the `Main.java` or the main application class.

### Controls:
- Arrow keys: Navigate target parts
- `A`: Add selected palette part to target
- `R`: Rotate current part
- `S`: Scale current part
- `D`: Delete selected part
- Mouse: Rotate camera
- `L`: Toggle lighting
- `F`: Finish construction and validate

## 📬 Author

Luka Trunić