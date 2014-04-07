package com.twotoasters.sectioncursoradaptersample.transformation;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class SquareTransformation implements Transformation {

    private final boolean mBottom;

    public SquareTransformation(boolean shouldCropBottom) {
        mBottom = shouldCropBottom;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int y = mBottom ? width : 0;

        Bitmap bitmap = Bitmap.createBitmap(source, 0, y, width, width);

        source.recycle();

        return bitmap;
    }

    @Override
    public String key() {
        return mBottom ? "bottom" : "top";
    }
}
