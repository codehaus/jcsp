    //////////////////////////////////////////////////////////////////////
    //                                                                  //
    //  JCSP ("CSP for Java") Libraries                                 //
    //  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
    //                2001-2004 Quickstone Technologies Limited.        //
    //                                                                  //
    //  This library is free software; you can redistribute it and/or   //
    //  modify it under the terms of the GNU Lesser General Public      //
    //  License as published by the Free Software Foundation; either    //
    //  version 2.1 of the License, or (at your option) any later       //
    //  version.                                                        //
    //                                                                  //
    //  This library is distributed in the hope that it will be         //
    //  useful, but WITHOUT ANY WARRANTY; without even the implied      //
    //  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
    //  PURPOSE. See the GNU Lesser General Public License for more     //
    //  details.                                                        //
    //                                                                  //
    //  You should have received a copy of the GNU Lesser General       //
    //  Public License along with this library; if not, write to the    //
    //  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
    //  Boston, MA 02111-1307, USA.                                     //
    //                                                                  //
    //  Author contact: P.H.Welch@kent.ac.uk                             //
    //                                                                  //
    //                                                                  //
    //////////////////////////////////////////////////////////////////////


import java.io.*;

/**
 * @author Quickstone Technologies Limited
 */
final class ImageDef implements Serializable {

    public static final int COLOR_TRANSPARENT = -1, COLOR_MIRRORED = -2;

    public double[] sphere_x, sphere_y, sphere_z, sphere_r;
    public int[] sphere_c;

    public double light_x, light_y, light_z;

    public double floor_y;

    public double camera_x, camera_y, camera_z; // rays start from here
    public double view_window_x1, view_window_x2, view_window_x3, view_window_x4,
    				view_window_y1, view_window_y2, view_window_y3, view_window_y4,
    				view_window_z1, view_window_z2, view_window_z3, view_window_z4; // and sample points through here

    public static final int maxIterations = 10;
    public static final int AMBIENT = 0x808080;
    public static final int MINLIGHT = 0x80;
    public static final int LIGHTSOURCE = 0x01FFFFFF;
    
