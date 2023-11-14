package com.template;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.animation.ObjectAnimator;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;

public class SlotsActivity extends AppCompatActivity {
    private final int spinTimeMs = 5000;
    private TextView betTextView;
    private TextView moneyTextView;
    private int money;
    private int bet;
    private final int[] icon_drawables = {
            R.drawable.ico_777,
            R.drawable.ico_crown,
            R.drawable.ico_gem_green,
            R.drawable.ico_gem_purple,
            R.drawable.ico_gem_red,
            R.drawable.ico_gem_yellow,
            R.drawable.ico_genie,
            R.drawable.ico_lamp
    };
    private boolean spinning = false;
    private boolean autoSpinEnabled = false;
    public void preventClicks(View view) {}
    private LinearLayout[] slotsColumns;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slots);

        View decorView = getWindow().getDecorView();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        ImageButton spinButton = findViewById(R.id.spinButton);
        ToggleButton spinAutoButton = findViewById(R.id.spinAutoToggle);
        ImageButton betDecreaseButton = findViewById(R.id.betDecreaseButton);
        ImageButton betIncreaseButton = findViewById(R.id.betIncreaseButton);
        betTextView = findViewById(R.id.betTextView);
        moneyTextView = findViewById(R.id.moneyTextView);
        ImageButton exitButton = findViewById(R.id.exitButton);
        ImageButton retryButton = findViewById(R.id.retryButton);
        slotsColumns = new LinearLayout[]{
                findViewById(R.id.slotsColumn1),
                findViewById(R.id.slotsColumn2),
                findViewById(R.id.slotsColumn3),
                findViewById(R.id.slotsColumn4)
        };

        getResources().getDisplayMetrics();
        setBet(5);
        setMoney(500);

        spinButton.setOnClickListener(v -> {
            if (isSpinning()) {
                return;
            }
            spinSlots();
        });
        spinAutoButton.setOnClickListener(v -> {
            autoSpinEnabled = spinAutoButton.isChecked();
            if (!isSpinning() && autoSpinEnabled) {
                spinSlots();
            }
        });
        betDecreaseButton.setOnClickListener(v -> adjustBet(-5));
        betIncreaseButton.setOnClickListener(v -> adjustBet(5));
        retryButton.setOnClickListener(v -> {
            startActivity(this.getIntent());
            finish();
        });
        exitButton.setOnClickListener(v -> {
            setAutoSpinEnabled(false);
            finish();
        });
        Utils.addButtonResponsiveness(spinButton);
        Utils.addButtonResponsiveness(exitButton);
        Utils.addButtonResponsiveness(betDecreaseButton);
        Utils.addButtonResponsiveness(betIncreaseButton);
        Utils.addButtonResponsiveness(spinAutoButton);
        Utils.addButtonResponsiveness(retryButton);
        fillSlots();
    }
    public void spinSlots() {
        int selectedBet = bet;
        setSpinning(true);
        animateSlots();
        new Handler().postDelayed(() -> {
            int random = new Random().nextInt(4);
            if (random == 0) {
                gainMoney(selectedBet);
            }
            else {
                loseMoney(selectedBet);
            }
            setSpinning(false);
            if (isAutoSpinEnabled()) {
                spinSlots();
            }
        }, spinTimeMs);
    }

    public void animateSlots() {
        final int min = icon_drawables.length;
        final int max = (icon_drawables.length * 2);
        for (LinearLayout slotsColumn : slotsColumns) {
            int random = new Random().nextInt((max - min) + 1) + min;
            float target = slotsColumn.getY() + Utils.dpToPx(80 * random);
            ObjectAnimator animator = ObjectAnimator.ofFloat(slotsColumn, "Y", slotsColumn.getY(), target);
            animator.setDuration(spinTimeMs);
            animator.start();
            new Handler().postDelayed(() -> {
                animator.cancel();
                slotsColumn.setY(slotsColumn.getY() % Utils.dpToPx(80 * icon_drawables.length) - Utils.dpToPx(80 * icon_drawables.length * 2));
            }, spinTimeMs);
        }
    }

    public void fillSlots() {
        for (LinearLayout slotsColumn : slotsColumns) {
            for (int i = 0; i < 4; i++) {
                for (int icon_drawable : icon_drawables) {
                    ImageView new_icon = new ImageView(this);
                    new_icon.setImageResource(icon_drawable);
                    ViewGroup parentView = slotsColumn;
                    parentView.addView(new_icon);
                    ViewGroup.LayoutParams layoutParams = new_icon.getLayoutParams();
                    layoutParams.width = MATCH_PARENT;
                    layoutParams.height = Utils.dpToPx(80);
                    new_icon.setLayoutParams(layoutParams);
                }
            }
            ViewGroup.LayoutParams layoutParams = slotsColumn.getLayoutParams();
            layoutParams.height = Utils.dpToPx(80 * icon_drawables.length * 4);
            slotsColumn.setLayoutParams(layoutParams);
            slotsColumn.setY(slotsColumn.getY() - Utils.dpToPx(80 * icon_drawables.length * 2));
        }
    }
    public void loseGame() {
        ConstraintLayout gameOverLayout = findViewById(R.id.gameOverLayout);
        gameOverLayout.setVisibility(View.VISIBLE);
        setAutoSpinEnabled(false);
    }

    public void gainMoney(int amount) {
        setMoney(money + amount);
    }
    public void loseMoney(int amount) {
        setMoney(money - amount);
        if (money < bet) {
            setBet(money);
        }
        if (money <= 0) {
            loseGame();
        }
    }
    public void setMoney(int amount) {
        money = amount;
        updateMoneyText();
    }

    public void updateMoneyText() {
        PropertySetter setter = value -> {
            moneyTextView.setText(Utils.intToStringWithLeadingZeros((int) value, 8));
//            if (value > 0 && money != 0) {
//                moneyTextView.setScaleX((float) (value / money));
//                moneyTextView.setScaleY((float) (value / money));
//            }

        };
        Utils.tweenProperty(Double.parseDouble((String) moneyTextView.getText()), money, 0.5, setter);
    }
    public void setBet(int amount) {
        bet = amount;
        runOnUiThread(() -> {
            betTextView.setText(Integer.toString(bet));
        });
    }

    public int getMoney() {
        return money;
    }

    public int getBet() {
        return bet;
    }

    public boolean isSpinning() {
        return spinning;
    }

    public boolean isAutoSpinEnabled() {
        return autoSpinEnabled;
    }

    public void setAutoSpinEnabled(boolean autoSpinEnabled) {
        this.autoSpinEnabled = autoSpinEnabled;
    }

    public void setSpinning(boolean spinning) {
        this.spinning = spinning;
    }

    public void adjustBet(int amount) {
        if ((amount > 0 && bet + amount > money) || (bet + amount < 1)) {
            return;
        }
        setBet(bet + amount);
    }
}

