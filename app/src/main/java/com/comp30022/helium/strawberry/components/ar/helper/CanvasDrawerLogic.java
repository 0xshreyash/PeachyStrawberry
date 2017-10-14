package com.comp30022.helium.strawberry.components.ar.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.comp30022.helium.strawberry.components.ar.ARRenderer;
import com.comp30022.helium.strawberry.components.ar.ARTrackerBeacon;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.helpers.ColourScheme;


public class CanvasDrawerLogic {
    // offset for the guide artefact
    private static final int GUIDE_OFFSET = 31;
    // when drawing the guide, offset the image away from the arrow
    private static final int IMAGE_OFFSET = 100;

    // offset for the username height
    private static final int NAME_HEIGHT_OFFSET = 70;
    // offset for the username width
    private static final int NAME_WIDTH_OFFSET = 7;
    // When the user has no profile picture/callback hasn't returned, we render a temporary
    // circle with this radius as replacement for the profile picture
    private static final int DEFAULT_CIRCLE_RADIUS = 30;

    // The following offsets are for visual treats, they make the cropped profile picture
    // align well with the guide arrow. The values are determined by eye.
    private static final int LEFT_IMAGE_OFFSET = 60;
    private static final int RIGHT_IMAGE_OFFSET = 30;
    private static final int BTM_LEFT_IMAGE_OFFSET = RIGHT_IMAGE_OFFSET;
    private static final int BTM_IMAGE_OFFSET = 50;
    private static final int TOP_LEFT_IMAGE_OFFSET = 60;
    private static final int BTM_RIGHT_IMAGE_OFFSET = 30;

    private Paint arrowPaint;
    private Paint profilePicturePaint;
    private Paint namePaint;

    public CanvasDrawerLogic() {
        this.namePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.namePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.namePaint.setColor(ColourScheme.PRIMARY_DARK);
        this.namePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        this.namePaint.setTextSize(60);

        this.arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.arrowPaint.setStyle(Paint.Style.FILL);
        this.arrowPaint.setColor(ColourScheme.PRIMARY_DARK);
        this.arrowPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        this.arrowPaint.setTextSize(120);

        this.profilePicturePaint = new Paint();
    }

    /**
     * Draws profile picture according to specified size on canvas at (x,y). Draws a default
     * knob if the user profile picture is null
     * @param canvas Canvas to draw on
     * @param target ARTrackerBeacon of current rendering target
     */
    public void drawProfilePicture(Canvas canvas, ARTrackerBeacon target) {
        Bitmap profilePicture = target.getProfilePicture();
        if (profilePicture != null)
            canvas.drawBitmap(profilePicture, target.getX(), target.getY(),
                    this.profilePicturePaint);
        else
            canvas.drawCircle(target.getX(), target.getY(), DEFAULT_CIRCLE_RADIUS, this.namePaint);
    }

    public void drawName(Canvas canvas, String username, float x, float y) {
        canvas.drawText(username,
                x - (NAME_WIDTH_OFFSET * username.length() / 2),
                y - NAME_HEIGHT_OFFSET, this.namePaint);
    }

    /**
     * Decude where to put the target guide by observing x and y values
     * @param canvas Canvas to be drawn on
     * @param target Target beacon
     */
    public void deduceGuide(Canvas canvas, ARTrackerBeacon target) {
        float x = target.getX();
        float y = target.getY();
        // Draw Guide artefact in general direction
        if (x < 0 && y < 0) {
            drawGuide(ARRenderer.Direction.TOP_LEFT, canvas, target);
        } else if (x > canvas.getWidth() && y < 0) {
            drawGuide(ARRenderer.Direction.TOP_RIGHT, canvas, target);
        } else if (x < 0 && y > canvas.getHeight()) {
            drawGuide(ARRenderer.Direction.BTM_LEFT, canvas, target);
        } else if (x > canvas.getWidth() && y > canvas.getHeight()) {
            drawGuide(ARRenderer.Direction.BTM_RIGHT, canvas, target);
        } else if (x < 0) {
            drawGuide(ARRenderer.Direction.LEFT, canvas, target);
        } else if (x > canvas.getWidth()) {
            drawGuide(ARRenderer.Direction.RIGHT, canvas, target);
        } else if (y < 0) {
            drawGuide(ARRenderer.Direction.UP, canvas, target);
        } else if (y > canvas.getHeight()) {
            drawGuide(ARRenderer.Direction.DOWN, canvas, target);
        }
    }

