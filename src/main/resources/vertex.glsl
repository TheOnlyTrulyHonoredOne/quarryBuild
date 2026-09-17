#version 330 core

// updated to vec3
layout (location = 0) in vec3 aPos;

void main()
{
    // got rid of second argument (0.0) after changing to vec3 (apos contains z now)
    gl_Position = vec4(aPos, 1.0);
}