    public final int trace(
        final double start_x,
        final double start_y,
        final double start_z,
        final double vector_x,
        final double vector_y,
        final double vector_z,
        final int tolight,
        final int bounce) {
        final double vector_mag = vector_x * vector_x + vector_y * vector_y + vector_z * vector_z;
        // Check the spheres
        do {
            //pp702-703 Foley, van Dam, Feigner, Hughes
            double t_best = 3.4e38f;
            int id_best = -1;
            for (int i = 0; i < sphere_x.length; i++) {
                // fast clipping didn't help with just 6 spheres
                final double xd = start_x - sphere_x[i], yd = start_y - sphere_y[i], zd = start_z - sphere_z[i];
                // a component is vector_mag
                final double b = 2 * (vector_x * xd + vector_y * yd + vector_z * zd);
                final double c = (xd * xd) + (yd * yd) + (zd * zd) - (sphere_r[i] * sphere_r[i]);
                // quadratic components of $15.17
                final double rp2 = (b * b) - 4 * vector_mag * c;
                if (rp2 < 0)
                    continue; // no real roots so ray doesn't touch sphere
                final double rp = Math.sqrt(rp2);
                final double t2 = (-b - rp) / (2 * vector_mag), t1 = (-b + rp) / (2 * vector_mag);
                double t = ((t1 < t2) && (t1 > 0)) ? t1 : t2;
                // closest solution to $15.17
                if (t < 0)
                    continue; // target is behind the viewer
                if (t < t_best) {
                    id_best = i;
                    t_best = t;
                }
            }
            if (id_best < 0)
                break; // no hit
            double x = start_x + vector_x * t_best, y = start_y + vector_y * t_best, z = start_z + vector_z * t_best;
            // (x, y, z) point of intersection of ray with sphere
            double nv_x = (x - sphere_x[id_best]) / sphere_r[id_best],
                nv_y = (y - sphere_y[id_best]) / sphere_r[id_best],
                nv_z = (z - sphere_z[id_best]) / sphere_r[id_best];
            // vector normal to point of intersection
            switch (sphere_c[id_best]) {
                case ImageDef.COLOR_TRANSPARENT :
                    {
                        if (bounce >= maxIterations)
                            return AMBIENT;
                        double vl = -Math.sqrt(vector_mag);
                        double vx = vector_x / vl, vy = vector_y / vl, vz = vector_z / vl;
                        // normalized viewing vector
                        double cosT = vx * nv_x + vy * nv_y + vz * nv_z;
                        double irefr; // refractive index
                        if (cosT > 0) {
                            // entering the object
                            irefr = 1.3f;
                        } else {
                            // leaving the object
                            cosT = -cosT;
                            irefr = 0.769f;
                            nv_x = -nv_x;
                            nv_y = -nv_y;
                            nv_z = -nv_z;
                        }
                        double theta = Math.acos(cosT);
                        double alpha = Math.sin(theta) * irefr;
                        int illLight, illRed, illGreen, illBlue, illRefl;
                        double rx = nv_x * 2 * cosT - vx, ry = nv_y * 2 * cosT - vy, rz = nv_z * 2 * cosT - vz;
                        illRefl = trace(x, y, z, rx, ry, rz, AMBIENT, bounce + 1);
                        if (alpha < 1) {
                            // not total internal reflection
                            alpha = Math.cos(theta + 3.14 - Math.asin(alpha));
                            // get the refracted light from behind the object
                            vx = nv_x * alpha - vx;
                            vy = nv_y * alpha - vy;
                            vz = nv_z * alpha - vz;
                            int illRefr = trace(x + vx, y + vy, z + vz, vx, vy, vz, AMBIENT, bounce + 1);
                            // compose refraction and reflection components
                            illRed =
                                (int) (((illRefr >> 16) & 0xFF) * cosT)
                                    + (int) (((illRefl >> 16) & 0xFF) * (1 - cosT));
                            illGreen =
                                (int) (((illRefr >> 8) & 0xFF) * cosT)
                                    + (int) (((illRefl >> 8) & 0xFF) * (1 - cosT));
                            illBlue = (int) ((illRefr & 0xFF) * cosT) + (int) ((illRefl & 0xFF) * (1 - cosT));
                        } else {
                            // total internal reflection
                            illRed = ((illRefl >> 16) & 0xFF);
                            illGreen = ((illRefl >> 8) & 0xFF);
                            illBlue = (illRefl & 0xFF);
                        }
                        // calculate the vector to a light source
                        double lv_x = light_x - x,
                            lv_y = light_y - y,
                            lv_z = light_z - z,
                            lv = Math.sqrt(lv_x * lv_x + lv_y * lv_y + lv_z * lv_z);
                        // vector to the light from p.of.I
                        lv_x /= lv;
                        lv_y /= lv;
                        lv_z /= lv;
                        cosT = nv_x * lv_x + nv_y * lv_y + nv_z * lv_z;
                        if (cosT < 0) {
                            // light is behind the object (but this is transparent so ok)
                            cosT = -cosT;
                        }
                        illLight = trace(x + lv_x, y + lv_y, z + lv_z, lv_x, lv_y, lv_z, LIGHTSOURCE, bounce + 1);
                        // determine the resulting color
                        return (
                            (((int) (((illLight >> 16) & 0xFF) * cosT) + (int) (illRed * (1 - cosT))) << 16)
                                & 0xFF0000)
                            | ((((int) (((illLight >> 8) & 0xFF) * cosT) + (int) (illGreen * (1 - cosT))) << 8)
                                & 0xFF00)
                            | (((int) ((illLight & 0xFF) * cosT) + (int) (illBlue * (1 - cosT))) & 0xFF);
                    }
                case ImageDef.COLOR_MIRRORED :
                    {
                        if (bounce > maxIterations)
                            return AMBIENT;
                        // pp280 Hearn & Baker
                        double vl = -Math.sqrt(vector_mag);
                        double vx = vector_x / vl, vy = vector_y / vl, vz = vector_z / vl;
                        // normalized viewing vector
                        double NL = 2 * (nv_x * vx + nv_y * vy + nv_z * vz);
                        // $14.8
                        vx = nv_x * NL - vx;
                        vy = nv_y * NL - vy;
                        vz = nv_z * NL - vz;
                        // (vx, vy, vz) is the reflected vector
                        // calculate the vector to a light source
                        double lv_x = light_x - x,
                            lv_y = light_y - y,
                            lv_z = light_z - z,
                            lv = Math.sqrt(lv_x * lv_x + lv_y * lv_y + lv_z * lv_z);
                        // vector to the light from p.of.I
                        lv_x /= lv;
                        lv_y /= lv;
                        lv_z /= lv;
                        double cosT = nv_x * lv_x + nv_y * lv_y + nv_z * lv_z;
                        final int illRefl = trace(x + vx, y + vy, z + vz, vx, vy, vz, AMBIENT, bounce + 1);
                        if (cosT < 0) {
                            // light is behind the object so just return the reflection
                            return illRefl;
                        } else {
                            final int illLight =
                                trace(x + lv_x, y + lv_y, z + lv_z, lv_x, lv_y, lv_z, LIGHTSOURCE, bounce + 1);
                            // compose light and reflection
                            return (
                                (((int) (((illLight >> 16) & 0xFF) * cosT)
                                    + (int) (((illRefl >> 16) & 0xFF) * (1 - cosT)))
                                    << 16)
                                    & 0xFF0000)
                                | ((((int) (((illLight >> 8) & 0xFF) * cosT)
                                    + (int) (((illRefl >> 8) & 0xFF) * (1 - cosT)))
                                    << 8)
                                    & 0xFF00)
                                | (((int) ((illLight & 0xFF) * cosT) + (int) ((illRefl & 0xFF) * (1 - cosT))) & 0xFF);
                        }
                    }
                default :
                    {
                        double lv_x = light_x - x,
                            lv_y = light_y - y,
                            lv_z = light_z - z,
                            lv = Math.sqrt(lv_x * lv_x + lv_y * lv_y + lv_z * lv_z);
                        // vector to the light from p.of.I
                        lv_x /= lv;
                        lv_y /= lv;
                        lv_z /= lv;
                        double cosT = nv_x * lv_x + nv_y * lv_y + nv_z * lv_z;
                        if ((cosT < 0) || (bounce >= maxIterations)) {
                            // light is behind the object (or bounce limit exceeded)
                            // return ambient illumination of object
                            cosT = 0;
                            return (((MINLIGHT * (sphere_c[id_best] & 0xFF0000)) >> 8) & 0xFF0000)
                                | (((MINLIGHT * (sphere_c[id_best] & 0xFF00)) >> 8) & 0xFF00)
                                | (((MINLIGHT * (sphere_c[id_best] & 0xFF)) >> 8) & 0xFF);
                        } else {
                            int ill = trace(x + lv_x, y + lv_y, z + lv_z, lv_x, lv_y, lv_z, LIGHTSOURCE, bounce + 1);
                            if ((ill & 0x01000000) == 0) {
                                // light is off of a reflective surface so diminish slightly
                                cosT *= cosT;
                                cosT *= cosT;
                                cosT *= cosT;
                            }
                            // compose light against color of object
                            return (
                                ((((int) ((((ill >> 16) & 0xFF) - MINLIGHT) * cosT) + MINLIGHT)
                                    * (sphere_c[id_best] & 0xFF0000))
                                    >> 8)
                                    & 0xFF0000)
                                | (((((int) ((((ill >> 8) & 0xFF) - MINLIGHT) * cosT) + MINLIGHT)
                                    * (sphere_c[id_best] & 0xFF00))
                                    >> 8)
                                    & 0xFF00)
                                | (((((int) (((ill & 0xFF) - MINLIGHT) * cosT) + MINLIGHT)
                                    * (sphere_c[id_best] & 0xFF))
                                    >> 8)
                                    & 0xFF);
                        }
                    }
            }
        } while (false);
        
        // give up if tracing to a light source
        if ((tolight & 0x01000000) != 0) return tolight;

        // check the floor (unless tracing to a light source)
        if (vector_y > 0) {
            final double t = (floor_y - start_y) / vector_y;
            final double x = start_x + vector_x * t, z = start_z + vector_z * t;
            // (x, floor_y, z) point of intersection with floor
            double lv_x = light_x - x,
                lv_y = light_y - floor_y,
                lv_z = light_z - z,
                lv = Math.sqrt(lv_x * lv_x + lv_y * lv_y + lv_z * lv_z);
            // vector to the light from p.of.I
            lv_x /= lv;
            lv_y /= lv;
            lv_z /= lv;
            double cosT = -lv_y;
            final boolean square = (((int) x & 512) ^ ((int) z & 512)) == 0;
            if ((cosT < 0) || (bounce >= maxIterations)) {
                //return square ? (((MINLIGHT << 8) * 0xFF) & 0xFF0000) : ((MINLIGHT * 0xFF) & 0xFF00);
                return square ? ((MINLIGHT * 0xC0) & 0xFF00) : ((MINLIGHT * 0xFF) & 0xFF00);
            } else {
                final int ill;
                if (((ill = trace(x, floor_y, z, lv_x, lv_y, lv_z, LIGHTSOURCE, bounce + 1)) & 0x01000000) == 0) {
                    // light is off of a reflective surface so diminish slightly
                    cosT *= cosT;
                    cosT *= cosT;
                    cosT *= cosT;
                }
                /*return square
                    ? ((((int) ((((ill >> 16) & 0xFF) - MINLIGHT) * cosT) + MINLIGHT) * (0xFF << 8)) & 0xFF0000)
                    : ((((int) ((((ill >> 8) & 0xFF) - MINLIGHT) * cosT) + MINLIGHT) * 0xFF) & 0xFF00);*/
                return square
                    ? ((((int) ((((ill >> 8) & 0xFF) - MINLIGHT) * cosT) + MINLIGHT) * 0xC0) & 0xFF00)
                    : ((((int) ((((ill >> 8) & 0xFF) - MINLIGHT) * cosT) + MINLIGHT) * 0xFF) & 0xFF00);
            }
        }
        
        // hit the ceiling - map some sky onto it
        //return tolight;
        
        double cosA = -(vector_y / Math.sqrt (vector_mag)); // cosine of angle between ray and the vertical
        double vl = vector_mag - (vector_y * vector_y);
        if (vl <= 0) return 0xFFFFFF; // safe for sqrt and other ops
        double cosT = vector_z / Math.sqrt (vl); // cosine of angle between ray and forward (0, 0, 1)
        double sinT = Math.sqrt (1 - (cosT * cosT));
		int index = (int)(cosT * cosA * Sky.radius) * Sky.width;
        if (vector_x > 0) {
        	return Sky.data[index + (int)(sinT * cosA * Sky.radius) + Sky.centre];
        } else {
        	return Sky.data[index - (int)(sinT * cosA * Sky.radius) + Sky.centre];
        }
    }

}
