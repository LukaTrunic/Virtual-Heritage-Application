package project;

import com.jogamp.opengl.GL2;

public class Shape {

    public static void cube(GL2 gl2){
        cube(gl2, 1, true);
    } // draw a cube

    public static void cylinder(GL2 gl){
        gl.glPushMatrix();
        gl.glRotated(90, 1, 0, 0);
        cylinder(gl, 0.3, 3, 16, 5, 5, true);
        gl.glPopMatrix();
    }

    public static void base(GL2 gl2){
        base(gl2, 1, true);
    }

    public static void rectangularPyramid(GL2 gl, double width, double depth, double height, boolean makeTextureCoordinate){
        gl.glPushMatrix();

        if (makeTextureCoordinate) {
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }

        // draw the four triangles
        gl.glBegin(GL2.GL_TRIANGLES);

        // front
        gl.glNormal3f(0, 0, 1);
        if (makeTextureCoordinate) gl.glTexCoord2f(0.5f, 1.0f);
        gl.glVertex3d(0.0, height, 0.0);
        if (makeTextureCoordinate) gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3d(-width / 2, 0.0, depth / 2);
        if (makeTextureCoordinate) gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3d(width / 2, 0.0, depth / 2);

        // right
        gl.glNormal3f(0, 0, 1);
        if (makeTextureCoordinate) gl.glTexCoord2f(0.5f, 1.0f);
        gl.glVertex3d(0.0, height, 0.0);
        if (makeTextureCoordinate) gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3d(width / 2, 0.0, depth / 2);
        if (makeTextureCoordinate) gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3d(width / 2, 0.0, -depth / 2);

        // left
        gl.glNormal3f(0, 0, 1);
        if (makeTextureCoordinate) gl.glTexCoord2f(0.5f, 1.0f);
        gl.glVertex3d(0.0, height, 0.0);
        if (makeTextureCoordinate) gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3d(-width / 2, 0.0, -depth / 2);
        if (makeTextureCoordinate) gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3d(-width / 2, 0.0, depth / 2);

        // back
        gl.glNormal3f(0, 0, 1);
        if (makeTextureCoordinate) gl.glTexCoord2f(0.5f, 1.0f);
        gl.glVertex3d(0.0, height, 0.0);
        if (makeTextureCoordinate) gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3d(width / 2, 0.0, -depth / 2);
        if (makeTextureCoordinate) gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3d(-width / 2, 0.0, -depth / 2);

        gl.glEnd();

        // the bottom rectangle
        gl.glBegin(GL2.GL_QUADS);
        if (makeTextureCoordinate) gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3d(-width / 2, 0.0, depth / 2);
        if (makeTextureCoordinate) gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3d(width / 2, 0.0, depth / 2);
        if (makeTextureCoordinate) gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3d(width / 2, 0.0, -depth / 2);
        if (makeTextureCoordinate) gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3d(-width / 2, 0.0, -depth / 2);
        gl.glEnd();

        if (makeTextureCoordinate) {
            gl.glDisable(GL2.GL_TEXTURE_2D);
        }

        gl.glPopMatrix();
    }


//    // build and draw a rectangular pyramid
//    public static void rectangularPyramid(GL2 gl){
//        gl.glPushMatrix();
//
//        // draw the four triangles
//        gl.glBegin(GL2.GL_TRIANGLES);

//        // front
//        gl.glNormal3f(0, 0, 1);
//        gl.glVertex3f(0.0f, 0.5f, 0.0f);
//        gl.glNormal3f(0, 0, -1);
//        gl.glVertex3f(-0.5f, -0.5f, 0.5f);
//        gl.glNormal3f(0, 0, -1);
//        gl.glVertex3f(0.5f, -0.5f, 0.5f);
//
//        // right
//        gl.glNormal3f(0, 0, 1);
//        gl.glVertex3f(0.0f, 0.5f, 0.0f);
//        gl.glNormal3f(0, 0, 1);
//        gl.glVertex3f(0.5f, -0.5f, 0.5f);
//        gl.glNormal3f(0, 0, -1);
//        gl.glVertex3f(0.5f, -0.5f, -0.5f);
//
//        // left
//        gl.glNormal3f(0, 0, 1);
//        gl.glVertex3f(0.0f, 0.5f, 0.0f);
//        gl.glNormal3f(0, 0, 1);
//        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
//        gl.glNormal3f(0, 0, -1);
//        gl.glVertex3f(-0.5f, -0.5f, 0.5f);
//
//        // back
//        gl.glNormal3f(0, 0, 1);
//        gl.glVertex3f(0.0f, 0.5f, 0.0f);
//        gl.glNormal3f(0, 0, 1);
//        gl.glVertex3f(0.5f, -0.5f, -0.5f);
//        gl.glNormal3f(0, 0, -1);
//        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
//
//        gl.glEnd();
//
//        // the bottom square
//        gl.glBegin(GL2.GL_QUADS);
//        gl.glVertex3f(-0.5f, -0.5f, 0.5f);
//        gl.glVertex3f(0.5f, -0.5f, 0.5f);
//        gl.glVertex3f(0.5f, -0.5f, -0.5f);
//        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
//        gl.glEnd();
//        gl.glPopMatrix();
//    }

