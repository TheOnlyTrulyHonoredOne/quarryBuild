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



        // Triangle - mathematical vals, not pixel vals
        float[] vertices = {
                0.0f,  0.5f,   // top
                -0.5f, -0.5f,   // bottom-left
                0.5f, -0.5f    // bottom-right
        };

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

            GL11.glDrawArrays(
                    GL11.GL_TRIANGLES,
                    0,
                    3
            );

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();


        }

        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }
}
