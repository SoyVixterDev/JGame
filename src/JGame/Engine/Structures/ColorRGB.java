package JGame.Engine.Structures;

import java.awt.*;
import java.util.Objects;

/**
 * Class representing a color with components R G and B
 */
public class ColorRGB
{
    public final float r,g,b;

    public ColorRGB(float r, float g, float b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * Creates and returns an RGB Color with the values specified in the Hex Code
     * @param hex
     * The color's hexadecimal representation of an RGB Color, should be 6 Characters long
     * @return
     * The color represented by the hex code
     */
    public static ColorRGB FromHex(String hex)
    {
        if (hex.startsWith("#"))
        {
            hex = hex.substring(1);
        }

        // Ensure the hex string is 6 characters long
        if (hex.length() != 6)
        {
            throw new IllegalArgumentException("Hex color code should be 6 characters long!");
        }

        // Split into R, G, B components
        String hex_r = hex.substring(0, 2);
        String hex_g = hex.substring(2, 4);
        String hex_b = hex.substring(4, 6);

        float r = Integer.parseInt(hex_r, 16) / 255f;
        float g = Integer.parseInt(hex_g, 16) / 255f;
        float b = Integer.parseInt(hex_b, 16) / 255f;

        return new ColorRGB(r, g, b);
    }

    /**
     * Gets the color as its Hexadecimal Code representation
     * @return
     * The color as its Hexadecimal Code representation
     */
    public String ToHex()
    {
        String hex_r = Integer.toHexString((int)(r * 255));
        String hex_g = Integer.toHexString((int)(g * 255));
        String hex_b = Integer.toHexString((int)(b * 255));

        return "#" + hex_r +  hex_g + hex_b;
    }

    @Override
    public String toString()
    {
        return "Color: (R: " + r + ", G: " + g + ", B: " + b + ")";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorRGB colorRGB = (ColorRGB) o;
        return Float.compare(r, colorRGB.r) == 0 && Float.compare(g, colorRGB.g) == 0 && Float.compare(b, colorRGB.b) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(r, g, b);
    }



    /**
     * The color White
     */
    public static final ColorRGB White = new ColorRGB(1f, 1f, 1f);

    /**
     * The color Black
     */
    public static final ColorRGB Black = new ColorRGB(0f, 0f, 0f);

    /**
     * The color Red
     */
    public static final ColorRGB Red = new ColorRGB(1f, 0f, 0f);

    /**
     * The color Green
     */
    public static final ColorRGB Green = new ColorRGB(0f, 1f, 0f);

    /**
     * The color Blue
     */
    public static final ColorRGB Blue = new ColorRGB(0f, 0f, 1f);

    /**
     * The color Yellow
     */
    public static final ColorRGB Yellow = new ColorRGB(1f, 1f, 0f);

    /**
     * The color Cyan
     */
    public static final ColorRGB Cyan = new ColorRGB(0f, 1f, 1f);

    /**
     * The color Magenta
     */
    public static final ColorRGB Magenta = new ColorRGB(1f, 0f, 1f);

    /**
     * The color Gray
     */
    public static final ColorRGB Gray = new ColorRGB(0.5f, 0.5f, 0.5f);

    /**
     * The color Orange
     */
    public static final ColorRGB Orange = new ColorRGB(1f, 0.5f, 0f);
}
