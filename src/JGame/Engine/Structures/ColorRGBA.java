package JGame.Engine.Structures;

import java.util.Objects;

/**
 * A class representing a color with components R G B and A
 */
public class ColorRGBA extends ColorRGB
{
    public final float a;

    public ColorRGBA(float r, float g, float b, float a)
    {
        super(r, g, b);
        this.a = a;
    }

    /**
     * Creates and returns an RGBA Color with the values specified in the Hex Code
     * @param hex
     * The hexadecimal representation of an RGBA Color, should be 8 Characters long
     * @return
     * The color represented by the hex code
     */
    public static ColorRGBA FromHex(String hex)
    {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        // Ensure the hex string is 6 characters long
        if (hex.length() != 8) {
            throw new IllegalArgumentException("Hex color code should be 8 characters long!");
        }

        // Split into R, G, B components
        String hex_r = hex.substring(0, 2);
        String hex_g = hex.substring(2, 4);
        String hex_b = hex.substring(4, 6);
        String hex_a = hex.substring(6, 8);

        float r = Integer.parseInt(hex_r, 16) / 255f;
        float g = Integer.parseInt(hex_g, 16) / 255f;
        float b = Integer.parseInt(hex_b, 16) / 255f;
        float a = Integer.parseInt(hex_a, 16) / 255f;

        return new ColorRGBA(r, g, b, a);
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
        String hex_a = Integer.toHexString((int)(a * 255));

        return "#" + hex_r +  hex_g + hex_b + hex_a;
    }

    @Override
    public String toString()
    {
        return "Color: (R: " + r + ", G: " + g + ", B: " + b + ", A: " + a + ")";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorRGBA colorRGBA = (ColorRGBA) o;
        return Float.compare(r, colorRGBA.r) == 0 && Float.compare(g, colorRGBA.g) == 0 && Float.compare(b, colorRGBA.b) == 0 && Float.compare(a, colorRGBA.a) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(r, g, b, a);
    }
    /**
     * The color White
     */
    public static final ColorRGBA White = new ColorRGBA(1f, 1f, 1f, 1f);

    /**
     * The color Black
     */
    public static final ColorRGBA Black = new ColorRGBA(0f, 0f, 0f, 1f);

    /**
     * The color Red
     */
    public static final ColorRGBA Red = new ColorRGBA(1f, 0f, 0f, 1f);

    /**
     * The color Green
     */
    public static final ColorRGBA Green = new ColorRGBA(0f, 1f, 0f, 1f);

    /**
     * The color Blue
     */
    public static final ColorRGBA Blue = new ColorRGBA(0f, 0f, 1f, 1f);

    /**
     * The color Yellow
     */
    public static final ColorRGBA Yellow = new ColorRGBA(1f, 1f, 0f, 1f);

    /**
     * The color Cyan
     */
    public static final ColorRGBA Cyan = new ColorRGBA(0f, 1f, 1f, 1f);

    /**
     * The color Magenta
     */
    public static final ColorRGBA Magenta = new ColorRGBA(1f, 0f, 1f, 1f);

    /**
     * The color Gray
     */
    public static final ColorRGBA Gray = new ColorRGBA(0.5f, 0.5f, 0.5f, 1f);

    /**
     * The color Orange
     */
    public static final ColorRGBA Orange = new ColorRGBA(1f, 0.5f, 0f, 1f);
}
