package de.kohlbau.customview;


import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    MoveView moveView;
    Button buttonView;
    TextView infoView;

    ValueAnimator mMoveAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mMoveAnimator = new ValueAnimator();


        moveView = (MoveView) findViewById(R.id.move);
        buttonView = (Button) findViewById(R.id.button);
        infoView = (TextView) findViewById(R.id.info);

        mMoveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer x = (Integer) animation.getAnimatedValue("x");
                Integer y = (Integer) animation.getAnimatedValue("y");
                moveView.setPosition(x, y);
            }
        });
        mMoveAnimator.setDuration(1500);

        moveView.setOnPositionChangedListener(new MoveView.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(float x, float y) {
                infoView.setText("Andy is located at:\nx: " + x + " y: " + y);
            }
        });


        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), getString(R.string.tapInfo), Toast.LENGTH_LONG).show();
                buttonView.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
                moveView.setOnTouchInputListener(new MoveView.OnTouchInputListener() {
                    @Override
                    public void onTouchInput(float x, float y) {
                        if (mMoveAnimator.isRunning()) {
                            mMoveAnimator.cancel();
                        } else {
                            buttonView.setTextColor(getResources().getColor(android.R.color.white));
                            PropertyValuesHolder xProp = PropertyValuesHolder.ofInt("x", (int) moveView.getPositionX(), (int) x);
                            PropertyValuesHolder yProp = PropertyValuesHolder.ofInt("y", (int) moveView.getPositionY(), (int) y);
                            mMoveAnimator.setValues(xProp, yProp);
                            mMoveAnimator.start();
                        }
                    }
                });
            }
        });


    }
}
