package ir.pathseeker.baran;

/**
 * Created by farid on 5/27/15.
 */
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtils {

    private static Map<String, Typeface> TYPEFACE = new HashMap<String, Typeface>();

    public static Typeface getFonts(Context context, String name) {
        Typeface typeface = TYPEFACE.get(name);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/"
                    + name);
            TYPEFACE.put(name, typeface);
        }
        return typeface;
    }
}
