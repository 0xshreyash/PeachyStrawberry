package com.comp30022.helium.strawberry.components.ar.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.ar.ARRenderer;
import com.comp30022.helium.strawberry.components.ar.ARTrackerBeacon;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.helpers.ColourScheme;


public class CanvasDrawerLogic {

    public static final String TAG = "CanvasDrawerLogic";
    // offset for the guide artefact
    private static final int GUIDE_OFFSET = 31;
    // when drawing the guide, offset the image away from the arrow
    private static final int IMAGE_OFFSET = 100;
    // size of the border around the selected user
    private static final float BORDER_SIZE = 1.5f;
    // offset y for the text around the circular path
    private static final int OFFSET_Y = -20;
    // angle multiplier to get to angle 0 along the path
    private static final float ANGLE_MULTIPLIER = 1.5f;

    // offset for the username height
    private static final int NAME_HEIGHT_OFFSET = 70;
    // offset for the username width
    private static final int NAME_WIDTH_OFFSET = 7;
    // When the user has no profile picture/callback hasn't returned, we render a temporary
    // circle with this radius as replacement for the profile picture
    private static final int DEFAULT_CIRCLE_RADIUS = 150;


    // The following offsets are for visual treats, they make the cropped profile picture
    // align well with the guide arrow. The values are determined by eye.
    private static final int LEFT_IMAGE_OFFSET = 60;
    private static final int RIGHT_IMAGE_OFFSET = 30;
    private static final int BTM_LEFT_IMAGE_OFFSET = RIGHT_IMAGE_OFFSET;
    private static final int BTM_IMAGE_OFFSET = 50;
    private static final int TOP_LEFT_IMAGE_OFFSET = 60;
    private static final int BTM_RIGHT_IMAGE_OFFSET = 30;

    private static final int USERNAME_LENGTH_THRESHOLD = 10;

    private Paint arrowPaint;
    private Paint profilePicturePaint;
    private Paint namePaint;
    private Paint profilePictureBorderPaint;

    private Path circleDrawPath;
    private double radius;
    private Bitmap profilePicture;


    public CanvasDrawerLogic() {
        this.namePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.namePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.namePaint.setColor(StrawberryApplication.getInstance().getResources().getColor(R.color.white));
        this.namePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        this.namePaint.setTextSize(40);

        this.arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.arrowPaint.setStyle(Paint.Style.FILL);
        this.arrowPaint.setColor(ColourScheme.PRIMARY_DARK);
        this.arrowPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        this.arrowPaint.setTextSize(120);

        this.profilePictureBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.profilePictureBorderPaint.setStyle(Paint.Style.FILL);
        this.profilePictureBorderPaint.setColor(ColourScheme.PRIMARY_DARK);
        this.profilePictureBorderPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        this.profilePictureBorderPaint.setTextSize(120);
        this.profilePicturePaint = new Paint();
        //this.circleDrawPath = new Path();
    }

    /**
     * Draws profile picture according to specified size on canvas at (x,y). Draws a default
     * knob if the user profile picture is null
     * @param canvas Canvas to draw on
     * @param target ARTrackerBeacon of current rendering target
     */
    public void drawProfilePicture(Canvas canvas, ARTrackerBeacon target) {
        profilePicture = target.getProfilePicture();
        if (profilePicture != null) {
            if(target.isActive()) {
                this.radius = Math.min(profilePicture.getHeight(), profilePicture.getWidth())/2;
                canvas.drawCircle(target.getX() + profilePicture.getWidth()/2,
                        target.getY() + profilePicture.getHeight()/2, (float)radius*BORDER_SIZE,
                        profilePictureBorderPaint);
                circleDrawPath = new Path();
                circleDrawPath.addCircle(target.getX() + profilePicture.getWidth()/2,
                        target.getY() + profilePicture.getHeight()/2, (float)radius,
                        Path.Direction.CW);
            }
            canvas.drawBitmap(profilePicture, target.getX(), target.getY(),
                    this.profilePicturePaint);
        }
        else {
            circleDrawPath = new Path();
            radius = DEFAULT_CIRCLE_RADIUS * BORDER_SIZE;
            if(target.isActive()) {
                canvas.drawCircle(target.getX(),
                        target.getY(), (float) radius,
                        profilePictureBorderPaint);

                circleDrawPath.addCircle(target.getX(),
                        target.getY(), (float) radius,
                        Path.Direction.CW);
                canvas.drawCircle(target.getX(), target.getY(), DEFAULT_CIRCLE_RADIUS,
                        this.namePaint);
            }
        }
    }

    public void drawName(Canvas canvas, String username) {

        // Just take the first name if the username is too long.
        if (username.length() > USERNAME_LENGTH_THRESHOLD) {
            username = username.split("\\.")[0];
        }
        float width = namePaint.measureText(username);
        if(profilePicture != null) {
            canvas.drawTextOnPath(username, circleDrawPath,
                    (float) (ANGLE_MULTIPLIER * Math.PI * radius - width / 2), OFFSET_Y, namePaint);
        }
        else {
            canvas.drawTextOnPath(username, circleDrawPath,
                    (float) (ANGLE_MULTIPLIER * Math.PI * radius - width / 2), 60, namePaint);

        }

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
