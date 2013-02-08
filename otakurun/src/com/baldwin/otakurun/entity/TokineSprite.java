package com.baldwin.otakurun.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.baldwin.libgdx.commons.entity.StatefulSprite;

/**
 * Ready state:
 * 60,57 32x49
 * 92,57 32x49
 * 124,57 32x49
 * 156,57 32x49
 * 188,57 32x49
 * 
 * Accelerating (pre-run)
 * 57,145 36x46
 * 
 * Run
 * 95,140 38x49
 * 133,140 46x49
 * 183,140 46x51
 * 232,140 39x51
 * 277,140 39x51
 * 316,140 39x51
 * 355,140 40x51
 * 396,140 40x51
 * 440,140 40x51
 * 482,140 40x51
 * 
 * Decelerating (post-run)
 * 527,140 39x51
 * 566,140 29x51
 * 601,140 26x51
 * 630,140 27x51
 * 
 * Jump start
 * 322,50 32x56
 * 358,50 37x56
 * 
 * Airborne rising
 * 403,52 35x54
 * 442,52 35x54
 * 
 * Airborne falling
 * 477,26 36x80
 * 518,26 36x80
 * 
 * Land (after airborne)
 * 555,64 32x42
 * 591,64 29x42
 * 
 * @author mbmartinez
 * 
 * Handle only animations here! Movement, acceleration, collisions, etc should
 * all be offloaded to some box2d physics handler
 */

public class TokineSprite extends StatefulSprite {
	public TokineSprite() {
		super();
		sheet = new Texture(Gdx.files.internal("data/sprites/tokine.png"));

		TextureRegion readyRegion = new TextureRegion(sheet, 60, 57, 160, 49);
		TextureRegion[] readySequence = readyRegion.split(32, 49)[0];
		Animation readyAnimation = new Animation(0.15f, readySequence);
		sequences.put(TokineState.ready, readyAnimation);

		TextureRegion a1 = new TextureRegion(sheet, 57, 145, 36, 46);
		/*
		 * stupid workaround for Animation.isAnimationFinished not working as expected.
		 * Add an additional frame to a terminating sequence then see also
		 * TestMove#isAnimationFinished()
		 */
		Animation acceleratingAnimation = new Animation(0.15f, a1, a1);
		sequences.put(TokineState.accelerating, acceleratingAnimation);

		TextureRegion run1 = new TextureRegion(sheet, 95, 140, 38, 49);
		TextureRegion run2 = new TextureRegion(sheet, 133, 140, 46, 49);
		TextureRegion run3 = new TextureRegion(sheet, 183, 140, 46, 51);
		TextureRegion run4 = new TextureRegion(sheet, 232, 140, 39, 51);
		TextureRegion run5 = new TextureRegion(sheet, 277, 140, 39, 51);
		TextureRegion run6 = new TextureRegion(sheet, 316, 140, 39, 51);
		TextureRegion run7 = new TextureRegion(sheet, 355, 140, 40, 51);
		TextureRegion run8 = new TextureRegion(sheet, 396, 140, 40, 51);
		TextureRegion run9 = new TextureRegion(sheet, 440, 140, 40, 51);
		TextureRegion run10 = new TextureRegion(sheet, 482, 140, 40, 51);
		Animation run = new Animation(0.065f, new TextureRegion[]{run1,run2,run3,run4,run5,run6,run7,run8,run9,run10});
		sequences.put(TokineState.run, run);

		TextureRegion d1 = new TextureRegion(sheet, 527, 140, 39, 51);
		TextureRegion d2 = new TextureRegion(sheet, 566, 140, 29, 51);
		TextureRegion d3 = new TextureRegion(sheet, 601, 140, 26, 51);
		TextureRegion d4 = new TextureRegion(sheet, 630, 140, 27, 51);
		Animation decelerating = new Animation(0.10f, new TextureRegion[]{d1, d2, d3, d4, d4});
		sequences.put(TokineState.decelerating, decelerating);

		TextureRegion js1 = new TextureRegion(sheet, 322, 50, 32, 56);
		TextureRegion js2 = new TextureRegion(sheet, 358, 50, 37, 56);
		Animation js = new Animation(0.10f, js1, js2, js2);
		sequences.put(TokineState.jump_start, js);

		TextureRegion ar1 = new TextureRegion(sheet, 403, 52, 35, 54);
		TextureRegion ar2 = new TextureRegion(sheet, 442, 52, 35, 54);
		Animation ar = new Animation(0.15f, ar1, ar2);
		sequences.put(TokineState.airborne_rising, ar);

		TextureRegion af1 = new TextureRegion(sheet, 477, 26, 36, 80);
		TextureRegion af2 = new TextureRegion(sheet, 518, 26, 36, 80);
		Animation af = new Animation(0.15f, af1, af2);
		sequences.put(TokineState.airborne_falling, af);

		TextureRegion land1 = new TextureRegion(sheet, 555, 64, 32, 42);
		TextureRegion land2 = new TextureRegion(sheet, 591, 64, 29, 42);
		Animation land = new Animation(0.15f, land1, land2, land2);
		sequences.put(TokineState.landing, land);
		
		this.state = TokineState.ready;
	}

}