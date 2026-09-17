package org.example;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() throws IOException {

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Could not initialize GLFW");
        }


        // Create Window
        long window = GLFW.glfwCreateWindow(
                800,
                600,
                "My First LWJGL Window",
                0,
                0
        );

        if (window == 0) {
            throw new IllegalStateException("Failed to create window");
        }


        // Make this window the current OpenGL context
        GLFW.glfwMakeContextCurrent(window);

        // Initialize OpenGL
        GL.createCapabilities();

        // enables depth testing to prevent primitives at different z vals
        // overwriting each other
        // tells opengl to check z before deciding whether to clear
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        // Render Window
        GL11.glClearColor(0.2f, 0.4f, 0.8f, 1.0f);
        GLFW.glfwShowWindow(window);



        // Squares - mathematical vals, not pixel vals
        // must use two triangles to form the two squares making a cube
        float[] vertices = {
                // Front face
                -0.5f,  0.5f,  0.5f,  // 0      tl
                0.5f,  0.5f,  0.5f,  // 1       tr
                -0.5f, -0.5f,  0.5f,  // 2      bl
                0.5f, -0.5f,  0.5f,  // 3       br

                // Back face
                -0.5f,  0.5f, -0.5f,  // 4      tl
                0.5f,  0.5f, -0.5f,  // 5       tr
                -0.5f, -0.5f, -0.5f,  // 6      bl
                0.5f, -0.5f, -0.5f   // 7       br
        };

        // indicies in order for each triangle
        // GL_triangles will read vals 3 at a time
        // Cube vertex indices (6 faces x 2 triangles x 3 vertices = 36 vertices)
        int[] indices = {
                // Front
                0, 2, 1,
                1, 2, 3,

                // Back
                4, 5, 6,
                5, 7, 6,

                // Left
                4, 6, 0,
                0, 6, 2,

                // Right
                1, 3, 5,
                5, 3, 7,

                // Top
                4, 0, 5,
                5, 0, 1,

                // Bottom
                2, 6, 3,
                3, 6, 7
        };


        // create EBO (element buffer object to store indicies on gpu)
        int ebo = GL15.glGenBuffers();

        // Create a VBO (vertex buffer object)
        // more efficient
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        GL15.glBufferData(
                GL15.GL_ARRAY_BUFFER,
                vertices,
                GL15.GL_STATIC_DRAW
        );

        // create vertex array object
        // lets gpu know every two floats are paired for the vertex
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);


        // bind ebo and upload indices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);

        GL15.glBufferData(
                GL15.GL_ELEMENT_ARRAY_BUFFER,
                indices,
                GL15.GL_STATIC_DRAW
        );

        // Connect VBO to VAO
        GL20.glVertexAttribPointer(
                0,                  // attribute number
                3,                  // 3 values per vertex
                GL11.GL_FLOAT,      // they're floats
                false,              // don't normalize
                3 * Float.BYTES,    // each vertex takes 3 floats worth of space
                0                   // start at the beginning
        );

        GL20.glEnableVertexAttribArray(0);




        // Create vert Shaders

            // create empty vertex shader
        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

            // load contents from file
        String source = Files.readString(
                Path.of("src/main/resources/vertex.glsl")
        );
            // compile
        GL20.glShaderSource(vertexShader, source);
            // verify compilation
        if (GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(vertexShader));
        }

        // Create Fragment shaders
        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        String fragmentSource = Files.readString(
                Path.of("src/main/resources/fragment.glsl")
        );

        GL20.glShaderSource(fragmentShader, fragmentSource);
        GL20.glCompileShader(fragmentShader);

        if (GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(fragmentShader));
        }


        // link shaders to single shader program
        int shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);

                // verify link is set
        if (GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetProgramInfoLog(shaderProgram));
        }

                // Tell opengl to use shader program for rendering
        GL20.glUseProgram(shaderProgram);



        // Create Fragment Shader (decides color of triangle pixels)

        // Game loop
        while (!GLFW.glfwWindowShouldClose(window)) {

            // erase prev fame, display frame just rendered
            // bitwise or operator used to combine calls (saves time when going to the same library)
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            // Draw triangle
            GL30.glBindVertexArray(vao);


            // draw element allows you to draw vertices by index while
            // draw array is sequential
            GL11.glDrawElements(
                    GL11.GL_TRIANGLES,
                    36,
                    GL11.GL_UNSIGNED_INT,
                    0
            );

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();


        }

        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }
}
