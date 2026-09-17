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


        // Render Window
        GL11.glClearColor(0.2f, 0.4f, 0.8f, 1.0f);
        GLFW.glfwShowWindow(window);



        // Square - mathematical vals, not pixel vals
        // must use two triangles to form square

        // since the window is longer than it is tall 1 unit of x/y have different vals
        // x 1 unit -> 400 pixels
        // y 1 unit -> 300 pixels
        // must divide larger unit by aspect ratio to ensure units match
        float aspect = 800.0f / 600.0f; // 1.333
        float[] vertices = {
                -0.5f/aspect,  0.5f,   // top-left    0
                0.5f/aspect,  0.5f,   // top-right    1
                -0.5f/aspect, -0.5f,   // bottom-left 2
                0.5f/aspect, -0.5f    // bottom-right 3
        };

        // indicies in order for each triangle
        int[] indices = {
                0, 1, 2,
                1, 3, 2
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
                2,                  // 2 values per vertex
                GL11.GL_FLOAT,      // they're floats
                false,              // don't normalize
                2 * Float.BYTES,    // how many bytes to the next vertex
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
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            // Draw triangle
            GL30.glBindVertexArray(vao);


            // draw element allows you to draw vertices by index while
            // draw array is sequential
            GL11.glDrawElements(
                    GL11.GL_TRIANGLES,
                    6,
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