    static void base(GL2 gl, double side, boolean makeTextureCoordinate){
        gl.glPushMatrix();

        gl.glColor3f(0.8f, 0.8f, 0.8f);

        // draw all the sides of the cuboid
        gl.glPushMatrix();
        gl.glScalef(12f, 0.6f, 8f);
        gl.glRotatef(0, 0, 1, 0);
        gl.glTranslated(0, 0, side/2);
        square(gl, side, makeTextureCoordinate);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(12f, 0.6f, 8f);
        gl.glRotatef(90, 0, 1, 0);
        gl.glTranslated(0, 0, side/2);
        square(gl, side, makeTextureCoordinate);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(12f, 0.6f, 8f);
        gl.glRotatef(180, 0, 1, 0);
        gl.glTranslated(0, 0, side/2);
        square(gl, side, makeTextureCoordinate);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(12f, 0.6f, 8f);
        gl.glRotatef(270, 0, 1, 0);
        gl.glTranslated(0, 0, side/2);
        square(gl, side, makeTextureCoordinate);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(12f, 0.6f, 8f);
        gl.glRotatef(90, 1, 0, 0);
        gl.glTranslated(0, 0, side/2);
        square(gl, side, makeTextureCoordinate);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glScalef(12f, 0.6f, 8f);
        gl.glRotatef(-90, 1, 0, 0);
        gl.glTranslated(0, 0, side/2);
        square(gl, side, makeTextureCoordinate);
        gl.glPopMatrix();

        gl.glPopMatrix();
    }

