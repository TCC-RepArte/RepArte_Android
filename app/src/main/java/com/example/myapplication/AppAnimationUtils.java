package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Classe utilitária para facilitar o uso de animações no app
 */
public class AppAnimationUtils {

    /**
     * Aplica animação de clique em um botão
     */
    public static void animateButtonClick(View button) {
        Animation animation = AnimationUtils.loadAnimation(button.getContext(), R.anim.button_click);
        button.startAnimation(animation);
    }

    /**
     * Aplica animação de fade in em uma view
     */
    public static void animateFadeIn(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
        view.startAnimation(animation);
    }

    /**
     * Aplica animação de bounce em uma view
     */
    public static void animateBounce(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce);
        view.startAnimation(animation);
    }

    /**
     * Aplica animação de shake em uma view (para erros)
     */
    public static void animateShake(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.shake);
        view.startAnimation(animation);
    }

    /**
     * Aplica animação de slide up em uma view
     */
    public static void animateSlideUp(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_up);
        view.startAnimation(animation);
    }

    /**
     * Aplica animação de clique com callback
     */
    public static void animateButtonClick(View button, Runnable onAnimationEnd) {
        Animation animation = AnimationUtils.loadAnimation(button.getContext(), R.anim.button_click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (onAnimationEnd != null) {
                    onAnimationEnd.run();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        button.startAnimation(animation);
    }

    /**
     * Aplica animação de fade in com callback
     */
    public static void animateFadeIn(View view, Runnable onAnimationEnd) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (onAnimationEnd != null) {
                    onAnimationEnd.run();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(animation);
    }

    /**
     * Aplica animação de fade out em uma view
     */
    public static void animateFadeOut(View view) {
        view.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Aplica animação de fade in em uma view (com visibilidade)
     */
    public static void animateFadeInWithVisibility(View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);
    }
} 