    public void drawGuide(ARRenderer.Direction direction, Canvas canvas, ARTrackerBeacon target) {
        float x = target.getX();
        float y = target.getY();
        if (x < 0 || x > canvas.getWidth()) {
            x = x < 0 ? GUIDE_OFFSET : canvas.getWidth() - GUIDE_OFFSET;
        }
        if (y < 0 || y > canvas.getHeight()) {
            y = y < 0 ? GUIDE_OFFSET : canvas.getHeight() - GUIDE_OFFSET;
        }
        float dx = 0;
        float dy = 0;
        float xOffset = 0;
        float yOffset = 0;
        final String arrow = "<";
        float rotation = 0;
        switch (direction) {
            case UP:
                dx = x;
                dy = GUIDE_OFFSET;
                rotation = -90;
                yOffset = IMAGE_OFFSET;
                break;
            case DOWN:
                dx = x;
                dy = canvas.getHeight() - GUIDE_OFFSET;
                rotation = 90;
                yOffset = -(IMAGE_OFFSET + BTM_IMAGE_OFFSET);
                xOffset = -BTM_IMAGE_OFFSET;
                break;
            case LEFT:
                dx = GUIDE_OFFSET;
                dy = y;
                rotation = 0;
                xOffset = IMAGE_OFFSET;
                yOffset = -LEFT_IMAGE_OFFSET;
                break;
            case RIGHT:
                dx = canvas.getWidth() - GUIDE_OFFSET;
                dy = y;
                rotation = 180;
                xOffset = -(IMAGE_OFFSET + RIGHT_IMAGE_OFFSET);
                break;
            case TOP_LEFT:
                dx = GUIDE_OFFSET;
                dy = GUIDE_OFFSET;
                rotation = -45;
                xOffset = IMAGE_OFFSET - TOP_LEFT_IMAGE_OFFSET / 2;
                yOffset = IMAGE_OFFSET - TOP_LEFT_IMAGE_OFFSET;
                break;
            case TOP_RIGHT:
                dx = canvas.getWidth() - GUIDE_OFFSET;
                dy = GUIDE_OFFSET;
                rotation = -135;
                xOffset = -IMAGE_OFFSET;
                yOffset = IMAGE_OFFSET;
                break;
            case BTM_LEFT:
                dx = GUIDE_OFFSET;
                dy = canvas.getHeight() - GUIDE_OFFSET;
                rotation = 45;
                xOffset = IMAGE_OFFSET - BTM_LEFT_IMAGE_OFFSET * 2;
                yOffset = -IMAGE_OFFSET - BTM_LEFT_IMAGE_OFFSET;
                break;
            case BTM_RIGHT:
                dx = canvas.getWidth() - GUIDE_OFFSET;
                dy = canvas.getHeight() - GUIDE_OFFSET;
                rotation = 135;
                xOffset = -(IMAGE_OFFSET + BTM_RIGHT_IMAGE_OFFSET);
                yOffset = -IMAGE_OFFSET + BTM_RIGHT_IMAGE_OFFSET;
                break;
        }
        canvas.save();
        // negative rotation because android uses positive clockwise system
        canvas.rotate(-rotation, dx, dy);
        canvas.drawText(arrow, dx, dy, this.arrowPaint);
        canvas.restore();

        // transaction start
        User.ProfilePictureType oldSize = target.getSize();
        target.setSize(User.ProfilePictureType.SMALL);
        Bitmap profilePicture = target.getProfilePicture();
        if (profilePicture != null) {
            canvas.drawBitmap(profilePicture, dx + xOffset, dy + yOffset, this.profilePicturePaint);
        } else {
            // no profile picture available for this user (yet) - draw a dot
            canvas.drawCircle(dx + xOffset, dy + yOffset, DEFAULT_CIRCLE_RADIUS, this.namePaint);
        }
        target.setSize(oldSize);
        // transaction end
    }

}