    // build a cylinder
    private static void cylinder(GL2 gl, double radius, double height, int slices, int stacks, int rings,
                                 boolean makeTextureCoordinates){
        if(radius <= 0) throw new IllegalArgumentException("Radius must be positive");
        if(height <= 0) throw new IllegalArgumentException("Height must be positive");
        if(slices < 3) throw new IllegalArgumentException("Number of slices must be at least 3.");
        if(stacks < 2) throw new IllegalArgumentException("Number of stacks must be at least 2.");

        // body
        for(int j = 0; j < stacks; j++){
            double z1 = (height / stacks) * j;
            double z2 = (height / stacks) * (j+1);
            gl.glBegin(GL2.GL_QUAD_STRIP);
            for(int i = 0; i <= slices; i++){
                double longitude = (2 * Math.PI / slices) * i;
                double sinLongitude = Math.sin(longitude);
                double cosineLongitude = Math.cos(longitude);
                double x = cosineLongitude;
                double y = sinLongitude;
                gl.glNormal3d(x, y, 0);
                if(makeTextureCoordinates){
                    gl.glTexCoord2d(1.0 / slices * i, 1.0 / stacks * (j+1));
                }
                gl.glVertex3d(radius*x, radius*y, z2);
                if(makeTextureCoordinates){
                    gl.glTexCoord2d(1.0 / slices * i, 1.0 / stacks * j);
                }
                gl.glVertex3d(radius * x, radius*y, z1);
            }
            gl.glEnd();
        }

        // draw the top and bottom
        if(rings > 0){
            gl.glNormal3d(0, 0, 1);
            for (int j=0; j<rings; j++){
                double d1 = (1.0 / rings) * j;
                double d2 = (1.0 / rings) * (j+1);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                for (int i = 0; i <= slices; i++) {
                    double angle = (2* Math.PI / slices) * i;
                    double sin = Math.sin(angle);
                    double cosine = Math.cos(angle);
                    if(makeTextureCoordinates){
                        gl.glTexCoord2d(1 * (1 + cosine * d1), 0.5 * (1 + sin * d1));
                    }
                    gl.glVertex3d(radius * cosine * d1, radius * sin * d1, height);

                    if(makeTextureCoordinates){
                        gl.glTexCoord2d(1 * (1 + cosine * d2), 0.5 * (1 + sin * d2));
                    }
                    gl.glVertex3d(radius * cosine * d2, radius * sin * d2, height);
                }
                gl.glEnd();
            }
            gl.glNormal3d(0, 0, -1);

            for (int j=0; j<rings; j++){
                double d1 = (1.0 / rings) * j;
                double d2 = (1.0 / rings) * (j+1);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                for (int i = 0; i <= slices; i++) {  // TODO: ADD  "i<= slices"
                    double angle = (2* Math.PI / slices) * i;
                    double sin = Math.sin(angle);
                    double cosine = Math.cos(angle);
                    if(makeTextureCoordinates){
                        gl.glTexCoord2d(0.5 * (1 + cosine * d2), 0.5 * (1 + sin * d2));
                    }
                    gl.glVertex3d(radius * cosine * d2, radius * sin * d2, 0);

                    if(makeTextureCoordinates){
                        gl.glTexCoord2d(0.5 * (1 + cosine * d1), 0.5 * (1 + sin * d1));
                    }
                    gl.glVertex3d(radius * cosine * d1, radius * sin * d1, 0);
                }
                gl.glEnd();
            }
        }
    }

    // build a cube
    public static void cube(GL2 gl, double side, boolean makeTextureCoordinate){
        // push the current matrix down in the stack
        gl.glPushMatrix();

        gl.glPushMatrix();
        gl.glRotatef(0, 0, 1, 0);
        gl.glTranslated(0, 0, side/2);
        square(gl, side, makeTextureCoordinate);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0);
        gl.glTranslated(0, 0, side/2);
        square(gl, side, makeTextureCoordinate);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glRotatef(180, 0, 1, 0);
        gl.glTranslated(0, 0, side/2);
        square(gl, side, makeTextureCoordinate);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glRotatef(270, 0, 1, 0);
        gl.glTranslated(0, 0, side/2);
        square(gl, side, makeTextureCoordinate);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glRotatef(90, 1, 0, 0);
        gl.glTranslated(0, 0, side/2);
        square(gl, side, makeTextureCoordinate);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glRotatef(-90, 1, 0, 0);
        gl.glTranslated(0, 0, side/2);
        square(gl, side, makeTextureCoordinate);
        gl.glPopMatrix();

        gl.glPopMatrix();
    }

    // draw a square in the (x,y) plane, with given side length
    public static void square(GL2 gl, double side, boolean makeTextureCoordinate) {

        double radius = side / 2;
        gl.glBegin(GL2.GL_POLYGON);

        // vector for lighting calculation
        gl.glNormal3f(0, 0, 1);

        // top left corner of a square
        if(makeTextureCoordinate){
            gl.glTexCoord2d(0, 1);
        }
        gl.glVertex2d(-radius, radius);

        // bottom left corner of a square
        if(makeTextureCoordinate){
            gl.glTexCoord2d(0, 0);
        }
        gl.glVertex2d(-radius, -radius);

        // bottom right corner of a square
        if(makeTextureCoordinate){
            gl.glTexCoord2d(1, 0);
        }
        gl.glVertex2d(radius, -radius);

        // top right corner of a square
        if(makeTextureCoordinate){
            gl.glTexCoord2d(1, 1);
        }
        gl.glVertex2d(radius, radius);

        gl.glEnd();
    }
}
